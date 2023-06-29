package CleaningRobot.RobotP2P.GrpcServices;

import CleaningRobot.RobotP2P.Mechanic.MechanicState;
import CleaningRobot.RobotP2P.Mechanic.RequestsQueue;
import CleaningRobot.RobotP2P.Mechanic.RobotsAnswers;
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
            RequestsQueue.getInstance().add(request);
            Status response = Status.newBuilder().setStatus(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        else {
            Status response = Status.newBuilder().setStatus(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void freeMechanic(Status request, StreamObserver<Status> responseObserver) {
        RobotsAnswers.getInstance().add(request.getRobotID());
        Status response = Status.newBuilder().setStatus(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
