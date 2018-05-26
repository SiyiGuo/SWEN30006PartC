package mycontroller;
import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
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
	
	public void addNode(Coordinate coor, int direction, HashMap<Coordinate, MapTile> knownMap) {
		System.out.println("path was: "+path.toString() + " \n cost was "+ cost + "direction was: " + this.currentDirection);
		switch (this.path.size()) {
		case(0):
			this.path.add(coor);
			this.currentDirection = direction;
			break;
		default:
			Coordinate previousCoor = path.get(path.size() - 1);
			Coordinate forwardCoor = getForwardCoor(coor, direction);
			Coordinate twoTileBackCoor = trackByDir(coor, direction, 2);
			Coordinate threeTileBackCoor = trackByDir(coor, direction, 3);
			
			this.path.add(coor);
			cost++;
			boolean turning = false;
			if (direction != currentDirection) {
				currentDirection = direction;
				turning = true;
			}
			if (turning) {
				cost += 5;
				if (isLava(previousCoor, knownMap))
					cost += 100;
			}
			if (isLava(coor, knownMap)) {
				cost += 15;
				if (!path.contains(twoTileBackCoor)) {
					cost += 10;
				}
				if (!path.contains(threeTileBackCoor)) {
					cost += 5;
				}
				if (isWall(forwardCoor, knownMap))
					cost += 100;
			}
			if (isHealth(coor, knownMap)) {
				cost -= 1;
			}
		}
	}
	public Coordinate trackByDir(Coordinate current, int dir, int distance) {
		int backCoorX = current.x;
		int backCoorY = current.y;
		switch (dir) {
		case (0):
			backCoorX -= distance;
			break;
		case (1):
			backCoorY -= distance;
			break;
		case (2):
			backCoorX += distance;
			break;
		case (3):
			backCoorY += distance;
			break;
		}
		return new Coordinate(backCoorX + "," + backCoorY);
	}
	
	public static boolean isLava(Coordinate coor, HashMap<Coordinate, MapTile> knownMap) {
		if (!knownMap.containsKey(coor)) {
			return false;
		}
		MapTile tile = knownMap.get(coor);
		if (tile.isType(MapTile.Type.TRAP) && ((TrapTile)tile).getTrap().equals("lava")) {
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean isHealth(Coordinate coor, HashMap<Coordinate, MapTile> knownMap) {
		if (!knownMap.containsKey(coor)) {
			return false;
		}
		MapTile tile = knownMap.get(coor);
		if (tile.isType(MapTile.Type.TRAP) && ((TrapTile)tile).getTrap().equals("health")) {
			return true;
		}else
			return false;
	}
	
	public static boolean isWall(Coordinate coor, HashMap<Coordinate, MapTile> knownMap) {
		if (!knownMap.containsKey(coor)) {
			return false;
		}
		MapTile tile = knownMap.get(coor);
		if (tile.isType(MapTile.Type.WALL)) {
			return true;
		}else
			return false;
	}
	
	public Coordinate getForwardCoor(Coordinate current, int direction) {
		int x = current.x;
		int y = current.y;
		switch(direction) {
		case 0:
			x++;
			break;
		case 1:
			y++;
			break;
		case 2:
			x--;
			break;
		case 3:
			y--;
			break;			
		}
		return new Coordinate(x+","+y);
	}
	
	public int getCurrentDirection() {
		return this.currentDirection;
	}
}
