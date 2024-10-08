package AdminServer.services;

import AdminServer.Beans.PollutionMeasurements;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("stats")
public class StatsService {
    @Path("robot-average/{robotID}:{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getRobotAverage(@PathParam("robotID") String robotID, @PathParam("n") int n) {
        Double average = PollutionMeasurements.getInstance().getRobotAverage(robotID, n);
        if (average != null) {
            return Response.ok(average).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No measurements found for the given inputs")
                    .build();
        }
    }

    @Path("robots-average/{t1}:{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverage(@PathParam("t1") long t1, @PathParam("t2") long t2) {
        Double average = PollutionMeasurements.getInstance().getAverage(t1, t2);
        if (average != null) {
            return Response.ok(average).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No measurement found in time range " + t1 + " to " + t2)
                    .build();
        }
    }

    @Path("number-of-measurements")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getTotalNumberOfMeasurements() {
        return Response.ok(PollutionMeasurements.getInstance().getNumberOfMeasurements()).build();
    }

    @Path("number-of-measurements/{robotID}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getTotalNumberOfMeasurements(@PathParam("robotID") String robotID) {
        Integer numberOfMeasurements = PollutionMeasurements.getInstance().getNumberOfMeasurements(robotID);
        if (numberOfMeasurements == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(numberOfMeasurements).build();
    }
}
