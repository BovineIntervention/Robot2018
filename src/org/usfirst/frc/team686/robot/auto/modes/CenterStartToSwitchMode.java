package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.CrossFieldOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartPositionOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;

public class CenterStartToSwitchMode extends AutoModeBase {
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public CenterStartToSwitchMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {

		System.out.println("STARTING AUTOMODE: Center to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		PathSegment.Options slowOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition();
		Vector2d turnPosition = 		  new Vector2d( 80, 64);
		Vector2d startSlowPosition = 	  new Vector2d(100, 64);
		Vector2d switchStopPosition = 	  new Vector2d(142 - Constants.kCenterToFrontBumper, 58);	// 58 is center of switch platform		
		
		double switchThresholdX = 138 - Constants.kCenterToFrontBumper;
		
		if (switchSide == 'R') {
			switchStopPosition.setY(-switchStopPosition.getY());
			turnPosition.setY(-turnPosition.getY());
			startSlowPosition.setY(-startSlowPosition.getY());
		}
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(startSlowPosition, pathOptions));
		
		Path slowPath = new Path();	// final velocity of this path will be 0
		slowPath.add(new Waypoint(startSlowPosition, slowOptions));
		slowPath.add(new Waypoint(switchStopPosition, slowOptions));
		
		System.out.println("CenterStartToSwitchMode path");
		System.out.println(path.toString());
		System.out.println(slowPath.toString());
		
		runAction( new PathFollowerAction(path) );						// drive towards switch
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),		// raise elevator
				new InterruptableAction(new CrossXYAction('x', switchThresholdX),	// crash into switch
				new PathFollowerAction(slowPath))
		})));
		runAction( new OuttakeAction() );								// shoot!
		
		
		
		
		
		
		// Pick up a 2nd cube from the pile
		
		Vector2d backupPosition = 		new Vector2d(24, 0);
		Vector2d cubePickupPosition = 	new Vector2d(140 - 3*13 - Constants.kCenterToFrontBumper +  3, 0);	// plow into pile an extra 3"
		Vector2d startIntakePosition =	new Vector2d(140 - 3*13 - Constants.kCenterToFrontBumper - 12, 0);	// start looking for cube 12" early
		
		Path backupPath = new Path();
		backupPath.add(new Waypoint(switchStopPosition, pathOptions));	// where we finished 
		backupPath.add(new Waypoint(backupPosition, pathOptions));
		backupPath.setReverseDirection();
		
		Path approachCubePath = new Path(Constants.kCollisionVel);
		approachCubePath.add(new Waypoint(backupPosition, pathOptions));
		approachCubePath.add(new Waypoint(startIntakePosition, pathOptions));
		
		Path intakeCubePath = new Path();
		intakeCubePath.add(new Waypoint(startIntakePosition, slowOptions));
		intakeCubePath.add(new Waypoint(cubePickupPosition, slowOptions));
		
		System.out.println("CenterStartToSwitchMode 2nd Cube Pickup");
		System.out.println(backupPath.toString());
		System.out.println(approachCubePath.toString());
		System.out.println(intakeCubePath.toString());
		
		runAction( new PathFollowerAction(backupPath) );	         // backup
		runAction( new IntakeStartAction() );						// turn on intake
		runAction( new GrabberOpenAction () );
		runAction( new PathFollowerAction(approachCubePath) );		// approach cube
		runAction( new DrivingCubeIntakeAction( intakeCubePath ));
		runAction( new GrabberCloseAction () );

		
		
		
		// score 2nd cube on switch
		
		Path backupPath2 = new Path();
		backupPath2.add(new Waypoint(cubePickupPosition, pathOptions));	
		backupPath2.add(new Waypoint(backupPosition, pathOptions));
		backupPath2.setReverseDirection();

		Path path2 = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path2.add(new Waypoint(backupPosition, pathOptions));
		path2.add(new Waypoint(turnPosition, pathOptions));
		path2.add(new Waypoint(startSlowPosition, pathOptions));
		
		Path collisionPath2 = new Path();	// final velocity of this path will be 0
		collisionPath2.add(new Waypoint(startSlowPosition, slowOptions));
		collisionPath2.add(new Waypoint(switchStopPosition, slowOptions));
		
		System.out.println("CenterStartToSwitchMode 2nd Cube Score");
		System.out.println(backupPath2.toString());
		System.out.println(path2.toString());
		System.out.println(collisionPath2.toString());
		
		
		// repeat cube scoring actions
		runAction( new PathFollowerAction(backupPath2) );				// backup
		runAction( new PathFollowerAction(path2) );						// approach switch						
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),		// raise elevator
				new InterruptableAction(new CrossXYAction('x', switchThresholdX),	// crash into switch
				new PathFollowerAction(collisionPath2))
		})));
		runAction( new OuttakeAction() );								// shoot!
		
	}

}
