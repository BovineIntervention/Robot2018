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
	char switchSide, scaleSide;
	
	public SideStartToFarSwitchMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: " + startPosition.name + " to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		// first assume start is left and goal is right
		Vector2d initialPosition = startPosition.initialPose.getPosition();  
		
		// drive straight until we are between the switch and platform
		Vector2d turnPosition = new Vector2d(230, 116);

		// switch corner
		Vector2d switchStopPosition = new Vector2d(190 + Constants.kCenterToFrontBumper/Math.sqrt(2), -71 - Constants.kCenterToFrontBumper/Math.sqrt(2));

		// position that defines the turn around point
		Vector2d turnAroundPosition = new Vector2d(230, -116);

		// distance at which we start checking collision detector
		Vector2d startCollisionPosition = switchStopPosition.add(Vector2d.magnitudeAngle(12.0, -Math.PI/4));
		
		if (switchSide == 'R') {
			turnPosition.setY(-turnPosition.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
			turnAroundPosition.setY(-turnAroundPosition.getY());
			switchStopPosition.setY(-switchStopPosition.getY());
		}
		
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
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
