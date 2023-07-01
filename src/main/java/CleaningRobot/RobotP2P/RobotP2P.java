package CleaningRobot.RobotP2P;

import CleaningRobot.RobotP2P.Communications.Communications;
import CleaningRobot.RobotP2P.Communications.ErrorHandler;
import CleaningRobot.RobotP2P.GrpcServices.BotNetServiceImpl;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class RobotP2P {
    RobotList robotList;
    RobotData robotData;
    Server grpcServer;
    ErrorHandler errorHandler;
    private static final Logger logger = Logger.getLogger(RobotP2P.class.getSimpleName());

    public RobotP2P(RobotData robotData, String ServerAddress) {
        robotList = RobotList.getInstance();
        this.robotData = robotData;
        grpcServer = ServerBuilder.forPort(robotData.getGrpcPort()).addService(new BotNetServiceImpl()).build();
        errorHandler = new ErrorHandler(ServerAddress);
    }

    public void start() {
        try {
            grpcServer.start();
            //errorHandler.start();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        grpcServer.shutdown();
        //errorHandler.interrupt();
    }

    public void presentToOthers() {
        Communications.broadcastHello(robotData);
    }

    public void notifyExit() {
        Communications.broadcastGoodbye(robotData);
    }
}
