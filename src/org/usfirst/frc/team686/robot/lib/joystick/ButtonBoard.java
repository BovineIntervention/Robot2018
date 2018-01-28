package org.usfirst.frc.team686.robot.lib.joystick;

import java.util.Optional;

import edu.wpi.first.wpilibj.Joystick;

/**
 * An abstract class that forms the base of joystick controls.
 */
public class ButtonBoard 
{
    private static ButtonBoard mInstance = new ButtonBoard();
    public static ButtonBoard getInstance() { return mInstance; }

    protected final Joystick mStick;

    protected ButtonBoard() 
    {
        mStick = new Joystick(1);
    }

    public boolean getButton(int _num) { return mStick.getRawButton(_num); }

    public Optional<Double> getDirection() 
    {
    	double vert = -mStick.getY();
    	double horiz = -mStick.getX();
    	double midPoint = 0.5;
    	
    	Optional<Double> directionDeg = Optional.empty();
    	if (vert > midPoint && Math.abs(horiz) < midPoint)
    		directionDeg = Optional.of(0.0);
    	else if (vert < -midPoint && Math.abs(horiz) < midPoint)
    		directionDeg = Optional.of(180.0);
    	else if (horiz > midPoint && Math.abs(vert) < midPoint)
    		directionDeg = Optional.of(90.0);
    	else if (horiz < -midPoint && Math.abs(vert) < midPoint)
    		directionDeg = Optional.of(-90.0);
   		
    	return directionDeg;
    }    
}
