package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;



public class PointTurnMode extends AutoModeBase {

	double targetHeadingDeg;
	
    public PointTurnMode(double _targetHeadingDeg) 
    {
    	targetHeadingDeg = _targetHeadingDeg;
    }

    @Override
    protected void routine() throws AutoModeEndedException 
    {
        runAction(new PointTurnAction(targetHeadingDeg));   
    }
}
