package boostingRegressionTree.loss;

import java.util.ArrayList;

import boostingRegressionTree.Instance;

public class AbsoluteLoss extends Loss {

	public double lossForOneInstance(Instance s, double probability1) {
		return Math.abs(s.trueValue - probability1);
	}

	public double getIncrement(ArrayList<Instance> data, int begin, int end) {

		double res = 0;
		for (int k = begin; k < end; k++) {
			Instance s = data.get(k);
			res += s.trueValue - s.positiveProbability;
		}
		return res / (end - begin);
	}
}
