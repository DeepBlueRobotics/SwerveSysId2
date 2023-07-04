package com.stuypulse.robot.subsystems;

import static com.stuypulse.robot.constants.Settings.Swerve.*;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;
import com.stuypulse.robot.Robot;
import com.stuypulse.robot.constants.Ports;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.subsystems.module.SimVoltageSwerveModule;
import com.stuypulse.robot.subsystems.module.VoltageSwerveModule;
import com.stuypulse.stuylib.math.Angle;
import com.stuypulse.stuylib.util.AngleVelocity;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VoltageSwerve extends SubsystemBase {

	// Subsystems
	private final SwerveModule[] modules;

	private final SwerveDriveKinematics kinematics;
	private final SwerveDriveOdometry odometry;

	// Sensors
	private final AHRS gyro;
	private final AngleVelocity angleFilter;
	private double angularVelocity;

	private double leftVoltage, rightVoltage;

	private Field2d field;

	public VoltageSwerve() {
		if (Robot.isReal()) {
			modules = new SwerveModule[] {
				new VoltageSwerveModule(FrontRight.ID, FrontRight.MODULE_OFFSET, Ports.Swerve.FrontRight.TURN, Ports.Swerve.FrontRight.TURN_ENCODER, FrontRight.ABSOLUTE_OFFSET, Ports.Swerve.FrontRight.DRIVE, FrontRight.pidConsts),
				new VoltageSwerveModule(FrontLeft.ID, FrontLeft.MODULE_OFFSET, Ports.Swerve.FrontLeft.TURN, Ports.Swerve.FrontLeft.TURN_ENCODER, FrontLeft.ABSOLUTE_OFFSET, Ports.Swerve.FrontLeft.DRIVE, FrontLeft.pidConsts),
				new VoltageSwerveModule(BackLeft.ID, BackLeft.MODULE_OFFSET, Ports.Swerve.BackLeft.TURN, Ports.Swerve.BackLeft.TURN_ENCODER, BackLeft.ABSOLUTE_OFFSET, Ports.Swerve.BackLeft.DRIVE, BackLeft.pidConsts),
				new VoltageSwerveModule(BackRight.ID, BackRight.MODULE_OFFSET, Ports.Swerve.BackRight.TURN, Ports.Swerve.BackRight.TURN_ENCODER, BackRight.ABSOLUTE_OFFSET, Ports.Swerve.BackRight.DRIVE, BackRight.pidConsts)
			};
		} else {
			modules = new SwerveModule[] {
				new SimVoltageSwerveModule(FrontRight.ID, FrontRight.MODULE_OFFSET),
				new SimVoltageSwerveModule(FrontLeft.ID, FrontLeft.MODULE_OFFSET),
				new SimVoltageSwerveModule(BackLeft.ID, BackLeft.MODULE_OFFSET),
				new SimVoltageSwerveModule(BackRight.ID, BackRight.MODULE_OFFSET)
			};
		}

		gyro = new AHRS(SPI.Port.kMXP);

		kinematics = new SwerveDriveKinematics(getModuleLocations());
		odometry = new SwerveDriveOdometry(kinematics, getRotation2d(), getModulePositions());

		angleFilter = new AngleVelocity();

		field = new Field2d();
		SmartDashboard.putData(field);
	}

	private Translation2d[] getModuleLocations() {
		return Arrays.stream(modules).map(x -> x.getLocation()).toArray(Translation2d[]::new);
	}

	private SwerveModuleState[] getModuleStates() {
		return Arrays.stream(modules).map(x -> x.getState()).toArray(SwerveModuleState[]::new);
	}

	private SwerveModulePosition[] getModulePositions() {
		return Arrays.stream(modules).map(x -> x.getPosition()).toArray(SwerveModulePosition[]::new);
	}

	public void setLeftVoltage(double voltage) {
		leftVoltage = voltage;
	}

	public double getLeftVoltage() {
		return leftVoltage;
	}

	// NOTE: might be incorrect if encoders don't both start at zero
	public double getLeftPosition() {
		return (modules[1].getDistance() + modules[2].getDistance()) / 2.0;
	}

	public double getLeftVelocity() {
		return (modules[1].getVelocity() + modules[2].getVelocity()) / 2.0;
	}

	public void setRightVoltage(double voltage) {
		rightVoltage = voltage;
	}

	public double getRightVoltage() {
		return rightVoltage;
	}

	// NOTE: might be incorrect if encoders don't both start at zero
	public double getRightPosition() {
		return (modules[0].getDistance() + modules[3].getDistance()) / 2.0;
	}

	public double getRightVelocity() {
		return (modules[0].getVelocity() + modules[3].getVelocity()) / 2.0;
	}

	// return ccw+ angle
	public Rotation2d getRotation2d() {
		return gyro.getRotation2d();
	}

	public double getAngularVelocity() {
		if (RobotBase.isReal())
			return Math.toRadians(gyro.getRate());
		else
			return angularVelocity;
	}


	@Override
	public void periodic() {
		odometry.update(getRotation2d(), getModulePositions());

		modules[0].setVoltage(rightVoltage);
		modules[1].setVoltage(leftVoltage);
		modules[2].setVoltage(leftVoltage);
		modules[3].setVoltage(rightVoltage);

		field.setRobotPose(odometry.getPoseMeters());

		SmartDashboard.putNumber("Swerve/Left Voltage", leftVoltage);
		SmartDashboard.putNumber("Swerve/Right Voltage", leftVoltage);
		SmartDashboard.putNumber("Swerve/Left Pos (rotations)", getLeftPosition());
		SmartDashboard.putNumber("Swerve/Left Vel (rotations per s)", getLeftVelocity());
		SmartDashboard.putNumber("Swerve/Right Pos (rotations)", getRightPosition());
		SmartDashboard.putNumber("Swerve/Right Vel (rotations per s)", getRightVelocity());
	}

	@Override
	public void simulationPeriodic() {
		var speeds = kinematics.toChassisSpeeds(getModuleStates());

        gyro.setAngleAdjustment(gyro.getAngle() - Math.toDegrees(speeds.omegaRadiansPerSecond * Settings.dT));

		angularVelocity = angleFilter.get(Angle.fromRotation2d(getRotation2d()));
	}

}
