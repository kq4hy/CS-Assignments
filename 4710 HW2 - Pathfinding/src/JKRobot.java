import world.Robot;
import world.World;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Created by lifereborn on 9/25/15.
 */
public class JKRobot extends Robot{
	private boolean is_uncertain = false;
	private Point start_pos;
	private Point end_pos;
	private int cols;
	private int rows;
	private int direc_x;
	private int direc_y;
	private int start_x;
	private int start_y;
	private int finish_x;
	private int finish_y;
	private ArrayList<Block> open_list = new ArrayList<Block>();
	private ArrayList<Block> closed_list = new ArrayList<Block>();
	private ArrayList<Integer> open_totals = new ArrayList<Integer>();
	private Block world_map[][]; 
	
    public JKRobot() { }
    
    @Override 
    public void addToWorld(World world) { 
        cols = world.numCols(); 
        rows = world.numRows();
        start_pos = world.getStartPos();
        end_pos = world.getEndPos();
        is_uncertain = world.getUncertain();
        world_map = new Block[rows][cols];
        direc_x = 0;
        direc_y = 0;
        super.addToWorld(world); 
    }
    
    @Override
    public void travelToDestination() {
    	start_x = (int)start_pos.getX();
		start_y = (int)start_pos.getY();
		finish_x = (int)end_pos.getX();
		finish_y = (int)end_pos.getY();
		
		// set up world_map with blocks containing the heuristics 
		for(int vert = 0; vert < rows; vert++) {
			for(int horiz = 0; horiz < cols; horiz++) {
				if(is_uncertain) { // if map is uncertain, calculate heuristics for every position
					int heur = (int)(Math.abs(end_pos.getX() - vert) + Math.abs(end_pos.getY() - horiz));
					world_map[vert][horiz] = new Block(0, heur, null, vert, horiz);
				} else {
					String ping = pingMap(new Point(vert, horiz));
					if(!ping.equals("X")) { //if map is certain, calculate heuristics based on obstacle or not
						int heur = (int)(Math.abs(end_pos.getX() - vert) + Math.abs(end_pos.getY() - horiz));
	    				world_map[vert][horiz] = new Block(0, heur, null, vert, horiz);
					} else {
						world_map[vert][horiz] = new Block(0, -10, null, vert, horiz);
					}
				}
			}
		}
		
		/**  TESTING HEURISTIC CALCULATIONS
		for(int vert = 0; vert < rows; vert++) {
			System.out.print("[");
			for(int horiz = 0; horiz < cols; horiz++) {
				System.out.print(world_map[vert][horiz].getHeuristic() + ", ");
			}
			System.out.print("]\n");
		} **/
		
    	if(is_uncertain) {
    		int current_x = (int)(getPosition().getX());
			int current_y = (int)(getPosition().getY());
    		String direction = calc_direc(start_x, start_y, finish_x, finish_y); // calculate which direction is ideal
    		while(current_x != finish_x || current_y != finish_y) {
    			Point new_spot = super.move(new Point(current_x + direc_x, current_y + direc_y)); // start traveling in that direction
				if(new_spot.getX() == current_x && new_spot.getY() == current_y) { // did not move
					String move_direc = set_ping_direc(direction, current_x + direc_x, current_y + direc_y, current_x, current_y);
					System.out.println("First opening is: " + move_direc);
					set_direction(move_direc);
					super.move(new Point(current_x + direc_x, current_y + direc_y));
				}
				current_x = (int)(getPosition().getX());
    			current_y = (int)(getPosition().getY());
    			world_map[current_x][current_y].setHeuristic(-10);
				direction = calc_direc(current_x, current_y, finish_x, finish_y);
    		}
    	} else {
    		closed_list.add(world_map[start_x][start_y]); // add initial starting position to closed list
    		add_adjacent_blocks(start_x, start_y, cols, rows); // add adjacent positions to open list
    		while(open_list.size() > 0) { // while open list is not empty
    			int index = open_totals.indexOf(Collections.min(open_totals));
    			Block current_block = open_list.get(index);
    			closed_list.add(current_block);
    			open_list.remove(current_block);
    			open_totals.remove(index);
    			//if (Math.abs(curr_x - finish_x) <= 1 || Math.abs(curr_y - finish_y) <= 1) { // check if current spot is adjacent to finish
    			if(current_block.getX() == finish_x && current_block.getY() == finish_y) {	
    				break;
    			}
    			add_adjacent_blocks(current_block.getX(), current_block.getY(), cols, rows);
    		}
    		world_map[start_x][start_y].setParent(null);
    		move_robot(world_map[finish_x][finish_y]);
    	}
    }
    
