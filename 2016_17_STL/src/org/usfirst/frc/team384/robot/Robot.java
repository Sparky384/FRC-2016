package org.usfirst.frc.team384.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	public static Joystick pilot = new Joystick(0);
	public static Joystick coPilot = new Joystick(1);
	public static CANTalon rightMotor0 = new CANTalon(RobotMap.RIGHT_CIM_MOTOR_0_CH);
	public static CANTalon rightMotor1 = new CANTalon(RobotMap.RIGHT_CIM_MOTOR_1_CH);
	public static CANTalon leftMotor0 = new CANTalon(RobotMap.LEFT_CIM_MOTOR_0_CH);
	public static CANTalon leftMotor1 = new CANTalon(RobotMap.LEFT_CIM_MOTOR_1_CH);
	public static CANTalon intakeWheelsMotor = new CANTalon(RobotMap.INTAKE_WHEELS_MOTOR_CH);
	public static CANTalon shooterMotor = new CANTalon(RobotMap.SHOOTER_MOTOR_CH);
	public static DoubleSolenoid gearboxSolenoid = new DoubleSolenoid(RobotMap.GEARBOX_SOLENOID_CH_A, RobotMap.GEARBOX_SOLENOID_CH_B);
	public static DoubleSolenoid intakeArmSolenoid = new DoubleSolenoid(RobotMap.INTAKE_SOLENOID_CH_A, RobotMap.INTAKE_SOLENOID_CH_B);
	public static PowerDistributionPanel pdp = new PowerDistributionPanel();
	public static DigitalInput shooterLimitSwitch = new DigitalInput(RobotMap.SHOOTER_LIMIT_SW_CH);
	public static RobotDrive Sparky = new RobotDrive(leftMotor0, rightMotor0);
	public static Timer sparkTime = new Timer();
	public static AHRS gyro;

	/* CAMERA SUPPORT REMOVED - FLAKY AND DISASTROUS WHEN FAILS */
	//public static USBCamera targetCam = new USBCamera("cam0");
	//NIVision.Image frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
	//CameraServer server;

	FlashlightControlThread fst;
	private boolean flashlightBtnPressed;
	private boolean flashlightBtnWasPressed;

	private boolean isGearboxHighSpeedSelected;
	private boolean gearBoxBtnPressed;
	private boolean gearBoxBtnWasPressed;

	private boolean isIntakeArmUp;
	private boolean intakeArmToggleBtnPressed;
	private boolean intakeArmToggleBtnWasPressed;

	private boolean shooterArmBtnPressed;
	private boolean shooterArmBtnWasPressed;
	private boolean shooterFireBtnPressed;
	private boolean shooterFireBtnWasPressed;
	private boolean isShooterArming;
	private boolean isShooterArmed;
	private boolean shooterReadyToFire;
	private boolean isShooterFiring;
	//private double  shooterLimSwInputAverager;
	//private int     shooterLimSwCapture;

	private boolean isAutonAlreadyDoneOnce;

	public enum IntakeWheelsSpin {
		FWD_SLOW, FWD, STOP, REV, REV_SLOW;
	}

	/* ================= AUTONOMOUS ================= */
	final String defaultAuto = "Shot from corner";
	final String armDownDrive = "Arm DOWN, then drive fwd";
	final String armUpDrive = "Arm UP, then drive fwd";
	final String somethingWonderful = "Something Wonderful";
	final String somethingEvenMoreWonderful = "Something Even More Wonderful";
	String autoSelected;
	SendableChooser chooser;
	/* ================= AUTONOMOUS ================= */

	public void robotInit() {

		/* ================= AUTONOMOUS ================= */
		chooser = new SendableChooser();
		chooser.addDefault("Shot from corner", defaultAuto);
		chooser.addObject("Arm down, then drive fwd", armDownDrive);
		chooser.addObject("Arm up, then drive fwd", armUpDrive);
		chooser.addObject("Something Wonderful", somethingWonderful);
		chooser.addObject("Something Even More Wonderful", somethingEvenMoreWonderful);
		SmartDashboard.putData("Auto choices", chooser);
		/* ================= AUTONOMOUS ================= */

		Sparky.setSafetyEnabled(false);
		
		rightMotor0.setInverted(true);
		leftMotor0.setInverted(true);

		rightMotor1.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightMotor1.set(rightMotor0.getDeviceID());

		leftMotor1.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftMotor1.set(leftMotor0.getDeviceID());

		//targetCam.setFPS(15);
		//targetCam.setExposureAuto();
		//targetCam.setWhiteBalanceAuto();
		//targetCam.updateSettings();
		//targetCam.startCapture();
		
		fst = new FlashlightControlThread(this);
		new Thread(fst).start();

		try {
			gyro = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex) {
			System.out.print("Error instantiating navX MXP:  " + ex.getMessage());
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
		}

		sparkTime.start();
		sparkTime.reset();
	}

	public void disabledInit() {
		// System.out.println("Overridden to prevent runtime message");
	}

	public void disabledPeriodic() {
		// System.out.println("Overridden to prevent runtime message");
	}

	public void autonomousInit() {
		isAutonAlreadyDoneOnce = false;
		sparkTime.start();

		autoSelected = (String) chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		gyro.reset();
	}

	public void autonomousPeriodic() {
		switch (autoSelected) {
		case somethingWonderful:
			if (!isAutonAlreadyDoneOnce) {
				isAutonAlreadyDoneOnce = true;
				autonLowGoalDrive();
			}
			break;

		case somethingEvenMoreWonderful:
			if (!isAutonAlreadyDoneOnce) {
				isAutonAlreadyDoneOnce = true;
                autonLowGoalDrive();
				//autonLowGoalDriveEncoderAwesomeness();
			}
			break;

		case armDownDrive:
			if (!isAutonAlreadyDoneOnce) {
				isAutonAlreadyDoneOnce = true;
				autonArmDownDriveFwd();
			}
			break;

		case armUpDrive:
			if (!isAutonAlreadyDoneOnce) {
				isAutonAlreadyDoneOnce = true;
				autonArmUpDriveFwd();
			}
			break;

		case defaultAuto: // One shot from corner
		default:
			if (!isAutonAlreadyDoneOnce) {
				isAutonAlreadyDoneOnce = true;
				autonOneShotFromCorner();
			}
			break;
		}
	}


	public void teleopInit() {
		sparkTime.start();
		sparkTime.reset();
		gyro.reset();
		while (gyro.isCalibrating()) {
			SmartDashboard.putString("Gyro", "CALIBRATING");
			Timer.delay(0.02);
		}
	}

	public void teleopPeriodic() {

		//targetCam.getImage(frame);
		//CameraServer.getInstance().setImage(frame);
		if (isOperatorControl() && isEnabled() && pilot.getTrigger()) {
			if (pilot.getRawButton(RobotMap.PILOT_AUTODRIVE_FWD_BTN)) {
				Sparky.drive(-0.8, -gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP);
			} else if (pilot.getRawButton(RobotMap.PILOT_AUTODRIVE_REV_BTN)) {
				//Sparky.drive(0.8, -gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP);
				Sparky.drive(0.8, (gyro.getYaw() + 180) * RobotMap.GYRO_FEEDBACK_KP);
			} else {
				Sparky.arcadeDrive(pilot, false);
			}

		} else {
			Sparky.drive(0.0, 0.0);
		}

		shooterHandler();
		intakeArmsHandler();
		intakeWheelsHandler();
		gearBoxHandler();
		flashlightHandler();
		instrumentation();
	}

	void shooterHandler() {

		shooterArmBtnPressed = coPilot.getRawButton(RobotMap.COPILOT_SHOOTER_ARM_BTN);
		shooterFireBtnPressed = coPilot.getRawButton(RobotMap.COPILOT_SHOOTER_FIRE_BTN);

		isShooterArmed = shooterLimitSwitch.get();

		//	// sets isShooterArmed == true if 6 sequential sw closures read
		//	if (shooterLimitSwitch.get()) 
		//	    shooterLimSwCapture = 1;
		//	else
		//	    shooterLimSwCapture = 0;
		//	shooterLimSwInputAverager = 0.5 * (shooterLimSwCapture + shooterLimSwInputAverager);
		//	// isShooterArmed = ((int)(shooterLimSwInputAverager + 0.0156) == 1) ? true : false;
		//	if ( (int)(shooterLimSwInputAverager + 0.0156) == 1 ) {
		//	    isShooterArmed = true;
		//	} else {
		//	    isShooterArmed = false;
		//	}

		shooterReadyToFire = isShooterArmed && !isShooterFiring && !isIntakeArmUp;

		if (shooterArmBtnPressed && !shooterArmBtnWasPressed) {
			if (!isShooterArmed)
				isShooterArming = true;
			else
				isShooterArming = false;
		}
		shooterArmBtnWasPressed = shooterArmBtnPressed;

		if (shooterFireBtnPressed && !shooterFireBtnWasPressed && shooterReadyToFire) {
			isShooterFiring = true;
			sparkTime.reset();
		}
		shooterFireBtnWasPressed = shooterFireBtnPressed;

		if (isShooterFiring) {
			if (sparkTime.get() < 0.50) {
				shooterMotor.set(-RobotMap.SHOOTER_MOTOR_FIRING_PWR);
			} else if (sparkTime.get() < 1.0) {
				shooterMotor.set(0.0);
			} else {
				shooterMotor.set(0.0);
				isShooterFiring = false;
				isShooterArmed = false;
				isShooterArming = true;
			}
		}

		if (isShooterArming) {
			if (!isShooterArmed) {
				shooterMotor.set(-RobotMap.SHOOTER_MOTOR_STD_PWR);
			} else {
				shooterMotor.set(0.0);
				isShooterArmed = true;
				isShooterArming = false;
			}
		}

		/* *** OVERRIDE *** */
		if (coPilot.getRawButton(RobotMap.COPILOT_SHOOTER_OVERRIDE_1)
				&& coPilot.getRawButton(RobotMap.COPILOT_SHOOTER_OVERRIDE_2)) {
			shooterMotor.set(-RobotMap.SHOOTER_MOTOR_STD_PWR);
		} else if (!isShooterArming && !isShooterFiring) {
			shooterMotor.set(0.0);
		}
	}

	void gearBoxHandler() {

		gearBoxBtnPressed = pilot.getRawButton(RobotMap.PILOT_GEARBOX_TOGGLE_BTN);

		if (gearBoxBtnPressed && !gearBoxBtnWasPressed) {
			if (isGearboxHighSpeedSelected == false) {
				gearBoxSelectHighSpeed();
			} else {
				gearBoxSelectLowSpeed();
			}
		}
		gearBoxBtnWasPressed = gearBoxBtnPressed;
	}

	void gearBoxSelectHighSpeed() {
		gearboxSolenoid.set(Value.kForward);
		isGearboxHighSpeedSelected = true;
	}

	void gearBoxSelectLowSpeed() {
		gearboxSolenoid.set(Value.kReverse);
		isGearboxHighSpeedSelected = false;
	}

	void intakeArmsHandler() {

		intakeArmToggleBtnPressed = pilot.getRawButton(RobotMap.PILOT_INTAKE_ARMS_TOGGLE_BTN);

		if (intakeArmToggleBtnPressed && !intakeArmToggleBtnWasPressed) {
			if (!isIntakeArmUp) {
				intakeArmUp();
			} else {
				intakeArmDown();
			}
		}
		intakeArmToggleBtnWasPressed = intakeArmToggleBtnPressed;
	}

	void intakeArmUp() {
		intakeArmSolenoid.set(Value.kReverse);
		isIntakeArmUp = true;
	}

	void intakeArmDown() {
		intakeArmSolenoid.set(Value.kForward);
		isIntakeArmUp = false;
	}

	void flashlightHandler() {
		flashlightBtnPressed = pilot.getRawButton(RobotMap.PILOT_FLASHLIGHT_TOGGLE_BTN);

		if (flashlightBtnPressed && !flashlightBtnWasPressed) {
			fst.flashlightMagicSwitch();
		}
		flashlightBtnWasPressed = flashlightBtnPressed;
	}

	void intakeWheelsHandler() {
		if (pilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_SLOW_MOD_BTN) || coPilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_SLOW_MOD_BTN)) {
			if (pilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_IN_BTN) || coPilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_IN_BTN)) {
				intakeWheelsControl(IntakeWheelsSpin.REV_SLOW);
			} else if (pilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_OUT_BTN) || coPilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_OUT_BTN)) {
				intakeWheelsControl(IntakeWheelsSpin.FWD_SLOW);
			}
		} else if (pilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_IN_BTN) || coPilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_IN_BTN)) {
			intakeWheelsControl(IntakeWheelsSpin.REV);
		} else if (pilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_OUT_BTN) || coPilot.getRawButton(RobotMap.COM_INTAKE_WHEELS_OUT_BTN)) {
			intakeWheelsControl(IntakeWheelsSpin.FWD);
		} else {
			intakeWheelsControl(IntakeWheelsSpin.STOP);
		}
	}

	void intakeWheelsControl(IntakeWheelsSpin spinDirection) {

		switch (spinDirection) {
		case REV:
			intakeWheelsMotor.set(-RobotMap.INTAKE_WHEELS_MOTOR_ACQUIRE_PWR);
			break;
		case REV_SLOW:
			intakeWheelsMotor.set(-RobotMap.INTAKE_WHEELS_MOTOR_DROP_PWR);
			break;
		case STOP:
			intakeWheelsMotor.set(0.0);
			break;
		case FWD:
			intakeWheelsMotor.set(RobotMap.INTAKE_WHEELS_MOTOR_EJECT_PWR);
			break;
		case FWD_SLOW:
			intakeWheelsMotor.set(RobotMap.INTAKE_WHEELS_MOTOR_DROP_PWR);
		}
	}

	void instrumentation() {
		SmartDashboard.putBoolean("isShooterArmed?", isShooterArmed);
		SmartDashboard.putBoolean("intakeArmsUp?", isIntakeArmUp);
		SmartDashboard.putBoolean("shooterArming?", isShooterArming);
		SmartDashboard.putBoolean("shooterReadyToFire?", shooterReadyToFire);

		SmartDashboard.putNumber("gyro YAW", gyro.getYaw());
		SmartDashboard.putNumber("gyro PITCH", gyro.getPitch());
		//SmartDashboard.putNumber("gyro Y Disp", gyro.getDisplacementY());
		//SmartDashboard.putNumber("gyro X Disp", gyro.getDisplacementX());

		//SmartDashboard.putNumber(" right enc ", -rightDriveEncoder.get());
		//SmartDashboard.putNumber(" left dist ", leftDriveEncoder.getDistance());
		//SmartDashboard.putNumber("  left enc ", leftDriveEncoder.get());
		//SmartDashboard.putNumber("   avg enc ", (-rightDriveEncoder.get() + leftDriveEncoder.get()) / 2.0);

		if ((gyro.getPitch() >= 0.0) && gyro.getPitch() <= 1.0) {
			SmartDashboard.putBoolean("GYRO EL SHOT", true);
		} else {
			SmartDashboard.putBoolean("GYRO EL SHOT", false);
		}

		//SmartDashboard.putNumber("sparkTime.get()", sparkTime.get());
	}

	public void testInit() {

	}

	public void testPeriodic() {
		LiveWindow.run();
	}

	void autonOneShotFromCorner() {

		sparkTime.reset();
		while (sparkTime.get() <= 3.0) {
			Timer.delay(0.2);
		}

		intakeArmDown();

		Timer.delay(2.0);

		sparkTime.reset();
		while (sparkTime.get() <= 0.50) {
			shooterMotor.set(-RobotMap.SHOOTER_MOTOR_FIRING_PWR);
		}

		shooterMotor.set(0.0);

		sparkTime.reset();
		while (sparkTime.get() <= 2.0) {
			Timer.delay(0.2);
		}

		isShooterFiring = false;
		isShooterArmed = false;
		isShooterArming = true;

		Timer.delay(0.5); // chill
	}

	void autonArmUpDriveFwd() {
		double autoThrottle = -0.5;

		//intakeArmUp();

		sparkTime.reset();
		while (sparkTime.get() <= 3.0) {
			Timer.delay(0.02);
		}

		gyro.reset();
		sparkTime.reset();
		while (sparkTime.get() <= 1.0) {
			Timer.delay(0.02);
		}

		sparkTime.reset();
		while (sparkTime.get() < 6.5) {
			Sparky.drive(autoThrottle, -gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP);
			Timer.delay(0.02);
		}

		Sparky.drive(0.0, 0.0);

	}

	void autonArmDownDriveFwd() {
		double autoThrottle = -0.5;

		intakeArmDown();

		sparkTime.reset();
		while (sparkTime.get() <= 3.0) {
			Timer.delay(0.02);
		}

		//gearBoxSelectHighSpeed();
		gyro.reset();
		while (gyro.isCalibrating()) {
			SmartDashboard.putString("Gyro", "CALIBRATING");
			Timer.delay(0.02);
		}
		sparkTime.reset();
		while (sparkTime.get() <= 1.0) {
			Timer.delay(0.02);
		}

		sparkTime.reset();
		while (sparkTime.get() < 6.5) {
			Sparky.drive(autoThrottle, -gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP);
			Timer.delay(0.02);
		}

		Sparky.drive(0.0, 0.0);

		Timer.delay(0.5); // chill
	}

	void autonLowGoalDrive() {
		double angle;
		double autoThrottle = -0.5;

		gyro.reset();
		intakeArmDown();
		sparkTime.start();
		sparkTime.reset();
		while (sparkTime.get() <= 0.5) {
			Timer.delay(0.02);
		}

		sparkTime.reset();
		while (sparkTime.get() <= 8.4) {
			Sparky.drive(autoThrottle, (-gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP));
			Timer.delay(0.02);
		}

		intakeArmUp();
		angle = 60.0;
		while (gyro.getYaw() < angle) {
			Sparky.drive(autoThrottle, 0.5);
			Timer.delay(0.02);
		}

		angle = 0.0;
		gyro.reset();
		sparkTime.reset();
		while (sparkTime.get() <= 2.3) {
			Sparky.drive(autoThrottle, -gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP);
			Timer.delay(0.02);
		}
		Sparky.drive(0.0, 0.0);

		intakeArmDown();
		sparkTime.reset();
		while (sparkTime.get() <= 1.5) {
			Timer.delay(0.02);
		}

		sparkTime.reset();
		intakeWheelsControl(IntakeWheelsSpin.FWD);
		while (sparkTime.get() <= 1.5) {
			Timer.delay(0.02);
		}
		intakeWheelsControl(IntakeWheelsSpin.STOP);
	}

	//    void autonLowGoalDriveEncoderAwesomeness() {
	//	double angle;
	//	double autoThrottle = -0.5;
	//
	//	gyro.reset();
	//	intakeArmDown();
	//	sparkTime.start();
	//	sparkTime.reset();
	//	while (sparkTime.get() <= 0.5) {
	//	    Timer.delay(0.02);
	//	}
	//
	//	encodersReset();
	//	while (leftDriveEncoder.getDistance() <= 224.0) {
	//	    Sparky.drive(autoThrottle, (-gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP));
	//	    Timer.delay(0.02);
	//	}
	//
	//	intakeArmUp();
	//	angle = 60.0;
	//	while (gyro.getYaw() < angle) {
	//	    Sparky.drive(autoThrottle, 0.5);
	//	    Timer.delay(0.02);
	//	}
	//
	//	angle = 0.0;
	//	gyro.reset();
	//	encodersReset();
	//	while (leftDriveEncoder.getDistance() <= 60.0) {
	//	    Sparky.drive(autoThrottle, -gyro.getYaw() * RobotMap.GYRO_FEEDBACK_KP);
	//	    Timer.delay(0.02);
	//	}
	//	Sparky.drive(0.0, 0.0);
	//
	//	intakeArmDown();
	//	sparkTime.reset();
	//	while (sparkTime.get() <= 1.5) {
	//	    Timer.delay(0.02);
	//	}
	//
	//	sparkTime.reset();
	//	intakeWheelsControl(IntakeWheelsSpin.FWD);
	//	while (sparkTime.get() <= 1.5) {
	//	    Timer.delay(0.02);
	//	}
	//	intakeWheelsControl(IntakeWheelsSpin.STOP);
	//
	//	Timer.delay(0.5);
	//    }
	//    
	//    double avgEncoderDistanceTravelled(boolean printDiagnostics) {
	//	double avgEncDist = (-rightDriveEncoder.getDistance() + leftDriveEncoder.getDistance()) / 2.0;
	//	if (printDiagnostics) {
	//	    SmartDashboard.putNumber("AvgEncDist", avgEncDist);
	//	}
	//	return avgEncDist;
	//    }
	//    
	//    void encodersReset() {
	//	rightDriveEncoder.reset();
	//	leftDriveEncoder.reset();
	//    }
}
