package org.usfirst.frc.team686.robot.subsystems;

import org.usfirst.frc.team686.robot.Constants;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class ArmBar extends Subsystem {
	
	TalonSRX armBarTalon;
	ControlMode armBarControlMode;
	DigitalInput forwardLimitSwitch;
	DigitalInput reverseLimitSwitch;
	Counter forwardCounter;
	Counter reverseCounter;
	
	public void intake(){
		armBarTalon = new TalonSRX(Constants.kArmBarTalonId);
		armBarControlMode = ControlMode.Velocity;
		
		armBarTalon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		armBarTalon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
	
		armBarTalon.overrideLimitSwitchesEnable(true);
		
		armBarTalon.getSensorCollection().isFwdLimitSwitchClosed();
		armBarTalon.getSensorCollection().isRevLimitSwitchClosed();
		
		armBarTalon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0, 10);
		
		forwardLimitSwitch = new DigitalInput(1);
		reverseLimitSwitch = new DigitalInput(2);
		
		forwardCounter = new Counter(forwardLimitSwitch);
		reverseCounter = new Counter(reverseLimitSwitch);
	}
	
	
	public void armUp(){
		armBarTalon.set(ControlMode.Velocity, Constants.kArmBarVelocity);
	}
	
	public void armDown(){
		armBarTalon.set(ControlMode.Velocity, -Constants.kArmBarVelocity);
	}
	
	public void armStop(){
		armBarTalon.set(ControlMode.Velocity, 0);
	}

	public boolean isForwardSwitchSet(){
		return forwardCounter.get() > 0;
	}
	
	public boolean isReverseSwitchSet(){
		return reverseCounter.get() > 0;
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