    // calls super.move to move the robot depending on the parents
    public void move_robot(Block destination) {
    	if(!destination.has_parent()) { return; }
    	else {
    		move_robot(destination.getParent());
    		super.move(new Point(destination.getX(), destination.getY()));
    	}
    }
    
    // calculate the direction that robot wants to travel to reach destination
    public String calc_direc(int curr_x, int curr_y, int final_x, int final_y) {
    	String direction = "";
		if(final_x == curr_x) { // ideally go vertical
			if(final_y - curr_y < 0) {
				set_direction("West");
				direction = "West";
			} else {
				set_direction("East");
				direction = "East";
			}
		} else if(final_y == curr_y) { // ideally go horizontal
			if(final_x - curr_x < 0) { 
				set_direction("North");
				direction = "North";
			} else {
				set_direction("South");
				direction = "South";
			}
		} else if(final_x - curr_x < 0) {
			if(final_y - curr_y < 0) {
				set_direction("Northwest");
				direction = "Northwest";
			} else {
				set_direction("Northeast");
				direction = "Northeast";
			}
		} else {
			if(final_y - curr_y < 0) {
				set_direction("Southwest");
				direction = "Southwest";
			} else {
				set_direction("Southeast");
				direction = "Southeast";
			}
		}
		return direction;
    }
    
    // calculates the adjacent blocks and adds them to open_list
    public void add_adjacent_blocks(int x, int y, int max_x, int max_y) { 
    	for(int loop_y = 1; loop_y < 10; loop_y += 3) {
    		int remain_y = loop_y / 3 - 1;
    		for(int loop_x = 1; loop_x < 10; loop_x += 3) {
    			if(loop_x == 4 && loop_y == 4) {
    				continue;
    			}
    			int remain_x = loop_x / 3 - 1;
    			try {
    				Block adj_block = world_map[x + remain_x][y + remain_y];
    				// check if adjacent block is obstacle and if the closed list contains it 
    				if(!is_obstacle(x + remain_x, y + remain_y)) {
    					if(closed_list.contains(adj_block)) { // closed list contains adjacent block already
    						continue;
    					} else if(!closed_list.contains(adj_block) && !open_list.contains(adj_block)) { // both lists do not contain adjacent
    						adj_block.setParent(get_block(x, y)); 
	    					int temp_cost = (int)adj_block.getCost();
	    					adj_block.setCost(temp_cost++);
		    				open_list.add(adj_block); // add to open list and change cost and parent Block
		    				open_totals.add(adj_block.getTotal());
		    				world_map[x + remain_x][y + remain_y] = adj_block;
    					} else if(open_list.contains(adj_block)) { // open list contains adjacent block, figure out if need to change parent
    						continue;
    					} 
	    			}
    			} catch (Exception e) {
    				continue;
    			}
    		}
		}
    	
    	/** TESTING ADD_ADJACENT_BLOCKS
    	for(Block p: open_list) {
    		System.out.print(p.getTotal() + ", ");
    	}
    	System.out.println(""); */
    }
    
    // sets the direction for x and y depending on which direction you want to travel in
    public void set_direction(String direc) { 
//    	System.out.println(direc);
    	if(direc.equals("North")) {
    		direc_x = -1;
    		direc_y = 0;
    	} else if(direc.equals("South")) {
    		direc_x = 1;
    		direc_y = 0;
    	} else if(direc.equals("West")) {
    		direc_x = 0;
    		direc_y = -1;
    	} else if(direc.equals("East")) {
    		direc_x = 0;
    		direc_y = 1;
    	} else if(direc.equals("Northwest")) {
    		direc_x = -1;
    		direc_y = -1;
    	} else if(direc.equals("Northeast")) {
    		direc_x = -1;
    		direc_y = 1;
    	} else if(direc.equals("Southwest")) {
    		direc_x = 1;
    		direc_y = -1;
    	} else if(direc.equals("Southeast")) {
    		direc_x = 1;
    		direc_y = 1;
    	}
    }
    
