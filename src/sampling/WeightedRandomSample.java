package sampling;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import main.Main;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.randvar.UniformIntGen;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import utils.ArrayUtils;
import utils.Binary;
import utils.MethodUtils.DoubleGetter;
import utils.Sequences;

/** Randomly choose elements from collections with possibly different probabilities associated with each item in the collection. */
public class WeightedRandomSample {

	public static final boolean LO_TO_HI = false, HI_TO_LO = true;

	/**
	 *  Get a 1D array of weights calculated from the values
	 *  retrieved by the getter.
	 * @param s
	 * @param getter
	 * @param invert
	 * @param offset
	 * @return
	 */
	public static<T> DblArrayMinMax weights(
			List<T> s, DoubleGetter<T> getter,
			boolean invert, double offset)
	{
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double val;
		double[] out = new double[s.size()];
		for (int i = 0; i < s.size(); i++)
		{
			val = Math.max(0, getter.get(s.get(i)) - offset);
			min = Math.min(min, val);
			max = Math.max(max, val);
		}
		if (invert) out = invertWeights(out, min, max);
		return new DblArrayMinMax(out, min, max);
	}

	
	private static<T> DblArrayMinMax weights(
			List<WeightedItem<T>> s, 
			boolean invert, double offset)
	{
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double val;
		double[] out = new double[s.size()];
		for (int i = 0; i < s.size(); i++)
		{
			val = Math.max(0, s.get(i).weight - offset);
//			val = Math.max(0, weightedItemGetter.get(s.get(i)) - offset);
			out[i] = val; 
			min = Math.min(min, val);
			max = Math.max(max, val);
		}
		if (invert) out = invertWeights(out, min, max);
		return new DblArrayMinMax(out, min, max);
	}

	
	
	public static <T> T weightedRandomSingleSample(
			List<WeightedItem<T>> ls, boolean inverse, double offset,
			boolean normalize, double minWeight, double maxWeight,
			RandomStream rs, boolean nullIfNoWeight)
	{
		DblArrayMinMax weights = 
				weights(ls, inverse, offset);
		if (weights.max == 0 && nullIfNoWeight) return null;

		
		if (normalize) 
		{
			double[] norm = Sequences.normalize2(weights.d, weights.min, weights.max, minWeight, maxWeight);
			weights.d = norm;
			weights.min = minWeight;
			weights.max = maxWeight;
		}
		double[] cumulativeWeights = ArrayUtils.cumulativeSum(weights.d, true);
		double key = cumulativeWeights[cumulativeWeights.length - 1] * rs.nextDouble();
		
		int index = Binary.insertionIndex(cumulativeWeights, key);
		return ls.get(index).item;

	}
	
	public static <T> T weightedRandomSingleSample(
			List<T> ls, DoubleGetter<T> getter,
			boolean inverse, double offset, RandomStream rs,
			boolean nullIfNoWeight)
	{
		DblArrayMinMax weights = weights(ls, getter, inverse, offset);
		if (weights.max == 0 && nullIfNoWeight) return null;
		double[] cumulativeWeights = ArrayUtils.cumulativeSum(weights.d, true);
		double key = cumulativeWeights[cumulativeWeights.length - 1] * rs.nextDouble();
		
		int index = Binary.insertionIndex(cumulativeWeights, key);
		return ls.get(index);
	}
	
	

