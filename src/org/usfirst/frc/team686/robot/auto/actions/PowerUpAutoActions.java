package org.usfirst.frc.team686.robot.auto.actions;

import java.util.Optional;

//import org.junit.Test;
import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.modes.FieldDimensions;
import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class PowerUpAutoActions {
	
	public PowerUpAutoActions(){}
	
	PathSegment.Options pathOptions   = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
	PathSegment.Options visionOptions = new PathSegment.Options(Constants.kVisionMaxVel,        Constants.kVisionMaxAccel,        Constants.kPathFollowingLookahead, true);

	public enum InitialStateEnum {
		
		CENTER(FieldDimensions.getCenterStartPose()),
		LEFT(FieldDimensions.getLeftStartPose()),
		RIGHT(FieldDimensions.getRightStartPose());
		
		public Pose pose;
	
		InitialStateEnum( Pose _pose ){
			this.pose = _pose;
		}
		
	}
	
	public enum TargetEnum {
		
		SWITCH(FieldDimensions.getLeftSwitchPose(), FieldDimensions.getRightSwitchPose(), FieldDimensions.getSwitchTurnOffsetX(), FieldDimensions.getSwitchTurnOffsetY()),
		SCALE(FieldDimensions.getLeftScalePose(), FieldDimensions.getRightScalePose(), FieldDimensions.getScaleTurnOffsetX(), FieldDimensions.getScaleTurnOffsetY());
		//POWER_CUBE(FieldDimensions.getPowerCubeZone(), FieldDimensions.getPoseCubeZone());
		
		public Pose leftPose;
		public Pose rightPose;
		public double turnOffsetX;
		public double turnOffsetY;
		
		TargetEnum( Pose _leftPose, Pose _rightPose, double _turnOffsetX, double _turnOffsetY ){
			
			this.leftPose = _leftPose;
			this.rightPose = _rightPose;
			this.turnOffsetX = _turnOffsetX;
			this.turnOffsetY = _turnOffsetY;
			
		}
		
	}
	

	private boolean isRight;
	
	private TargetEnum target;
	private InitialStateEnum initialState;
	private Pose initialPose;
	
	private SeriesAction actions;
	

	public Pose getInitialPose() { return initialPose; }
	
	public void setInitialState(InitialStateEnum _initialState){
		initialState = _initialState;
		initialPose = _initialState.pose;
	}
	
	public void setTarget(TargetEnum _target){ target = _target; }
	
	public void isRight(boolean _isRight){ isRight = _isRight; }
	
	
	public SeriesAction getActions(boolean _backup){
		
		Path path;
		Path pathAlign;
		Path pathToTarget;
		Path pathBackup;
		
		if (initialPose == null)
			System.out.println("DIDN'T SET initialPose BEFORE CALLING AutoActions.getActions()\n");
		if (target == null)
			System.out.println("DIDN'T SET target BEFORE CALLING AutoActions.getActions()\n");
		
		
		// get initial position
		Vector2d initialPosition = initialPose.getPosition();
		double initialHeading = initialPose.getHeading();
		
		
		// get forward position from start
		Vector2d initialForwardPosition = new Vector2d(initialPosition.getX() + Constants.kCenterToCornerBumper, initialPosition.getY());
		
		
		// get target position
		Pose targetPose = target.leftPose;
		if(isRight)
			targetPose = target.rightPose;
		Vector2d targetPosition = targetPose.getPosition();
		double targetHeading = targetPose.getHeading();
		
		boolean cross = false;
		if((initialState == InitialStateEnum.LEFT && isRight) || ((initialState == InitialStateEnum.RIGHT && !isRight)))
			cross = true;
		
		
		// get transition pose for crossing from left to right side
		
		Pose crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(-65));
		if(isRight)
			crossPose = new Pose(initialPosition.getX(), initialPose.getY(), Math.toRadians(65));
		Pose centerPose = FieldDimensions.getCenterStartPose();
		
		Optional<Vector2d> intersection = Util.getLineIntersection(crossPose, centerPose);
		Vector2d crossPosition;
		if (intersection.isPresent())
			crossPosition = intersection.get();
		else
			crossPosition = new Vector2d(FieldDimensions.getPowerCubeZoneFromCenterStartDistX() - Constants.kCenterToSideBumper, 0);
	
		
		Vector2d crossPosition1 = new Vector2d(initialPosition.getX() + FieldDimensions.getCrossOffsetX(), initialPosition.getY() + FieldDimensions.getCrossOffsetY());
		if(isRight)
			crossPosition1 = new Vector2d(initialPosition.getX() + FieldDimensions.getCrossOffsetX(), initialPosition.getY() - FieldDimensions.getCrossOffsetY());
		Vector2d crossPosition2 = new Vector2d(crossPosition1.getX(), -crossPosition.getY());

		
		// get turn position
		double turnOffsetX = target.turnOffsetX;
		Pose turnPoseX = new Pose(turnOffsetX, 0, Math.toRadians(-90));
		if(isRight)
			turnPoseX = new Pose(turnOffsetX, 0, Math.toRadians(90));
				
		double turnOffsetY = target.turnOffsetY;
		Pose turnPoseY = new Pose(targetPosition.getX(), targetPosition.getY() - target.turnOffsetY);
		if(isRight)
			 turnPoseY = new Pose(targetPosition.getX(), targetPosition.getY() + target.turnOffsetY);
		
		intersection = Util.getLineIntersection(turnPoseX, turnPoseY);
		Vector2d turnPosition;
		if(intersection.isPresent())
			turnPosition = intersection.get();
		else
			turnPosition = new Vector2d(turnOffsetX, turnOffsetY);
		
		
		// get backup position
		Vector2d backupPosition = targetPosition.add(FieldDimensions.getBackupPosition());
		if(isRight)
			backupPosition = targetPosition.sub(FieldDimensions.getBackupPosition());
		
		
		//add positions to paths
		path = new Path();
		path.add(new Waypoint(initialPosition, 	pathOptions));
System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(initialForwardPosition, pathOptions));
System.out.println("INITIAL FORWARD POSITION: " + initialForwardPosition.toString());
		if(cross){
			path.add(new Waypoint(crossPosition1, pathOptions));
	System.out.println("CROSS POSITION 1: " + crossPosition.toString());
			path.add(new Waypoint(crossPosition2, pathOptions));
	System.out.println("CROSS POSITION 2: " + crossPosition.toString());
		}
		path.add(new Waypoint(turnPosition,     pathOptions));
