/************************ PROJECT PHIL ************************/
/* Copyright (c) 2022 StuyPulse Robotics. All rights reserved.*/
/* This work is licensed under the terms of the MIT license.  */
/**************************************************************/

package com.stuypulse.robot.constants;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.stuypulse.stuylib.network.SmartBoolean;
import com.stuypulse.stuylib.network.SmartNumber;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;

/*-
 * File containing tunable settings for every subsystem on the robot.
 *
 * We use StuyLib's SmartNumber / SmartBoolean in order to have tunable
 * values that we can edit on Shuffleboard.
 */
public interface Settings {
    int UPDATE_RATE = 200;
	double dT = 1.0 / UPDATE_RATE;

	public interface Swerve {
		double WIDTH = Units.inchesToMeters(28.75);
		double HEIGHT = Units.inchesToMeters(19.75);
		
		public interface Encoder {
			public interface Drive {
				double WHEEL_DIAMETER = Units.inchesToMeters(4)*7.36/7.65;
				double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;
				double GEAR_RATIO = 6.75;
				
				double POSITION_CONVERSION = WHEEL_CIRCUMFERENCE / GEAR_RATIO;
				double VELOCITY_CONVERSION = POSITION_CONVERSION / 60.0;
			}
	
			public interface Turn {
                // if changing units please double check the getAbsolutePosition() method to make sure unit conversions are correct
                // TODO: make it so that you don't have to check there
				double POSITION_CONVERSION = 0.087890625; // exact default value from api, converts to degrees
                String UNIT_STRING = "deg";
                SensorTimeBase SENSOR_TIME_BASE = SensorTimeBase.PerSecond;
                boolean SENSOR_DIRECTION = false; // default, positive rotation counter-clockwise when observer is facing the LED side
                AbsoluteSensorRange ABSOLUTE_SENSOR_RANGE = AbsoluteSensorRange.Signed_PlusMinus180;
	
                // I have no idea where this is used?
				double MIN_PID_INPUT = 0;
				double MAX_PID_INPUT = POSITION_CONVERSION;
			}
		}

		public interface FrontRight {
            String ID = "Front Right";
            //Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(174).plus(Rotation2d.fromDegrees(0));
            Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(85.0782);
            Translation2d MODULE_OFFSET = new Translation2d(WIDTH * +0.5, HEIGHT * -0.5);
        }

        public interface FrontLeft {
            String ID = "Front Left";
            //Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(-131).plus(Rotation2d.fromDegrees(270));
            Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(85.7812);
            Translation2d MODULE_OFFSET = new Translation2d(WIDTH * +0.5, HEIGHT * +0.5);
        }

        public interface BackLeft {
            String ID = "Back Left";
            //Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(119).plus(Rotation2d.fromDegrees(180));
            Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(-96.9433);
            Translation2d MODULE_OFFSET = new Translation2d(WIDTH * -0.5, HEIGHT * +0.5);
        }

        public interface BackRight {
            String ID = "Back Right";
            //Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(-2).plus(Rotation2d.fromDegrees(90));
            Rotation2d ABSOLUTE_OFFSET = Rotation2d.fromRotations(-162.9492);
            Translation2d MODULE_OFFSET = new Translation2d(WIDTH * -0.5, HEIGHT * -0.5);
        }
	}
}
