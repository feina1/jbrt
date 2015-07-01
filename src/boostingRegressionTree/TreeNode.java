package boostingRegressionTree;

import java.util.ArrayList;

public class TreeNode {

	public int featureIndex = -1;
	public String featureName = null;
	public double splitValue = Double.NaN;
	public double trueDelta = 0;
	public double falseDelta = 0;
	public TreeNode trueChild = null;
	public TreeNode falseChild = null;
	public double loss = Double.MAX_VALUE;

	public double avgTrueValue = Double.NaN;
	public double sampleNum = Double.NaN;

	ArrayList<Instance> data;
	int splitIndex = -1;

	public TreeNode() {
	}

	public TreeNode(ArrayList<Instance> data) {
		this.data = data;
		sampleNum = data.size();
		avgTrueValue = TreeNode.getPositivePercentage(data);
	}

	private static double getPositivePercentage(ArrayList<Instance> data) {
		double total = 0, pos = 0;
		for (Instance s : data) {
			total++;
			pos += s.trueValue;
		}
		return pos / total;
	}

	public boolean hasNoResult() {
		return loss == Double.MAX_VALUE;
	}

	public boolean leaf() {
		return trueChild == null && falseChild == null;
	}
}
