package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.GoalChecker;
import cz.matocmir.tours.model.TourNode;

public class BackPathGoalChecker implements GoalChecker<BackPathLabel> {
	private boolean searchFinished = false;
	private TourNode goalNode;

	public BackPathGoalChecker(TourNode goalNode) {
		this.goalNode = goalNode;
	}

	@Override
	public boolean visitGoal(BackPathLabel current) {
		searchFinished = (current.getCandidate().correspNode.getNode().getId() == goalNode.getId());
		return searchFinished;
	}

	@Override
	public boolean isSearchFinished() {
		return searchFinished;
	}
}
