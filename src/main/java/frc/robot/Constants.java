package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;

public class Constants {
    public class DrivetrainConstants {
        // Left-to-right distance between drivetrain wheels
        public static final double DRIVETRAIN_TRACKWIDTH_METERS = Units.inchesToMeters(20.5);
        // Front-to-back distance between drivetrain wheels
        public static final double DRIVETRAIN_WHEELBASE_METERS = Units.inchesToMeters(20.5);

        public static final Rotation2d FRONT_LEFT_MODULE_STEER_OFFSET = Rotation2d.fromDegrees(330);
        public static final double FRONT_LEFT_MIN_STEER_PCT = 0.1;
        public static final Rotation2d BACK_LEFT_MODULE_STEER_OFFSET = Rotation2d.fromDegrees(240);
        public static final double BACK_LEFT_MIN_STEER_PCT = 0.075;
        public static final Rotation2d BACK_RIGHT_MODULE_STEER_OFFSET = Rotation2d.fromDegrees(210);
        public static final double BACK_RIGHT_MIN_STEER_PCT = 0.1;
        public static final Rotation2d FRONT_RIGHT_MODULE_STEER_OFFSET = Rotation2d.fromDegrees(300);
        public static final double FRONT_RIGHT_MIN_STEER_PCT = 0.125;

        public static final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
                // Front left
                new Translation2d(DRIVETRAIN_WHEELBASE_METERS / 2, DRIVETRAIN_WHEELBASE_METERS / 2),
                // Back left
                new Translation2d(-DRIVETRAIN_WHEELBASE_METERS / 2, DRIVETRAIN_WHEELBASE_METERS / 2),
                // Back right
                new Translation2d(-DRIVETRAIN_WHEELBASE_METERS / 2, -DRIVETRAIN_WHEELBASE_METERS / 2),
                // Front right
                new Translation2d(DRIVETRAIN_WHEELBASE_METERS / 2, -DRIVETRAIN_WHEELBASE_METERS / 2));
    }
}
