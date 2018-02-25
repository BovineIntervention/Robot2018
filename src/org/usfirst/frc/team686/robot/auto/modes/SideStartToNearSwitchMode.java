package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.CollisionDetectionAction;
import org.usfirst.frc.team686.robot.auto.actions.InterruptableAction;
import org.usfirst.frc.team686.robot.auto.actions.ParallelAction;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerWithVisionAction;
import org.usfirst.frc.team686.robot.auto.modes.PowerUpAutoMode.InitialStateEnum;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class SideStartToNearSwitchMode extends AutoModeBase {
	
	InitialStateEnum initialState;
	boolean toRight;
	
	public SideStartToNearSwitchMode(InitialStateEnum _initialState, boolean _toRight){
		
		initialState = _initialState;
		toRight = _toRight;
		
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		 
System.out.println("STARTING AUTOMODE: " + initialState.name + " to Near Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 36, false);
		PathSegment.Options tightTurnOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 18, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = initialState.pose.getPosition();
		
		Vector2d switchStopPosition = new Vector2d(FieldDimensions.kSwitchFromCenterStartDistX + (FieldDimensions.kSwitchLengthX/2) + 12,
													(FieldDimensions.kSwitchLengthY/2) + Constants.kCenterToFrontBumper + 6);
		
		//Vector2d turnPosition = new Vector2d(switchStopPosition.getX(), initialPosition.getY());
		
		Vector2d turnPosition = new Vector2d(switchStopPosition.getX() + 12, switchStopPosition.getY() + 36);
		
		//Vector2d turnPosition1 = new Vector2d(switchStopPosition.getX() - 12, switchStopPosition.getY() + 36);
		
		Vector2d startCollisionPosition = new Vector2d(switchStopPosition.getX()+6, switchStopPosition.getY() + 12);
		
		if(toRight){
			turnPosition.setY(-turnPosition.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
			switchStopPosition.setY(-switchStopPosition.getY());
		}

		
		Path path = new Path();
		path.add(new Waypoint(initialPosition, pathOptions));
		System.out.println("INITIAL POSITION: " + initialPosition.toString());
		path.add(new Waypoint(turnPosition, pathOptions));
		System.out.println("TURN POSITION: " + turnPosition.toString());
		//path.add(new Waypoint(turnPosition1, pathOptions));
		path.add(new Waypoint(startCollisionPosition, tightTurnOptions));
		System.out.println("COLLISION POSITION: " + startCollisionPosition.toString());
		
		Path collisionPath = new Path(Constants.kCollisionVel);
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
		System.out.println("STOP POSITION:" + switchStopPosition.toString());
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				//new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(),
				new PathFollowerWithVisionAction(collisionPath))
		})));
		
		//runAction( new OuttakeAction() );
		
	}
	
}
