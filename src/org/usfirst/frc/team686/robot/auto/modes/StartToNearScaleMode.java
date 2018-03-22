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

public class StartToNearScaleMode extends AutoModeBase {
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public StartToNearScaleMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}


 
	@Override
	protected void routine() throws AutoModeEndedException {

		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		PathSegment.Options slowPathOptions	= new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);

		Vector2d initialPosition = startPosition.initialPose.getPosition();
		Vector2d centerStartTurnPosition = new Vector2d(140, 130);	// needed if starting from center to avoid clipping switch
		Vector2d scaleStopPosition =       new Vector2d(287, 103);
		Vector2d turnToScalePosition =     scaleStopPosition.add(Vector2d.magnitudeAngle(25.0, 135 * Vector2d.degreesToRadians));
		Vector2d startElevatorPosition =   scaleStopPosition.add(Vector2d.magnitudeAngle(12.0, 135 * Vector2d.degreesToRadians));

		if (scaleSide == 'R') {
			centerStartTurnPosition.setY(-centerStartTurnPosition.getY());
			turnToScalePosition.setY(-turnToScalePosition.getY());
			startElevatorPosition.setY(-startElevatorPosition.getY());
			scaleStopPosition.setY(-scaleStopPosition.getY());
		}
		
		Path path = new Path(slowPathOptions.getMaxSpeed());
		path.add(new Waypoint(initialPosition, 			pathOptions));
		path.add(new Waypoint(centerStartTurnPosition, 	pathOptions));
		path.add(new Waypoint(turnToScalePosition, 		pathOptions));
		path.add(new Waypoint(startElevatorPosition, 	pathOptions));
		
		Path approachPath = new Path();
		approachPath.add(new Waypoint(startElevatorPosition, slowPathOptions));
		approachPath.add(new Waypoint(scaleStopPosition,     slowPathOptions));
		
		System.out.println("SideStartToNearScaleMode path");
		System.out.println(path.toString());
		System.out.println(approachPath.toString());
	
		
		runAction( new PathFollowerAction(path) );
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SCALE_HIGH),		// raise elevator
				new PathFollowerAction(approachPath)
		})));
		runAction( new OuttakeAction() );
		runAction( new ElevatorAction(ElevatorArmBarStateEnum.GROUND) );

		
		// go after second cube
		SeriesAction secondCubeActions;
		
		if (switchSide == scaleSide)
			secondCubeActions = SecondCubeForSameSideSwitchMode.getActions(scaleStopPosition, switchSide, scaleSide);
		else
			secondCubeActions = SecondCubeForScaleMode.getActions(scaleStopPosition, switchSide, scaleSide);
		
		runAction( secondCubeActions );
	}

}
