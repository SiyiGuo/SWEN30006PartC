package mycontroller;

import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import mycontroller.DecisionModule.Mode;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class ActionModule {
	private MyAIController car;
	
	private StraightLineStrategy StraightLineModule;
	private TurningStrategy TurningModule;
	private Direction lastStraightLineDirection;
	private boolean forwardLava = true;
	public enum TurnDirection {LEFT, RIGHT, INVERSE};
	public boolean needAdjustment;
	
	public ActionModule(MyAIController car) {
		this.car = car;
		this.StraightLineModule = new StraightLineStrategy1(this.car);
		this.TurningModule = new TurningStrategy2(this.car);
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
		if (this.car.getSpeed() > 2.5) {
			this.car.applyReverseAcceleration();
		} else {
			this.car.applyBrake();
		}
	}
	
	public void lavaEscaptor(ArrayList<Coordinate> path) {
		Coordinate nextPos;
		try {
			nextPos = path.get(1); //as 0th element in list is our position
		} catch(Exception e) {
			return;
		}
		
		
		float accurate_x = this.car.getX();
		float accurate_y = this.car.getY();
		WorldSpatial.Direction currentDirection = this.car.getOrientation();

			    
		float x_dir = nextPos.x-accurate_x;
		float y_dir = nextPos.y-accurate_y;
		Direction nextDirection = this.getDirection(x_dir, y_dir);
		
		
		switch (currentDirection) {
		case EAST:
			if (nextDirection == Direction.WEST) {
				this.car.applyReverseAcceleration();
			}
			this.car.applyForwardAcceleration();
			return;
		case WEST:
			if (nextDirection == Direction.EAST) {
				this.car.applyReverseAcceleration();
			}
			this.car.applyForwardAcceleration();
			return;
		case SOUTH:
			if (nextDirection == Direction.NORTH) {
				this.car.applyReverseAcceleration();
			}
			this.car.applyForwardAcceleration();
			return;
		case NORTH:
			if (nextDirection == Direction.SOUTH) {
				this.car.applyReverseAcceleration();
			}
			this.car.applyForwardAcceleration();
			return;
		}
		return;
		
	}

	public void drive(float delta, ArrayList<Coordinate> path) {
		System.out.println(path);
		switch (this.car.getMode()) {
		case SEARCHING:
			this.StraightLineModule.setMaxSpeed((float)5);
			break;
		case DESTINATION:
			this.StraightLineModule.setMaxSpeed((float)5);
			break;
		}
		
		HashMap<Coordinate, MapTile> knownMap = this.car.getKnownMap();
		if (knownMap.get(new Coordinate(this.car.getPosition())).isType(MapTile.Type.TRAP) && ((TrapTile)knownMap.get(new Coordinate(this.car.getPosition()))).getTrap().equals("lava")) {
			this.lavaEscaptor(path);
			return;
		} 
		
		if (path.size() == 1) {
			if (path.get(0).toString().equals("99,99")) {
				System.out.println("Do MNothjing");
				Coordinate currentPos = new Coordinate(this.car.getPosition());
				System.out.println(currentPos);
				this.car.applyBrake();;
			}
			
		} else {
			Coordinate nextPos = path.get(1); //as 0th element in list is our position
			Coordinate currentPos = new Coordinate(this.car.getPosition());
			float accurate_x = this.car.getX();
			float accurate_y = this.car.getY();
			WorldSpatial.Direction currentDirection = this.car.getOrientation();
	
				    
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
				if (!currentDirection.equals(futureDirection) && this.car.getSpeed() > 1.7) {
					this.slowDown();
					
					
				}else {
					System.out.println("Move forward");
					this.move(nextPos, accurate_x, accurate_y);
					this.lastStraightLineDirection = nextDirection;
					this.needAdjustment = true;
				}
				
			} else { 
			    this.turn(delta, nextDirection);		
			}		
		}	
	}
	
	private boolean detectFrontWall() {
		Coordinate currentPos = new Coordinate(this.car.getPosition());
		HashMap<Coordinate, MapTile> aroundView = this.car.getView();
		Coordinate nextPos;
		switch (this.car.getOrientation()) {
		case NORTH:
			nextPos = new Coordinate((currentPos.x)+","+(currentPos.y+1));
			if (aroundView.get(nextPos).isType(MapTile.Type.WALL)) {
				return true;
			}
			return false;
		case SOUTH:
			nextPos = new Coordinate((currentPos.x)+","+(currentPos.y-1));
			if (aroundView.get(nextPos).isType(MapTile.Type.WALL)) {
				return true;
			}
			return false;
		case WEST:
			nextPos = new Coordinate((currentPos.x-1)+","+(currentPos.y));
			if (aroundView.get(nextPos).isType(MapTile.Type.WALL)) {
				return true;
			}
			return false;
		case EAST:
			nextPos = new Coordinate((currentPos.x+1)+","+(currentPos.y));
			if (aroundView.get(nextPos).isType(MapTile.Type.WALL)) {
				return true;
			}
			return false;
		default:
			return false;
		}
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
