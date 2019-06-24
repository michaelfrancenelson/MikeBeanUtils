package imaging.imageFactories;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.imagerData.ImagerData;
import imaging.imagers.imagerData.PrimitiveImagerData;
import utils.ColorUtils;

public class ImageFactory 
{
	public static class ImageMinMax
	{
		public ImageMinMax(double min, double max, Image img)
		{ this.min = min; this.max = max; this.img = img; }
		private double min;
		private double max;
		private Image img;
		public Image getImg() { return this.img; }
		public double getMin() { return this.min; }
		public double getMax() { return this.max; }
	}

	public static <T> ImageMinMax buildPackageImage(
			ImagerData<T> dat, ColorInterpolator ci, FieldWatcher<T> w)
	{
		dat.setDataMinMax(w, ci);
		BufferedImage out = new BufferedImage(dat.getWidth(), dat.getHeight(), ColorUtils.RGB_TYPE);
		
		for (int i = 0; i < dat.getWidth(); i++) for (int j = 0; j < dat.getHeight(); j++)
		{
			out.setRGB(i, j, dat.getRGBInt(i, j, ci, w));
		}
		return new ImageMinMax(dat.getDataMin(), dat.getDataMax(), out);
	}
	
	public static <T> ImageMinMax buildPrimitiveImage(
			PrimitiveImagerData<T> dat, ColorInterpolator ci)
	{
		dat.setDataMinMax(null, ci);
		BufferedImage out = new BufferedImage(dat.getWidth(), dat.getHeight(), ColorUtils.RGB_TYPE);
		
//		int width = dat.getWidth();
//		int height = dat.getHeight();
		for (int i = 0; i < dat.getWidth(); i++) for (int j = 0; j < dat.getHeight(); j++)
		{
			int rgb = dat.getRGBInt(i, j, ci, null);
			out.setRGB(i, j, rgb);
//			out.setRGB(i, j, dat.getRGBInt(i, j, ci, null));
		}
		return new ImageMinMax(dat.getDataMin(), dat.getDataMax(), out);
	}
	public static <T> ImageMinMax buildPackageImage(
			ImagerData<T> dat, ColorInterpolator ci, FieldWatcher<T> w, double min, double max)
	{
		BufferedImage out = new BufferedImage(
				dat.getWidth(), dat.getHeight(), ColorUtils.RGB_TYPE);
		ci.updateMinMax(min, max);
		
		for (int i = 0; i < dat.getWidth(); i++) for (int j = 0; j < dat.getHeight(); j++)
		{
			out.setRGB(i, j, dat.getRGBInt(i, j, ci, w));
		}
		return new ImageMinMax(dat.getDataMin(), dat.getDataMax(), out);
	}
	
	
	/**
	 *  Build an image directly from an array of int data
	 * 
	 * @param data
	 * @param ci
	 * @param flipAxisX
	 * @param flipAxisY
	 * @param transpose
	 * @return
	 */
	public static ImageMinMax buildPrimitiveImage(
			int[][] data, ColorInterpolator ci,
			boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		PrimitiveImagerData<Object> dat = new 
				PrimitiveImagerData<Object>(data, flipAxisX, flipAxisY, transpose);
		dat.setDataMinMax(null, ci);
		ci.updateMinMax(dat.getDataMin(), dat.getDataMax());
		return buildPrimitiveImage(dat, ci);
	}

	/**
	 *  Build an image directly from an array of double data

	 * @param data
	 * @param ci
	 * @param flipAxisX
	 * @param flipAxisY
	 * @param transpose
	 * @return
	 */
	public static ImageMinMax buildPrimitiveImage(
			double[][] data, ColorInterpolator ci,
			boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		ImagerData<Object> dat = new PrimitiveImagerData<Object>(data, flipAxisX, flipAxisY, transpose);
		dat.setDataMinMax(null, ci);
		return buildPackageImage(dat, ci, null);
	}

	/**
	 *  Build an image directly from an array of int data
	 *  
	 * @param data
	 * @param ci
	 * @param flipAxisX
	 * @param flipAxisY
	 * @param transpose
	 * @return
	 */
	public static <T> ImageMinMax buildPrimitiveImage(
			byte[][] data, ColorInterpolator ci,
			boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		ImagerData<Object> dat = new PrimitiveImagerData<Object>(data, flipAxisX, flipAxisY, transpose);
		ci.updateMinMax(dat.getDataMin(), dat.getDataMax());
		return buildPackageImage(dat, ci, null);
	}

	/**
	 *  Build an image directly from an array of boolean data
	 * 
	 * @param data
	 * @param ci
	 * @param flipAxisX
	 * @param flipAxisY
	 * @param transpose
	 * @return
	 */
	public static ImageMinMax buildPrimitiveImage(
			boolean[][] data, ColorInterpolator ci,
			boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		ImagerData<Object> dat = new PrimitiveImagerData<Object>(data, flipAxisX, flipAxisY, transpose);
		ci.updateMinMax(dat.getDataMin(), dat.getDataMax());
		return buildPackageImage(dat, ci, null);
	}


	/**
	 *  Build an image directly from an array of boolean data
	 * 
	 * @param data
	 * @param ci
	 * @param flipAxisX
	 * @param flipAxisY
	 * @param transpose
	 * @return
	 */
	public static ImageMinMax buildPrimitiveImage(
			Boolean[][] data, ColorInterpolator ci)
	{
		int
		startX = 0, startY = 0,
		endX = data.length, endY = data[0].length, 
		incrementX = 1, incrementY = 1;

		BufferedImage img;

			img = new BufferedImage(data.length, data[0].length, ColorUtils.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(i, j, ci.getBoxedColor(data[i][j]));
				}
			}
		return new ImageMinMax(0, 1, img);
	}
	
	/** A simple panel, filled with a resizing image. */
	public static class SimpleImagePanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		public SimpleImagePanel(Image img) { this.img = img; }
		public SimpleImagePanel(ImageMinMax img) { this.img = img.img; }
		Image img;

		@Override public void paintComponent(Graphics g)
		{
			int width = this.getWidth(); int height = this.getHeight();
			g.drawImage(img, 0, 0, width, height, this);
		}
	}
}