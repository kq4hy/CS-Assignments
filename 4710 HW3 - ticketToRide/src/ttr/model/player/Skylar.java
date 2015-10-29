package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import ttr.model.destinationCards.*;
import ttr.model.trainCards.*;

public class Skylar extends Player {
	
	public Routes routesList = new Routes();
	public ArrayList<Route> allRoutes = routesList.getAllRoutes();
	//public ArrayList<City> Cities = new ArrayList<City>();
	public HashMap<Destination,City> CitiesMap = new HashMap<Destination,City>();
	
	public Skylar(String name) {
		super(name);
	}
	
	public Skylar() {
		super("Skylar");
	}

	@Override
	public void makeMove() {
		// TODO Auto-generated method stub

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
	    public final int weight;
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
	
//	public static List<City> getShortestPathTo(City target)
//	{
//	    List<City> path = new ArrayList<City>();
//	    for (City vertex = target; vertex != null; vertex = vertex.previous)
//	        path.add(vertex);
//
//	    Collections.reverse(path);
//	    return path;
//	}
	
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
	
	static class State {
		public final String name;
		public int reward = 0;
		public ArrayList<Edge> actions = new ArrayList<Edge>();
		public State(String n) {
			name = n;
		}
		
	}
	
	static class Edge {
		public String name;
		public int reward;
		public double chance;
		public State currState;
		public State nextState;
		public Edge() {
			
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
		
	}
}
