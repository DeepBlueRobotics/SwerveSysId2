package com.stuypulse.robot.commands;

import com.stuypulse.robot.RobotContainer;
import com.stuypulse.robot.constants.Settings;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class QuasistaticTest extends CommandBase {
	
	private final RobotContainer robot;

	private Number rampRate;

	private double volts;

	private boolean forward;

	public QuasistaticTest(RobotContainer robot, Number rampRate) {
		this.robot = robot;
		this.rampRate = rampRate;

		forward();
	}

	public QuasistaticTest forward() {
		forward = true;
		return this;
	}

	public QuasistaticTest reverse() {
		forward = false;
		return this;
	}

	@Override
	public void initialize() {
		robot.logger.clear();

		volts = 0;
	}

	@Override
	public void execute() {
		robot.swerve.setVoltage(volts);

		if (forward)
			volts += rampRate.doubleValue() * Settings.dT;
		else
			volts -= rampRate.doubleValue() * Settings.dT;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void end(boolean interrupted) {
		robot.swerve.setVoltage(0.0);

		robot.logger.publish("slow", forward);
	}
	
}
