package AdminServer.services;

import AdminServer.Beans.Position;
import AdminServer.Beans.RobotData;
import AdminServer.Beans.RobotList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("robots")
public class BotDispatcherService {

    static Logger logger = Logger.getLogger(BotDispatcherService.class.getSimpleName());

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
        int district = 0;
        Position newPos = new Position(0, 0);
        newRobot.setDistrict(district);
        newRobot.setGridPos(newPos);

        if (newRobot.getRobotID() == null || !RobotList.getInstance().addRobot(newRobot)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        logger.info("Adding robot: " + newRobot);
        return Response.ok(RobotList.getInstance()).build();
    }

    @Path("remove/{robotID}")
    @DELETE
    public Response removeRobot(@PathParam("robotID") String robotID) {
        RobotList.getInstance().removeRobot(robotID);
        return Response.ok().build();
    }
}
