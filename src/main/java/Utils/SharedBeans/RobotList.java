package Utils.SharedBeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RobotList {

    @XmlElement(name="robots")
    private ArrayList<RobotData> robots;

    private static final RobotList instance = new RobotList();

    private RobotList() {
        robots = new ArrayList<>();
    }

    public static RobotList getInstance() {
        return instance;
    }

    public synchronized boolean addRobot(RobotData robot) {
        if (!robots.contains(robot)) {
            robots.add(robot);
            return true;
        }
        return false;
    }

    public synchronized RobotData getRobot(String robotID) {
        RobotData robot = new RobotData(robotID, 0, "localhost", 0, new Position(0, 0));
        try {
            return robots.get(robots.indexOf(robot));
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public synchronized boolean contains(String robotID) {
        return robots.contains(new RobotData(robotID, 0, "localhost", 0, new Position(0, 0)));
    }

    public synchronized RobotData getRobot(RobotData robot) {
        try {
            return robots.get(robots.indexOf(robot));
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public synchronized void removeRobot(String robotID) {
        RobotData robot = new RobotData(robotID, 0, "localhost", 0, new Position(0, 0));
        robots.remove(robot);
    }

    public synchronized int size() {
        return robots.size();
    }

    public synchronized ArrayList<RobotData> getList() {
        return new ArrayList<>(robots);
    }

    public synchronized void setRobots(ArrayList<RobotData> robots) {
        this.robots = robots;
    }

    public synchronized boolean isPortAvailable(int port) {
        for(RobotData robot : robots) {
            if (robot.getGrpcPort() == port) {
                return false;
            }
        }
        return true;
    }
}
