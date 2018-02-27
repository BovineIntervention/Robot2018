package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class ScaleSecondCubeMode  extends AutoModeBase {

	Vector2d startPosition;
	char switchSide;
	char scaleSide;
	
	public ScaleSecondCubeMode(Vector2d _currentPosition, char _switchSide, char _scaleSide)
	{
		startPosition = _currentPosition;
		switchSide = _switchSide;
		scaleSide = _scaleSide;
	}
	
	protected void routine() throws AutoModeEndedException
	{
//		// define positions, angles
//		Vector2d centerOfSwitch = new Vector2d(168, 0);
//		double turnAroundAngleDeg = startPosition.angle(centerOfSwitch) * Vector2d.radiansToDegrees;
//		
//		Vector2d slowDownPosition = 	 new Vector2d(224 + Constants.kCenterToFrontBumper, 70);
//		Vector2d closeIntakePosition = 	 new Vector2d(212 + Constants.kCenterToFrontBumper, 70);
//		Vector2d stopPosition = 		 new Vector2d(196 + Constants.kCenterToFrontBumper, 70);
//
//		if (switchSide == 'R') {
//			turnAroundAngleDeg = -turnAroundAngleDeg;
//			closeIntakePosition.setY(-closeIntakePosition.getY());
//			stopPosition.setY(-stopPosition.getY());
//		}
//		
//
//		// define paths
//		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
//		PathSegment.Options slowDownOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
//		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
//		
//		Path toCubePath = new Path(slowDownOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
//		toCubePath.add(new Waypoint(startPosition, pathOptions));
//		toCubePath.add(new Waypoint(slowDownPosition, pathOptions));
//
//		Path intakePath = new Path(collisionOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
//		intakePath.add(new Waypoint(slowDownPosition, slowDownOptions));
//		intakePath.add(new Waypoint(closeIntakePosition, slowDownOptions));
//		
//		Path collisionPath = new Path();							// final velocity of this path will be 0
//		collisionPath.add(new Waypoint(startPosition, pathOptions));
//		collisionPath.add(new Waypoint(closeIntakePosition, pathOptions));
//		
//		
//		// display paths for debug
//		System.out.println("SameSideSwitchSecondCubeMode path");
//		System.out.println(toCubePath.toString());
//		System.out.println(intakePath.toString());
//		System.out.println(collisionPath.toString());
//
//
//		// run sequence
//		runAction( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );		// drop elevator (if not already done)
//		runAction( new PointTurnAction(turnAroundAngleDeg) );					// turn towards cube at end of switch
//		runAction( new ParallelAction(Arrays.asList(new Action[] {
//						new IntakeStartAction(),								// turn on intake
//						new PathFollowerWithVisionAction(toCubePath) })));		// close in on cube
//		runAction( new ParallelAction(Arrays.asList(new Action[] {
//					new PathFollowerWithVisionAction(intakePath),				// intake cube at slower speed
//					new IntakeCubeAction() })));								// close grabber
//		runAction( new ElevatorAction(ElevatorArmBarStateEnum.SWITCH) );		// raise four bar to switch height
//		runAction( new InterruptableAction(new CollisionDetectionAction(),		// run into switch fence
//										   new PathFollowerWithVisionAction(collisionPath)));
//		runAction( new OuttakeAction() );										// shoot!
	}
}	
