package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import ttr.model.destinationCards.*;

public class Skylar extends Player {
	
	public Routes routesList = new Routes();
	public ArrayList<Route> allRoutes = routesList.getAllRoutes();
	//public ArrayList<City> Cities = new ArrayList<City>();
	public HashMap<String,City> CitiesMap = new HashMap<String,City>();
	
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
				if (!CitiesMap.containsKey(r.getDest1().toString())) { //if Cities doesn't contain Dest1 City
					City c = new City(r.getDest1().toString());
					Path p = new Path(new City(r.getDest2().toString()), r.getCost(), r.getColor().toString());
					c.adjacencies.add(p);
					CitiesMap.put(r.getDest1().toString(), c);
				}
				else { //Cities contains Dest1 City
					City c = CitiesMap.get(r.getDest1().toString());
					Path p = new Path(new City(r.getDest2().toString()), r.getCost(), r.getColor().toString());
					if (!c.containsPath(p)) {
						c.adjacencies.add(p);
						CitiesMap.remove(r.getDest1().toString()); //update
						CitiesMap.put(r.getDest1().toString(), c); 
					}
				}
				if (!CitiesMap.containsKey(r.getDest2().toString())) { //if Cities doesn't contain Dest1 City
					City c = new City(r.getDest2().toString());
					Path p = new Path(new City(r.getDest1().toString()), r.getCost(), r.getColor().toString());
					c.adjacencies.add(p);
					CitiesMap.put(r.getDest2().toString(), c);
				}
				else { //Cities contains Dest1 City
					City c = CitiesMap.get(r.getDest2().toString());
					Path p = new Path(new City(r.getDest1().toString()), r.getCost(), r.getColor().toString());
					if (!c.containsPath(p)) {
						c.adjacencies.add(p);
						CitiesMap.remove(r.getDest2().toString()); //update
						CitiesMap.put(r.getDest2().toString(), c); 
					}
				}
			}
		}
	}
	
    static class City implements Comparable<City>
	{
	    public final String name;
	    public ArrayList<Path> adjacencies = new ArrayList<Path>();
	    public int minDistance = 999;
	    public City previous;
	    public City(String argName) { name = argName; }
	    public String toString() { return name; }
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
	}
	
	static class Path
	{
	    public final City target;
	    public final int weight;
	    public final String color;
	    public Path(City argTarget, int argWeight, String argColor) { 
	    	target = argTarget; 
	    	weight = argWeight; 
	    	color = argColor;
	    }
	}
	
	public static void computePaths(City source, HashMap<String,City> cities)
	{
	    source.minDistance = 0;
	    PriorityQueue<City> vertexQueue = new PriorityQueue<City>();
	    vertexQueue.add(source);

	    while (!vertexQueue.isEmpty()) {
//	    	System.out.println(vertexQueue);
	        City u = vertexQueue.poll();
//	         Visit each edge exiting u
//	        System.out.println("City: " + u.name + "; adjancicies: " + u.adjacencies);
	        for (Path e : u.adjacencies)
	        {
	            //City v = e.target; //***
	            City v = cities.get(e.target.name);
	            int weight = e.weight;
//	            System.out.println(v);
	            int distanceThroughU = u.minDistance + weight;
	            if (distanceThroughU < v.minDistance) {
	            	//System.out.println(distanceThroughU + " " + v.minDistance);
	            	vertexQueue.remove(v);
	            	v.minDistance = distanceThroughU ;
	                v.previous = u;
	                //System.out.println(v);
	                vertexQueue.add(v);
//	                System.out.println(vertexQueue);
	            }	      
	        }
//	        System.out.println("1 while cycle");
	    }
	}
	public static List<City> getShortestPathTo(City target)
	{
	    List<City> path = new ArrayList<City>();
	    for (City vertex = target; vertex != null; vertex = vertex.previous)
	        path.add(vertex);

	    Collections.reverse(path);
	    return path;
	}
	
	public static void main(String[] args) {
		Skylar testSkylar = new Skylar();
		testSkylar.initiateGraph();
		for (City c: testSkylar.CitiesMap.values())
			c.exfoliate();
		computePaths(testSkylar.CitiesMap.get("LosAngeles"), testSkylar.CitiesMap);

		for (City v : testSkylar.CitiesMap.values())
		{
		    System.out.println("Distance to " + v + ": " + v.minDistance);
		    List<City> path = getShortestPathTo(v);
		    System.out.println("Path: " + path);
		}
		/*
		System.out.println("------------------");
		computePaths(testSkylar.CitiesMap.get("Chicago"));
		List<City> shortestPath = getShortestPathTo(testSkylar.CitiesMap.get("SanFrancisco"));
		for (City c: shortestPath)
			System.out.print(c.name + " ");*/
	}
}
