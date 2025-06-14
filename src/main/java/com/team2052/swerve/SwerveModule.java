package com.team2052.swerve;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPXPIDSetConfiguration;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {
    private final TalonSRX driveMotor;
    private final VictorSPX steerMotor;
    private PIDController steerController;
    private final DutyCycleEncoder encoder;
    private String debugName;

    public SwerveModule(
            int driveMotorChannel,
            int steerMotorChannel,
            int encoderChannel,
            String debugName,
            Rotation2d steerOffset) {

        this.debugName = debugName;

        /*
         * DutyCycleEncoder Initialization
         */
        encoder = new DutyCycleEncoder(encoderChannel);
        encoder.setPositionOffset(steerOffset.getRadians() / (2 * Math.PI));
        encoder.setDistancePerRotation(360);

        /*
         * Drive Motor Initialization
         */

        driveMotor = new TalonSRX(driveMotorChannel);
        driveMotor.setNeutralMode(NeutralMode.Brake);
        driveMotor.setInverted(SwerveConstants.SwerveModule.DRIVE_INVERTED);
        
        /*
         *  Steer Controller Initialization
         */

        steerController = new PIDController(
            SwerveConstants.SwerveModule.STEER_MOTOR_P, 
            SwerveConstants.SwerveModule.STEER_MOTOR_I, 
            SwerveConstants.SwerveModule.STEER_MOTOR_D
        );

        /*
         * Steer Motor Initialization
         */

        steerMotor = new VictorSPX(steerMotorChannel);
        steerMotor.configAllSettings(new VictorSPXConfiguration());
        // VictorSPXConfiguration steerConfig = new VictorSPXConfiguration();
        steerMotor.setInverted(SwerveConstants.SwerveModule.STEER_INVERTED);
        steerMotor.setNeutralMode(NeutralMode.Brake);
    }

    public double getAngle() {
        return encoder.getDistance();
    }

    public void setState(double velocityMetersPerSecond, Rotation2d steerAngle) {
        SwerveModuleState desiredState = new SwerveModuleState(velocityMetersPerSecond, steerAngle);
        // Reduce radians to 0 to 2pi range and simplify to nearest angle
        desiredState = SwerveModuleState.optimize(
                desiredState,
                Rotation2d.fromDegrees(getAngle()));

        // Set the motor to our desired velocity as a percentage of our max velocity
        driveMotor.set(TalonSRXControlMode.PercentOutput,
                desiredState.speedMetersPerSecond / getMaxVelocityMetersPerSecond());

        steerController.setSetpoint(desiredState.angle.getDegrees());

        SmartDashboard.putNumber(debugName + ": Desired Rotation", steerAngle.getDegrees());
    }

    public void setState(double driveSpeed, double steerSpeed) {
        driveMotor.set(TalonSRXControlMode.PercentOutput, MathUtil.clamp(driveSpeed, -1, 1));
        steerMotor.set(VictorSPXControlMode.PercentOutput, MathUtil.clamp(steerSpeed, -1, 1));
    }

    public void updateSteer() {
        steerMotor.set(VictorSPXControlMode.PercentOutput, steerController.calculate(encoder.getDistance()) / 360);
    }

    public static double getMaxVelocityMetersPerSecond() {
        /*
         * The formula for calculating the theoretical maximum velocity is:
         * [Motor free speed (RPM)] / 60 * [Drive reduction] * [Wheel diameter (m)] * pi
         * This is a measure of how fast the robot should be able to drive in a straight
         * line.
         */
        return SwerveConstants.SwerveModule.CIM_ROUNDS_PER_MINUTE / 60 * SwerveConstants.SwerveModule.DRIVE_REDUCTION *
                SwerveConstants.SwerveModule.WHEEL_DIAMETER_METERS * Math.PI;
    }
}