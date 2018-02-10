package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Optional;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerWithVisionAction;
import org.usfirst.frc.team686.robot.auto.actions.WaitAction;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;

import edu.wpi.first.wpilibj.Timer;

public class TestCenterStartToLeftSwitchMode extends AutoModeBase {
	FieldDimensions fieldDimensions;
	Path pathToSwitch;
	Path pathBackupFromSwitch;
	Path path;
	
	
    public TestCenterStartToLeftSwitchMode() 
    {
    	
    }
    
    private void init()
    {
    	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
    	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);


		// get positions, based on red/blue alliance
		Pose initialPose = fieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		// where to stop to place Power Cube
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX()/5, (switchPosition.getY() + Constants.kCenterToFrontBumper + 2.0)/5);
		
		
		//get where to turn
		Vector2d switchTurnPosition = new Vector2d((switchStopPosition.getX()-fieldDimensions.kSwitchTurnPositionOffsetX)/5, (switchStopPosition.getY() - 36)/5);

		
		//ELEVATOR ACTIONS
		
		
		// where to backup to after scoring gear
		Vector2d backupPosition = new Vector2d(switchStopPosition.getX()/5, (switchStopPosition.getY() + fieldDimensions.kBackupDistY)/5);
		
		path = new Path();
		path.add(new Waypoint(new Vector2d(0.0, 0.0), pathOptions));
		path.add(new Waypoint(new Vector2d(48.0, 24.0), pathOptions));
		
		// define path to peg
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(initialPosition, 	pathOptions));
		pathToSwitch.add(new Waypoint(switchTurnPosition,     visionOptions));
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
    	runAction( new PathFollowerWithVisionAction( path ) );
    	
   		//runAction( new PathFollowerWithVisionAction( pathToSwitch ) );			// drive to switch   
   		//runAction( new WaitAction(5) ); 
   		//runAction( new ScoreCubeAction() );	    							// score cube
    	
   		//runAction( new PathFollowerWithVisionAction( pathBackupFromSwitch ) );	// backup from switch

    }
}