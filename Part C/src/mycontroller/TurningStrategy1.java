package mycontroller;

import world.Car;

public class TurningStrategy1 implements TurningStrategy{
	private Car car;
	public TurningStrategy1(Car car) {
		this.car = car;
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		this.car.applyReverseAcceleration();
		this.turnToDirection(this.car.getAngle(), delta, absoluteDegree);
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
//		System.out.println(absoluteDegree);
		if ((currentDegree != absoluteDegree)) {
			if ((currentDegree - absoluteDegree) >= 0 ) {
//				System.out.println(currentDegree-absoluteDegree);
				if (Math.abs(currentDegree - absoluteDegree) <= Math.abs(360 - currentDegree + absoluteDegree)) {
					this.car.turnLeft(2*delta);
				} else {
					this.car.turnRight(2*delta);
				}
			} else {
				if (Math.abs(absoluteDegree-currentDegree) < Math.abs(360 + currentDegree - absoluteDegree)) {
					this.car.turnRight(2*delta);
				} else {
					this.car.turnLeft(2*delta);
				}
			}
		} 
	}

}
