package mycontroller;
import utilities.Coordinate;
import world.Car;

public class StraightLineStrategy1 implements StraightLineStrategy{
	private Car car;
	public StraightLineStrategy1(Car car) {
		this.car = car;
	}
	
	@Override
	public void move(Coordinate nextPos, float accurate_x, float accurate_y) {
		float maxSpeed = (float)5;
		
		if (this.car.getSpeed() < maxSpeed) {
			this.car.applyForwardAcceleration();
		}
		
	}
}
