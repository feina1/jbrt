package boostingRegressionTree.loss;


import java.util.ArrayList;

import boostingRegressionTree.Instance;

public class ExponentialLoss extends Loss {

	public double lossForOneInstance(Instance s, double predictValue) {
		return Math.exp(-s.trueValue * predictValue);
	}

	public double getIncrement(ArrayList<Instance> data, int begin, int end) {

		double positive = 0;
		double negative = 0;

		for (int k = begin; k < end; k++) {
			Instance s = data.get(k);
			if (s.trueValue == 1)
				positive += Math.exp(-s.positiveProbability);
			else if (s.trueValue == -1)
				negative += Math.exp(s.positiveProbability);
			else {
				System.err.println("wrong tag:" + s.trueValue);
				System.exit(-1);
			}
		}
		if (positive == 0 || negative == 0)
			System.out.print("");
		else
			System.out.print("");
		return 0.5 * Math.log(positive / negative);
	}

}
