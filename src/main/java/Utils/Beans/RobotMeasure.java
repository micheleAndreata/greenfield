package Utils.Beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RobotMeasure {
    private final String robotID;
    private final double value;
    private final long timestamp;

    public RobotMeasure(String robotID, double value, long timestamp) {
        this.robotID = robotID;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getRobotID() {
        return robotID;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotMeasure that = (RobotMeasure) o;
        return Double.compare(that.value, value) == 0 && timestamp == that.timestamp && Objects.equals(robotID, that.robotID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(robotID, value, timestamp);
    }

    @Override
    public String toString() {
        return "{" +
                "robotID='" + robotID + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
