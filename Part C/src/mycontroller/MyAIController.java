package mycontroller;

import java.util.HashMap; 
import java.util.ArrayList; 
import controller.CarController; 
import tiles.LavaTrap; 
import tiles.MapTile; 
import tiles.TrapTile; 
import utilities.Coordinate; 
import world.Car; 
import world.WorldSpatial; 
import world.WorldSpatial.Direction;

public class MyAIController extends CarController{
		// Offset used to differentiate between 0 and 360 degrees
		private PerceptionModule pModule; 
		private DecisionModule dModule; 
		private ActionModule aModule;
		private ArrayList<Coordinate> path; 
		
		public MyAIController(Car car) {
			super(car);
			this.pModule = new PerceptionModule(this, car); 
		    this.dModule = new DecisionModule(this, car); 
		    this.aModule = new ActionModule(car);
		} 
		 
		public ArrayList<Coordinate> generatePath(HashMap<Integer, Coordinate> worldMap, ArrayList<Coordinate> unsearched, Coordinate source, Coordinate destination){ 
		    return new ArrayList<Coordinate>(); 
		} 
		
		
		Coordinate initialGuess;
		boolean notSouth = true;
		int i = 0;
		@Override
		public void update(float delta) {
			
			// Gets what the car can see
			pModule.update(); 

			// Deciding the path
		    path = dModule.generatePath();
		    
		    // Move
			aModule.drive(delta, path);
//		    aModule.turn(delta, Direction.NORTH);

		}
		
		public PerceptionModule getPModule() {
			return this.pModule;
		}
		
		public ArrayList<Coordinate> getPath() {
			return path;	
		}
}
