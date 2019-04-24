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

}
