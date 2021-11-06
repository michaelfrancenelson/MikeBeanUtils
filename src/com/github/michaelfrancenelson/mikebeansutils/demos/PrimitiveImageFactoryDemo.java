package com.github.michaelfrancenelson.mikebeansutils.demos;

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.github.michaelfrancenelson.mikebeansutils.imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import com.github.michaelfrancenelson.mikebeansutils.imaging.colorInterpolator.SimpleColorInterpolator;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imageFactories.ImageFactory;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imageFactories.ImageFactory.SimpleImagePanel;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.imagerData.PrimitiveImagerData;
import com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick.PanelFactory;
import com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick.PrimitiveImagePanel;
import com.github.michaelfrancenelson.mikebeansutils.utils.ColorUtils;
import com.github.michaelfrancenelson.mikebeansutils.utils.Sequences;
import com.github.michaelfrancenelson.mikebeansutils.utils.SwingUtils;

public class PrimitiveImageFactoryDemo extends DemoConsts 
{
	static double[][] datDbl;
	static int[][] datInt;
	static boolean[][] datBool;
	static byte[][] datByte;
	static Random r = new Random();

	static void setup()
	{
	}

	public static void main(String[] args) 
	{
		boolean show = true, save = false;
//		doubleDemo(50, 800, 600, 10, 200.1, 1.5, show, save);
		intDemo(200, 300, 10, 20, 4.3, show, save);
//		booleanDemo(200, 300, 3.5, show, save);
//		byteDemo(200, 300, 4, show, save);
	}

	static void byteDemo(int width, int height, double size, boolean show, boolean saveFile)
	{
		setup();
		List<PrimitiveImagerData<Object>> data2 = new ArrayList<>();
		List<PrimitiveImagerData<Object>> data1 = new ArrayList<>();
		byte[][] bytes1 = new byte[width][height];
		int maxb = (int) Byte.MAX_VALUE;
		byte maxB = 0;

		for (int ii = 0; ii < 2; ii++) for (int jj = 0; jj < 2; jj++)
		{
			maxB = (byte) (maxb - 50 - r.nextInt(60)); 
			bytes1 = new byte[width][height];
			for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
			{	
				byte val = (byte) (r.nextInt(1 + (i + j) / 2) % maxB);
				bytes1[i][j] = val;
			}
			data2.add(new PrimitiveImagerData<Object>(bytes1, trueFalse[jj], trueFalse[ii], true));
			data1.add(new PrimitiveImagerData<Object>(bytes1, trueFalse[jj], trueFalse[ii], false));
		}
		c = SimpleColorInterpolator.factory(ColorUtils.RAINBOW, 0, maxb, Color.gray, "%0.4f");
		f1 = SwingUtils.frameFactory((int)(width * size), (int)(height * size), "Byte array demo", 2, 2);
		f2 = SwingUtils.frameFactory((int)(height * size), (int)(width * size), " Transposed byte array demo", 2, 2);
		int z = 0;
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++)
		{
			f1.add(PanelFactory.primitivePanel(
					data1.get(z), 
					c, c, 
					"byte", 
					dblFmt, 
					null,
					false, 0, 0, 
					ptSize,
					false));
			//			f2.add(ImagePanelFactory.buildPanel(
			//					data2.get(z), c, "byte", null, false, 0, 0, ptSize, true));

			//			f1.add(new SimpleImagePanel(ImageFactory.buildPrimitiveImage(
			//					data1.get(z), c)));
			f2.add(new SimpleImagePanel(ImageFactory.buildPrimitiveImage(
					data1.get(z), c )));
			z++;
		}

