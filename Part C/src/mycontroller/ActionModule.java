package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ActionModule {
	private MyAIController carController;
	
	private StraightLineStrategy StraightLineModule;
	private TurningStrategy TurningModule;
	
	public ActionModule(MyAIController carController) {
		this.carController = carController;
		this.StraightLineModule = new StraightLineStrategy1(this.carController);
		this.TurningModule = new TurningStrategy2(this.carController);
	}
	
	public void recoverHealth(ArrayList<Coordinate> path) {
		/* case the command is Staying here to heal the health */
		if (path.get(0).toString().equals(DecisionModule.DONOTHING)) {
			this.carController.applyBrake();;
		}
	}
	
	public void escapeLava() {
		this.carController.applyReverseAcceleration();
	}
	
	public void drive(float delta, ArrayList<Coordinate> path) {
		System.out.println("Received path: " + path);
		System.out.println("curr Pos: " + this.carController.getPosition());
		
		Coordinate currentPos = new Coordinate(this.carController.getPosition());
		
		HashMap<Coordinate, MapTile> knownMap = this.carController.getKnownMap();
		
		/* case: we are one a lava */
		if (PerceptionModule.isLava(currentPos, knownMap)) {
			if(this.reverseLavaEscaptorNeeded(path)) {
				/* case: we have escape this lava immediately */
				this.escapeLava();
				return;
			}
			/* case: no need to escape, follow plan*/
		} 
		
		/* case:  arrayList only has one element */
		if (path.size() == 1) {
			this.recoverHealth(path);
		} else {
		/* case: normal route, multiple element in arrayList */
			Coordinate nextPos = path.get(1); //as 0th element in list is our position
			float accurate_x = this.carController.getX();
			float accurate_y = this.carController.getY();
			WorldSpatial.Direction currentDirection = this.carController.getOrientation();
	
				    
			float x_dir = nextPos.x-accurate_x;
			float y_dir = nextPos.y-accurate_y;
			Direction nextDirection = this.getDirection(x_dir, y_dir);
			System.out.println(String.format("next:%s, current:%s, currentDirection:%s, nextDirection:%s, myX:%s, myY:%s", nextPos, currentPos, currentDirection, nextDirection,
					accurate_x, accurate_y));
			
			if (currentDirection.equals(nextDirection)) {
				//Case: on a Straight line
				
				Coordinate futurePos;
				if (path.size() >= 3) {
					futurePos = path.get(2);
				} else {
					futurePos = path.get(1);
				}
				
				
				float future_x_dir = futurePos.x - accurate_x;
				float future_y_dir = futurePos.y - accurate_y;
				Direction futureDirection = this.getDirection(future_x_dir, future_y_dir);

				System.out.println(futureDirection);
				//If there is going to be a turn
				if (!currentDirection.equals(futureDirection) && this.carController.getSpeed() > 1.7) {
					this.slowDown();
					
					
				}else {
					System.out.println("Move forward");
					this.move(nextPos, accurate_x, accurate_y);
				}
				
			} else { 
				if (nextDirection == null) {
					this.carController.applyForwardAcceleration();
				}else {
					this.turn(delta, nextDirection);
				}	
			}		
		}	
	}
	
	Direction getDirection(float x_dir, float y_dir) {
		
		if ((Math.round(x_dir) == 0 & y_dir > 0)) {
			return Direction.NORTH;
		} 
		else if ((x_dir > 0) && (Math.round(y_dir) == 0)){
			return Direction.EAST;
		}
		else if ((Math.round(x_dir) == 0) && (y_dir< 0)) {
			return Direction.SOUTH;
		}
		else if ((x_dir < 0) && (Math.round(y_dir) == 0)) {
			return Direction.WEST;
		}
		else {
			System.out.println(String.format("%s, %s", x_dir, y_dir));
			System.out.println("exception case");
			return null;
		}
		
	}
	
	public void slowDown() {
		if (this.carController.getSpeed() > 2.5) {
			this.carController.applyReverseAcceleration();
		} else {
			this.carController.applyBrake();
		}
	}
	
	public boolean reverseLavaEscaptorNeeded(ArrayList<Coordinate> path) {
		System.out.println("escape");
		Coordinate nextPos;
		try {
			nextPos = path.get(1); //as 0th element in list is our position
		} catch(Exception e) {
			System.out.println("exception");
			return false;
		}
		
		
		float accurate_x = this.carController.getX();
		float accurate_y = this.carController.getY();
		WorldSpatial.Direction currentDirection = this.carController.getOrientation();

			    
		float x_dir = nextPos.x-accurate_x;
		float y_dir = nextPos.y-accurate_y;
		Direction nextDirection = this.getDirection(x_dir, y_dir);
		System.out.println("escape: " + x_dir + " : " + y_dir);
		System.out.println("escape direction: " + nextDirection);
		switch (currentDirection) {
		case EAST:
			if (nextDirection == Direction.WEST) {
				return true;
			}
			return false;
		case WEST:
			if (nextDirection == Direction.EAST) {
				return true;
			}
			return false;
		case SOUTH:
			if (nextDirection == Direction.NORTH) {
				return true;
			}
			return false;
		case NORTH:
			if (nextDirection == Direction.SOUTH) {
				return true;
			}
			return false;
		}
		return false;
		
	}

	public void move(Coordinate nextPos, float accurate_x, float accurate_y) {
		this.StraightLineModule.move(nextPos, accurate_x, accurate_y);	
	}
		
	public void turn(float delta, Direction direction) {
		int absoluteDegree = 0;
		switch (direction) {
		case EAST:
			absoluteDegree = WorldSpatial.EAST_DEGREE_MAX;
			break;
		case NORTH:
			absoluteDegree = WorldSpatial.NORTH_DEGREE;
			break;
		case SOUTH:
			absoluteDegree = WorldSpatial.SOUTH_DEGREE;
			break;
		case WEST:
			absoluteDegree = WorldSpatial.WEST_DEGREE;
		default:
			break;
		}
		this.TurningModule.turn(delta, absoluteDegree);
	}
}
