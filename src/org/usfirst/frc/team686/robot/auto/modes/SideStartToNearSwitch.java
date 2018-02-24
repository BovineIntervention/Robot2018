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




public class SideStartToNearSwitch extends AutoModeBase 
{
	boolean rightSwitch;
	Path path;
	
    public SideStartToNearSwitch(boolean _isRight) 
    {
    	rightSwitch = _isRight;
    }
    
    // called by AutoModeExecuter.start() --> AutoModeBase.run()
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("STARTING AUTOMODE: Side To Switch");
   	 
    	// TODO: need longer lookahead distance for smoother driving?
    	PathSegment.Options pathOptions      = new PathSegment.Options(Constants.kPathFollowingMaxVel, Constants.kPathFollowingMaxAccel, Constants.kPathFollowingLookahead, false);
    	PathSegment.Options collisionOptions = new PathSegment.Options(Constants.kCollisionVel, Constants.kCollisionAccel, Constants.kPathFollowingLookahead, false);

		Vector2d initialPosition = FieldDimensions.getLeftStartPose().getPosition();

		
		// TODO: do a little math to figure out these points, especially the final position using kCenterToFrontBumper
		Vector2d waypoint1 = new Vector2d(120, 132);	// crossing auto line
		Vector2d waypoint2 = new Vector2d(168, 132);	// center of switch
		Vector2d waypoint3 = new Vector2d(168, 101+12);	// last foot will have collision detection
		Vector2d waypoint4 = new Vector2d(168, 101);	// shooting position
		
		if (rightSwitch)
		{
			initialPosition = FieldDimensions.getRightStartPose().getPosition();
			// negate Y coordinates if going right
			waypoint1.setY(-waypoint1.getY());
			waypoint2.setY(-waypoint2.getY());
			waypoint3.setY(-waypoint3.getY());
			waypoint4.setY(-waypoint4.getY());
		}
		
		
		// drive almost all the way to the shooting position
		Path mainPath = new Path(Constants.kCollisionVel);
		mainPath.add(new Waypoint(initialPosition, pathOptions));
		mainPath.add(new Waypoint(waypoint1,  	pathOptions));
		mainPath.add(new Waypoint(waypoint2,  	pathOptions));
		mainPath.add(new Waypoint(waypoint3,  	pathOptions));

		// TODO: add ability to finish PathFollower at a speed higher than 0

		// finish drive with collision detection
		Path collisionPath = new Path();
		collisionPath.add(new Waypoint(waypoint3, collisionOptions));
		collisionPath.add(new Waypoint(waypoint4, collisionOptions));
		
		// drive close
		runAction( new PathFollowerWithVisionAction( mainPath ));
		// prepare to shoot (simultaneously raise elevator, while driving forward to touch switch wall) 
		runAction( new ParallelAction(Arrays.asList(new Action[] {
				new ElevatorAction(ElevatorArmBarStateEnum.SWITCH),
				new InterruptableAction(new CollisionDetectionAction(), new PathFollowerWithVisionAction(collisionPath))
				})));
		// shoot!
		runAction( new OuttakeAction() );
    }
}