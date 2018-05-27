package mycontroller;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial.Direction;

public class TurningStrategy2 implements TurningStrategy{
	private MyAIController car;
	private int wallSensitivity = 4;
	public enum WallPosition {FRONT, FOLLOWING, NOWALL};
	public TurningStrategy2(MyAIController car) {
		this.car = car;
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		if (this.car.getSpeed() > 0.2) {			
			this.car.applyBrake();
		}else {
			if (adjust()) {
				System.out.println("adjust");
				return;
			} else {
				this.turnToDirection(this.car.getAngle(), delta, absoluteDegree);
			}
		}
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
		if ((currentDegree != absoluteDegree)) {
			
			if (this.car.getSpeed() > 0.1) {
				this.car.applyBrake();
			} else {
				this.car.applyReverseAcceleration();
			}			
			if ((currentDegree - absoluteDegree) >= 0 ) {
				if (Math.abs(currentDegree - absoluteDegree) <= Math.abs(360 - currentDegree + absoluteDegree)) {
					this.car.turnLeft(delta);
				} else {
					this.car.turnRight(delta);
				}
			} else {
				if (Math.abs(absoluteDegree-currentDegree) < Math.abs(360 + currentDegree - absoluteDegree)) {
					this.car.turnRight(delta);
				} else {
					this.car.turnLeft(delta);
				}
			}
		}  else {
			this.car.applyBrake();
		}
	}
	
	public void minorBack() {
		if (this.car.getSpeed() > 0.1) {
			this.car.applyBrake();
		} else {
			this.car.applyReverseAcceleration();
		}
	}
	
	public void minorForward() {
		if (this.car.getSpeed() > 0.1) {
			this.car.applyBrake();
		} else {
			this.car.applyForwardAcceleration();
		}
	}
	
	private boolean adjust() {
		WallPosition wallPosition = checkWall();
		System.out.println("WallPosition: " + wallPosition);
		switch (wallPosition) {
		case FRONT:
			return frontWallAdjust();
		case FOLLOWING:
			return backWallAdjust();
		default:
			return false;
		}
	}
	
	private boolean frontWallAdjust() {
		Direction currentDirection = this.car.getOrientation();
		Coordinate currentPos = new Coordinate(this.car.getPosition());
		float x = this.car.getX();
		float y = this.car.getY();
		switch (currentDirection){
		case WEST:
			if (currentPos.x > x) {
				this.minorBack();
				return true;
			}
			return false;
		case EAST:
			if (currentPos.x < x) {
				this.minorBack();
				return true;
			}
			return false;
		case SOUTH:
			if (currentPos.y > y) {
				this.minorBack();
				return true;
			}
			return false;
		case NORTH:
			if (currentPos.y < y){
				this.minorBack();
				return true;
			}
			return false;
		default:
			return false;
		}
	}
	
	private boolean backWallAdjust() {
		Direction currentDirection = this.car.getOrientation();
		Coordinate currentPos = new Coordinate(this.car.getPosition());
		float x = this.car.getX();
		float y = this.car.getY();
		switch (currentDirection){
		case WEST:
			if (currentPos.x < x) {
				this.minorForward();
				return true;
			}
			return false;
		case EAST:
			if (currentPos.x > x) {
				this.minorForward();
				return true;
			}
			return false;
		case SOUTH:
			if (currentPos.y < y) {
				this.minorForward();
				return true;
			}
			return false;
		case NORTH:
			if (currentPos.y > y){
				this.minorForward();
				return true;
			}
			return false;
		default:
			return false;
		}
	}
	
	private WallPosition checkWall() {
		Direction currentDirection = this.car.getOrientation();
		HashMap<Coordinate, MapTile> currentView = this.car.getView();
		switch (currentDirection) {
		case EAST:
			if (checkEast(currentView)) {
				return WallPosition.FRONT;
			} else if (checkWest(currentView)){
				return WallPosition.FOLLOWING;
			} else {
				return WallPosition.NOWALL;
			}
		case WEST:
			if (checkEast(currentView)) {
				return WallPosition.FOLLOWING;
			} else if (checkWest(currentView)){
				return WallPosition.FRONT;
			} else {
				return WallPosition.NOWALL;
			}
		case SOUTH:
			if (checkSouth(currentView)) {
				return WallPosition.FRONT;
			} else if (checkNorth(currentView)){
				return WallPosition.FOLLOWING;
			} else {
				return WallPosition.NOWALL;
			}
		case NORTH:
			if (checkNorth(currentView)) {
				return WallPosition.FRONT;
			} else if (checkSouth(currentView)){
				return WallPosition.FOLLOWING;
			} else {
				return WallPosition.NOWALL;
			}
		default:
			return WallPosition.NOWALL;
		}
	}
	
	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for (Coordinate coor: currentView.keySet()) {
			if (coor.x == currentPosition.x+1) {
				MapTile tile = currentView.get(coor);
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for (Coordinate coor: currentView.keySet()) {
			if (coor.x == currentPosition.x-1) {
				MapTile tile = currentView.get(coor);
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for (Coordinate coor: currentView.keySet()) {
			if (coor.y == currentPosition.y+1) {
				MapTile tile = currentView.get(coor);
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for (Coordinate coor: currentView.keySet()) {
			if (coor.y == currentPosition.y-1) {
				MapTile tile = currentView.get(coor);
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
		}
		return false;
	}
}