System.out.println("TURN POSITION: " + turnPosition.toString());
		path.add(new Waypoint(targetPosition, 	pathOptions));
System.out.println("TARGET POSITION: " + turnPosition.toString());	

		pathAlign = new Path();
		pathAlign.add(new Waypoint(targetPosition, pathOptions));
		if(_backup)
			pathAlign.add(new Waypoint(backupPosition, pathOptions));
		pathAlign.setReverseDirection();
		
		pathToTarget = new Path();
		if(_backup)
			pathToTarget.add(new Waypoint(backupPosition, pathOptions));
		pathToTarget.add(new Waypoint(targetPosition, pathOptions));
		
		pathBackup = new Path();
		pathBackup.add(new Waypoint(targetPosition, pathOptions));
System.out.println("STOP POSITION" + targetPosition.toString());
		if(_backup){
			pathBackup.add(new Waypoint(backupPosition, 		pathOptions));
			System.out.println("BACKUP POSITION: " + backupPosition.toString());
		}
		pathBackup.setReverseDirection();	
		
		SeriesAction actions = new SeriesAction();
		actions.add( new PathFollowerWithVisionAction( path ) );
		actions.add( new PathFollowerWithVisionAction( pathAlign ) );
		actions.add( new PathFollowerWithVisionAction( pathToTarget ) );
		//actions.add( new PointTurnAction(90) );
		//actions.add( new ElevatorAction(ElevatorArmBarStateEnum.SWITCH) );
		actions.add( new OuttakeAction() );
		actions.add( new PathFollowerWithVisionAction( pathBackup ) );
		
		return actions;
	}
	

}
