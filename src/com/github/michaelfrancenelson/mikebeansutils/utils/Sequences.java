package com.github.michaelfrancenelson.mikebeansutils.utils;

public class Sequences
{

	public static double[] normalize1(double[] in, double min, double max)
	{
		double range = min - max;
		double tol = 0.00001;
		double vMin = Double.MAX_VALUE;
		double vMax = -Double.MAX_VALUE;

		double[] out = new double[in.length];
		if (range <= 0)
		{
			for (int i = 0; i < in.length; i++) out[i] = 1.0;
			return out;
		}

		for (int i = 0; i < in.length; i++) 
		{
			double val = Math.max(0.0, Math.min(1.0, (in[i] - min) / range));
			out[i] = val;
			vMin = Math.min(val, vMin);
			vMax = Math.min(val, vMax);
			if (val > 1 || val < 0) throw new IllegalArgumentException ("value is outside range 0 - 1");
			if (Math.abs(vMin - 0.0) > tol) throw new IllegalArgumentException("minimum value is not zero");
			if (Math.abs(vMax - 1.0) > tol) throw new IllegalArgumentException("maximum value is not one");
		}
		return out;
	}

	/**
	 * 
	 * @param in
	 * @param newMin
	 * @param newMax
	 * @return
	 */
	public static double[] normalize2(
			double[] in, 
			double min, double max, 
			double newMin, double newMax)
	{
		double range = max - min;
		double newRange = newMax - newMin;
		double tol = 0.00001;
		double vMin = Double.MAX_VALUE;
		double vMax = -Double.MAX_VALUE;

		double[] out = new double[in.length];
		if (range <= 0)
		{
			for (int i = 0; i < in.length; i++) out[i] = 1.0;
			return out;
		}

		for (int i = 0; i < in.length; i++) 
		{
			double val = Math.max(0.0, Math.min(1.0, (in[i] - min) / range));
			double val2 = newMin + val * newRange;
			out[i] = val2;
			vMin = Math.min(val2, vMin);
			vMax = Math.max(val2, vMax);
			if (val > 1 || val < 0) throw new IllegalArgumentException ("value is outside range 0 - 1");
		}
		if (Math.abs(vMin - newMin) > tol) throw new 
		IllegalArgumentException("minimum value \n" + vMin + " \nis not the new min \n" + newMin);
		if (Math.abs(vMax - newMax) > tol) throw new 
		IllegalArgumentException("aximum value \n" + vMax + " \nis not the new max \n" + newMax);
		return out;
	}

	/**
	 * 
	 * @param start interval endpoint
	 * @param end interval endpoint
	 * @param nBreaks number of breaks.  If < 1, breaks are calculated automatically.
	 * @return
	 */
	public static int[] spacedIntervals(int start, int end, int nBreaks)
	{
		double interval;
		double sign = 1.0; if (end < start) sign = -1.0;
		int range = (int) Math.abs(end - start);

		int nElements;

		/* If the number of breaks is greater than the range, truncate nElenemts to range.
		 * If the number of breaks is less than 1, automatically create number of breaks from the range.
		 */

		if (start == end)
		{
			nElements = 1;
			interval = 0;
		}
		else if ((nBreaks > range) ||  (nBreaks < 1))
		{
			nElements = range + 1;
			interval = sign;
		}
		else
		{
			nElements = nBreaks;
			interval = sign * (double)(range + 0) / ((double)nElements - 1.0);
		}

		int[] out = new int[nElements];

		/* In case of non-integer intervals, this allows rounding to maintain the right number of breaks. */
		double sum = 0.0;
		out[0] = start;
		for (int i = 1; i < nElements - 1; i++)
		{
			sum += interval;
			out[i] = start + (int) sum;
		}
		out[nElements - 1] = end;
		return out;
	}

