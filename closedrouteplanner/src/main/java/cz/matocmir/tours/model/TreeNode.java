package cz.matocmir.tours.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private final TourEdge edgeFromParent;
	private final List<TourNode> children;
	private final TourNode node;
	private final TreeNode parent;

	public TreeNode(TourEdge edgeFromParent, TreeNode parent, TourNode thisNode) {
		this.edgeFromParent = edgeFromParent;
		this.parent = parent;
		this.node = thisNode;

		children = new ArrayList<>();
	}

	public void addChildren(TourNode node) {
		children.add(node);
	}

	public void addChildren(List<TourNode> nodes) {
		children.addAll(nodes);
	}

	public TourEdge getEdgeFromParent() {
		return edgeFromParent;
	}

	public List<TourNode> getChildren() {
		return children;
	}

	public TourNode getNode() {
		return node;
	}

	public TreeNode getParent() {
		return parent;
	}

	public ArrayList<TreeNode> pathFromRoot() {
		ArrayList<TreeNode> pathHere;

		if (parent == null) {
			pathHere = new ArrayList<>();
		} else {
			pathHere = parent.pathFromRoot();
		}

		pathHere.add(this);
		return pathHere;
	}

	@Override
	public String toString() {
		return "TreeNode{" + "node=" + node.getId() + ", parent=" + (this.parent == null ? "NULL" : parent.node.getId()) + '}';
	}
}
