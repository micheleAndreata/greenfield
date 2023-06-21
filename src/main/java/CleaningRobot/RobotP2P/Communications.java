package CleaningRobot.RobotP2P;

import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.function.BiConsumer;
import java.util.logging.Logger;

import CleaningRobot.RobotP2P.BotNetServiceGrpc.*;
import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;

public class Communications {
    private static final Logger logger = Logger.getLogger(Communications.class.getSimpleName());

    public static void broadcastHello(RobotData sendingRobot) {
        broadcastMessage(sendingRobot, Communications::hello);
    }

    public static void broadcastGoodbye(RobotData sendingRobot) {
        broadcastMessage(sendingRobot, Communications::goodbye);
    }

    public static void broadcastMessage(RobotData sendingRobot, BiConsumer<RobotData, RobotData> messageHandler) {
        for(RobotData targetRobot : RobotList.getInstance().getList()) {
            if (targetRobot.equals(sendingRobot))
                continue;
            messageHandler.accept(sendingRobot, targetRobot);
        }
    }

    public static void hello(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        BotNetServiceOuterClass.RobotDataProto request = callingRobot.toProto();

        CommQueue.getInstance().incrementOpenChannels();

        stub.hello(request, new StreamObserver<Status>() {
            @Override
            public void onNext(Status value) { }
            @Override
            public void onError(Throwable t) {
                logger.warning("error when talking to robot " + targetRobot.getRobotID());
                channel.shutdownNow();
                CommQueue.getInstance().decrementOpenChannels();
                CommQueue.getInstance().addFailedRobot(targetRobot);
            }
            @Override
            public void onCompleted() {
                channel.shutdownNow();
                CommQueue.getInstance().decrementOpenChannels();
            }
        });
    }

    public static void goodbye(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        BotNetServiceOuterClass.RobotDataProto request = callingRobot.toProto();

        CommQueue.getInstance().incrementOpenChannels();

        stub.goodbye(request, new StreamObserver<Status>() {
            @Override
            public void onNext(Status value) { }
            @Override
            public void onError(Throwable t) {
                logger.warning("error when talking to robot " + targetRobot.getRobotID());
                channel.shutdownNow();
                CommQueue.getInstance().decrementOpenChannels();
                CommQueue.getInstance().addFailedRobot(targetRobot);
            }
            @Override
            public void onCompleted() {
                channel.shutdownNow();
                CommQueue.getInstance().decrementOpenChannels();
            }
        });
    }
}
