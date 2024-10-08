package CleaningRobot.RobotP2P.Communications;

import CleaningRobot.RobotP2P.BotNetServiceGrpc;
import CleaningRobot.RobotP2P.MechanicHandler.MechanicState;
import CleaningRobot.RobotP2P.MechanicHandler.RobotsAnswers;
import Utils.SharedBeans.RobotData;
import Utils.SharedBeans.RobotList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import CleaningRobot.RobotP2P.BotNetServiceGrpc.*;
import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;

public class Communications {

    public static void broadcastHello(RobotData callingRobot) {
        broadcastMessage(callingRobot, Communications::hello);
    }

    public static void broadcastGoodbye(RobotData callingRobot) {
        broadcastMessage(callingRobot, Communications::goodbye);
    }

    public static void broadcastGoodbye(RobotData callingRobot, List<RobotData> broadcastList) {
        for (RobotData targetRobot : broadcastList) {
            if (targetRobot.equals(callingRobot))
                continue;
            goodbye(callingRobot, targetRobot);
        }
    }

    public static void broadcastRequestMaintenance(RobotData callingRobot) {
        RobotsAnswers.getInstance().addPositive(callingRobot.getRobotID());
        broadcastMessage(callingRobot, Communications::requestMaintenance);
    }

    public static void broadcastMessage(RobotData callingRobot, BiConsumer<RobotData, RobotData> messageHandler) {
        for(RobotData targetRobot : RobotList.getInstance().getList()) {
            if (targetRobot.equals(callingRobot))
                continue;
            messageHandler.accept(callingRobot, targetRobot);
        }
    }

    public static void areYouOK(RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        Empty request = Empty.newBuilder().build();

        stub.areYouOK(request, new MyStreamObserver<Status>(channel, targetRobot) {
            @Override
            public void onNext(Status value) {}
        });
    }

    public static void hello(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        RobotDataProto request = callingRobot.toProto();

        stub.hello(request, new MyStreamObserver<Status>(channel, targetRobot) {
            @Override
            public void onNext(Status value) {}
        });
    }

    public static void goodbye(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        RobotDataProto request = callingRobot.toProto();

        stub.goodbye(request, new MyStreamObserver<Status>(channel, targetRobot) {
            @Override
            public void onNext(Status value) {}
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

        stub.requestMaintenance(request, new MyStreamObserver<Status>(channel, targetRobot) {
            @Override
            public void onNext(Status value) {
                if (value.getStatus())
                    RobotsAnswers.getInstance().addPositive(targetRobot.getRobotID());
                else
                    RobotsAnswers.getInstance().addNegative(targetRobot.getRobotID());
            }
        });
    }

    public static void freeMechanic(RobotData callingRobot, RobotData targetRobot) {
        ManagedChannel channel = ManagedChannelBuilder.
                forTarget(targetRobot.getIPAddress() + ":" + targetRobot.getGrpcPort()).usePlaintext().build();
        BotNetServiceStub stub = BotNetServiceGrpc.newStub(channel);

        FreeMechanic request = FreeMechanic.newBuilder().setRobotID(callingRobot.getRobotID()).build();

        stub.freeMechanic(request, new MyStreamObserver<Status>(channel, targetRobot) {
            @Override
            public void onNext(Status value) {}
        });
    }
}
