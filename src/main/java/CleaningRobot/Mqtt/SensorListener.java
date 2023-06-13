package CleaningRobot.Mqtt;

import CleaningRobot.Mqtt.AveragesBuffer;
import CleaningRobot.Sensor.MBuffer;
import CleaningRobot.Sensor.Simulator.Measurement;

import java.util.ArrayList;

import static Utils.Config.WINDOW_SIZE;

public class SensorListener extends Thread {

    private final MBuffer mBuffer;
    private final AveragesBuffer avgsBuffer;
    private volatile boolean stopCondition = false;

    public SensorListener() {
        this.mBuffer = MBuffer.getInstance();
        this.avgsBuffer = AveragesBuffer.getInstance();
    }

    @Override
    public void run() {
        final int windowSize = WINDOW_SIZE;

        while(!stopCondition) {
            ArrayList<Measurement> measurements = (ArrayList<Measurement>) mBuffer.readAllAndClean();

            String id = measurements.get(0).getId();
            String type = measurements.get(0).getType();
            double averageValue = 0;
            int averageTimestamp = 0;
            for (int i=0; i < windowSize; i++) {
                averageValue += measurements.get(i).getValue();
                averageTimestamp += measurements.get(i).getTimestamp();
            }
            averageValue = averageValue / windowSize;
            averageTimestamp = averageTimestamp / windowSize;
            avgsBuffer.add(new Measurement(id, type, averageValue, averageTimestamp));
        }
    }

    public void stopMeGently() {
        stopCondition = true;
    }
}
