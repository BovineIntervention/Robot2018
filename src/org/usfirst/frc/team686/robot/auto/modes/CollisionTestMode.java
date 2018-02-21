package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;


/**
 * Go over the defenses in the starting configuration, then launch one ball (in
 * the robot at start)
 */
public class CollisionTestMode extends AutoModeBase {

	Path path;
    public CollisionTestMode() 
    {
 
    }
    
    private void init(){
    	
    	double collisionVelocity = 24;
    	double collisionAccel = 12;
    	PathSegment.Options options = new PathSegment.Options(collisionVelocity, collisionAccel, Constants.kPathFollowingLookahead, false);
    	
    	path = new Path();
    	path.add(new Waypoint(new Vector2d( 0, 0), options));
        path.add(new Waypoint(new Vector2d( 240.0, 0), options));
    }

    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("Starting Auto Mode: Collision Test");

    	init();
    	
        runAction(new InterruptableAction(new CollisionDetectionAction(), new PathFollowerWithVisionAction(path)));			
    }
}
