package com.team2052.swerve;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {
    private final TalonSRX driveMotor;
    private final VictorSPX steerMotor;
    private final SlewRateLimiter driveLimiter;
    private PIDController steerController;
    private final DutyCycleEncoder encoder;
    private double minSteerPct;
    private String debugName;

    public SwerveModule(
            int driveMotorChannel,
            int steerMotorChannel,
            int encoderChannel,
            String debugName,
            double minSteerPct,
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
         *  Drive Limiter
         */

        driveLimiter = new SlewRateLimiter(0.15);
        
        /*
         *  Steer Controller Initialization
         */

        steerController = new PIDController(
            SwerveConstants.SwerveModule.STEER_MOTOR_P, 
            SwerveConstants.SwerveModule.STEER_MOTOR_I, 
            SwerveConstants.SwerveModule.STEER_MOTOR_D
        );

        steerController.setTolerance(2);
        steerController.enableContinuousInput(0, 360);

        /*
         * Steer Motor Initialization
         */

        steerMotor = new VictorSPX(steerMotorChannel);
        steerMotor.configAllSettings(new VictorSPXConfiguration());
        // VictorSPXConfiguration steerConfig = new VictorSPXConfiguration();
        steerMotor.setInverted(SwerveConstants.SwerveModule.STEER_INVERTED);
        steerMotor.setNeutralMode(NeutralMode.Brake);

        this.minSteerPct = minSteerPct;
    }

    public double getAngle() {
        if (encoder.getDistance() < 0) {
            return (Math.abs(encoder.getDistance()) + 180) % 360;
        }
        return encoder.getDistance() % 360;
    }

    public void setState(double velocityMetersPerSecond, Rotation2d steerAngle) {
        SwerveModuleState desiredState = new SwerveModuleState(velocityMetersPerSecond, steerAngle);
        // desiredState = SwerveModuleState.optimize(
        //         desiredState,
        //         Rotation2d.fromDegrees(getAngle()));

        // Set the motor to our desired velocity as a percentage of our max velocity
        driveMotor.set(TalonSRXControlMode.PercentOutput,
                Math.min(driveLimiter.calculate(desiredState.speedMetersPerSecond / getMaxVelocityMetersPerSecond()), SwerveConstants.SwerveModule.MAX_DRIVE_PCT));

        steerController.setSetpoint(desiredState.angle.getDegrees());

        SmartDashboard.putNumber(debugName + ": Desired Rotation", steerAngle.getDegrees());
    }

    public void setStateManual(double driveSpeed, double steerSpeed) {
        driveMotor.set(TalonSRXControlMode.PercentOutput, MathUtil.clamp(driveSpeed, -1, 1));
        steerMotor.set(VictorSPXControlMode.PercentOutput, MathUtil.clamp(steerSpeed, -1, 1));
    }

    public void updateSteer() {
        SmartDashboard.putNumber(debugName + " PID Value", steerController.calculate(getAngle()) / 360);
        double steerPCT = steerController.calculate(getAngle()) / 360;
        if (!steerController.atSetpoint()) {
            steerPCT += Math.copySign(minSteerPct, steerPCT);
        } else {
            steerPCT = 0;
        }
        steerMotor.set(VictorSPXControlMode.PercentOutput, Math.min(steerPCT, SwerveConstants.SwerveModule.MAX_STEER_PCT));
    }

    public void debug() {
        SmartDashboard.putNumber(debugName + " encoder value", getAngle());
        SmartDashboard.putNumber(debugName + " drive speed", driveMotor.getMotorOutputPercent());
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