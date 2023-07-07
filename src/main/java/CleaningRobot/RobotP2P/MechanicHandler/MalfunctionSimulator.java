package CleaningRobot.RobotP2P.MechanicHandler;

import java.util.Random;
import java.util.logging.Logger;

public class MalfunctionSimulator extends Thread {

    private final Random random = new Random();
    private boolean stopCondition = false;

    private static final Logger logger = Logger.getLogger(MalfunctionSimulator.class.getSimpleName());

    @Override
    public void run() {
        while (!stopCondition) {
            try {
                Thread.sleep(10000);

                if (random.nextDouble() < 0.1 && !MechanicState.getInstance().isNeedingMaintenance()) {
                    logger.info("Malfunction! Needing maintenance");
                    MechanicState.getInstance().needMaintenance();
                    MechanicState.getInstance().waitForMaintenanceDisinterest();
                }
            }
            catch (InterruptedException e) {
                stopCondition = true;
                break;
            }
        }
    }
}
