package boostingRegressionTree.loss;


import java.util.ArrayList;

import boostingRegressionTree.Instance;

public class SquareLoss extends Loss {

	@Override
	public double lossForOneInstance(Instance s, double probability1) {
		return Math.pow(s.trueValue - probability1, 2);
	}

	public double getIncrement(ArrayList<Instance> data, int begin, int end) {

		double res = 0;
		for (int k = begin; k < end; k++) {
			Instance s = data.get(k);
			res += s.trueValue - s.positiveProbability;
		}
		return res / (end - begin);
	}

//	@Override
	// goal: find s to minimize least square. This is equivalent to maximize:
	// M=(sum_0^s)^2/s + (sum - sum_0^s)^2/(n-s)
	// where sum_i^j is sum_{i<=k<j}z_k, z_k=y_k-f(x_k)
//	public TreeNode getOptimalSplit(ArrayList<Instance> data, int featureIndex) {
//
//		Collections.sort(data, new TreeComparator(featureIndex));
//		ArrayList<Integer> indexs = Model.getDifferentValueIndex(data,
//				featureIndex);
//
//		double sum = 0;
//		for (Instance i : data)
//			sum += i.trueTag - i.positiveProbability;
//
//		double M = -1;
//		int bestSplit = -1;
//		int beginIndex = 0;
//		double sum_0_s = 0;// sum_0^s
//		for (int i = 1; i < indexs.size(); i++) {
//			int s = indexs.get(i);
//			if (s - beginIndex < Model.divideAndCount_Step)
//				continue;
//			for (int in = beginIndex; in < s; in++)
//				sum_0_s += data.get(in).trueTag - data.get(in).positiveProbability;
//			double m = sum_0_s * sum_0_s / s + (sum - sum_0_s)
//					* (sum - sum_0_s) / (data.size() - s);
//			if (m > M) {
//				M = m;
//				bestSplit = s;
//			}
//			beginIndex = s;
//		}
//		TreeNode res = new TreeNode(data);
//		if (bestSplit == -1)
//			return res;
//		res.splitIndex = bestSplit;
//		res.featureIndex = featureIndex;
//		res.featureName = Instance.title(featureIndex);
//		res.splitValue = data.get(res.splitIndex).v(featureIndex);
//
//		res.trueDelta = getIncrement(data, 0, res.splitIndex);// can speed up
//		res.falseDelta = getIncrement(data, res.splitIndex, data.size());
//		res.loss = loss(data, 0, res.splitIndex, res.trueDelta)
//				+ loss(data, res.splitIndex, data.size(), res.falseDelta);
//		return res;
//	}
}
