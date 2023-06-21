package CleaningRobot.Sensor;

import CleaningRobot.Mqtt.AveragesBuffer;
import CleaningRobot.Sensor.Simulator.Measurement;

import java.util.List;
import java.util.logging.Logger;

import static Utils.Config.WINDOW_SIZE;

public class SensorListener extends Thread {

    private final MBuffer mBuffer;
    private final AveragesBuffer avgsBuffer;
    private volatile boolean stopCondition = false;

    private static final Logger logger = Logger.getLogger(SensorListener.class.getSimpleName());

    public SensorListener() {
        this.mBuffer = MBuffer.getInstance();
        this.avgsBuffer = AveragesBuffer.getInstance();
    }

    @Override
    public void run() {
        final int windowSize = WINDOW_SIZE;

        while(!stopCondition) {
            List<Measurement> measurements = mBuffer.readAllAndClean();

            if (measurements == null)
                break;

            String id = measurements.get(0).getId();
            String type = measurements.get(0).getType();
            double averageValue = 0;
            long averageTimestamp = 0;
            for (int i=0; i < windowSize; i++) {
                averageValue += measurements.get(i).getValue();
                averageTimestamp += measurements.get(i).getTimestamp();
            }
            averageValue = averageValue / windowSize;
            averageTimestamp = averageTimestamp / windowSize;
            Measurement average = new Measurement(id, type, averageValue, averageTimestamp);

            //logger.info("\nsize: " + measurements.size() + ", average: " + average);

            avgsBuffer.add(average);
        }
    }

    public void stopMeGently() {
        stopCondition = true;
    }
}
