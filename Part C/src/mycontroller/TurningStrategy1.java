package mycontroller;

import world.Car;

public class TurningStrategy1 implements TurningStrategy{
	private Car car;
	public TurningStrategy1(Car car) {
		this.car = car;
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		this.turnToDirection(this.car.getAngle(), delta, absoluteDegree);
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
		float turning_ratio = (float)2;
		if ((currentDegree != absoluteDegree)) {

			this.car.applyReverseAcceleration();
			
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
