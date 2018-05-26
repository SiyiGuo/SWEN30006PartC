package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class DecisionModule {
	public enum Mode{SEARCHING, DESTINATION};
	public static final String DONOTHING = "99,99";
	public static final int LOWHEALTHTHRESHOLD = 20;
	public static final int RECOVERTHRESHOLD = 90;
	public static final float STOPTHRESHOLD = 1.5f;
	public static final int EAST = 0;
	public static final int NORTH = 1;
	public static final int WEST = 2;
	public static final int SOUTH = 3;
	public static final int MAXCOST = 999999;
	
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> destinations;
	private HashMap<Coordinate, MapTile> roadMap;
	private Coordinate positionWhenLastFindPath;
	private ArrayList<Coordinate> lastPath;
	
	public DecisionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
		this.roadMap = controller.getMap();
	}
	//reminder: health»ØÑª,
	public ArrayList<Coordinate> generatePath() {
		Coordinate currentCoor = new Coordinate(controller.getPosition());
		HashMap<Position, Integer> costs = new HashMap<Position, Integer>();
		Position currentPos, newPos;
		for (Coordinate coor:this.roadMap.keySet()) {
			//representing four directions
			for (int i = 0; i < 4; i++) {
				Position pos = new Position(coor, i);
				costs.put(pos, MAXCOST);
			}
		}
		
		int iniDirection = Math.round(car.getAngle() / 90) % 4;
		
		if (positionWhenLastFindPath != null && currentCoor.equals(positionWhenLastFindPath)) {
			return lastPath;
		}
		
		if (this.car.getHealth() < RECOVERTHRESHOLD && 
			Route.isHealth(currentCoor, this.controller.getKnownMap())) {
			ArrayList<Coordinate> path = new ArrayList<Coordinate>();
			path.add(new Coordinate(DONOTHING));
			return path;
		}
		
		Route route;
		ArrayList<Route> routes;
		routes = new ArrayList<Route>();
		
		if (this.car.getHealth() < LOWHEALTHTHRESHOLD) {
			this.destinations = this.controller.getPModule().getHealthTraps();
		} else if ((this.controller.getKey() == 1) || 
				   (this.controller.getPModule().getKeyMap().
					containsKey(this.controller.getKey() - 1))){
			if (this.controller.getKey() == 1) {
				this.destinations = this.controller.getPModule().getExit();
			} else {
				this.destinations = new ArrayList<Coordinate>();
				this.destinations.add(this.controller.getPModule().getKeyMap().
									  get(this.controller.getKey() - 1));
			}
		} else {
			this.destinations = this.controller.getPModule().getUnsearched();
		}
		
		route = new Route(new ArrayList<Coordinate>(), 0, iniDirection);
		route.addNode(currentCoor, iniDirection, this.controller.getKnownMap());
		currentPos = new Position(currentCoor, iniDirection);
		if (route.getCost() < costs.get(currentPos))
			costs.put(currentPos, route.getCost());
		
		if (destinations.contains(currentCoor)) {
			return route.getPath();
		}
		
		routes.add(route);
		while (!routes.isEmpty()) {
			route = routes.remove(0);
			ArrayList<Coordinate> path = route.getPath();
			Coordinate lastNode = path.get(path.size() - 1);
			
			if (destinations.contains(lastNode)) {
				this.positionWhenLastFindPath = currentCoor;
				this.lastPath = new ArrayList<Coordinate>(route.getPath());
				return processWithRecoverStrategy(route.getPath());
			}
			
			Coordinate lastNodeWest = new Coordinate((lastNode.x - 1) + "," + lastNode.y);
			Coordinate lastNodeEast = new Coordinate((lastNode.x + 1) + "," + lastNode.y);
			Coordinate lastNodeNorth = new Coordinate(lastNode.x + "," + (lastNode.y + 1));
			Coordinate lastNodeSouth = new Coordinate(lastNode.x + "," + (lastNode.y - 1));
			
			Coordinate[] neighbours = {lastNodeEast, lastNodeNorth, lastNodeWest, lastNodeSouth};
			for (int i = 0; i < neighbours.length; i++) {

				if (this.roadMap.containsKey(neighbours[i])) {
					if ((this.roadMap.get(neighbours[i]).isType(MapTile.Type.ROAD)) ||
						(this.roadMap.get(neighbours[i]).isType(MapTile.Type.FINISH)) ||
						(this.roadMap.get(neighbours[i]).isType(MapTile.Type.START)) ||
						(this.roadMap.get(neighbours[i]).isType(MapTile.Type.TRAP))) {
						if (!path.contains(neighbours[i])) {
							Route routeCopy = new Route(route);
							routeCopy.addNode(neighbours[i], i, this.controller.getKnownMap());
							newPos = new Position(neighbours[i], i);
							if (route.getCost() < costs.get(newPos)) {
								costs.put(newPos, route.getCost());
								routes = insertRoute(routes, routeCopy);
							}
						}
					}
				}
			}
		}
		System.out.println("returning null!");
		return null;
	}
	
	public ArrayList<Route> insertRoute(ArrayList<Route> routes, Route route) {
		for (int i = 0; i < routes.size() - 1; i++) {
			if (route.getCost() < routes.get(i).getCost()) {
				routes.add(i, route);
				return routes;
			}
		}
		routes.add(route);
		return routes;
	}
	
	public ArrayList<Coordinate> processWithRecoverStrategy(ArrayList<Coordinate> path){
		if (this.car.getHealth() > RECOVERTHRESHOLD) {
			return path;
		}
		
		for (int i = 0; i < path.size(); i++) {
			if (Route.isHealth(path.get(i), this.controller.getKnownMap())) {
				for (int j = i + 1; j < path.size(); j++)
					path.remove(j);
			}
		}
		return path;
	}
}
