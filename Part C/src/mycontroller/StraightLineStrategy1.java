package mycontroller;
import utilities.Coordinate;
import world.Car;

public class StraightLineStrategy1 implements StraightLineStrategy{
	private MyAIController carController;
	private float maxSpeed = (float)5;
	
	public StraightLineStrategy1(MyAIController car) {
		this.carController = car;
	}
	
	@Override
	public void setMaxSpeed(float newMaxSpeed){
		this.maxSpeed = newMaxSpeed;
//		System.out.println(this.maxSpeed);
	}
	
	@Override
	public void move(Coordinate nextPos, float accurate_x, float accurate_y) {
		if (this.carController.getSpeed() < this.maxSpeed) {
			this.carController.applyForwardAcceleration();
		} else {
			this.carController.applyBrake();
		}
		 
	}
}
