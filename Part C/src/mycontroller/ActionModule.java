/**
 * this file is created for project work for SWEN30006 Software Modeling Design
 * Group 77, group members: Jihai Fan, Raymond Guo, Mofan Li
 */
package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ActionModule {
	
	private final int nextPosPathIndex = 1;
	private final float turningThreashold = (float) 1.7;
	
	private MyAIController carController;
	private StraightLineStrategy StraightLineModule;
	private TurningStrategy TurningModule;
	
	public ActionModule(MyAIController carController) {
		this.carController = carController;
		this.StraightLineModule = new StraightLineStrategy1(this.carController);
		this.TurningModule = new TurningStrategy2(this.carController);
	}
	
	public Coordinate getNextTurnPosition(ArrayList<Coordinate> path) {
		/* this function return the position which turn is required in the future 3 step*/
		final int futureVisionRange = 3;
		final int normalRangeTurn = 2;
		final int nearestTurn = 1;
		if (path.size() >= futureVisionRange) {
			return path.get(normalRangeTurn);
		} else {
			return path.get(nearestTurn);
		}
	}
	
	public void drive(float delta, ArrayList<Coordinate> path) {
		System.out.println("Received path: " + path);
		System.out.println("curr Pos: " + this.carController.getPosition());
		System.out.println("currAngle: " + this.carController.getAngle());
		
		//Get current position information
		float accurate_x = this.carController.getX();
		float accurate_y = this.carController.getY();
		WorldSpatial.Direction currentDirection = this.carController.getOrientation();
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
			
			//Get next position's info
			Coordinate nextPos = path.get(nextPosPathIndex); //as 0th element in list is our position	  
			Direction nextDirection = this.getNextDirection(nextPos, accurate_x, accurate_y);
			
			
			if (currentDirection.equals(nextDirection)) {
				/* Case: on a Straight line */
				if (this.needAligh(currentDirection)) {
					this.turn(delta, currentDirection);
					return;
				}
				
				//get future turning position info
				Coordinate futurePos = this.getNextTurnPosition(path);
				Direction futureDirection = this.getFutureDirection(futurePos, accurate_x, accurate_y);

				if (!currentDirection.equals(futureDirection) && this.carController.getSpeed() > turningThreashold) {
					// case: turn in the future
					// we need to slow down
					this.slowDown();
				}else {
					// case: no need to slow down
					this.goStraight(nextPos, accurate_x, accurate_y);
				}
				
			} else { 
				/* Case: turning */
				if (nextDirection == null) {
					// safe mechanism, when decision module decide to go by 45 degree, we move forward first
					this.carController.applyForwardAcceleration();
				}else {
					// we are at a turning point, turn
					this.turn(delta, nextDirection);
				}	
			}		
		}	
	}
	
	/* function that help decide future direction */
	private Direction getFutureDirection(Coordinate futurePos, float accurate_x, float accurate_y) {
		float future_x_dir = futurePos.x - accurate_x;
		float future_y_dir = futurePos.y - accurate_y;
		return this.getDirection(future_x_dir, future_y_dir);
	}
	
	private Direction getNextDirection(Coordinate nextPos, float accurate_x, float accurate_y) {
		float x_dir = nextPos.x-accurate_x;
		float y_dir = nextPos.y-accurate_y;
		return this.getDirection(x_dir, y_dir);
	}
	
	private Direction getDirection(float x_dir, float y_dir) {
		
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
	

	/*function that judging on the current state */
	private boolean reverseLavaEscaptorNeeded(ArrayList<Coordinate> path) {
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
	
	/*different kinds of motion module */
	private boolean needAligh(Direction currentDirection) {
		float currentAngle = this.carController.getAngle();
		int absoluteDegree = 0;
		switch (currentDirection) {
		case EAST:
			absoluteDegree = WorldSpatial.EAST_DEGREE_MIN;
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
		
		if (Math.abs(currentAngle - (float) absoluteDegree) > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void recoverHealth(ArrayList<Coordinate> path) {
		/* case the command is Staying here to heal the health */
		if (path.get(0).toString().equals(DecisionModule.DONOTHING)) {
			this.carController.applyBrake();;
		}
	}
	
	private void escapeLava() {
		this.carController.applyReverseAcceleration();
	}
		
	private void slowDown() {
		if (this.carController.getSpeed() > 2.5) {
			this.carController.applyReverseAcceleration();
		} else {
			this.carController.applyBrake();
		}
	}
	
	private void goStraight(Coordinate nextPos, float accurate_x, float accurate_y) {
		this.StraightLineModule.move(nextPos, accurate_x, accurate_y);	
	}
		
	private void turn(float delta, Direction direction) {
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
