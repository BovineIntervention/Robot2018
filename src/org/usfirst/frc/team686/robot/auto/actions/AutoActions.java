package org.usfirst.frc.team686.robot.auto.actions;

import java.util.Optional;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.modes.FieldDimensions;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class AutoActions {
	
	FieldDimensions fieldDimensions;
	
	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);

	
	public SeriesAction LeftStartToLeftSwitchEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = fieldDimensions.getExchangeStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get switch position
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get turn position
		double switchTurnPositionX = fieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(-90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() + 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
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
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction LeftStartToRightSwitchEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = fieldDimensions.getExchangeStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(55));
		Pose centerPose = fieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(fieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		   
		
		// get switch position
		Pose switchPose = fieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get turn position
		double switchTurnPositionX = fieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90));
		
		intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d switchBackupPosition = switchPosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(crossPosition, pathOptions));
		path.add(new Waypoint(switchTurnPosition,     pathOptions));
		path.add(new Waypoint(switchStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(switchStopPosition, pathOptions));
		pathBackup.add(new Waypoint(switchBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction RightStartToLeftSwitchEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = fieldDimensions.getOtherStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-55));
		Pose centerPose = fieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(fieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		   
		
		// get switch position
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get turn position
		double switchTurnPositionX = fieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(-90));
		
		intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() + 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
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
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction RightStartToRightSwitchEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = FieldDimensions.getOtherStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get scale position
		Pose scalePose = fieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = fieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY(), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = fieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(scaleTurnPoseX, shiftedScalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() - fieldDimensions.getScaleTurnOffsetY());
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction CenterStartToLeftSwitchEdgeAction(){

		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = fieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get switch position
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		// get turn position
		double switchTurnPositionX = fieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(-90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() + 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
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
		
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
	}
	
	public SeriesAction CenterStartToRightSwitchEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = fieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get switch position
		Pose switchPose = fieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get turn position
		double switchTurnPositionX = fieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
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
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
				
	}
	
	public SeriesAction LeftStartToLeftScaleEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = FieldDimensions.getExchangeStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get scale position
		Pose scalePose = fieldDimensions.getLeftScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY(), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = fieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(-90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(scaleTurnPoseX, shiftedScalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() + fieldDimensions.getScaleTurnOffsetY());
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() + 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction LeftStartToRightScaleEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = FieldDimensions.getExchangeStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(55));
		Pose centerPose = fieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(fieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		   
		
		// get scale position
		Pose scalePose = fieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = fieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY(), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = fieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(90));
		
		intersection = Util.getLineIntersection(scaleTurnPoseX, shiftedScalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() - fieldDimensions.getScaleTurnOffsetY());
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
	}
	
	public SeriesAction RightStartToLeftScaleEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = FieldDimensions.getOtherStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get transition pose for crossing from left to right side
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-55));
		Pose centerPose = fieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(fieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
		
		// get scale position
		Pose scalePose = fieldDimensions.getLeftScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY(), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = fieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(-90));
		
		intersection = Util.getLineIntersection(scaleTurnPoseX, shiftedScalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() + fieldDimensions.getScaleTurnOffsetY());
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() + 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();		
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction RightStartToRightScaleEdgeAction(){
		
		Path path;
		Path pathBackup;
		
		// get initial position
		Pose initialPose = fieldDimensions.getOtherStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get switch position
		Pose switchPose = fieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		double switchHeading = switchPose.getHeading();
		
		
		// get turn position
		double switchTurnPositionX = fieldDimensions.getSwitchTurnPositionX();
		Pose switchTurnPoseX = new Pose(switchTurnPositionX, 0, Math.toRadians(90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(switchTurnPoseX, switchPose);
		Vector2d switchTurnPosition;
		if (intersection.isPresent())
			switchTurnPosition = intersection.get();
		else
			switchTurnPosition = new Vector2d(switchTurnPositionX, switchPosition.getY() + fieldDimensions.getSwitchTurnOffsetY());
		
		
		// get switch stop position
		Vector2d switchStopPosition = new Vector2d(switchPosition.getX(), switchPosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
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
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction CenterStartToLeftScaleEdgeAction(){
		
		Path path;
		Path pathBackup;
    	
		// get initial position
		Pose initialPose = FieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get scale position
		Pose scalePose = fieldDimensions.getLeftScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = fieldDimensions.getLeftSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY(), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = fieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(-90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(scaleTurnPoseX, shiftedScalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() + fieldDimensions.getScaleTurnOffsetY());
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() + 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
	public SeriesAction CenterStartToRightScaleEdgeAction(){
		
		Path path;
		Path pathBackup;
    	
		// get initial position
		Pose initialPose = FieldDimensions.getCenterStartPose();
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		   
		
		// get scale position
		Pose scalePose = fieldDimensions.getRightScalePose();
		Vector2d scalePosition = scalePose.getPosition();
		double scaleHeading = scalePose.getHeading();
		
		// get switch position
		Pose switchPose = fieldDimensions.getRightSwitchPose();
		Vector2d switchPosition = switchPose.getPosition();
		
		
		Pose shiftedScalePose = new Pose(switchPosition.getX(), scalePosition.getY(), scaleHeading); // for finding intersection
		
		// get turn position
		double scaleTurnPositionX = fieldDimensions.getScaleTurnPositionX();
		Pose scaleTurnPoseX = new Pose(scaleTurnPositionX, 0, Math.toRadians(90));
		
		Optional<Vector2d> intersection = Util.getLineIntersection(scaleTurnPoseX, shiftedScalePose);
		Vector2d scaleTurnPosition;
		if (intersection.isPresent())
			scaleTurnPosition = intersection.get();
		else
			scaleTurnPosition = new Vector2d(scaleTurnPositionX, scalePosition.getY() - fieldDimensions.getScaleTurnOffsetY());
		
		
		// get scale stop position
		Vector2d scaleStopPosition = new Vector2d(scalePosition.getX(), scalePosition.getY() - 1); //avoid collision with fence
		
		
		// get backup position
		Vector2d backupPosition = fieldDimensions.getBackupPosition();
		Vector2d scaleBackupPosition = scalePosition.add(backupPosition);
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
		path.add(new Waypoint(scaleTurnPosition,     pathOptions));
		path.add(new Waypoint(scaleStopPosition, 	pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(scaleStopPosition, pathOptions));
		pathBackup.add(new Waypoint(scaleBackupPosition, 		pathOptions));
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new WaitAction(5) );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
		
	}
	
}
