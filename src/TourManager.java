import java.util.ArrayList;

public class TourManager {
	
	// Array of Cities
    private static ArrayList<City> cities = new ArrayList<City>();

    // Add a city
    public static void addCity(City city) {
        cities.add(city);
    }
    
    // Get a city in an index
    public static City getCity(int index){
        return (City)cities.get(index);
    }
    
    // Get the number of cities
    public static int numberOfCities(){
        return cities.size();
    }
}
