package demos;

import java.awt.Color;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import image.colorInterpolator.ColorInterpolator;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;
import image.imageFactories.PrimitiveImageFactory;
import image.imageFactories.PrimitiveImageFactory.SimpleImagePanel;
import swing.SwingUtils;
import utils.ColorUtils;
import utils.Sequences;

public class PrimitiveImageFactoryDemo 
{
	static double[][] datDbl;
	static int[][] datInt;
	static boolean[][] datBool;

	static Random r;
	static ColorInterpolator c1;

	static JFrame f;

	static void setup()
	{
		r = new Random();
	}

	public static void main(String[] args) 
	{
		doubleDemo(100, 500, 600, 10, 200.1, 1.5);
		intDemo(500, 600, 10, 200, 1.3);
		booleanDemo(500, 600, 1.5);
	}

	static void booleanDemo(int nRow, int nCol, double size)
	{
		datBool = new boolean[nRow][nCol];
		datInt  = new int[nRow][nCol];
		Border b = BorderFactory.createLineBorder(Color.red);
		int cutoff0 = (nRow + nCol) / 2;
		int cutoff1 = (nRow + nCol) / 3;
		int cutoff2 = 2 * (nRow + nCol) / 3;

		for (int i = 0; i < nRow; i++) for (int j = 0; j < nCol; j++)
		{
			if (i + j < cutoff0) datBool[i][j] = true;
			if (i + j < cutoff1) datInt[i][j] = 0;
			else if (i + j < cutoff2) datInt[i][j] = 1;
			else datInt[i][j] = Integer.MIN_VALUE;

		}

		c1 = SimpleBooleanColorInterpolator.factory(ColorUtils.BLUES, Color.gray);
		f = SwingUtils.frameFactory((int)(2 * nCol * size), (int)(nRow * size), "Boolean array demos", 1, 2);

		JPanel pp = new SimpleImagePanel(PrimitiveImageFactory.buildImage(datBool, c1));
		pp.setBorder(b);
		f.add(pp);
		pp = new SimpleImagePanel(PrimitiveImageFactory.buildImage(datInt, c1));
		pp.setBorder(b);
		f.add(pp);
		f.setVisible(true);
	}

	static void intDemo(int nCol, int nRow, int min, int max, double size)
	{
		setup();
		double ciMin, ciMax;
		int nPts = max - min;

		int[] vals = Sequences.spacedIntervals(min, max, nPts);
		nPts = vals.length;

		int[] x = new int[nPts], y = new int[nPts];
		double[] dists = new double[nPts];
		datInt = new int[nCol][nRow];

		for (int i = 0; i < nPts; i++)
		{
			x[i] = r.nextInt(nCol + 1); y[i] = r.nextInt(nRow + 1);
		}

		for (int i = 0; i < nCol; i++) for (int j = 0; j < nRow; j++)
		{
			dists = dist(i, j, x, y);
			datInt[i][j] = nearest(dists, vals);
		}

		ciMin = min; ciMax = max;
		c1 = SimpleColorInterpolator.factory(
				ColorUtils.TOPO_COLORS, 
				ciMin, ciMax,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, "%0.4f");

		f = SwingUtils.frameFactory((int)(nCol * size), (int) (nRow * size), "Int value array demo");
		f.add(new SimpleImagePanel(PrimitiveImageFactory.buildImage(datInt, c1)));
		f.setVisible(true);
	}


	static void doubleDemo(int nPts, int nCol, int nRow, double min, double max, double size)
	{
		setup();
		double ciMin, ciMax, dd;
		double minVal = Double.MAX_VALUE, maxVal = Double.MIN_VALUE;
		double range = max - min;
		ciMax = max; ciMin = min;

		int[] x = new int[nPts], y = new int[nPts];
		double[] dists = new double[nPts], vals = new double[nPts];
		datDbl = new double[nCol][nRow];

		for (int i = 0; i < nPts; i++)
		{
			x[i] = r.nextInt(nCol + 1);
			y[i] = r.nextInt(nRow + 1);
			vals[i] = range * r.nextDouble() + min;
			System.out.println("random val = " + vals[i]);
		}

		for (int i = 0; i < nCol; i++) for (int j = 0; j < nRow; j++)
		{
			dists = dist(i, j, x, y);
			dd = min1(prods(dists, vals));

			datDbl[i][j] = dd;

			if (dd < minVal) minVal = dd;
			if (dd > maxVal) maxVal = dd;
		}

		ciMin = minVal; ciMax = maxVal;
		c1 = SimpleColorInterpolator.factory(
				ColorUtils.TOPO_COLORS, 
				ciMin, ciMax,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, "%0.4f");

		f = SwingUtils.frameFactory((int)(nCol * size), (int) (nRow * size), "Double value array demo");
		f.add(new SimpleImagePanel(PrimitiveImageFactory.buildImage(datDbl, c1)));
		f.setVisible(true);
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
			//			out += vals[i] * dists[i];
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
