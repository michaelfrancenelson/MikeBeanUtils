package image;

import java.awt.Color;

/** 
 * 
 * @author michaelfrancenelson
 *
 */
public interface ColorInterpolator 
{
	/**
	 * 
	 * @param nLabels        Number of labels to generate.
	 * @param endpointOffset Percentage of interval to skip at endpoints.
	 * @param dlbFmt         Display format for the output labels.
	 * @return 1D array of text labels of evenly spaced intervals within the range of values
	 *         (possibly adjusted from endpoints) covered by the interpolator.
	 */
	public String[] getGradientLabels(int nLabels, double endpointOffset, String dblFmt);

	/**
	 * 
	 * @param nLabels        Number of labels to generate.
	 * @param endpointOffset Percentage of interval to skip at endpoints.
	 * @return 1D array of text labels of evenly spaced intervals within the range of values
	 *         (possibly adjusted from endpoints) covered by the interpolator.
	 */
	public String[] getGradientLabels(int nLabels, double endpointOffset);

	/**
	 * 
	 * @param nLabels        Number of positions to generate.
	 * @param endpointOffset Percentage of interval to skip at endpoints.
	 * @return 1D array of relative positions for labels of evenly spaced intervals within the range of values 
	 *         (possibly adjusted from endpoints) covered by the interpolator.
	 */
	public double[] getGradientRelativePositions(int nLabels, double endpointOffset);

	/**
	 * 
	 * @param val int values from which the color is interpolated.   
	 *            If this matches the interpolator's NA value, the NA color is returned.
	 * @return resulting color
	 */
	public int getColor(int... val);

	/**
	 * 
	 * @param val double values from which the color is interpolated.
	 *            If this matches the interpolator's NA value, the NA color is returned.
	 * @return resulting color
	 */
	public int getColor(double... val);

	/**
	 * 
	 * @param val boolean value from which color is interpolated.  
	 * @return resulting color. There is no NA color possible with this method.
	 */
	public int getColor(boolean val);

	/**
	 * 
	 * @return the color code to display for a NA pixel value
	 */
	public int getNAColor();
	
	/** Set new min and max for the color scale
	 * 
	 * @param min
	 * @param max
	 */
	public void updateMinMax(double min, double max);
	
	/** Set new min and max for the color scale
	 * 
	 * @param min
	 * @param max
	 */
	public void updateMinMax(int min, int max);
	
	/** Set new colors for the scale
	 * 
	 * @param colors
	 */
	public void updateColors(Color[] colors);
	
}

