// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.drive;

import java.util.function.DoubleSupplier;

import com.team2052.swerve.SwerveModule;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.Command;

public class ManualSwerveModuleDriveCommand extends Command {
  public SwerveModule module;

  private final DoubleSupplier xSupplier;
  private final DoubleSupplier ySupplier;

  private final SlewRateLimiter xLimiter;
  private final SlewRateLimiter yLimiter;

  public ManualSwerveModuleDriveCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      SwerveModule module) {
    this.module = module;
    this.xSupplier = xSupplier;
    this.ySupplier = ySupplier;
    xLimiter = new SlewRateLimiter(2);
    yLimiter = new SlewRateLimiter(2);
  }

  protected double getDrive() {
    return slewAxis(xLimiter, deadBand(-xSupplier.getAsDouble()));
  }

  protected double getSteer() {
    return slewAxis(yLimiter, deadBand(-ySupplier.getAsDouble()));
  }

  @Override
  public void execute() {
    module.setState(getDrive(), -getDrive());
  }

  @Override
  public void end(boolean interrupted) {
    module.setState(0, 0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  protected double slewAxis(SlewRateLimiter limiter, double value) {
    return limiter.calculate(Math.copySign(Math.pow(value, 2), value));
  }

  protected double deadBand(double value) {
    if (Math.abs(value) <= 0.075) {
      return 0.0;
    }
    // Limit the value to always be in the range of [-1.0, 1.0]
    return Math.copySign(Math.min(1.0, Math.abs(value)), value);
  }
}
