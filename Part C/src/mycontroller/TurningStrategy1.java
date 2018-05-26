package mycontroller;

import world.Car;

public class TurningStrategy1 implements TurningStrategy{
	private Car car;
	public TurningStrategy1(Car car) {
		this.car = car;
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		System.out.println("turing to "+absoluteDegree);
		this.turnToDirection(this.car.getAngle(), delta, absoluteDegree);
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
		float directionDiff = Math.abs(currentDegree - absoluteDegree);
		if (directionDiff > 180) directionDiff = 360 - directionDiff;
		float turning_ratio = directionDiff / 180 * 54;
		//if (this.car.getSpeed() > 2)
		//	this.car.applyReverseAcceleration();
		//if (this.car.getSpeed() < 2)
		//	this.car.applyForwardAcceleration();
		if ((currentDegree != absoluteDegree)) {
			
			if ((currentDegree - absoluteDegree) >= 0 ) {
				if (Math.abs(currentDegree - absoluteDegree) <= Math.abs(360 - currentDegree + absoluteDegree)) {
					this.car.turnLeft(turning_ratio*delta);
				} else {
					this.car.turnRight(turning_ratio*delta);
				}
			} else {
				if (Math.abs(absoluteDegree-currentDegree) < Math.abs(360 + currentDegree - absoluteDegree)) {
					this.car.turnRight(turning_ratio*delta);
				} else {
					this.car.turnLeft(turning_ratio*delta);
				}
			}
		}  else {
			this.car.brake();
		}
	}

}
