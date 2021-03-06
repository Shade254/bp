package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.GoalChecker;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.model.TreeNode;
import cz.matocmir.tours.utils.TourUtils;

public class ForwardPathGoalChecker implements GoalChecker<ForwardPathLabel> {
	private double minLength;
	private TourNode goalNode;
	private static final double epsilon = 0.001;
	private static final double COEF = 1.8;


	public ForwardPathGoalChecker(double minLength, TourNode goalNode) {
		this.minLength = minLength;
		this.goalNode = goalNode;
	}

	@Override
	public boolean visitGoal(ForwardPathLabel current) {
		TreeNode curN = current.getCandidate().correspNode;

		double minlenMinCurlen = minLength - current.getCandidate().length;
		double distance = TourUtils
				.computeEuclideanDistance(curN.getNode().getLatProjected(), curN.getNode().getLonProjected(),
						goalNode.getLatProjected(), goalNode.getLonProjected()) * COEF;
		return (minlenMinCurlen < 0 || distance + epsilon >= minlenMinCurlen);

	}

	@Override
	public boolean isSearchFinished() {
		return false;
	}
}
