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
		
		double velocity = 48;
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition();
		Vector2d switchStopPosition = 	  new Vector2d(140 - Constants.kCenterToFrontBumper, 58);	// 58 is center of switch platform		
		Vector2d turnPosition = 		  new Vector2d( 80, 58);
		Vector2d startCollisionPosition = new Vector2d(100, 58);
		
		if (switchSide == 'R') {
			switchStopPosition.setY(-switchStopPosition.getY());
			turnPosition.setY(-turnPosition.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
		}
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(startCollisionPosition, pathOptions));
		
		Path collisionPath = new Path();	// final velocity of this path will be 0
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
		
		System.out.println("CenterStartToSwitchMode path");
		System.out.println(path.toString());
		System.out.println(collisionPath.toString());
		
		runAction( new PathFollowerAction(path) );						// drive towards switch
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),		// raise elevator
				new InterruptableAction(new CollisionDetectionAction(),	// crash into switch
				new PathFollowerAction(collisionPath))
		})));
		runAction( new OuttakeAction() );								// shoot!
		
		
		
		
		
		
		// Pick up a 2nd cube from the pile
		
		Vector2d backupPosition = 		new Vector2d(36, 0);
		Vector2d cubePickupPosition = 	new Vector2d(98 - Constants.kCenterToFrontBumper, 0);	
		Vector2d startIntakePosition =	cubePickupPosition.sub(new Vector2d(18,0));
		
		Path backupPath = new Path();
		backupPath.add(new Waypoint(switchStopPosition, pathOptions));	// where we finished 
		backupPath.add(new Waypoint(backupPosition, pathOptions));
		backupPath.setReverseDirection();
		
		Path approachCubePath = new Path(Constants.kCollisionVel);
		approachCubePath.add(new Waypoint(backupPosition, pathOptions));
		approachCubePath.add(new Waypoint(startIntakePosition, pathOptions));
		
		Path intakeCubePath = new Path();
		intakeCubePath.add(new Waypoint(startIntakePosition, collisionOptions));
		intakeCubePath.add(new Waypoint(cubePickupPosition, collisionOptions));
		
		System.out.println("CenterStartToSwitchMode 2nd Cube Pickup");
		System.out.println(backupPath.toString());
		System.out.println(approachCubePath.toString());
		System.out.println(intakeCubePath.toString());
		
		runAction( new PathFollowerAction(backupPath) );			// backup
		runAction( new PathFollowerAction(approachCubePath) );		// approach cube
		runAction( new IntakeStartAction() );						// turn on intake
		runAction( new InterruptableAction( new CollisionDetectionAction(), 
				   new PathFollowerAction(intakeCubePath)) );		// close in on cube
		runAction( new PickUpCubeAction() );						// close grabber

		
		
		// score 2nd cube on switch
		
		// redo path to start at cubePickupPosition
		backupPath = new Path();
		backupPath.add(new Waypoint(cubePickupPosition, pathOptions));	
		backupPath.add(new Waypoint(backupPosition, pathOptions));
		backupPath.setReverseDirection();

		path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(backupPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(startCollisionPosition, pathOptions));
		
		System.out.println("CenterStartToSwitchMode 2nd Cube Score");
		System.out.println(backupPath.toString());
		System.out.println(path.toString());
		System.out.println(collisionPath.toString());
		
		
		// repeat cube scoring actions
		runAction( new PathFollowerAction(backupPath) );				// backup
		runAction( new PathFollowerAction(path) );						// approach switch						
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),		// raise elevator
				new InterruptableAction(new CollisionDetectionAction(),	// crash into switch
				new PathFollowerAction(collisionPath))
		})));
		runAction( new OuttakeAction() );								// shoot!
		
	}

}
