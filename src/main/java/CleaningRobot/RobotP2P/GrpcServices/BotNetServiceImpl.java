package CleaningRobot.RobotP2P.GrpcServices;

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
}
