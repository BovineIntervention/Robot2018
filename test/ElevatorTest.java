
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class ElevatorTest {

	public ElevatorTest(){
		
	}

	
    public static double getAcceleration (double voltage){
		return(-Constants.KElevatorTorque * Constants.kElevatorGearRatio * Constants.kElevatorGearRatio
				/ (Constants.KMotorVelocity * Constants.kElevatorResistance * Constants.kElevatorPulleyRadius * Constants.kElevatorPulleyRadius * Constants.kElevatorMass) * 
				velocity_ + Constants.kElevatorGearRatio * Constants.KElevatorTorque / 
				(Constants.kElevatorResistance * Constants.kElevatorPulleyRadius * Constants.kElevatorMass) * voltage);
	}
    
    public static double position_ = 0.1;
    public static double velocity_ = 0;
    public static double offset = 0.1;
    
    public static double getEncoder() { return position_ + offset; }
    public static boolean getLimitTriggered(){ return position_ > -0.04 && position_ < 0.0; }

    public static void SimulateTime(double voltage, double time){
    	assertTrue("voltage level is out of range: " + voltage, -Constants.kElevatorVoltage <= voltage && voltage <= Constants.kElevatorVoltage);
    	double kSimTime = 0.0001;
    	double current_time = 0.0;
    	while (current_time < time){
    		position_ += kSimTime * velocity_;
    		velocity_ += kSimTime * getAcceleration(voltage);
    		current_time += kSimTime;
    		if(getLimitTriggered()){ assertTrue("VELOCITY" + velocity_, velocity_ > -0.05); }
    	}
    	
    }
    
	@Test
	public void test() throws FileNotFoundException, UnsupportedEncodingException {
    	ElevatorLoop elevator = new ElevatorLoop();
    	elevator.setGoal(0.5);
    	String file = "C:\\Users\\susan\\Documents\\FIRST 2018\\testing\\ElevatorTest.csv";
    	PrintWriter writer = new PrintWriter(file, "UTF-8");//("C:\\Users\\team686\\Desktop\\Documents\\Robot2018Testing\\ElevatorTest.csv", "UTF-8");
    	writer.println("TIME,VOLTAGE,POSITION,VELOCITY,GOAL,ERROR");
		double current_time = 0.0;
		while (current_time < 10.0) {
			double voltage = elevator.Update(getEncoder() , getLimitTriggered(), true);
			assertTrue("voltage level is out of range: " + voltage, -Constants.kElevatorVoltage <= voltage && voltage <= Constants.kElevatorVoltage);
			SimulateTime(voltage, Constants.kElevatorDt);
			current_time += Constants.kElevatorDt;
			writer.printf("%f,%f,%f,%f,%f,%f%n", current_time, voltage, position_, velocity_, elevator.getFilteredGoal(), elevator.lastError);
		}
		writer.close();
		assertEquals(0.5, position_, 0.01);
	}
	
}
