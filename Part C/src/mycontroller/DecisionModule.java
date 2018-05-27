/**
 * this file is created for project work for SWEN30006 Software Modeling Design
 * Group 77, group members: Jihai Fan, Raymond Guo, Mofan Li
 */
package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class DecisionModule {
	public enum Mode{SEARCHING, DESTINATION};
	public static final String DONOTHING = "99,99";
	public static final int LOWHEALTHTHRESHOLD = 30;
	public static final int RECOVERTHRESHOLD = 90;
	public static final float STOPTHRESHOLD = 1.5f;
	public static final int EAST = 0;
	public static final int NORTH = 1;
	public static final int WEST = 2;
	public static final int SOUTH = 3;
	public static final int MAXCOST = 999999;
	private static final int MAXESCAPESEARCHRANGE = 10;
	private static final int TURNPUNISHMENT = 5;
	private static final int REVERSEPUNISHMENT = 3;
	
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> destinations;
	private HashMap<Coordinate, MapTile> roadMap;
	private Coordinate positionWhenLastFindPath;
	private ArrayList<Coordinate> lastPath;
	private int lastPathLavaCount;
	private Mode mode;
	
	public DecisionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
		this.roadMap = controller.getMap();
	}
	//reminder: health»ØÑª,
	public ArrayList<Coordinate> generatePath() {
		
		Coordinate currentCoor = new Coordinate(controller.getPosition());
		HashMap<Position, Integer> costs = new HashMap<Position, Integer>();
		HashMap<Integer, Coordinate> keyMap = this.controller.getPModule().getKeyMap();
		ArrayList<Coordinate> healths = this.controller.getPModule().getHealthTraps();
		Position currentPos, newPos, pos;
		ArrayList<Route> routes = new ArrayList<Route>();
		Route route;
		int iniDirection = Math.round(car.getAngle() / 90) % 4,
			currentKey = this.controller.getKey();
		
	
		// initialize the cost map for all positions
		for (Coordinate coor:this.roadMap.keySet()) {
			//representing four directions
			for (int i = 0; i < 4; i++) {
				pos = new Position(coor, i);
				costs.put(pos, MAXCOST);
			}
		}
		
		// if the car did not leave the previous tile, do not find find again.
		if (positionWhenLastFindPath != null && currentCoor.equals(positionWhenLastFindPath)) {
			return lastPath;
		}
		
		// stay in a health trap if health lower than 90
		if (this.car.getHealth() < RECOVERTHRESHOLD && 
			PerceptionModule.isHealth(currentCoor, this.controller.getKnownMap())) {
			ArrayList<Coordinate> path = new ArrayList<Coordinate>();
			path.add(new Coordinate(DONOTHING));
			return path;
		}
		
		// decides destination depending on 
		
		// 1) if the car is in critical low health
		if (this.car.getHealth() < LOWHEALTHTHRESHOLD && !healths.isEmpty()) {
			this.mode = Mode.DESTINATION;
			this.destinations = healths;
		} 
		
		else if ((currentKey == 1) || (keyMap.containsKey(currentKey - 1))){
			this.mode = Mode.DESTINATION;
			
			// 2) if the car holds the key to the exit, go to it
			if (this.controller.getKey() == 1) {
				this.destinations = this.controller.getPModule().getExit();
			} 
			
			// 3) if the car knows where the next key is, go to it
			else {
				this.destinations = new ArrayList<Coordinate>();
				this.destinations.add(keyMap.get(currentKey - 1));
			}
		} 

		// 4) otherwise try to search the unsearched tiles
		else {
			this.destinations = this.controller.getPModule().getUnsearched();
			this.mode = Mode.SEARCHING;
		}		

		// avoid repeatedly generating path
		if (lastPath != null && pathLeadToDestinations(destinations, lastPath) && lastPath.contains(currentCoor) 
			&& lastPathLavaCount >= Route.numOfLavaOnPath(lastPath, this.controller.getKnownMap())) {
			for (int i = lastPath.indexOf(currentCoor); i > 0; i--) {
				lastPath.remove(i - 1);
			}
			lastPathLavaCount = Route.numOfLavaOnPath(lastPath, this.controller.getKnownMap());
			return lastPath;
		}
		
		// generate a new route starting from current position and 
		// update the cost of current position
		route = new Route(new ArrayList<Coordinate>(), 0, iniDirection);
		route.addNode(currentCoor, iniDirection, this.controller.getKnownMap());
		currentPos = new Position(currentCoor, iniDirection);
		if (route.getCost() < costs.get(currentPos))
			costs.put(currentPos, route.getCost());
		
		if (destinations.contains(currentCoor)) {
			return route.getPath();
		}
		
		// add route to a queue of routes and start dijkstra algorithms.
		routes.add(route);
		while (!routes.isEmpty()) {
			route = routes.remove(0);  // read the first(lowest cost) route from queue.
			ArrayList<Coordinate> path = route.getPath(); // try expand the route from last node in the path
			Coordinate lastNode = path.get(path.size() - 1);
			if (route.getCost() > costs.get(new Position(lastNode, route.getCurrentDirection()))) {
				continue;
			}
			
			if (destinations.contains(lastNode)) {
				
				this.positionWhenLastFindPath = currentCoor;
				path = route.getPath();
				path = processWithRecoverStrategy(path);
				if (PerceptionModule.isLava(path.get(path.size() - 1), this.controller.getKnownMap())) {
					path = processWithLavaEscapeStrategy(path, route.getCurrentDirection());
				}
				this.lastPath = new ArrayList<Coordinate>(path);
				this.lastPathLavaCount = Route.numOfLavaOnPath(lastPath, this.controller.getKnownMap());
				return path;
			}
			
			Coordinate lastNodeWest = new Coordinate((lastNode.x - 1) + "," + lastNode.y);
			Coordinate lastNodeEast = new Coordinate((lastNode.x + 1) + "," + lastNode.y);
			Coordinate lastNodeNorth = new Coordinate(lastNode.x + "," + (lastNode.y + 1));
			Coordinate lastNodeSouth = new Coordinate(lastNode.x + "," + (lastNode.y - 1));
			
			// arrange the neighbors in the order of their relative direction to the lastNode
			// 0 for east, 1 for north, 2 for west and 3 for south as defined constants
			Coordinate[] neighbours = {lastNodeEast, lastNodeNorth, lastNodeWest, lastNodeSouth};
			for (int i = 0; i < neighbours.length; i++) {
				
				if (this.roadMap.containsKey(neighbours[i]) && canDriveOn(this.roadMap.get(neighbours[i]))) {
					if (!path.contains(neighbours[i])) {
						Route routeCopy = new Route(route);
						routeCopy.addNode(neighbours[i], i, this.controller.getKnownMap());
						newPos = new Position(neighbours[i], i);
						if (route.getCost() < costs.get(newPos)) {
							costs.put(newPos, routeCopy.getCost());
							routes.add(routeCopy);
							routes = routesSort(routes);
						}
					}
				}
			}
		}
		System.out.println("returning null!");
		return null;
	}
	
	private boolean pathLeadToDestinations(ArrayList<Coordinate> dests, ArrayList<Coordinate> path) {
		for (int i = path.size() - 1; i >= 0; i--) {
			if (dests.contains(path.get(i))) return true;
		}
		return false;
	}
	private ArrayList<Coordinate> escapeSearch(Coordinate lastNode, int dir, ArrayList<Coordinate> path) {
		HashMap<Coordinate, MapTile> knownMap = this.controller.getKnownMap();
		for (int i = 1; i <= MAXESCAPESEARCHRANGE; i++) {
			Coordinate forwardTile = Route.getForwardCoor(lastNode, dir, i);
			if (PerceptionModule.isWall(forwardTile, knownMap)) {
				break;
			}
			if (!PerceptionModule.isLava(forwardTile, knownMap)) {
				// escape tile found, add to path
				ArrayList<Coordinate> pathCopy = new ArrayList<Coordinate>(path);
				for (int j = 1; j <= i; j++) {
					pathCopy.add(Route.getForwardCoor(lastNode, dir, j));
				}
				return pathCopy;
			}				
		}
		return null;
	}
	
	private ArrayList<Coordinate> processWithLavaEscapeStrategy(ArrayList<Coordinate> path, int dir) {
		Coordinate lastNode = path.get(path.size() - 1);
		ArrayList<Coordinate> fresult, bresult, lresult, rresult;
		int forward = dir, backward = (dir + 2) % 4, min = path.size() + MAXESCAPESEARCHRANGE,
			left = (dir + 1) % 4, right = (dir + 3) % 4;
		
		fresult = this.escapeSearch(lastNode, forward, path);
		bresult = this.escapeSearch(lastNode, backward, path);
		lresult = this.escapeSearch(lastNode, left, path);
		rresult = this.escapeSearch(lastNode, right, path);

		min = fresult != null && fresult.size() < min? fresult.size(): min;
		min = bresult != null && bresult.size() + REVERSEPUNISHMENT < min? bresult.size() + REVERSEPUNISHMENT: min;
		min = lresult != null && lresult.size() + TURNPUNISHMENT < min? lresult.size() + TURNPUNISHMENT: min;
		min = rresult != null && rresult.size() + TURNPUNISHMENT < min? rresult.size() + TURNPUNISHMENT: min;

		if(fresult != null && min == fresult.size()) return fresult;
		if(bresult != null && min == bresult.size() + REVERSEPUNISHMENT) return bresult;
		if(lresult != null && min == lresult.size() + TURNPUNISHMENT) return lresult;
		if(rresult != null && min == rresult.size() + TURNPUNISHMENT) return rresult;
		return path;
	}
	
	/**
	 * test if the car can drive on a given tile
	 */
	public boolean canDriveOn(MapTile tile) {
		if ((tile.isType(MapTile.Type.ROAD)) ||
			(tile.isType(MapTile.Type.FINISH)) ||
			(tile.isType(MapTile.Type.START)) ||
			(tile.isType(MapTile.Type.TRAP))){
			return true;
		}
		return false;
	}
	
	/**
	 * insertion sort on last element.
	 */	
	public ArrayList<Route> routesSort(ArrayList<Route> routes) {
		Route last = routes.remove(routes.size() - 1);
		for (int i = 0; i < routes.size(); i++) {
			if (routes.get(i).getCost() > last.getCost()) {
				routes.add(i, last);
				return routes;
			}
		}
		routes.add(last);
		return routes;
	}
	
	/**
	 * if the path passes by a health trap while car's health is not full, recover in it
	 */
	public ArrayList<Coordinate> processWithRecoverStrategy(ArrayList<Coordinate> path){
		if (this.car.getHealth() > RECOVERTHRESHOLD) {
			return path;
		}
		
		for (int i = 0; i < path.size(); i++) {
			if (PerceptionModule.isHealth(path.get(i), this.controller.getKnownMap())) {
				for (int j = path.size() - 1; j > i; j--)
					path.remove(j);
			}
		}
		return path;
	}
	
	public Mode getMode() {
		return this.mode;
	}
}
