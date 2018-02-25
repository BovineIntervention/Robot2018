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

public class CenterStartToNearSwitchMode extends AutoModeBase {
	

	
	boolean toRight;
	
	public CenterStartToNearSwitchMode (boolean _toRight)
	{
		toRight = _toRight;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {

		System.out.println("STARTING AUTOMODE: Center to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = FieldDimensions.getCenterStartPose().getPosition();
		
		Vector2d switchStopPosition = FieldDimensions.getLeftSwitchPose().getPosition();
		
		if(toRight)
			switchStopPosition.setY(-switchStopPosition.getY());
		
		Vector2d turnPosition = new Vector2d(switchStopPosition.getX() - 36, switchStopPosition.getY());
		
		Vector2d startCollisionPosition = new Vector2d(turnPosition.getX() + 12, switchStopPosition.getY());
		
		
		Path path = new Path();
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(startCollisionPosition, pathOptions));
		
		Path collisionPath = new Path(Constants.kCollisionVel);
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
		
		runAction( new PathFollowerWithVisionAction(path) );
System.out.println("DONE RUNNING PATH TO SWITCH");
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				//new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(),
				new PathFollowerWithVisionAction(collisionPath))
		})));
		
		//runAction( new OuttakeAction() );
		
	}

}
