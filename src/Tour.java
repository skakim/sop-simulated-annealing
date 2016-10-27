import java.util.ArrayList;
import java.util.Collections;

public class Tour{

    // Holds our tour of cities
    private ArrayList<City> tour = new ArrayList<City>();
    
    // Cache
    private int distance = 0;
    
    // Constructs a blank tour
    public Tour(){
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }
    }
    
    // Constructs a tour from another tour
	public Tour(ArrayList<City> tour){
        this.tour = (ArrayList<City>) tour.clone();
    }
    
    // Returns tour information
    public ArrayList<City> getTour(){
        return tour;
    }
    
    public Boolean violatePriorities(){
    	
    	ArrayList<Integer> alreadyPassed = new ArrayList<Integer>();
    	
    	// Loop through the tour
    	for (int cityIndex = 0; cityIndex < TourManager.numberOfCities(); cityIndex++){
    		City city = getCity(cityIndex);
    		
    		//Test if all city precursors have not already passed
    		if (!alreadyPassed.containsAll(city.getPrecursors())){
    			//System.out.println(tour.toString() + " violates! ");
    			return true;
    		}
    		
    		//add the city to alreadyPassed
    		alreadyPassed.add(city.getName());
    	}
    	
    	//true if the priorities are not violated
    	return false;
    }
    
    public int countViolations(){
    	ArrayList<Integer> alreadyPassed = new ArrayList<Integer>();
    	int violations = 0;
    	
    	// Loop through the tour
    	for (int cityIndex = 0; cityIndex < TourManager.numberOfCities(); cityIndex++){
    		City city = getCity(cityIndex);
    		
    		//Test if all city precursors have not already passed
    		if (!alreadyPassed.containsAll(city.getPrecursors())){
    			//System.out.println(tour.toString() + " violates! ");
    			violations++;
    		}
    		
    		//add the city to alreadyPassed
    		alreadyPassed.add(city.getName());
    	}
    	
    	//true if the priorities are not violated
    	return violations;
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
    	for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) {
          setCity(cityIndex, TourManager.getCity(cityIndex));
        }
    	//doesn't matter if violates or not: will be punished after if it is the case        
        Collections.shuffle(tour);
    }

    // Gets a city from the tour
    public City getCity(int tourPosition) {
        return (City)tour.get(tourPosition);
    }

    // Sets a city in a certain position within a tour
    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);
        // If the tours been altered we need to reset the fitness and distance
        distance = 0;
    }
    
    // Gets the total distance of the tour
    public int getDistance(){
    	if (distance == 0) {
    		int tourDistance = 0;
    		// Loop through the tour
    		for (int cityIndex=0; cityIndex < tourSize()-1; cityIndex++) {
    			// Get city we're traveling out
    			City fromCity = getCity(cityIndex);
    			// City we're traveling into
    			City destinationCity;
    			destinationCity = getCity(cityIndex+1);
    			// Get the distance between the two cities
    			// if distance = "-1", uses it anyway, because it will be punished after
    			tourDistance += fromCity.distanceToCity(destinationCity);
    			
    		}
    		int violations = this.countViolations();
    		distance = tourDistance + (1000000 * violations); //punishing if violates
    	}
        return distance;
    }

    // Get number of cities on our tour
    public int tourSize() {
        return tour.size();
    }
    
    @Override
    public String toString() {
        String tourString = "";
        int i = 0;
        for (i = 0; i < tourSize()-1; i++) {
            tourString += getCity(i)+"->";
        }
        tourString += getCity(i);
        return tourString;
    }
}