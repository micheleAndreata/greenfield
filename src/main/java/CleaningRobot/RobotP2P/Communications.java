package CleaningRobot.RobotP2P;

import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

import CleaningRobot.RobotP2P.BotNetServiceGrpc.*;
import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;

public class Communications {
    private static final Logger logger = Logger.getLogger(Communications.class.getSimpleName());
    public static void broadcastHello(RobotData presentingRobot) {
        // TODO: check if all robots responded
        // it could be that a robot registers but doesn't start its grpc server immediately,
        // another robots registers and presents to that robot that did not start its server
        // executing the onError method.
        for(RobotData robot : RobotList.getInstance().getList()) {
            ManagedChannel channel = ManagedChannelBuilder.
                    forTarget(robot.getIPAddress() + ":" + robot.getGrpcPort()).usePlaintext().build();
            BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

            BotNetServiceOuterClass.RobotDataProto request = presentingRobot.toProto();

            stub.hello(request, new StreamObserver<Status>() {
                @Override
                public void onNext(Status value) {
                    logger.info("status from robot " + robot.getRobotID() + ": " + value.getStatus());
                }
                @Override
                public void onError(Throwable t) {
                    logger.severe("error when presenting to robot " + robot.getRobotID());
                    logger.severe("cause: " + t.getMessage());
                }
                @Override
                public void onCompleted() {
                    channel.shutdownNow();
                }
            });
        }
    }
}
