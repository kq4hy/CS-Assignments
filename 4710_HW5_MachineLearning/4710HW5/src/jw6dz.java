import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

//authors: jw6dz and kq4hy
public class jw6dz extends Classifier {
	
	public double curr_Entropy = 0;
	public Choice the_choice;
	public int num_data = 0; //number of elements in the training set
	public List<String> names_info;
	public List<String> numeric_feature_names = new ArrayList<String>();
	public List<String> information;
	public FNode theStart;
	public List<FNode> initialFNodes = new ArrayList<FNode>();
	public int iF_index = 1;
	public List<String> features_list = new ArrayList<String>();
	public int FNodes = 0; int SNodes = 0; int TNodes = 0;
		
	public jw6dz(String namesFilePath) {
		super(namesFilePath);
		try {
			this.names_info = Files.readAllLines(Paths.get(namesFilePath));
			String[] choices = names_info.get(0).split("\\s+");
			this.the_choice = new Choice(choices);
			for (int i = 2; i < names_info.size(); i++) {
				String[] line = names_info.get(i).split("\\s+");
				String f_name = line[0];
				this.features_list.add(f_name);
				Feature temp;
				if (line[1].equals("numeric")) {
					temp = new Feature(f_name, choices.length);
					numeric_feature_names.add(f_name);
				}
				else {
					temp = new Feature(f_name, line, choices.length);
				}
				this.the_choice.addFeature(temp);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void train(String trainingDataFilpath) {
		try {
			this.information = Files.readAllLines(Paths.get(trainingDataFilpath));
//			System.out.println("The file " + trainingDataFilpath + " contains " + information.size() + " lines.");
			this.num_data = information.size();
			if (this.num_data < 1) {
				System.out.println("There's no data in the file!!");
				System.exit(0);
			}
			for (int i = 0; i < information.size(); i++) {
				String[] choices = information.get(i).split("\\s+");
				String line_result = choices[choices.length-1];
//				System.out.println(line_result);
				int index = 0;
				for (int z = 0; z < this.the_choice.all_choices.length; z++) {
					if (this.the_choice.all_choices[z].equals(line_result)) {
						index = z;
						this.the_choice.nums_each_choice[index]++;
						break;
					}
				}
				//fit all data in to respective features
				for(int j = 0; j < choices.length - 1; j++) { //for each number/stat in the line //******* -1
//					System.out.println(this.all_choices.get(index).features.size());
					if (this.the_choice.features.get(j).isNumeric) { //if feature is numeric
						if (this.the_choice.features.get(j).levels.containsKey(choices[j])) 
							this.the_choice.features.get(j).levels.get(choices[j]).set(index, this.the_choice.features.get(j).levels.get(choices[j]).get(index) + 1);
						else {
							this.the_choice.features.get(j).createLevel(choices[j]);
							this.the_choice.features.get(j).levels.get(choices[j]).set(index, this.the_choice.features.get(j).levels.get(choices[j]).get(index) + 1);
						}
					}
					else { //if feature isn't numeric
						this.the_choice.features.get(j).levels.get(choices[j]).set(index, this.the_choice.features.get(j).levels.get(choices[j]).get(index) + 1);
					}
				}
			}
			//calculate current system entropy
			this.curr_Entropy = this.the_choice.calc_entropy();
			
			//calculate and input entropy levels
			for (Feature f: this.the_choice.features) {
				for (Entry<String, ArrayList<Double>> entry : f.levels.entrySet()) {
				    ArrayList<Double> value = entry.getValue();
				    value.set(2, calc_entropy(value.get(0), value.get(1)));
				}
			}
			
			//calculate gain for each Feature
			for (Feature f: this.the_choice.features) {
				f.gain = calc_gain(this.curr_Entropy, f, this.num_data);
			}
			
			//sort greatest gain to least gain
			Collections.sort(this.the_choice.features, new Comparator<Feature>() { 
			    @Override
			    public int compare(Feature f1, Feature f2) {
			        if (f1.gain > f2.gain)
			            return -1;
			        if (f1.gain < f2.gain)
			            return 1;
			        return 0;
			    }
			});
			
//			this.base = new NodeWorld(this.names_info, this.information);
			ArrayList<Integer> vi = new ArrayList<Integer>();
			for (int i = 0; i < this.names_info.size() - 2; i++) 
				vi.add(i);
			
			this.theStart = new FNode(this.information, vi, this.the_choice.features.get(0), null);
			
//			System.out.println("FNodes#: " + FNodes + " SNodes#: " + SNodes + " TNodes#: " + TNodes);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void makePredictions(String testDataFilepath) {
//		System.out.println("#$@$!@$!@#@!#!@$@!Make Predictions Starts Here#@!$@!#@!@!#!@@!#!@$@$!@#");
		double total_correct = 0;
		double total_lines = 0;
		try {
			this.information = Files.readAllLines(Paths.get(testDataFilepath));
			total_lines = (double)this.information.size();
			HashMap<String, String> values = new HashMap<String, String>();
//			System.out.println(this.features_list.toString());
			for (String line: this.information) {
				String[] chunks = line.trim().split("\\s+"); 
				for (int i = 0; i < this.features_list.size(); i++) {
					values.put(this.features_list.get(i), chunks[i]);
				}
//				System.out.println(values.toString());
				Node curr_node = this.theStart;
				boolean terminate = false;
				String curr_feature_name = this.theStart.name;
				while (!terminate) {
//					System.out.println(curr_node.name);
					if (curr_node.type == "FNode") {
						curr_feature_name = curr_node.name;
						String val = values.get(curr_node.name);
						int difference = 99999;
						boolean found = false;
						Node tempNode = null;
						for (Node n: curr_node.children) {
//							System.out.println(n.name + " " + val);
							if (this.numeric_feature_names.contains(curr_feature_name)) { //If feature is numeric
							/////////COMPLICATED NEW FEATURE////////////////
								int tempD = Math.abs(Integer.parseInt(val)- Integer.parseInt(n.name));
//								System.out.println("tempD: " + tempD);
								if (Integer.parseInt(val) == Integer.parseInt(n.name)) {
									curr_node = n;
									found = true;
									break;
								}
								if (tempD < difference) {
									difference = tempD;
									tempNode = n;
//									System.out.println("hi");
//									tempNode = new TNode(n.name, 1, false);
								}
							/////////COMPLICATED NEW FEATURE////////////////
							}
							else {
								if (n.name.equals(val)) {
									curr_node = n;
									found = true;
									break;
								}
							}
						} 
						if (!found) { 
							curr_node = tempNode;
//							System.out.println("avged out with " + tempNode);
						}
					}
					else if (curr_node.type == "SNode") {
//						System.out.println("Stuck in SNode loop");
						if (curr_node.children.size() == 1) { //its child is a FNode
							curr_node = curr_node.children.get(0);
						}
						else {	
//							System.out.println("Stuck in SNode loop Else");
							String val = values.get(curr_feature_name);
							int difference = 99999;
							boolean found = false;
							Node tempNode = null;
							for (Node n: curr_node.children) {	
								if (this.numeric_feature_names.contains(curr_feature_name)) {
									int tempD = Math.abs(Integer.parseInt(val)- Integer.parseInt(n.name));
//									System.out.println("im in here: " + found);
									if (Integer.parseInt(val) == Integer.parseInt(n.name)) {
										curr_node = n;
										found = true;
//										terminate = true;
										break;
									}
									if (tempD < difference) {
										difference = tempD;
										tempNode = n;
//										tempNode = new TNode(curr_node.name, 1, false);
									}
								}
								else {
									if (n.name.equals(val)) {
										curr_node = n;
										found = true;
										break;
									}
								}
							} 
							if (!found) { 
								curr_node = tempNode;
							}
						}
					}
					else { //TNode
//						System.out.println(line + "  prediction: " + curr_node.outcome);
//						if (curr_node.isRG) {
//							curr_node = this.initialFNodes.get(this.iF_index);
//							this.iF_index++;
//						}
//						if (chunks[chunks.length - 1].trim().equals(curr_node.outcome)) { total_correct+= 1.0; }
						System.out.println(curr_node.outcome);
						terminate = true;
//						System.out.println("IM here");
					}
//					curr_node = curr_node.children
				}
			
			}
//			System.out.println("Total correct: " + total_correct + " out of " + total_lines);
//			System.out.println("% correct: " + total_correct/total_lines);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//log 2
	public static double log2(double x) {
		return Math.log(x)/ Math.log(2);
	}
	
	public static double calc_entropy(double pos, double neg) {
		double total = pos + neg;
		if (pos == 0.0 || neg == 0.0) 
			return 0.0;
		return -1 * pos/total * log2(pos/total) - neg/total * log2(neg/total);
	}
 
	public static double calc_gain(double curr_e, Feature f, int total_num) {
		double result = curr_e;
		if (total_num == 0) //***
			return 0.0;
		for (Entry<String, ArrayList<Double>> entry : f.levels.entrySet()) {
		    ArrayList<Double> value = entry.getValue();
		    result = result - (value.get(0) + value.get(1)) / (double)total_num * value.get(2);
		}
		return result;
	}
	
	public static String[][] convert_to_matrix(List<String> info, int num_features) {
		String[][] toreturn = new String[info.size()][num_features];
		for (int i = 0; i < info.size(); i++) {
			String[] choices = info.get(i).split("\\s+");
			for (int j = 0; j < choices.length; j++)
				toreturn[i][j] = choices[j];
		}
		return toreturn;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////
	public class Feature {
		public String name;
		public double gain = 0.0;
		public boolean isNumeric;
		public int num_outcomes = 0;
		public HashMap<String, ArrayList<Double>> levels = new HashMap<String, ArrayList<Double>>();
		
		//Non-Numeric Feature
		public Feature(String n, String[] choices, int num_outs) {
			this.name = n;
			this.isNumeric = false;
			this.num_outcomes = num_outs;
			//Initiate Levels dictionary with the options of the Feature
			for (int i = 1; i < choices.length; i++) {
				createLevel(choices[i]);
			}
		}
		//Numeric Feature
		public Feature(String n, int num_outs) {
			this.name = n;
			this.num_outcomes = num_outs;
			this.isNumeric = true;
		}
		
		public void createLevel(String n) {
			ArrayList<Double> tempA = new ArrayList<Double>();
			
			for(int i = 0; i < this.num_outcomes + 1; i++) 
				tempA.add(0.0);
			this.levels.put(n, tempA);
		}
		
		public String toString() {
			String toReturn = this.name + ": \r\n" + "Gain: " + this.gain + "\r\n";
			for (Entry<String, ArrayList<Double>> entry : this.levels.entrySet()) {
			    String key = entry.getKey();
			    ArrayList<Double> value = entry.getValue();
			    toReturn += "\t";
			    toReturn += key + ": ";
			    for (Double d: value) {
			    	toReturn += d.toString() + " ";
			    }
			    toReturn += "\r\n";
			}
			return toReturn;
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////
	public class Choice {
		
		public ArrayList<Feature> features;
		public String[] all_choices;
		public Integer[] nums_each_choice;
		
		public Choice(String[] choices) {
			this.all_choices = choices;
			this.nums_each_choice = new Integer[choices.length];
			this.features = new ArrayList<Feature>();
			for (int i = 0; i < this.all_choices.length; i++) {
				this.nums_each_choice[i] = 0;
			}
		}
		
		public String toString() {
			String toReturn = "*********************************NEW CHOICE?*********************************\r\n Options: ";
			for (int i = 0; i < this.all_choices.length; i++) {
				toReturn += this.all_choices[i] + ": " + this.nums_each_choice[i].toString() + " ";
			}
			toReturn += "\r\n" + "\r\n";
			for (Feature f: this.features) {
				toReturn += f.toString() + "\r\n";
			}
			return toReturn;
		}
		
		public void addFeature(Feature f) {
			this.features.add(f);
		}
		
		public double calc_entropy() {
			double total = this.nums_each_choice[0] + this.nums_each_choice[1];
			if (this.nums_each_choice[0] == 0.0 || this.nums_each_choice[1] == 0.0) 
				return 0.0;
			return -1 * this.nums_each_choice[0]/total * log2(this.nums_each_choice[0]/total) - this.nums_each_choice[1]/total * log2(this.nums_each_choice[1]/total);
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////
	public abstract class Node {
		public boolean isRG;
		public ArrayList<Node> children;
		public String name;
		public int total_num;
		public List<String> information;
		public List<Integer> valid_indices;
		public List<String> invalid_feature_names;
		public abstract void initializeChildren();
		public String type;
		public String outcome = "";
		public Feature parent;
	}
//////////////////////////////////////////////////////////////////////////////////////////
	//Feature Node
	public class FNode extends jw6dz.Node {
		public int feature_index;
		public HashMap<String, ArrayList<String>> categorized_lines = new HashMap<String, ArrayList<String>>();
		
		public FNode(List<String> info, List<Integer> indices, Feature f, List<String> invalids) {
			FNodes++;
			this.name = f.name;
			this.parent = f;
			this.type = "FNode";
			this.valid_indices = indices;
			this.information = info;
			this.children = new ArrayList<Node>();
			if (invalids == null) 
				this.invalid_feature_names = new ArrayList<String>();
			else 
				this.invalid_feature_names = invalids;
			//find feature f's index
			for (int index = 2; index < names_info.size(); index++) {
				String[] choices = names_info.get(index).split("\\s+");
				if (choices[0].equals(f.name)) {
					this.feature_index = index - 2;
					if (!this.invalid_feature_names.contains(choices[0])) 
						this.invalid_feature_names.add(choices[0]);
					break;
				}
			}
			
			//remove f's index from valid indices
			this.valid_indices.remove((Integer)feature_index);
//			System.out.println("###########FNode " + this.name + "##################");
//			System.out.println("valid indices: "+valid_indices);
//			
			set_categorized_lines(f.levels);
			
			initializeChildren();
			
		}
		public void initializeChildren() {
			for(String s: categorized_lines.keySet()) {
				SNode temp = new SNode(s, categorized_lines.get(s), this.valid_indices, this.invalid_feature_names, this.parent);
//				System.out.println(temp.toString());
				this.children.add(temp);
//				System.out.println(temp.toString1());
			}
		}
		
		public void set_categorized_lines(HashMap<String, ArrayList<Double>> levels) {
			for (Entry<String, ArrayList<Double>> entry : levels.entrySet()) {
			    String key = entry.getKey();
			    this.categorized_lines.put(key, new ArrayList<String>());
			}
			//categorize all lines based on the value at the feature_index
			for(String line: this.information) {
				String[] fields = line.trim().split("\\s+");
				ArrayList<String> tempL = this.categorized_lines.get(fields[this.feature_index]);
				tempL.add(line);
				this.categorized_lines.put(fields[this.feature_index], tempL);
			}
		}
		
		/*public FNode(Feature f, List<String> names_info, List<String> info) {
			
			this.name = f.name;
						
			this.names = names_info;
			this.children = new ArrayList<Node>();
			this.feature_index = -1;
			this.relevant_lines = info;
			
			findFeatureIndex(this.name, info);
			//make a new SNode for each level
			trimNamesListByFeature(this.name, info);
			for (String s: f.levels.keySet()) {
//				System.out.println("CREATING SNODE");
				System.out.println("Building a new SNode " + s + " from FNode " + this.name);
				SNode temp = new SNode(s, this.names, trimListByFeature(createSubList(s, this.feature_index)));
				this.children.add(temp);
//				System.out.println(temp.toString());
			}		
		}
		
		public void findFeatureIndex(String name, List<String> info) {
			int index;
//			System.out.println("the name of the feature w're looking for is: " + name);
			for (index = 2; index < this.names.size(); index++) {
				String[] choices = this.names.get(index).split("\\s+");
				if (choices[0].equals(name)) {
					this.feature_index = index;
					break;
				}
			}
			System.out.println("The feature index is: " + this.feature_index);
		}
		public void trimNamesListByFeature(String name, List<String> info) {
			this.names.remove(this.feature_index);
		}	
		public List<String> trimListByFeature(List<String> info) {
			int index = this.feature_index - 2;
			List<String> modifiedList = new ArrayList<String>();
			for (int i = 0; i < info.size(); i++) {
				String[] choices = info.get(i).split("\\s+");
				choices[index] = "";
				String putbackin = "";
				for(String s: choices) {
					putbackin += s + " ";
				}
				putbackin += "\r\n";
//				System.out.print(putbackin);
				modifiedList.add(putbackin);
			}
			return modifiedList;
		}
		
		public List<String> createSubList(String s, int index) {
			ArrayList<String> returnList = new ArrayList<String>();
			for (String line: this.relevant_lines) {
				String[] choices = line.split("\\s+");
				if (choices[index-2].equals(s))
					returnList.add(line);
			}
			return returnList;
		}
		*/
		public String toString() {
			String returnS = "";
			for(Node s: this.children) {
				returnS += s.toString() + "\r\n";
			}
			return returnS;
		}
	}
	
//////////////////////////////////////////////////////////////////////////////////////////
	//Sub-feature Node
	//has only one child, a feature Node
	public class SNode extends jw6dz.Node {
		public double entropy;
		public Choice the_choice;
		public int num_data;
		public Feature chosen_one;
		public ArrayList<Feature> chosen_ones = new ArrayList<Feature>();
		public int status = 0; //0: Gain is > 0, build FNode
							   //1: Terminate, Salary >50k
							   //2: Terminate, Salary <=50k
		
		public SNode(String n, List<String> info, List<Integer> indices, List<String> invalids, Feature p) {
			SNodes ++;
			this.name = n;
			this.parent = p;
			this.type = "SNode";
			this.chosen_one = null;
			this.children = new ArrayList<Node> ();
			this.information = info;
			this.valid_indices = indices;
			this.invalid_feature_names = invalids;
			String[] choices = names_info.get(0).split("\\s+");
			this.the_choice = new Choice(choices);
			for (int i = 2; i < names_info.size(); i++) {
				String[] line = names_info.get(i).split("\\s+");
				String f_name = line[0];
				Feature temp;
				if (line[1].equals("numeric")) {
					temp = new Feature(f_name, choices.length);
				}
				else {
					temp = new Feature(f_name, line, choices.length);
				}
				this.the_choice.addFeature(temp);
			}
			
			doCalculations();
//			System.out.println(toString());
			//initializeChildren();
		}
		
		public void initializeChildren() {
//			if (chosen_one.gain == 0.0) {
//				chosen_one = this.parent;
//				for (Entry<String, ArrayList<Double>> entry : chosen_one.levels.entrySet()) {
//				    String key = entry.getKey();
//				    ArrayList<Double> value = entry.getValue();
//				    int d = 1;
//				    if (value.get(0) == 0.0) { d = 2; }
//				    TNode tempT = new TNode(key, d);
//				    this.children.add(tempT);
//				}
//			}
//			else {
//				FNode tempF = new FNode(this.information, this.valid_indices, this.chosen_one, this.invalid_feature_names);
//				this.children.add(tempF);
//				chosen_ones.add(chosen_one);
//			}
		}
		
		public void doCalculations() {
			int num_data  = this.information.size();
//			System.out.println("there are this many rows of data: " + num_data);
//			System.out.println(invalid_feature_names.toString());
			for (int i = 0; i < this.information.size(); i++) {
				String[] choices = this.information.get(i).split("\\s+");
				String line_result = choices[choices.length-1];
//				if (num_data <= 5) System.out.println(this.information.get(i));
				int index = 0;
				for (int z = 0; z < this.the_choice.all_choices.length; z++) {
					if (this.the_choice.all_choices[z].equals(line_result)) {
						index = z;
						this.the_choice.nums_each_choice[index]++;
						break;
					}
				}
				//fit all data in to respective features
				for(int j: this.valid_indices) { //for each number/stat in the line //******* -1
//					System.out.println(this.all_choices.get(index).features.size());
//					System.out.println(features_list.get(j));
					if (this.the_choice.features.get(j).isNumeric) { //if feature is numeric
						if (this.the_choice.features.get(j).levels.containsKey(choices[j])) 
							this.the_choice.features.get(j).levels.get(choices[j]).set(index, this.the_choice.features.get(j).levels.get(choices[j]).get(index) + 1);
						else {
							this.the_choice.features.get(j).createLevel(choices[j]);
							this.the_choice.features.get(j).levels.get(choices[j]).set(index, this.the_choice.features.get(j).levels.get(choices[j]).get(index) + 1);
						}
					}
					else { //if feature isn't numeric
						this.the_choice.features.get(j).levels.get(choices[j]).set(index, this.the_choice.features.get(j).levels.get(choices[j]).get(index) + 1);
					}
				}	
			}
			//calculate current system entropy
			this.entropy = this.the_choice.calc_entropy();
			
			//calculate and input entropy levels
			for (Feature f: this.the_choice.features) {
				for (Entry<String, ArrayList<Double>> entry : f.levels.entrySet()) {
				    String key = entry.getKey();
				    ArrayList<Double> value = entry.getValue();
				    value.set(2, calc_entropy(value.get(0), value.get(1)));
				}
			}
			//calculate gain for each Feature
			for (Feature f: this.the_choice.features) {
				f.gain = calc_gain(this.entropy, f, num_data);
			}
			
			Collections.sort(this.the_choice.features, new Comparator<Feature>() { 
			    @Override
			    public int compare(Feature f1, Feature f2) {
			        if (f1.gain > f2.gain)
			            return -1;
			        if (f1.gain < f2.gain)
			            return 1;
			        return 0;
			    }
			});
//			System.out.println("Post Sort");
//			for(Feature f: this.the_choice.features) { System.out.print(f.name + " has gain: " + f.gain); }
			
//			System.out.println("Im here! num_data: " + num_data + " SNode name: " + this.name);
			for(int i = 0; i < this.the_choice.features.size(); i++) {
				if (!(this.invalid_feature_names.contains(this.the_choice.features.get(i).name))) {
//					System.out.println(this.the_choice.features.get(i).name +" is not in invalid features!");
					chosen_one = this.the_choice.features.get(i);
//					System.out.println("the chosen feature is: " + chosen_one.name + " with gain of " + chosen_one.gain);
//					System.out.println("hi");
					break;
				}
			}
			
			
//			if (chosen_one == null || valid_indices.size() == 0 || chosen_one.gain == 0.0) {
			if (valid_indices.size()==0) {
				chosen_one = this.parent;
//				System.out.println("the chosen one of " + this.name + " is parent " + this.parent.name);
				for (Entry<String, ArrayList<Double>> entry : chosen_one.levels.entrySet()) {
				    String key = entry.getKey();
				    ArrayList<Double> value = entry.getValue();
				    double d = 1;
				    if (value.get(0) < value.get(1) ) { d = 2; }
				    TNode tempT = new TNode(key, (int)d, false);
				    this.children.add(tempT);
				}
				return;
			}
			
			if (chosen_one.gain == 0.0) {
				if (num_data > 0) {
					double npos = 0; double nneg = 0;
					for (Entry<String, ArrayList<Double>> entry : chosen_one.levels.entrySet()) {
					    ArrayList<Double> value = entry.getValue();
					    npos += value.get(0);
					    nneg += value.get(1);
					}
					TNode t;
					if (npos > nneg) 
						 t = new TNode(this.name, 1, false);
					else
						 t = new TNode(this.name, 2, false);
//					System.out.println(t);
					this.children.add(t);
					return;
				}
				else {
//					System.out.println("Im here! before reandomly generating tnode value");
					Random random = new Random();
					TNode t = new TNode(this.name, random.nextInt(2 - 1 + 1) + 1, true);
//					TNode t = new TNode(this.name, 2, true);
//					System.out.println(t);
					this.children.add(t);
					return;
				}
			}
	
//			else {
//				if (FNodes < 3) {
//					System.out.println("Building FNode " + this.chosen_one.name + " from SNode: " + this.name);
					FNode tempF = new FNode(this.information, this.valid_indices, this.chosen_one,
							this.invalid_feature_names);
					this.children.add(tempF);
					//chosen_ones.add(chosen_one);
//				}
//			}	
		}
		
		public String toString() {
			String ret = this.invalid_feature_names.toString() + "\r\n"; 
			ret += "SNode: " + this.name + ", E = " + this.entropy + ", S: " + this.information.size() + "\r\n" + "\t" 
					+ "Feature with max gain: " + chosen_one.name + " with " + chosen_one.gain + " gain.";
			ret += "\r\n" + chosen_one.toString();
			return ret;
		}
		
		public String toString1() {
			return "SNode: " + this.name + "\r\n\t" + this.information.get(0);
		}
	}
	
	public class TNode extends Node {
		public TNode(String n, int d, boolean i) {
			isRG = i;
			TNodes ++;
			this.name = n;
			this.type = "TNode";
			outcome = the_choice.all_choices[0];
			
			for (int b = 0; b < the_choice.all_choices.length; b++) {
				if (d == b+1) {
					outcome = the_choice.all_choices[b];
				}
			}
//			if (d == 2) { outcome = "<=50K"; }
		}
		
		@Override
		public void initializeChildren() {

		}
		public String toString() {
			return "TNode: " + this.name + " outcome: " + this.outcome; 
		}
	}
	
	public static void main(String[] args) {
		jw6dz hi = new jw6dz("src/changed_census.names");
		hi.train("src/changed_census.train.1000");
		hi.makePredictions("src/changed_census.test");
//		jw6dz hi = new jw6dz("src/census.names");
//		hi.train("src/practiceTrain1");
//		hi.makePredictions("src/practiceTest1");
	}
	
}
