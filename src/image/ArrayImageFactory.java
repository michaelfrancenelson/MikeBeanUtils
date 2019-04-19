package image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import beans.memberState.FieldWatcher;
import image.arrayImager.ObjectArrayImager;
import image.colorInterpolator.ColorInterpolator;


public class ArrayImageFactory 
{
	public static final int RGB_TYPE = BufferedImage.TYPE_3BYTE_BGR;

	/**
	 *  Build an image from a 2D array of objects using an already existing imager.
	 *  Wrapper for {@link #buildArrayImage(Object[][], FieldWatcher, ColorInterpolator, int, int, boolean, boolean)}
	 *  
	 * @param data
	 * @param imager
	 * @param field optional field name.  If null, the image corresponding to the currently selected field in the imager will be build.
	 * @param orientation1
	 * @param orientation2
	 * @param transpose
	 * @param boolNA
	 * @return
	 */
	public static <T> Image buildArrayImage(
			T[][] data, ObjectArrayImager<T> imager, String field,
			//			int orientation1, int orientation2, 
			boolean flipAxisX, boolean flipAxisY,
			boolean transpose, boolean boolNA)
	{
		ColorInterpolator ci;
		if (field != null) imager.setField(field);
		if (imager.getWatcher().getField().getType().getSimpleName().equals("boolean"))
			ci = imager.getBooleanInterpolator();
		else ci = imager.getInterpolator();

		return buildArrayImage(data, imager.getWatcher(), ci,
				flipAxisX, flipAxisY, transpose, boolNA);
	}

