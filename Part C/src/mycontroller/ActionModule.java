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
	private float maxSpeed = (float) 1.6;
	private float wallSensitivity = (float) 0.6;
	private int bug = 0;
	int x = 0;
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
		Coordinate next = path.get(1);
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		WorldSpatial.Direction myDirection = controller.getOrientation();
		int x = Math.round(next.x-this.car.getX() - (float)0.3);
		int y = Math.round(next.y-this.car.getY() - (float)0.3);
		System.out.println(x);
		System.out.println(y);
		if ((x == 0) && (y == 1)) {
			if (controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				this.x = 0;
				if(!checkRightWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyLeftTurn(this.controller.getOrientation(),delta);
				}
				if(!checkLeftWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyRightTurn(this.controller.getOrientation(),delta);
				}
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
				
			}
			else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.NORTH_DEGREE);
			}
		}
		else if ((x == 0) && (y == -1)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				this.x = 0;
				if(!checkRightWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyLeftTurn(this.controller.getOrientation(),delta);
				}
				if(!checkLeftWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyRightTurn(this.controller.getOrientation(),delta);
				}
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.SOUTH_DEGREE);
			}
		}
		else if ((x == 1) && (y == 0)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				this.x = 0;
				if(!checkRightWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyLeftTurn(this.controller.getOrientation(),delta);
				}
				if(!checkLeftWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyRightTurn(this.controller.getOrientation(),delta);
				}
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
				controller.applyReverseAcceleration();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.EAST_DEGREE_MAX);
			}
		}		
		else if ((x == -1) && (y == 0)) {
			this.x = 0;
			if(controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
				if(!checkRightWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyLeftTurn(this.controller.getOrientation(),delta);
				}
				if(!checkLeftWall(this.controller.getOrientation(),this.controller.getView())){
					this.controller.applyRightTurn(this.controller.getOrientation(),delta);
				}
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getAngle(), delta, WorldSpatial.WEST_DEGREE);
				
			}
		}
		
	}
	
	
	
	private void turnToDirection(float f, float delta, int northDegree) {
		System.out.println(northDegree);
		if ((f != northDegree)) {
			
			if ((f - northDegree) >= 0 ) {
				System.out.println(f-northDegree);
				if (Math.abs(f - northDegree) <= Math.abs(360 - f + northDegree)) {
					
					this.controller.turnLeft(2*delta);
				} else {
					this.controller.turnRight(2*delta);
				}
			} else {
				if (Math.abs(northDegree-f) < Math.abs(360 + f - northDegree)) {
					this.controller.turnRight(2*delta);
				} else {
					this.controller.turnLeft(2*delta);
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
