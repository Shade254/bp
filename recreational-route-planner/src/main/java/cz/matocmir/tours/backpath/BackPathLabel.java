package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.Label;
import cz.matocmir.tours.model.Candidate;


public class BackPathLabel extends Label {
	private Candidate node;

	public BackPathLabel(int objectId, int[] costVector, Label predecessorLabel, Candidate node) {
		super(objectId, costVector, predecessorLabel);
		this.node = node;
	}

	public Candidate getCandidate() {
		return node;
	}
}
