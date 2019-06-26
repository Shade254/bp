package cz.matocmir.tours.model;

import cz.matocmir.tours.utils.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class AverageTourRoundnessTest {
	private static final String pathToGraph = "./src/main/resources/prague_min.csv";

	TourGraph graph;

	@Parameterized.Parameter
	public int[] ids1;

	@Parameterized.Parameter(1)
	public int[] ids2;

	@Parameterized.Parameter(2)
	public String name;

	Tour tour1;
	Tour tour2;

	@Parameterized.Parameters
	public static Collection<Object[]> data() {

		return Arrays.asList(new Object[][] {
				{ new int[]{471, 470, 1674, 470, 471}, new int[]{471, 470, 1674, 939, 574, 1525, 470, 471}, "first" },
				{ new int[]{834, 649, 829, 522, 829, 649, 834}, new int[]{834, 649, 829, 522, 523, 834}, "second" },
				{ new int[]{2118,2771,2650,2628,1471,2653,50,812,38,2775,2117,2112,2776,2777,2778,2167,811,2309,2779,2780,2781,2771,2118}, new int[]{2118,2771,2650,2628,1469,2168,2167,2778,2777,2776,2112,2117,2775,38,812,50,1549,2812,165,819,2656,2655,2654,2650,2771,2118}, "oval" },
				{ new int[]{511,2801,2384,580,581,615,2802,1942,2546,2806,2676,633,353,351,2547,331,354,2680,355,2708,2709,2711,2710,2707,2698,1711,2697,887,27,2215,888,388,2216,2201,2206,2207,2210,2212,2213,686,2223,2581,1018,1020,865,1020,1026,2581,2223,2222,2529,37,823,2535,2009,1546,814,378,2791,1538,821,851,257,820,832,250,833,255,254,839,844,855,2801,511
				}, new int[]{511,2801,2384,580,581,615,2802,1942,2546,2806,2676,633,353,351,2547,331,354,2680,355,2708,2709,2711,2710,2707,2698,1711,2697,887,27,2215,888,388,2216,2201,2206,2207,2210,2212,2213,686,2223, 2222,2529,37,823,2535,2009,1546,814,378,2791,1538,821,851,257,820,832,250,833,255,254,839,844,855,2801,511
				}, "cutted" },

		});
	}


	@Before
	public void setUp() throws Exception {

		assert ids1[0] == ids2[0];
		assert ids1[ids1.length-1] == ids2[ids2.length-1];
		assert ids1[ids1.length-1] == ids1[0];
		assert ids2[ids2.length-1] == ids2[0];


		graph = TourGraph.graphFromCSV(pathToGraph, 2065);
		System.out.println("=================FIRST====================");
		tour1 = createTour(ids1);
		System.out.println("==================SECOND===================");
		tour2 = createTour(ids2);
		System.out.println("=================DONE====================");


		try (PrintWriter out = new PrintWriter(name+"1.json")) {
			out.println(IOUtils.visualizeEdges(tour1.getOriginalEdges()));
		}

		try (PrintWriter out = new PrintWriter(name+"2.json")) {
			out.println(IOUtils.visualizeEdges(tour2.getOriginalEdges()));
		}
	}

	private Tour createTour(int[] ids) {
		List<TourEdge> edges = new ArrayList<>();

		for(int i = 0;i<ids.length;i++){
			if(i == ids.length-1){
				return new Tour(edges, graph.getNode(ids[i]), graph.getNode(ids[i]));
			}

			TourEdge found = graph.getEdge(ids[i], ids[i+1]);

			if(found == null){
				fail("Edge " + ids[i] + " -> " + ids[i+1] + " not found");
			}

			edges.add(found);

		}
		return null;
	}

	@Test
	public void printProgress() {
		double roundness1 = tour1.getRoundness();
		double roundness2 = tour2.getRoundness();
		System.out.println(name);
		System.out.println(tour1.length + " - " + tour2.length);
		System.out.println(roundness1 + (roundness1>roundness2 ? ">" : "=<") + roundness2);
		assertTrue(roundness1 > roundness2);
		System.out.println("=====================================");
	}
}
