//package utils;
//
//public class Sequences {
//
//
//
//	public static String printSequence(double[] d, String fmt)
//	{ 
//		String out = "";
//		for (double s : d) out += String.format(fmt + " ", s);
//		System.out.println(out);
//		return out;
//	}
//
//	public static String printSequence(int[] d, String fmt)
//	{
//		String out = "";
//		for (int s : d) out += String.format(fmt + " ", s);
//		System.out.println(out);
//		return out;
//	}
//
//	public static void main(String[] args) {
//
//		demoDoubleSequences();
//		demoIntSequences();
//	}
//
//	public static void demoDoubleSequences()
//	{
//		System.out.print("double intervals: ");
//		printSequence(spacedIntervals(0.0, 50.0, 6), "%.2f");
//		System.out.print("double intervals: ");
//		printSequence(spacedIntervals(0.0, -50.0, 6), "%.2f");
//		System.out.print("double intervals: ");
//		printSequence(spacedIntervals(0.0, 0.0, 6), "%.2f");
//	}
//
//	public static void demoIntSequences()
//	{
//		System.out.print("int intervals:  ");
//		printSequence(spacedIntervals(0, -51, 6), "%d");
//		System.out.print("int intervals: ");
//		printSequence(spacedIntervals(0, 51, 6), "%d");
//		System.out.print("int intervals: ");
//		printSequence(spacedIntervals(49, 51, 6), "%d");
//	}
//
//	/**
//	 * 
//	 * @param x
//	 * @param min
//	 * @param max
//	 * @return
//	 */
//	public static double[] recenterAndNormalize(double[] x, double min, double max)
//	{
//		double[] minmax = minMax(x);
//		double[] out = new double[x.length];
//
//		double range = minmax[1] - minmax[0];
//		double range2 = max - min;
//
//		double ratio = range2 / range;
//
//		if (minmax[0] < 0 || minmax[1] < 0) throw new IllegalArgumentException("All elements must be nonnegative.");
//		if (range == 0) //throw new IllegalArgumentException("the array must have more than 1 distinct value.");
//			//			out = VectorTools.addConstantToVector(x, min);
//			return x;
//		else		
//			for (int i = 0; i < x.length; i++)
//			{
//				out[i] = min + ratio * ((x[i] - minmax[0]));
//			}
//		return out;
//	}
//
//	/** Normalize the elements of an array of doubles so that their sum is one. */
//	public static double[] normalizeSum(double[] x)
//	{
//		double sum = 0d;
//		for (double d : x) {
//			if (d < 0) throw new IllegalArgumentException("All elements of the array must be nonnegative.");
//			sum += d;
//		}
//		if (sum > 0)
//		{
//			for (int i = 0; i < x.length; i++) x[i] /= sum;
//			return x;
//		}
//		else throw new IllegalArgumentException("There must be at least one positive element in the sequence.");
//	}
//
//
//	/** Evenly spaced intervals.
//	 * @param min lower limit for the set of intervals
//	 * @param max upper limit for the set of intervals
//	 * @param nBreaks number of intervals to calculate
//	 * @return a set of endpoints for intervals, length is nIntervals + 1
//	 */
//	public static double[] spacedIntervals(double min, double max, int nBreaks)
//	{
//		double[] out = new double[nBreaks + 1];
//		out[nBreaks] = max;
//
//		double interval = (max - min) / nBreaks;
//		for (int i = 0; i < nBreaks; i++) out[i] = min + i * interval;
//		return out;
//	}
//
//	public static int[] spacedIntervals(int min, int max, int nBreaks)
//	{
//		int[] out = new int[nBreaks];
//		double interval = ((double) (max - min)) / ((double) (nBreaks - 1));
//		double sum = 0.0;
//		out[0] = min;
//		for (int i = 1; i < nBreaks - 1; i++)
//		{
//			sum += interval;
//			out[i] = min + (int) sum;
//		}
//		out[nBreaks - 1] = max;
//		return out;
//	}
//
//	
//	/**
//	 * 
//	 * @param intervalMins The 
//	 * @param values
//	 * @param key
//	 * @param toAdd
//	 */
//	public static void incrementInInterval(double[] intervalMins, double[] values, double key, double toAdd)
//	{
//		/* Find the index to which to add the quantity: */
//		int index = Binary.indexOfLessThanKey(intervalMins, key);
//		//		int index = Binary.indexOfLessThanOrEqual(intervalMins, key);
//		values[index] += toAdd;
//	}
//
//	public static void incrementInInterval(double[] intervalMins, int[] values, double key)
//	{
//		/* Find the index to which to add the quantity: */
//		int index = Binary.indexOfLessThanKey(intervalMins, key);
//		//		int index = Binary.indexOfLessThanOrEqual(intervalMins, key);
//		values[index]++;
//	}
//
//	/**
//	 * 
//	 * @param min
//	 * @param max
//	 * @param intervalWidth width for intervals.  Adjusted as needed to make evenly spaced intervals between min ana max.
//	 * @return
//	 */
//	public static double[] spacedIntervals(double min, double max, double intervalWidth)
//	{
//		double tol = 0.01;
//		double[] out;
//
//		int nIntervals = (int)(Math.abs(max - min) / intervalWidth);
//		double remainder = ((max - min) % intervalWidth);
//		double diff = Math.abs(intervalWidth - remainder);
//
//		if (!(remainder < tol | diff < tol)) nIntervals++;
//
//		out = new double[nIntervals + 1];
//
//		for(int i = 0; i < nIntervals; i++)
//		{
//			out[i] = min + i * intervalWidth;
//		}
//		out[nIntervals] = max;
//		return out;
//	}
//
//	public static int[] sequence(int start, int end) 
//	{
//		if (start == end) return new int[] {start}; 
//
//		int length = Math.abs(end - start) + 1;
//		int[] out = new int[length];
//		int sign;
//		if (end > start) sign = 1; else sign = -1;
//
//		int incr = 0;
//		for (int i = 0; i < length; i++)
//		{
//			out[i] = start + incr;
//			incr += sign;
//		}
//		return out;
//	}
//
//	public static double sum(double[] seq) { double sum = 0d; for(double d : seq) sum += d; return sum; }
//	public static int sum(int[] seq) { int sum = 0; for(int i : seq) sum += i; return sum; }
//
//	public static int[] repeatedInts(int value, int nElements)
//	{
//		int[] out = new int[nElements];
//		for(int i = 0; i < nElements; i++) out[i] = value;
//		return out;
//	}
//
//	public static double[] repeatedDoubles(double value, int nElements)
//	{
//		double[] out = new double[nElements];
//		for(int i = 0; i < nElements; i++) out[i] = value;
//		return out;		
//	}
//
//	/** Minimum and maximum values of an array
//	 * 
//	 * @param array
//	 * @param naVal ignore entries with this value
//	 * @return
//	 */
//	public static double[] minMax(double[][] array, double naVal)
//	{
//		double min = Double.MAX_VALUE;
//		double max = Double.MIN_VALUE;
//
//		for (double[] d1 : array) for (double d2 : d1)
//		{
//			if (d2 != naVal)
//			{
//				if (d2 < min) min = d2;
//				if (d2 > max) max = d2;
//			}
//		}
//		return new double[] {min, max};
//	}
//
//	/** Minimum and maximum values of an array
//	 * 
//	 * @param array
//	 * @param naVal ignore entries with this value
//	 * @return {min(x), max(x)}
//	 */
//	public static int[] minMax(int[][] array, int naVal)
//	{
//		int min = Integer.MAX_VALUE;
//		int max = Integer.MIN_VALUE;
//
//		for (int[] d1 : array) for (int d2 : d1)
//		{
//			if (d2 != naVal)
//			{
//				if (d2 < min) min = d2;
//				if (d2 > max) max = d2;
//			}
//		}
//		return new int[] {min, max};
//	}
//
//	/** Minimum and maximum values of an array
//	 * @param x array to find min and max for, does not need to be sorted
//	 * @return {min(x), max(x)}
//	 */
//	public static double[] minMax(double[] x)
//	{
//		double min1 = Double.MAX_VALUE;
//		double max1 = Double.MIN_VALUE;
//		for(double d : x) { if (d < min1) min1 = d; if (d > max1) max1 = d; }
//		return new double[] {min1, max1};
//	}
//
//}
