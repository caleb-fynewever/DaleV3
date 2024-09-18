// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2052.swerve;

/** 
 * Constants for swerve modules.
 */
public final class SwerveConstants {
    public static final double CAN_TIMEOUT_SECONDS = 0.25;

    public static final class SwerveModule {
        public static final int CIM_ROUNDS_PER_MINUTE = 5330;
    
        public static final double STEER_MOTOR_P = 0.75;
        public static final double STEER_MOTOR_I = 0.0;
        public static final double STEER_MOTOR_D = 0.25;

        public static final double MIN_STEER_PCT = 0.20;

        public static final double WHEEL_DIAMETER_METERS = 0.10033; //TODO: find dis
        public static final double DRIVE_REDUCTION = (16.0 / 54.0) * (16.0 / 24.0) * (16.0 / 48.0);
        public static final boolean DRIVE_INVERTED = true;
        public static final boolean STEER_INVERTED = true;
    }
}