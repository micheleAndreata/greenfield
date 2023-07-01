package Utils.SharedBeans;

import CleaningRobot.RobotP2P.BotNetServiceOuterClass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RobotData {
    private String robotID;
    private int grpcPort;

    private String ipAddress;

    private int district;
    private Position gridPos;

    public RobotData(){}
    public RobotData(String robotID, int grpcPort, String ipAddress, int district, Position gridPos) {
        this.robotID = robotID;
        this.grpcPort = grpcPort;
        this.ipAddress = ipAddress;
        this.district = district;
        this.gridPos = gridPos;
    }

    public RobotData(BotNetServiceOuterClass.RobotDataProto robotDataProto){
        this(robotDataProto.getId(),
                robotDataProto.getGrpcPort(),
                robotDataProto.getIpAddress(),
                robotDataProto.getDistrict(),
                new Position(
                        robotDataProto.getGridPos().getX(),
                        robotDataProto.getGridPos().getY()
                ));
    }

    public RobotData(String robotID, int grpcPort, String ipAddress) {
        this.robotID = robotID;
        this.grpcPort = grpcPort;
        this.ipAddress = ipAddress;
    }

    public void setRobotID(String robotID) {
        this.robotID = robotID;
    }

    public void setGrpcPort(int grpcPort) {
        this.grpcPort = grpcPort;
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
                ", grpcPort=" + grpcPort +
                ", ipAddress='" + ipAddress + '\'' +
                ", district=" + district +
                ", gridPos=" + gridPos +
                '}';
    }

    public String getRobotID() {
        return robotID;
    }

    public int getGrpcPort() {
        return grpcPort;
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

    public BotNetServiceOuterClass.RobotDataProto toProto() {
        return BotNetServiceOuterClass.RobotDataProto.newBuilder()
                .setId(robotID)
                .setGrpcPort(grpcPort)
                .setIpAddress(ipAddress)
                .setDistrict(district)
                .setGridPos(BotNetServiceOuterClass.RobotDataProto.Position.newBuilder()
                        .setX(gridPos.getX())
                        .setY(gridPos.getY()))
                .build();
    }
}
