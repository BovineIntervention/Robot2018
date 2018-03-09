package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartDelayOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;

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
		
		// wait X seconds, then
		// drive forward at least 10 feet
		// assume we are lined up on left or right (not center)
	}
}