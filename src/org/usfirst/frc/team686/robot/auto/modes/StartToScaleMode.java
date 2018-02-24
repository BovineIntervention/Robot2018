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

public class StartToScaleMode extends AutoModeBase {
	

	
	boolean toRight;
	InitialStateEnum initialState;
	
	public StartToScaleMode (InitialStateEnum _initialState, boolean _toRight)
	{
		toRight = _toRight;
		initialState = _initialState;
	}
	



	@Override
	protected void routine() throws AutoModeEndedException {

		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, 36, false);
		
		Vector2d initialPosition = initialState.pose.getPosition();
		
		Vector2d turnPosition = new Vector2d(205, 130);
				
		Vector2d scaleStopPosition = new Vector2d(300 - (Constants.kCenterToFrontBumper/Math.sqrt(2)) - 6, (FieldDimensions.kScaleLengthY/2) + (Constants.kCenterToFrontBumper/Math.sqrt(2)));

		if(toRight){
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
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				//new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new PathFollowerWithVisionAction(pathToScale)
		})));
		
		runAction( new OuttakeAction() );
		
	}

}
