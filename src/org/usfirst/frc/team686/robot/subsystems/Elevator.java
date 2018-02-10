package org.usfirst.frc.team686.robot.subsystems;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.loop.ElevatorLoop;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;


public class Elevator extends Subsystem {


	static DigitalInput hallEffect;
	private Talon motor;
	private TalonControlMode motorControlMode;
	private static ElevatorLoop elevator;
	public static double initialPosition;
    
	public Elevator(){
		
		hallEffect = new DigitalInput(1);
		motor = new Talon(Constants.kElevatorMotorId);
		motorControlMode = TalonControlMode.Voltage;
		elevator = new ElevatorLoop();
		initialPosition = motor
	}

	
    public static double getAcceleration (double voltage){
		return (-Constants.KElevatorTorque * Constants.kElevatorGearRatio * Constants.kElevatorGearRatio
				/ (Constants.KMotorVelocity * Constants.kElevatorResistance * Constants.kElevatorPulleyRadius * Constants.kElevatorPulleyRadius * Constants.kElevatorMass) * 
				velocity_ + Constants.kElevatorGearRatio * Constants.KElevatorTorque / 
				(Constants.kElevatorResistance * Constants.kElevatorPulleyRadius * Constants.kElevatorMass) * voltage);
	} 
    
    public static double getEncoder() { return position_ - initialPosition; }	// encoder is offset by the initialPosition.  The elevator will need to learn this and calibrate it out
    public static boolean getLimitTriggered(){ return !hallEffect.get(); }	

    public static void setGoal(double _goal){ elevator.setGoal(_goal); }
    

	public void update() throws FileNotFoundException, UnsupportedEncodingException 
	{
	
		// get voltage to be applied to the elevator (based on it's current state and goal)
		double voltage = elevator.Update(getEncoder() , getLimitTriggered(), true);
		
		motor.set(TalonControlMode.Voltage, voltage);
    	
	}
	
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zeroSensors() {
		// TODO Auto-generated method stub
		
	}

}
