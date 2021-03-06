package cz.matocmir.tours.model;

import cz.matocmir.tours.model.TreeNode;

/***
 * Class representing candidate found in forward routing stage of the algorithm
 */
public class Candidate {
	public TreeNode correspNode;
	public double weight;
	public double length;

	public Candidate(TreeNode correspNode, double weight, double length) {
		this.correspNode = correspNode;
		this.weight = weight;
		this.length = length;
	}

	@Override
	public String toString() {
		return "Candidate{" + "correspNode=" + correspNode + ", weight=" + weight + ", length=" + length + '}';
	}
}