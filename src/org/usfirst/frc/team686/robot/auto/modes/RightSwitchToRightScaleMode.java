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

public class RightSwitchToRightScaleMode extends AutoModeBase {
	FieldDimensions fieldDimensions;
	Path path;
	Path pathBackup;
	
	
    public RightSwitchToRightScaleMode() 
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
		
		// get initial backed up position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d initialPosition = switchPosition.sub(backupPosition);
		
		// get switch backup position
		Vector2d switchBackupPosition = new Vector2d(initialPosition.getX(), initialPosition.getY() - Constants.kCenterToSideBumper - 36); // backup robot more to prevent collision with switch
		
	
		// get scale position
		Pose scalePose = fieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
	
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - 1); //avoid collision with fence
		
		
		// get scale backup position
		Vector2d scaleBackupPosition = scalePosition.sub(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(switchBackupPosition,     pathOptions));
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
    	System.out.println("STARTING AUTOMODE: Right Switch To Right Scale");

    	init();																
    	
   		runAction( new PathFollowerWithVisionAction( path ) );
   		runAction( new WaitAction(5) ); 
   		//runAction( new ScoreCubeAction() );	    							// score cube
    	
   		runAction( new PathFollowerWithVisionAction( pathBackup ) );

    }
}