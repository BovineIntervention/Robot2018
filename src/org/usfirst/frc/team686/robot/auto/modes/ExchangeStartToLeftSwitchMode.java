package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Optional;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerWithVisionAction;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;

import edu.wpi.first.wpilibj.Timer;

public class ExchangeStartToLeftSwitchMode extends AutoModeBase {
	FieldDimensions fieldDimensions;
	Path pathToSwitch;
	Path pathBackupFromSwitch;
	
	
    public ExchangeStartToLeftSwitchMode() 
    {
    	
    }
    
    private void init()
    {
    	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
    	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);


		// get positions, based on red/blue alliance
		Pose initialPose = fieldDimensions.getExchangeStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		// where to stop to place Power Cube
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() + Constants.kCenterToFrontBumper + 2.0);


		
		//ELEVATOR ACTIONS
		
		
		// where to backup to after scoring gear
		Vector2d backupPosition = new Vector2d(switchStopPosition.getX(), switchStopPosition.getY() + fieldDimensions.kBackupDistY);
		
		
		// define path to peg
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(initialPosition, 	pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		
		// backup away from peg, turn front towards boiler
		pathBackupFromSwitch = new Path();
		pathBackupFromSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		pathBackupFromSwitch.add(new Waypoint(backupPosition, 		pathOptions));
		pathBackupFromSwitch.setReverseDirection();	

	}

    // called by AutoModeExecuter.start() --> AutoModeBase.run()
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("STARTING AUTOMODE: Center Start To Left Switch");

   	 
    	init();																
    	
   		//runAction( new PathFollowerWithVisionAction( pathToSwitch ) );			// drive to switch   
    	//runAction( new ScoreCubeAction() );	    							// score cube
    	
   		//runAction( new PathFollowerWithVisionAction( pathBackupFromSwitch ) );	// backup from switch

    }
}