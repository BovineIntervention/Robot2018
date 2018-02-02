package org.usfirst.frc.team686.robot.auto.modes;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
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

public class RightSwitchToLeftScaleMode extends AutoModeBase {
	FieldDimensions fieldDimensions;
	Path path;
	Path pathBackup;
	
	
    public RightSwitchToLeftScaleMode() 
    {
    	fieldDimensions = new FieldDimensions();
    }
    
    private void init()
    {
    	
    	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
    	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);

    	
		// get initial position
		Pose switchPose = FieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = initialPose.getPosition();
		double switchHeading = initialPose.getHeading();
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition = switchPosition.sub(backupPosition);
		Pose switchBackupPose = new Pose(switchBackupPosition, Math.toRadians(-30));
		
		// get switch turn position
		double turnPositionX = fieldDimensions.getScaleTurnFromSwitchPositionX();
		Pose turnPoseX = new Pose(turnPositionX, 0, Math.toRadians(90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(turnPoseX, switchBackupPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(turnPositionX, switchPosition.getY() - fieldDimensions.getSwitchTurnOffsetY());
		   
		
		// get scale position
		Pose scalePose = fieldDimensions.getLeftScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get scale turn position
		turnPoseX = new Pose(turnPositionX, 0, Math.toRadians(-90));
		
		intersection = Util.getLineIntersection(turnPoseX, scalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(turnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() + 1); //avoid collision with fence
		
		
		// get scale backup position
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(switchBackupPosition, 	pathOptions));
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleTurnPosition, pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	
		

	}
    
    @Test
    public void test() {
    	
    	init();
    	
    	List<Waypoint> points = path.getPath();
    	Waypoint turnPoint = points.get(1);
    	Vector2d turnPosition = turnPoint.position;
    	System.out.println(turnPosition.getX());
    	System.out.println(turnPosition.getY());
   
    	
    }

    // called by AutoModeExecuter.start() --> AutoModeBase.run()
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("STARTING AUTOMODE: Center Start To Left Scale");

    	init();																
    	
   		runAction( new PathFollowerWithVisionAction( path ) );
   		runAction( new WaitAction(5) ); 
   		//runAction( new ScoreCubeAction() );	    							// score cube
    	
   		runAction( new PathFollowerWithVisionAction( pathBackup ) );

    }
}