package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class ActionModule {
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> path; 
	private float maxSpeed = (float)5;
	private float wallSensitivity = (float) 0.6;

	public ActionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
	}
	
	public void drive(float delta) {
		System.out.println(this.car.getX());
		System.out.println(this.car.getY());
//        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.SOUTH)); //270
//        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.NORTH)); // 90
//        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.EAST)); // 0
//        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.WEST)); // 180
		path = this.controller.getPath();
		Coordinate next = path.get(0);
		if (path.size() > 1) {
			next = path.get(1);
		}
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		WorldSpatial.Direction myDirection = controller.getOrientation();
		
		float offset = (float) (0.2);
		
		int x = 0;
		if(controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
			x += Math.round(next.x-this.car.getX() - offset);
		} else if (controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
			x += Math.round(next.x-this.car.getX() + offset);
		} else {
			x += Math.round(next.x-this.car.getX());
		}
		
		int y = 0;
		if(controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
			y += Math.round(next.y-this.car.getY() + offset);
		} else if (controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
			y += Math.round(next.y-this.car.getY() - offset);
		} else {
			y += Math.round(next.y-this.car.getY());
		}
		System.out.println(x);
		System.out.println(y);
		if ((x == 0) && (y > 0)) {
			if (controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {

				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
				
			}
			else {
				controller.applyReverseAcceleration();
//				controller.applyBrake();
//				controller.applyForwardAcceleration();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.NORTH_DEGREE);
			}
		}
		else if ((x == 0) && (y < 0)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {

				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
//				controller.applyBrake();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.SOUTH_DEGREE);
			}
		}
		else if ((x > 0) && (y == 0)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {

				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
				controller.applyReverseAcceleration();
//				controller.applyBrake();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.EAST_DEGREE_MAX);
			}
		}		
		else if ((x < 0) && (y == 0)) {

			if(controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {

				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
//				controller.applyBrake();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.WEST_DEGREE);
				
			}
		}
		
	}
	
	
	
	private void turnToDirection(float f, float delta, int northDegree) {
		System.out.println(northDegree);
		System.out.println(f);
		float turnRate = 2f;
		if ((Math.round(f) != northDegree)) {
			
			if ((f - northDegree) >= 0 ) {
				System.out.println(f-northDegree);
				if (Math.abs(f - northDegree) <= Math.abs(360 - f + northDegree)) {
					
					this.controller.turnLeft(turnRate*delta);
				} else {
					this.controller.turnRight(turnRate*delta);
				}
			} else {
				if (Math.abs(northDegree-f) < Math.abs(360 + f - northDegree)) {
					this.controller.turnRight(turnRate*delta);
				} else {
					this.controller.turnLeft(turnRate*delta);
				}
			}
		} 
	}
	
	
	private boolean checkLeftWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
		default:
			return false;
		}
		
	}
	
	private boolean checkRightWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case WEST:
			return checkNorth(currentView);
		case SOUTH:
			return checkWest(currentView);
		case NORTH:
			return checkEast(currentView);
		case EAST:
			return checkSouth(currentView);
		default:
			return false;
		}
		
	}
	
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(this.car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	
}
