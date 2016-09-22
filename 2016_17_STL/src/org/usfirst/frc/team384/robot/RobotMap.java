package org.usfirst.frc.team384.robot;

public class RobotMap {
    // Constants
    public static final double GYRO_FEEDBACK_KP = 0.03;
    //public static final double SPARKY_TOP_SPEED = 11;
    public static final double DRIVE_WHEEL_DIAMETER = 8.75;
    public static final double DRIVE_ENCODER_PULSES_PER_REV = 128.0;

    /****** Motor controllers (all CANTalons) ******/
    public static final int LEFT_CIM_MOTOR_0_CH = 1;
    public static final int LEFT_CIM_MOTOR_1_CH = 2;
    public static final int RIGHT_CIM_MOTOR_0_CH = 3;
    public static final int RIGHT_CIM_MOTOR_1_CH = 4;
    public static final int SHOOTER_MOTOR_CH = 5;
    public static final int INTAKE_WHEELS_MOTOR_CH = 6;

    public static final double INTAKE_WHEELS_MOTOR_EJECT_PWR = 1.0;
    public static final double INTAKE_WHEELS_MOTOR_ACQUIRE_PWR = 1.0;
    public static final double INTAKE_WHEELS_MOTOR_DROP_PWR = 0.80;

    public static final double SHOOTER_MOTOR_STD_PWR = 0.80;
    public static final double SHOOTER_MOTOR_FIRING_PWR = 1.0;

    /****** Pneumatic valve solenoids ******/
    /*
     * A Robot Values
     */
    //  public static final int GEARBOX_SOLENOID_CH_A = 2;
    //  public static final int GEARBOX_SOLENOID_CH_B = 1;
    //  public static final int INTAKE_SOLENOID_CH_A = 5;
    //  public static final int INTAKE_SOLENOID_CH_B = 4;
    //  public static final int SHOOTER_LIMIT_SW_CH = 1;
    //  public static final int FLASHLIGHT_DIO_CH = 0;
    /* A ROBOT VALUES */

    /*
     * B Robot Values
     */
    public static final int GEARBOX_SOLENOID_CH_A = 5;
    public static final int GEARBOX_SOLENOID_CH_B = 4;
    public static final int INTAKE_SOLENOID_CH_A = 2;
    public static final int INTAKE_SOLENOID_CH_B = 1;
    public static final int SHOOTER_LIMIT_SW_CH = 0;
    public static final int FLASHLIGHT_DIO_CH = 2;
    /* B ROBOT VALUES */

    // Encoders
    //  public static final int LEFT_ENCODER_CH_A = 4;
    //  public static final int LEFT_ENCODER_CH_B = 5;
    //  public static final int RIGHT_ENCODER_CH_A = 2;
    //  public static final int RIGHT_ENCODER_CH_B = 3;

    // Pilot Joystick buttons
    public static final int PILOT_SAFETY_INTERLOCK_BTN = 1;
    public static final int PILOT_GEARBOX_TOGGLE_BTN = 2;
    public static final int PILOT_INTAKE_ARMS_TOGGLE_BTN = 3;
    public static final int PILOT_FLASHLIGHT_TOGGLE_BTN = 4;
    public static final int PILOT_AUTODRIVE_FWD_BTN = 8;
    public static final int PILOT_AUTODRIVE_REV_BTN = 9;

    // coPilot Joystick buttons
    public static final int COPILOT_SHOOTER_ARM_BTN = 2;
    public static final int COPILOT_SHOOTER_FIRE_BTN = 3;
    public static final int COPILOT_SHOOTER_OVERRIDE_1 = 8;
    public static final int COPILOT_SHOOTER_OVERRIDE_2 = 9;

    // Common joystick buttons
    public static final int COM_INTAKE_WHEELS_SLOW_MOD_BTN = 5;
    public static final int COM_INTAKE_WHEELS_OUT_BTN = 6;
    public static final int COM_INTAKE_WHEELS_IN_BTN = 7;

}
