package demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import imaging.colorInterpolator.SimpleColorInterpolator;
import imaging.imageFactories.GradientImageFactory;
import imaging.imageFactories.ImageFactory.SimpleImagePanel;
import imaging.imagers.imagerData.PrimitiveImagerData;
import swing.stretchAndClick.PanelFactory;
import swing.stretchAndClick.PrimitiveImagePanel;
import utils.ColorUtils;
import utils.SwingUtils;

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
		intGradientClickableDemo(nBreaks, 10 + 4 * cellSize, 10);
//				intGradientImgDemo(nBreaks, 10 + 4 * cellSize, 10);
//				directionOrientationDemo(nBreaks, 10, 10);
//				doubleGradientImgDemo(nBreaks, 10 + 4 * cellSize, 10 + 4 * cellSize);
//				booleanGradientImgDemo(10 + 9 * cellSize, 10 + 4 * cellSize);
	}

	/** Show the vertical/horizontal and increasing/decreasing arrangements. */
	public static void directionOrientationDemo(int nBreaks, int x, int y)
	{
		int min = -3, max = 2;
		c = SimpleColorInterpolator.factory(
				ColorUtils.BLUES, min, max,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);

		f1 = SwingUtils.frameFactory(4 * cellSize , 2 * cellSize, "Direction/orientation demo", 2, 2);
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++)
		{
			horiz = trueFalse[i]; 
			loToHi = trueFalse[j];
			simplePan = new SimpleImagePanel(GradientImageFactory.buildGradientImage(min, max, nBreaks, c, loToHi, horiz));
			f1.add(simplePan);
		}
		f1.setVisible(true);
	}

	public static void intGradientClickableDemo(int nBreaks, int x, int y)
	{
		int[] mins = new int[] {0, -1, -2, -1, 1, -50, -78};
		int[] maxs = new int[] {0, -2, -1, -3, 4, -60, 555};

		simplePanels = new ArrayList<>();
		List<JPanel> panList = new ArrayList<>();
		for(int horiz = 0; horiz < 2; horiz++)
			for (int loHi = 0; loHi < 2; loHi++)
				for (int i = 0; i < mins.length; i++)
				{
					c = SimpleColorInterpolator.factory(
							ColorUtils.TERRAIN_COLORS, mins[i], maxs[i],
							Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);

					PrimitiveImagerData<Object> datArr = 
							PrimitiveImagerData.buildGradientData(
									"int", mins[i], maxs[i], nBreaks,
									trueFalse[horiz], trueFalse[loHi], true); 

					PrimitiveImagePanel<Object> pan = 
							PanelFactory.primitivePanel(
									datArr, 
									c, c, 
									"int", "%.0d",
									null, false, -1, -1, 0.01, false);
					pan.setBorder(border);
					panList.add(pan);

//					c = SimpleColorInterpolator.factory(
//							ColorUtils.TERRAIN_COLORS, mins[i], maxs[i],
//							Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);

					simplePan = new SimpleImagePanel(
							GradientImageFactory.buildGradientImage(
									mins[i], maxs[i], nBreaks, 
									c, trueFalse[horiz], trueFalse[loHi]));
					simplePan.setBorder(border);
					simplePanels.add(simplePan);


				}

		f1 = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Clickable Integer Image: Number of intervals: " + nBreaks, 4,  mins.length);
		for (JPanel p : panList) f1.add(p);
		f1.setVisible(true); f1.setLocation(x, y);
		f2 = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Integer Image: Number of intervals: " + nBreaks, 4,  mins.length);
		for (SimpleImagePanel p : simplePanels) f2.add(p);
		f2.setVisible(true); f2.setLocation(0, f1.getHeight());
	}

	public static void intGradientImgDemo(int nBreaks, int x, int y)
	{
		int[] mins = new int[] {0, -1, -2, -1, 1, -50, -78};
		int[] maxs = new int[] {0, -2, -1, -3, 4, -60, 555};

		simplePanels = new ArrayList<>();
		for(int horiz = 0; horiz < 2; horiz++) 
			for (int loHi = 0; loHi < 2; loHi++)
				for (int i = 0; i < mins.length; i++)
				{
					c = SimpleColorInterpolator.factory(
							ColorUtils.TERRAIN_COLORS, mins[i], maxs[i],
							Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);



					simplePan = new SimpleImagePanel(
							GradientImageFactory.buildGradientImage(
									mins[i], maxs[i], nBreaks, c, trueFalse[horiz], trueFalse[loHi]));
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