	/** 
	 *  Perform a weighted random sample of k items from a list of
	 *  length n using the Chao method.
	 * 
	 * adapted from:
	 *  <a href="https://en.wikipedia.org/wiki/Reservoir_sampling#Algorithm_A-Chao">https://en.wikipedia.org/wiki/Reservoir_sampling#Algorithm_A-Chao</a>}
	 * 
	 * @param rs
	 * @param s
	 * @param k
	 * @param comp
	 * @param getter
	 * @param invert
	 * @param offset
	 * @param propToSample if this is less than 1, the input array is sorted according
	 *                     to 'comp' and then only 
	 * @return
	 */
	public static <T> List<T> chaoSample(
			RandomStream rs,
			List<T> s, int k, 
			Comparator<T> comp,
			DoubleGetter<T> getter, 
			double offset, double propToSample
			)
	{
		List<T> out;
		boolean invert;
		int n =  s.size();

		/* If the input list is smaller than the requested sample, return the

		 * original list, as if all had been 'randomly' chosen.  */
		if (n <= k) 
		{
			out = new ArrayList<>(s.size());
			for (T t : s) out.add(t);
			out.sort(comp);
			return out;
		}
		/* need a defensive copy so that the input list is not 
		 * unintentionally sorted; */
		List<T> s2;

		s2 = new ArrayList<T>(n);
		for (T t : s) s2.add(t);

		s2.sort(comp);

		if (propToSample < 1) n = Math.max(k, (int) ((double) n * propToSample));

		if (getter.get(s2.get(0)) > getter.get(s2.get(n - 1)))
			invert = false;
		else invert = true;

		out = new ArrayList<>(k);
		int[] indices = new int[k];

		DblArrayMinMax weights = weights(s2, getter, invert, offset); 

		double weightSum = 0;
		double weightI = 0;
		double p, j;

		for (int i = 0; i < k; i++)
		{
			indices[i] = i;
			weightSum += weights.d[i];
		}

		for (int i = k; i < n; i++)
		{
			weightI = weights.d[i];
			weightSum += weightI;

			p = weightI / weightSum;
			j = rs.nextDouble();

			if (j <= p) indices[rs.nextInt(0, k - 1)] = i;
		}

		for (int i : indices) out.add(s2.get(i));
		out.sort(comp);
		return out;
	}

	/**
	 * adapted from 
	 *  <a href="https://en.wikipedia.org/wiki/Reservoir_sampling#Algorithm_A-Res">https://en.wikipedia.org/wiki/Reservoir_sampling#Algorithm_A-Res</a>}
	 * @param rs
	 * @param s
	 * @param k
	 * @param comp
	 * @param getter
	 * @param invert
	 * @param offset
	 * @param propToSample
	 * @return
	 */
	public static <T> List<T> efraimidisWeightedSample(
			RandomStream rs, List<T> s, int k, 
			Comparator<T> comp,
			DoubleGetter<T> getter, 
			double offset, double propToSample
			)
	{
		List<T> out;
		boolean invert;
		int n =  s.size();

		/* If the input list is smaller than the requested sample, return the
		 * original list, as if all had been 'randomly' chosen.  */
		if (n <= k) 
		{
			out = new ArrayList<>(s.size());
			for (T t : s) out.add(t);
			out.sort(comp);
			return out;
		}

		/* need a defensive copy so that the input list is not 
		 * unintentionally sorted; */
		List<T> s2;
		List<WeightedItem<T>> s3;

		s2 = new ArrayList<T>(n);
		for (T t : s) s2.add(t);
		s2.sort(comp);

		if (propToSample < 1) n = Math.max(k, (int) ((double) n * propToSample));

		if (getter.get(s2.get(0)) > getter.get(s2.get(n - 1)))
			invert = false;
		else invert = true;

		out = new ArrayList<>(k);

		DblArrayMinMax weights = weights(s2, getter, invert, offset); 

		s3 = new ArrayList<WeightedItem<T>>(n);
		for (int i = 0; i < n; i++) s3.add(new WeightedItem<T>(s2.get(i), weights.d[i]));
		s2 = null;

		PriorityQueue<WeightedItem<T>> q = new PriorityQueue<WeightedItem<T>>(k);

		double test, testAdj;

		for (int i = 0; i < n; i++)
		{
			WeightedItem<T> w = s3.get(i);

			test = rs.nextDouble();
			testAdj = Math.pow(test, 1.0 / w.weight);

			w.weight = testAdj;

			if (q.size() < k) q.add(w);
			else if (q.peek().weight < testAdj)
			{
				//				System.out.println(String.format("weight : %.4f", q.peek().weight));
				//				System.out.println(String.format("test   : %.4f", test));
				//				System.out.println(String.format("testAdj: %.4f", testAdj));
				//				System.out.println();
				q.poll();
				q.add(w);
			}
		}
		for (WeightedItem<T> w : q) out.add(w.item);
		out.sort(comp);
		return out;
	}

