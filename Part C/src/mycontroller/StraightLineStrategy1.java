package mycontroller;
import utilities.Coordinate;
import world.Car;

public class StraightLineStrategy1 implements StraightLineStrategy{
	private MyAIController car;
	private float maxSpeed = (float)2.5;
	
	public StraightLineStrategy1(MyAIController car) {
		this.car = car;
	}
	
	@Override
	public void setMaxSpeed(float newMaxSpeed){
		this.maxSpeed = newMaxSpeed;
	}
	
	@Override
	public void move(Coordinate nextPos, float accurate_x, float accurate_y) {
		
		
		if (this.car.getSpeed() < this.maxSpeed) {
			this.car.applyForwardAcceleration();
		}
		
	}
}
