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
		
		// TODO: Center to Scale will hit the corner of the switch.  Add an intermediate point (maybe (140,130)) to keep it away
		Vector2d turnPosition = new Vector2d(205, 130);
				
		Vector2d scaleStopPosition = new Vector2d(300 - (Constants.kCenterToFrontBumper/Math.sqrt(2)) - 6, (FieldDimensions.kScaleLengthY/2) + (Constants.kCenterToFrontBumper/Math.sqrt(2)));

		if (scaleSide == 'R') {
			turnPosition.setY(-turnPosition.getY());
			scaleStopPosition.setY(-scaleStopPosition.getY());
		}
		
		Path path = new Path();
		path.add(new Waypoint(initialPosition, pathOptions));
		path.add(new Waypoint(turnPosition, pathOptions));
		
		Path pathToScale = new Path();
		pathToScale.add(new Waypoint(turnPosition, pathOptions));
		//pathToScale.add(new Waypoint(turnPosition1, pathOptions));
		pathToScale.add(new Waypoint(scaleStopPosition, pathOptions));
		
		System.out.println("StartToScaleMode path");
		System.out.println(path.toString());
		System.out.println(pathToScale.toString());
	
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				//new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new PathFollowerWithVisionAction(pathToScale)
		})));
		
		runAction( new OuttakeAction() );
		
	}

}
