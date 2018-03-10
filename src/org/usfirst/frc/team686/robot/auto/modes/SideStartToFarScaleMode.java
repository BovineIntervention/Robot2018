package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.CrossFieldOption;
import org.usfirst.frc.team686.robot.SmartDashboardInteractions.StartPositionOption;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.Action;
import org.usfirst.frc.team686.robot.auto.actions.CollisionDetectionAction;
import org.usfirst.frc.team686.robot.auto.actions.ElevatorAction;
import org.usfirst.frc.team686.robot.auto.actions.InterruptableAction;
import org.usfirst.frc.team686.robot.auto.actions.OuttakeAction;
import org.usfirst.frc.team686.robot.auto.actions.ParallelAction;
import org.usfirst.frc.team686.robot.auto.actions.PathFollowerAction;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.Superstructure.ElevatorArmBarStateEnum;

public class SideStartToFarScaleMode extends AutoModeBase {
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public SideStartToFarScaleMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}
	

 
	@Override
	protected void routine() throws AutoModeEndedException 
	{
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		
		Vector2d initialPosition 	= startPosition.initialPose.getPosition();
		Vector2d turnPosition1 		= new Vector2d(240, 132 - Constants.kCenterToSideBumper);
		Vector2d turnPosition2 		= new Vector2d(240, -90);
		Vector2d shootPosition 		= new Vector2d(282 - Constants.kCenterToFrontBumper, -78);
		
		if (startPosition == StartPositionOption.RIGHT_START) {
			turnPosition1.setY(-turnPosition1.getY());
			turnPosition2.setY(-turnPosition2.getY());
			shootPosition.setY(-shootPosition.getY());
		}

		if (crossField == CrossFieldOption.YES)
		{
		
//TODO: add arc to smooth steering???			
			
			Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
			path.add(new Waypoint(initialPosition, pathOptions));
			path.add(new Waypoint(turnPosition1,   pathOptions));
			path.add(new Waypoint(turnPosition2,   pathOptions));
			path.add(new Waypoint(shootPosition,   pathOptions));
			
			System.out.println("SideStartToFarScaleMode path");
			System.out.println(path.toString());
		
			runAction( new PathFollowerAction(path) );
			runAction( new ElevatorAction(ElevatorArmBarStateEnum.SCALE_HIGH) );
			runAction( new OuttakeAction() );
			runAction( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );
		}
		else
		{
			// just cross line if everything is on the other side, and we don't want to interfere with a partner
			
			Path path = new Path();	// final velocity of this path will be collisionVelocity required by next path
			path.add(new Waypoint(initialPosition, pathOptions));
			path.add(new Waypoint(turnPosition1,   pathOptions));

			runAction( new PathFollowerAction(path) );
		}
	}

}
