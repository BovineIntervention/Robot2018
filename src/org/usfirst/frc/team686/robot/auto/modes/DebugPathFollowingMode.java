package org.usfirst.frc.team686.robot.auto.modes;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Pose;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;



/**
 * Go over the defenses in the starting configuration, then launch one ball (in
 * the robot at start)
 */
public class DebugPathFollowingMode extends AutoModeBase 
{
	// initialPose in inherited from AutoModeBase
	List<Path> pathList;
	
	Vector2d waypoint0;
	Vector2d waypoint1;
	Vector2d waypoint2;
	Vector2d waypoint3;
	Vector2d waypoint4;
	
    public DebugPathFollowingMode() 
    {
    	initialPose = new Pose(0, 0, 0*Pose.degreesToRadians);
    	
    	waypoint0 = initialPose.getPosition();
    	waypoint1 = new Vector2d(48.0,  0.0);
    	waypoint2 = new Vector2d(96.0, -48.0);
//    	waypoint2 = new Vector2d(96.0, 0);
    	
    	waypoint3 = new Vector2d(96.0, 0.0);
    	waypoint4 = initialPose.getPosition();
    }
    
    private void init()
    {
       	double maxVelocity 		= 48;
       	double maxAccel    		= 48;
       	double lookaheadDist    = 24;
    	
    	PathSegment.Options pathOptions   = new PathSegment.Options(maxVelocity, maxAccel, lookaheadDist, false);
    	
		pathList = new ArrayList<Path>();

		// drive to target 1
		Path path0 = new Path();
		path0.add(new Waypoint(waypoint0, pathOptions));
		path0.add(new Waypoint(waypoint1, pathOptions));
		path0.add(new Waypoint(waypoint2, pathOptions));
		pathList.add(path0);

		// back up
		Path path1 = new Path();
		path1.add(new Waypoint(waypoint2, pathOptions));
		path1.add(new Waypoint(waypoint3, pathOptions));
		path1.add(new Waypoint(waypoint4, pathOptions));
		path1.setReverseDirection();
		pathList.add(path1);
   }

    // called by AutoModeExecuter.start() --> AutoModeBase.run()
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("Starting DebugPathFollowingMode");

    	// generate pathList 
    	init();

    	// execute each path, in sequence
    	for (Path path : pathList)
    		runAction( new PathFollowerWithVisionAction( path ) );   
    		
    }
    
}
