package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.GoalChecker;
import cz.matocmir.tours.model.TourNode;

public class BackPathGoalChecker implements GoalChecker<BackPathLabel> {
	private boolean searchFinished = false;
	private TourNode startingPoint;

	public BackPathGoalChecker(TourNode startingPoint) {
		this.startingPoint = startingPoint;
	}

	@Override
	public boolean visitGoal(BackPathLabel current) {
		searchFinished = (current.getCandidate().correspNode.getNode().getId() == startingPoint.getId());
		return searchFinished;
	}

	@Override
	public boolean isSearchFinished() {
		return searchFinished;
	}
}
