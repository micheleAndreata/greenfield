package CleaningRobot.RobotP2P.GrpcServices;

import CleaningRobot.RobotP2P.MechanicHandler.MechanicState;
import CleaningRobot.RobotP2P.MechanicHandler.RequestsQueue;
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
        RobotList.getInstance().addRobot(new RobotData(request));
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void goodbye(RobotDataProto request, StreamObserver<Status> responseObserver) {
        RobotList.getInstance().removeRobot(request.getId());
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void requestMaintenance(MaintenanceRequest request, StreamObserver<Status> responseObserver) {
        MechanicState mechanicState = MechanicState.getInstance();
        if(mechanicState.isInMaintenance() ||
                (mechanicState.isNeedingMaintenance() && mechanicState.getRequestTimestamp() < request.getTimestamp())) {
            logger.info("request maintenance from " + request.getRobotID() + " not accepted");
            RequestsQueue.getInstance().add(request);
            Status response = Status.newBuilder().setStatus(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        else {
            logger.info("request maintenance from " + request.getRobotID() + " accepted");
            Status response = Status.newBuilder().setStatus(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void freeMechanic(FreeMechanic request, StreamObserver<Status> responseObserver) {
        RobotsAnswers.getInstance().add(request.getRobotID());
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
