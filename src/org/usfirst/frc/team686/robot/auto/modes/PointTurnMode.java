package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;



public class PointTurnMode extends AutoModeBase {

	double targetHeading;
	
    public PointTurnMode(double _targetHeading) 
    {
    	targetHeading = _targetHeading;
    }

    @Override
    protected void routine() throws AutoModeEndedException 
    {
        runAction(new PointTurnAction(targetHeading));   
    }
}
