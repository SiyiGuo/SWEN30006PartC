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

public class MyAIController extends CarController{
	// How many minimum units the wall is away from the player.
		private int wallSensitivity = 2;
		
		
		private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
		private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
		private boolean isTurningLeft = false;
		private boolean isTurningRight = false; 
		private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
		
		// Car Speed to move at
		private final float CAR_SPEED = 3;
		
		// Offset used to differentiate between 0 and 360 degrees
		private int EAST_THRESHOLD = 3;
		private PerceptionModule pModule; 
		private DecisionModule dModule; 
		private ActionModule aModule;
		private ArrayList<Coordinate> path; 
		
		public MyAIController(Car car) {
			super(car);
			this.pModule = new PerceptionModule(this, car); 
		    this.dModule = new DecisionModule(this, car); 
		    this.aModule = new ActionModule(this, car);
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
			HashMap<Coordinate, MapTile> currentView = getView();

			pModule.update(); 
		    path = dModule.generatePath();
		    System.out.println(path.toString());
		    System.out.println(path.get(1));
			aModule.drive(delta);

		}
		
		void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
			switch(orientation){
			case EAST:
				if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
					turnLeft(delta);
				}
				break;
			case NORTH:
				if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
					turnLeft(delta);
				}
				break;
			case SOUTH:
				if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
					turnLeft(delta);
				}
				break;
			case WEST:
				if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)){
					turnLeft(delta);
				}
				break;
			default:
				break;
			
			}
			
		}
		
		/**
		 * Turn the car clock wise (think of a compass going clock-wise)
		 */
		void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
			switch(orientation){
			case EAST:
				if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)){
					turnRight(delta);
				}
				break;
			case NORTH:
				if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
					turnRight(delta);
				}
				break;
			case SOUTH:
				if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
					turnRight(delta);
				}
				break;
			case WEST:
				if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
					turnRight(delta);
				}
				break;
			default:
				break;
			
			}
			
		}

		public PerceptionModule getPModule() {
			// TODO Auto-generated method stub
			return this.pModule;
		}
		
		public ArrayList<Coordinate> getPath() {
			return path;	
		}
}
