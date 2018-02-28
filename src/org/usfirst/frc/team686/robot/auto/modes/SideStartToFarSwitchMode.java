package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
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
	
	public SideStartToFarSwitchMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: " + startPosition.name + " to Switch");
		
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		PathSegment.Options tightTurnOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 18, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, 48, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition(); //first assume start is left and goal is right 
		
		Vector2d turnPosition = new Vector2d( FieldDimensions.kSwitchFromCenterStartDistX,
											  -(FieldDimensions.kSwitchLengthY/2) - Constants.kCenterToCornerBumper);
		
		Vector2d turnPosition1 = new Vector2d(FieldDimensions.getScalePlatformFromCenterStartDistX(), 
											  turnPosition.getY() + 12);
		
		
		Vector2d turnPosition2 = new Vector2d(230, -(132 - Constants.kCenterToSideBumper));
		
		
		Vector2d startCollisionPosition = new Vector2d(196 + (Constants.kCenterToFrontBumper+12)/Math.sqrt(2), 76.75 + Constants.kCenterToFrontBumper/Math.sqrt(2));
		
		Vector2d switchStopPosition = new Vector2d(196 + (Constants.kCenterToFrontBumper)/Math.sqrt(2), 76.75 + Constants.kCenterToFrontBumper/Math.sqrt(2));
		
		
		if(switchSide == 'R'){
			switchStopPosition.setY(-switchStopPosition.getY());
			turnPosition.setY(-turnPosition.getY());
			turnPosition1.setY(-turnPosition1.getY());
			turnPosition2.setY(-turnPosition2.getY());
			startCollisionPosition.setY(-startCollisionPosition.getY());
		}

		
		
		
		Path path = new Path(collisionOptions.getMaxSpeed());
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		path.add(new Waypoint(turnPosition1, pathOptions));
		path.add(new Waypoint(turnPosition2, tightTurnOptions));
		path.add(new Waypoint(startCollisionPosition, tightTurnOptions));
		
		Path collisionPath = new Path();
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
		
		System.out.println(path.toString());
		System.out.println(collisionPath.toString());
		
		runAction( new PathFollowerWithVisionAction(path) );
/*		runAction( new ParallelAction(Arrays.asList(new Action[] {
				//new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(),
				new PathFollowerWithVisionAction(collisionPath))
		})));
		runAction( new OuttakeAction() );
*/		
		
	}
}