	/**
	 *  Build an image from a 2D array of objects using a field watcher and color interpolator.<br>
	 *  This is the method that actually builds the image.
	 * 
	 * @param data
	 * @param w
	 * @param ci
	 * @param orientation1
	 * @param orientation2
	 * @param transpose
	 * @param boolNA
	 * @return
	 */
	public static <T> Image buildArrayImage(
			T[][] data, FieldWatcher<T> w, ColorInterpolator ci, 
			//			int orientation1, int orientation2,
			boolean flipAxisX, boolean flipAxisY,
			boolean transpose, boolean boolNA)
	{
		int imgType = RGB_TYPE;
		ImageDimensions dim = new ImageDimensions(
				data.length, data[0].length, flipAxisX, flipAxisY, transpose);
		//		data.length, data[0].length, orientation1, orientation2, transpose);
		BufferedImage img = new BufferedImage(dim.lengthX, dim.lengthY, imgType);

		String fieldType = w.getField().getType().getSimpleName();

		int datIndex1 = 0, datIndex2 = 0;

		switch(fieldType)
		{
		case("int"):
		{
			if (!transpose)
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						int val = w.getIntVal(data[datIndex1][datIndex2]);
						img.setRGB(i, j, ci.getColor(val));
						datIndex2++;
					}
					datIndex1++;
					datIndex2 = 0;
				}
			else
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						img.setRGB(i, j, ci.getColor(w.getIntVal(data[datIndex1][datIndex2])));
						datIndex1++; 
					}
					datIndex2++;
					datIndex1 = 0;
				}
			break;
		}
		case("double"):
			if (!transpose)
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						T t = data[datIndex1][datIndex2];
						double val = w.getDoubleVal(t);
						img.setRGB(i, j, ci.getColor(val));
						datIndex2++;
					}
					datIndex1++;
					datIndex2 = 0;
				}
			else
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						img.setRGB(i, j, ci.getColor(w.getDoubleVal(data[datIndex1][datIndex2])));
						datIndex1++; 
					}
					datIndex2++;
					datIndex1 = 0;
				}
		break;
		case("boolean"):
		{

			break;
		}

		}

		return img;
	}


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
		if (nSteps == 1) nSteps = 2;
		if (nSteps <= 0) nSteps = 100;
		double[] data = spacedIntervals(min, max, nSteps - 1);
		return buildGradientImage(data, nSteps, ci, lowToHigh, horizontal);
	}
	
	public static Image buildGradientImage(
			double[] data, int nSteps, ColorInterpolator ci,
			boolean lowToHigh, boolean horizontal)
	{

		int imgType = RGB_TYPE;
		ImageDimensions dir = new ImageDimensions(data.length, lowToHigh, horizontal);
		BufferedImage img = new BufferedImage(dir.lengthX, dir.lengthY, imgType);
		int datIndex = 0;
		if (dir.incrementY == 0)
			for (int i = dir.startX; i != dir.endX; i += dir.incrementX)
			{
				img.setRGB(i, 0, ci.getColor(data[datIndex]));
				datIndex++;
			}
		else
			for (int i = dir.startY; i != dir.endY; i += dir.incrementY)
			{
				img.setRGB(0, i, ci.getColor(data[datIndex]));
				datIndex++;
			}

		return img;
	}

	public static Image buildGradientImage(
			int[] data, int nSteps, ColorInterpolator ci,
			boolean lowToHigh, boolean horizontal)
	{
		int imgType = RGB_TYPE;
		ImageDimensions dir = new ImageDimensions(data.length, lowToHigh, horizontal);
		BufferedImage img = new BufferedImage(dir.lengthX, dir.lengthY, imgType);
		int datIndex = 0;
		if (dir.incrementY == 0)
			for (int i = dir.startX; i != dir.endX; i += dir.incrementX)
			{
				img.setRGB(i, 0, ci.getColor(data[datIndex]));
				datIndex++;
			}
		else
			for (int i = dir.startY; i != dir.endY; i += dir.incrementY)
			{
				img.setRGB(0, i, ci.getColor(data[datIndex]));
				datIndex++;
			}
		return img;
	}
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
			int min, int max, int nSteps, ColorInterpolator ci,
			boolean lowToHigh, boolean horizontal)
	{
		if (nSteps == 1) nSteps++;
		int[] data = spacedIntervals(min, max, nSteps);
		return buildGradientImage(data, nSteps, ci, lowToHigh, horizontal);
//		int imgType = RGB_TYPE;
//		ImageDimensions dir = new ImageDimensions(data.length, lowToHigh, horizontal);
//		BufferedImage img = new BufferedImage(dir.lengthX, dir.lengthY, imgType);
//		int datIndex = 0;
//		if (dir.incrementY == 0)
//			for (int i = dir.startX; i != dir.endX; i += dir.incrementX)
//			{
//				img.setRGB(i, 0, ci.getColor(data[datIndex]));
//				datIndex++;
//			}
//		else
//			for (int i = dir.startY; i != dir.endY; i += dir.incrementY)
//			{
//				img.setRGB(0, i, ci.getColor(data[datIndex]));
//				datIndex++;
//			}
//
//		return img;
	}

	public static Image buildGradientImage(
			boolean includeNABoolean, ColorInterpolator ci,
			boolean lowToHigh, boolean horizontal)
	{
		int imgType = RGB_TYPE;

		int n = 2;
		if (includeNABoolean) n = 3;

		ImageDimensions dir = new ImageDimensions(n, lowToHigh, horizontal);
		boolean[] data = new boolean[] {true, false, false};
		BufferedImage img = new BufferedImage(dir.lengthX, dir.lengthY, imgType);

		int datIndex = 0;
		if (dir.incrementY == 0)
			for (int i = dir.startX; i != dir.endX; i += dir.incrementX)
			{
				if (datIndex == 2)	img.setRGB(i, 0, ci.getNAColor());
				else img.setRGB(i, 0, ci.getColor(data[datIndex]));
				datIndex++;
			}
		else
			for (int i = dir.startY; i != dir.endY; i += dir.incrementY)
			{
				if (datIndex == 2)	img.setRGB(0, i, ci.getNAColor());
				else img.setRGB(0, i, ci.getColor(data[datIndex]));
				datIndex++;
			}

		return img;
	}

	/** Evenly spaced intervals.
	 * @param min lower limit for the set of intervals
	 * @param max upper limit for the set of intervals
	 * @param nBreaks number of intervals to calculate
	 * @return a set of endpoints for intervals, length is nIntervals + 1
	 */
	public static double[] spacedIntervals(double min, double max, int nBreaks)
	{
		double[] out = new double[nBreaks + 1];
		out[nBreaks] = max;

		double interval = (max - min) / nBreaks;
		for (int i = 0; i < nBreaks; i++) out[i] = min + i * interval;
		return out;
	}

	/**
	 * 
	 * @param min interval endpoint
	 * @param max interval endpoint
	 * @param nBreaks number of breaks.  If < 1, breaks are calculated automatically.
	 * @return
	 */
	public static int[] spacedIntervals(int min, int max, int nBreaks)
	{
		double interval;
		double sign = 1.0; if (max < min) sign = -1.0;
		int range = (int) Math.abs(max - min);

		int nElements;

		//		System.out.println("ArrayImageFactory.spacedIntervals: min = " + min + " max = " + max);	
		//		System.out.println("ArrayImageFactory.spacedIntervals: range = " +
		//				range + " requested number of breaks (nBreaks) = " + nBreaks);

		/* If the number of breaks is greater than the range, truncate nElenemts to range.
		 * If the number of breaks is less than 1, automatically create number of breaks from the range.
		 */

		if (min == max)
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
		out[0] = min;
		for (int i = 1; i < nElements - 1; i++)
		{
			sum += interval;
			out[i] = min + (int) sum;
		}
		out[nElements - 1] = max;

		//		for (int i = 0; i < out.length; i++) System.out.println("ArrayImageFactory.spacedIntervals() out[" + i + "] = " + out[i]);
		return out;
	}

	private static class ImageDimensions
	{
		public int lengthX, lengthY;
		public int startX, startY;
		public int endX, endY;
		public int incrementX, incrementY;

		private void transpose()
		{
			int t1;

			t1 = lengthX; lengthX = lengthY; lengthY = t1;
			t1 = startX; startX = startY; startY = t1;
			t1 = endX; endX = endY; endY = t1;
			t1 = incrementX; incrementX = incrementY; incrementY = t1;
		}

		ImageDimensions(int d1, int d2,
				boolean flipAxisX, boolean flipAxisY,
				//				int direction1, int direction2,
				boolean transpose)
		{
			lengthX = d1; lengthY = d2;
			if (! flipAxisX)
				//				if (direction1 == 1)
			{
				startX = 0; endX = lengthX;
				incrementX = 1;
			}
			else 
			{
				startX = lengthX - 1; endX = -1;
				incrementX = -1;
			}

			if (! flipAxisY)
				//				if (direction2 == 1)
			{
				startY = 0; endY = lengthY;
				incrementY = 1;
			}
			else 
			{
				startY = lengthY - 1; endY = -1;
				incrementY = -1;
			}
			if (transpose) this.transpose();
		}

		ImageDimensions (int dataLength, boolean lowToHigh, boolean horizontal)
		{
			int n = dataLength;

			if (lowToHigh)
			{
				this.lengthX = n; this.lengthY = 1;
				this.startY = 0; this.endY = 0;
				this.incrementY = 0;
				if (horizontal)
				{
					this.startX = 0; this.endX = n;
					this.incrementX = 1;
				}
				else
				{
					this.startX = n - 1; this.endX =  -1;
					this.incrementX = -1;
				}
			}
			else
			{
				this.lengthX = 1; this.lengthY = n;
				this.startX = 0; this.endX = 0;
				this.incrementX = 0;
				if (horizontal)
				{
					this.startY = 0; this.endY = n;
					this.incrementY = 1;
				}
				else
				{
					this.startY = n - 1; this.endY = -1;
					this.incrementY = -1;
				}
			}
		}
	}

	/** A simple panel, filled with a resizing image. */
	public static class SimpleImagePanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		public SimpleImagePanel(Image img) { this.img = img; }
		Image img;

		@Override public void paintComponent(Graphics g)
		{
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
	}



	//	/**
	//	 * 
	//	 * @param data
	//	 * @param imager
	//	 * @param orientation1
	//	 * @param orientation2
	//	 * @param transpose
	//	 * @param boolNA
	//	 * @return
	//	 */
	//	@Deprecated
	//	public static <T> Image buildArrayImage(
	//			T[][] data, ObjectArrayImager<T> imager,
	//			int orientation1, int orientation2, boolean transpose, boolean boolNA)
	//	{
	//		ColorInterpolator ci;
	//		if (imager.getWatcher().getField().getType().getSimpleName().equals("boolean"))
	//			ci = imager.getBooleanInterpolator();
	//		else ci = imager.getInterpolator();
	//		return buildArrayImage(data, imager.getWatcher(), ci, orientation1, orientation2, transpose, boolNA);
	//	}

}
