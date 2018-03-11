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
		// define paths
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options slowDownOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);

		// define positions
		// 30 degree approach angle for 2nd cube
		Vector2d backupPosition2 = 	 	 new Vector2d(270, 120);
		Vector2d stopPosition2 = 		 new Vector2d(202, 70).add(Vector2d.magnitudeAngle(Constants.kCenterToFrontBumper -  2, 30.0*Vector2d.degreesToRadians));
		Vector2d slowDownPosition2 = 	 new Vector2d(202, 70).add(Vector2d.magnitudeAngle(Constants.kCenterToFrontBumper + 18, 30.0*Vector2d.degreesToRadians));
		Vector2d closeIntakePosition2 =	 new Vector2d(202, 70).add(Vector2d.magnitudeAngle(Constants.kCenterToFrontBumper +  6, 30.0*Vector2d.degreesToRadians));

		// 0 degree approach angle for 2nd cube
		Vector2d backupPosition3 = 	 	 new Vector2d(250, 42);
		Vector2d stopPosition3 = 		 new Vector2d(202, 42).add(Vector2d.magnitudeAngle(Constants.kCenterToFrontBumper -  2,  0.0*Vector2d.degreesToRadians));
		Vector2d slowDownPosition3 = 	 new Vector2d(202, 42).add(Vector2d.magnitudeAngle(Constants.kCenterToFrontBumper + 18,  0.0*Vector2d.degreesToRadians));
		Vector2d closeIntakePosition3 =	 new Vector2d(202, 42).add(Vector2d.magnitudeAngle(Constants.kCenterToFrontBumper +  6,  0.0*Vector2d.degreesToRadians));
		
		double shootThresholdX =  200 + Constants.kCenterToFrontBumper;
				
		if (switchSide == 'R') {
			backupPosition2.setY(-backupPosition2.getY());
			slowDownPosition2.setY(-slowDownPosition2.getY());
			closeIntakePosition2.setY(-closeIntakePosition2.getY());
			stopPosition2.setY(-stopPosition2.getY());

			backupPosition3.setY(-backupPosition3.getY());
			slowDownPosition3.setY(-slowDownPosition3.getY());
			closeIntakePosition3.setY(-closeIntakePosition3.getY());
			stopPosition3.setY(-stopPosition3.getY());
		}


		Path backupPath2 = new Path();	// final velocity of this path will be velocity required by next path
		backupPath2.add(new Waypoint(currentPosition, pathOptions));
		backupPath2.add(new Waypoint(backupPosition2, pathOptions));		
		backupPath2.setReverseDirection();
		
		Path toCubePath2 = new Path(slowDownOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
		toCubePath2.add(new Waypoint(backupPosition2, pathOptions));
		toCubePath2.add(new Waypoint(slowDownPosition2, pathOptions));

		Path intakePath2 = new Path(collisionOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
		intakePath2.add(new Waypoint(slowDownPosition2, slowDownOptions));
		intakePath2.add(new Waypoint(closeIntakePosition2, slowDownOptions));
		
		Path collisionPath2 = new Path();							// final velocity of this path will be 0
		collisionPath2.add(new Waypoint(closeIntakePosition2, collisionOptions));
		collisionPath2.add(new Waypoint(stopPosition2, collisionOptions));
		
		
		// display paths for debug
		System.out.println("SameSideSwitchSecondCubeMode path");
		System.out.println(backupPath2.toString());
		System.out.println(toCubePath2.toString());
		System.out.println(intakePath2.toString());
		System.out.println(collisionPath2.toString());

		
		
		Path backupPath3 = new Path();	// final velocity of this path will be velocity required by next path
		backupPath3.add(new Waypoint(stopPosition2,   pathOptions));
		backupPath3.add(new Waypoint(backupPosition3, pathOptions));		
		backupPath3.setReverseDirection();
		
		Path toCubePath3 = new Path(slowDownOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
		toCubePath3.add(new Waypoint(backupPosition3, pathOptions));
		toCubePath3.add(new Waypoint(slowDownPosition3, pathOptions));

		Path intakePath3 = new Path(collisionOptions.getMaxSpeed());	// final velocity of this path will be velocity required by next path
		intakePath3.add(new Waypoint(slowDownPosition3, slowDownOptions));
		intakePath3.add(new Waypoint(closeIntakePosition3, slowDownOptions));
		
		Path collisionPath3 = new Path();							// final velocity of this path will be 0
		collisionPath3.add(new Waypoint(closeIntakePosition3, collisionOptions));
		collisionPath3.add(new Waypoint(stopPosition3, collisionOptions));
		
		
		// display paths for debug
		System.out.println("SameSideSwitchSecondCubeMode path");
		System.out.println(backupPath3.toString());
		System.out.println(toCubePath3.toString());
		System.out.println(intakePath3.toString());
		System.out.println(collisionPath3.toString());

		
		// build sequence
		SeriesAction actions = new SeriesAction();
		
		actions.add( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.GROUND),				// drop elevator (if not already done
				new PathFollowerAction(backupPath2) })));						// backup towards wall
		
		actions.add( new IntakeStartAction() );									// turn on intake
		actions.add( new PathFollowerAction(toCubePath2) );						// quickly close in on cube
		actions.add( new InterruptableAction( new CubeDetectionAction(), 
				     new PathFollowerAction(intakePath2)) );					// slowly close in on cube
		actions.add( new PickUpCubeAction() );									// close grabber
		
		actions.add( new ElevatorAction(ElevatorArmBarStateEnum.SWITCH) );		// raise four bar to switch height
		actions.add( new InterruptableAction(new CrossXAction(shootThresholdX),	// run into switch fence
				     new PathFollowerAction(collisionPath2)));
		actions.add( new OuttakeAction() );										// shoot!

		
		
		// 3rd cube!?!?!?!?!?!
		actions.add( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.GROUND),				// drop elevator (if not already done
				new PathFollowerAction(backupPath3) })));						// backup towards wall
		
		actions.add( new IntakeStartAction() );									// turn on intake
		actions.add( new PathFollowerAction(toCubePath3) );						// quickly close in on cube
		actions.add( new InterruptableAction( new CubeDetectionAction(), 
				     new PathFollowerAction(intakePath3)) );					// slowly close in on cube
		actions.add( new PickUpCubeAction() );									// close grabber
		
		actions.add( new ElevatorAction(ElevatorArmBarStateEnum.SWITCH) );		// raise four bar to switch height
		actions.add( new InterruptableAction(new CrossXAction(shootThresholdX),	// run into switch fence
					 new PathFollowerAction(collisionPath3)));
		actions.add( new OuttakeAction() );										// shoot!
		
		
		
		return actions;
	}
}	
