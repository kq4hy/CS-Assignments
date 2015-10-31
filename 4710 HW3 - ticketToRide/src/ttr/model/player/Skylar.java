package ttr.model.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import ttr.model.destinationCards.*;
import ttr.model.trainCards.*;

public class Skylar extends Player {
	
	public State buy_able = new State("Buy_able");
	public State unbuy_able = new State("Unbuy_able");
	public State curr_state;
	public Routes routesList = Routes.getInstance();
	public ArrayList<Route> allRoutes = routesList.getAllRoutes();
	public HashMap<TrainCardColor, Integer> current_train_cards = new HashMap<TrainCardColor, Integer>();
	public HashMap<Route, Integer> routes_to_claim = new HashMap<Route, Integer>();
	//public ArrayList<City> Cities = new ArrayList<City>();
	public HashMap<Destination,City> CitiesMap = new HashMap<Destination,City>();
	public List<TrainCardColor> color_list = Arrays.asList(TrainCardColor.rainbow, TrainCardColor.black, 
			TrainCardColor.blue, TrainCardColor.green, TrainCardColor.orange, TrainCardColor.purple, 
			TrainCardColor.red, TrainCardColor.white, TrainCardColor.yellow); 
	
	public Skylar(String name) {
		super(name);
		initiate_actions(buy_able, unbuy_able);
		initiateGraph();
		curr_state = unbuy_able;
	}
	
	public Skylar() {
		super("Skylar");
	}

