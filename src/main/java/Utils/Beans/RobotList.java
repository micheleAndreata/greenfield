package Utils.Beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RobotList {

    @XmlElement(name="robots")
    private final ArrayList<RobotData> robots;

    private static RobotList instance;

    private RobotList() {
        robots = new ArrayList<>();
    }

    public synchronized static RobotList getInstance() {
        if (instance == null) {
            instance = new RobotList();
        }
        return instance;
    }

    public synchronized boolean addRobot(RobotData robot) {
        if (!robots.contains(robot)) {
            robots.add(robot);
            return true;
        }
        return false;
    }

    public synchronized void removeRobot(String robotID) {
        RobotData robot = new RobotData(robotID, 0, "localhost", 0, new Position(0, 0));
        robots.remove(robot);
    }

    public synchronized ArrayList<RobotData> getList() {
        return new ArrayList<>(robots);
    }
}
