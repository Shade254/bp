package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.LabelFactory;
import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.model.TreeNode;
import cz.matocmir.tours.utils.TourUtils;

import java.util.ArrayList;
import java.util.List;

public class ForwardPathLabelFactory implements LabelFactory<ForwardPathLabel> {
	private static final double epsilon = 0.001;
	private TourGraph graph;
	private int goalNode;
	private double maxLength;
	List<Candidate> boundaryNodes;

	public ForwardPathLabelFactory(TourGraph graph, int goalNode, double maxLength) {
		this.graph = graph;
		this.goalNode = goalNode;
		this.maxLength = maxLength;
		this.boundaryNodes = new ArrayList<>();
	}

	private double countWeight(TourEdge newEdge, Candidate candidate) {
		return newEdge.getCost();
	}

	@Override
	public List<ForwardPathLabel> successorsOf(ForwardPathLabel current) {
		Candidate curCan = current.getCandidate();
		List<TourEdge> outEdges = graph.getOutEdges(curCan.correspNode.getNode().getId());
		List<ForwardPathLabel> labels = new ArrayList<>();

		for (TourEdge e : outEdges) {
			Candidate nxtCan = new Candidate(new TreeNode(e, curCan.correspNode, e.getTo()),
					curCan.weight + countWeight(e, curCan), curCan.length + e.getLengthInMeters());

			double tourL2 = nxtCan.length + TourUtils
					.computeGreatCircleDistance(nxtCan.correspNode.getNode().getLatitude(),
							nxtCan.correspNode.getNode().getLongitude(), graph.getNode(goalNode).getLatitude(),
							graph.getNode(goalNode).getLongitude());

			if (tourL2 <= maxLength + epsilon) {
				ForwardPathLabel label = new ForwardPathLabel(e.getToId(), new int[] { (int) nxtCan.weight }, current,
						nxtCan);
				labels.add(label);
			} else if (nxtCan.length > 0) {
				boundaryNodes.add(current.getCandidate());
			}
		}

		return labels;
	}

	public List<Candidate> getBoundaryNodes() {
		return boundaryNodes;
	}
}
