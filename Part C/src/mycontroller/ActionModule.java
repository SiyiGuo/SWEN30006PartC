package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ActionModule {
	private Car car;
	
	private StraightLineStrategy StraightLineModule;
	private TurningStrategy TurningModule;

	public ActionModule(Car car) {
		this.car = car;
		this.StraightLineModule = new StraightLineStrategy1(this.car);
		this.TurningModule = new TurningStrategy1(this.car);
	}
	
	Direction getDirection(int x_dir, int y_dir) {
		if ((x_dir == 0 & y_dir > 0)) {
			return Direction.NORTH;
		} 
		else if ((x_dir > 0) && (y_dir == 0)){
			return Direction.EAST;
		}
		else if ((x_dir == 0) && (y_dir < 0)) {
			return Direction.SOUTH;
		}
		else if ((x_dir < 0) && (y_dir == 0)) {
			return Direction.WEST;
		}
		else {
			System.out.println(String.format("%s, %s", x_dir, y_dir));
			System.out.println("exception case");
			return null;
		}
		
	}
	
	public void drive(float delta, ArrayList<Coordinate> path) {
		
		Coordinate nextPos = path.get(1);
		Coordinate currentPos = new Coordinate(this.car.getPosition());
		float accurate_x = this.car.getX();
		float accurate_y = this.car.getY();
		WorldSpatial.Direction currentDirection = this.car.getOrientation();
		System.out.println(String.format("next:%s, current:%s, myDirection:%s, myX:%s, myY:%s", nextPos, currentPos, currentDirection, 
							accurate_x, accurate_y));
			    
		int x_dir = nextPos.x-currentPos.x;
		int y_dir = nextPos.y-currentPos.y;
		Direction direction = this.getDirection(x_dir, y_dir);
		System.out.println(String.format("x_dir:%s, y_dir:%s", x_dir, y_dir));
		
		switch (direction) {
		case EAST:
			if(currentDirection.equals(WorldSpatial.Direction.EAST)) {
				this.StraightLineModule.move(nextPos, accurate_x, accurate_y);	
			} else {
				this.TurningModule.turn(this.car.getAngle(), delta, WorldSpatial.EAST_DEGREE_MAX);
			}
			break;
		case NORTH:
			if (currentDirection.equals(WorldSpatial.Direction.NORTH)) {
				this.StraightLineModule.move(nextPos, accurate_x, accurate_y);
			}
			else {
				this.TurningModule.turn(this.car.getAngle(), delta, WorldSpatial.NORTH_DEGREE);
			}
			break;
		case SOUTH:
			if(currentDirection.equals(WorldSpatial.Direction.SOUTH)) {
				this.StraightLineModule.move(nextPos, accurate_x, accurate_y);
			} else {
				this.TurningModule.turn(this.car.getAngle(), delta, WorldSpatial.SOUTH_DEGREE);
			}
			break;
		case WEST:
			if(currentDirection.equals(WorldSpatial.Direction.WEST)) {
				this.StraightLineModule.move(nextPos, accurate_x, accurate_y);
			} else {
				this.TurningModule.turn(this.car.getAngle(), delta, WorldSpatial.WEST_DEGREE);
			}
		default:
			break;
		}
		
	}
	
	
	
	
}
