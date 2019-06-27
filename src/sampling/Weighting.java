package sampling;

public class Weighting 
{
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
	
}
