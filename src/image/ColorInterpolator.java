package image;

import java.awt.Color;

import search.Binary;
import search.Binary.IndicesAndRelativePosition;
import sequences.Sequences;

/**
 * 
 * @author michaelfrancenelson
 *
 */
public interface ColorInterpolator 
{
	/**
	 * 
	 * @param val
	 * @return
	 */
	public int getColor(int... val);
	
	/**
	 * 
	 * @param val
	 * @return
	 */
	public int getColor(double... val);

	public void updateMinMax(double min, double max);
	public void updateMinMax(int min, int max);
	
	/**
	 * 
	 * @author michaelfrancenelson
	 *
	 */
	public static class ColorInterpolatorSingleField implements ColorInterpolator
	{
		private Color[] colors;

		private Color naColor = Color.GRAY;
		private int naInt = Integer.MIN_VALUE;
		private double naDouble = Double.MIN_VALUE;

		private double[] breaks;

		public int getColor(int val) 
		{
			if (val == naInt) return naColor.getRGB();
			return getColor((double) val); 
		}

		public int getColor(double val)
		{
			if (val == naDouble) return naColor.getRGB();

			IndicesAndRelativePosition pos = Binary.interpolateRelativePosition(breaks, val);
			int out = interpolateColor(colors[pos.lowIndex], colors[pos.highIndex], pos.relativePosition);
			return out;
		}

		private int interpolateColor(Color c1, Color c2, double proportion)
		{
			double prop1 = 1 - proportion;
			int red = (int) (c1.getRed() * prop1 + c2.getRed() * proportion);
			int green = (int) (c1.getGreen() * prop1 + c2.getGreen() * proportion);
			int blue = (int) (c1.getBlue() * prop1 + c2.getBlue() * proportion);
			int col = (red << 16) | (green << 8) | blue;
			return col;
		}
		
		public static ColorInterpolator factory(
				Color[] colors, double min, double max, double naDouble, Color naColor)
		{
			ColorInterpolatorSingleField ci = new ColorInterpolatorSingleField();
			if (min > max) throw new IllegalArgumentException("max must be greater than or equal to min.");
			ci.colors = colors; ci.naDouble = naDouble; ci.naColor = naColor;
			ci.breaks = Sequences.spacedIntervals((double) min, (double) max, colors.length - 1); 
			return ci;
		}

		public static ColorInterpolator factory(
				Color[] colors, int min, int max, int naInt, Color naColor)
		{
			ColorInterpolatorSingleField ci = new ColorInterpolatorSingleField();
			if (min > max) throw new IllegalArgumentException("max must be greater than or equal to min.");
			ci.colors = colors; ci.naInt = naInt; ci.naColor = naColor;
			double[] breaks = 
					Sequences.spacedIntervals((double) min, (double) max, colors.length - 1);
			ci.breaks = breaks;
			return ci;
		}

		@Override
		/**
		 * 
		 * @param val only the first is used
		 * @return
		 */
		public int getColor(int... val) {
			return getColor(val[0]);
		}

		@Override
		/**
		 * 
		 * @param val only the first is used
		 * @return
		 */
		public int getColor(double... val) {
			return getColor(val[0]);
		}

		@Override
		public void updateMinMax(double min, double max) 
		{
			breaks = Sequences.spacedIntervals(min, max, colors.length - 1); 
		}

		@Override
		public void updateMinMax(int min, int max) {
			breaks = Sequences.spacedIntervals(
					(double) min, (double) max, colors.length - 1); 
		}
	}
}
