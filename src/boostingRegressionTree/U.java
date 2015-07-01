package boostingRegressionTree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

//Utility
public class U {

	public static void ArrayList2file(ArrayList res, String file)
			throws Exception {
		BufferedWriter w = U.newWriter(file);
		for (Object s : res)
			w.write(s.toString() + "\n");
		w.close();
	}
	
	public static ArrayList<String> file2ArrayList(String f) throws IOException {
		ArrayList<String> v = new ArrayList<String>();
		BufferedReader r = U.newReader(f);
		while (true) {
			String l = r.readLine();
			if (null == l)
				break;
			v.add(TrimGood(l));
		}
		r.close();
		return v;
	}

	public static BufferedReader newUtf8Reader(String in)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(
				new FileInputStream(in), "UTF-8"));
	}

	public static BufferedWriter newUtf8writer(String string) throws Exception {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				string), "UTF-8"));
	}

	public static int getFileLineNum(String f) throws IOException {
		BufferedReader r = U.newReader(f);
		int res = 0;
		while (true) {
			String l = r.readLine();
			if (null == l)
				break;
			res++;
		}
		r.close();
		return res;
	}

	public static String TrimGood(String s) {
		if (null == s)
			return null;
		s = s.replace("\n", "");
		s = s.replace("\r", "");
		s = trimBeginAndEnd(s);
		return s;
	}

	public static boolean isEmpty(String s) {
		if (null == s || s.trim().equals(""))
			return true;
		return false;
	}

	public static <E> void collection2file(AbstractCollection<E> c, String fpath)
			throws Exception {
		BufferedWriter w = newWriter(fpath);
		for (E s : c)
			w.write(s.toString() + '\n');
		w.close();
	}

	public static void closeW(Writer w) throws IOException {
		w.flush();
		w.close();
	}

	public static String getHTMLSrc(String url, String charSet)
			throws IOException {

		URL u = new URL(url);
		InputStream in = u.openStream();
		BufferedReader is = new BufferedReader(new InputStreamReader(in,
				charSet));
		String c = "";
		StringBuffer sb = new StringBuffer();
		while ((c = is.readLine()) != null) {
			sb.append(c).append("\n");// 读入数据
		}
		in.close();
		return U.TrimGood(new String(sb.toString()));
	}

	public static String getHTMLSource(String url) throws IOException {
		return U.getHTMLSrc(url, "utf-8");
	}

	public static int getIndex(String[] sa, String s) {
		for (int i = 0; i < sa.length; i++) {
			if (sa[i].equals(s))
				return i;
		}
		return -1;
	}

	public static void setProxy() {
		System.getProperties().put("http.proxyHost", "162.105.146.215");
		System.getProperties().put("http.proxyPort", "3128");
		Authenticator.setDefault(new MyAuthenticator());
	}

	static class MyAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("jjyao", new char[] { 'p', 's',
					'd', 'j', 'j', 'y' });
		}
	}

	public static double minOr0(double[] da) {
		double res = 0;
		for (int i = 0; i < da.length; i++) {
			if (da[i] < res)
				res = da[i];
		}
		return res;
	}

	static List<String> getMatchedStringByRegex(String src, String regEx) {
		List<String> matchList = new ArrayList<String>();

		Pattern regex = Pattern.compile(regEx);
		Matcher regexMatcher = regex.matcher(src);
		while (regexMatcher.find()) {
			matchList.add(regexMatcher.group());
		}
		return matchList;
	}

	public static boolean isIn(int[] ia, int content) {
		for (int i = 0; i < ia.length; i++) {
			if (content == ia[i])
				return true;
		}
		return false;
	}

	public static boolean isIn(String[] ia, String content) {
		for (int i = 0; i < ia.length; i++) {
			if (content.equals(ia[i]))
				return true;
		}
		return false;
	}

	public static BufferedReader getReader(String file)
			throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}

	static boolean isDigit(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	static boolean isInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static double truncation(double d) {
		long _d = (long) (d * 100);
		double res = ((double) _d) / 100;
		if (d > 100 && Math.abs(d - res) > 0.1 * Math.abs(d)) {
			// System.err.println("U.truncation error!");
			return Double.NaN;
		}
		return res;
	}

	public static BufferedWriter newWriter(String file) throws Exception {
		return U.newUtf8writer(file);
	}

	public static <T> void add(HashMap<T, Integer> m, T s) {
		int i = 0;
		if (null != m.get(s))
			i = m.get(s);
		m.put(s, i + 1);
	}

	int find(int[] a, int t) {
		if (a == null || a.length == 0)
			return -1;
		int begin = 0, end = a.length - 1;
		while (begin <= end) {
			int middle = (begin + end) / 2;
			if (a[middle] == t)
				return middle;
			if (a[middle] < t)
				begin = middle + 1;
			else
				end = middle - 1;
		}
		return -1;
	}

	public static void add(Hashtable<String, Integer> m, String s) {
		int i = 0;
		if (null != m.get(s))
			i = m.get(s);
		m.put(s, i + 1);
	}

	public static double getExpectation(ArrayList<Double> v) {
		double result = 0;
		for (int i = 0; i < v.size(); i++) {
			result += v.get(i);
		}
		return result / v.size();
	}

	public static double getSD(ArrayList<Double> v) {
		if (v.size() == 1)
			return Double.NaN;
		double e = getExpectation(v);
		double result = 0;
		for (int i = 0; i < v.size(); i++) {
			result += (v.get(i) - e) * (v.get(i) - e);
		}
		return Math.sqrt(result / (v.size() - 1));
	}

	public static double relatedCoefficient(ArrayList<Double> v,
			ArrayList<Double> v2) {

		double res = 0;
		double e = getExpectation(v);
		double e2 = getExpectation(v2);
		for (int i = 0; i < v.size(); i++) {
			res += (v.get(i) - e) * (v2.get(i) - e2);
		}
		return res / getSD(v) / getSD(v2) / v.size();
	}

	public static int tryGet(HashMap<String, Integer> hm, String s) {
		if (null != hm.get(s))
			return hm.get(s);
		return 0;
	}

	public static int hashtableTryGet(Hashtable<String, Integer> hm, String s) {
		if (null != hm.get(s))
			return hm.get(s);
		return 0;
	}

	public static Object getProperty(Object owner, String fieldName)
			throws Exception {
		Class ownerClass = owner.getClass();

		Field field = ownerClass.getField(fieldName);

		Object property = field.get(owner);

		return property;
	}

	public static BufferedReader newReader(String file) {
		try {
			return newUtf8Reader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("fail to read file " + file);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String Array2String(Object[] a) {
		String res = "[";
		for (int i = 0; i < a.length; i++) {
			res += " " + a[i];
		}
		return res + " ]";
	}

	public static String Array2String(int[] a) {
		String res = "[";
		for (int i = 0; i < a.length; i++) {
			res += " " + a[i];
		}
		return res + " ]";
	}

	public static String Array2String(double[] a) {
		String res = "[";
		for (int i = 0; i < a.length; i++) {
			res += " " + a[i];
		}
		return res + " ]";
	}

	static int[] intSas2A(String s) {
		String[] sa = s.split(" ");
		int[] res = new int[sa.length - 2];
		for (int i = 1; i < sa.length - 1; i++) {
			res[i - 1] = Integer.parseInt(sa[i]);
		}
		return res;
	}

	static double[] doubleSas2A(String s) {
		String[] sa = s.split(" ");
		double[] res = new double[sa.length - 2];
		for (int i = 1; i < sa.length - 1; i++) {
			res[i - 1] = Double.parseDouble(sa[i]);
		}
		return res;
	}

	public static String trimBeginAndEnd(String s) {
		if (null == s || s.equals(""))
			return s;
		int i = 0;
		for (; i < s.length() && s.charAt(i) == ' '; i++)
			;
		if (i == s.length())
			return "";
		return s.substring(i).trim();
	}

	public static HashSet<String> loadHashSet(String f) throws IOException {
		BufferedReader r = U.newReader(f);
		HashSet<String> res = new HashSet<String>();
		while (true) {
			String l = r.readLine();
			if (null == l)
				break;
			res.add(l);
		}
		r.close();
		return res;
	}

	public static int[] arrayAdd(int[] a, int i) {
		int[] res = new int[a.length + 1];
		for (int j = 0; j < a.length; j++) {
			res[j] = a[j];
		}
		res[res.length - 1] = i;
		return res;
	}

	public static double[] arrayAdd(double[] a, double i) {
		double[] res = new double[a.length + 1];
		for (int j = 0; j < a.length; j++) {
			res[j] = a[j];
		}
		res[res.length - 1] = i;
		return res;
	}



	public static class KeyValuePair {
		public Object key;
		public double value;

		public KeyValuePair(Object k, double v) {
			key = k;
			value = v;
		}
	}

	public static <T> ArrayList<T> sort(ArrayList<T> list,
			ArrayList<Double> value) {

		ArrayList<KeyValuePair> l = new ArrayList<U.KeyValuePair>();
		for (int i = 0; i < list.size(); i++)
			l.add(new KeyValuePair(list.get(i), value.get(i)));
		sort(l);

		ArrayList<T> res = new ArrayList<T>();
		for (KeyValuePair p : l)
			res.add((T) p.key);
		return res;
	}

	public static void sort(ArrayList<KeyValuePair> list) {
		Comparator comp = new Comparator() {

			public final int compare(Object o1, Object o2) {
				double first = ((KeyValuePair) o1).value;
				double second = ((KeyValuePair) o2).value;
				double diff = first - second;
				if (diff > 0)
					return 1;
				if (diff < 0)
					return -1;
				else
					return 0;
			}

		};
		Collections.sort(list, comp);
	}

	//make sure that the constructor exists
	static Object jsonStr2object(String userDataJSON, Class t)
			throws Exception {
		return new ObjectMapper().readValue(userDataJSON, t);
	}

	public static String object2jsonStr(Object o) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();
		mapper.writeValue(strWriter, o);
		return strWriter.toString();
	}


}