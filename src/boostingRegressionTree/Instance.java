package boostingRegressionTree;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Instance {
	public double[] featureValue;
	public double positiveProbability = 0;
	public double profit = 0;// to be deleted
	public double trueValue;

	public static HashMap<String, Integer> title2index;
	public static String[] featureNameDict;
	public static double missingValue = Double.MAX_VALUE;

	static public Instance parseInstance(String l) {
		Instance ins = new Instance();
		String[] sa = l.split(",");
		ins.trueValue = Double.parseDouble(sa[0]);
		ins.featureValue = new double[title2index.size()];
		for (int i = 1; i - 1 < title2index.size(); i++) {
			if (sa[i].trim().length() == 0)
				ins.featureValue[i - 1] = missingValue;
			else {
				ins.featureValue[i - 1] = Double.parseDouble(sa[i]);
				if(new Double(ins.featureValue[i - 1]).isNaN() )
					ins.featureValue[i - 1] =missingValue;
			}
		}
		return ins;
	}

	public String toString() {
		return "{" + trueValue + "," + positiveProbability + "}";
	}

	static public String title(int i) {
		return featureNameDict[i];
	}

	static public int dimension() {
		return featureNameDict.length;
	}

	// public Instance(String l,double missingValue,Sample sample) {
	// String[] sa = l.split(",");
	// trueTag = Integer.parseInt(sa[1]);
	// profit = Double.parseDouble(sa[2]);
	// featureValue = new double[sample.title2index.size()];
	// for (int i = 3; i < sample.title2index.size()+3; i++) {
	// if (sa[i].length() == 0)
	// featureValue[i - 3] = missingValue;
	// else{
	// featureValue[i - 3] = Double.parseDouble(sa[i]);
	// }
	// }
	// }

	// public int titleIndex(String label) {
	// return sample.title2index.get(label);
	// }

	// public double loss() {
	// return (trueTag - probability1) * (trueTag - probability1);
	// }

	public double v(String title) {
		return featureValue[title2index.get(title)];
	}

	public double v(int i) {
		return featureValue[i];
	}

	public static void saveDataset(ArrayList<Instance> s, String path)
			throws IOException {
		FileWriter w = new FileWriter(path);
		w.write(Instance.label);
		for (String title : Instance.featureNameDict)
			w.write("," + title);
		w.write("\n");
		for (Instance ins : s) {
			w.write(ins.trueValue + "");
			for (double d : ins.featureValue)
				w.write("," + d);
			w.write("\n");
		}
		w.close();

	}

	// suppose binary classification
	public static ArrayList<Instance> loadDatasetAndSchema(String file)
			throws Exception {
		refreshInstanceStatic(file);
		System.out.print("loading " + file + " ...");
		BufferedReader r = U.newReader(file);
		r.readLine();// skip title line

		ArrayList<Instance> al = new ArrayList<Instance>();
		while (true) {
			String l = r.readLine();
			if (l == null)
				break;
			Instance ins = parseInstance(l);
			al.add(ins);
		}
		r.close();
		System.out.println("ok");

		sampleNum=al.size();
		return al;
	}

	private static void refreshInstanceStatic(String file) throws IOException {
		BufferedReader r = U.newReader(file);
		String[] ts = r.readLine().split(",");
		featureNameDict = new String[ts.length - 1];
		title2index = new HashMap<String, Integer>();
		for (int i = 1; i < ts.length; i++) {
			featureNameDict[i - 1] = ts[i];
			title2index.put(ts[i], i - 1);
		}
	}

	public Instance copy() {
		Instance res = new Instance();
		res.featureValue = featureValue;
		res.positiveProbability = positiveProbability;
		res.profit = profit;
		res.trueValue = trueValue;
		return res;
	}

	private static String label = "value";
	public static int sampleNum;

	public static double[] getTrueValues(ArrayList<Instance> dataset) {
		
		double[]  res=new double[dataset.size()];
		for(int i=0;i<res.length;i++)
			res[i]=dataset.get(i).trueValue;
		return res;
	}
}