	/** Evenly spaced intervals.
	 * @param start lower limit for the set of intervals
	 * @param end upper limit for the set of intervals
	 * @param nBreaks number of intervals to calculate
	 * @return a set of endpoints for intervals, length is nIntervals + 1
	 */
	public static double[] spacedIntervals(double start, double end, int nBreaks)
	{
		double[] out = new double[nBreaks + 1];
		out[nBreaks] = end;

		double interval = (end - start) / nBreaks;
		for (int i = 0; i < nBreaks; i++) out[i] = start + i * interval;
		return out;
	}

	public static double[][] spacedIntervals2D(double start, double end, int nSteps, boolean horizontal)
	{
		double[][] out;

		double[] dat = spacedIntervals(start, end, nSteps);

		if (horizontal)
		{
			out = new double[dat.length][1];
			for (int i = 0; i < dat.length; i++) out[i][0] = dat[i];
		}
		else
		{
			out = new double[1][dat.length];
			for (int j = 0; j < dat.length; j++) out[0][j] = dat[j];

		}
		return out;
	}

	public static int[][] spacedIntervals2D(int start, int end, int nSteps, boolean horizontal)
	{
		int[][] out;
		int[] dat = spacedIntervals(start, end, nSteps);

		if (horizontal)
		{
			out = new int[dat.length][1];
			for (int i = 0; i < dat.length; i++) out[i][0] = dat[i];
		}
		else
		{
			out = new int[1][dat.length];
			for (int j = 0; j < dat.length; j++) out[0][j] = dat[j];

		}
		return out;
	}

	public static byte[][] spacedIntervals2D(byte start, byte end, int nSteps, boolean horizontal)
	{
		byte[][] out;
		int[] dat = spacedIntervals((int) start, (int) end, nSteps);

		if (horizontal)
		{
			out = new byte[dat.length][1];
			for (int i = 0; i < dat.length; i++) out[i][0] = (byte) dat[i];
		}
		else
		{
			out = new byte[1][dat.length];
			for (int j = 0; j < dat.length; j++) out[0][j] = (byte) dat[j];
		}
		return out;
	}

	public static Boolean[][] booleanGradient2D(boolean includeNA, boolean horizontal)
	{
		return booleanGradient2D(includeNA, horizontal, true);
	}

	public static Boolean[][] booleanGradient2D(boolean includeNA, boolean horizontal, boolean loToHi)
	{
		Boolean[][] out;
		Boolean[] dat;
		if (includeNA) {
			dat = new Boolean[] { true, false, null };
			if (!loToHi) dat = new Boolean[] { null, false, true };
		}
		else
		{
			dat = new Boolean[] { true, false};
			if (!loToHi) dat = new Boolean[] { false, true };
		}

		int startIndex, endIndex, increment;
		if (loToHi) { startIndex = 0; endIndex = dat.length; increment = 1; }
		else {startIndex = dat.length - 1; endIndex = -1; increment = -1; }

		if (horizontal)
		{

			out = new Boolean[dat.length][1];
			for (int i = startIndex; i != endIndex; i += increment) out[i][0] = dat[i];
		}
		else
		{
			out = new Boolean[1][dat.length];
			for (int j = startIndex; j != endIndex; j++) out[0][j] = dat[j];
		}
		return out;
	}

	/**
	 * Calculate the sum of all the <code>double</code> elements of a  
	 * 1D array.
	 *  
	 * @param dd input array
	 * @return sum of elements in dd
	 */
	public static double arraySum(double[] dd) 
	{
		double sum = 0.0;
		for (int i = 0; i < dd.length; i++) sum += dd[i];
		return sum;
	}

	/** 
	 * Calculate the sum of all the <code>int</code> elements of a  
	 * 1D array.
	 * 
	 * @param ii input array
	 * @return sum of elements in ii
	 */
	public static int arraySum(int[] ii) 
	{
		int sum = 0;
		for (int i = 0; i < ii.length; i++) sum += ii[i];
		return sum;
	}
}
