package com.stuypulse.robot.subsystems;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Logger extends SubsystemBase {

	// 200 samples/s * 20 seconds * 9 doubles per sample
	private static final int LEN_VALUES = 36000;

	private final List<Double[]> values;
	private final VoltageSwerve swerve;

	public Logger(VoltageSwerve swerve) {
		values = new ArrayList<>(LEN_VALUES);
		this.swerve = swerve;
	}

	public void clear() {
		values.clear();
	}

	public void publish(String test, boolean forwards) {
		String path = test + (forwards ? "-forward" : "-backward");
		String data = values.stream().map(datapoint -> "[" + Arrays.stream(datapoint).map(Object::toString).collect(Collectors.joining(",")) + "]").collect(Collectors.joining(",\n"));
		values.clear();
		Path outFile = Path.of(System.getProperty("user.home"), "sysid-tests", path + ".json");
		try {
			Files.createDirectories(outFile.getParent());
			Files.deleteIfExists(outFile);
			Files.createFile(outFile);
			try(FileWriter writer = new FileWriter(outFile.toFile())) {
				writer.write(data);
				writer.flush();
			}
		} catch(IOException e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String str = writer.toString();
			System.err.println(str);
		}
		// SmartDashboard.putString(path, data);
	}

	@Override
	public void periodic() {
		values.add(new Double[] {Timer.getFPGATimestamp(),
		swerve.getLeftVoltage(),
		swerve.getRightVoltage(),
		swerve.getLeftPosition(),
		swerve.getRightPosition(),
		swerve.getLeftVelocity(),
		swerve.getRightVelocity(),
		swerve.getRotation2d().getDegrees() / 360.0,
		swerve.getAngularVelocity() / Math.PI / 2.0});
	}
}