	@Override
	public void makeMove() {
		routesList = Routes.getInstance();
		allRoutes = routesList.getAllRoutes();
		compute_hand();
		update_graph();
		ArrayList<DestinationTicket> curr_destinations = this.getDestinationTickets();
		for(DestinationTicket ticket: curr_destinations) {
			ArrayList<Route> list_routes = new ArrayList<Route>();
			list_routes = getShortestPath(ticket.getFrom(), ticket.getTo());
		    System.out.println("Path: " + list_routes);
			for(Route r: list_routes){
				if(routes_to_claim.get(r) == null)
					routes_to_claim.put(r, 0);
				routes_to_claim.put(r, routes_to_claim.get(r) + r.getPoints());
			}
			CitiesMap.clear();
			initiateGraph();
		}
		
		if(curr_state == unbuy_able) {
			// calculate reward and then move
			int maximum_reward = 0;
			for(Action a: curr_state.get_actions()) {
				if(a.name.equals("a3")) {
					// a3: amount of points that the player receives from claiming a route after drawing a train color card
					// calculate the probability of getting a certain color
					double total_reward = 0;
					ArrayList<Route> curr_buyable_routes = get_buyable_routes(routes_to_claim, current_train_cards);
					if(curr_buyable_routes.size() > 0) {
						HashMap<TrainCardColor, Integer> temp_hand = current_train_cards;
						int count = 0;
						while(count != temp_hand.size() - 1) {
							count = 0;
							for(Route buy_route: curr_buyable_routes) {
								int cost = buy_route.getCost();
								TrainCardColor color_needed = buy_route.getColor();
								if(temp_hand.get(color_needed) >= cost) {
									total_reward += routes_to_claim.get(buy_route);
									temp_hand.put(color_needed, temp_hand.get(color_needed) - cost);
								} else
									count++;
							}
						}
					}
					for(TrainCardColor curr_color: color_list) {
						HashMap<TrainCardColor, Integer> temp_hand = current_train_cards;
						temp_hand.put(curr_color, temp_hand.get(curr_color) + 1);
						ArrayList<Route> buyable_routes = get_buyable_routes(routes_to_claim, temp_hand);
						double maximum_points = 0;
						if(buyable_routes.size() !=  0) {
							for(Route r : buyable_routes) {
								if(routes_to_claim.get(r) > maximum_points)
									maximum_points = routes_to_claim.get(r);
							}
						}
						total_reward += maximum_points / 9; 
					}
					a.set_reward((int)total_reward);
					if((int) total_reward > maximum_reward)
						maximum_reward = (int)total_reward;
					System.out.println("a3 reward is: " + (int)total_reward);
				} if(a.name.equals("a4")) {
					// a4: amount of points that you can get if you save up a specific color instead of buying immediately					
					// just return the maximum number of points AI can get if saving up
					int curr_maxim = 0;
					for(Map.Entry<Route, Integer> entry : routes_to_claim.entrySet()){ 
						if(entry.getValue() > curr_maxim)
							curr_maxim = entry.getValue();
					} 
					a.set_reward(curr_maxim);
					if(curr_maxim > maximum_reward)
						maximum_reward = curr_maxim;
					System.out.println("a4 reward is: " + curr_maxim);
				} if(a.name.equals("a5")) {
					// a5: 25 - sum of current points from the destination cards	
					if(curr_destinations.size() > 4 || this.getTotalDestTicketCost() >= 25)
						a.set_reward(0);
					else
						a.set_reward(25 - this.getTotalDestTicketCost());
					if(a.get_reward() > maximum_reward)
						maximum_reward = a.get_reward();	
					System.out.println("a5 reward is: " + a.get_reward());
				}
			}
		}
		else if(curr_state == buy_able){
			// calculate reward and then move
			ArrayList<Route> buyable_routes = get_buyable_routes(routes_to_claim, current_train_cards);
			Route maximum_route;
			int maximum_reward = 0;
			for(Action a: curr_state.get_actions()) {
				if (a.name.equals("a1")) {
					HashMap<TrainCardColor, Integer> temp_hand = current_train_cards;
					int count = 0;
					int sum = 0;
					while(count != temp_hand.size() - 1) {
						count = 0;
						for(Route buy_route: buyable_routes) {
							int cost = buy_route.getCost();
							TrainCardColor color_needed = buy_route.getColor();
							if(temp_hand.get(color_needed) >= cost) {
								sum += routes_to_claim.get(buy_route);
								temp_hand.put(color_needed, temp_hand.get(color_needed) - cost);
							} else
								count++;
						}
					}
					a.set_reward(sum);
					if(sum > maximum_reward) 
						maximum_reward = sum;
					System.out.println("a1 reward is: " + sum);
					// a1: the number of points that the player gets for claiming that route as well as all subsequent routes that it can claim
					
				} if(a.name.equals("a2")) {
					// a2: number of points that the player can receive for claiming that single route and then going back to the sate of un-buy-able
					int maxi = 0;
					for(Route buy_route: buyable_routes) {
						if(routes_to_claim.get(buy_route) > maxi) {
							maxi = routes_to_claim.get(buy_route);
							maximum_route = buy_route;
						}
					}
					a.set_reward(maxi);
					if(maxi > maximum_reward) 
						maximum_reward = maxi;
					System.out.println("a2 reward is: " + maxi);
				}
			}
			
		}
		super.drawTrainCard(0);
	}
	
	// organizes a HashMap in accordance with how many train cards and of what color
	public void compute_hand() { 
		for(TrainCardColor a: color_list) {
			if(current_train_cards.get(a) == null)
				current_train_cards.put(a, 0);
			current_train_cards.put(a, this.getNumTrainCardsByColor(a));
		}
	}
	
	// returns all the routes player can buy in the current routes to claim
	public ArrayList<Route> get_buyable_routes(HashMap<Route, Integer> curr_routes, HashMap<TrainCardColor, Integer> curr_hand) {
		ArrayList<Route> ret_route = new ArrayList<Route>();
		for(Map.Entry<Route, Integer> entry : curr_routes.entrySet()){ 
			TrainCardColor color_req = entry.getKey().getColor();
			int cost = entry.getKey().getCost();
			int num_trains = curr_hand.get(color_req);
			if(cost <= num_trains) 
				ret_route.add(entry.getKey());
		}
		return ret_route;
	}
	
