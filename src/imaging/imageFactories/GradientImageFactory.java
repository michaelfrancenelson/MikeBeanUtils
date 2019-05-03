package imaging.imageFactories;

import java.awt.Image;
import java.awt.image.BufferedImage;

import imaging.colorInterpolator.ColorInterpolator;
import utils.Sequences;

public class GradientImageFactory 
{


	/**
	 * Build an image showing a color gradient using double data.
	 * @param min
	 * @param max
	 * @param nSteps
	 * @param ci
	 * @param direction if 1, low index is min value; if 2, low index is max value
	 * @param orientation if 1, gradient is oriented vertically; if 2, gradient is horizontal
	 * @return
	 */
	public static Image buildGradientImage(
			double min, double max, int nSteps, ColorInterpolator ci,
			boolean lowToHigh, boolean horizontal)
	{
		if (lowToHigh) { if (min > max) { double t = min; min = max; min = t; } }
		if (nSteps == 1) nSteps = 2;
		if (nSteps <= 0) nSteps = 100;
		double[][] data = Sequences.spacedIntervals2D(min, max, nSteps - 1, horizontal);
		return PrimitiveImageFactory.buildImage(data, ci, false, false, false).img;
	}

	/**
	 * Build an image showing a color gradient using double data.
	 * @param start
	 * @param end
	 * @param nSteps
	 * @param ci
	 * @param direction if 1, low index is min value; if 2, low index is max value
	 * @param orientation if 1, gradient is oriented vertically; if 2, gradient is horizontal
	 * @return
	 */
	public static Image buildGradientImage(
			int start, int end, int nSteps, ColorInterpolator ci,
			boolean lowToHigh, boolean horizontal)
	{
		if (nSteps == 1) nSteps++;
		if (lowToHigh && start > end)
		{
			int t = start; end = start; start = t;
		}
		int[][] data = Sequences.spacedIntervals2D(start, end, nSteps, horizontal);
		return PrimitiveImageFactory.buildImage(data, ci, false, false, false).img;
	}

	
	public static Image buildBooleanGradient(
		ColorInterpolator ci, boolean horizontal, boolean includeNA)
	{
		BufferedImage img;
		int maxDim; 	if (includeNA) maxDim = 3; else maxDim = 2;
		
		if (horizontal)
		{
			img = new BufferedImage(maxDim, 1, ObjectImageFactory.RGB_TYPE);
			img.setRGB(0, 0, ci.getColor(true));
			img.setRGB(1, 0, ci.getColor(false));
			if (includeNA) img.setRGB(2, 0, ci.getNAColor());
		}
		else 
		{
			img = new BufferedImage(1, maxDim, ObjectImageFactory.RGB_TYPE);
			img.setRGB(0, 0, ci.getColor(true));
			img.setRGB(0, 1, ci.getColor(false));
			if (includeNA) img.setRGB(0, 2, ci.getNAColor());
		}
		return img;
	}

	

}
