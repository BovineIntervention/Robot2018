package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Superstructure;



public class GrabberCloseAction implements Action {
	Superstructure superstructure = Superstructure.getInstance();

	
	public GrabberCloseAction() {
		}
	
	
	@Override
	public void start() {
		superstructure.grabberIn();
		
}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void update() {
		
	}

	@Override
	public void done() {
	}

	private final DataLogger logger = new DataLogger()
    {
        @Override
        public void log()
        {
	    }
    };
	
	@Override
	public DataLogger getLogger() { return logger; }

}