	public void initiate_actions(State buy, State cant_buy) {
		buy.add_action(new Action("a1", buy, buy));
		buy.add_action(new Action("a2", buy, cant_buy));
		cant_buy.add_action(new Action("a3", cant_buy, buy));
		cant_buy.add_action(new Action("a4", cant_buy, cant_buy));
		cant_buy.add_action(new Action("a5", cant_buy, cant_buy));
	}
	
	public void update_graph() {
		for (int i = 0; i < this.allRoutes.size(); i++) {
			Route r = this.allRoutes.get(i);
			Player owner = r.getOwner(); // null if unclaimed
			City c1 = CitiesMap.get(r.getDest1());
			City c2 = CitiesMap.get(r.getDest2());
			if (owner != null) {
				if (owner.getName() != "Skylar") {
					//go into CitiesMaps, find cities associated with r.getDest1 and r.getDest2
					//go into their adjacencies and set the Path object�s cost to 999.
					c1.findPath(r.getDest2()).weight = 999;
					c2.findPath(r.getDest1()).weight = 999;
				}
				else if (owner.getName() == "Skylar") {
					//if we already own it then set weights to 0
					c1.findPath(r.getDest2()).weight = 0;
					c2.findPath(r.getDest1()).weight = 0;
				}
			}
		}
	}
	
	public void initiateGraph() {
		for (int i = 0; i < this.allRoutes.size(); i++) {
			Route r = this.allRoutes.get(i);
			if (r.getDest1() != r.getDest2()) {
				if (!CitiesMap.containsKey(r.getDest1())) { //if Cities doesn't contain Dest1 City
					City c = new City(r.getDest1());
					Path p = new Path(new City(r.getDest2()), r.getCost(), r.getColor());
					c.adjacencies.add(p);
					CitiesMap.put(r.getDest1(), c);
				}
				else { //Cities contains Dest1 City
					City c = CitiesMap.get(r.getDest1());
					Path p = new Path(new City(r.getDest2()), r.getCost(), r.getColor());
					if (!c.containsPath(p)) {
						c.adjacencies.add(p);
						CitiesMap.remove(r.getDest1()); //update
						CitiesMap.put(r.getDest1(), c); 
					}
				}
				if (!CitiesMap.containsKey(r.getDest2())) { //if Cities doesn't contain Dest1 City
					City c = new City(r.getDest2());
					Path p = new Path(new City(r.getDest1()), r.getCost(), r.getColor());
					c.adjacencies.add(p);
					CitiesMap.put(r.getDest2(), c);
				}
				else { //Cities contains Dest1 City
					City c = CitiesMap.get(r.getDest2());
					Path p = new Path(new City(r.getDest1()), r.getCost(), r.getColor());
					if (!c.containsPath(p)) {
						c.adjacencies.add(p);
						CitiesMap.remove(r.getDest2()); //update
						CitiesMap.put(r.getDest2(), c); 
					}
				}
			}
		}
	}
	
    static class City implements Comparable<City>
	{
	    public final Destination name;
	    public ArrayList<Path> adjacencies = new ArrayList<Path>();
	    public int minDistance = 999;
	    public City previous;
	    
	    public City(Destination argName) { name = argName; }
	    
	    public String toString() { return name.toString(); }
	    
	    public int compareTo(City other)
	    {
	        return Integer.compare(minDistance, other.minDistance);
	    }
	    
	    public boolean equals(City other) {
	    	return this.name == other.name;
	    }
	    
	    public void exfoliate() {
	    	System.out.print(this.name + ": ");
	    	for (Path p: adjacencies) {
	    		System.out.print(p.target.name + ", " + p.weight + " ");
	    	}
	    	System.out.println("");
	    }
	    
	    public boolean containsPath(Path p) {
	    	for (Path pat: this.adjacencies) {
	    		if (pat.target.name == p.target.name) {
	    			return true;
	    		}
	    	} return false;
	    }
	    
	    public Path findPath(Destination n) {
	    	for (Path c: this.adjacencies) {
	    		if (c.target.name == n) {
	    			return c;
	    		}
	    	}
	    	return new Path(null, 999, null);
	    }
	}
	
