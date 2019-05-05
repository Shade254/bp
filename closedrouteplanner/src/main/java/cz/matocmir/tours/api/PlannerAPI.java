package cz.matocmir.tours.api;

import com.umotional.basestructures.BoundingBox;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.model.TourRequest;
import cz.matocmir.tours.model.TourResponse;
import cz.matocmir.tours.utils.IOUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

@Path("/")
public class PlannerAPI {
	private static final Logger log = Logger.getLogger(PlannerAPI.class);
	private static PlannerService service = new PlannerService();

	@GET
	@Path("/closed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClosedTour(@QueryParam("start") Integer startId, @QueryParam("minLength") Integer minLength,
			@QueryParam("maxLength") Integer maxLength, @DefaultValue("-1") @QueryParam("tours") Integer toursNumber,
			@DefaultValue("-1") @QueryParam("strict") Double strictness,
			@DefaultValue("-1") @QueryParam("factor") Double factor, @DefaultValue("1") @QueryParam("method") Boolean method) {

		TourRequest request = new TourRequest(startId, -1, factor, strictness, minLength, maxLength);
		TourResponse result = null;

		try {
			if(method) {
				result = service.getClosedTours2(request, toursNumber);
			} else {
				result = service.getClosedTours(request, toursNumber);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return Response.status(400).build();
		}

		if (result == null || result.getTours().length == 0) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/p2p")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getP2PTour(@QueryParam("start") Integer startId, @QueryParam("goal") Integer goalId,
			@QueryParam("minLength") Integer minLength, @QueryParam("maxLength") Integer maxLength,
			@DefaultValue("-1") @QueryParam("tours") Integer toursNumber,
			@DefaultValue("-1") @QueryParam("strict") Double strictness,
			@DefaultValue("-1") @QueryParam("factor") Double factor, @DefaultValue("1") @QueryParam("method") Boolean method) {

		TourRequest request = new TourRequest(startId, goalId, factor, strictness, minLength, maxLength);
		TourResponse result = null;
		try {
			if(method) {
				result = service.getP2PTours2(request, toursNumber);
			} else {
				result = service.getP2PTours(request, toursNumber);
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return Response.status(400).build();
		}

		if (result == null || result.getTours().length == 0) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/border")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGraphBorders() {
		return Response.status(200).entity(bbToGeojson(service.getGraphBorders())).build();
	}

	@GET
	@Path("/map")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNearestNode(@QueryParam("lat") double lat, @QueryParam("lon") double lon) {
		return Response.ok().entity(service.getNearestNode(new double[] { lon, lat })).build();
	}

	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		double latMin = 50.051500;
		double latMax = 50.100559;
		double lonMin = 14.395430;
		double lonMax = 14.532180;
		Random r = new Random();

		double rndLat1 = latMin + (latMax - latMin) * r.nextDouble();
		double rndLat2 = latMin + (latMax - latMin) * r.nextDouble();
		double rndLon1 = lonMin + (lonMax - lonMin) * r.nextDouble();
		double rndLon2 = lonMin + (lonMax - lonMin) * r.nextDouble();

		return String.format("[{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": [{\n"
				+ "      \"type\": \"Feature\",\n" + "      \"geometry\": {\n" + "        \"type\": \"LineString\",\n"
				+ "        \"coordinates\": [[%.5f, %.5f],[%.5f, %.5f]]},\n" + "      \"properties\": {\n"
				+ "\t       \"name\": \"Anglická\",\n" + "\t       \"from\": \"Škrétova\",\n"
				+ "\t       \"to\": \"null\",\n" + "\t       \"MC\": \"Praha 2\",\n"
				+ "\t       \"forwardTransits\": \"0\",\n" + "\t       \"backwardTransits\": \"0\"\n" + "      }\n"
				+ "    }]\n" + "}]", rndLon1, rndLat1, rndLon2, rndLat2);
	}

	private String bbToGeojson(BoundingBox bb) {
		String template =
				"{\n" + "  \"type\": \"Feature\",\n" + "  \"properties\": {\n" + "    \"name\": \"Graph Borders\"\n"
						+ "  },\n" + "  \"geometry\": {\n" + "    \"type\": \"Polygon\",\n" + "    \"coordinates\": [\n"
						+ "      [\n" + "        [%.5f, %.5f],\n" + "      [%.5f, %.5f],\n" + "      [%.5f, %.5f],\n"
						+ "      [%.5f, %.5f],\n" + "        [%.5f, %.5f]\n" + "      ]\n" + "    ]\n" + "  }\n" + "}";
		return String.format(template, bb.getMinLon(), bb.getMaxLat(), bb.getMinLon(), bb.getMinLat(), bb.getMaxLon(),
				bb.getMinLat(), bb.getMaxLon(), bb.getMaxLat(), bb.getMinLon(), bb.getMaxLat());
	}

	private String toursToGeojson(List<List<TourEdge>> edges, List<TourNode> turningPoints) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < edges.size(); i++) {
			String edgesVis = IOUtils.visualizeEdges(edges.get(i));
			int end = edgesVis.lastIndexOf("]");
			edgesVis = edgesVis.substring(0, end);
			edgesVis += ", " + IOUtils.nodeToFeatureString(turningPoints.get(i));
			edgesVis += "]\n" + "}";
			sb.append(edgesVis);
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1) + "]";
	}
}
