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

public class OtherStartToRightSwitchMode extends AutoModeBase {
	FieldDimensions FieldDimensions;
	Path path;
	Path pathBackup;
	
	
    public OtherStartToRightSwitchMode() 
    {
    	
    }
    
    private void init()
    {
    	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
    	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);


		// get initial position
		Pose initialPose = FieldDimensions.getOtherStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get switch position
		Pose switchPose = FieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition = switchPosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	

	}

    // called by AutoModeExecuter.start() --> AutoModeBase.run()
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("STARTING AUTOMODE: Other Start To Left Switch");

    	init();																
    	
   		runAction( new PathFollowerWithVisionAction( path ) );
   		runAction( new WaitAction(5) ); 
   		//runAction( new ScoreCubeAction() );	    							// score cube
    	
   		runAction( new PathFollowerWithVisionAction( pathBackup ) );

    }
}