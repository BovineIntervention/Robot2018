
public class ElevatorTest
{
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

}
