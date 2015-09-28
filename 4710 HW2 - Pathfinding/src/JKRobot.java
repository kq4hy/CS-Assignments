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
        super.addToWorld(world); 
    }
    
    @Override
    public void travelToDestination() {
    	if(is_uncertain) {
    		// do something that involves randomness
    	} else {
    		for(int vert = 0; vert < rows; vert++) {
    			for(int horiz = 0; horiz < cols; horiz++) {
    				String ping = pingMap(new Point(vert, horiz));
    				if(!ping.equals("X")) {
    					int heur = (int)(Math.abs(end_pos.getX() - vert) + Math.abs(end_pos.getY() - horiz));
        				world_map[vert][horiz] = new Block(0, heur, null, vert, horiz);
    				} else {
    					world_map[vert][horiz] = new Block(0, -10, null, vert, horiz);
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
    		
    		Point current_postition = start_pos;
    		int start_x = (int)start_pos.getX();
    		int start_y = (int)start_pos.getY();
    		int finish_x = (int)end_pos.getX();
    		int finish_y = (int)end_pos.getY();
    		closed_list.add(world_map[start_x][start_y]); // add initial starting position to closed list
    		add_adjacent_blocks(start_x, start_y, cols, rows); // add adjacent positions to open list
    		while(open_list.size() > 0) { // while open list is not empty
    			int index = open_totals.indexOf(Collections.min(open_totals));
    			Block current_block = open_list.get(index);
    			closed_list.add(current_block);
    			open_list.remove(current_block);
    			open_totals.remove(index);
    			//if (Math.abs(curr_x - finish_x) <= 1 || Math.abs(curr_y - finish_y) <= 1) { // check if current spot is adjacent to finish
    			if (current_block.getX() == finish_x && current_block.getY() == finish_y) {	
    				break;
    			}
    			add_adjacent_blocks(current_block.getX(), current_block.getY(), cols, rows);
    		}
    		world_map[start_x][start_y].setParent(null);
    		move_robot(world_map[finish_x][finish_y]);
    	}
        
    }
    
    public void move_robot(Block destination) {
    	if (!destination.has_parent()) { return; }
    	else {
    		move_robot(destination.getParent());
    		super.move(new Point(destination.getX(), destination.getY()));
    	}
    		
    }
    
    public void add_adjacent_blocks(int x, int y, int max_x, int max_y) { // adds all valid adjacent blocks to open_list
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
    
    public Block get_block(int x, int y) { // returns the block associated with the x and y
    	return world_map[x][y];
    }
    
    public boolean is_obstacle(int x, int y) {
    	return world_map[x][y].getHeuristic() < 0;
    }
    
    public static void main(String[] args) {
        try{
            World myWorld = new World("TestCases/myInputFile4.txt", false);
            JKRobot myRobot = new JKRobot();
            myWorld.createGUI(1300, 700, 300);
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