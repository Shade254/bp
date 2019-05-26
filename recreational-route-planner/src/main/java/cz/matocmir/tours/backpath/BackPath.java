package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.Path;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TreeNode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BackPath implements Path<BackPathLabel> {

	private BackPathLabel last;
	private double totalLength;

	public BackPath(BackPathLabel last) {
		this.last = last;
		totalLength = last.getCandidate().correspNode.pathFromRoot().stream().map(TreeNode::getEdgeFromParent)
				.filter(Objects::nonNull).mapToDouble(TourEdge::getLengthInMeters).sum();
	}

	@Override
	public int[] getCostVector() {
		return last.getCostVector();
	}

	@Override
	public BackPathLabel getLastLabelObjId() {
		return last;
	}

	@Override
	public List<Integer> getPath() {
		return last.getCandidate().correspNode.pathFromRoot().stream().map(n -> n.getNode().getId())
				.collect(Collectors.toList());
	}

	public List<TourEdge> getExactPath() {
		return last.getCandidate().correspNode.pathFromRoot().stream().map(TreeNode::getEdgeFromParent)
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	public double getTotalLength() {
		return totalLength;
	}
}
