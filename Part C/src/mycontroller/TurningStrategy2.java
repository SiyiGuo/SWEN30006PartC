package mycontroller;

import world.Car;

public class TurningStrategy2 implements TurningStrategy{
	private MyAIController car;
	public TurningStrategy2(MyAIController car) {
		this.car = car;
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		this.turnToDirection(this.car.getAngle(), delta, absoluteDegree);
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
		if ((currentDegree != absoluteDegree)) {
			
			if (this.car.getSpeed() > 0.10) {
				this.car.applyBrake();
			} else {
				this.car.applyReverseAcceleration();
			}
			
			
			
			if ((currentDegree - absoluteDegree) >= 0 ) {
				if (Math.abs(currentDegree - absoluteDegree) <= Math.abs(360 - currentDegree + absoluteDegree)) {
					this.car.turnLeft(delta);
				} else {
					this.car.turnRight(delta);
				}
			} else {
				if (Math.abs(absoluteDegree-currentDegree) < Math.abs(360 + currentDegree - absoluteDegree)) {
					this.car.turnRight(delta);
				} else {
					this.car.turnLeft(delta);
				}
			}
		}  else {
			this.car.applyBrake();
		}
	}

}
