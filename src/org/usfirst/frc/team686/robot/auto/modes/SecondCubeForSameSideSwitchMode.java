package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class SecondCubeForSameSideSwitchMode {

	public static SeriesAction getActions(Vector2d currentPosition, char switchSide, char scaleSide)
	{
		// define positions, angles
		Vector2d slowDownPosition = 	 new Vector2d(224 + Constants.kCenterToFrontBumper, 70);
		Vector2d closeIntakePosition = 	 new Vector2d(212 + Constants.kCenterToFrontBumper, 70);
		Vector2d stopPosition = 		 new Vector2d(196 + Constants.kCenterToFrontBumper, 70);

		if (switchSide == 'R') {
			slowDownPosition.setY(-slowDownPosition.getY());
			closeIntakePosition.setY(-closeIntakePosition.getY());
			stopPosition.setY(-stopPosition.getY());
		}

		double turnAroundHeadingDeg = currentPosition.angle(slowDownPosition) * Vector2d.radiansToDegrees;
		

		// define paths
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options slowDownOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Path toCubePath = new Path(slowDownOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
		toCubePath.add(new Waypoint(currentPosition, pathOptions));
		toCubePath.add(new Waypoint(slowDownPosition, pathOptions));

		Path intakePath = new Path(collisionOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
		intakePath.add(new Waypoint(slowDownPosition, slowDownOptions));
		intakePath.add(new Waypoint(closeIntakePosition, slowDownOptions));
		
		Path collisionPath = new Path();							// final velocity of this path will be 0
		collisionPath.add(new Waypoint(closeIntakePosition, collisionOptions));
		collisionPath.add(new Waypoint(stopPosition, collisionOptions));
		
		
		// display paths for debug
		System.out.println("SameSideSwitchSecondCubeMode path");
		System.out.printf("Turnaround heading: %.1f deg\n", turnAroundHeadingDeg);
		System.out.println(toCubePath.toString());
		System.out.println(intakePath.toString());
		System.out.println(collisionPath.toString());


		// build sequence
		SeriesAction actions = new SeriesAction();
		
		actions.add( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );		// drop elevator (if not already done)
		actions.add( new PointTurnAction(turnAroundHeadingDeg) );					// turn towards cube at end of switch
		actions.add( new ParallelAction(Arrays.asList(new Action[] {
						new IntakeStartAction(),								// turn on intake
						new PathFollowerAction(toCubePath) })));		// close in on cube
		actions.add( new InterruptableAction( new CollisionDetectionAction(), 
				       new PathFollowerAction(intakePath)) );			// close in on cube
		actions.add( new PickUpCubeAction() );									// close grabber
		
		actions.add( new ElevatorAction(ElevatorArmBarStateEnum.SWITCH) );		// raise four bar to switch height
		actions.add( new InterruptableAction(new CollisionDetectionAction(),	// run into switch fence
						new PathFollowerAction(collisionPath)));
		actions.add( new OuttakeAction() );										// shoot!
		
		return actions;
	}
}	
