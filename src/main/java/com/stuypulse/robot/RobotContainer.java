/************************ PROJECT PHIL ************************/
/* Copyright (c) 2022 StuyPulse Robotics. All rights reserved.*/
/* This work is licensed under the terms of the MIT license.  */
/**************************************************************/

package com.stuypulse.robot;

import com.stuypulse.robot.commands.DynamicTest;
import com.stuypulse.robot.commands.QuasistaticTest;
import com.stuypulse.robot.subsystems.Logger;
import com.stuypulse.robot.subsystems.VoltageSwerve;
import com.stuypulse.stuylib.network.SmartNumber;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class RobotContainer {

    // Subsystem
    public VoltageSwerve swerve = new VoltageSwerve();
    public Logger logger = new Logger(swerve);

    // Autons
    private static SendableChooser<Command> autonChooser = new SendableChooser<>();

    private static SmartNumber rampRate = new SmartNumber("Quasistatic Ramp Rate (V per s)", 0.25);
    private static SmartNumber dynamic = new SmartNumber("Dynamic Step Voltage (V)", 7);

    // Robot container

    public RobotContainer() {
        configureDefaultCommands();
        configureButtonBindings();
        configureAutons();
    }

    /****************/
    /*** DEFAULTS ***/
    /****************/

    private void configureDefaultCommands() {}

    /***************/
    /*** BUTTONS ***/
    /***************/

    private void configureButtonBindings() {}

    /**************/
    /*** AUTONS ***/
    /**************/

    public void configureAutons() {
        autonChooser.setDefaultOption("Quasistatic Forward", new QuasistaticTest(this, rampRate).forward());
        autonChooser.addOption("Quasistatic Backward", new QuasistaticTest(this, rampRate).reverse());
        autonChooser.addOption("Dynamic Forward", new DynamicTest(this, dynamic).forward());
        autonChooser.addOption("Dynamic Backward", new DynamicTest(this, dynamic).reverse());

        SmartDashboard.putData("Autonomous", autonChooser);
    }

    public Command getAutonomousCommand() {
        return autonChooser.getSelected();
    }
}

