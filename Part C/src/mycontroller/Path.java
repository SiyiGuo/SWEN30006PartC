package mycontroller;
import java.util.ArrayList;

import utilities.Coordinate; 

public class Path {
	private ArrayList<Coordinate> path;
	private int cost;
	
	public Path() {
		this.path = new ArrayList<Coordinate>();
		this.cost = 0;
	}
}
