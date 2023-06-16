package CleaningRobot.RobotP2P;

import CleaningRobot.RobotP2P.GrpcServices.BotNetServiceImpl;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

import CleaningRobot.RobotP2P.BotNetServiceGrpc.*;
import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;

public class RobotP2P {
    RobotList robotList;
    RobotData robotData;
    Server grpcServer;
    private static final Logger logger = Logger.getLogger(RobotP2P.class.getSimpleName());

    public RobotP2P(RobotData robotData) {
        robotList = RobotList.getInstance();
        this.robotData = robotData;
        grpcServer = ServerBuilder.forPort(robotData.getGrpcPort()).addService(new BotNetServiceImpl()).build();
    }

    public void start() {
        try {
            grpcServer.start();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
        logger.info("Grpc server started");
    }

    public void presentToOthers() {
        Communications.broadcastHello(robotData);
    }
}
