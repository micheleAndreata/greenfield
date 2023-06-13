package Utils.Beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RobotData {
    private String robotID;
    private int port;

    private String ipAddress;

    private int district;
    private Position gridPos;

    public RobotData(){}
    public RobotData(String robotID, int port, String ipAddress, int district, Position gridPos) {
        this.robotID = robotID;
        this.port = port;
        this.ipAddress = ipAddress;
        this.district = district;
        this.gridPos = gridPos;
    }

    public RobotData(String robotID, int port, String ipAddress) {
        this.robotID = robotID;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public void setRobotID(String robotID) {
        this.robotID = robotID;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String setIPAddress() { return ipAddress; }

    public void setDistrict(int district) {
        this.district = district;
    }

    public void setGridPos(Position gridPos) {
        this.gridPos = gridPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotData robotData = (RobotData) o;
        return Objects.equals(robotID, robotData.robotID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(robotID);
    }

    @Override
    public String toString() {
        return "RobotData{" +
                "robotID='" + robotID + '\'' +
                ", port=" + port +
                ", district=" + district +
                ", gridPos=" + gridPos +
                '}';
    }

    public String getRobotID() {
        return robotID;
    }

    public int getPort() {
        return port;
    }

    public String getIPAddress() {
        return ipAddress;
    }

    public int getDistrict() {
        return district;
    }

    public Position getGridPos() {
        return gridPos;
    }
}
