package org.usfirst.frc.team686.robot.auto.actions.test;

import org.usfirst.frc.team686.robot.lib.sensors.GyroBase;

// fake gyro class that 

public class FakeGyro extends GyroBase
{
	private static GyroBase instance = new FakeGyro();
	public static GyroBase getInstance() { return instance; }
	
	double headingDeg = 0.0;
	
    // constructors
    public FakeGyro() 
    {
    	headingDeg = 0.0;
    }

	public void setHeadingDeg(double _headingDeg) {
		headingDeg = _headingDeg;	 
	}

	/**
	 * Returns heading for the GyroBase class.
	 *
	 */
	public double getHeadingDeg() {
		return headingDeg;	 
	}

}
