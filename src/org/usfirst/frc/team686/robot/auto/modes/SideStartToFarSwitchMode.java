package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;
import java.util.Optional;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartPositionOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.CollisionDetectionAction;
import org.usfirst.frc.team686.robot.auto.actions.ElevatorAction;
import org.usfirst.frc.team686.robot.auto.actions.InterruptableAction;
import org.usfirst.frc.team686.robot.auto.actions.OuttakeAction;
import org.usfirst.frc.team686.robot.auto.actions.ParallelAction;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerWithVisionAction;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Util;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class SideStartToFarSwitchMode extends AutoModeBase {
	
	
	StartPositionOption startPosition;
	boolean toRight;
	
	public SideStartToFarSwitchMode (StartPositionOption _startPosition, boolean _toRight)
	{
		startPosition = _startPosition; 
		toRight = _toRight;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: " + startPosition.name + " to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		// first assume start is left and goal is right
		Vector2d initialPosition = startPosition.initialPose.getPosition();  
		
		// drive straight until we are between the switch and platform
		Vector2d turnPosition1 = new Vector2d(FieldDimensions.getScalePlatformFromCenterStartDistX() - Constants.kCenterToSideBumper - 12, initialPosition.getY());

		// turn between switch and platform
		Vector2d turnPosition2 = new Vector2d(FieldDimensions.getScalePlatformFromCenterStartDistX() - Constants.kCenterToSideBumper - 12, FieldDimensions.kSwitchLengthY/2);
		
		// drive to opposite side of field
		Vector2d turnPosition3 = new Vector2d(turnPosition2.getX(), -turnPosition2.getY());
		
		// switch corner
		Vector2d switchStopPosition = new Vector2d(FieldDimensions.kSwitchFromCenterStartDistX + FieldDimensions.kSwitchLengthX - 12, FieldDimensions.kSwitchLengthY/2 - 6);

		// position that defines the turn around point
		Optional<Vector2d> turnAroundPositionOption = Util.getLineIntersection(new Pose(switchStopPosition, -Math.PI/4), new Pose(turnPosition3, -Math.PI/2));
		if (!turnAroundPositionOption.isPresent())
			System.out.println("Error in line intersection calc");
		
		Vector2d turnAroundPosition = turnAroundPositionOption.get();

		// distance at which we 
		Vector2d startCollisionPosition = switchStopPosition.add(Vector2d.magnitudeAngle(24.0, -Math.PI/4));
		
		if (toRight)
		{
			turnPosition1.setY(-turnPosition1.getY());
			turnPosition2.setY(-turnPosition2.getY());
			turnPosition3.setY(-turnPosition3.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
			turnAroundPosition.setY(-turnAroundPosition.getY());
			switchStopPosition.setY(-switchStopPosition.getY());
		}
		
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition1, pathOptions));
		path.add(new Waypoint(turnPosition2, pathOptions));
		path.add(new Waypoint(turnPosition3, pathOptions));
		path.add(new Waypoint(turnAroundPosition, pathOptions));
		path.add(new Waypoint(startCollisionPosition, pathOptions));
		
		Path collisionPath = new Path();	// final velocity of this path will be 0
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));

		System.out.println("SideStartToFarSwitchMode path");
		System.out.println(path.toString());
		System.out.println(collisionPath.toString());
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				//new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(),
				new PathFollowerWithVisionAction(collisionPath))
		})));
		
		runAction( new OuttakeAction() );
		
	}

}
