package CleaningRobot.RobotP2P.Communications;

import Utils.SharedBeans.RobotData;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

abstract class MyStreamObserver<T> implements StreamObserver<T> {
    private static final Logger logger = Logger.getLogger(Communications.class.getSimpleName());

    ManagedChannel channel;
    RobotData targetRobot;

    public MyStreamObserver(ManagedChannel channel, RobotData targetRobot) {
        this.channel = channel;
        this.targetRobot = targetRobot;
        CommQueue.getInstance().incrementOpenChannels();
    }
    @Override
    public abstract void onNext(T value);

    @Override
    public void onError(Throwable t) {
        logger.warning("error when talking to robot " + targetRobot.getRobotID());
        channel.shutdownNow();
        CommQueue.getInstance().addFailedRobot(targetRobot);
        CommQueue.getInstance().decrementOpenChannels();
    }
    @Override
    public void onCompleted() {
        channel.shutdownNow();
        CommQueue.getInstance().decrementOpenChannels();
    }
}
