package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.LabelFactory;
import cz.matocmir.tours.model.*;
import cz.matocmir.tours.utils.TourUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ForwardPathLabelFactory implements LabelFactory<ForwardPathLabel> {
	private static final double epsilon = 0.001;
	private TourGraph graph;
	private int maxLength;
	private TourNode startNode;
	//this could be set
	//or not - paths are distinctive
	private List<Candidate> boundaryNodes;

	public ForwardPathLabelFactory(TourGraph graph, int maxLength, TourNode startNode) {
		this.graph = graph;
		this.maxLength = maxLength;
		this.startNode = startNode;
		this.boundaryNodes = new ArrayList<>();
	}

	@Override
	public List<ForwardPathLabel> successorsOf(ForwardPathLabel label) {
		Candidate curCan = label.getCandidate();
		List<ForwardPathLabel> res = new ArrayList<>();

		List<TourEdge> outEdges = graph.getOutEdges(curCan.correspNode.getNode().getId());
		for(TourEdge e : outEdges){
			Candidate cn = new Candidate(new TreeNode(e, curCan.correspNode, e.getTo()), curCan.weight + e.getCost(),
					curCan.length + e.getLengthInMeters());
			List<TourEdge> cnOutEdges = graph.getOutEdges(cn.correspNode.getNode().getId());
			HashSet<Integer> encounteredIds = new HashSet<>(); // Cycle detection

			while (cnOutEdges.size() == 1 && cn.length <= maxLength + epsilon) {
				e = cnOutEdges.get(0);
				cn = new Candidate(new TreeNode(e, cn.correspNode, e.getTo()), cn.weight + e.getCost(),
						cn.length + e.getLengthInMeters());
				cnOutEdges = graph.getOutEdges(cn.correspNode.getNode().getId());

				if (!encounteredIds.add(cn.correspNode.getNode().getId())) {
					cn.length = Integer.MAX_VALUE;
					break;
				}
			}

			double tourL = cn.length + TourUtils
					.computeGreatCircleDistance(cn.correspNode.getNode().getLatitude(),
							cn.correspNode.getNode().getLongitude(), startNode.getLatitude(),
							startNode.getLongitude());
			if (tourL <= maxLength + epsilon) {
					res.add(new ForwardPathLabel(cn.correspNode.getNode().getId(), new int[]{(int)cn.weight}, label, cn));
			} else if(cn.length>0){
				boundaryNodes.add(label.getCandidate());
			}
		}
		return res;
	}

	public List<Candidate> getBoundaryNodes() {
		return boundaryNodes;
	}
}
