package com.github.michaelfrancenelson.mikebeansutils.sampling;

import java.util.List;

import com.github.michaelfrancenelson.mikebeansutils.utils.ArrayUtils.DblArrayMinMax1D;

public class Weighting 
{
	public static final boolean LO_TO_HI = false, HI_TO_LO = true;

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
	public static double[] invertWeights(
			double[] weights, 
			double minWeight, 
			double maxWeight)
	{
		if (Math.min(maxWeight, minWeight) < 0) 
			throw new IllegalArgumentException("All weights must be nonnegative");

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
	
	/**
	 * Calculate a set of weights to associate with a set of objects.
	 * 
	 * @param s
	 * @param invert
	 * @param offset
	 * 
	 * @return a struct with a 2D array of double weights and records of the min and max weights
	 */
	public static<T> DblArrayMinMax1D weights(
			List<WeightedItem<T>> s, 
			boolean invert)
	{
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double val;
		double[] out = new double[s.size()];
		for (int i = 0; i < s.size(); i++)
		{
			val = Math.max(0, s.get(i).getWeight());
			out[i] = val; 
			min = Math.min(min, val);
			max = Math.max(max, val);
		}
		if (invert) out = Weighting.invertWeights(out, min, max);
		return new DblArrayMinMax1D(out, min, max);
	}
	
	
}
