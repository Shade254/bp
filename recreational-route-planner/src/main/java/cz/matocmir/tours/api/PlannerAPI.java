package cz.matocmir.tours.api;

import com.umotional.basestructures.BoundingBox;
import cz.matocmir.tours.PlannerService;
import cz.matocmir.tours.model.TourRequest;
import cz.matocmir.tours.model.TourResponse;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/***
 * Class wraping exposed REST API, individual enpoints of the API and its parameters are described in the project paper Chapter 5
 */
@Path("/")
public class PlannerAPI {
	private static final Logger log = Logger.getLogger(PlannerAPI.class);
	private static PlannerService service = new PlannerService();

	@GET
	@Path("/closed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClosedTours(@QueryParam("start") Integer startId, @QueryParam("minLength") Integer minLength,
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

//		File dir = new File("./result" + Path2DistanceRatioFilter.counter);
//		try {
//			for(int i = 0;i<result.getTours().length;i++){
//				System.out.println("Output " + i);
//				String e = IOUtils.visualizeEdges(result.getTours()[i].getOriginalEdges());
//				BufferedWriter br = new BufferedWriter(new FileWriter(dir.getName() + File.separator + "tour" + i + ".json"));
//				br.write(e);
//				br.flush();
//				br.close();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Arrays.stream(result.getTours()).forEach(t -> {
//
//		});
//

		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/p2p")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getP2PTours(@QueryParam("start") Integer startId, @QueryParam("goal") Integer goalId,
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

	private String bbToGeojson(BoundingBox bb) {
		String template =
				"{\n" + "  \"type\": \"Feature\",\n" + "  \"properties\": {\n" + "    \"name\": \"Graph Borders\"\n"
						+ "  },\n" + "  \"geometry\": {\n" + "    \"type\": \"Polygon\",\n" + "    \"coordinates\": [\n"
						+ "      [\n" + "        [%.5f, %.5f],\n" + "      [%.5f, %.5f],\n" + "      [%.5f, %.5f],\n"
						+ "      [%.5f, %.5f],\n" + "        [%.5f, %.5f]\n" + "      ]\n" + "    ]\n" + "  }\n" + "}";
		return String.format(template, bb.getMinLon(), bb.getMaxLat(), bb.getMinLon(), bb.getMinLat(), bb.getMaxLon(),
				bb.getMinLat(), bb.getMaxLon(), bb.getMaxLat(), bb.getMinLon(), bb.getMaxLat());
	}
}
