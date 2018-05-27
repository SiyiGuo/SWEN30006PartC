package mycontroller;
import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate; 

public class Route {
	private ArrayList<Coordinate> path;
	private int currentDirection;
	private int cost;
	public Route(ArrayList<Coordinate> path, int cost, int currentDirection) {
		this.path = path;
		this.cost = cost;
		this.currentDirection = currentDirection;
	}
	
	public Route(Route route) {
		this.cost = route.getCost();
		this.currentDirection = route.getCurrentDirection();
		this.path = new ArrayList<Coordinate>();
		for (Coordinate coor: route.getPath())
			this.path.add(coor);
	}
	
	public ArrayList<Coordinate> getPath(){
		return this.path;
	}
	
	public int getCost() {
		return this.cost;
	}
	/**
	 * this method add a new node to a route and updates its cost.
	 */
	public void addNode(Coordinate coor, int direction, HashMap<Coordinate, MapTile> knownMap) {
		//System.out.println("path was " + this.path.toString() + "\ncost was "+this.cost);
		switch (this.path.size()) {
		case(0):
			this.path.add(coor);
			this.currentDirection = direction;
			break;
		default:
			Coordinate previousCoor = path.get(path.size() - 1);
			Coordinate forwardCoor = getForwardCoor(coor, direction, 1);
			Coordinate backwardTwoCoor = getForwardCoor(coor, direction, -2);
			Coordinate backwardThreeCoor = getForwardCoor(coor, direction, -3);
			
			this.path.add(coor);
			cost++;
			boolean turning = false;
			if (direction != currentDirection) {
				currentDirection = direction;
				turning = true;
			}
			// turning incurs speed reducing, add cost.
			if (turning) {
				cost += 3;
				// speed reducing can be deadly in a lava tile.
				if (PerceptionModule.isLava(previousCoor, knownMap))
					cost += 200;
			}
			
			// entering lava incurs great cost
			if (PerceptionModule.isLava(coor, knownMap)) {
				cost += 40;
				
				// track back the tile behind the car, if it is not in the route, the car
				//  will be at a low speed, entering lava with low speed costs more health
				if (!path.contains(backwardTwoCoor)) {
					cost += 10;
				}
				if (!path.contains(backwardThreeCoor)) {
					cost += 5;
				}
				
				// entering lava with a wall forward, means u can not speed up and leave,
				// must turn or reverse to exit lava, can be slow and deadly
				if (PerceptionModule.isWall(forwardCoor, knownMap))
					cost += 200;
			}
			
			// rewards for passing a health trap
			if (PerceptionModule.isHealth(coor, knownMap)) {
				cost -= 5;
			}
		}

		//System.out.println("path now " + this.path.toString() + "\ncost now "+this.cost+"\n");
	}

	/**
	 * this method finds the coordinate of the tile behind the car by a certain distance
	 */
	public static Coordinate getForwardCoor(Coordinate current, int direction, int distance) {
		int x = current.x;
		int y = current.y;
		switch(direction) {
		case 0:
			x += distance;
			break;
		case 1:
			y += distance;
			break;
		case 2:
			x -= distance;
			break;
		case 3:
			y -= distance;
			break;			
		}
		return new Coordinate(x+","+y);
	}
	
	public static int numOfLavaOnPath(ArrayList<Coordinate> path, HashMap<Coordinate, MapTile> knownMap) {
		int count = 0;
		for (Coordinate coor: path) {
			if (PerceptionModule.isLava(coor, knownMap)) count++;
		}
		return count;
	}
	
	public int getCurrentDirection() {
		return this.currentDirection;
	}
}
