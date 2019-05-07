package demos;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.BorderFactory;

import imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import imaging.colorInterpolator.SimpleColorInterpolator;
import imaging.imageFactories.GradientImageFactory;
import imaging.imageFactories.ImageFactory.SimpleImagePanel;
import swing.SwingUtils;
import utils.ColorUtils;

/**
 * Demonstration cases for the gradient images.
 * @author michaelfrancenelson
 *
 */
public class GradientImageDemo extends DemoConsts
{
	public static void main(String[] args) 
	{ 
		int nBreaks = 30;
		cellSize = 230;
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

		f1 = SwingUtils.frameFactory(4 * cellSize , 2 * cellSize, "Horizontal, low to high", 2, 2);
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++)
		{
			horiz = trueFalse[i]; loToHi = trueFalse[j];
			simplePan = new SimpleImagePanel(GradientImageFactory.buildGradientImage(min, max, nBreaks, c, loToHi, horiz));
			f1.add(simplePan);
		}
		f1.setVisible(true);
	}

	public static void intGradientImgDemo(int nBreaks, int x, int y)
	{
		int[] mins = new int[] {0, -1, -2, -1, 1, -50, -78};
		int[] maxs = new int[] {0, -2, -1, -3, 4, -60, 555};
		boolean[] test = new boolean[] {true, false};
		
		simplePanels = new ArrayList<>();
			for(int dir = 0; dir < 2; dir++) for (int orient = 0; orient < 2; orient++)
			for (int i = 0; i < mins.length; i++)
			{
				c = SimpleColorInterpolator.factory(
						ColorUtils.TERRAIN_COLORS, mins[i], maxs[i],
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);
				
				
				
				simplePan = new SimpleImagePanel(GradientImageFactory.buildGradientImage(
						mins[i], maxs[i], nBreaks, c, test[dir], test[orient]));
				simplePan.setBorder(border);
				simplePanels.add(simplePan);

			}

		f1 = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Integer Image: Number of intervals: " + nBreaks, 4,  mins.length);
		for (SimpleImagePanel p : simplePanels) f1.add(p);
		f1.setVisible(true); f1.setLocation(x, y);
	}

	public static void doubleGradientImgDemo(int nBreaks, int x, int y)
	{
		double[] mins = new double[] {0, -1, -2, -50, -78};
		double[] maxs = new double[] {0, -2, -1, -60, 555};

		simplePanels = new ArrayList<>();
		for(int dir = 0; dir < 2; dir++) 
			for (int orient = 0; orient < 2; orient++)
			for (int i = 0; i < mins.length; i++)
			{
				c = SimpleColorInterpolator.factory(
						ColorUtils.HEAT_COLORS, mins[i], maxs[i],
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);
				simplePan = new SimpleImagePanel(GradientImageFactory.buildGradientImage(
						mins[i], maxs[i], nBreaks, c, trueFalse[orient], trueFalse[dir]));
				simplePan.setBorder(border);
				simplePanels.add(simplePan);

			}

		f1 = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Double gradient: Number of intervals: " + nBreaks,
				4,  mins.length);

		for (SimpleImagePanel p : simplePanels) f1.add(p);
		f1.setVisible(true); f1.setLocation(x, y);
	}

	public static void booleanGradientImgDemo(int x, int y)
	{
		border = BorderFactory.createLineBorder(Color.white, 4);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.BLUES, Color.gray); 
		simplePanels = new ArrayList<>();


		boolean[] test = new boolean[] {true, false};

		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++)
		{
			simplePan = new SimpleImagePanel(GradientImageFactory.buildBooleanGradient(c, test[i], test[j]));
			simplePan.setBorder(border);
			simplePanels.add(simplePan);
			simplePan = new SimpleImagePanel(GradientImageFactory.buildBooleanGradient(c, test[i], test[j]));
			simplePan.setBorder(border);
			simplePanels.add(simplePan);
		}

		f1 = SwingUtils.frameFactory(4 * cellSize, 2 * cellSize, "Boolean Gradient Demo", 2, 4);
		for (SimpleImagePanel p : simplePanels) f1.add(p);

		f1.setVisible(true); f1.setLocation(x, y);
	}
}