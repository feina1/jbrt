package boostingRegressionTree;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mloss.roc.StartPoint;
import boostingRegressionTree.loss.Loss;
import boostingRegressionTree.loss.SquareLoss;

public class Model {

	/**
	 * Multi-thread is used in tree building step. For each feature, a thread is
	 * built to find best split point. MaxThreadNum is the size of thread pool.
	 */
	static int maxThreadNum = 50;
	static Loss loss = new SquareLoss();
	
	public static void main(String[] args) throws IOException, Exception {

		// model is stored as json type and can be visualized when opening
		// ui/gbrt.html
		// Plz use Firefox to open it for other browsers cannot load local file
		String outputModelPath = "ui/model.json";
		ArrayList<Instance> dataset = Instance
				.loadDatasetAndSchema("data/testDataset.csv");
		ArrayList<TreeNode> model = train(dataset, 3, 3);
		saveModel(model, outputModelPath);
		evaluate(outputModelPath, dataset, "data/testDatasetResult.csv");
	}

	private static void evaluate(String modelPath, ArrayList<Instance> dataset,
			String outputPath) throws Exception {
		double[] trueValues = Instance.getTrueValues(dataset);
		double[] probs = new double[dataset.size()];
		for (int j = 0; j < dataset.size(); j++) {
			probs[j] = predictOneInstance(dataset.get(j), loadModel(modelPath));
			dataset.get(j).trueValue = probs[j];
		}
		Instance.saveDataset(dataset, outputPath);
		printAUC(probs, trueValues);
	}

	public static int treeDepth;
	public static int treeNum;

	public static ArrayList<TreeNode> loadModel(String file) throws Exception {
		ArrayList<String> al = U.file2ArrayList(file);
		ArrayList<TreeNode> trees = new ArrayList<TreeNode>();
		for (int i = 1; i < al.size() - 1; i++)
			trees.add((TreeNode) U.jsonStr2object(cutStrTail(al.get(i), 1),
					TreeNode.class));
		trees.add((TreeNode) U.jsonStr2object(
				cutStrTail(al.get(al.size() - 1), 2), TreeNode.class));

		return trees;
	}

	static String cutStrTail(String s, int i) {
		return s.substring(0, s.length() - i);
	}

	public static double[] predict(ArrayList<TreeNode> model,
			ArrayList<Instance> data) {
		double[] probs = new double[data.size()];
		for (int j = 0; j < data.size(); j++) {
			probs[j] = predictOneInstance(data.get(j), model);
			data.get(j).positiveProbability = probs[j];
		}
		return probs;
	}

	public static void saveModel(ArrayList<TreeNode> trees, String string)
			throws IOException, Exception {
		FileWriter w = new FileWriter(string);
		w.write("{\"trees\":[" + "\n");
		for (int i = 0; i < trees.size() - 1; i++)
			w.write(U.object2jsonStr(trees.get(i)) + ",\n");
		w.write(U.object2jsonStr(trees.get(trees.size() - 1)) + "]}");
		w.close();
	}

	private static void printAUC(double[] probs, double[] trueTags) {

		int[] t = new int[trueTags.length];
		for (int i = 0; i < trueTags.length; i++) {
			double d = trueTags[i];
			if (Math.abs(d - Math.floor(d)) != 0)
				return;
			else
				t[i] = (int) d;

		}
		System.out.println("AUC: " + StartPoint.getRoc(probs, t));
	}

	public static double predictOneInstance(Instance s,
			ArrayList<TreeNode> trees) {

		double res = 0;

		for (TreeNode tree : trees) {
			double v = predictUsingOneTree(s, tree);
			res += v;
		}
		// res = restrictTo0_1(res);
		return res;
	}

	private static double predictUsingOneTree(Instance s, TreeNode tree) {
		if (tree == null || tree.leaf())// why tree ==null?
			return 0;
		if (s.v(tree.featureIndex) < tree.splitValue)
			return tree.trueDelta + predictUsingOneTree(s, tree.trueChild);
		else
			return tree.falseDelta + predictUsingOneTree(s, tree.falseChild);
	}

	private static TreeNode getOneRegressionTree(int depth,
			final ArrayList<Instance> data) throws IOException,
			InterruptedException {

		if (depth >= treeDepth)
			return new TreeNode(data);
		// System.out.println("----------------------------------");
		// System.out.println("Loss: " + optimalGoal.loss(data));
		// System.out.println("depth: " + depth);
		int threadNum = data.get(0).featureValue.length;
		TreeNode[] result = new TreeNode[threadNum];
		Thread[] threads = new Thread[threadNum];

		ExecutorService pool = Executors.newFixedThreadPool(maxThreadNum);
		CountDownLatch latch = new CountDownLatch(threadNum);
		for (int i = 0; i < threadNum; i++) {
			threads[i] = new OptimalSplitThread(
					(ArrayList<Instance>) data.clone(), i, result, latch);
			pool.execute(threads[i]);
		}
		latch.await();
		// System.out.println();
		pool.shutdown();
		TreeNode bestRes = result[0];
		for (int i = 1; i < result.length; i++)
			if (result[i].loss < bestRes.loss)
				bestRes = result[i];
		if (bestRes.hasNoResult())
			return bestRes;
		ArrayList<Instance> sortedData = bestRes.data;
		bestRes.data = null;// !!!!!!!!!!!!!!!!!!
		int bestFeatureIndex = bestRes.featureIndex;
		int splitIndex = bestRes.splitIndex;
		TreeNode leftNode = getOneRegressionTree(
				depth + 1,
				cloneSubListAndAddValue(sortedData, 0, splitIndex,
						bestFeatureIndex, bestRes.trueDelta));
		TreeNode rightNode = getOneRegressionTree(
				depth + 1,
				cloneSubListAndAddValue(sortedData, splitIndex,
						sortedData.size(), bestFeatureIndex, bestRes.falseDelta));
		bestRes.trueChild = leftNode;
		bestRes.falseChild = rightNode;
		return bestRes;
	}