    // ping depending on the direction you want to travel, returns the direction of first opening
    public String set_ping_direc(String opt_direc, int curr_x, int curr_y, int robot_x, int robot_y) { 
    	System.out.println("Current x: " + curr_x + " , Current y: " + curr_y + ", Optimal direction is: " + opt_direc);
    	boolean open_spot = false;
    	int index_vert = 1;
    	int index_horiz = 1;
    	while(!open_spot) {
    		String west = "";
    		String east = "";
    		String north = "";
    		String south = "";
    		boolean east_obstacle = false;
    		boolean west_obstacle = false;
    		boolean north_obstacle = false;
    		boolean south_obstacle = false;
    		
    		// trying to go North or South, check East and West
    		if(opt_direc.equals("North") || opt_direc.equals("South") ) { 
    			if(curr_y - index_horiz < 0 && curr_y + index_horiz >= cols) // can't go East or West anymore
    				index_horiz = 1;
    			else if(curr_y - index_horiz < 0) { // can't go West, only set East
    				east = pingMap(new Point(curr_x, curr_y + index_horiz));
    				east_obstacle = is_obstacle(robot_x, robot_y + 1);
    			} else if(curr_y + index_horiz >= cols) { // can't go East, only set West
    				west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				west_obstacle = is_obstacle(robot_x, robot_y - 1);
    			} else { // can go in both directions
    				east = pingMap(new Point(curr_x, curr_y + index_horiz));
    				west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				east_obstacle = is_obstacle(robot_x, robot_y + 1);
    				west_obstacle = is_obstacle(robot_x, robot_y - 1);
    			}
				
				if(east_obstacle) { // we came from the east, keep going west
					return "West";
				} else if(west_obstacle) { // we came from the west, keep going east
					return "East";
				}
    			
    			if(east.equals("X") && west.equals("X")) {
    				index_horiz++;
    				continue;
    			} else if(east.equals("O") && west.equals("O") && index_horiz == 1) {
    				if((int)(Math.random() * 2) == 0)
    					return opt_direc + "west";
    				return opt_direc + "east";
    			} else if(east.equals("O") && west.equals("O")) {
    				if((int)(Math.random() * 2) == 0)
    					return "West";
    				return "East";
    			} else if(east.equals("O") && index_horiz == 1) {
    				return opt_direc + "east";
    			} else if(west.equals("O") && index_horiz == 1) {
    				return opt_direc + "west";
    			} else if(east.equals("O")) {
    				return "East";
    			} else if(west.equals("O")) {
    				return "West";
    			} else {
    				index_horiz++;
    				continue;
    			}
    		} 
    		
    		// trying to go East or West, check North and South
    		else if(opt_direc.equals("West") || opt_direc.equals("East")) {
    			if(curr_x - index_vert < 0 && curr_x + index_vert >= rows) // can't go North or South anymore
    				index_vert = 1;
    			else if(curr_x - index_vert < 0) { // can't go North, check South
    				south = pingMap(new Point(curr_x + index_vert, curr_y));
    				south_obstacle = is_obstacle(robot_x + 1, robot_y);
    			} else if(curr_x + index_vert >= rows) { // can't go South, check North
    				north = pingMap(new Point(curr_x - index_vert, curr_y));
    				north_obstacle = is_obstacle(robot_x - 1, robot_y);
    			} else { // can go in both directions
    				north = pingMap(new Point(curr_x - index_vert, curr_y));
    				south = pingMap(new Point(curr_x + index_vert, curr_y));
    				north_obstacle = is_obstacle(robot_x - 1, robot_y);
    				south_obstacle = is_obstacle(robot_x + 1, robot_y);
    			}
				
				if(north_obstacle) { // we came from the north, keep going south
					return "South";
				} else if(south_obstacle) { // we came from the south, keep going north
					return "North";
				}
    			
    			if(north.equals("X") && south.equals("X")) {
    				index_vert++;
    				continue;
    			} else if(north.equals("O") && south.equals("O") && index_vert == 1) {
    				if((int)(Math.random() * 2) == 0)
    					return "North" + opt_direc.toLowerCase();
    				return "South" + opt_direc.toLowerCase();
    			} else if(north.equals("O") && south.equals("O")) {
    				if((int)(Math.random() * 2) == 0)
    					return "North";
    				return "South";
    			} else if(north.equals("O") && index_vert == 1) {
    				return "North" + opt_direc.toLowerCase();
    			} else if(south.equals("O") && index_vert == 1) {
    				return "South" + opt_direc.toLowerCase();
    			} else if(north.equals("O")) {
    				return "North";
    			} else if(south.equals("O")) {
    				return "South";
    			} else {
    				index_vert++;
    				continue;
    			}
    		} 
    		
    		// the other 4 directions, Southeast, Southwest, Northeast, Northwest
    		else {
    			if(curr_y - index_horiz < 0 && curr_y + index_horiz >= cols && curr_x - index_vert < 0 && curr_x + index_vert >= rows) { 
    				index_vert = 1;
    				index_horiz = 1;
    			} else if(curr_x - index_vert < 0 && curr_x + index_vert >= rows) { // can't go North or South anymore, check only East and West
    				if(curr_y - index_horiz < 0)
    					return "East";
    				else if(curr_y + index_horiz >= cols)
    					return "West";
	    			west = pingMap(new Point(curr_x, curr_y - index_horiz));
	    			east = pingMap(new Point(curr_x, curr_y + index_horiz));
    			} else if(curr_y - index_horiz < 0 && curr_y + index_horiz >= cols) { // can't go East or West anymore, check only North and South
    				if(curr_x - index_vert < 0)
    					return "South";
    				else if(curr_x + index_vert >= rows) 
    					return "North";
    				north = pingMap(new Point(curr_x + index_vert, curr_y));
        			south = pingMap(new Point(curr_x - index_vert, curr_y));
    			} else if(curr_x - index_vert < 0) { // can't go North anymore
    				south = pingMap(new Point(curr_x + index_vert, curr_y));
    				if(curr_y - index_horiz < 0) // can't go West anymore
    					east = pingMap(new Point(curr_x, curr_y + index_horiz));
    				else if(curr_y + index_horiz >= cols) // can't go East anymore 
    					west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				else {
    					east = pingMap(new Point(curr_x, curr_y + index_horiz));
    					west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				}
    			} else if(curr_x + index_vert >= rows) { // can't go South anymore
    				north = pingMap(new Point(curr_x - index_vert, curr_y));
    				if(curr_y - index_horiz < 0) // can't go West anymore
    					east = pingMap(new Point(curr_x, curr_y + index_horiz));
    				else if(curr_y + index_horiz >= cols) // can't go East anymore 
    					west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				else {
    					east = pingMap(new Point(curr_x, curr_y + index_horiz));
    					west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				}						
    			} else if(curr_y - index_horiz < 0) { // can't go East anymore
    				north = pingMap(new Point(curr_x - index_vert, curr_y));
    				south = pingMap(new Point(curr_x + index_vert, curr_y));
    				west = pingMap(new Point(curr_x, curr_y - index_horiz));
    			} else if(curr_y + index_horiz >= cols) { // can't go West anymore
    				north = pingMap(new Point(curr_x - index_vert, curr_y));
    				south = pingMap(new Point(curr_x + index_vert, curr_y));
    				east = pingMap(new Point(curr_x, curr_y + index_horiz));
    			} else { // can go in all directions
    				north = pingMap(new Point(curr_x - index_vert, curr_y));
    				south = pingMap(new Point(curr_x + index_vert, curr_y));
    				west = pingMap(new Point(curr_x, curr_y - index_horiz));
    				east = pingMap(new Point(curr_x, curr_y + index_horiz));
    			}

    			if(!north.equals(""))
    				north_obstacle = is_obstacle(curr_x - index_vert, curr_y);
    			if(!south.equals(""))
    				south_obstacle = is_obstacle(curr_x + index_vert, curr_y);
    			if(!west.equals(""))
    				west_obstacle = is_obstacle(curr_x, curr_y + index_horiz);
    			if(!east.equals(""))
    				east_obstacle = is_obstacle(curr_x, curr_y + index_horiz);
    			
    			// if all are obstacles, then continue to spread out
    			if(north.equals("X") && south.equals("X") && west.equals("X") && east.equals("X")) {
    				index_horiz++;
    				index_vert++;
    			}
    			
    			// special case where one in either direction is a different than actual direction due to the nature of diagonal
    			if(opt_direc.equals("Southwest")) {
    				if(index_horiz == 1 && index_vert == 1 && east.equals("O") && north.equals("O")) {
    					if((int)(Math.random()*2) == 0)
        					return "West";
        				return "South";
    				} else if(index_horiz == 1 && east.equals("O"))
    					return "South";
    				else if(index_vert == 1 && north.equals("O"))
    					return "West";
    			} else if(opt_direc.equals("Northwest")) {
    				if(index_horiz == 1 && index_vert == 1 && east.equals("O") && south.equals("O")) {
    					if((int)(Math.random()*2) == 0)
        					return "West";
        				return "North";
    				} else if(index_horiz == 1 && east.equals("O"))
    					return "North";
    				else if(index_vert == 1 && south.equals("O"))
    					return "West";
    			} else if(opt_direc.equals("Southeast")) {
    				if(index_horiz == 1 && index_vert == 1 && west.equals("O") && north.equals("O")) {
    					if((int)(Math.random()*2) == 0)
        					return "East";
        				return "South";
    				} else if(index_horiz == 1 && west.equals("O"))
    					return "South";
    				else if(index_vert == 1 && north.equals("O"))
    						return "East";
    			} else if(opt_direc.equals("Northeast")) {
    				if(index_horiz == 1 && index_vert == 1 && west.equals("O") && south.equals("O")) {
    					if((int)(Math.random()*2) == 0)
        					return "East";
        				return "North";
    				} else if(index_horiz == 1 && west.equals("O"))
    					return "North";
    				else if(index_vert == 1 && south.equals("O"))
    					return "East";
    			}
    			
    			if(north.equals("O") && south.equals("O") && west.equals("O") && east.equals("O")) {
    				int rand_num = (int)(Math.random() * 4);
    				if(rand_num == 0) 
    					return "West";
    				else if(rand_num == 1)
    					return "East";
    				else if(rand_num == 2)
    					return "North";
    				return "South";
    			} else if(north.equals("O") && south.equals("O") && east.equals("O")) {
    				int rand_num = (int)(Math.random() * 3);
    				if(rand_num == 0) 
    					return "North";
    				else if(rand_num == 1)
    					return "South";
    				return "East";
    			} else if(north.equals("O") && south.equals("O") && west.equals("O")) {
    				int rand_num = (int)(Math.random() * 3);
    				if(rand_num == 0) 
    					return "North";
    				else if(rand_num == 1)
    					return "South";
    				return "West";
    			} else if(north.equals("O") && east.equals("O") && west.equals("O")) {
    				int rand_num = (int)(Math.random() * 3);
    				if(rand_num == 0) 
    					return "North";
    				else if(rand_num == 1)
    					return "East";
    				return "West";
    			} else if(east.equals("O") && south.equals("O") && west.equals("O")) {
    				int rand_num = (int)(Math.random() * 3);
    				if(rand_num == 0) 
    					return "East";
    				else if(rand_num == 1)
    					return "South";
    				return "West";
    			} else if(north.equals("O") && south.equals("O")) {
    				if((int)(Math.random() * 2) == 0) 
    					return "North";
    				return "South";
    			} else if(north.equals("O") && east.equals("O")) {
    				if((int)(Math.random() * 2) == 0) 
    					return "North";
    				return "East";
    			} else if(east.equals("O") && south.equals("O")) {
    				if((int)(Math.random() * 2) == 0) 
    					return "East";
    				return "South";
    			} else if(north.equals("O") && west.equals("O")) {
    				if((int)(Math.random() * 2) == 0) 
    					return "North";
    				return "West";
    			} else if(south.equals("O") && west.equals("O")) {
    				if((int)(Math.random() * 2) == 0) 
    					return "West";
    				return "South";
    			} else if(east.equals("O") && west.equals("O")) {
    				if((int)(Math.random() * 2) == 0) 
    					return "East";
    				return "West";
    			} else if (north.equals("O")) {
    				return "North";
    			} else if(south.equals("O")) {
    				return "South";
    			} else if(east.equals("O")) {
    				return "East";
    			} else {
    				return "West";
    			}
    		}
    	}
    	return "Direction does not ever exist for some odd reason.";
    }
    
