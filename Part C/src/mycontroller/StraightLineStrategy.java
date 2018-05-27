/**
 * this file is created for project work for SWEN30006 Software Modeling Design
 * Group 77, group members: Jihai Fan, Raymond Guo, Mofan Li
 */
package mycontroller;

import utilities.Coordinate;

public interface StraightLineStrategy {
	public abstract void move(Coordinate nextPost, float accurate_x, float accurate_y);
	public abstract void setMaxSpeed(float max_speed);
}
