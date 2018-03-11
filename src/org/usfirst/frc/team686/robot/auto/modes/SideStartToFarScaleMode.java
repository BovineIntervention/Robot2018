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
		PathSegment.Options slowPathOptions	= new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition 		= startPosition.initialPose.getPosition();
//		Vector2d turnPosition1 			= new Vector2d(240, 132 - Constants.kCenterToSideBumper);
		Vector2d farTurnPosition		= new Vector2d(240, -90);
		Vector2d shootPosition 			= new Vector2d(290 - Constants.kCenterToFrontBumper, -78);
		Vector2d startElevatorPosition =   shootPosition.add(Vector2d.magnitudeAngle(12.0, 180 * Vector2d.degreesToRadians));
	
		// drive straight until we are between the switch and platform
		Vector2d turnPosition = new Vector2d(255, 116);
		Vector2d turnCenter = new Vector2d(215, 76);	// 15 inches beyond desired path
		double turnRadius = 40.0;
		double turnAngleStart = +90.0;
		double turnAngleEnd   = -20.0;					// extra 20 degrees brings us back to x=240"
		double turnAngleStep  = -10.0;
		
		
		if (startPosition == StartPositionOption.RIGHT_START) {
			turnPosition.setY(-turnPosition.getY());
			turnAngleStart = -turnAngleStart;
			turnAngleEnd = -turnAngleEnd;
			turnAngleStep = -turnAngleStep;
//			turnPosition1.setY(-turnPosition1.getY());
			farTurnPosition.setY(-farTurnPosition.getY());
			shootPosition.setY(-shootPosition.getY());
		}

		if (crossField == CrossFieldOption.YES)
		{
			Path path = new Path(slowPathOptions.getMaxSpeed());	// final velocity of this path will be collisionVelocity required by next path
			path.add(new Waypoint(initialPosition, pathOptions));
//			path.add(new Waypoint(turnPosition1,   pathOptions));
			for (double turnAngle = turnAngleStart; turnAngle >= turnAngleEnd; turnAngle += turnAngleStep)
			{
				Vector2d turnPoint = turnCenter.add(Vector2d.magnitudeAngle(turnRadius, turnAngle*Vector2d.degreesToRadians));
				path.add(new Waypoint(turnPoint, pathOptions));
			}
			path.add(new Waypoint(farTurnPosition,   pathOptions));
			path.add(new Waypoint(startElevatorPosition,     pathOptions));
			
			Path approachPath = new Path();
			approachPath.add(new Waypoint(startElevatorPosition, slowPathOptions));
			approachPath.add(new Waypoint(shootPosition,         slowPathOptions));
			
			System.out.println("SideStartToFarScaleMode path");
			System.out.println(path.toString());
			System.out.println(approachPath.toString());
		
			runAction( new PathFollowerAction(path) );

			runAction( new PathFollowerAction(path) );
			runAction( new ParallelAction(Arrays.asList(new Action[] {
					new ElevatorAction(ElevatorArmBarStateEnum.SCALE_HIGH),		// raise elevator
					new PathFollowerAction(approachPath)
			})));
			runAction( new OuttakeAction() );
			runAction( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );

		
		}
		else
		{
			// just cross line if everything is on the other side, and we don't want to interfere with a partner
			
			Vector2d stopPosition = new Vector2d(230, 132);
			
			Path path = new Path();	// final velocity of this path will be collisionVelocity required by next path
			path.add(new Waypoint(initialPosition, pathOptions));
			path.add(new Waypoint(   stopPosition, pathOptions));

			runAction( new PathFollowerAction(path) );
		}
	}

}
