package cz.matocmir.tours.point2point;

import com.umotional.planningalgorithms.core.Path;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TreeNode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Point2PointPath implements Path<Point2PointLabel> {

	private Point2PointLabel last;

	public Point2PointPath(Point2PointLabel last) {
		this.last = last;
	}

	@Override
	public int[] getCostVector() {
		return last.getCostVector();
	}

	@Override
	public Point2PointLabel getLastLabelObjId() {
		return last;
	}

	@Override
	public List<Integer> getPath() {
		return last.getCandidate().correspNode.pathFromRoot().stream().map(n -> n.getNode().getId()).collect(
				Collectors.toList());
	}

	public List<TourEdge> getExactPath(){
		return last.getCandidate().correspNode.pathFromRoot().stream().map(TreeNode::getEdgeFromParent).filter(Objects::nonNull).collect(
				Collectors.toList());
	}

}
