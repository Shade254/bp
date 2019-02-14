package cz.matocmir.tours;

public class Candidate {
	public TreeNode correspNode;
	public int weight;
	public int length;

	public Candidate(TreeNode correspNode, int weight, int length) {
		this.correspNode = correspNode;
		this.weight = weight;
		this.length = length;
	}

	@Override
	public String toString() {
		return "Candidate{" + "correspNode=" + correspNode + ", weight=" + weight + ", length=" + length + '}';
	}
}