	static class Path
	{
	    public final City target;
	    public int weight;
	    public final TrainCardColor color;
	    
	    public Path(City argTarget, int argWeight, TrainCardColor argColor) { 
	    	target = argTarget; 
	    	weight = argWeight; 
	    	color = argColor;
	    }
	}
	
	public void computePaths(City source, HashMap<Destination,City> cities)
	{
	    source.minDistance = 0;
	    PriorityQueue<City> vertexQueue = new PriorityQueue<City>();
	    vertexQueue.add(source);

	    while (!vertexQueue.isEmpty()) {
	        City u = vertexQueue.poll();
//	         Visit each edge exiting u
	        for (Path e : u.adjacencies)
	        {
	            //City v = e.target; //***
	            City v = cities.get(e.target.name);
	            int weight = e.weight;
	            int distanceThroughU = u.minDistance + weight;
	            if (distanceThroughU < v.minDistance) {
	            	vertexQueue.remove(v);
	            	v.minDistance = distanceThroughU ;
	                v.previous = u;
	                vertexQueue.add(v);
	            }	      
	        }
	    }
	}
	
	public ArrayList<Route> getShortestPathTo(City target) {
		ArrayList<Route> routes = new ArrayList<Route>();

		Route temp;
	    for (City vertex = target; vertex != null; vertex = vertex.previous) {
	    	if (vertex.previous != null) {
	    		Path p = vertex.findPath(vertex.previous.name);
	    		temp = new Route(vertex.previous.name, vertex.name, p.weight, p.color);
	    		routes.add(temp); 
	    	}
	    }
	    Collections.reverse(routes);
	    return routes;
	}
	
	//Takes in two Destinations and calculates the shortest path from start to dest
	//Doesn't work while tested in this main b/c Routes.getInstance() doesn't work in this main,
	//   but should work when run in TTRMain (not tested but test it for me!)
	public ArrayList<Route> getShortestPath(Destination start, Destination dest) {
		//this.CitiesMap.clear(); 
		//this.initiateGraph();
		this.update_graph();
		this.computePaths(this.CitiesMap.get(start), this.CitiesMap);
		
		ArrayList<Route> routes = new ArrayList<Route>();

		Route temp;
	    for (City vertex = this.CitiesMap.get(dest); vertex != null; vertex = vertex.previous) {
	    	if (vertex.previous != null) {
	    		Path p = vertex.findPath(vertex.previous.name);
	    		temp = new Route(vertex.previous.name, vertex.name, p.weight, p.color);
	    		routes.add(temp); 
	    	}
	    }
	    Collections.reverse(routes);
	    return routes;
		
	}
	
	static class State {
		public final String name;
		public int reward = 0;
		public ArrayList<Action> actions = new ArrayList<Action>();
		
		public State(String n) { name = n; }
		public void add_action(Action a) { actions.add(a); }
		public ArrayList<Action> get_actions() {
			return actions;
		}
	}
	
	static class Action {
		public String name;
		public int reward;
		public double chance;
		public State currState;
		public State nextState;
		public Action(String name_,State curr_state, State next_state) {
			name = name_;
			reward = 0;
			chance = 0;
			currState = curr_state;
			nextState = next_state;
		}
		public int get_reward() { return reward; }
		public void set_reward(int i) { reward = i; }
	}
	
//	public static void main(String[] args) {
//		Skylar testSkylar = new Skylar();
//		testSkylar.initiateGraph();
//		for (City c: testSkylar.CitiesMap.values())
//			c.exfoliate();
//		
//		computePaths(testSkylar.CitiesMap.get(Destination.LosAngeles), testSkylar.CitiesMap);
//		for (City v : testSkylar.CitiesMap.values())
//		{
//		    System.out.println("Distance to " + v + ": " + v.minDistance);
//		    List<Route> routes = getShortestPathTo(v);
//		    System.out.println("Path: " + routes);
//		}
//		//testSkylar.getShortestPath(Destination.LosAngeles, Destination.Chicago);
//		
//	}
}
