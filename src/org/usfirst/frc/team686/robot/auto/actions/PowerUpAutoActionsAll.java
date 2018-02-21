package org.usfirst.frc.team686.robot.auto.actions;

import java.util.Optional;

//import org.junit.Test;
import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.modes.FieldDimensions;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class PowerUpAutoActionsAll {
	
	public PowerUpAutoActionsAll(){}
	
	static Pose initialPose;
	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);

	public static Pose getInitialPose() { return initialPose; }
	/*
	public SeriesAction LeftStartToLeftSwitchEdgeAction(){
System.out.println("RUNNING: LeftStartToLeftSwitchEdgeAction");
		double isRight = -1;
		
		Path path;
		Path pathAlign;
		Path pathToSwitch;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getLeftStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		
		
		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();, ini
		
		Vector2d switchTurnPosition = new Vector2d(switchPosition.getX() - FieldDimensions.getSwitchTurnPositionX(), switchPosition.getY() - (FieldDimensions.getSwitchTurnOffsetY()*isRight) - (Constants.kCenterToFrontBumper*isRight));

		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 
		
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		path.add(new Waypoint(switchTurnPosition, pathOptions));
		System.out.println(switchTurnPosition.toString());
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(switchStopPosition, pathOptions));
		pathAlign.add(new Waypoint(switchBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(switchBackupPosition, pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
System.out.println("SWITCH STOP POSITION: " + switchStopPosition.toString());
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
System.out.println("BACKUP POSITION: " + switchBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction(pathToSwitch) );
		//actions.add( new ElevatorAction(true) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction LeftStartToRightSwitchEdgeAction(){
		
		double isRight = 1;
		
		Path path;
		Path pathAlign;
		Path pathToSwitch;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getLeftStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();

		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		

		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-65*isRight));
		Pose centerPose = FieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(FieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - FieldDimensions.getSwitchTurnOffsetY()*isRight);
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch

		
		intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		

		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 

		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		path.add(new Waypoint(crossPosition, pathOptions));
System.out.println("CROSS POSITION: " + crossPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
System.out.println("SWITCH TURN POSITION: " + switchTurnPosition.toString());
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(switchStopPosition, pathOptions));
		pathAlign.add(new Waypoint(switchBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(switchBackupPosition, pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
System.out.println("SWITCH STOP POSITION" + switchStopPosition.toString());
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
System.out.println("SWITCH BACKUP POSITION: " + switchBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction( pathToSwitch ) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(true) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction RightStartToLeftSwitchEdgeAction(){
		
		double isRight = -1;
		
		Path path;
		Path pathAlign;
		Path pathToSwitch;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getRightStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();

		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		

		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-65*isRight));
		Pose centerPose = FieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(FieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - FieldDimensions.getSwitchTurnOffsetY()*isRight);
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch

		
		intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		

		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 

		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		path.add(new Waypoint(crossPosition, pathOptions));
System.out.println("CROSS POSITION: " + crossPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
System.out.println("SWITCH TURN POSITION: " + switchTurnPosition.toString());
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(switchStopPosition, pathOptions));
		pathAlign.add(new Waypoint(switchBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(switchBackupPosition, pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
System.out.println("SWITCH STOP POSITION" + switchStopPosition.toString());
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
System.out.println("SWITCH BACKUP POSITION: " + switchBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction(pathAlign) );
		actions.add( new PathFollowerWithVisionAction(pathToSwitch) );
		//actions.add( new ElevatorAction(true) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction RightStartToRightSwitchEdgeAction(){
		
System.out.println("RUNNING: RightStartToRightSwitchEdgeAction");
		
		double isRight = 1;
		
		Path path;
		Path pathAlign;
		Path pathToSwitch;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getRightStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		
		
		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - FieldDimensions.getSwitchTurnOffsetY()*isRight);
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch

		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 
		
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		path.add(new Waypoint(switchTurnPosition, pathOptions));
		System.out.println(switchTurnPosition.toString());
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(switchStopPosition, pathOptions));
		pathAlign.add(new Waypoint(switchBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(switchBackupPosition, pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
System.out.println("SWITCH STOP POSITION: " + switchStopPosition.toString());
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
System.out.println("BACKUP POSITION: " + switchBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction( pathToSwitch ) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(true) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction CenterStartToLeftSwitchEdgeAction(){
		
System.out.println("RUNNING: CenterStartToLeftSwitchEdgeAction");

		double isRight = -1;
				
		Path path;
		Path pathAlign;
		Path pathToSwitch;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());


		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - FieldDimensions.getSwitchTurnOffsetY()*isRight);
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch

		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		

		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		System.out.println(initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
		System.out.println(initialForwardPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		System.out.println(switchTurnPosition.toString());
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(switchStopPosition, pathOptions));
		pathAlign.add(new Waypoint(switchBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(switchBackupPosition, pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
		System.out.println(switchStopPosition.toString());
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
		System.out.println(switchBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction(pathAlign) );
		actions.add( new PathFollowerWithVisionAction(pathToSwitch) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(true) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
	}
	
	public SeriesAction CenterStartToRightSwitchEdgeAction(){
		
		double isRight = 1;
		
		Path path;
		Path pathAlign;
		Path pathToSwitch;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());


		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - FieldDimensions.getSwitchTurnOffsetY()*isRight);
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch

		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		

		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		System.out.println(initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
		System.out.println(initialForwardPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		System.out.println(switchTurnPosition.toString());
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(switchStopPosition, pathOptions));
		pathAlign.add(new Waypoint(switchBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToSwitch = new Path();
		pathToSwitch.add(new Waypoint(switchBackupPosition, pathOptions));
		pathToSwitch.add(new Waypoint(switchStopPosition, pathOptions));
		
		pathBackup = new Path();
		//pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
		System.out.println(switchStopPosition.toString());
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
		System.out.println(switchBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add(new PathFollowerWithVisionAction( pathAlign ));
		actions.add(new PathFollowerWithVisionAction(pathToSwitch));
		//actions.add( new ElevatorAction(true) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
				
	}

	public SeriesAction LeftStartToLeftScaleEdgeAction(){
		
		double isRight = -1;
		
		Path path;
		Path pathAlign;
		Path pathToScale;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getLeftStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		// get initial forward position
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		
		
		// get scale position
		Pose scalePose = FieldDimensions.getLeftScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY() - (FieldDimensions.getScaleTurnOffsetY()*isRight), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = FieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d scaleTurnPositionY = new Vector2d(switchPosition.getX(), scalePosition.getY() - FieldDimensions.getScaleTurnOffsetY()*isRight);
		Pose scaleTurnPoseY = new Pose(scaleTurnPositionY, scaleHeading); // shift intersection line from switch pose 36" from the switch


		Optional<Vector2d> intersection = Util.getLineIntersection(scaleTurnPoseX, scaleTurnPoseY);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() + FieldDimensions.getScaleTurnOffsetY());
		
		scaleTurnPositionY = new Vector2d(scalePosition.getX(), scalePosition.getY() - FieldDimensions.getScaleTurnOffsetY()*isRight);
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition;
		if(isRight == 1)
			scaleBackupPosition = scaleStopPosition.sub(backupPosition); 
		else
			scaleBackupPosition = scaleStopPosition.add(backupPosition); 
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		System.out.println(initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
		System.out.println(initialForwardPosition.toString());
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
System.out.println(scaleTurnPosition.toString());
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(scaleStopPosition, pathOptions));
		pathAlign.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToScale = new Path();
		pathToScale.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
System.out.println(scaleStopPosition.toString());
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
System.out.println(scaleBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction(pathAlign) );
		actions.add( new PathFollowerWithVisionAction(pathToScale) );
		//actions.add( new ElevatorAction(false));
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction LeftStartToRightScaleEdgeAction(){
		
		double isRight = 1;
		
		Path path;
		Path pathAlign;
		Path pathToScale;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getLeftStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();

		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		

		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-65*isRight));
		Pose centerPose = FieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(FieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - (FieldDimensions.getSwitchTurnOffsetY()*isRight) - (Constants.kCenterToFrontBumper*isRight));
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch
		
		intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		
		
		// get scale pose
		Pose scalePose = FieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		
		// get pose to stop in front of scale
		Vector2d scaleStopTurnPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - Constants.kCenterToSideBumper*isRight - FieldDimensions.getScaleTurnOffsetY()*isRight);
		
		// get switch stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition;
		if(isRight == 1)
			scaleBackupPosition = scaleStopPosition.sub(backupPosition); 
		else
			scaleBackupPosition = scaleStopPosition.add(backupPosition); 

		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		path.add(new Waypoint(crossPosition, pathOptions));
System.out.println("CROSS POSITION: " + crossPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
System.out.println("SWITCH TURN POSITION: " + switchTurnPosition.toString());
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(scaleStopPosition, pathOptions));
		pathAlign.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToScale = new Path();
		pathToScale.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
System.out.println("SWITCH STOP POSITION" + scaleStopPosition.toString());
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
System.out.println("SWITCH BACKUP POSITION: " + scaleBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction(pathAlign) );
		actions.add( new PathFollowerWithVisionAction(pathToScale) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(false));
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
	}
	
	public SeriesAction RightStartToLeftScaleEdgeAction(){
		
		double isRight = -1;
		
		Path path;
		Path pathAlign;
		Path pathToScale;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getRightStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();

		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		

		// get switch position
		Pose switchPose;
		if(isRight == 1)
			switchPose = FieldDimensions.getRightSwitchPose();
		else
			switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-65*isRight));
		Pose centerPose = FieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(FieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - (FieldDimensions.getSwitchTurnOffsetY()*isRight) - (Constants.kCenterToFrontBumper*isRight));
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch
		
		intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);
		
		
		// get scale pose
		Pose scalePose;
		if(isRight == 1)
			scalePose = FieldDimensions.getRightScalePose();
		else
			scalePose = FieldDimensions.getLeftScalePose();
		
		Vector2d scalePosition = scalePose.getPosition();
		
		// get pose to stop in front of scale
		Vector2d scaleStopTurnPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - Constants.kCenterToSideBumper*isRight - FieldDimensions.getScaleTurnOffsetY()*isRight);
		
		// get switch stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition;
		if(isRight == 1)
			scaleBackupPosition = scaleStopPosition.sub(backupPosition); 
		else
			scaleBackupPosition = scaleStopPosition.add(backupPosition); 

		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		path.add(new Waypoint(crossPosition, pathOptions));
System.out.println("CROSS POSITION: " + crossPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
System.out.println("SWITCH TURN POSITION: " + switchTurnPosition.toString());
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(scaleStopPosition, pathOptions));
		pathAlign.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToScale = new Path();
		pathToScale.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
System.out.println("SWITCH STOP POSITION" + scaleStopPosition.toString());
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
System.out.println("SWITCH BACKUP POSITION: " + scaleBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction( pathToScale ) );
		//actions.add( new ElevatorAction(false));
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
	}
	
	public SeriesAction RightStartToRightScaleEdgeAction(){
		
		double isRight = 1;
		
		Path path;
		Path pathAlign;
		Path pathToScale;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getRightStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		
		// get scale position
		Pose scalePose = FieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = FieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY() - (FieldDimensions.getScaleTurnOffsetY()*isRight), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = FieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d scaleTurnPositionY = new Vector2d(switchPosition.getX(), scalePosition.getY() - FieldDimensions.getScaleTurnOffsetY()*isRight);
		Pose scaleTurnPoseY = new Pose(scaleTurnPositionY, scaleHeading); // shift intersection line from switch pose 36" from the switch


		Optional<Vector2d> intersection = Util.getLineIntersection(scaleTurnPoseX, scaleTurnPoseY);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() + FieldDimensions.getScaleTurnOffsetY());
		
		scaleTurnPositionY = new Vector2d(scalePosition.getX(), scalePosition.getY() - FieldDimensions.getScaleTurnOffsetY()*isRight);
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition;
		if(isRight == 1)
			scaleBackupPosition = scaleStopPosition.sub(backupPosition); 
		else
			scaleBackupPosition = scaleStopPosition.add(backupPosition); 
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		System.out.println(initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
		System.out.println(initialForwardPosition.toString());
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
System.out.println(scaleTurnPosition.toString());
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(scaleStopPosition, pathOptions));
		pathAlign.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToScale = new Path();
		pathToScale.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
System.out.println(scaleStopPosition.toString());
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
System.out.println(scaleBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction(pathToScale) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(false));
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction CenterStartToLeftScaleEdgeAction(){
		
		double isRight = -1;
		
		Path path;
		Path pathAlign;
		Path pathToScale;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();

		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());


		// get switch position
		Pose switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - (FieldDimensions.getSwitchTurnOffsetY()*isRight) - (Constants.kCenterToFrontBumper*isRight));
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, switchHeading); // shift intersection line from switch pose 36" from the switch
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);

		
		// get scale pose
		Pose scalePose = FieldDimensions.getLeftScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition;
		if(isRight == 1)
			scaleBackupPosition = scaleStopPosition.sub(backupPosition); 
		else
			scaleBackupPosition = scaleStopPosition.add(backupPosition); 
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		System.out.println(initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
		System.out.println(initialForwardPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		System.out.println(switchTurnPosition.toString());
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(scaleStopPosition, pathOptions));
		pathAlign.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToScale = new Path();
		pathToScale.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		System.out.println(scaleStopPosition.toString());
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		System.out.println(scaleBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction( pathToScale ) );
		//actions.add( new ElevatorAction(false));
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction CenterStartToRightScaleEdgeAction(){	
		
		double isRight = 1;
		
		Path path;
		Path pathAlign;
		Path pathToScale;
		Path pathBackup;
		
		// get initial position
		initialPose = FieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();

		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());


		// get switch position
		Pose switchPose = FieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		// get scale pose
		Pose scalePose = FieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		

		// get turn position
		double switchTurnPositionX = FieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90*isRight));
		
		Vector2d switchTurnPositionY = new Vector2d(switchPosition.getX(), switchPosition.getY() - (FieldDimensions.getSwitchTurnOffsetY()*isRight) - (Constants.kCenterToFrontBumper*isRight));
		Pose switchTurnPoseY = new Pose(switchTurnPositionY, scaleHeading); // shift intersection line from switch pose 36" from the switch
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchTurnPoseY);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + FieldDimensions.getSwitchTurnOffsetY()*isRight);

		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition;
		if(isRight == 1)
			scaleBackupPosition = scaleStopPosition.sub(backupPosition); 
		else
			scaleBackupPosition = scaleStopPosition.add(backupPosition); 
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		System.out.println(initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
		System.out.println(initialForwardPosition.toString());
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		System.out.println(switchTurnPosition.toString());
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathAlign = new Path();
		pathAlign.add(new Waypoint(scaleStopPosition, pathOptions));
		pathAlign.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToScale = new Path();
		pathToScale.add(new Waypoint(scaleBackupPosition, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		System.out.println(scaleStopPosition.toString());
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		System.out.println(scaleBackupPosition.toString());
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction(pathAlign) );
		actions.add( new PathFollowerWithVisionAction(pathToScale) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(false));
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction LeftSwitchEdgeToLeftPowerCubeAction() {
		double isRight = -1;
		
		Path path;
		Path pathBackup;
		
		// get switch position
		Pose switchPose = FieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - (Constants.kCenterToFrontBumper*isRight) - (FieldDimensions.getCollisionAvoidanceOffsetY()*isRight)); //avoid collision with fence
		
		// get backup position
		Vector2d backupPosition = FieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition;
		if(isRight == 1)
			switchBackupPosition = switchStopPosition.sub(backupPosition); 
		else
			switchBackupPosition = switchStopPosition.add(backupPosition); 
		
		return null;
	}
	
	public SeriesAction LeftSwitchEdgeToRightPowerCubeAction() {
		return null;
	}
	
	public SeriesAction RightSwitchEdgeToLeftPowerCubeAction() {
		return null;
	}
	
	public SeriesAction RightSwitchEdgeToRightPowerCubeAction() {
		return null;
	}
	
	public SeriesAction LeftScaleEdgeToLeftPowerCubeAction() {
		return null;
	}
	
	public SeriesAction LeftScaleEdgeToRightPowerCubeAction(){
		return null;
	}
	
	public SeriesAction RightScaleEdgeToLeftPowerCubeAction() {
		return null;
	}
	
	public SeriesAction RightScaleEdgeToRightPowerCubeAction() {
		return null;
	}
	
	
	public SeriesAction LeftPowerCubeToLeftSwitchAction() {
		return null;
	}
	
	public SeriesAction LeftPowerCubeToLeftScaleAction(){
		return null;
	}
	
	public SeriesAction RightPowerCubeToRightSwitchAction(){
		return null;
	}
	
	public SeriesAction RightPowerCubeToRightScaleAction(){
		return null;
	}
	*/
}
