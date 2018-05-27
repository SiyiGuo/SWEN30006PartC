package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

public class PerceptionModule {
	private ArrayList<Coordinate> unsearched;
	private HashMap<Coordinate, MapTile> knownMap;
	private HashMap<Integer, Coordinate> keyMap;
	private ArrayList<Coordinate> exits;
	private ArrayList<Coordinate> healths;
	
	/**
	 * a perception module is a car's detector and memory module
	 * @param controller
	 * @param car
	 */
	public PerceptionModule(HashMap<Coordinate, MapTile> knownMap) {
		
		// knownMap is the whole map with all the trap tiles hidden.
		this.keyMap = new HashMap<Integer, Coordinate>();
		this.unsearched = new ArrayList<Coordinate>();
		this.exits = new ArrayList<Coordinate>();
		this.healths = new ArrayList<Coordinate>();
		this.knownMap = knownMap;
		for (Coordinate coor: knownMap.keySet()) {
			if (knownMap.get(coor).isType(MapTile.Type.ROAD))
				this.unsearched.add(coor);
			if (knownMap.get(coor).isType(MapTile.Type.FINISH)) 
				this.exits.add(coor);
		}
	}
	
	/**
	 * check if a given coordinate is in the map and if is a a lava trap
	 * @param coor is the Coordinate to check
	 * @param knownMap is a map recorded by the PerceptionModule
	 * @return
	 */
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

	/**
	 * check if a given coordinate is in the map and if is a a health trap
	 * @param coor is the Coordinate to check
	 * @param knownMap is a map recorded by the PerceptionModule
	 * @return
	 */
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

	/**
	 * check if a given coordinate is in the map and if is a a wall
	 * @param coor is the Coordinate to check
	 * @param knownMap is a map recorded by the PerceptionModule
	 * @return
	 */
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

	/**
	 * check if a given coordinate is in the map and if is a a road
	 * @param coor is the Coordinate to check
	 * @param knownMap is a map recorded by the PerceptionModule
	 * @return
	 */
	public static boolean isRoad(Coordinate coor, HashMap<Coordinate, MapTile> knownMap) {
		if (!knownMap.containsKey(coor)) {
			return false;
		}
		MapTile tile = knownMap.get(coor);
		if (tile.isType(MapTile.Type.ROAD)) {
			return true;
		}else
			return false;
	}
	
	/**
	 * record what the car detected at the current position into the knownMap
	 * @param currentView is what car detected at the current position
	 */
	public void update(HashMap<Coordinate, MapTile> currentView) {
		
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
					if (trap.getTrap().equals("health")){
						healths.add(coor);
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
		return this.healths;
	}
	
	public ArrayList<Coordinate> getUnsearched(){
		return unsearched;
	}
}
