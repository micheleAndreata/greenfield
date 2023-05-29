package AdminServer.Beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PollutionMeasurements {
    @XmlElement(name="measurements")
    private final HashMap<String, ArrayList<RobotMeasure>> measurements;
    private static PollutionMeasurements instance;

    public synchronized static PollutionMeasurements getInstance() {
        if (instance == null) {
            instance = new PollutionMeasurements();
        }
        return instance;
    }

    private PollutionMeasurements() {
        measurements = new HashMap<>();
    }

    public synchronized void addMeasurement(RobotMeasure measure) {
        if (!measurements.containsKey(measure.getRobotID())) {
            measurements.put(measure.getRobotID(), new ArrayList<>());
        }
        measurements.get(measure.getRobotID()).add(measure);
    }

    public synchronized Double getRobotAverage(String robotID, int n) {
        if (!measurements.containsKey(robotID) || n < 0) {
            return null;
        }
        ArrayList<RobotMeasure> robotMeasures = measurements.get(robotID);
        int size = robotMeasures.size();
        List<RobotMeasure> subList = robotMeasures.subList(size-Math.min(size,n), size);
        OptionalDouble average = subList.stream().mapToDouble(RobotMeasure::getValue).average();
        if (average.isPresent())
            return average.getAsDouble();
        else
            return null;
    }

    public synchronized Double getAverage(long t1, long t2) {
        double sum = 0;
        int i = 0;
        for (String robotID : measurements.keySet()) {
            for (RobotMeasure measure : measurements.get(robotID)) {
                if (t1 <= measure.getTimestamp() && measure.getTimestamp() <= t2) {
                    sum += measure.getValue();
                    i++;
                }
            }
        }
        if (i == 0) {
            return null;
        }
        return sum / i;
    }
}