	/**
	 * A struct to hold an item and an associated weight.
	 * Useful for weighted selection algorithms.
	 * 
	 * @author michaelfrancenelson
	 *
	 * @param <T>
	 */
	public static class WeightedItem<T> implements Comparable<WeightedItem<T>>
	{
		T item;
		double weight;

		public WeightedItem(T t, double w) { item = t; weight = w; }

		@Override
		public int compareTo(WeightedItem<T> t) 
		{
			if (t.weight < weight) return -1;
			if (weight > t.weight) return 1;
			return 0;
		}
	}


	/**  Randomly choose an object from a list, weighted by the value of 
	 *   a <code>double</code> or <code>int</code> field.
	 * @param rs
	 * @param objects
	 * @param f
	 * @param invertWeights
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> T sample(RandomStream rs, List<T> objects, Field f, boolean invertWeights) 
			throws IllegalArgumentException, IllegalAccessException
	{
		if (! (f.getType().getSimpleName().equals("double") || f.getType().getSimpleName().equals("double")))
			throw new IllegalArgumentException("Can only sample on an 'int' or 'double' field.");

		double[] weights = new double[objects.size()];
		double minWeight = Double.MAX_VALUE;
		double maxWeight = - minWeight;
		double wt = 0.0;

		f.setAccessible(true);
		if (f.getType().getSimpleName().equals("double"))
		{
			int i = 0;
			for (T t : objects)
			{
				wt = f.getDouble(t); 
				weights[i] = wt;

				minWeight = Math.min(minWeight, wt);
				maxWeight = Math.max(maxWeight, wt);

				i++;
			}
		}

		else if (f.getType().getSimpleName().equals("int"))
		{
			int i = 0;
			for (T t : objects) { weights[i] = (double) f.getInt(t); i++; }
		}

		int index;

		if (invertWeights) 
		{
			double[] inverseWeights = invertWeights(weights, minWeight, maxWeight);
			index = inverseSample(rs, inverseWeights);
		}
		else index = sample(rs, weights);
		return objects.get(index);
	}

	/** Invert input weights such that: <br>
	 *  <li> the element in the output array corresponding to
	 *       the greatest element in the input array
	 *       has the value of the input min value;
	 *  <li> the element in the output array corresponding to
	 *       the smallest element in the input array
	 *       has the value of the input max value;
	 * 
	 * @param weights
	 * @param minWeight
	 * @param maxWeight
	 * @return
	 */
	public static double[] invertWeights(double[] weights, double minWeight, double maxWeight)
	{
		double[] inverseWeights = new double[weights.length];

		double newWeight = 0.0, diff = 0.0;
		for (int i = 0; i < weights.length; i++)
		{
			diff = maxWeight - weights[i];
			newWeight = minWeight + diff;
			inverseWeights[i] = newWeight;
		}
		return inverseWeights;
	}

	public static int inverseSample(RandomStream rs, double[] weights, double minWeight, double maxWeight)
	{
		double tol = 0.00001;
		if (Math.min(maxWeight, minWeight) < 0) 
			throw new IllegalArgumentException("All weights must be nonnegative");

		/* If all weights are (approximately) equal, return a uniform random index */
		if (((1.0 - (minWeight / maxWeight)) < tol) || (maxWeight < tol))
			return rs.nextInt(0, weights.length);

		double[] inverseWeights = invertWeights(weights, minWeight, maxWeight);
		return sample(rs, inverseWeights);
	}


