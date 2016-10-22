import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SimulatedAnnealing {
	
    // Calculate the acceptance probability
    private static double acceptanceProbability(int energy, int newEnergy, double temperature) {
        // If the new solution is better, accept it
        if (newEnergy < energy) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((energy - newEnergy) / temperature);
    }
    
    private static Integer readFile(String file){
    	String csvSplitBy = " ";
    	String line = "";
    	BufferedReader br = null;
    	int lineNumber = 1;
    	int dimension = 0;
    	
    	System.out.println("File name: " + file);
    	
    	try{
    		 br = new BufferedReader(new FileReader(file));
    		 while ((line = br.readLine()) != null) {

                //using space as separator
                String[] bufLine = line.split(csvSplitBy);

                //clear ""
                List<String> buf2Line = new ArrayList<String>();

                for(String s : bufLine) {
                    if(s != null && s.length() > 0) {
                       buf2Line.add(s);
                    }
                 }
                
                String[] sepLine = new String[buf2Line.size()];
                sepLine = buf2Line.toArray(sepLine);
                 
                 //ignore lines 1-7
                 if(lineNumber==8){ //line 8 = dimension/number_of_cities
                	 dimension = Integer.parseInt(sepLine[0]);
                	 //System.out.println(dimension);
                 }
                 
                 if(lineNumber>=9 && lineNumber<(9+dimension)){ //line 9 until 9+dimension = matrix
                	 
                	 Hashtable<Integer,Integer> distances = new Hashtable<Integer, Integer>();
                	 ArrayList<Integer> precursors = new ArrayList<Integer>();
                	 
                	 for(int city=0; city<dimension; city++){
                		 int value = Integer.parseInt(sepLine[city]);
                		 if(value==-1)
                			 precursors.add(city+1);
                		 else
                			 distances.put(city+1, value);                			 
                	 }
                	 City city = new City(lineNumber-8,distances,precursors);
                	 TourManager.addCity(city);
                 }
                 
                 lineNumber++;
             }
    	}
    	catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    	
    	return dimension;
    	
    }

    public static void main(String[] args) {
        // Create and add our cities
    	//TODO: relative path
        int dimension = readFile("C:\\Users\\Usuario\\workspace\\SOP\\src\\inputs\\ESC07.sop");
        
        //TODO: while external to Simulated Annealing
        // Set initial temp
        //TODO: Improve temperature and coolingRate choice
        double temp = 10000.0 * Math.pow(111/dimension,2);

        // Cooling rate
        double coolingRate = 0.003 * (1.0/Math.pow(111/dimension,2));
        
        System.out.println("Temperature: " + temp);
        System.out.println("Cooling Rate: " + coolingRate);
        System.out.println("Dimension: " + dimension + " cities");
        
        long startTime = System.nanoTime();

        // Initialize initial solution
        Tour currentSolution = new Tour();
        currentSolution.generateIndividual();
        
        System.out.println("Initial solution distance: " + currentSolution.getDistance());

        // Set as current best
        Tour best = new Tour(currentSolution.getTour());
        
        long elapsedTime = 0;
        int totalFailCounter = 0;
        
        // Loop until system has cooled
        while (temp > 1 && elapsedTime < 10000) {
        	Tour newSolution = new Tour(currentSolution.getTour());
        	int failCounter = 0;
        	
        	//2-OPT method
        	//TODO: Need to improve the 2-OPT (2069046 errors into ESC07 (9 cities)!)
			do{
	            // Create new neighbour tour
	            newSolution = new Tour(currentSolution.getTour());
	
	            // Get a random positions in the tour
	            int tourPos1 = (int) (newSolution.tourSize() * Math.random());
	            int tourPos2 = (int) (newSolution.tourSize() * Math.random());
	
	            // Get the cities at selected positions in the tour
	            City citySwap1 = newSolution.getCity(tourPos1);
	            City citySwap2 = newSolution.getCity(tourPos2);
	
	            // Swap them
	            newSolution.setCity(tourPos2, citySwap1);
	            newSolution.setCity(tourPos1, citySwap2);
	            
	            failCounter++;
            
            //test if valid
        	}while(newSolution.violatePriorities());
            
            //System.out.println("Failed to swap " + (failCounter-1) + " times!");
            
            // Get energy of solutions
            int currentEnergy = currentSolution.getDistance();
            int neighbourEnergy = newSolution.getDistance();

            // Decide if we should accept the neighbour
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                currentSolution = new Tour(newSolution.getTour());
            }

            // Keep track of the best solution found
            if (currentSolution.getDistance() < best.getDistance()) {
                best = new Tour(currentSolution.getTour());
            }
            
            // Cool system
            temp *= 1-coolingRate;
            
            long nowTime = System.nanoTime();
            elapsedTime = (nowTime - startTime)/1000000;//in milisseconds
            
            totalFailCounter += failCounter;
        }
        
        long endTime = System.nanoTime();

        System.out.println("\nFinal solution distance: " + best.getDistance());
        System.out.println("Tour: " + best);
        System.out.println("Total swap errors: " + totalFailCounter + " times.");
        
        long duration = (endTime - startTime)/1000000; //in milisseconds
        System.out.println("Simulated Annealing Time: " + duration/1000.0 + "s");
    }
}