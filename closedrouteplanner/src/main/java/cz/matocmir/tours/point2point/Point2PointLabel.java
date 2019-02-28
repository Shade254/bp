package cz.matocmir.tours.point2point;

import com.umotional.planningalgorithms.core.Label;
import cz.matocmir.tours.model.Candidate;

public class Point2PointLabel extends Label {
	private Candidate candidate;
	public Point2PointLabel(int objectId, int[] costVector, Label predecessorLabel, Candidate candidate) {
		super(objectId, costVector, predecessorLabel);
		this.candidate = candidate;
	}

	public Candidate getCandidate() {
		return candidate;
	}
}
