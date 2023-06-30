/************************ PROJECT PHIL ************************/
/* Copyright (c) 2022 StuyPulse Robotics. All rights reserved.*/
/* This work is licensed under the terms of the MIT license.  */
/**************************************************************/

package com.stuypulse.robot.constants;

/** This file contains the different ports of motors, solenoids and sensors */
public interface Ports {
    public interface Gamepad {
        int DRIVER = 0;
        int OPERATOR = 1;
        int DEBUGGER = 2;
    }

    public interface Swerve {
        public interface FrontRight {
            int DRIVE = 13;
            int TURN = 14;
            int TURN_ENCODER = 2;
        }

        public interface FrontLeft {
            int DRIVE = 8;
            int TURN = 7;
            int TURN_ENCODER = 1;
        }

        public interface BackRight{
            int DRIVE = 11;
            int TURN = 12;
            int TURN_ENCODER = 4;
        }

        public interface BackLeft{
            int DRIVE = 5;
            int TURN = 6; 
            int TURN_ENCODER = 3;
        }
    }

    public interface Arm {
        int SHOULDER_LEFT = -1;
        int SHOULDER_RIGHT =  -1;
        int WRIST = -1;
    }
}