		f1.setVisible(show);
		f2.setVisible(show);
		f2.setLocation(f1.getX() + f1.getWidth(), f1.getY());
		if (saveFile)
		{
			File imgFile = new File("sampleOutput/" + "primitive_demo_byte.png");
			try {
				ImageIO.write((RenderedImage) ImageFactory.buildPrimitiveImage(datByte, c, false, false, false), "png", imgFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void booleanDemo(int width, int height, double size, boolean show, boolean saveFile)
	{
		datBool = new boolean[width][height];
		datByte = new byte[width][height];
		Border b = BorderFactory.createLineBorder(Color.red);
		int cutoff0 = (height + width) / 2;
		int cutoff1 = (height + width) / 3;
		int cutoff2 = 2 * (height + width) / 3;

		for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
		{
			if (i + j < cutoff0) datBool[i][j] = true;
			if (i + j < cutoff1) datByte[i][j] = 0;
			else if (i + j < cutoff2) datByte[i][j] = 1;
			else datByte[i][j] = Byte.MIN_VALUE;
		}

		c = SimpleBooleanColorInterpolator.factory(ColorUtils.BLUES, Color.gray);
		f1 = SwingUtils.frameFactory((int)(2 * width * size), (int)(height * size), "Boolean array demos", 1, 2);

		JPanel pp = new SimpleImagePanel(ImageFactory.buildPrimitiveImage(datBool, c, false, false, false));
		pp.setBorder(b);
		f1.add(pp);
		pp = PanelFactory.primitivePanel(new PrimitiveImagerData<Object>(
				datByte, false, false, false), c, c, "bool", null, null, true, 0, 0, ptSize, true);
		pp.setBorder(b);
		f1.add(pp);
		f1.setVisible(show);
		if (saveFile)
		{
			File imgFile = new File("sampleOutput/" + "primitive_demo_boolean.png");
			File imgFile2 = new File("sampleOutput/" + "primitive_demo_boolean_from_byte.png");
			try {
				ImageIO.write((RenderedImage) ImageFactory.buildPrimitiveImage(datBool, c, false, false, false), "png", imgFile);
				ImageIO.write((RenderedImage) ImageFactory.buildPrimitiveImage(datByte, c, false, false, false), "png", imgFile2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void intDemo(int nCol, int nRow, 
			int min, int max, double size,
			boolean show, boolean saveFile)
	{
		setup();
		int nPts = max - min;
		int[] vals = Sequences.spacedIntervals(min, max, nPts);

		int[] x = new int[nPts], y = new int[nPts];
		int[] x2 = new int[nPts], y2 = new int[nPts];
		double[] dists = new double[nPts];

		for (int i = 0; i < nPts; i++) { 
			x2[i] = r.nextInt(nCol + 1); 
			y2[i] = r.nextInt(nRow + 1); }

		List<PrimitiveImagerData<Object>> arrDat1 = new ArrayList<>();
		List<PrimitiveImagerData<Object>> arrDat2 = new ArrayList<>();

		for (int tr = 0; tr < 2; tr++) for (int fa = 0; fa < 2; fa++)
		{
			int[][] dat1 = new int[nCol][nRow];
			for (int ii = 0; ii < nPts; ii++) 
			{
				x[ii] = r.nextInt(nCol + 1); y[ii] = r.nextInt(nRow + 1); 
			}
			for (int i = 0; i < nCol; i++) for (int j = 0; j < nRow; j++)
			{
				dists = dist(i, j, x, y); 
				dat1[i][j] = nearest(dists, vals); 
			}
			arrDat1.add(new PrimitiveImagerData<Object>(dat1, trueFalse[tr], trueFalse[fa], false));
			arrDat2.add(new PrimitiveImagerData<Object>(dat1, trueFalse[tr], trueFalse[fa], true));
		}

		c = SimpleColorInterpolator.factory(ColorUtils.TOPO_COLORS, min, max,
//				Double.MIN_VALUE, Integer.MIN_VALUE,
				Color.gray, "%0.4f");

		f1 = SwingUtils.frameFactory((int)(nCol * size), (int) (nRow * size), "Int value array demo", 2, 2);
		f2 = SwingUtils.frameFactory((int) (nRow * size), (int)(nCol * size), "Transposed int value array demo", 2, 2);

		int z = 0;
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2; j++)
		{
			PrimitiveImagePanel<Object> pan = 
					PanelFactory.primitivePanel(
							arrDat1.get(z),	c, c, "int", "%.0d", null, true, -1, -1, 0.01, false);
			f1.add(pan);

			pan = PanelFactory.primitivePanel(arrDat2.get(z), c, c, "int", null, null, true, -1, -1, 0.01, false);
			f2.add(pan);


			//
			//			f1.add(new SimpleImagePanel(ImageFactory.buildPrimitiveImage(
			//					ints[z], c, trueFalse[j], trueFalse[i], false)));
			//			f2.add(new SimpleImagePanel(ImageFactory.buildPrimitiveImage(
			//					ints[z], c, trueFalse[j], trueFalse[i], true)));
			z++;
		}

		f1.setLocation((int)(nCol * size), 0);
		f1.setVisible(show);
		f2.setVisible(show);

		if (saveFile)
		{
			File imgFile = new File("sampleOutput/" + "primitive_demo_int.png");
			try { ImageIO.write((RenderedImage) 
					ImageFactory.buildPrimitiveImage(datInt, c, false, false, false), "png", imgFile);
			} catch (IOException e) {e.printStackTrace();}
		}
	}

	static void doubleDemo(int nPts, int width, int height, 
			double min, double max, double size, boolean show, boolean saveFile)
	{
		setup();
		File imgFile = new File("sampleOutput/" + "primitive_demo_double.png");
		double minVal = Double.MAX_VALUE, maxVal = Double.MIN_VALUE;
		double range = max - min, dd;

		int[] x = new int[nPts], y = new int[nPts];
		double[] dists = new double[nPts], vals = new double[nPts];
		datDbl = new double[width][height];

		for (int i = 0; i < nPts; i++)
		{	x[i] = r.nextInt(width + 1); y[i] = r.nextInt(height + 1);
		vals[i] = range * r.nextDouble() + min;
		}

		for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
		{	dists = dist(i, j, x, y);
		dd = min1(prods(dists, vals));
		datDbl[i][j] = dd;
		if (dd < minVal) minVal = dd; if (dd > maxVal) maxVal = dd;
		}
		c = SimpleColorInterpolator.factory(
				ColorUtils.TOPO_COLORS,	minVal, maxVal,
//				Double.MIN_VALUE, Integer.MIN_VALUE,
				Color.gray, "%0.4f");

		f1 = SwingUtils.frameFactory((int)(width * size), (int) (height * size), "Double value array demo");
		f1.add(new SimpleImagePanel(ImageFactory.buildPrimitiveImage(datDbl, c, false, false, false)));
		f1.setVisible(show);

		if (saveFile)
		{
			try { ImageIO.write((RenderedImage) ImageFactory.buildPrimitiveImage(datDbl, c, false, false, false), "png", imgFile);
			} catch (IOException e) { e.printStackTrace(); }
		}
	}

	static double weightedNearest(double[] dists, double[] vals)
	{
		double minDist = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < dists.length; i++)
		{
			double d = dists[i] * vals[i];
			if (d < minDist)
			{
				minDist = d;
				index = i;
			}
		}
		return vals[index];
	}

	static int logWeightedNearest(double[] dists, int[] vals)
	{
		double minDist = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < dists.length; i++)
		{
			double d = Math.log(dists[i] * vals[i]);
			if (d < minDist)
			{
				minDist = d;
				index = i;
			}
		}
		return vals[index];
	}

	static double logWeightedNearest(double[] dists, double[] vals)
	{
		double minDist = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < dists.length; i++)
		{
			double d = Math.log(dists[i] * vals[i]);
			if (d < minDist)
			{
				minDist = d;
				index = i;
			}
		}
		return vals[index];
	}

	static double nearest(double[] dists, double[] vals)
	{
		double minDist = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < dists.length; i++)
		{
			double d = dists[i];
			if (d < minDist)
			{
				minDist = d;
				index = i;
			}
		}
		return vals[index];
	}

	static int nearest(double[] dists, int[] vals)
	{
		double minDist = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < dists.length; i++)
		{
			double d = dists[i];
			if (d < minDist)
			{
				minDist = d;
				index = i;
			}
		}
		return vals[index];
	}


	static double min1(double[] d)
	{
		double min = Double.MAX_VALUE;
		for (double dd : d) if (dd < min) min = dd;
		return min;
	}

	static double[] prods(double[] d1, double[] d2)
	{
		double[] out = new double[d1.length];
		for(int i = 0; i < d1.length; i++)
		{
			out[i] = d1[i] * d2[i];
		}
		return out;
	}

	static double meanProd(double[] vals, double[] dists)
	{ 
		double out = 0.0; 
		for(int i = 0; i < vals.length; i++)
		{
			out += vals[i] / Math.pow((dists[i] + 1), 2.0);
		}
		out = out / (double) (vals.length);
		return out;
	} 

	static double[] dist(int x1, int y1, int[] x2, int[]y2)
	{
		double[] out = new double[x2.length];
		for (int i = 0; i < x2.length; i++)
			out[i] = dist(x1, x2[i], y1, y2[i]);
		return out;
	}

	static double dist(int x1, int x2, int y1, int y2)
	{
		return Math.sqrt(Math.pow((double) (x1 - x2), 2.0) + Math.pow((double)(y1 - y2), 2.0));
	}
}
