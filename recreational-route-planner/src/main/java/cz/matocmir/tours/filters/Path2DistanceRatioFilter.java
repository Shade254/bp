package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.utils.TourUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Path2DistanceRatioFilter implements CandidateFilter {
	private double upperRatio;
	private double lowerRatio;
	private TourNode goal;
	public static int counter = 0;

	public Path2DistanceRatioFilter(double upperRatio, double lowerRatio, TourNode goal) {
		this.upperRatio = upperRatio;
		this.lowerRatio = lowerRatio;
		this.goal = goal;
	}

	@Override
	public List<Candidate> filter(List<Candidate> cands) {
		counter++;
//		File dir = new File("./result" + Path2DistanceRatioFilter.counter);
//		dir.mkdirs();

		//IOUtils.visualizeNodes(cands.stream().map(c -> c.correspNode.getNode()).collect(Collectors.toList()),
		//		"./result" + counter + "/before" + counter + ".json");
		List<Candidate> filtered = cands.stream().filter(this::inRatioRange).collect(Collectors.toList());
		//IOUtils.visualizeNodes(filtered.stream().map(c -> c.correspNode.getNode()).collect(Collectors.toList()),
		//		"./result" + counter + "/after" + counter + ".json");
		System.out.println((cands.size() - filtered.size()) + " removed after first step (Ratio)");
		return filtered;
	}

	private boolean inRatioRange(Candidate c) {
		double ratio = c.length / TourUtils.computeEuclideanDistance(c.correspNode.getNode(), goal);
		return (ratio > lowerRatio && ratio < upperRatio);
	}
}
