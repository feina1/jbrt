package boostingRegressionTree.loss;


import java.util.ArrayList;

import boostingRegressionTree.Instance;


public abstract class Loss {

	public double get(ArrayList<Instance> data, double leftAddValue,
			double rightAddValue, int splitIndex) {

		double loss = 0;
		for (int i = 0; i < data.size(); i++) {
			Instance s = data.get(i);
			double currP = s.positiveProbability;
			if (i < splitIndex)
				currP += leftAddValue;
			else
				currP += rightAddValue;
			loss += lossForOneInstance(s, currP);
		}
		return loss;
	}

	public double loss(ArrayList<Instance> data, int begin, int end, double inc) {
		double loss = 0;
		for (int i = begin; i < end; i++) {
			Instance s = data.get(i);
			double currP = s.positiveProbability+inc;
			loss += lossForOneInstance(s, currP);
		}
		return loss;
	}
	
	public abstract double getIncrement(ArrayList<Instance> data, int begin,
			int end) ;
	
	abstract public double lossForOneInstance(Instance s, double probability1);

	public double loss(ArrayList<Instance> data) {
		return loss(data,0,data.size(),0);
	}

	
	public double penalty(double trueDelta, double falseDelta, int size) {
		return size*(trueDelta*trueDelta+falseDelta*falseDelta)*lambda;
	}
	public static double lambda=1;
	
}
