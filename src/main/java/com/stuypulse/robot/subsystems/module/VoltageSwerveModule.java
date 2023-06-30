package com.stuypulse.robot.subsystems.module;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.stuypulse.robot.constants.Motors;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.constants.Settings.Swerve.Encoder;
import com.stuypulse.robot.subsystems.SwerveModule;
import com.stuypulse.stuylib.control.angle.AngleController;
import com.stuypulse.stuylib.control.angle.feedback.AnglePIDController;
import com.stuypulse.stuylib.math.Angle;
import com.stuypulse.stuylib.network.SmartNumber;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VoltageSwerveModule extends SubsystemBase implements SwerveModule {

    private interface Turn {
        double kP = .00374;
        double kI = 0.0;
        double kD = 0.2;
    }
    SmartNumber turnP = new SmartNumber("turnP", Turn.kP);
    SmartNumber turnI = new SmartNumber("turnI", Turn.kI);
    SmartNumber turnD = new SmartNumber("turnD", Turn.kD);

    SmartNumber target = new SmartNumber("Target Deg", 0);

    // module data
    private String id;
    private Translation2d location;
    private Rotation2d angleOffset;

    // turn
    private CANSparkMax turnMotor;
    private CANCoder absoluteEncoder;

    // drive
    private CANSparkMax driveMotor;
    private RelativeEncoder driveEncoder;

    // controller
    private AnglePIDController turnPID;

    private double voltage;

    public VoltageSwerveModule(String id, Translation2d location, int turnCANId, int turnEncoderId,
            Rotation2d angleOffset, int driveCANId) {

        // module data
        this.id = id;
        this.location = location;
        this.angleOffset = angleOffset;

        // turn
        turnMotor = new CANSparkMax(turnCANId, MotorType.kBrushless);
        turnPID = new AnglePIDController(Turn.kP, Turn.kI, Turn.kD);
        absoluteEncoder = new CANCoder(turnEncoderId);
        configureTurnMotor(angleOffset);

        // drive
        driveMotor = new CANSparkMax(driveCANId, MotorType.kBrushless);
        configureDriveMotor();
    }

    private void configureTurnMotor(Rotation2d angleOffset) {
        turnMotor.restoreFactoryDefaults();
        
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.sensorCoefficient = Encoder.Turn.POSITION_CONVERSION;
        config.unitString = Encoder.Turn.UNIT_STRING;
        config.sensorTimeBase = Encoder.Turn.SENSOR_TIME_BASE;
        config.absoluteSensorRange = Encoder.Turn.ABSOLUTE_SENSOR_RANGE;
        config.sensorDirection = Encoder.Turn.SENSOR_DIRECTION;
        absoluteEncoder.configAllSettings(config);

        turnMotor.enableVoltageCompensation(12);

        Motors.TURN.config(turnMotor);
    }

    private void configureDriveMotor() {
        driveMotor.restoreFactoryDefaults();
        
        driveEncoder = driveMotor.getEncoder();
        driveEncoder.setPositionConversionFactor(Encoder.Drive.POSITION_CONVERSION);
        driveEncoder.setVelocityConversionFactor(Encoder.Drive.VELOCITY_CONVERSION);
        
        driveMotor.enableVoltageCompensation(12.0);
        Motors.DRIVE.config(driveMotor);
        driveEncoder.setPosition(0);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Translation2d getLocation() {
        return location;
    }

    @Override
    public double getVelocity() {
        return driveEncoder.getVelocity();
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(getVelocity(), getRotation2d());
    }

    @Override
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(getDistance(), getRotation2d());
    }

    private Rotation2d getAbsolutePosition() {
        return Rotation2d.fromDegrees(absoluteEncoder.getAbsolutePosition());
    }

    private Rotation2d getRotation2d() {
        return getAbsolutePosition().minus(angleOffset);
    }

    @Override
    public double getDistance() {
        return driveEncoder.getPosition();
    }

    @Override
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public void periodic() {
        turnPID.setP(turnP.get());
        turnPID.setI(turnI.get());
        turnPID.setD(turnD.get());

        double turnVoltage = turnPID.update(Angle.fromDegrees(target.get()), Angle.fromRotation2d(getRotation2d()));
        turnMotor.set(turnVoltage);
        driveMotor.setVoltage(voltage);

        SmartDashboard.putNumber(id + "/Angle Deg", getRotation2d().getDegrees());
        //SmartDashboard.putNumber(id + "/Absolute Angle Deg", getAbsolutePosition().getDegrees());

        SmartDashboard.putNumber(id + "/Velocity", getVelocity());

        SmartDashboard.putNumber(id + "/Drive Voltage", voltage);
        SmartDashboard.putNumber(id + "/Turn Voltage", turnVoltage);
    }

}
