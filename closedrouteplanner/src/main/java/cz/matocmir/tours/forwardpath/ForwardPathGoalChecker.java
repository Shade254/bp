package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.GoalChecker;
import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.model.TreeNode;
import cz.matocmir.tours.utils.TourUtils;

import java.util.ArrayList;
import java.util.List;

public class ForwardPathGoalChecker implements GoalChecker<ForwardPathLabel> {
	private int minLength;
	private TourNode startNode;
	private static final double epsilon = 0.001;

	public ForwardPathGoalChecker(int minLength, TourNode startNode) {
		this.minLength = minLength;
		this.startNode = startNode;
	}

	@Override
	public boolean visitGoal(ForwardPathLabel current) {
		TreeNode curN = current.getCandidate().correspNode;

		double minlenMinCurlen = minLength - current.getCandidate().length;
		double distance = TourUtils
				.computeGreatCircleDistance(curN.getNode().getLatitude(), curN.getNode().getLongitude(),
						startNode.getLatitude(), startNode.getLongitude());
		return (minlenMinCurlen < 0 || distance + epsilon >= minlenMinCurlen);

	}

	@Override
	public boolean isSearchFinished() {
		return false;
	}
}
