package CleaningRobot.RobotP2P.Communications;

import CleaningRobot.RobotP2P.BotNetServiceGrpc;
import CleaningRobot.RobotP2P.MechanicHandler.MechanicState;
import CleaningRobot.RobotP2P.MechanicHandler.RobotsAnswers;
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

    public static void broadcastHello(RobotData callingRobot) {
        broadcastMessage(callingRobot, Communications::hello);
    }

    public static void broadcastGoodbye(RobotData callingRobot) {
        broadcastMessage(callingRobot, Communications::goodbye);
    }

    public static void broadcastRequestMaintenance(RobotData callingRobot) {
        RobotsAnswers.getInstance().add(callingRobot.getRobotID());
        broadcastMessage(callingRobot, Communications::requestMaintenance);
    }

    public static void broadcastMessage(RobotData callingRobot, BiConsumer<RobotData, RobotData> messageHandler) {
        for(RobotData targetRobot : RobotList.getInstance().getList()) {
            if (targetRobot.equals(callingRobot))
                continue;
            messageHandler.accept(callingRobot, targetRobot);
        }
    }

    public static void hello(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        RobotDataProto request = callingRobot.toProto();

        //CommQueue.getInstance().incrementOpenChannels();

        stub.hello(request, new StreamObserver<Status>() {
            @Override
            public void onNext(Status value) { }
            @Override
            public void onError(Throwable t) {
                logger.warning("error when talking to robot " + targetRobot.getRobotID());
                channel.shutdownNow();
                //CommQueue.getInstance().decrementOpenChannels();
                //CommQueue.getInstance().addFailedRobot(targetRobot);
            }
            @Override
            public void onCompleted() {
                channel.shutdownNow();
                //CommQueue.getInstance().decrementOpenChannels();
            }
        });
    }

    public static void goodbye(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        RobotDataProto request = callingRobot.toProto();

        //CommQueue.getInstance().incrementOpenChannels();

        stub.goodbye(request, new StreamObserver<Status>() {
            @Override
            public void onNext(Status value) { }
            @Override
            public void onError(Throwable t) {
                logger.warning("error when talking to robot " + targetRobot.getRobotID());
                channel.shutdownNow();
                //CommQueue.getInstance().decrementOpenChannels();
                //CommQueue.getInstance().addFailedRobot(targetRobot);
            }
            @Override
            public void onCompleted() {
                channel.shutdownNow();
                //CommQueue.getInstance().decrementOpenChannels();
            }
        });
    }

    public static void requestMaintenance(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        MaintenanceRequest request = MaintenanceRequest.newBuilder()
                .setRobotID(callingRobot.getRobotID())
                .setTimestamp(MechanicState.getInstance().getRequestTimestamp())
                .build();

        stub.requestMaintenance(request, new StreamObserver<Status>() {
            @Override
            public void onNext(Status value) {
                if (value.getStatus())
                    RobotsAnswers.getInstance().add(targetRobot.getRobotID());
            }
            @Override
            public void onError(Throwable t) {
                logger.severe("error when talking to robot " + targetRobot.getRobotID());
                channel.shutdownNow();
            }
            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });
    }

    public static void freeMechanic(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        FreeMechanic request = FreeMechanic.newBuilder().setRobotID(callingRobot.getRobotID()).build();

        stub.freeMechanic(request, new StreamObserver<Status>() {
            @Override
            public void onNext(Status value) {}

            @Override
            public void onError(Throwable t) {
                logger.severe("error when talking to robot " + targetRobot.getRobotID());
            }
            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }
        });
    }
}
