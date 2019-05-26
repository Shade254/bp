package cz.matocmir.tours;

import cz.matocmir.tours.model.Tour;
import cz.matocmir.tours.model.TourRequest;
import cz.matocmir.tours.model.TourResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/***
 * Class containing all experiments described in the project paper (Chapter 6)
 *
 * When using other graph than prague.csv, you have to choose the set of starting nodes in the code.
 */
public class ExperimentMain {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String path = null;
		File f = null;
		while(f == null) {
			System.out.print("Enter path to the graph\n>");
			path = sc.nextLine();
			f = new File(path);
			if(!f.exists() || !f.isFile()){
				System.out.println("File does not exist");
				f = null;
			}
		}

		PlannerService service = new PlannerService(path);

		String choice = null;
		while(choice == null) {
			System.out.print("Enter experiment to run (c - Candidate, l - Length, f - Factor, s - Strictness)\n>");
			choice = sc.nextLine();
			if(choice.equals("c")){
				candsTest(service);
			} else if(choice.equals("l")){
				lengthTest(service);
			} else if(choice.equals("f")){
				factorTest(service);
			} else if(choice.equals("r")){
				strictnessTest(service);
			} else{
				System.out.println("Not a valid choice");
			}


			choice = null;
		}
	}

	public static void candsTest(PlannerService service) {
		List<Integer> starts = new ArrayList<>();

		//		LONG STARTS
		starts.add(205121);
		starts.add(143883);
		starts.add(204053);
		starts.add(109711);
		starts.add(120076);
		starts.add(190408);
		starts.add(112162);
		starts.add(186794);
		starts.add(200821);
		starts.add(201353);

		TourRequest request = new TourRequest(-1, -1, 450, 0.9, 16000, 18000);

		int minTries = 5;
		int maxTries = 50;

		List<Double> scores1 = new ArrayList<>();
		List<Double> scores2 = new ArrayList<>();
		List<Double> time1 = new ArrayList<>();
		List<Double> time2 = new ArrayList<>();
		List<Double> fail1 = new ArrayList<>();
		List<Double> fail2 = new ArrayList<>();

		for (int i = minTries; i < maxTries; i++) {
			double totalScore1 = 0;
			double totalScore2 = 0;
			double totalTime1 = 0;
			double totalTime2 = 0;
			double totalFails1 = 0;
			double totalFails2 = 0;

			System.out.println("Testing " + i);

			for (Integer id : starts) {
				request.setStartNode(id);
				System.out.println("Start " + id);
				TourResponse response1 = service.getClosedTours(request, i);
				TourResponse response2 = service.getClosedTours2(request, i);

				Tour best1 = Arrays.stream(response1.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor()))).get();
				Tour best2 = Arrays.stream(response2.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor()))).get();

				totalScore1 += best1.getFinalMeanCost(request.getFactor());
				totalScore2 += best2.getFinalMeanCost(request.getFactor());
				totalTime1 += response1.getResponseTime();
				totalTime2 += response2.getResponseTime();
				totalFails1 += ((double) service.notFound1Last / (double) service.tried1Last);
				totalFails2 += ((double) service.notFound2Last / (double) service.tried2Last);
			}

			scores1.add(totalScore1 / starts.size());
			scores2.add(totalScore2 / starts.size());
			System.out.println("Scores: " + scores1.get(scores1.size() - 1) + "/" + scores2.get(scores2.size() - 1));
			time1.add(totalTime1 / starts.size());
			time2.add(totalTime2 / starts.size());
			System.out.println("Times: " + time1.get(scores1.size() - 1) + "/" + time2.get(scores2.size() - 1));
			fail1.add(totalFails1 / starts.size());
			fail2.add(totalFails2 / starts.size());
			System.out.println("Fails: " + fail1.get(scores1.size() - 1) + "/" + fail2.get(scores2.size() - 1));
			System.out.println();
			System.out.println();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(scores1.toString());
		sb.append(scores2.toString());
		sb.append("===============================================");
		sb.append(time1.toString());
		sb.append(time2.toString());
		sb.append("===============================================");
		sb.append(fail1.toString());
		sb.append(fail2.toString());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("candsTest.txt"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void lengthTest(PlannerService service) {
		List<Integer> starts = new ArrayList<>();
		starts.add(205121);
		starts.add(143883);
		starts.add(204053);
		starts.add(109711);
		starts.add(120076);
		starts.add(190408);
		starts.add(112162);
		starts.add(186794);
		starts.add(200821);
		starts.add(201353);

		List<Double> scores1 = new ArrayList<>();
		List<Double> scores2 = new ArrayList<>();
		List<Double> time1 = new ArrayList<>();
		List<Double> time2 = new ArrayList<>();
		List<Double> fail1 = new ArrayList<>();
		List<Double> fail2 = new ArrayList<>();
		List<Integer> nf1 = new ArrayList<>();
		List<Integer> nf2 = new ArrayList<>();

		TourRequest request = new TourRequest(-1, -1, 450, 0.9, 4000, 6000);
		int minCoef = 1;
		int maxCoef = 23;

		int candidateCount = 30;
		int interval = 2200;

		for (int i = minCoef; i < maxCoef; i++) {
			double totalScore1 = 0;
			double totalScore2 = 0;
			double totalTime1 = 0;
			double totalTime2 = 0;
			double totalFails1 = 0;
			double totalFails2 = 0;

			System.out.println("Testing " + (i * 1000));
			int counter1NF = 0;
			int counter2NF = 0;
			for (Integer id : starts) {
				request.setStartNode(id);
				request.setMinLength(i * 1000);
				request.setMaxLength((i * 1000) + interval);

				System.out.println("Start " + id);
				TourResponse response1 = service.getClosedTours(request, candidateCount);
				TourResponse response2 = service.getClosedTours2(request, candidateCount);

				Optional<Tour> best1 = Arrays.stream(response1.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor())));
				Optional<Tour> best2 = Arrays.stream(response2.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor())));

				if (!best1.isPresent()) {
					counter1NF++;
					continue;
				}

				if (!best2.isPresent()) {
					counter2NF++;
					continue;
				}

				totalScore1 += best1.get().getFinalMeanCost(request.getFactor());
				totalScore2 += best2.get().getFinalMeanCost(request.getFactor());
				totalTime1 += ((double) response1.getResponseTime());
				totalTime2 += ((double) response2.getResponseTime());
				totalFails1 += ((double) service.notFound1Last / (double) service.tried1Last);
				totalFails2 += ((double) service.notFound2Last / (double) service.tried2Last);
			}
			nf1.add(counter1NF);
			nf2.add(counter2NF);

			scores1.add(totalScore1 / (starts.size() - counter1NF));
			scores2.add(totalScore2 / (starts.size() - counter2NF));
			System.out.println("Scores: " + scores1.get(scores1.size() - 1) + "/" + scores2.get(scores2.size() - 1));
			time1.add(totalTime1 / (starts.size() - counter1NF));
			time2.add(totalTime2 / (starts.size() - counter2NF));
			System.out.println("Times: " + time1.get(scores1.size() - 1) + "/" + time2.get(scores2.size() - 1));
			fail1.add(totalFails1 / (starts.size() - counter1NF));
			fail2.add(totalFails2 / (starts.size() - counter2NF));
			System.out.println("Fails: " + fail1.get(scores1.size() - 1) + "/" + fail2.get(scores2.size() - 1));
			System.out.println();
			System.out.println();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(scores1.toString());
		sb.append(scores2.toString());
		sb.append("===============================================");
		sb.append(time1.toString());
		sb.append(time2.toString());
		sb.append("===============================================");
		sb.append(fail1.toString());
		sb.append(fail2.toString());
		sb.append("===============================================");
		sb.append(nf1.toString());
		sb.append(nf2.toString());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("lengthTest.txt"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void strictnessTest(PlannerService service) {
		List<Integer> starts = new ArrayList<>();

		starts.add(205121);
		starts.add(143883);
		starts.add(204053);
		starts.add(109711);
		starts.add(120076);
		starts.add(190408);
		starts.add(112162);
		starts.add(186794);
		starts.add(200821);
		starts.add(201353);

		List<Double> scores1 = new ArrayList<>();
		List<Double> scores2 = new ArrayList<>();
		List<Double> time1 = new ArrayList<>();
		List<Double> time2 = new ArrayList<>();
		List<Double> fail1 = new ArrayList<>();
		List<Double> fail2 = new ArrayList<>();
		List<Integer> nf1 = new ArrayList<>();
		List<Integer> nf2 = new ArrayList<>();
		List<Double> round1 = new ArrayList<>();
		List<Double> round2 = new ArrayList<>();

		double minCoef = 0.05;
		double maxCoef = 1;

		int candidateCount = 20;
		int minLength = 13000;
		int maxLength = 16000;
		TourRequest request = new TourRequest(-1, -1, 450, 0.9, minLength, maxLength);

		for (double i = minCoef; i <= maxCoef; i += 0.05) {
			double totalScore1 = 0;
			double totalScore2 = 0;
			double totalTime1 = 0;
			double totalTime2 = 0;
			double totalFails1 = 0;
			double totalFails2 = 0;
			double totalRound1 = 0;
			double totalRound2 = 0;

			System.out.println("Testing " + i);
			int counter1NF = 0;
			int counter2NF = 0;
			for (Integer id : starts) {
				request.setStartNode(id);
				request.setStrictness(i);

				System.out.println("Start " + id);
				TourResponse response1 = service.getClosedTours(request, candidateCount);
				TourResponse response2 = service.getClosedTours2(request, candidateCount);

				Optional<Tour> best1 = Arrays.stream(response1.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor())));
				Optional<Tour> best2 = Arrays.stream(response2.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor())));

				if (!best1.isPresent()) {
					counter1NF++;
					continue;
				}

				if (!best2.isPresent()) {
					counter2NF++;
					continue;
				}

				totalScore1 += best1.get().getFinalMeanCost(request.getFactor());
				totalScore2 += best2.get().getFinalMeanCost(request.getFactor());
				totalTime1 += ((double) response1.getResponseTime());
				totalTime2 += ((double) response2.getResponseTime());
				totalFails1 += ((double) service.notFound1Last / (double) service.tried1Last);
				totalFails2 += ((double) service.notFound2Last / (double) service.tried2Last);
				totalRound1 += best1.get().getRoundness();
				totalRound2 += best2.get().getRoundness();
			}
			nf1.add(counter1NF);
			nf2.add(counter2NF);

			scores1.add(totalScore1 / (starts.size() - counter1NF));
			scores2.add(totalScore2 / (starts.size() - counter2NF));
			System.out.println("Scores: " + scores1.get(scores1.size() - 1) + "/" + scores2.get(scores2.size() - 1));
			time1.add(totalTime1 / (starts.size() - counter1NF));
			time2.add(totalTime2 / (starts.size() - counter2NF));
			System.out.println("Times: " + time1.get(scores1.size() - 1) + "/" + time2.get(scores2.size() - 1));
			fail1.add(totalFails1 / (starts.size() - counter1NF));
			fail2.add(totalFails2 / (starts.size() - counter2NF));
			System.out.println("Fails: " + fail1.get(scores1.size() - 1) + "/" + fail2.get(scores2.size() - 1));
			round1.add(totalRound1 / (starts.size() - counter1NF));
			round2.add(totalRound2 / (starts.size() - counter2NF));
			System.out.println("Round: " + round1.get(scores1.size() - 1) + "/" + round2.get(scores2.size() - 1));
			System.out.println();
			System.out.println();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(scores1.toString());
		sb.append(scores2.toString());
		sb.append("\n===============================================\n");
		sb.append(time1.toString());
		sb.append(time2.toString());
		sb.append("\n===============================================\n");
		sb.append(fail1.toString());
		sb.append(fail2.toString());
		sb.append("\n===============================================\n");
		sb.append(nf1.toString());
		sb.append(nf2.toString());
		sb.append("\n===============================================\n");
		sb.append(round1.toString());
		sb.append(round2.toString());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("strictTest.txt"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void factorTest(PlannerService service) {
		List<Integer> starts = new ArrayList<>();

		starts.add(205121);
		starts.add(143883);
		starts.add(204053);
		starts.add(109711);
		starts.add(120076);
		starts.add(190408);
		starts.add(112162);
		starts.add(186794);
		starts.add(200821);
		starts.add(201353);

		List<Double> scores1 = new ArrayList<>();
		List<Double> scores2 = new ArrayList<>();
		List<Double> time1 = new ArrayList<>();
		List<Double> time2 = new ArrayList<>();
		List<Double> fail1 = new ArrayList<>();
		List<Double> fail2 = new ArrayList<>();
		List<Integer> nf1 = new ArrayList<>();
		List<Integer> nf2 = new ArrayList<>();
		List<Double> round1 = new ArrayList<>();
		List<Double> round2 = new ArrayList<>();

		double minCoef = 50;
		double maxCoef = 1550;

		int candidateCount = 20;
		int minLength = 13000;
		int maxLength = 16000;
		TourRequest request = new TourRequest(-1, -1, 450, 0.9, minLength, maxLength);

		for (double i = minCoef; i <= maxCoef; i += 100) {
			double totalScore1 = 0;
			double totalScore2 = 0;
			double totalTime1 = 0;
			double totalTime2 = 0;
			double totalFails1 = 0;
			double totalFails2 = 0;
			double totalRound1 = 0;
			double totalRound2 = 0;

			System.out.println("Testing " + i);
			int counter1NF = 0;
			int counter2NF = 0;
			for (Integer id : starts) {
				request.setStartNode(id);
				request.setFactor(i);

				System.out.println("Start " + id);
				TourResponse response1 = service.getClosedTours(request, candidateCount);
				TourResponse response2 = service.getClosedTours2(request, candidateCount);

				Optional<Tour> best1 = Arrays.stream(response1.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor())));
				Optional<Tour> best2 = Arrays.stream(response2.getTours())
						.min(Comparator.comparingDouble(e -> e.getFinalMeanCost(request.getFactor())));

				if (!best1.isPresent()) {
					counter1NF++;
					continue;
				}

				if (!best2.isPresent()) {
					counter2NF++;
					continue;
				}

				totalScore1 += best1.get().getFinalMeanCost(request.getFactor());
				totalScore2 += best2.get().getFinalMeanCost(request.getFactor());
				totalTime1 += ((double) response1.getResponseTime());
				totalTime2 += ((double) response2.getResponseTime());
				totalFails1 += ((double) service.notFound1Last / (double) service.tried1Last);
				totalFails2 += ((double) service.notFound2Last / (double) service.tried2Last);
				totalRound1 += best1.get().getRoundness();
				totalRound2 += best2.get().getRoundness();
			}
			nf1.add(counter1NF);
			nf2.add(counter2NF);

			scores1.add(totalScore1 / (starts.size() - counter1NF));
			scores2.add(totalScore2 / (starts.size() - counter2NF));
			System.out.println("Scores: " + scores1.get(scores1.size() - 1) + "/" + scores2.get(scores2.size() - 1));
			time1.add(totalTime1 / (starts.size() - counter1NF));
			time2.add(totalTime2 / (starts.size() - counter2NF));
			System.out.println("Times: " + time1.get(scores1.size() - 1) + "/" + time2.get(scores2.size() - 1));
			fail1.add(totalFails1 / (starts.size() - counter1NF));
			fail2.add(totalFails2 / (starts.size() - counter2NF));
			System.out.println("Fails: " + fail1.get(scores1.size() - 1) + "/" + fail2.get(scores2.size() - 1));
			round1.add(totalRound1 / (starts.size() - counter1NF));
			round2.add(totalRound2 / (starts.size() - counter2NF));
			System.out.println("Round: " + round1.get(scores1.size() - 1) + "/" + round2.get(scores2.size() - 1));
			System.out.println();
			System.out.println();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(scores1.toString());
		sb.append(scores2.toString());
		sb.append("\n===============================================\n");
		sb.append(time1.toString());
		sb.append(time2.toString());
		sb.append("\n===============================================\n");
		sb.append(fail1.toString());
		sb.append(fail2.toString());
		sb.append("\n===============================================\n");
		sb.append(nf1.toString());
		sb.append(nf2.toString());
		sb.append("\n===============================================\n");
		sb.append(round1.toString());
		sb.append(round2.toString());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("factorTest.txt"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
