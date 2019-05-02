package imaging.colorInterpolator;

import java.awt.Color;

import utils.Binary;
import utils.Binary.IndicesAndRelativePosition;
import utils.Sequences;

/**
 * 
 * @author michaelfrancenelson
 *
 */
public class SimpleColorInterpolator implements ColorInterpolator
{
	private Color[] colors;

	private Color naColor = Color.GRAY;
	private int naInt = Integer.MIN_VALUE;
	private double naDouble = Double.MIN_VALUE;

	private double minVal, maxVal;
	private String dblFmt;
	private double[] breaks;

	/**
	 * 
	 * @param c1
	 * @param c2
	 * @param proportion
	 * @return
	 */
	private int interpolateColor(Color c1, Color c2, double proportion)
	{
		double prop1 = 1 - proportion;
		int red = (int) (c1.getRed() * prop1 + c2.getRed() * proportion);
		int green = (int) (c1.getGreen() * prop1 + c2.getGreen() * proportion);
		int blue = (int) (c1.getBlue() * prop1 + c2.getBlue() * proportion);
		int col = (red << 16) | (green << 8) | blue;
		return col;
	}

	/**
	 * 
	 * @param colors
	 * @param min
	 * @param max
	 * @param naDouble
	 * @param naInt
	 * @param naColor
	 * @return
	 */
	public static ColorInterpolator factory(
			Color[] colors, 
			double min, double max,
			double naDouble, int naInt, 
			Color naColor, String dblFmt)
	{
		SimpleColorInterpolator ci = new SimpleColorInterpolator();
		if (min > max)
		{
			double t = min;
			min = max; max = t;
		}
//		if (dblFmt == null) dblFmt = "%.2f";
		ci.colors = colors; ci.naDouble = naDouble; ci.naInt = naInt; ci.naColor = naColor;
		ci.minVal = min; ci.maxVal = max;
		ci.dblFmt = dblFmt;
		ci.setBreaks();
		return ci;
	}

	/**
	 * 
	 * @param colors
	 * @param min
	 * @param max
	 * @param naDouble
	 * @param naInt
	 * @param naColor
	 * @return
	 */
	public static ColorInterpolator factory(
			Color[] colors,
			int min, int max, 
			double naDouble, int naInt,
			Color naColor, String dblFmt)
	{ return factory(colors, (double) min, (double) max, naDouble, naInt, naColor, dblFmt); }

	/** 
	 *  Rebuild the interpolation intervals when min/max values or count of colors changes.
	 */
	private void setBreaks()
	{
		double[] breaks = Sequences.spacedIntervals((double) minVal, (double) maxVal, colors.length - 1);
		this.breaks = breaks;
	}

	/**
	 *  Updates the interpolation intervals;
	 */
	@Override
	public void updateMinMax(double min, double max) 
	{
		this.minVal = min; this.maxVal = max;
		breaks = Sequences.spacedIntervals(minVal, maxVal, colors.length - 1); 
	}

	/**
	 *  Updates the interpolation intervals;
	 */
	@Override
	public void updateMinMax(int min, int max) { updateMinMax((double) min, (double) max); }

	@Override
	public String[] getGradientLabels(int nLabels, double endpointOffset, String dblFmt) {
		double range = maxVal - minVal;
		double offset = range * endpointOffset;
		double gradMin = minVal + offset;
		double gradMax = maxVal - offset;
		double[] points = Sequences.spacedIntervals(gradMin, gradMax, nLabels - 1);
		String[] labels = new String[nLabels];
		for (int i = 0; i < points.length; i++)
			labels[i] = String.format(dblFmt, points[i]);
		return labels;
	}

	@Override
	public double[] getGradientRelativePositions(int nLabels, double endpointOffset) {
		if (endpointOffset >= 0.5) throw new IllegalArgumentException("Endpoint offset must be less than 50%.");
		double gradMin = 0.0 + endpointOffset;
		double gradMax = 1.0 - endpointOffset;

		return Sequences.spacedIntervals(gradMin, gradMax, nLabels - 1);
	}

	@Override
	public String[] getGradientLabels(int nLabels, double endpointOffset) {
		return getGradientLabels(nLabels, endpointOffset, this.dblFmt);
	}

	/** 
	 *  Rebuilds the interpolation intervals to adjust for number of updated colors.
	 */
	@Override
	public void updateColors(Color[] colors) 
	{ 
		this.colors = colors;
		updateMinMax(this.minVal, this.maxVal);
	}

	/**
	 * @param val only the first is used
	 * @return
	 */
	@Override public int getNAColor() { return this.naColor.getRGB(); }
	/**
	 * @param val only the first is used
	 * @return
	 */

	@Override 
	public int getColor(boolean... val)
	{
		if (val[0]) return colors[0].getRGB();
		return colors[colors.length - 1].getRGB();
	}

	/**
	 * @param val only the first is used
	 * @return
	 */
	@Override public int getColor(byte... val) { return getColor((int) val[0]); }
	/**
	 * @param val only the first is used
	 * @return
	 */
	
	@Override 
	public int getColor(int... val) 
	{
		if (val[0] == naInt) return naColor.getRGB();
		return getColor((double) val[0]); 
	}

	/**
	 * @param val only the first is used
	 * @return
	 */
	@Override 
	public int getColor(double... val) 
	{ 
		if (val[0] == naDouble) return naColor.getRGB();
		IndicesAndRelativePosition pos = Binary.interpolateRelativePosition(breaks, val[0]);
		int out = interpolateColor(colors[pos.lowIndex], colors[pos.highIndex], pos.relativePosition);
		return out;
	}

	@Override
	public int getBoxedColor(Boolean... val)
	{
		if (val == null) naColor.getRGB(); 
		if (val[0] == null) return  naColor.getRGB();
		if (val[0]) return colors[0].getRGB();
		return colors[colors.length - 1].getRGB();
	}

	@Override public String getDoubleFmt() { return dblFmt; }
	@Override public int getColor(short... val) { return getColor((int) val[0]); }
	@Override public int getColor(long... val)  { return getColor((int) val[0]); }
	@Override public int getColor(float... val)  { return getColor((double) val[0]); }
	
}