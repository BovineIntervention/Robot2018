package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.CrossFieldOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartPositionOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class SideStartToNearSwitchMode extends AutoModeBase {
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public SideStartToNearSwitchMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
 
System.out.println("STARTING AUTOMODE: " + startPosition.name + " to Near Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 36, false);
		PathSegment.Options tightTurnOptions = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 18, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, 18, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition();
		
		Vector2d switchStopPosition = new Vector2d(FieldDimensions.kSwitchFromCenterStartDistX + (FieldDimensions.kSwitchLengthX/2) + 12,
													(FieldDimensions.kSwitchLengthY/2) + Constants.kCenterToFrontBumper);
		
		Vector2d turnPosition = new Vector2d(switchStopPosition.getX() + 12 , switchStopPosition.getY() + 50);
		
		Vector2d startCollisionPosition = new Vector2d(switchStopPosition.getX() + 3, switchStopPosition.getY() + 12);
		
		if (startPosition == StartPositionOption.RIGHT_START) {
			turnPosition.setY(-turnPosition.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
			switchStopPosition.setY(-switchStopPosition.getY());
		}

		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(startCollisionPosition, tightTurnOptions));
		
		Path collisionPath = new Path();	// final velocity of this path will be 0
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
		
		System.out.println("SideStartToNearSwitchMode path");
		System.out.println(path.toString());
		System.out.println(collisionPath.toString());
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(),
				new PathFollowerWithVisionAction(collisionPath))
		})));
		runAction( new OuttakeAction() );
	}
	
}