	/** Choose an index from an array with the provided probability weights.
	 *  <br> Weights do not need to sum to 1.
	 * 
	 * @param rs
	 * @param weights
	 * @return
	 */
	public static int sample(RandomStream rs, double[] weights)
	{
		double sum = 0.0;
		double[] cumulativeWeights = new double[weights.length];
		for (int i = 0; i < weights.length; i++) 
		{
			sum += weights[i];
			cumulativeWeights[i] = sum;
		}
		/* In case there is zero weight, choose an index uniformly. */
		if (sum <= 0)
		{
			if (weights.length == 0) throw new IllegalArgumentException("Weight array must contain at least one entry.");
			return UniformIntGen.nextInt(rs, 0, weights.length - 1);
		}
		double key = UniformGen.nextDouble(rs, 0.0, sum);
		int index = Binary.insertionIndex(cumulativeWeights, key);

		return index;
	}

	/** Choose an index from an array with an inverted version of the provided probability weights. <br>
	 *  Weights do not need to sum to 1. <br>
	 *  To create the values of the inverse weight array, each weight is subtracted 
	 *  from the sum of the min and max elements of the original weight array.
	 * 
	 * @param rs
	 * @param weights
	 * @return
	 */
	public static int inverseSample(RandomStream rs, double[] weights)
	{
		double sum = 0.0, const1 = 0.0, const2 = 0.0;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double[] cumulativeWeights = new double[weights.length];

		for (int i = 0; i < weights.length; i++)
		{
			if (weights[i] < min) min = weights[i];
			if (weights[i] > max) max = weights[i];
		}

		const1 = max + min;

		for (int i = 0; i < weights.length; i++) 
		{
			const2 = const1 - weights[i];
			sum += const2;
			cumulativeWeights[i] = sum;
		}

		/* In case there is zero weight, choose an index uniformly. */
		if (sum <= 0) 
		{
			Main.logger.error("Zero weight, choosing an index uniformly");
			return UniformIntGen.nextInt(rs, 0, weights.length - 1);
		}
		double key = UniformGen.nextDouble(rs, 0, sum);
		int index = Binary.insertionIndex(cumulativeWeights, key);

		return index;
	}

	/**
	 *  A struct to hold a 1D array and its minimum and maximum values;
	 * 
	 * @author michaelfrancenelson
	 *
	 */
	static class DblArrayMinMax
	{
		double[] d;
		double min;
		double max;

		public DblArrayMinMax(double[] dbl, double mn, double mx)
		{
			this.d = dbl; this.min = mn; this.max = mx;
		}
	}
	public static void _main(String[] args) 
	{
		RandomStream rs = new MRG31k3p();
		double[] weights, invertWeights;

		weights = new double[] {1, 2, 3, 4, 4.5};
		invertWeights = invertWeights(weights, 1, 4.5);
		int[] counts = new int[weights.length], invertCounts = new int[invertWeights.length];
		int nTests = 100;
		double sum1 = 0, sum2 = 0;
		for (int i = 0; i < weights.length; i++) { sum1 += weights[i]; sum2 += invertWeights[i]; }

		for (int i = 0; i < nTests; i++)
		{
			counts[sample(rs, weights)] ++;
			invertCounts[sample(rs, invertWeights)] ++;
		}

		System.out.println("Test: weights");
		for (int i = 0; i < counts.length; i++)
		{
			System.out.print(String.format("(%.2f/", sum1 * ((double) counts[i] / (double) nTests)));
			System.out.print(String.format("%.2f) ",  weights[i]));
		}
		System.out.println("Test: inverted weights");
		for (int i = 0; i < counts.length; i++)
		{
			System.out.print(String.format("(%.2f/", sum2 * ((double) invertCounts[i] / (double) nTests)));
			System.out.print(String.format("%.2f) ",  invertWeights[i]));
		}
	}

}
