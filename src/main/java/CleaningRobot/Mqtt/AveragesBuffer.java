package CleaningRobot.Mqtt;

import CleaningRobot.Sensor.Simulator.Measurement;

import java.util.ArrayList;
import java.util.List;

public class AveragesBuffer {
    ArrayList<Measurement> averages;

    public static AveragesBuffer instance = new AveragesBuffer();
    public static AveragesBuffer getInstance() {
        return instance;
    }

    private AveragesBuffer() {
        averages = new ArrayList<>();
    }

    public synchronized void add(Measurement m) {
        averages.add(m);
    }

    public synchronized List<Measurement> readAllAndClean() {
        ArrayList<Measurement> avgs = new ArrayList<>(averages);
        averages.clear();
        return avgs;
    }
}
