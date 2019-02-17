package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.Path;

import java.util.List;
import java.util.stream.Collectors;

public class ForwardPath implements Path<ForwardPathLabel> {
	private ForwardPathLabel last;

	public ForwardPath(ForwardPathLabel label) {
		this.last = label;
	}

	@Override
	public int[] getCostVector() {
		return last.getCostVector();
	}

	@Override
	public ForwardPathLabel getLastLabelObjId() {
		return last;
	}

	@Override
	public List<Integer> getPath() {
		return last.getCandidate().correspNode.pathFromRoot().stream().map(n -> n.getNode().getId()).collect(Collectors.toList());
	}
}
