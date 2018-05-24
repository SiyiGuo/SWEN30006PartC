package mycontroller;

import world.Car;

public class TurningStrategy1 implements TurningStrategy{
	private Car car;
	public TurningStrategy1(Car car) {
		this.car = car;
	}
	
	public void turn(float angle, float delta, int northDegree) {
		this.car.applyReverseAcceleration();
		this.turnToDirection(angle, delta, northDegree);
		
	}
	
	private void turnToDirection(float f, float delta, int northDegree) {
		System.out.println(northDegree);
		if ((f != northDegree)) {
			if ((f - northDegree) >= 0 ) {
				System.out.println(f-northDegree);
				if (Math.abs(f - northDegree) <= Math.abs(360 - f + northDegree)) {
					this.car.turnLeft(2*delta);
				} else {
					this.car.turnRight(2*delta);
				}
			} else {
				if (Math.abs(northDegree-f) < Math.abs(360 + f - northDegree)) {
					this.car.turnRight(2*delta);
				} else {
					this.car.turnLeft(2*delta);
				}
			}
		} 
	}

}
