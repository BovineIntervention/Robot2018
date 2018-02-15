package org.usfirst.frc.team686.robot.auto.modes;

import org.usfirst.frc.team686.robot.auto.AutoModeBase;
import org.usfirst.frc.team686.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team686.robot.auto.actions.AutoActions;
import org.usfirst.frc.team686.robot.auto.actions.SeriesAction;
import org.usfirst.frc.team686.robot.lib.util.Pose;

/**
 * Fallback for when all autonomous modes do not work, resulting in a robot
 * standstill
 */
public class RunSeriesActionMode extends AutoModeBase {
	
	SeriesAction autoActions;

	public RunSeriesActionMode( SeriesAction _autoActions ){
		autoActions = _autoActions;
	}
	
    @Override
    protected void routine() throws AutoModeEndedException {
        
    	runAction( autoActions );
    	
    }
    
    @Override
    public Pose getInitialPose()
    {
    	return AutoActions.getInitialPose();
    }
    
    
}