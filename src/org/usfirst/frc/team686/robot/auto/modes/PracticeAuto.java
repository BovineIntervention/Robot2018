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
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;

public class PracticeAuto extends AutoModeBase {
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public PracticeAuto(StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition;   
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}
	

	public PracticeAuto(String gameData, StartDelayOption startDelay, StartPositionOption startPose,
			PriorityOption priority, CrossFieldOption crossField2) {
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void routine() throws AutoModeEndedException {

		System.out.println("STARTING AUTOMODE: Practice Auto");
		
		double velocity = 48;    
		PathSegment.Options pathOptions	= new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
		//PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);             //what does false do?
		
		Vector2d initialPosition = startPosition.initialPose.getPosition();
	    Vector2d cubePosition = new Vector2d(58, 22.5);	
		//Vector2d startCollisionPosition = new Vector2d(70, 15);
		
		Path path = new Path(Constants.kCollisionVel);	// final velocity of this path will be collisionVelocity required by next path
		path.add(new Waypoint(initialPosition, pathOptions)); 
		path.add(new Waypoint(cubePosition, pathOptions));
      //path.add(new Waypoint(startCollisionPosition, pathOptions));
		
		/*
		Path collisionPath = new Path();	// final velocity of this path will be 0
		collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
		collisionPath.add(new Waypoint(CubeStopPosition, collisionOptions));
		*/
		
		System.out.println("Practice Auto path");
		System.out.println(path.toString());
      //System.out.println(collisionPath.toString());
		
		runAction( new PathFollowerWithVisionAction(path) );
		runAction(new ElevatorAction(ElevatorArmBarStateEnum.EXCHANGE));
		//runAction( new ParallelAction(Arrays.asList(new Action[] {            //what do these do?
				//new ElevatorAction(ElevatorArmBarStateEnum.EXCHANGE),     
           //new InterruptableAction(new CollisionDetectionAction()
          //new PathFollowerWithVisionAction(collisionPath))
		//})));
		//runAction( new intakeAction() );         
		
		
		
		
		
		
		
	}

}
