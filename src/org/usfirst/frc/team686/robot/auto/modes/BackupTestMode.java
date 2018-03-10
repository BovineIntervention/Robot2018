package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.*;
import org.usfirst.frc.team686.robot.lib.util.Path;
import org.usfirst.frc.team686.robot.lib.util.PathSegment;
import org.usfirst.frc.team686.robot.lib.util.Vector2d;
import org.usfirst.frc.team686.robot.lib.util.Path.Waypoint;

public class BackupTestMode extends AutoModeBase {

    public BackupTestMode() {}
    
    @Override
    protected void routine() throws AutoModeEndedException 
    {
    	System.out.println("Starting Auto Mode: Backup Test");

    	double velocity = 72;
    	double accel = velocity/0.5;
    	double lookahead = 48;
    	PathSegment.Options options = new PathSegment.Options(velocity, accel, lookahead, false);
    	
    	Vector2d begPos = new Vector2d(0,0);
    	Vector2d endPos = new Vector2d(72,0);
    	
    	Path path = new Path();
    	path.add(new Waypoint(begPos, options));
        path.add(new Waypoint(endPos, options));

    	Path backupPath = new Path();
    	backupPath.add(new Waypoint(endPos, options));
    	backupPath.add(new Waypoint(begPos, options));
        backupPath.setReverseDirection();
        
    	Path path2 = new Path();
    	path2.add(new Waypoint(begPos, options));
        path2.add(new Waypoint(endPos, options));

    	Path backupPath2 = new Path();
    	backupPath2.add(new Waypoint(endPos, options));
    	backupPath2.add(new Waypoint(begPos, options));
        backupPath2.setReverseDirection();
    	
		System.out.println("BackupTestMode path");
		System.out.println(path.toString());
		System.out.println(backupPath.toString());
        
        runAction(new PathFollowerAction(path));	
        runAction(new WaitAction(0.25));
        runAction(new PathFollowerAction(backupPath));			
        runAction(new WaitAction(0.25));
        runAction(new PathFollowerAction(path2));	
        runAction(new WaitAction(0.25));
        runAction(new PathFollowerAction(backupPath2));			
    }
}
