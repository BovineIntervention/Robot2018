
public class ElevatorTest {

	
    double kVoltage = 12.0;
    
    double kStallTorque = 2.402; //Stall Torque in N*m
    double kStallCurrent = 126.145; //Stall Current in Amps
    double kFreeSpeed = 5015.562; //Free Speed in RPM
    double kFreeCurrent = 1.170; //Free Current in Amps
    double kMass = 20.0; //Mass of the Elevator
    
    double kNumMotors = 2.0; //Number of motors
    double kResistance = kVoltage/kStallCurrent; //Resistance of motor
    double Kv = ((kFreeSpeed / 60) * (2.0 * Math.PI)) / 
    							(kVoltage - kResistance * kFreeCurrent); //Motor velocity constant (kFreeSpeed converted to rad/s)
    double Kt = (kNumMotors * kStallTorque) / kStallCurrent; //Torque constant
    double kG = 5; //Gear ratio
    double kr = 17 * 0.25 * 0.0254 / Math.PI / 2.0; //radius of pulley
    
    double kDt = 0.010; //Control loop time step
	
    public double getAcceleration (double voltage){
		return(-Kt * kG * kG / (Kv * kResistance * kr * kr * kMass) * velocity_ + kG * Kt / 
				(kResistance * kr * kMass) * voltage);
	}
    
    double position_ = 0.0;
    double velocity_ = 0;
    public boolean limit_triggered(){ return position_ > -0.04 && position_ < 0.0; }

    public void SimulateTime(double time){
    	double kSimTime = 0.0001;
    	double current_time = 0.0;
    	while (current_time < time){
    		position_ += kSimTime * velocity_;
    		velocity_ += kSimTime * getAcceleration(kVoltage);
    		current_time += kSimTime;	
    	}
    	
    }
	
    @Test
	public void test() {
		ElevatorCommand elevator;
		double current_time = 0.0;
		while (current_time < 1.0) {
			double voltage = elevator.getVoltage()
		}
	}
}

