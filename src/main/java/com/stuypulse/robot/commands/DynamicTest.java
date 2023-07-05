package com.stuypulse.robot.commands;

import com.stuypulse.robot.RobotContainer;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DynamicTest extends CommandBase {

	private final RobotContainer robot;
	private final Number volts;
	private boolean forward;

	public DynamicTest(RobotContainer robot, Number volts) {
		this.robot = robot;
		this.volts = volts;

		forward();
	}

	public DynamicTest forward() {
		forward = true;
		return this;
	}

	public DynamicTest reverse() {
		forward = false;
		return this;
	}

	private double getVolts() {
		return forward ? volts.doubleValue() : -volts.doubleValue();
	}

	@Override
	public void initialize() {
		robot.logger.clear();
	}

	@Override
	public void execute() {
		robot.swerve.setVoltage(getVolts());
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void end(boolean interrupted) {
		robot.swerve.setVoltage(0.0);

		robot.logger.publish("fast", forward);
	}

}
