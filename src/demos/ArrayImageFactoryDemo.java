package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import beans.sampleBeans.TerrainBean;
import image.ArrayImageFactory;
import image.ArrayImageFactory.ImagePanel;
import image.ColorInterpolator;
import image.ColorUtils;
import image.ObjectArrayImager;
import image.SimpleArrayImager;
import image.SimpleBooleanColorInterpolator;
import image.SimpleColorInterpolator;
import swing.SwingUtils;

public class ArrayImageFactoryDemo 
{
	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};
	static ImagePanel pp;
	static ColorInterpolator c;

	static Border border = BorderFactory.createLineBorder(Color.black, 4);
	static JFrame f;
	static int cellSize = 200;
	static List<ImagePanel> panels;
	public static void main(String[] args) 
	{
		int nBreaks = 30;

		intGradientImgDemo(nBreaks);
		doubleGradientImgDemo(nBreaks);
		booleanGradientImgDemo();
		objectArrayImageDemo();
	}

	public static void objectArrayImageDemo()
	{
		int nRows = 303;
		int nCols = 504;

		TerrainBean[][] cells = TerrainBean.factory(nRows, nCols, 1.57, 75);

		ObjectArrayImager<TerrainBean> imager1 = SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"elevation", 
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);		

		panels = new ArrayList<>();
		
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2;  j++)
		{
			TerrainBean.perturbElevations(cells, 300.0);
			TerrainBean.perturbAges(cells, 20);
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "elevation", i, j, false, true)));
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "elevation", i, j, true, true)));
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "age", i, j, false, true)));
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "age", i, j, true, true)));
		}

		
		f = SwingUtils.frameFactory(4 * cellSize, 4 * cellSize, "Terrain Bean Array Demo", 4, 4);

		for (ImagePanel pan : panels) { pan.setBorder(border); f.add(pan); }
		f.setVisible(true);
	}

	public static void intGradientImgDemo(int nBreaks)
	{
		int[] mins = new int[] {0, -1, -2, -1, 1, -50, -78};
		int[] maxs = new int[] {0, -2, -1, -3, 4, -60, 555};

		panels = new ArrayList<>();
		for(int dir = 1; dir < 3; dir++) for (int orient = 1; orient < 3; orient++)
			for (int i = 0; i < mins.length; i++)
			{
				c = SimpleColorInterpolator.factory(
						ColorUtils.TERRAIN_COLORS, mins[i], maxs[i],
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);
				pp = new ImagePanel(ArrayImageFactory.buildGradientImage(
						mins[i], maxs[i], nBreaks, c, dir, orient));
				pp.setBorder(border);
				panels.add(pp);

			}

		f = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Integer Image: Number of intervals: " + nBreaks, 4,  mins.length);
		for (ImagePanel p : panels) f.add(p);
		f.setVisible(true);
	}

	public static void doubleGradientImgDemo(int nBreaks)
	{
		double[] mins = new double[] {0, -1, -2, -50, -78};
		double[] maxs = new double[] {0, -2, -1, -60, 555};

		panels = new ArrayList<>();
		for(int dir = 1; dir < 3; dir++) for (int orient = 1; orient < 3; orient++)
			for (int i = 0; i < mins.length; i++)
			{
				c = SimpleColorInterpolator.factory(
						ColorUtils.HEAT_COLORS, mins[i], maxs[i],
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null);
				pp = new ImagePanel(ArrayImageFactory.buildGradientImage(
						mins[i], maxs[i], nBreaks, c, dir, orient));
				pp.setBorder(border);
				panels.add(pp);

			}

		f = SwingUtils.frameFactory(cellSize * mins.length, 4 * cellSize, 
				"Double gradient: Number of intervals: " + nBreaks,
				4,  mins.length);

		for (ImagePanel p : panels) f.add(p);
		f.setVisible(true);
	}

	public static void booleanGradientImgDemo()
	{
		border = BorderFactory.createLineBorder(Color.white, 4);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.BLUES, Color.gray); 
		panels = new ArrayList<>();

		for (int i = 1; i < 3; i++) for (int j = 1; j < 3; j++)
		{
			pp = new ImagePanel(ArrayImageFactory.buildGradientImage(true, c, i, j));
			pp.setBorder(border);
			panels.add(pp);
			pp = new ImagePanel(ArrayImageFactory.buildGradientImage(false, c, i, j));
			pp.setBorder(border);
			panels.add(pp);
		}

		f = SwingUtils.frameFactory(4 * cellSize, 2 * cellSize, "Boolean Gradient Demo", 2, 4);
		for (ImagePanel p : panels) f.add(p);

		f.setVisible(true);
	}
}
