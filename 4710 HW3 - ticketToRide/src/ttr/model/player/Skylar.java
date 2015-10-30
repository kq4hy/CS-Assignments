package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import ttr.model.destinationCards.*;
import ttr.model.trainCards.*;

public class Skylar extends Player {
	
	public State buy_able = new State("Buy_able");
	public State unbuy_able = new State("Unbuy_able");
	public State curr_state;
	public Routes routesList = Routes.getInstance();
	public ArrayList<Route> allRoutes = routesList.getAllRoutes();
	public HashMap<Route, Integer> routes_to_claim = new HashMap<Route, Integer>();
	//public ArrayList<City> Cities = new ArrayList<City>();
	public HashMap<Destination,City> CitiesMap = new HashMap<Destination,City>();
	
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
		update_graph();
		ArrayList<DestinationTicket> curr_destinations = this.getDestinationTickets();
		for(DestinationTicket ticket: curr_destinations) {
			computePaths(CitiesMap.get(ticket.getFrom()), this.CitiesMap);
			System.out.println("Distance from " + CitiesMap.get(ticket.getFrom()) + " to " + CitiesMap.get(ticket.getTo()) + ": " + CitiesMap.get(ticket.getTo()).minDistance);
			List<Route> list_routes = getShortestPathTo(CitiesMap.get(ticket.getTo()));
		    System.out.println("Path: " + list_routes);
			for(Route r: list_routes){
				if(routes_to_claim.get(r) == null)
					routes_to_claim.put(r, 0);
				routes_to_claim.put(r, routes_to_claim.get(r) + r.getPoints());
			}
		}
		super.drawTrainCard(0);
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
	
	public static void computePaths(City source, HashMap<Destination,City> cities)
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
	
	public static List<Route> getShortestPathTo(City target) {
		List<Route> routes = new ArrayList<Route>();

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
	public List<Route> getShortestPath(Destination start, Destination dest) {
		//this.CitiesMap.clear(); 
		//this.initiateGraph();
		this.update_graph();
		this.computePaths(this.CitiesMap.get(start), this.CitiesMap);
		
		List<Route> routes = new ArrayList<Route>();

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
		public State(String n) {
			name = n;
		}
		public void add_action(Action a) {
			actions.add(a);
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
	}
	
	public static void main(String[] args) {
		Skylar testSkylar = new Skylar();
		testSkylar.initiateGraph();
		for (City c: testSkylar.CitiesMap.values())
			c.exfoliate();
		
		computePaths(testSkylar.CitiesMap.get(Destination.LosAngeles), testSkylar.CitiesMap);
		for (City v : testSkylar.CitiesMap.values())
		{
		    System.out.println("Distance to " + v + ": " + v.minDistance);
		    List<Route> routes = getShortestPathTo(v);
		    System.out.println("Path: " + routes);
		}
		//testSkylar.getShortestPath(Destination.LosAngeles, Destination.Chicago);
		
	}
}
