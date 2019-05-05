package cz.matocmir.tours.api;

import cz.matocmir.tours.model.Tour;
import cz.matocmir.tours.model.TourRequest;
import cz.matocmir.tours.model.TourResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PlannerServiceTest {
	private PlannerService service;
	private final String csvHeader = "startNode;goalNode;minLength;maxLength;strictness;factor;";

	@Before
	public void setUp() throws Exception {
		service = new PlannerService();
	}

	@Test
	public void runtimeTestClosed() {

		List<TourRequest> requests = new ArrayList<>();
		requests.add(new TourRequest(1392, -1, 400, 0.9, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 700, 0.9, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 900, 0.9, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 2500, 0.9, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 900, 1.0, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 900, 0.7, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 900, 0.5, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 900, 0.3, 4000, 6000));
		requests.add(new TourRequest(1392, -1, 900, 0.9, 6000, 8000));
		requests.add(new TourRequest(1392, -1, 900, 0.9, 8000, 10000));
		requests.add(new TourRequest(1392, -1, 900, 0.9, 10000, 12000));

		for (TourRequest request : requests) {
			System.out.println("Testing: " + request.toString());
			TourResponse response1 = service.getClosedTours(request, 20);
			TourResponse response2 = service.getClosedTours2(request, 20);

			System.out.println(
					"Method 1 time: " + response1.getResponseTime() + " ms" + " tours: " + response1.getTours().length);
			System.out.println(
					"Method 2 time: " + response2.getResponseTime() + " ms" + " tours: " + response2.getTours().length);

			double average1 = Arrays.stream(response1.getTours()).mapToDouble(Tour::getRoundness).average()
					.getAsDouble();
			double average2 = Arrays.stream(response2.getTours()).mapToDouble(Tour::getRoundness).average()
					.getAsDouble();

			System.out.println("Method 1 avg roundness: " + average1);
			System.out.println("Method 2 avg roundness: " + average2);

			Tour best1 = Arrays.stream(response1.getTours()).min(Comparator.comparingDouble(Tour::getTotalCost)).get();
			Tour best2 = Arrays.stream(response2.getTours()).min(Comparator.comparingDouble(Tour::getTotalCost)).get();

			System.out.println(
					"Method 1 best score: " + best1.getTotalCost() + " with roundness " + best1.getRoundness());
			System.out.println(
					"Method 2 best score: " + best2.getTotalCost() + " with roundness " + best2.getRoundness());
			System.out.println("=============================================================================");
		}

	}
}