package mycontroller;
import utilities.Coordinate;
import world.Car;

public class StraightLineStrategy1 implements StraightLineStrategy{
	private Car car;
	public StraightLineStrategy1(Car car) {
		this.car = car;
	}
	
	public void move(Coordinate nextPos, float accurate_x, float accurate_y) {
		float maxSpeed = (float) 1.6;
		
		if (this.car.getSpeed() < maxSpeed) {
			this.car.applyForwardAcceleration();
		}
		
	}
}
