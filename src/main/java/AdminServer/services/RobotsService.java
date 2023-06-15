package AdminServer.services;

import AdminServer.Beans.Districts;
import Utils.SharedBeans.Position;
import Utils.SharedBeans.RobotData;
import AdminServer.Beans.RobotList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

@Path("robots")
public class RobotsService {
    static Logger logger = Logger.getLogger(RobotsService.class.getSimpleName());

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getRobots() {
        return Response.ok(RobotList.getInstance().getList()).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addRobot(RobotData newRobot) {
        int district = Districts.getInstance().findBestDistrict();
        Position newPos = Districts.getInstance().findBestPosition(district);
        newRobot.setDistrict(district);
        newRobot.setGridPos(newPos);

        if (newRobot.getRobotID() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Robot ID cannot be null")
                    .build();
        }

        if (!RobotList.getInstance().addRobot(newRobot)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Robot with ID " + newRobot.getRobotID() + " already exists")
                    .build();
        }
        Districts.getInstance().increment(district);
        logger.info("Added robot: " + newRobot);
        logger.info("Current districts distribution: " + Arrays.toString(Districts.getInstance().getDistricts()));
        return Response.ok(RobotList.getInstance()).build();
    }

    @Path("remove/{robotID}")
    @DELETE
    public Response removeRobot(@PathParam("robotID") String robotID) {
        int district = RobotList.getInstance().getRobot(robotID).getDistrict();
        RobotList.getInstance().removeRobot(robotID);
        Districts.getInstance().decrement(district);
        logger.info("removed robot with ID " + robotID);
        logger.info("Current districts distribution: " + Arrays.toString(Districts.getInstance().getDistricts()));
        return Response.ok().build();
    }
}
