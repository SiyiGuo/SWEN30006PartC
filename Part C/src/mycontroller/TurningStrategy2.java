package mycontroller;

import java.util.concurrent.TimeUnit;

import world.Car;

public class TurningStrategy2 implements TurningStrategy{
	private MyAIController car;
	public TurningStrategy2(MyAIController car) {
		this.car = car;
	}
	
	@Override
	public void turn(float delta, int absoluteDegree) {
		if (this.car.getSpeed() > 0.5) {
		/*	System.out.println("stopppppppppppp!!");
			
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//			}*/
			
			this.car.applyBrake();
		}else {
			this.turnToDirection(this.car.getAngle(), delta, absoluteDegree);
		}
	}
	
	private void turnToDirection(float currentDegree, float delta, int absoluteDegree) {
		if ((currentDegree != absoluteDegree)) {
			
			if (this.car.getSpeed() > 0.1) {
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
