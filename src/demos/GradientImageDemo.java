package demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import image.colorInterpolator.ColorInterpolator;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;
import image.imageFactories.GradientImageFactory;
import image.imageFactories.PrimitiveImageFactory.SimpleImagePanel;
import swing.SwingUtils;
import utils.ColorUtils;


/**
 * Demonstration cases for the gradient images.
 * @author michaelfrancenelson
 *
 */
public class GradientImageDemo
{
	static Color[] bCol = new Color[] {Color.gray, Color.green};
	static SimpleImagePanel pp;
	static ColorInterpolator c;

	static Border border = BorderFactory.createLineBorder(Color.black, 4);
	static JFrame f;
	static int cellSize = 230;
	static List<SimpleImagePanel> panels;

	static boolean horizontal, lowToHigh;

	public static void main(String[] args) 
	{
		int nBreaks = 30;
		directionOrientationDemo(nBreaks, 10, 10);
		intGradientImgDemo(nBreaks, 10 + 4 * cellSize, 10);
		doubleGradientImgDemo(nBreaks, 10 + 4 * cellSize, 10 + 4 * cellSize);
		booleanGradientImgDemo(10 + 9 * cellSize, 10 + 4 * cellSize);
	}

	/** Show the vertical/horizontal and increasing/decreasing arrangements. */
	public static void directionOrientationDemo(int nBreaks, int x, int y)
	{
		int min = -3, max = 2;
		c = SimpleColorInterpolator.factory(
				ColorUtils.BLUES, min, max,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);

		horizontal = true;lowToHigh = true;
		pp = new SimpleImagePanel(GradientImageFactory.buildGradientImage(min, max, nBreaks, c, horizontal, lowToHigh));
		f = SwingUtils.frameFactory(4 * cellSize , 2 * cellSize, "Horizontal, low to high", 1, 1);
		f.add(pp); f.setVisible(true); f.setLocation(x, y);

		horizontal = true; lowToHigh = false;
		pp = new SimpleImagePanel(GradientImageFactory.buildGradientImage(min, max, nBreaks, c, horizontal, lowToHigh));
		f = SwingUtils.frameFactory(4 * cellSize , 2 * cellSize, "Horizontal, high to low", 1, 1);
		f.add(pp); f.setVisible(true); f.setLocation(x, y + 2 * cellSize);

		horizontal = false;	lowToHigh = true;
		pp = new SimpleImagePanel(GradientImageFactory.buildGradientImage(min, max, nBreaks, c, horizontal, lowToHigh));
		f = SwingUtils.frameFactory(4 * cellSize , 2 * cellSize, "Vertical, low to high", 1, 1);
		f.add(pp); f.setVisible(true); f.setLocation(x, y + 4 * cellSize);

		horizontal = false;	lowToHigh = false;
		pp = new SimpleImagePanel(GradientImageFactory.buildGradientImage(min, max, nBreaks, c, horizontal, lowToHigh));
		f = SwingUtils.frameFactory(4 * cellSize , 2 * cellSize,"Vertical, high to low", 1, 1);
		f.add(pp); f.setVisible(true); f.setLocation(x , y + 6 * cellSize);
	}

	public static void intGradientImgDemo(int nBreaks, int x, int y)
	{
		int[] mins = new int[] {0, -1, -2, -1, 1, -50, -78};
		int[] maxs = new int[] {0, -2, -1, -3, 4, -60, 555};
		boolean[] test = new boolean[] {true, false};

		panels = new ArrayList<>();
			for(int dir = 0; dir < 2; dir++) for (int orient = 0; orient < 2; orient++)
			for (int i = 0; i < mins.length; i++)
			{
				c = SimpleColorInterpolator.factory(
						ColorUtils.TERRAIN_COLORS, mins[i], maxs[i],
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);
				pp = new SimpleImagePanel(GradientImageFactory.buildGradientImage(
						mins[i], maxs[i], nBreaks, c, test[dir], test[orient]));
				pp.setBorder(border);
				panels.add(pp);

			}

		f = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Integer Image: Number of intervals: " + nBreaks, 4,  mins.length);
		for (SimpleImagePanel p : panels) f.add(p);
		f.setVisible(true); f.setLocation(x, y);
	}

	public static void doubleGradientImgDemo(int nBreaks, int x, int y)
	{
		double[] mins = new double[] {0, -1, -2, -50, -78};
		double[] maxs = new double[] {0, -2, -1, -60, 555};

		panels = new ArrayList<>();
		boolean[] test = new boolean[] {true, false};
		for(int dir = 0; dir < 2; dir++) for (int orient = 0; orient < 2; orient++)
			for (int i = 0; i < mins.length; i++)
			{
				c = SimpleColorInterpolator.factory(
						ColorUtils.HEAT_COLORS, mins[i], maxs[i],
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);
				pp = new SimpleImagePanel(GradientImageFactory.buildGradientImage(
						mins[i], maxs[i], nBreaks, c, test[dir], test[orient]));
				pp.setBorder(border);
				panels.add(pp);

			}

		f = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Double gradient: Number of intervals: " + nBreaks,
				4,  mins.length);

		for (SimpleImagePanel p : panels) f.add(p);
		f.setVisible(true); f.setLocation(x, y);
	}

	public static void booleanGradientImgDemo(int x, int y)
	{
		border = BorderFactory.createLineBorder(Color.white, 4);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.BLUES, Color.gray); 
		panels = new ArrayList<>();


		boolean[] test = new boolean[] {true, false};

		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++)
		{
			pp = new SimpleImagePanel(GradientImageFactory.buildBooleanGradient(c, test[i], test[j]));
			pp.setBorder(border);
			panels.add(pp);
			pp = new SimpleImagePanel(GradientImageFactory.buildBooleanGradient(c, test[i], test[j]));
			pp.setBorder(border);
			panels.add(pp);
		}

		f = SwingUtils.frameFactory(4 * cellSize, 2 * cellSize, "Boolean Gradient Demo", 2, 4);
		for (SimpleImagePanel p : panels) f.add(p);

		f.setVisible(true); f.setLocation(x, y);
	}
}