package mycontroller;

import utilities.Coordinate;

public interface StraightLineStrategy {
	public abstract void move(Coordinate nextPost, float accurate_x, float accurate_y);
	public abstract void setMaxSpeed(float max_speed);
}
