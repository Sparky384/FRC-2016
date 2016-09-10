package org.usfirst.frc.team384.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;

public class FlashlightControlThread implements Runnable {
    Robot r;
    DigitalOutput flashlight = new DigitalOutput(RobotMap.FLASHLIGHT_DIO_CH);
    Timer flashlightSwitchTimer = new Timer();

    public FlashlightControlThread(Robot sparky) {
        this.r = sparky;
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    }

    /*
     * NOTE - It was very late and Mr. Jeff was very tired... the relay is wired
     * backwards. So where it says 'false' actually means 'true' and vice-versa.
     * Red light blaze on relay module means 'on'
     */

    @Override
    public void run() {
        flashlightSwitchTimer.start();
        flashlight.disablePWM();
        //flashlight.set(true);
        flashlightSwitchTimer.stop();
        flashlightSwitchTimer.reset();
    }

    public void flashlightMagicSwitch() {
        flashlightSwitchTimer.reset();
        flashlightSwitchTimer.start();
        flashlight.set(false);
        while (flashlightSwitchTimer.get() < 0.1) {
            Timer.delay(0.02);
        }
        flashlight.set(true);
        flashlightSwitchTimer.stop();
    }
}
