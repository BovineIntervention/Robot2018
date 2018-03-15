package org.usfirst.frc.team686.robot.auto.actions;

import org.usfirst.frc.team686.robot.lib.util.DataLogger;
import org.usfirst.frc.team686.robot.subsystems.Superstructure;

import edu.wpi.first.wpilibj.Timer;

public class GrabberOpenAction implements Action {
	Superstructure superstructure = Superstructure.getInstance();

	
	public GrabberOpenAction() {
		}
	
	
	@Override
	public void start() {
		superstructure.grabberOut();
		
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
