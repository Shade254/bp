package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.Label;
import cz.matocmir.tours.model.Candidate;

public class ForwardPathLabel extends Label {
	private Candidate node;
	public ForwardPathLabel(int objectId, int[] costVector, Label predecessorLabel, Candidate candidate) {
		super(objectId, costVector, predecessorLabel);
		this.node = candidate;
	}

	public Candidate getCandidate() {
		return node;
	}
}
