/************************ PROJECT PHIL ************************/
/* Copyright (c) 2022 StuyPulse Robotics. All rights reserved.*/
/* This work is licensed under the terms of the MIT license.  */
/**************************************************************/

package com.stuypulse.robot.constants;

import com.revrobotics.CANSparkMax.IdleMode;
import com.stuypulse.robot.util.SparkMaxConfig;

/*-
 * File containing all of the configurations that different motors require.
 *
 * Such configurations include:
 *  - If it is Inverted
 *  - The Idle Mode of the Motor
 *  - The Current Limit
 *  - The Open Loop Ramp Rate
 */
public interface Motors {
	SparkMaxConfig DRIVE = new SparkMaxConfig(false, IdleMode.kBrake, 40, 0);
	SparkMaxConfig TURN  = new SparkMaxConfig(true, IdleMode.kBrake, 20, 0);
}
