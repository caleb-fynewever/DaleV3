// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import javax.sound.sampled.Port;

import com.kauailabs.navx.frc.AHRS;
import com.team2052.swerve.SwerveModule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Ports;
import frc.robot.Constants.DrivetrainConstants;

public class DrivetrainSubsystem extends SubsystemBase {
  public final SwerveModule frontLeftModule; // this 'module' is public because the motor controllers are sometimes used to test other things
  // private final SwerveModule backLeftModule;
  // private final SwerveModule backRightModule;
  // private final SwerveModule frontRightModule;

  private final AHRS navx;

  /** Creates a new DrivetrainSubsystem. */
  public DrivetrainSubsystem() {
    frontLeftModule = new SwerveModule(
        Ports.FL_DRIVE,
        Ports.FL_STEER,
        Ports.FL_ENCODER,
        "front left",
        DrivetrainConstants.FRONT_LEFT_MODULE_STEER_OFFSET_RADIANS);
    // backLeftModule = new SwerveModule(
    //     Ports.BL_DRIVE,
    //     Ports.BL_STEER,
    //     Ports.BL_ENCODER,
    //     "back left",
    //     DrivetrainConstants.BACK_LEFT_MODULE_STEER_OFFSET_RADIANS);
    // backRightModule = new SwerveModule(
    //     Ports.BR_DRIVE,
    //     Ports.BR_STEER,
    //     Ports.BR_ENCODER,
    //     "back right",
    //     DrivetrainConstants.BACK_RIGHT_MODULE_STEER_OFFSET_RADIANS);
    // frontRightModule = new SwerveModule(
    //     Ports.BR_DRIVE,
    //     Ports.BR_STEER,
    //     Ports.BR_ENCODER,
    //     "front right",
    //     DrivetrainConstants.FRONT_RIGHT_MODULE_STEER_OFFSET_RADIANS);

    navx = new AHRS(SPI.Port.kMXP, (byte) 200);

  }

  public AHRS getNavx() {
    return navx;
  }

  public Rotation2d getRotation() {
    return navx.getRotation2d();
  }

  /**
   * All parameters are taken in normalized terms of [-1.0 to 1.0].
   */
  public void drive(
      double normalizedXVelocity,
      double normalizedYVelocity,
      double normalizedRotationVelocity,
      boolean fieldCentric) {
    normalizedXVelocity = Math.copySign(
        Math.min(Math.abs(normalizedXVelocity), 1.0),
        normalizedXVelocity);
    normalizedYVelocity = Math.copySign(
        Math.min(Math.abs(normalizedYVelocity), 1.0),
        normalizedYVelocity);
    normalizedRotationVelocity = Math.copySign(
        Math.min(Math.abs(normalizedRotationVelocity), 1.0),
        normalizedRotationVelocity);

    ChassisSpeeds chassisSpeeds = new ChassisSpeeds(
        normalizedXVelocity * getMaxVelocityMetersPerSecond(),
        normalizedYVelocity * getMaxVelocityMetersPerSecond(),
        normalizedRotationVelocity * getMaxAngularVelocityRadiansPerSecond());

    // The origin is always blue. When our alliance is red, X and Y need to be
    // inverted
    var alliance = DriverStation.getAlliance();
    var invert = 1;
    if (alliance.isPresent() && alliance.get() == Alliance.Red) {
      invert = -1;
    }

    if (fieldCentric) {
      chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
          chassisSpeeds.vxMetersPerSecond * invert,
          chassisSpeeds.vyMetersPerSecond * invert,
          chassisSpeeds.omegaRadiansPerSecond,
          getRotation());
    }

    drive(chassisSpeeds);
  }

  public void drive(ChassisSpeeds chassisSpeeds) {
    SwerveModuleState[] swerveModuleStates = DrivetrainConstants.kinematics.toSwerveModuleStates(chassisSpeeds);
    setModuleStates(swerveModuleStates);
  }

  public void stop() {
    drive(0, 0, 0, false);
  }

  public void setModuleStates(SwerveModuleState[] swerveModuleStates) {
    // Check if the wheels don't have a drive velocity to maintain the current wheel
    // orientation.
    boolean hasVelocity = swerveModuleStates[0].speedMetersPerSecond != 0
        || swerveModuleStates[1].speedMetersPerSecond != 0
        || swerveModuleStates[2].speedMetersPerSecond != 0
        || swerveModuleStates[3].speedMetersPerSecond != 0;

    frontLeftModule.setState(
        swerveModuleStates[0].speedMetersPerSecond,
        hasVelocity ? swerveModuleStates[0].angle : Rotation2d.fromDegrees(frontLeftModule.getAngle()));
    // backLeftModule.setState(
    // swerveModuleStates[1].speedMetersPerSecond,
    // hasVelocity ? swerveModuleStates[1].angle :
    // Rotation2d.fromDegrees(backLeftModule.getAngle()));
    // backRightModule.setState(
    // swerveModuleStates[2].speedMetersPerSecond,
    // hasVelocity ? swerveModuleStates[2].angle :
    // Rotation2d.fromDegrees(backRightModule.getAngle()));
    // frontRightModule.setState(
    // swerveModuleStates[3].speedMetersPerSecond,
    // hasVelocity ? swerveModuleStates[3].angle :
    // Rotation2d.fromDegrees(frontRightModule.getAngle()));
  }

  @Override
  public void periodic() {
    frontLeftModule.updateSteer();
  }

  public static double getMaxVelocityMetersPerSecond() {
    return SwerveModule.getMaxVelocityMetersPerSecond();
  }

  public static double getMaxAngularVelocityRadiansPerSecond() {
    /*
     * Find the theoretical maximum angular velocity of the robot in radians per
     * second
     * (a measure of how fast the robot can rotate in place).
     */

    return SwerveModule.getMaxVelocityMetersPerSecond() / Math.hypot(
        DrivetrainConstants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
        DrivetrainConstants.DRIVETRAIN_WHEELBASE_METERS / 2.0);

    // return 6 * Math.PI;
  }
}
