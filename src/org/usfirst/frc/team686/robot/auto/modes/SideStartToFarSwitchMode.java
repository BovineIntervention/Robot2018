package org.usfirst.frc.team686.robot.auto.modes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class SideStartToFarSwitchMode extends AutoModeBase {
	
	
	StartPositionOption startPosition;
	char switchSide, scaleSide;
	CrossFieldOption crossField;
	
	public SideStartToFarSwitchMode (StartPositionOption _startPosition, char _switchSide, char _scaleSide, CrossFieldOption _crossField)
	{
		startPosition = _startPosition; 
		switchSide = _switchSide;
		scaleSide = _scaleSide;
		crossField = _crossField;
	}
	

	@Override
	protected void routine() throws AutoModeEndedException {
		
		
		System.out.println("STARTING AUTOMODE: " + startPosition.name + " to Switch");
		
		double fastPathVelocity = 100.0;
		PathSegment.Options pathOptions	= new PathSegment.Options(fastPathVelocity, Constants.kPathFollowingMaxAccel, 48, false);
		PathSegment.Options backOptions	= new PathSegment.Options(fastPathVelocity, Constants.kPathFollowingMaxAccel, 24, false);
		PathSegment.Options tightTurnOptions	= new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, 18, false);
		PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);
		
		Vector2d initialPosition = startPosition.initialPose.getPosition(); //first assume start is left and goal is right 
		
		// drive straight until we are between the switch and platform
		// smooth arc around corner
		Vector2d turnCenter = new Vector2d(215, 76);	// 15 inches beyond desired path
		double turnRadius = 40.0;
		double turnAngleStart = +90.0;
		double turnAngleEnd   = -20.0;					// extra 20 degrees brings us back to x=240"
		double turnAngleStep  = -10.0;

		List<Vector2d> turnPoints = new ArrayList<Vector2d>();
		for (double turnAngle = turnAngleStart; turnAngle >= turnAngleEnd; turnAngle += turnAngleStep)
			turnPoints.add( turnCenter.add(Vector2d.magnitudeAngle(turnRadius, turnAngle*Vector2d.degreesToRadians)) );
		
		// switch corner
		Vector2d switchStopPosition = new Vector2d(196 + Constants.kCenterToFrontBumper/Math.sqrt(2), -77 - Constants.kCenterToFrontBumper/Math.sqrt(2));

		// position that defines the turn around point
		Vector2d turnAroundPosition = new Vector2d(240, -160);

		// distance at which we start checking collision detector
		Vector2d startCollisionPosition = switchStopPosition.add(Vector2d.magnitudeAngle(24.0, -Math.PI/4));
		
		double switchThresholdY = switchStopPosition.getY() + 4;
		
		
		if (startPosition == StartPositionOption.RIGHT_START) {
			for (Vector2d turnPoint : turnPoints)
				turnPoint.setY(-turnPoint.getY());
		    turnAroundPosition.setY(-turnAroundPosition.getY());     
		    startCollisionPosition.setY(-startCollisionPosition.getY());
		    switchStopPosition.setY(-switchStopPosition.getY());
		    switchThresholdY = -switchThresholdY;
		}

		
		if (crossField == CrossFieldOption.YES)
		{
			Path path = new Path(collisionOptions.getMaxSpeed());
			path.add(new Waypoint(initialPosition, pathOptions));
			for (Vector2d turnPoint : turnPoints)
				path.add(new Waypoint(turnPoint, pathOptions));
			path.add(new Waypoint(turnAroundPosition, backOptions));
			path.add(new Waypoint(startCollisionPosition, tightTurnOptions));
			
			Path collisionPath = new Path();
			collisionPath.add(new Waypoint(startCollisionPosition, collisionOptions));
			collisionPath.add(new Waypoint(switchStopPosition, collisionOptions));
			
			System.out.println(path.toString());
			System.out.println(collisionPath.toString());
			
			runAction( new PathFollowerAction(path) );
			runAction( new ParallelAction(Arrays.asList(new Action[] {
					new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
					new InterruptableAction(new CrossXYAction('y', switchThresholdY),
					new PathFollowerAction(collisionPath))
			})));
			runAction( new OuttakeAction() );
		}
		else
		{
			// just cross line if everything is on the other side, and we don't want to interfere with a partner
			
			Path path = new Path();	// final velocity of this path will be collisionVelocity required by next path
			path.add(new Waypoint(initialPosition, 	 pathOptions));
			path.add(new Waypoint(turnPoints.get(0), pathOptions));

			runAction( new PathFollowerAction(path) );
		}
	}
}