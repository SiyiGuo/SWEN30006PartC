package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ActionModule {
	private Car car;
	
	private StraightLineStrategy StraightLineModule;
	private TurningStrategy TurningModule;
	private Direction lastStraightLineDirection;
	public enum TurnDirection {LEFT, RIGHT, INVERSE};
	private Coordinate lastTurnTo;
	private double directionInDegree;
	
	public ActionModule(Car car) {
		this.car = car;
		this.StraightLineModule = new StraightLineStrategy1(this.car);
		this.TurningModule = new TurningStrategy1(this.car);
	}
	public float getDirectionInDegree(float x_dir, float y_dir) {
		
		if (x_dir == 0) {
			return y_dir>0 ? 90 : -90;
		}
		float degree = (float)(Math.atan(y_dir/x_dir) / Math.PI * 180);
		if (x_dir > 0) {
			if (y_dir >= 0) {
				return degree;
			}else {
				return degree + 360;
			}
		}else {
			return degree + 180;
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
	
	public void drive(float delta, ArrayList<Coordinate> path) {
//		System.out.println(path);
		if (path.size() == 1) {
			if (path.get(0).toString().equals("99,99")) {
				System.out.println("Do Nothing");
				Coordinate currentPos = new Coordinate(this.car.getPosition());
				System.out.println(currentPos);
				this.car.brake();
			}
			
		} else {
			Coordinate nextPos = path.get(1); //as 0th element in list is our position
			Coordinate currentPos = new Coordinate(this.car.getPosition());
			float accurate_x = this.car.getX();
			float accurate_y = this.car.getY();
			float currentDirection = this.car.getRotation();
			//System.out.println(String.format("next:%s, current:%s, myDirection:%s, myX:%s, myY:%s", nextPos, currentPos, currentDirection, 
			//					accurate_x, accurate_y));
				    
			float x_dir = nextPos.x-accurate_x;
			float y_dir = nextPos.y-accurate_y;
			float direction = this.getDirectionInDegree(x_dir, y_dir);
			
			if (Math.abs(currentDirection - direction) < 1) {
				this.StraightLineModule.move(nextPos, accurate_x, accurate_y);
			} else if (lastTurnTo == null || !lastTurnTo.equals(nextPos)){
				System.out.println("turn to "+nextPos.toString()+" current angle is "+car.getAngle());
				this.TurningModule.turn(delta, (int)getDirectionInDegree(x_dir, y_dir));
				this.lastTurnTo = nextPos;
			}	
		}	
	}
	
	private boolean needAdjust(Direction lastDirection, float now_x, float now_y, Coordinate nextPos) {
	    switch (lastDirection ) {
	 
	    case WEST:
	      if (nextPos.x < now_x) {
	        System.out.println(nextPos.x);
	        System.out.println(now_x);
	        return true;
	      }
	      else {
	        return false;
	      }
	    case EAST:
	      if (nextPos.x > now_x) {
	        System.out.println(nextPos.x);
	        System.out.println(now_x);
	        return true;
	      }
	      else {
	        return false;
	      }
	    case NORTH:
	      if (nextPos.y > now_y) { 
	        System.out.println(nextPos.y);
	        System.out.println(now_y);
	        return true;
	      }
	      else {
	        return false;
	      }
	    case SOUTH:
	        if (nextPos.y < now_y) {
	          System.out.println(nextPos.y);
	          System.out.println(now_y);
	          return true;
	        }
	        else {
	          return false;
	        }
	    default:
	      return false;
	    }
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
