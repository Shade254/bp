package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.LabelFactory;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;

import java.util.ArrayList;
import java.util.List;

public class BackPathLabelFactory implements LabelFactory<BackPathLabel> {
private static final double epsilon = 0.001;
private TourGraph graph;
private double forwardLength;
private double maxLength;
private TourNode startNode;

	public BackPathLabelFactory(TourGraph graph, double forwardLength, double maxLength, TourNode startNode) {
		this.graph = graph;
		this.maxLength = maxLength;
		this.forwardLength = forwardLength;
		this.startNode = startNode;
	}

	@Override
	public List<BackPathLabel> successorsOf(BackPathLabel current) {
		Candidate curCan = current.getCandidate();
		List<TourEdge> outEdges = graph.getOutEdges(curCan.correspNode.getNode().getId());
		List<BackPathLabel> labels = new ArrayList<>();

		for(TourEdge e : outEdges){
			double tourL = current.getCandidate().length + forwardLength + TourUtils.computeGreatCircleDistance(
					startNode, curCan.correspNode.getNode());

			if (tourL<=maxLength+epsilon) {
				Candidate nxtCan = new Candidate(new TreeNode(e, curCan.correspNode, e.getTo()),curCan.weight + countWeight(e), curCan.length + e.getLengthInMeters());
				BackPathLabel label = new BackPathLabel(e.getToId(), new int[]{(int)nxtCan.weight}, current, nxtCan);
				labels.add(label);
			}
		}

		return labels;
	}

	private double countWeight(TourEdge e) {
		return e.getCost();
	}
}
