package cz.matocmir.tours.point2point;

import com.umotional.planningalgorithms.core.GoalChecker;

public class Point2PointGoalChecker implements GoalChecker<Point2PointLabel> {
	private int goalID;

	public Point2PointGoalChecker(int goalID) {
		this.goalID = goalID;
	}

	@Override
	public boolean visitGoal(Point2PointLabel current) {
		return (goalID == current.getCandidate().correspNode.getNode().getId());
	}

	@Override
	public boolean isSearchFinished() {
		return false;
	}
}
