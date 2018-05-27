/**
 * this file is created for project work for SWEN30006 Software Modeling Design
 * Group 77, group members: Jihai Fan, Raymond Guo, Mofan Li
 */
package mycontroller;

import java.util.Objects;

import utilities.Coordinate;

public class Position {
	Coordinate coor;
	int direction;
	
	/**
	 *  position records in what direction does the car stayed in which coordinate
	 */
	public Position(Coordinate coor, int dir) {
		this.coor = coor;
		this.direction = dir;
	}

	/**
	 * Defined in order to use it as keys in a hashmap
	 */
	public boolean equals(Object c){
		if(c == this){
			return true;
		}
		if(!(c instanceof Position)){
			return false;
		}
		Position pos = (Position) c;
		return (pos.coor.equals(this.coor) && pos.direction == this.direction);
	}
	
	public int hashCode(){
		return Objects.hash(coor.x, coor.y, direction);
	}
}
