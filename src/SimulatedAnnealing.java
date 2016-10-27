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
        double temp = 10000.0*dimension;

        // Cooling rate
        double coolingRate = 0.003*(1.0/dimension);
        
        System.out.println("Temperature: " + temp);
        System.out.println("Cooling Rate: " + coolingRate);
        System.out.println("Dimension: " + dimension + " cities");
        
        long startTime = System.nanoTime();

        // Initialize initial solution
        Tour currentSolution = new Tour();
        currentSolution.generateIndividual();
        
        System.out.println("Initial solution distance: " + currentSolution.getDistance());

        // Set as current best
        Tour currentBest = new Tour(currentSolution.getTour());
        Tour finalBest = new Tour(currentSolution.getTour());
        
        long thisStartTime = startTime;
        
        long nodesVisited = 0;
        
        int multiStart = 0;
        
        
        for(multiStart=0; multiStart<dimension; multiStart++)
        { //multistart loop
        	long elapsedTime = 0;
        	thisStartTime = System.nanoTime();
	        // Loop until system has cooled
	        while (temp > 1) {
	        	Tour newSolution = new Tour(currentSolution.getTour());
	        	long failCounter = 0;
	        	
	        	//2-OPT method
	        	//CHOICE: if it is invalid, use this anyway
				
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
	        		            
	            // Get energy of solutions
	            int currentEnergy = currentSolution.getDistance();
	            int neighbourEnergy = newSolution.getDistance();
	
	            // Decide if we should accept the neighbour
	            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
	                currentSolution = new Tour(newSolution.getTour());
	            }
	
	            // Keep track of the best solution found
	            if (currentSolution.getDistance() < currentBest.getDistance()) {
	                currentBest = new Tour(currentSolution.getTour());
	            }
	            
	            // Cool system
	            temp *= 1-coolingRate;
	            
	            long nowTime = System.nanoTime();
	            elapsedTime = (nowTime - thisStartTime)/1000000;//in milisseconds
	            
	            nodesVisited++;
	        }
	        
	        System.out.println("Best at start number " + (multiStart+1) + ": " + currentBest.getDistance() + " (" + elapsedTime + "ms)");
	        
	        if(currentBest.getDistance() < finalBest.getDistance()){
	        	finalBest = new Tour(currentBest.getTour());
	        }
	        
	        //new beggining
	        currentSolution = new Tour(); //new "currentSolution" to the new start
	        currentSolution.generateIndividual();
	        
	        currentBest = new Tour(currentSolution.getTour());
	        
	        //restart temperature
	        temp = 10000.0*dimension;
        	coolingRate = 0.003*(1.0/dimension);
        }
        
        long endTime = System.nanoTime();

        System.out.println("\nFinal solution distance: " + finalBest.getDistance());
        System.out.println("Tour: " + finalBest);
        System.out.println("Total nodes visited: " + nodesVisited + " nodes.");
        System.out.println("Total multi-starts: " + dimension + " starts.");
        
        long duration = (endTime - startTime)/1000000; //in milisseconds
        System.out.println("Simulated Annealing Time: " + duration/1000.0 + "s");
    }
}