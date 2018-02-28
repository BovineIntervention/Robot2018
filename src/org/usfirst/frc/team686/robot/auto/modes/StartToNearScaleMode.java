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
import org.usfirst.frc.team686.robot.auto.actions.SeriesAction;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class StartToNearScaleMode extends AutoModeBase {
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	
	public StartToNearScaleMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
	}


 
	@Override
	protected void routine() throws AutoModeEndedException {

		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 48, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition();
		
		Vector2d centerStartTurnPosition = new Vector2d(140, 130);	// needed if starting from center to avoid clipping switch

		Vector2d turnToScalePosition = new Vector2d(205, 130);
				
		Vector2d scaleStopPosition = new Vector2d(300 - (Constants.kCenterToFrontBumper/Math.sqrt(2)) - 6, (FieldDimensions.kScaleLengthY/2) + (Constants.kCenterToFrontBumper/Math.sqrt(2)));

		if (startPosition == StartPositionOption.RIGHT_START) {
			centerStartTurnPosition.setY(-centerStartTurnPosition.getY());
			turnToScalePosition.setY(-turnToScalePosition.getY());
			scaleStopPosition.setY(-scaleStopPosition.getY());
		}
		
		Path path = new Path();
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(centerStartTurnPosition, pathOptions));
		path.add(new Waypoint(turnToScalePosition, pathOptions));
		path.add(new Waypoint(scaleStopPosition, pathOptions));
		
		System.out.println("SideStartToNearScaleMode path");
		System.out.println(path.toString());
	
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ElevatorAction(ElevatorArmBarStateEnum.SCALE_HIGH) );
		runAction( new OuttakeAction() );

		
		
		// go after second cube
		SeriesAction secondCubeActions;
		
		if (switchSide == scaleSide)
			secondCubeActions = SecondCubeForSameSideSwitchMode.getActions(scaleStopPosition, switchSide, scaleSide);
		else
			secondCubeActions = SecondCubeForScaleMode.getActions(scaleStopPosition, switchSide, scaleSide);
		
		runAction( secondCubeActions );
		
	}

}
