package utils;

public class ArrayUtils 
{
	
	
		
	public static double[] cumulativeSum(double[] in, boolean strictlyPositive)
	{
		double[] out = new double[in.length];
		double val;
		double sum = 0.0;
		for (int i = 0; i < in.length; i++)
		{
			val = in[i];
			if (strictlyPositive) sum += Math.max(0, val); 
			else sum += in[i];

			out[i] = sum;
		}
		return out;
	}
	
	
	
	
	/**
	 * 
	 * @param relCoord
	 * @param length
	 * @return
	 */
	public static int relToAbsCoord(double relCoord, int length)
	{
		return Math.min(
				length - 1, 
				(int)Math.floor(((double) length) * relCoord));
	}

	public static double absToRelCoord(int point, int length)
	{
		return absToRelCoord((double) point, length);
	}

	public static double absToRelCoord(double point, int length)
	{
		return  Math.min(1.0, Math.max(0.0,  point / ((double) length)));
	}


	/** 
	 *  A container for an int array with its min and max values
	 *  already calculated.
	 * @author michaelfrancenelson
	 *
	 */
	public static class IntArrayMinMax
	{
		public IntArrayMinMax(int[][] d, int mn, int mx)
		{this.data = d; this.min = mn; this.max = mx; }
		int[][] data; int min; int max;
		public int[][] getDat() { return this.data; }
		public int getMin() { return this.min; }
		public int getMax() { return this.max; }
	}

	/** 
	 *  A container for a byte array with its min and max values
	 *  already calculated.
	 * @author michaelfrancenelson
	 *
	 */
	public static class ByteArrayMinMax
	{
		public ByteArrayMinMax(byte[][] d, byte mn, byte mx)
		{this.data = d; this.min = mn; this.max = mx; }
		byte[][] data; byte min; byte max;
		public byte[][] getDat() { return this.data; }
		public byte getMin() { return this.min; }
		public byte getMax() { return this.max; }
	}

	/**
	 * A container for a double array with its min
	 * and max values precalculated.
	 * @author michaelfrancenelson
	 *
	 */
	public static class DblArrayMinMax
	{
		public DblArrayMinMax(double[][] d, double mn, double mx)
		{this.data = d; this.min = mn; this.max = mx; }
		double[][] data; double min; double max;
		public double[][] getDat() { return this.data; }
		public double getMin() { return this.min; }
		public double getMax() { return this.max; }
	}


	
	
	
	public static int[][] transpose(int[][] dat)
	{
		int[][] out = new int[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static byte[][] transpose(byte[][] dat)
	{
		byte[][] out = new byte[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static short[][] transpose(short[][] dat)
	{
		short[][] out = new short[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static long[][] transpose(long[][] dat)
	{
		long[][] out = new long[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static char[][] transpose(char[][] dat)
	{
		char[][] out = new char[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static String[][] transpose(String[][] dat)
	{
		String[][] out = new String[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static double[][] transpose(double[][] dat)
	{
		double[][] out = new double[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}
	public static float[][] transpose(float[][] dat)
	{
		float[][] out = new float[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}

	public static boolean[][] transpose(boolean[][] dat)
	{
		boolean[][] out = new boolean[dat[0].length][dat.length];
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++) out[j][i] = dat[i][j];
		return out;
	}


	public static double[] getArrMinMax(double[][] dat)
	{
		double dataMin = Double.MAX_VALUE; double dataMax = -dataMin;
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			double val = dat[i][j];
			if (val < dataMin) dataMin = val; 
			if (val > dataMax) dataMax = val;
		}
		return new double[] { dataMin, dataMax };
	}
	
	public static double[] getArrMinMax(int[][] dat)
	{
//		double dataMin = Double.MAX_VALUE;
//		double dataMax = -dataMin;
		int intMin = Integer.MAX_VALUE;
		int intMax = Integer.MIN_VALUE;
		
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			int val = dat[i][j];
//			double val = doubleCaster(dat[i][j]);

			if (val != Integer.MIN_VALUE) 
			{
				if (val < intMin) intMin = val;
				if (val > intMax) intMax = val;
			}
		}
		return new double[] { intMin, intMax };
	}

	public static int[] getIntArrMinMax(int[][] dat, int naVal)
	{
		int intMin = Integer.MAX_VALUE;
		int intMax = Integer.MIN_VALUE;
		
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			int val = dat[i][j];
			if (val != naVal) 
			{
				if (val < intMin) intMin = val;
				if (val > intMax) intMax = val;
			}
		}
		return new int[] { intMin, intMax };
	}
	
	public static double[] getArrMinMax(char[][] dat)
	{
		double dataMin = Double.MAX_VALUE; double dataMax = -dataMin;
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			double val = doubleCaster(dat[i][j]);
			if (val < dataMin) dataMin = val; if (val > dataMax) dataMax = val;
		}
		return new double[] { dataMin, dataMax };
	}
	public static double[] getArrMinMax(byte[][] dat)
	{
		double dataMin = Double.MAX_VALUE; double dataMax = -dataMin;
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			double val = doubleCaster(dat[i][j]);
			if (val < dataMin) dataMin = val; if (val > dataMax) dataMax = val;
		}
		return new double[] { dataMin, dataMax };
	}
	public static double[] getArrMinMax(short[][] dat)
	{
		double dataMin = Double.MAX_VALUE; double dataMax = -dataMin;
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			double val = doubleCaster(dat[i][j]);
			if (val < dataMin) dataMin = val; if (val > dataMax) dataMax = val;
		}
		return new double[] { dataMin, dataMax };
	}
	public static double[] getArrMinMax(long[][] dat)
	{
		double dataMin = Double.MAX_VALUE; double dataMax = -dataMin;
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			double val = doubleCaster(dat[i][j]);
			if (val < dataMin) dataMin = val; if (val > dataMax) dataMax = val;
		}
		return new double[] { dataMin, dataMax };
	}
	public static double[] getArrMinMax(float[][] dat)
	{
		double dataMin = Double.MAX_VALUE; double dataMax = -dataMin;
		for (int i = 0; i < dat.length; i++) for (int j = 0; j < dat[0].length; j++)
		{
			double val = doubleCaster(dat[i][j]);
			if (val < dataMin) dataMin = val; if (val > dataMax) dataMax = val;
		}
		return new double[] { dataMin, dataMax };
	}
	public static double[] getArrMinMax(boolean[][] dat)
	{
		return new double[] { 0.0, 1.0 };
	}

	public static double doubleCaster(float f) { return (double) f; }
	public static double doubleCaster(byte f) { return (double) f; }
	public static double doubleCaster(char f) { return (double) f; }
	public static double doubleCaster(short f) { return (double) f; }
	public static double doubleCaster(int f) { return (double) f; }
	public static double doubleCaster(long f) { return (double) f; }
	public static double doubleCaster(boolean f) { if (f) return 1; return 0; }
	public static double doubleCaster(Boolean f)
	{ 
		if (f == null) return -Double.MAX_VALUE;
		if (f) return 1; 
		return 0;
	}

	public static String stringCaster(double f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(float f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(byte f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(char f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(short f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(int f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(long f, String fmt) { return String.format(fmt, f); }
	public static String stringCaster(boolean f, String fmt) { if (f) return String.format(fmt, "true"); return String.format(fmt, "false"); }
	public static String stringCaster(Boolean f, String fmt) 
	{
		if (f == null) return String.format(fmt, "NA");
		if (f) return String.format(fmt, "true");
		return String.format(fmt, "false"); 
	}

}
