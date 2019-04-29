package utils;

public class ArrayUtils 
{
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
	
}
