package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.Path;
import cz.matocmir.tours.model.TreeNode;

import java.util.List;
import java.util.stream.Collectors;

public class BackPath implements Path<Integer> {

	List<Integer> path;
	List<TreeNode> fullPath;
	int[] costVector;
	Integer lastId;
	double totalLength;

	public BackPath(BackPathLabel label) {
		fullPath = label.getCandidate().correspNode.pathFromRoot();
		path = fullPath.stream().map(t -> t.getNode().getId()).collect(Collectors.toList());
		costVector = label.getCostVector();
		lastId = label.getObjectId();
		totalLength = label.getCandidate().length;
	}

	@Override
	public int[] getCostVector() {
		return costVector;
	}

	@Override
	public Integer getLastLabelObjId() {
		return lastId;
	}

	@Override
	public List<Integer> getPath() {
		return path;
	}

	public List<TreeNode> getFullPath(){
		return fullPath;
	}

	public double getTotalLength() {
		return totalLength;
	}
}
