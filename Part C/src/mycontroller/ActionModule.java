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
		System.out.println(path);
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
		} else {
			this.turn(delta, direction);
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
