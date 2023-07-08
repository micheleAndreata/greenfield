package CleaningRobot.RobotP2P.GrpcServices;

import CleaningRobot.RobotP2P.MechanicHandler.MechanicState;
import CleaningRobot.RobotP2P.MechanicHandler.PendingRequests;
import CleaningRobot.RobotP2P.MechanicHandler.RobotsAnswers;
import Utils.SharedBeans.RobotList;
import CleaningRobot.RobotP2P.BotNetServiceGrpc.*;
import CleaningRobot.RobotP2P.BotNetServiceOuterClass.*;
import Utils.SharedBeans.RobotData;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class BotNetServiceImpl extends BotNetServiceImplBase {

    private static final Logger logger = Logger.getLogger(BotNetServiceImpl.class.getSimpleName());
    @Override
    public void hello(RobotDataProto request, StreamObserver<Status> responseObserver) {
        logger.info("Robot " + request.getId() + " entered");
        RobotList.getInstance().addRobot(new RobotData(request));

        if (MechanicState.getInstance().isWaitingForMaintenance()) {
            RobotsAnswers.getInstance().addPositive(request.getId());
        }

        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void goodbye(RobotDataProto request, StreamObserver<Status> responseObserver) {

        if (MechanicState.getInstance().isWaitingForMaintenance()) {
            RobotsAnswers.getInstance().removeRobotFromEverywhere(request.getId());
        }
        else {
            RobotList.getInstance().removeRobot(request.getId());
        }

        logger.info("Robot " + request.getId() + " left");
        logger.info("Change in RobotList size: " + RobotList.getInstance().size());
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void requestMaintenance(MaintenanceRequest request, StreamObserver<Status> responseObserver) {
        MechanicState mechanicState = MechanicState.getInstance();
        if(mechanicState.isInMaintenance() ||
                (mechanicState.isNeedingMaintenance() && mechanicState.getRequestTimestamp() < request.getTimestamp())) {
            logger.info("Request maintenance from " + request.getRobotID() + " not accepted");
            PendingRequests.getInstance().add(request);
            Status response = Status.newBuilder().setStatus(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        else {
            logger.info("Request maintenance from " + request.getRobotID() + " accepted");
            Status response = Status.newBuilder().setStatus(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void freeMechanic(FreeMechanic request, StreamObserver<Status> responseObserver) {
        RobotsAnswers.getInstance().addPositive(request.getRobotID());
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void areYouOK(Empty request, StreamObserver<Status> responseObserver) {
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
