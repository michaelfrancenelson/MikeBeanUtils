package image.colorInterpolator;

import java.awt.Color;

/** Color interpolator for a boolean field.  tr
 * 
 * @author michaelfrancenelson
 *
 */
public class SimpleBooleanColorInterpolator implements ColorInterpolator
{
	private Color[] colors;
	private Color naColor = Color.GRAY;
	private int trueRGB, falseRGB, naRGB;

	/** Set the color codes.  This should be called after setting or resetting RGB colors.
	 * 
	 */
	private void init() 
	{
		trueRGB  = colors[0].getRGB();
		falseRGB = colors[colors.length - 1].getRGB();
		naRGB    = naColor.getRGB();
	}

	/**
	 * @return 'true' color code if val is greater than 0, 'false' color code otherwise.
	 */
	@Override
	public int getColor(int... val)
	{
		if (val[0] == 0) return naRGB;
		if (val[0] > 0) return trueRGB;
		return falseRGB; 
	}
	
	/**
	 * @return 'true' color code if val is greater than 0, 'false' color code otherwise.
	 */
	@Override
	public int getColor(double... val)
	{
		if (val[0] == 0) return naRGB;
		if (val[0] > 0) return trueRGB;
		return falseRGB; 
	}

	@Override
	public int getColor(boolean... val)
	{
		if (val[0]) return trueRGB;
		return falseRGB;
	}
	
	@Override
	public int getBoxedColor(Boolean... val)
	{
		if (val[0] == null) return naRGB;
		else if (val[0]) return trueRGB;
		return falseRGB;
	}
	
	@Override
	public int getColor(byte... val)
	{
		if (val[0] == 0) return naRGB;
		if (val[0] > 0) return trueRGB;
		return falseRGB; 
	}

	/**
	 * 
	 * @param colors
	 * @param naDouble
	 * @param naInt
	 * @param naColor
	 * @return
	 */
	public static ColorInterpolator factory(Color[] colors, Color naColor)
	{
		SimpleBooleanColorInterpolator ci = new SimpleBooleanColorInterpolator();

		ci.colors = colors; 
		if (naColor != null) ci.naColor = naColor;
		ci.init();
		return ci;
	}

	/**
	 *  @param endpointOffset is ignored for boolean interpolator since it's a categorical variable.
	 *  @param nLabels is ignored for boolean interpolator since there are only two possible values.
	 *  @param dblFmt ignored since scale is not numeric.
	 *  @return labels array:  {"True", "False"}
	 */
	@Override
	public String[] getGradientLabels(int nLabels, double endpointOffset, String dblFmt) {
		return new String[] {"True", "False"};
	}

	/**
	 *  @param nLabels is ignored for boolean interpolator since there are only two possible values.
	 */
	@Override
	public double[] getGradientRelativePositions(int nLabels, double endpointOffset) {

		if (endpointOffset >= 0.5) throw new IllegalArgumentException("Endpoint offset must be less than 50%.");
		double gradMin = 0.0 + endpointOffset;
		double gradMax = 1.0 - endpointOffset;
		return new double[] { gradMin, gradMax };
	}

	/** not applicable for boolean. */
	@Override public void updateMinMax(double min, double max) {}

	/** not applicable for boolean. */
	@Override public void updateMinMax(int min, int max) {} 

	/**
	 *  @param endpointOffset is ignored for boolean interpolator since it's a categorical variable.
	 *  @param nLabels is ignored for boolean interpolator since there are only two possible values.
	 *  @return labels array:  {"True", "False"}
	 */
	@Override
	public String[] getGradientLabels(int nLabels, double endpointOffset) {
		return new String[] {"True", "False"};
	}

	/**
	 * NOTE:  only the first and last color int he array are used for boolean color scale.
	 */
	@Override
	public void updateColors(Color[] colors) 
	{
		this.colors = colors;
		init();
	}
	
	@Override public int getNAColor() { return this.naColor.getRGB(); }
	@Override public String getDoubleFmt() { return null; }
}