	private static ArrayList<Instance> cloneSubListAndAddValue(
			ArrayList<Instance> sortedData, int begin, int end,
			int featureIndex, double addValue) {
		ArrayList<Instance> res = new ArrayList<Instance>();
		for (int i = begin; i < end; i++) {
			sortedData.get(i).positiveProbability += addValue;
			res.add(sortedData.get(i));
		}
		return res;
	}

	static class OptimalSplitThread extends Thread {
		ArrayList<Instance> data;
		int i;
		TreeNode[] result;
		CountDownLatch latch;

		public OptimalSplitThread(final ArrayList<Instance> data, int i,
				TreeNode[] result, CountDownLatch latch) {
			this.data = data;
			this.i = i;
			this.result = result;
			this.latch = latch;
		}

		public void run() {
			try {
				result[i] = getOptimalSplit(data, i);// optimalGoal.getOptimalSplit(data,
														// i);
				latch.countDown();
				// if (latch.getCount() % 50 == 0)
				// System.out.print(latch.getCount() + " ");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static TreeNode getOptimalSplit(ArrayList<Instance> data, int index)
			throws IOException {

		Collections.sort(data, new TreeComparator(index));

		// System.out.println();
		// for(Instance ins :data)
		// System.out.println(ins.v(index));
		// System.out.println();

		// splitAndCount
		ArrayList<Integer> indexs = getDifferentValueIndex(data, index);

		TreeNode res = new TreeNode(data);
		int beginIndex = 0;
		int divideAndCount_Step = Instance.sampleNum / 100;

		for (int i = 1; i < indexs.size(); i++) {
			int endIndex = indexs.get(i);
			int length = endIndex - beginIndex;
			if (length < divideAndCount_Step)
				continue;

			TreeNode currentRes = new TreeNode(data);
			currentRes.splitIndex = endIndex;
			currentRes.trueDelta = loss.getIncrement(data, 0, endIndex); // predictor:
			currentRes.falseDelta = loss.getIncrement(data, endIndex,
					data.size());
			currentRes.loss = loss.loss(data, 0, currentRes.splitIndex,
					currentRes.trueDelta)
					+ loss.loss(data, currentRes.splitIndex, data.size(),
							currentRes.falseDelta);
			currentRes.loss += loss.penalty(currentRes.trueDelta,
					currentRes.falseDelta, data.size());
			loss.get(data, currentRes.trueDelta, currentRes.falseDelta,
					currentRes.splitIndex);
			if (currentRes.loss < res.loss)
				res = currentRes;
			beginIndex = endIndex;
		}

		if (res.hasNoResult())
			return res;
		res.splitValue = data.get(res.splitIndex).v(index);
		res.data = data;
		res.featureIndex = index;
		res.featureName = Instance.title(index);
		return res;
	}

	static class TreeComparator implements Comparator<Instance> {

		int index;

		public TreeComparator(int index) {
			// without the below code, exceptions will happen in sorting
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

			this.index = index;
		}

		public int compare(Instance o1, Instance o2) {

			double first = o1.v(index);
			double second = o2.v(index);
			double diff = first - second;
			if (diff > 0)
				return 1;
			if (diff < 0)
				return -1;
			else
				return 0;
		}

	}

	static ArrayList<Integer> getDifferentValueIndex(ArrayList<Instance> data,
			int index) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		res.add(0);
		for (int i = 1; i < data.size(); i++) {
			double currentValue = data.get(res.get(res.size() - 1)).v(index);
			if (data.get(i).v(index) != currentValue)
				res.add(i);
		}
		return res;
	}

	public static ArrayList<TreeNode> train(ArrayList<Instance> data,
			int treeDepth, int treeNum) throws Exception {

		Model.treeDepth = treeDepth;
		Model.treeNum = treeNum;
		System.out.println("Dataset size: " + data.size());

		ArrayList<TreeNode> model = new ArrayList<TreeNode>();

		for (int i = 1; i <= treeNum; i++) {
			TreeNode tree = getOneRegressionTree(0, data);
			if (tree == null)
				break;
			model.add(tree);

			System.out.println("Loss on tree_" + i + ": " + loss.loss(data));
		}
		return model;
	}

}
