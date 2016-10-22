// Modeling a City

import java.util.ArrayList;
import java.util.Hashtable;

public class City {
	
	//name, distances (name->distance) and list of precursors
	public Integer name;
	public Hashtable<Integer,Integer> distances = new Hashtable<Integer, Integer>();
	public ArrayList<Integer> precursors = new ArrayList<Integer>();
	
	//constructor of a city
	public City(Integer label, Hashtable<Integer,Integer> hash, ArrayList<Integer> percList) {
		this.name = label;
		this.distances = hash;
		this.precursors = percList;
	}
	
	//get a city name
	public Integer getName(){
		return this.name;
	}
	
	//get the list of precursors
	public ArrayList<Integer> getPrecursors(){
		return this.precursors;
	}
	
	//distance to another city
	public Integer distanceToCity(City city){
		Integer dist = -1;
		try
		{
			dist = distances.get(city.getName());
		}
		catch (Exception e){
			System.out.println("cities " + this.toString() + " " + city.toString());
		}
		
		return dist;
	}
	
	@Override
    public String toString(){
        return this.name.toString();
    }

}
