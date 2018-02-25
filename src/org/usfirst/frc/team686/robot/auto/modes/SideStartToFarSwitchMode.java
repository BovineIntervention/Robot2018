package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.CollisionDetectionAction;
import org.usfirst.frc.team686.robot.auto.actions.ElevatorAction;
import org.usfirst.frc.team686.robot.auto.actions.InterruptableAction;
import org.usfirst.frc.team686.robot.auto.actions.OuttakeAction;
import org.usfirst.frc.team686.robot.auto.actions.ParallelAction;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerWithVisionAction;
import org.usfirst.frc.team686.robot.auto.modes.PowerUpAutoMode.InitialStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class SideStartToFarSwitchMode extends AutoModeBase {
	
	
	InitialStateEnum initialState;
	
	public SideStartToFarSwitchMode (InitialStateEnum _initialState)
	{
		initialState = _initialState; 
	}
	
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: " + initialState.name + " to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = initialState.pose.getPosition(); //first assume start is left and goal is right 
		
		Vector2d switchStopPosition = new Vector2d(FieldDimensions.kSwitchFromCenterStartDistX + (FieldDimensions.kSwitchLengthX/2),
													-(FieldDimensions.kSwitchLengthY/2) - Constants.kCenterToFrontBumper);
		
		Vector2d turnPosition = new Vector2d( FieldDimensions.kSwitchFromCenterStartDistX - Constants.kCenterToCornerBumper,
											  FieldDimensions.kSwitchLengthY + Constants.kCenterToCornerBumper);
		
		Vector2d turnPosition1 = new Vector2d(FieldDimensions.getScalePlatformFromCenterStartDistX() - Constants.kCenterToSideBumper, 
											  turnPosition.getY());
		
		Vector2d turnPosition2 = new Vector2d(turnPosition1.getX(), -turnPosition1.getY());
		
		Vector2d startCollisionPosition = new Vector2d(turnPosition2.getX(), turnPosition.getY() - 12);
		
		
		if(initialState == InitialStateEnum.LEFT)
		{
			switchStopPosition.setY(-switchStopPosition.getY());
			turnPosition.setY(-turnPosition.getY());
			turnPosition1.setY(-turnPosition1.getY());
			turnPosition2.setY(-turnPosition2.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
		}
		
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(turnPosition1, pathOptions));
		path.add(new Waypoint(turnPosition2, pathOptions));
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
