package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.CrossFieldOption;
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
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class SideStartToFarSwitchMode extends AutoModeBase {
	
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public SideStartToFarSwitchMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: " + startPosition.name + " to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		PathSegment.Options backOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 24, false);
		PathSegment.Options tightTurnOptions	= new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, 18, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition(); //first assume start is left and goal is right 
		
		// drive straight until we are between the switch and platform
		Vector2d turnPosition = new Vector2d(240, 116);

		// switch corner
		Vector2d switchStopPosition = new Vector2d(178 + Constants.kCenterToFrontBumper/Math.sqrt(2), -71 - Constants.kCenterToFrontBumper/Math.sqrt(2));

		// position that defines the turn around point
		Vector2d turnAroundPosition = new Vector2d(240, -160);

		// distance at which we start checking collision detector
		Vector2d startCollisionPosition = switchStopPosition.add(Vector2d.magnitudeAngle(24.0, -Math.PI/4));
		
		if (startPosition == StartPositionOption.RIGHT_START) {
			turnPosition.setY(-turnPosition.getY());
		    turnAroundPosition.setY(-turnAroundPosition.getY());     
		    startCollisionPosition.setY(-startCollisionPosition.getY());
		    switchStopPosition.setY(-switchStopPosition.getY());
		}

		
		if (crossField == CrossFieldOption.YES)
		{
			Path path = new Path(collisionOptions.getMaxSpeed());
			path.add(new Waypoint(initialPosition, pathOptions));
			path.add(new Waypoint(turnPosition, pathOptions));
			path.add(new Waypoint(turnAroundPosition, backOptions));
			path.add(new Waypoint(startCollisionPosition, tightTurnOptions));
			
			Path collisionPath = new Path();
			collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
			collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
			
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
		else
		{
			// just cross line if everything is on the other side, and we don't want to interfere with a partner
			
			Path path = new Path();	// final velocity of this path will be collisionVelocity required by next path
			path.add(new Waypoint(initialPosition, pathOptions));
			path.add(new Waypoint(turnPosition,   pathOptions));

			runAction( new PathFollowerWithVisionAction(path) );
		}
	}
}