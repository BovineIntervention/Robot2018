package org.usfirst.frc.team686.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.subsystems.ElevatorArmBar.ElevatorArmBarStateEnum;




public class CenterToSwitchMode extends AutoModeBase 
{
	boolean rightSwitch;
	Path path;
	
    public CenterToSwitchMode(boolean _isRight) 
    {
    	rightSwitch = _isRight;
    }
    
    // called by AutoModeExecuter.start() --> AutoModeBase.run()
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("STARTING AUTOMODE: Center To Switch");
   	 
    	// TODO: need longer lookahead distance for smoother driving?
    	PathSegment.Options pathOptions      = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
    	PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);

		Vector2d initialPosition = FieldDimensions.getCenterStartPose().getPosition();

		// TODO: do a little math to figure out these points, especially the final position using kCenterToFrontBumper
		Vector2d waypoint1 = new Vector2d(115-12, 58.44);	// last foot will have collision detection
		Vector2d waypoint2 = new Vector2d(115   , 58.44);	// shooting position 
		
		if (rightSwitch)
		{
			// negate Y coordinates if going right
			waypoint1.setY(-waypoint1.getY());
			waypoint2.setY(-waypoint2.getY());
		}
		
		
		// drive almost all the way to the shooting position
		Path mainPath = new Path(Constants.kCollisionVel);
		mainPath.add(new Waypoint(initialPosition, 	pathOptions));
		mainPath.add(new Waypoint(waypoint1,  		pathOptions));

		// TODO: add ability to finish PathFollower at a speed higher than 0

		// finish drive with collision detection
		Path collsionPath = new Path();
		collsionPath.add(new Waypoint(waypoint1, collisionOptions));
		collsionPath.add(new Waypoint(waypoint2, collisionOptions));
		
		// drive close
		runAction( new PathFollowerWithVisionAction( mainPath ));
		// prepare to shoot (simultaneously raise elevator, while driving forward to touch switch wall) 
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(), new PathFollowerWithVisionAction(collsionPath))
				})));
		// shoot!
		runAction( new OuttakeAction() );
    }
}