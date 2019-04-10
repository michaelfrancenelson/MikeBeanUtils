package image;

import java.awt.Image;
import java.awt.image.BufferedImage;

import beans.memberState.FieldWatcher;


public class ArrayImageFactory 
{

	public static final int RGB_TYPE = BufferedImage.TYPE_3BYTE_BGR;


	
	
	
	public static <T> Image buildArrayImage(
			T[][] data, FieldWatcher<T> w, ColorInterpolator ci, 
			int orientation1, int orientation2, boolean transpose, boolean boolNA)
	{
		int imgType = RGB_TYPE;
		ImageDimensions dim = new ImageDimensions(data.length, data[0].length, orientation1, orientation2, transpose);
		BufferedImage img = new BufferedImage(dim.dim1, dim.dim2, imgType);
		
		String fieldType = w.getField().getType().getSimpleName();
		
		int datIndex1 = 0, datIndex2 = 0;
		
		switch(fieldType)
		{
		case("int"):
		{
			if (!transpose)
			{
				for (int i = dim.start1; i != dim.end1; i += dim.increment1)
				{
					for (int j = dim.start2; j != dim.end2; j += dim.increment2)
					{
						int val = w.getIntVal(data[datIndex1][datIndex2]);
						img.setRGB(i, j, ci.getColor(val));
						datIndex2++;
					}
					datIndex1++;
					datIndex2 = 0;
				}
			}
			else
			{
				for (int i = dim.start1; i != dim.end1; i += dim.increment1)
				{
					for (int j = dim.start2; j != dim.end2; j += dim.increment2)
					{
						img.setRGB(i, j, ci.getColor(w.getIntVal(data[datIndex1][datIndex2])));
						datIndex1++; 
					}
					datIndex2++;
					datIndex1 = 0;
				}
			}
			break;
		}
		case("double"):
			if (!transpose)
			{
				for (int i = dim.start1; i != dim.end1; i += dim.increment1)
				{
					for (int j = dim.start2; j != dim.end2; j += dim.increment2)
					{
						T t = data[datIndex1][datIndex2];
						double val = w.getDoubleVal(t);
						img.setRGB(i, j, ci.getColor(val));
						datIndex2++;
					}
					datIndex1++;
					datIndex2 = 0;
				}
			}
			else
			{
				for (int i = dim.start1; i != dim.end1; i += dim.increment1)
				{
					for (int j = dim.start2; j != dim.end2; j += dim.increment2)
					{
						img.setRGB(i, j, ci.getColor(w.getDoubleVal(data[datIndex1][datIndex2])));
						datIndex1++; 
					}
					datIndex2++;
					datIndex1 = 0;
				}
			}
			break;
		case("boolean"):
		{
			
			break;
		}
		
		}
		
		return img;
	}
	
	
	public static Image buildGradientImage(double min, double max, int nSteps, ColorInterpolator ci, int direction, int orientation)
	{
		double[] data = spacedIntervals(min, max, nSteps);
		
		int imgType = RGB_TYPE;
		ImageDimensions dir = new ImageDimensions(data.length, direction, orientation);
		BufferedImage img = new BufferedImage(dir.dim1, dir.dim2, imgType);
		int datIndex = 0;
		if (dir.increment2 == 0)
			for (int i = dir.start1; i != dir.end1; i += dir.increment1)
			{
				img.setRGB(i, 0, ci.getColor(data[datIndex]));
				datIndex++;
			}
		else
			for (int i = dir.start2; i != dir.end2; i += dir.increment2)
			{
				img.setRGB(0, i, ci.getColor(data[datIndex]));
				datIndex++;
			}

		return img;
	}
	
	
	public static Image buildGradientImage(int min, int max, int nSteps, ColorInterpolator ci, int direction, int orientation)
	{
		int[] data = spacedIntervals(min, max, nSteps);
		
		int imgType = RGB_TYPE;
		ImageDimensions dir = new ImageDimensions(data.length, direction, orientation);
		BufferedImage img = new BufferedImage(dir.dim1, dir.dim2, imgType);
		int datIndex = 0;
		if (dir.increment2 == 0)
			for (int i = dir.start1; i != dir.end1; i += dir.increment1)
			{
				img.setRGB(i, 0, ci.getColor(data[datIndex]));
				datIndex++;
			}
		else
			for (int i = dir.start2; i != dir.end2; i += dir.increment2)
			{
				img.setRGB(0, i, ci.getColor(data[datIndex]));
				datIndex++;
			}

		return img;
	}
	
