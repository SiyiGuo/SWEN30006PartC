package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

public class PerceptionModule {
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> unsearched;
	private HashMap<Coordinate, MapTile> knownMap;
	private HashMap<Integer, Coordinate> keyMap;
	private ArrayList<Coordinate> exits;
	
	public PerceptionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
		
		// knownMap is the whole map with all the trap tiles hiden.
		this.knownMap = controller.getMap();
		this.keyMap = new HashMap<Integer, Coordinate>();
		this.unsearched = new ArrayList<Coordinate>();
		this.exits = new ArrayList<Coordinate>();
		for (Coordinate coor: knownMap.keySet()) {
			if (knownMap.get(coor).isType(MapTile.Type.ROAD))
				this.unsearched.add(coor);
			if (knownMap.get(coor).isType(MapTile.Type.FINISH)) 
				this.exits.add(coor);
		}
	}

	public boolean isLava(Coordinate coor) {
		if (!this.knownMap.containsKey(coor)) {
			return false;
		}
		MapTile tile = knownMap.get(coor);
		if (tile.isType(MapTile.Type.TRAP) && ((TrapTile)tile).getTrap().equals("lava")) {
			return true;
		}else
			return false;
	}
	
	public boolean isHealth(Coordinate coor) {
		if (!this.knownMap.containsKey(coor)) {
			return false;
		}
		MapTile tile = knownMap.get(coor);
		if (tile.isType(MapTile.Type.TRAP) && ((TrapTile)tile).getTrap().equals("health")) {
			return true;
		}else
			return false;
	}
	
	public void update() {

		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = this.controller.getView();
		
		// update what the car have seen, check the keys.
		for (Coordinate coor: currentView.keySet()) {
			
			if (unsearched.contains(coor)) {
				this.knownMap.put(coor, currentView.get(coor));
				unsearched.remove(coor);
				
				// if the recorded tile is a trap, check if it contains a key.
				if (currentView.get(coor).isType(MapTile.Type.TRAP)) {
					TrapTile trap = (TrapTile)currentView.get(coor);
					if (trap.getTrap().equals("lava")){
						LavaTrap lava = (LavaTrap)trap;
						if (lava.getKey() != 0) {
							this.keyMap.put(lava.getKey(), coor);
						}
					}
				}
			}
		}
	}
	
	public HashMap<Coordinate, MapTile> getKnownMap(){
		return knownMap;
	}
	
	public HashMap<Integer, Coordinate> getKeyMap() {
		return keyMap;
	}

	public ArrayList<Coordinate> getExit(){
		return exits;
	}
	
	public ArrayList<Coordinate> getHealthTraps(){
		ArrayList<Coordinate> healths = new ArrayList<Coordinate>();
		for(Coordinate coor: knownMap.keySet()) {
			if (isHealth(coor)) {
				healths.add(coor);
			}
		}
		return healths;
	}
	
	public ArrayList<Coordinate> getUnsearched(){
		return unsearched;
	}
}
