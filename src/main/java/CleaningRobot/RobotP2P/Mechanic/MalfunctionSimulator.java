package CleaningRobot.RobotP2P.Mechanic;

import java.util.Random;

public class MalfunctionSimulator extends Thread {

    private final Random random = new Random();
    private boolean stopCondition = false;

    @Override
    public void run() {
        while (!stopCondition) {
            try {
                if (random.nextDouble() < 0.1 && !MechanicState.getInstance().isNeedingMaintenance()) {
                    MechanicState.getInstance().needMaintenance();
                }
                MechanicState.getInstance().waitForMaintenanceDisinterest();

                Thread.sleep(10000);
            }
            catch (InterruptedException e) {
                stopCondition = true;
                break;
            }
        }
    }
}
