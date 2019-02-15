package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.GoalChecker;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.model.TreeNode;

import java.util.List;

public class BackPathGoalChecker implements GoalChecker<BackPathLabel> {
	private boolean searchFinished = false;
	private TourNode startingPoint;
	private List<TreeNode> result;
	public BackPathGoalChecker(TourNode startingPoint) {
		this.startingPoint = startingPoint;
	}

	@Override
	public boolean visitGoal(BackPathLabel current) {
		if(current.getCandidate().correspNode.getNode().getId() == startingPoint.getId()){
			result = current.getCandidate().correspNode.pathFromRoot();
			return true;
		}
		return false;
	}

	@Override
	public boolean isSearchFinished() {
		return searchFinished;
	}

	public List<TreeNode> getResult() {
		return result;
	}
}
