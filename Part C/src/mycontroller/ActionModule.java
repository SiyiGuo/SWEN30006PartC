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
	public enum WallPosition {FRONT, BACK,  NOWALL};
	
	public ActionModule(Car car) {
		this.car = car;
		this.StraightLineModule = new StraightLineStrategy1(this.car);
		this.TurningModule = new TurningStrategy1(this.car);
	}
	
	Direction getDirection(float x_dir, float y_dir) {
		float offset = (float)0;
		
		float x_offset = Math.abs(x_dir - Math.round(x_dir));
		float y_offset = Math.abs(y_dir - Math.round(y_dir));
		
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
		System.out.println(delta);
		Coordinate nextPos = path.get(1);
		Coordinate currentPos = new Coordinate(this.car.getPosition());
		float accurate_x = this.car.getX();
		float accurate_y = this.car.getY();
		WorldSpatial.Direction currentDirection = this.car.getOrientation();
		System.out.println(String.format("next:%s, current:%s, myDirection:%s, myX:%s, myY:%s", nextPos, currentPos, currentDirection, 
							accurate_x, accurate_y));
			    
		float x_dir = nextPos.x-accurate_x;
		float y_dir = nextPos.y-accurate_y;
		Direction direction = this.getDirection(x_dir, y_dir);
		
		if (currentDirection.equals(direction)) {
			this.StraightLineModule.move(nextPos, accurate_x, accurate_y);	
			this.lastStraightLineDirection = direction;
		} else {
			if (needAdjust(this.lastStraightLineDirection, accurate_x, accurate_y, nextPos, direction)) {
				System.out.println("pass");
			} else {
				this.turn(delta, direction);
				System.out.println(String.format("%s, %s", nextPos.x, accurate_x));
			}
		}		 
	}
	
	
	private boolean needAdjust(Direction lastDirection, float now_x, float now_y, Coordinate nextPos, Direction nextDirection) {
		float offset = (float) 0.1;
		
		WallPosition wallDirection = this.detechWall(lastDirection, nextPos);
		
		switch (wallDirection) {
		case BACK:
			this.car.applyReverseAcceleration();
			return true;
		default:
			return false;
		}
	}
	
	private WallPosition detechWall(Direction lastDirection, Coordinate nextPos) {
		HashMap<Coordinate, MapTile> aroundView = this.car.getView();
		int back_check;
		int front_check;
		System.out.println(lastDirection);
		switch (lastDirection) {
		case EAST:
			//case the car comes from WEST to EAST 
			// ----->
			back_check = nextPos.x - 1;
			front_check = nextPos.x + 1;
			for (Coordinate key:aroundView.keySet()) {
				if ((key.x == back_check) && aroundView.get(key).isType(MapTile.Type.WALL)) {
					return WallPosition.BACK;
				}
				else if ((key.x == front_check) && aroundView.get(key).isType(MapTile.Type.WALL)){
					return WallPosition.FRONT;
				}
			}
			return WallPosition.NOWALL;
		case WEST:
			//case the car comes from EAST to WEST
			// <-------
			back_check = nextPos.x + 1;
			front_check = nextPos.x - 1;
			for (Coordinate key:aroundView.keySet()) {
				if ((key.x == back_check) && aroundView.get(key).isType(MapTile.Type.WALL)) {
					return WallPosition.BACK;
				}
				else if ((key.x == front_check) && aroundView.get(key).isType(MapTile.Type.WALL)){
					return WallPosition.FRONT;
				}
			}
			return WallPosition.NOWALL;
		default:
			return WallPosition.NOWALL;
		}
	}
	
	private void turn(float delta, Direction direction) {
		switch (direction) {
		case EAST:
			this.TurningModule.turn(delta, WorldSpatial.EAST_DEGREE_MAX);
			break;
		case NORTH:
			this.TurningModule.turn(delta, WorldSpatial.NORTH_DEGREE);
			break;
		case SOUTH:
			this.TurningModule.turn(delta, WorldSpatial.SOUTH_DEGREE);
			break;
		case WEST:
			this.TurningModule.turn(delta, WorldSpatial.WEST_DEGREE);
		default:
			break;
		}
	}
	
	
	
	
}
