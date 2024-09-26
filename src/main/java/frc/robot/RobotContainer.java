// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.drive.DefaultDriveCommand;
import frc.robot.commands.drive.ManualMotorControlCommand;
import frc.robot.subsystems.DrivetrainSubsystem;

public class RobotContainer {
  public final DrivetrainSubsystem drivetrain;

  private final Joystick translationJoystick;
  private final Joystick rotationJoystick;

  public RobotContainer() {
    drivetrain = new DrivetrainSubsystem();

    translationJoystick = new Joystick(0);
    rotationJoystick = new Joystick(1);


    drivetrain.setDefaultCommand(
      new DefaultDriveCommand(
          // Forward velocity supplier.
          translationJoystick::getY,
          // Sideways velocity supplier.
          translationJoystick::getX,
          // Rotation velocity supplier.
          rotationJoystick::getX,
          () -> false,
          drivetrain
      )
    );

    configureBindings();
  }

  private void configureBindings() {
    // JoystickButton manualMotorButton = new JoystickButton(translationJoystick, 1);
    // manualMotorButton.whileTrue(new ManualMotorControlCommand(translationJoystick::getY, translationJoystick::getX, drivetrain.frontLeftModule));
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