	public static Image buildGradientImage(boolean includeNABoolean, ColorInterpolator ci, int direction, int orientation)
	{
		int imgType = RGB_TYPE;

		int n = 2;
		if (includeNABoolean) n = 3;
		ImageDimensions dir = new ImageDimensions(n, direction, orientation);
		boolean[] data = new boolean[] {true, false, false};
		BufferedImage img = new BufferedImage(dir.dim1, dir.dim2, imgType);

		int datIndex = 0;
		if (dir.increment2 == 0)
			for (int i = dir.start1; i != dir.end1; i += dir.increment1)
			{
				if (datIndex == 2)	img.setRGB(i, 0, ci.getNAColor());
				else img.setRGB(i, 0, ci.getColor(data[datIndex]));
				datIndex++;
			}
		else
			for (int i = dir.start2; i != dir.end2; i += dir.increment2)
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

	public static int[] spacedIntervals(int min, int max, int nBreaks)
	{
	
		double interval = ((double) (max - min)) / ((double) (nBreaks - 1));
		if (interval <= 1 )
			nBreaks = Math.abs(max - min) + 1;
		
		int[] out = new int[nBreaks];
		
		/* In case of non-integer intervals, this allows rounding to maintain the right number of breaks. */
		double sum = 0.0;
		out[0] = min;
		for (int i = 1; i < nBreaks - 1; i++)
		{
			sum += interval;
			out[i] = min + (int) sum;
		}
		out[nBreaks - 1] = max;
		return out;
	}


	
	private static class ImageDimensions
	{
		public int dim1, dim2;
		public int start1, start2;
		public int end1, end2;
		public int increment1, increment2;

		private void transpose()
		{
			int t1;
			
			t1 = dim1; dim1 = dim2; dim2 = t1;
			t1 = start1; start1 = start2; start2 = t1;
			t1 = end1; end1 = end2; end2 = t1;
			t1 = increment1; increment1 = increment2; increment2 = t1;
		}
		
		
		ImageDimensions(int d1, int d2, int direction1, int direction2, boolean transpose)
		{
			dim1 = d1; dim2 = d2;
			if (direction1 == 1)
			{
				start1 = 0; end1 = dim1;
				increment1 = 1;
			}
			else 
			{
				start1 = dim1 - 1; end1 = -1;
				increment1 = -1;
			}
			
			if (direction2 == 1)
			{
				start2 = 0; end2 = dim2;
				increment2 = 1;
			}
			else 
			{
				start2 = dim2 - 1; end2 = -1;
				increment2 = -1;
			}
			if (transpose) this.transpose();
		}
		
		
		/**
		 * Direction codes:
		 * <li> 1: low index = low value
		 * <li> 2: low index = high value
		 * 
		 * Orientation codes
		 * <li> 1: gradient along first index.
		 * <li> 2: gradient along second index.
		 * 
		 */
		ImageDimensions (int dataLength, int direction, int orientation)
		{
			int n = dataLength;

			if (orientation == 1)
			{
				this.dim1 = n; this.dim2 = 1;
				this.start2 = 0; this.end2 = 0;
				this.increment2 = 0;
				if (direction == 1)
				{
					this.start1 = 0; this.end1 = n;
					this.increment1 = 1;
				}
				else
				{
					this.start1 = n - 1; this.end1 =  -1;
					this.increment1 = -1;
				}
			}
			else if (orientation == 2)
			{
				this.dim1 = 1; this.dim2 = n;
				this.start1 = 0; this.end1 = 0;
				this.increment1 = 0;
				if (direction == 1)
				{
					this.start2 = 0; this.end2 = n;
					this.increment2 = 1;
				}
				else
				{
					this.start2 = n - 1; this.end2 = -1;
					this.increment2 = -1;
				}
			}
		}

	}
}
