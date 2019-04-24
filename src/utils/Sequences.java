package utils;

public class Sequences
{

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

		//		System.out.println("ArrayImageFactory.spacedIntervals: min = " + min + " max = " + max);	
		//		System.out.println("ArrayImageFactory.spacedIntervals: range = " +
		//				range + " requested number of breaks (nBreaks) = " + nBreaks);

		/* If the number of breaks is greater than the range, truncate nElenemts to range.
		 * If the number of breaks is less than 1, automatically create number of breaks from the range.
		 */

		if (start == end)
		{
			nElements = 1;
			interval = 0;
			//			System.out.println("ArrayImageFactory.spacedIntervals: min and max qre equal");
		}
		else if ((nBreaks > range) ||  (nBreaks < 1))
		{
			nElements = range + 1;
			interval = sign;
			//			System.out.println("ArrayImageFactory.spacedIntervals: auto interval = " + interval);
		}
		else
		{
			nElements = nBreaks;
			interval = sign * (double)(range + 0) / ((double)nElements - 1.0);
			//			System.out.println("ArrayImageFactory.spacedIntervals: calculated interval = " + interval);
		}

		//		System.out.println("ArrayImageFactory.spacedIntervals:  nElements = " + nElements);
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

		//		for (int i = 0; i < out.length; i++) System.out.println("ArrayImageFactory.spacedIntervals() out[" + i + "] = " + out[i]);
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
		Boolean[][] out;
		Boolean[] dat;
		if (includeNA) dat = new Boolean[] { true, false, null };
		else dat = new Boolean[] { true, false};

		if (horizontal)
		{
			out = new Boolean[dat.length][1];
			for (int i = 0; i < dat.length; i++) out[i][0] = dat[i];
		}
		else
		{
			out = new Boolean[1][dat.length];
			for (int j = 0; j < dat.length; j++) out[0][j] = dat[j];
		}
		return out;
	}

}
