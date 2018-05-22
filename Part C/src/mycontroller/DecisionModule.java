package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

public class DecisionModule {
	public enum Mode{SEARCHING, DESTINATION};
	
	private Mode actionMode;
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> destinations;
	private ArrayList<Coordinate> path;
	private HashMap<Coordinate, MapTile> roadMap;
	private Coordinate positionWhenLastFindPath;
	private ArrayList<Coordinate> lastPath;
	
	public DecisionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
		this.roadMap = controller.getMap();
	}
	
	public ArrayList<Coordinate> generatePath() {
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		
		if (positionWhenLastFindPath != null && currentPosition.equals(positionWhenLastFindPath)) {
			return lastPath;
		}
		ArrayList<ArrayList<Coordinate>> paths;
		ArrayList<Coordinate> traversed;
		paths = new ArrayList<ArrayList<Coordinate>>();
		
		if ((this.controller.getKey() == 1) || 
			(this.controller.getPModule().getKeyMap().
			 containsKey(this.controller.getKey() - 1))){
			this.actionMode = Mode.DESTINATION;
			if (this.controller.getKey() == 1) {
				this.destinations = this.controller.getPModule().getExit();
			} else {
				this.destinations = new ArrayList<Coordinate>();
				this.destinations.add(this.controller.getPModule().getKeyMap().
									  get(this.controller.getKey() - 1));
			}
		}else {
			this.actionMode = Mode.SEARCHING;
			this.destinations = this.controller.getPModule().getUnsearched();
		}
		
		path = new ArrayList<Coordinate>();
		traversed = new ArrayList<Coordinate>();
		path.add(currentPosition);
		traversed.add(currentPosition);
		
		paths.add(path);
		while (!paths.isEmpty()) {
			path = paths.remove(0);
			if (!paths.isEmpty() && (path.size() > paths.get(0).size())) {
				paths.add(path);
				continue;
			}
			Coordinate lastNode = path.get(path.size() - 1);
			Coordinate lastNodeWest = new Coordinate((lastNode.x - 1) + "," + lastNode.y);
			Coordinate lastNodeEast = new Coordinate((lastNode.x + 1) + "," + lastNode.y);
			Coordinate lastNodeNorth = new Coordinate(lastNode.x + "," + (lastNode.y + 1));
			Coordinate lastNodeSouth = new Coordinate(lastNode.x + "," + (lastNode.y - 1));
			Coordinate[] neighbours = {lastNodeWest, lastNodeEast, lastNodeNorth, lastNodeSouth};
			for (int i = 0; i < neighbours.length; i++) {
				if (destinations.contains(neighbours[i])) {
					path.add(neighbours[i]);
					this.positionWhenLastFindPath = currentPosition;
					this.lastPath = new ArrayList<Coordinate>(path);
					return path;
				}
				if (this.roadMap.containsKey(neighbours[i])) {
					if ((this.roadMap.get(neighbours[i]).isType(MapTile.Type.ROAD)) ||
						(this.roadMap.get(neighbours[i]).isType(MapTile.Type.TRAP))) {
						if (!traversed.contains(neighbours[i])) {
							ArrayList<Coordinate> pathcopy = new ArrayList<Coordinate>(path);
							pathcopy.add(neighbours[i]);
							traversed.add(neighbours[i]);
							MapTile neighbour = this.controller.getPModule().getKnownMap().
												get(neighbours[i]);
							if (neighbour.isType(MapTile.Type.TRAP) && 
								((TrapTile)neighbour).getTrap().equals("lava")) {
								pathcopy.add(neighbours[i]);
							}
							paths.add(pathcopy);
						}
					}
				}
			}
		}
		return null;
	}
}
