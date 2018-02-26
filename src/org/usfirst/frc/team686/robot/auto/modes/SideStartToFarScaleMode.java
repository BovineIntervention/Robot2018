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

public class SideStartToFarScaleMode extends AutoModeBase {
	
	StartPositionOption startPosition;
	boolean startRight;
	
	public SideStartToFarScaleMode (StartPositionOption _startPosition, boolean _toRight)
	{
		startPosition = _startPosition; 
		startRight = _toRight;	// if true: start on right, drive to left side scale
	}


 
	@Override
	protected void routine() throws AutoModeEndedException 
	{

		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		
		Vector2d initialPosition 	= startPosition.initialPose.getPosition();
		Vector2d turnPosition1 		= new Vector2d(230, 132 - Constants.kCenterToSideBumper);
		Vector2d turnPosition2 		= new Vector2d(218, -90);
		Vector2d shootPosition 		= new Vector2d(294 - Constants.kCenterToFrontBumper, -78);
		
		if (startRight)
		{
			turnPosition1.setY(-turnPosition1.getY());
			turnPosition2.setY(-turnPosition2.getY());
			shootPosition.setY(-shootPosition.getY());
		}
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition1,   pathOptions));
		path.add(new Waypoint(turnPosition2,   pathOptions));
		path.add(new Waypoint(shootPosition,   pathOptions));
		
		System.out.println("SideStartToFarScaleMode path");
		System.out.println(path.toString());
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ElevatorAction(ElevatorArmBarStateEnum.SCALE_HIGH) );
		runAction( new OuttakeAction() );
		runAction( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );
	}

}
