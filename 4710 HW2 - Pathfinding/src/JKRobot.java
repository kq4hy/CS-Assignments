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
    	int start_x = (int)start_pos.getX();
		int start_y = (int)start_pos.getY();
		int finish_x = (int)end_pos.getX();
		int finish_y = (int)end_pos.getY();
		
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
    		Block current_block = world_map[start_x][start_y];
    		calc_direc(start_x, start_y, finish_x, finish_y); // calculate which direction is ideal
    		while(current_block.getX() != finish_x || current_block.getY() != finish_y) {
    			int current_x = current_block.getX();
    			int current_y = current_block.getY();
				super.move(new Point(current_x + direc_x, current_y + direc_y)); // start traveling in that direction
				current_block = world_map[current_x + direc_x][current_y + direc_y];
				if(current_block.getX() == current_x && current_block.getY() == current_y) { // did not move so implement logic here
					
					// if you cannot move in that direction then ping the map until you find the first opening and go that way
					// re-ping each move to figure out if there's an opening	
				}
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
    public void calc_direc(int curr_x, int curr_y, int final_x, int final_y) {
		if(final_x == curr_x) { // ideally go vertical
			if(final_y - curr_y < 0)
				set_direction("West");
			else 
				set_direction("East");
		} else if(final_y == curr_y) { // ideally go horizontal
			if(final_x - curr_x < 0) 
				set_direction("North");
			else
				set_direction("South");
		} else if(final_x - curr_x < 0) {
			if(final_y - curr_y < 0)
				set_direction("Northwest");
			else
				set_direction("Northeast");
		} else {
			if(final_y - curr_y < 0) 
				set_direction("Southwest");
			else
				set_direction("Southeast");
		}
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
    	System.out.println(direc);
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
    
    public Block get_block(int x, int y) { // returns the block associated with the x and y
    	return world_map[x][y];
    }
    
    public boolean is_obstacle(int x, int y) { // returns if location of x and y is an obstacle or not
    	return world_map[x][y].getHeuristic() < 0;
    }
    
    public static void main(String[] args) {
        try{
            World myWorld = new World("TestCases/myInputFile2.txt", true);
            JKRobot myRobot = new JKRobot();
            myWorld.createGUI(700, 500, 300);
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