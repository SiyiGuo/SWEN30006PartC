package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class ActionModule {
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> path; 
	private float maxSpeed = (float) 1.4;
	
	private int bug = 0;
	
	public ActionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
	}
	
	public void drive(float delta) {
        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.SOUTH));
        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.NORTH));
        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.EAST));
        System.out.println(controller.getOrientation().compareTo(WorldSpatial.Direction.WEST));
		path = this.controller.getPath();
		Coordinate next = path.get(1);
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		WorldSpatial.Direction myDirection = controller.getOrientation();
		int x = next.x-currentPosition.x;
		int y = next.y-currentPosition.y;
		if ((x == 0) && (y == 1)) {
			if (controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
				
			}
			else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getOrientation(), delta, WorldSpatial.Direction.NORTH);
			}
		}
		else if ((x == 0) && (y == -1)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getOrientation(), delta, WorldSpatial.Direction.SOUTH);
			}
		}
		else if ((x == 1) && (y == 0)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getOrientation(), delta, WorldSpatial.Direction.EAST);
			}
		}		
		else if ((x == -1) && (y == 0)) {
			if(controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
				
				if (controller.getSpeed() < maxSpeed) {
					controller.applyForwardAcceleration();
				}
			} else {
				controller.applyReverseAcceleration();
				turnToDirection(controller.getOrientation(), delta, WorldSpatial.Direction.WEST);
				
			}
		}
	}
	
	
	
	private void turnToDirection(WorldSpatial.Direction orientation, float delta, WorldSpatial.Direction target) {
		
		switch(orientation){
		case SOUTH:
//			controller.turnRight(delta);
			System.out.println("going south");
			switch(target) {
			case NORTH:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			case EAST:
				if(!controller.getOrientation().equals(target)){
					controller.turnLeft(delta);
				}
				break;
			case WEST:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			}
			break;
		case EAST:
			System.out.println("i m east");
			switch(target) {
			case NORTH:
				if(!controller.getOrientation().equals(target)){
					controller.turnLeft(delta);
				}
				break;
			case SOUTH:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			case WEST:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			}
			break;
		case WEST:
			System.out.println("i m west");
			switch(target) {
			case NORTH:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			case EAST:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			case SOUTH:
				if(!controller.getOrientation().equals(target)){
					controller.turnLeft(delta);
				}
				break;
			}
			break;
		case NORTH:
			System.out.println("i m north");
			switch(target) {
			case SOUTH:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			case EAST:
				if(!controller.getOrientation().equals(target)){
					controller.turnRight(delta);
				}
				break;
			case WEST:
				if(!controller.getOrientation().equals(target)){
					controller.turnLeft(delta);
				}
				break;
			}
			break;
		default:
			break;	
		}	
	}
	
	private void turnLeftToDirection(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case SOUTH:
//			controller.turnRight(delta);
			if(!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				controller.turnLeft(delta);
			}
			break;
		case EAST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.turnLeft(delta);
			}
			break;
		case WEST:
			
			if(!controller.getOrientation().equals(WorldSpatial.Direction.WEST)){
				controller.turnLeft(delta);
			}
			break;
		case NORTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.turnLeft(delta);
			}
			break;
		default:
			break;	
		}	
	}
}