    public void set_new_location(int x, int y) {
    	finish_x = x;
    	finish_y = y;
    }
    
    // returns the block associated with the x and y
    public Block get_block(int x, int y) { 
    	return world_map[x][y];
    }
    
    // 	returns if location of x and y is an obstacle or not
    public boolean is_obstacle(int x, int y) {
    	return world_map[x][y].getHeuristic() < 0;
    }
    
    public static void main(String[] args) {
        try{
            World myWorld = new World("TestCases/myInputFile4.txt", true);
            JKRobot myRobot = new JKRobot();
            myWorld.createGUI(700, 500, 500);
            myRobot.addToWorld(myWorld);
            myRobot.travelToDestination();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

class Block {
	private int cost;
	private int heuristic;
	private int total;
	private Block parent;
	private int x;
	private int y;
	
	public Block() {
		this.cost = 0;
		this.heuristic = 0;
		this.total = 0;
		this.parent = null;
	}
	
	public Block(int c, int h, Block p, int _x, int _y) {
		this.cost = c;
		this.heuristic = h;
		this.total = c + h;
		this.parent = p;
		this.x = _x;
		this.y = _y;
	}
	
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Block getParent() {
		return parent;
	}

	public void setParent(Block p) {
		parent = p;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public boolean has_parent() {
		return parent != null;
	}
}