// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2052.swerve;

import edu.wpi.first.math.util.Units;

/** 
 * Constants for swerve modules.
 */
public final class SwerveConstants {
    public static final double CAN_TIMEOUT_SECONDS = 0.25;

    public static final class SwerveModule {
        public static final int CIM_ROUNDS_PER_MINUTE = 5330;
    
        public static final double STEER_MOTOR_P = 0.5;
        public static final double STEER_MOTOR_I = 0.0;
        public static final double STEER_MOTOR_D = 0.0;

        public static final double MAX_DRIVE_PCT = 0.50;
        public static final double MAX_STEER_PCT = 0.50;

        public static final double WHEEL_DIAMETER_METERS = Units.inchesToMeters(6);
        public static final double DRIVE_REDUCTION = 1.0 / 8.0;
        public static final boolean DRIVE_INVERTED = true;
        public static final boolean STEER_INVERTED = true;
    }
}