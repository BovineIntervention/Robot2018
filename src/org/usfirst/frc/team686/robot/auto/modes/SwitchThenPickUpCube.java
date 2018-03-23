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

public class SwitchThenPickUpCube extends AutoModeBase {
	
	double startDelay;
	
	public SwitchThenPickUpCube (StartDelayOption _startDelayOption)
	{
	   	startDelay = _startDelayOption.delaySec;
	}
	

	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: SwitchThenPickUpCube");
	
		
		// TODO: fill out
			
		//getting ready, setting variables
		
		//actually tell the robot to run
		//runAction( new PathFollowerAction(path) );
		//runAction( new PathFollowerAction(path) );
	}
}