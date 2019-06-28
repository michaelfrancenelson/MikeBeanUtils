package sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import umontreal.ssj.rng.RandomStream;
import utils.ArrayUtils;
import utils.Binary;
import utils.ArrayUtils.DblArrayMinMax1D;

public class WeightedRandomSample 
{
	
	/**
	 * Sampling with replacement from an array of weights.
	 * 
	 * @author michaelfrancenelson
	 *
	 */
	public static class DoubleArrayWeightedSampler
	{
		double[] sums;
		double max;
		RandomStream rs;
		
		public DoubleArrayWeightedSampler(double[] weights, RandomStream rs)
		{
			sums = ArrayUtils.cumulativeSum(weights, true);
			this.rs = rs;
		}

		/** 
		 * 
		 * @return
		 */
		public int getSampleIndex()
		{
			double key = max * rs.nextDouble();
			return Binary.indexOfLessThanOrEqualToKey(sums, key);
		}
		
	}
	
	/**
	 * Sample a single item from a list of weighted items.
	 * Creates a 1D array of cumulative sums of weights, then
	 * uses randomly drawn double between 0 and the final sum
	 * as a key in a binary search.
	 * 
	 * @param w
	 * @param inverse if true, inverts the weights so that the transformed 
	 *                weight of the lowest cell is equal to the untransformed
	 *                weight of the highest cell and vice versa. Interpolates
	 *                intermediate values.
	 * @param rs source of pseudorandom numbers
	 * @param nullIfNoWeight if true, returns a null value if all input
	 *                       items have zero weight.  Otherwise returns
	 *                       if false, returns a uniformly chosen item
	 *                       if all the input items have zero weight.
	 * @return
	 */
	public static <T> WeightedItem<T> weightedRandomSample(
			List<WeightedItem<T>> w,
			boolean inverse, 
			RandomStream rs,
			boolean nullIfNoWeight)
	{
		DblArrayMinMax1D weights = Weighting.weights(w, inverse);
		if (weights.getMax() == 0)
		{
			if (nullIfNoWeight) return null;
			else return w.get(rs.nextInt(0, w.size() - 1));
		}
		double[] cumulativeWeights = ArrayUtils.cumulativeSum(weights.getData(), true);
		double key = cumulativeWeights[cumulativeWeights.length - 1] * rs.nextDouble();
		int index = Binary.insertionIndex(cumulativeWeights, key);
		return w.get(index);
	}
	
	/** Reservoir sampling that does not destroy the weights
	 *  of the input list of weighted items.
	 * 
	 * @param in
	 * @param sampleSize
	 * @param rs
	 * @return
	 */
	public static <T> List<WeightedItem<T>> reservoirES2(
			List<WeightedItem<T>> in, int sampleSize, RandomStream rs)
	{
		PriorityQueue<WeightedItem<WeightedItem<T>>> q;
		q = new PriorityQueue<WeightedItem<WeightedItem<T>>>(sampleSize);
		int itemsProcessed = 0;
		int n = in.size();
		
		for (int i = 0; i < n; i++)
		{
			WeightedItem<T> newItem = in.get(i);
			double key = genEfraimidisKey(newItem, rs);
			
			if (itemsProcessed < sampleSize)
				q.add(new WeightedItem<>(newItem, key));
			
			else
			{
				/* The head is the least element in the queue. */
				double currentThreshold = q.peek().getWeight();
				if (key > currentThreshold)
				{
					q.poll();
					q.add(new WeightedItem<>(newItem, key));
				}
			}
			itemsProcessed++;
		}
		
		List<WeightedItem<T>> out = new ArrayList<>(sampleSize);
		for (WeightedItem<WeightedItem<T>> w: q) out.add(w.getItem());
		
		return out;
	}

	/**
	 * Weighted Reservoir sampling with a side effect of altering
	 * the weights of the input list.
	 * 
	 * @param in
	 * @param sampleSize
	 * @param rs
	 * @return
	 */
	public static <T> List<T> reservoirDestructiveES(
			List<WeightedItem<T>> in, int sampleSize, RandomStream rs)
	{
		int n = in.size();
		int itemsProcessed = 0;

		PriorityQueue<WeightedItem<T>> 
		q2 = new PriorityQueue<WeightedItem<T>>(sampleSize);

		for (int i = 0; i < n; i++)
		{
			WeightedItem<T> newItem = in.get(i);
			double key = genEfraimidisKey(newItem, rs);

			if (itemsProcessed < sampleSize)
			{
				newItem.setWeight(key);
				q2.add(newItem);
			}

			else
			{
				/* The head is the least element in the queue. */
				double currentThreshold = q2.peek().getWeight();

				if (key > currentThreshold)
				{
					newItem.setWeight(key);
					q2.poll();
					q2.add(newItem);
				}
			}
			itemsProcessed++;
		}

		List<T> out3 = new ArrayList<>(sampleSize);
		for (WeightedItem<T> w: q2) out3.add(w.getItem()); 
		
		return out3;
	}

	public static <T> double genEfraimidisKey(WeightedItem<T> item, RandomStream rs)
	{
		return Math.pow(rs.nextDouble(), 1.0 / item.getWeight());
	}
}
