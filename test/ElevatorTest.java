
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import org.usfirst.frc.team686.robot.Constants;
import org.usfirst.frc.team686.robot.loop.ElevatorLoop;


public class ElevatorTest {

    //ELEVATOR TEST
    public static double kVoltage = 12.0;
    
    public static double kStallTorque = 2.402; //Stall Torque in N*m
    public static double kStallCurrent = 126.145; //Stall Current in Amps
    public static double kFreeSpeed = 5015.562; //Free Speed in RPM
    public static double kFreeCurrent = 1.170; //Free Current in Amps
    public static double kMass = 20.0; //Mass of the Elevator
    
    public static double kNumMotors = 2.0; //Number of motors
    public static double kResistance = kVoltage/kStallCurrent; //Resistance of motor
    public static double Kv = ((kFreeSpeed / 60) * (2.0 * Math.PI)) / 
    							(kVoltage - kResistance * kFreeCurrent); //Motor velocity constant (kFreeSpeed converted to rad/s)
    public static double Kt = (kNumMotors * kStallTorque) / kStallCurrent; //Torque constant
    public static double kG = 5; //Gear ratio
    public static double kr = 17 * 0.25 * 0.0254 / Math.PI / 2.0; //radius of pulley
    
    public static double kDt = 0.010; //Control loop time step
    public static double kZeroingVelocity = 0.05; //zeroing velocity in m/s
    public static double kMaxHeight = 0.45;
    public static double kMinHeight = -0.45;
    public static double kElevatorOffset = 0.0; //encoder + offset = absolute position
	
    
	
	public static double kElevatorStallTorque = 2.402; //Stall Torque in N*m
    public static double kElevatorStallCurrent = 126.145; //Stall Current in Amps
    public static double kElevatorFreeSpeed = 5015.562; //Free Speed in RPM
    public static double kElevatorFreeCurrent = 1.170; //Free Current in Amps
    public static double kElevatorMass = 20.0; //Mass of the Elevator
    
    public static double kElevatorNumMotors = 2.0; //Number of motors
    public static double kElevatorResistance = Constants.kMaxBatteryVoltage/kElevatorStallCurrent; //Resistance of motor
    public static double KMotorVelocity = ((kElevatorFreeSpeed / 60) * (2.0 * Math.PI)) / 
    							(Constants.kMaxBatteryVoltage - kElevatorResistance * kElevatorFreeCurrent); //Motor velocity constant (kFreeSpeed converted to rad/s)
    public static double KElevatorTorque = (kElevatorNumMotors * kElevatorStallTorque) / kElevatorStallCurrent; //Torque constant
    public static double kElevatorGearRatio = 5; //Gear ratio
    public static double kElevatorPulleyRadius = 17 * 0.25 * 0.0254 / Math.PI / 2.0; //radius of pulley
    

    
    
	public ElevatorTest(){
		
	}

	
    public static double getAcceleration (double voltage){
		return (-ElevatorTest.KElevatorTorque * ElevatorTest.kElevatorGearRatio * ElevatorTest.kElevatorGearRatio
				/ (ElevatorTest.KMotorVelocity * ElevatorTest.kElevatorResistance * ElevatorTest.kElevatorPulleyRadius * ElevatorTest.kElevatorPulleyRadius * ElevatorTest.kElevatorMass) * 
				velocity_ + ElevatorTest.kElevatorGearRatio * ElevatorTest.KElevatorTorque / 
				(ElevatorTest.kElevatorResistance * ElevatorTest.kElevatorPulleyRadius * ElevatorTest.kElevatorMass) * voltage);
	}

    public static final double initialPosition = 0.12;	// initial position (initially unknown to encoder, elevator)
    public static double position_ = initialPosition;	// actual position
    public static double velocity_ = 0;					// actual velocity
    
    public static double getEncoder() { return position_ - initialPosition; }	// encoder is offset by the initialPosition.  The elevator will need to learn this and calibrate it out
    public static boolean getLimitTriggered(){ return position_ > -0.04 && position_ < 0.0; }	// limit switch triggered when near magnet

    
    public static void SimulateTime(double voltage, double time)
    {
    	// simulate physics of mechanism over the robot's time step
    	double kSimTime = 0.0001;	// simulate physics at a higher time resolution
    	double current_time = 0.0;
    	
		// check that voltage has been clipped to +/-12V
		assertTrue("voltage level is out of range: " + voltage, -Constants.kMaxBatteryVoltage <= voltage && voltage <= Constants.kMaxBatteryVoltage);

    	while (current_time < time)
    	{
    		position_ += kSimTime * velocity_;
    		velocity_ += kSimTime * getAcceleration(voltage);
    		current_time += kSimTime;
    		
    		// verify that elevator isn't going downwards too fast when it hits the limit switch
    		// (allow it to go fast upwards)
    		if (getLimitTriggered())
    		{ 
				assertTrue("VELOCITY: " + velocity_, velocity_ > -Constants.kElevatorZeroingVelocity);	
    		}
    	}
    	
    }
    
	@Test
	public void test() throws FileNotFoundException, UnsupportedEncodingException 
	{
    	ElevatorLoop elevator = new ElevatorLoop();
    	double testGoal = 0.5;
    	double testGoalTolerance = 0.01;
    	
    	elevator.setGoal(testGoal);
    	
    	String file = "ElevatorTest.csv";
    	PrintWriter writer = new PrintWriter(file, "UTF-8");
    	writer.println("time,state,voltage,position,velocity,goal,error");
    	
    	try
    	{
    		
	    	// run elevator for 10 seconds -- enough time to zero out, then move to goal position
			double current_time = 0.0;
			while (current_time < 10.0) 
			{
				// get voltage to be applied to the elevator (based on it's current state and goal)
				double voltage = elevator.Update(getEncoder() , getLimitTriggered(), true);
							
				// simulate what physics of mechanism will do with current settings over a kLoopDt time period
				SimulateTime(voltage, Constants.kLoopDt);
				current_time += Constants.kLoopDt;
				
				// write results to file for analysis
				writer.printf("%f,%d,%f,%f,%f,%f,%f\n", current_time, elevator.getState().ordinal(), voltage, position_, velocity_, elevator.getFilteredGoal(), elevator.lastError);
			}

			// verify that elevator ended up at goal position
			assertEquals(testGoal, position_, testGoalTolerance);
			
    	}
    	finally
    	{
    		writer.close();
    	}
    	
	}
	
}
