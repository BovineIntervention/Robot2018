package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartDelayOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerAction;
import org.usfirst.frc.team686.robot.auto.actions.WaitAction;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class CrossTheLineMode extends AutoModeBase {
	
	double startDelay;
	
	public CrossTheLineMode (StartDelayOption _startDelayOption)
	{
	   	startDelay = _startDelayOption.delaySec;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: CrossTheLine");
	
		
		// TODO: fill out
			
		//getting ready, setting variables
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
        Vector2d initialPosition = initialPose.getPosition();
		Vector2d stopPosition = new Vector2d(initialPosition.getX() + 300, initialPosition.getY());	// needed if starting from center to avoid clipping switch
		Path path = new Path();// drive forward at least 10 feet
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(stopPosition, pathOptions));

		
		//actually tell the robot to run
		runAction( new WaitAction(startDelay) );// wait X seconds, then
		runAction( new PathFollowerAction(path) );
		// assume we are lined up on left or right (not center)
	}
}