package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class SecondCubeForScaleMode {

	public static SeriesAction getActions(Vector2d currentPosition, char switchSide, char scaleSide)
	{
		// define paths
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options slowDownOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);

		// define positions
		// 30 degree approach angle for 2nd cube
		Vector2d backupPosition2 = 	 	 new Vector2d(270, 120);
		Vector2d stopPosition2 = 		 new Vector2d(202, 67).add(Vector2d.magnitudeAngle(Constants.kCenterToExtendedIntake +  0, 20.0*Vector2d.degreesToRadians));
		Vector2d slowDownPosition2 = 	 new Vector2d(202, 67).add(Vector2d.magnitudeAngle(Constants.kCenterToExtendedIntake + 18, 20.0*Vector2d.degreesToRadians));
		Vector2d closeIntakePosition2 =	 new Vector2d(202, 67).add(Vector2d.magnitudeAngle(Constants.kCenterToExtendedIntake +  6, 20.0*Vector2d.degreesToRadians));

		// shoot on scale positions
		Vector2d scaleStopPosition =       new Vector2d(287, 103);
		Vector2d turnToScalePosition =     scaleStopPosition.add(Vector2d.magnitudeAngle(25.0, 135 * Vector2d.degreesToRadians));
		Vector2d startElevatorPosition =   scaleStopPosition.add(Vector2d.magnitudeAngle(12.0, 135 * Vector2d.degreesToRadians));
						
		if (scaleSide == 'R') {
			backupPosition2.setY(-backupPosition2.getY());
			slowDownPosition2.setY(-slowDownPosition2.getY());
			closeIntakePosition2.setY(-closeIntakePosition2.getY());
			stopPosition2.setY(-stopPosition2.getY());

			turnToScalePosition.setY(-turnToScalePosition.getY());
			startElevatorPosition.setY(-startElevatorPosition.getY());
			scaleStopPosition.setY(-scaleStopPosition.getY());
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

		Path backupScalePath = new Path();	// final velocity of this path will be velocity required by next path
		backupScalePath.add(new Waypoint(stopPosition2, pathOptions));
		backupScalePath.add(new Waypoint(turnToScalePosition, pathOptions));		
		backupScalePath.setReverseDirection();
		
		Path path = new Path(slowDownOptions.getMaxSpeed());
		path.add(new Waypoint(turnToScalePosition, 		pathOptions));
		path.add(new Waypoint(startElevatorPosition, 	pathOptions));
		
		Path approachPath = new Path();
		approachPath.add(new Waypoint(startElevatorPosition, slowDownOptions));
		approachPath.add(new Waypoint(scaleStopPosition,     slowDownOptions));
		
		// display paths for debug
		System.out.println("SecondCubeForScaleMode");
		System.out.println(backupPath2.toString());
		System.out.println(toCubePath2.toString());
		System.out.println(intakePath2.toString());
		System.out.println(collisionPath2.toString());
		System.out.println(backupScalePath.toString());
		System.out.println(path.toString());
		System.out.println(approachPath.toString());

		
		

		
		// build sequence
		SeriesAction actions = new SeriesAction();
		
		actions.add( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.GROUND),				// drop elevator (if not already done
				new PathFollowerAction(backupPath2) })));						// backup towards wall
		
		actions.add( new IntakeStartAction() );									// turn on intake
		actions.add( new PathFollowerAction(toCubePath2) );						// quickly close in on cube
		actions.add( new DrivingCubeIntakeAction( intakePath2 ));				// intake cube

		actions.add( new PathFollowerAction(backupScalePath) );
		
		actions.add( new PathFollowerAction(path) );
		actions.add( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SCALE_HIGH),		// raise elevator
				new PathFollowerAction(approachPath)
		})));
		actions.add( new OuttakeAction() );
		actions.add( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );
		
		
		
		
		return actions;
	}
}	
