package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class GrabberTestMode extends AutoModeBase {

    public GrabberTestMode() {}
    
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("Starting Auto Mode: Grabber Test");

    	for (int k=0; k<100; k++)
    	{
	        runAction(new GrabberOpenAction());	
	        runAction(new WaitAction(0.5));
	        runAction(new GrabberCloseAction());	
	        runAction(new WaitAction(0.5));
    	}
    }
}
