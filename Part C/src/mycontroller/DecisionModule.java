package mycontroller;

import java.util.ArrayList;
import java.util.Queue;

import utilities.Coordinate;
import world.Car;

public class DecisionModule {
	public enum Mode{SEARCHING, DESTINATION};
	
	private Mode actionMode;
	private Car car;
	private MyAIController controller;
	private ArrayList<Coordinate> destinations;
	
	public DecisionModule(MyAIController controller, Car car) {
		this.car = car;
		this.controller = controller;
	}
	
	public ArrayList<Coordinate> generatePath() {
		Queue<ArrayList<Coordinate>> paths;
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
		
		Coordinate currentPosition = new Coordinate(controller.getPosition());
	}
}
