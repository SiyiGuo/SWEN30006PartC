package mycontroller;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial.Direction;

public class TurningStrategy2 implements TurningStrategy{
	private MyAIController carController;
	
	public enum WallPosition {FRONT, FOLLOWING, NOWALL};
	public TurningStrategy2(MyAIController car) {
		this.carController = car;
	}
	
	private void slowDown() {
		if (this.carController.getSpeed() > 2.5){
			this.carController.applyReverseAcceleration();
		} else {
			this.carController.applyBrake();
		}
		
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		final float maxTurningSpeed = (float) 0.2;
		
		/* case maximum turning speed */
		if (this.carController.getSpeed() > maxTurningSpeed) {
			this.slowDown();
		}else {
			if (adjust()) {
				System.out.println("adjust");
				return;
			} else {
				this.turnToDirection(this.carController.getAngle(), delta, absoluteDegree);
			}
		}
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
		final float maxSmallTurnSpeed = (float) 0.1;
		if ((currentDegree != absoluteDegree)) {
			if (this.carController.getSpeed() > maxSmallTurnSpeed) {
				this.carController.applyBrake();
			} else {
				this.carController.applyReverseAcceleration();
			}			
			
			if ((currentDegree - absoluteDegree) >= 0 ) {
				if (Math.abs(currentDegree - absoluteDegree) <= Math.abs(360 - currentDegree + absoluteDegree)) {
					this.carController.turnLeft(delta);
				} else {
					this.carController.turnRight(delta);
				}
			} else {
				if (Math.abs(absoluteDegree-currentDegree) < Math.abs(360 + currentDegree - absoluteDegree)) {
					this.carController.turnRight(delta);
				} else {
					this.carController.turnLeft(delta);
				}
			}
		}  else {
			//case we r at right direction
			this.carController.applyBrake();
		}
	}
	
	private void minorBack() {
		final float maxBackAdjustmentSpeed = (float) 0.1;
		if (this.carController.getSpeed() > maxBackAdjustmentSpeed) {
			this.carController.applyBrake();
		} else {
			this.carController.applyReverseAcceleration();
		}
	}
	
	private void minorForward() {
		final float maxForwardAdjustmentSpeed = (float) 0.1;
		
		if (this.carController.getSpeed() > maxForwardAdjustmentSpeed) {
			this.carController.applyBrake();
		} else {
			this.carController.applyForwardAcceleration();
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
		Direction currentDirection = this.carController.getOrientation();
		Coordinate currentPos = new Coordinate(this.carController.getPosition());
		
		float x = this.carController.getX();
		float y = this.carController.getY();
		
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
		Direction currentDirection = this.carController.getOrientation();
		Coordinate currentPos = new Coordinate(this.carController.getPosition());
		float x = this.carController.getX();
		float y = this.carController.getY();
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
		Direction currentDirection = this.carController.getOrientation();
		HashMap<Coordinate, MapTile> currentView = this.carController.getView();
		switch (currentDirection) {
		case EAST:		
			if (checkWest(currentView)) {
				return WallPosition.FOLLOWING;
			} else if (checkEast(currentView)){
				return WallPosition.FRONT;
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
			if (checkNorth(currentView)) {
				return WallPosition.FOLLOWING;
			} else if (checkSouth(currentView)){
				return WallPosition.FRONT;
			} else {
				return WallPosition.NOWALL;
			}
			
		case NORTH:
			
			if (checkSouth(currentView)) {
				return WallPosition.FOLLOWING;
			} else if (checkNorth(currentView)){
				return WallPosition.FRONT;
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
	 * checkEast will check up all tiles that are wall to the right in currentView.
	 * checkWest will check up all tiles that are wall to the left  in currentView.
	 * checkNorth will check up all tiles that are wall to the top  in currentView.
	 * checkSouth will check up all tiles that are wall below  in currentView.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(this.carController.getPosition());
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
		Coordinate currentPosition = new Coordinate(this.carController.getPosition());
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
		Coordinate currentPosition = new Coordinate(this.carController.getPosition());
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
		Coordinate currentPosition = new Coordinate(this.carController.getPosition());
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
