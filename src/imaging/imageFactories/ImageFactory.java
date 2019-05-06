package imaging.imageFactories;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.ImagerData;
import imaging.imagers.PrimitiveArrayData;

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
		BufferedImage out = new BufferedImage(dat.getWidth(), dat.getHeight(), ObjectImageFactory.RGB_TYPE);
		
		for (int i = 0; i < dat.getWidth(); i++) for (int j = 0; j < dat.getHeight(); j++)
		{
			out.setRGB(i, j, dat.getRGBInt(i, j, ci, w));
		}
		return new ImageMinMax(dat.getDataMin(), dat.getDataMax(), out);
	}
	public static <T> ImageMinMax buildPrimitiveImage(
			PrimitiveArrayData<T> dat, ColorInterpolator ci, FieldWatcher<T> w)
	{
		dat.setDataMinMax(null, ci);
		BufferedImage out = new BufferedImage(dat.getWidth(), dat.getHeight(), ObjectImageFactory.RGB_TYPE);
		
		for (int i = 0; i < dat.getWidth(); i++) for (int j = 0; j < dat.getHeight(); j++)
		{
			out.setRGB(i, j, dat.getRGBInt(i, j, ci, w));
		}
		return new ImageMinMax(dat.getDataMin(), dat.getDataMax(), out);
	}
	public static <T> ImageMinMax buildPackageImage(
			ImagerData<T> dat, ColorInterpolator ci, FieldWatcher<T> w, double min, double max)
	{
		BufferedImage out = new BufferedImage(
				dat.getWidth(), dat.getHeight(), ObjectImageFactory.RGB_TYPE);
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
		ImagerData<Object> dat = new 
				PrimitiveArrayData<Object>(data, flipAxisX, flipAxisY, transpose);
		dat.setDataMinMax(null, ci);
		ci.updateMinMax(dat.getDataMin(), dat.getDataMax());
		return buildPackageImage(dat, ci, null);
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
		ImagerData<Object> dat = new PrimitiveArrayData<Object>(data, flipAxisX, flipAxisY, transpose);
		dat.setDataMinMax(null, ci);
//		ci.updateMinMax(dat.getDataMin(), dat.getDataMax());
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
		ImagerData<Object> dat = new PrimitiveArrayData<Object>(data, flipAxisX, flipAxisY, transpose);
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
		ImagerData<Object> dat = new PrimitiveArrayData<Object>(data, flipAxisX, flipAxisY, transpose);
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

			img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
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
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
	}
}

//
//	/**
//	 *  Build an image directly from an array of boolean data
//	 * 
//	 * @param data
//	 * @param ci
//	 * @return
//	 */
//	@Deprecated
//	public static Image buildPrimitiveImage(Boolean[][] data,  ColorInterpolator ci)
//	{ return buildPrimitiveImage(data, ci, ObjectImageFactory.RGB_TYPE); }
//
//	/**
//	 *  Build an image directly from an array of boolean data
//	 * @param data
//	 * @param ci
//	 * @param rgbType
//	 * @return
//	 */
//	@Deprecated
//	public static Image buildPrimitiveImage(Boolean[][] data,  ColorInterpolator ci, int rgbType)
//	{
//		BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
//		for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
//			out.setRGB(i, j, ci.getBoxedColor(data[i][j]));
//		return out;
//	}
//
//ImagerData<Object> dat = new PrimitiveArrayData<Object>(data, flipAxisX, flipAxisY, transpose);
//BufferedImage out = new BufferedImage(dat.getWidth(), dat.getHeight(), ObjectImageFactory.RGB_TYPE);
//for (int i = 0; i < dat.getWidth(); i++) for (int j = 0; j < dat.getHeight(); j++)
//	out.setRGB(i, j, ci.getColor(dat.getInterpolatorData(i, j)));
//return out;
//
//int
//startX = 0, startY = 0,
//endX = data.length, endY = data[0].length, 
//incrementX = 1, incrementY = 1;
//
//if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
//if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }
//
//BufferedImage img;
//
//if (!transpose)
//{
//	img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
//	for (int i = startX; i != endX; i += incrementX) {
//		for (int j = startY; j != endY; j += incrementY) {
//			img.setRGB(i, j, ci.getColor(data[i][j]));
//		}
//	}
//}
//else
//{
//	img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
//	for (int i = startX; i != endX; i += incrementX) {
//		for (int j = startY; j != endY; j += incrementY) {
//			img.setRGB(j, i, ci.getColor(data[i][j]));
//		}
//	}
//}
//
//return img;
//}



//
//
//int
//startX = 0, startY = 0,
//endX = data.length, endY = data[0].length, 
//incrementX = 1, incrementY = 1;
//
//int width = data.length, height = data[0].length;
//
//int offsetX = 0;
//int offsetY = 0;
//
//if (flipAxisX) 
//{
//	int t = endX; endX = startX - 1; startX = t - 1; incrementX = -1; 
//	offsetX = width - 1;
//}
//if (flipAxisY) {
//	int t = endY; endY = startY - 1; startY = t - 1; incrementY = -1; 
//	offsetY = height - 1;
//}
//
//BufferedImage img;
//
//int imgI = 0; int imgJ = 0;

//
//if (!transpose)
//{
//	
//	img = new BufferedImage(width, height, ObjectImageFactory.RGB_TYPE);
//	for (int i = startX; i != endX; i += incrementX) 
//	{
//		imgI = offsetX + (incrementX * i);
//		for (int j = startY; j != endY; j += incrementY) 
//		{
//			imgJ = offsetY + (incrementY * j);
//			img.setRGB(imgI, imgJ, ci.getColor(data[i][j]));
//		}
//	}
//}
//else
//{
//	img = new BufferedImage(height, width, ObjectImageFactory.RGB_TYPE);
//	for (int i = startX; i != endX; i += incrementX) 
//	{
//		imgI = offsetX + (incrementX * i);
//		
//		for (int j = startY; j != endY; j += incrementY) 
//		{
//			imgJ = offsetY + (incrementY * j);
//			img.setRGB(j, i, ci.getColor(data[i][j]));
//		}
//	}
//}
//
//return out;
//}

//*  Build an image directly from an array of double data
//* @param data
//* @param ci
//* @param rgbType
//* @return
//*/
//@Deprecated
//public static Image buildPrimitiveImage(double[][] data,  ColorInterpolator ci, int rgbType)
//{
//	BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
//	for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
//		out.setRGB(i, j, ci.getColor(data[i][j]));
//	return out;
//}

/**
 *  Build an image directly from an array of double data
 * 
 * @param data
 * @param ci
 * @return
 */
//@Deprecated
//public static Image buildPrimitiveImage(double[][] data,  ColorInterpolator ci)
//{ return buildPrimitiveImage(data, ci, ObjectImageFactory.RGB_TYPE); }
//
///**
//
//
//
///**
//*  Build an image directly from an array of int data
//* @param data
//* @param ci
//* @param rgbType
//* @return
//*/
//@Deprecated
//public static Image buildPrimitiveImage(int[][] data,  ColorInterpolator ci, int rgbType)
//{
//	BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
//	for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
//		out.setRGB(i, j, ci.getColor(data[i][j]));
//	return out;
//}
//
///**
//* 
//*  Build an image directly from an array of int data
//* @param data
//* @param ci
//* @return
//*/
//@Deprecated
//public static Image buildPrimitiveImage(int[][] data,  ColorInterpolator ci)
//{ return buildPrimitiveImage(data, ci, ObjectImageFactory.RGB_TYPE); }


///**
// *  Build an image directly from an array of byte data
// * @param data
// * @param ci
// * @param rgbType
// * @return
// */
//@Deprecated
//public static Image buildPrimitiveImage(byte[][] data,  ColorInterpolator ci, int rgbType)
//{
//	BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
//	for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
//		out.setRGB(i, j, ci.getColor(data[i][j]));
//	return out;
//}
//
///**
// *  Build an image directly from an array of byte data
// * 
// * @param data
// * @param ci
// * @return
// */
//@Deprecated
//public static Image buildPrimitiveImage(byte[][] data,  ColorInterpolator ci)
//{ return buildPrimitiveImage(data, ci, ObjectImageFactory.RGB_TYPE); }

//int
//startX = 0, startY = 0,
//endX = data.length, endY = data[0].length, 
//incrementX = 1, incrementY = 1;
//
//if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
//if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }
//
//BufferedImage img;
//
//if (!transpose)
//{
//	img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
//	for (int i = startX; i != endX; i += incrementX) {
//		for (int j = startY; j != endY; j += incrementY) {
//			img.setRGB(i, j, ci.getColor(data[i][j]));
//		}
//	}
//}
//else
//{
//	img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
//	for (int i = startX; i != endX; i += incrementX) {
//		for (int j = startY; j != endY; j += incrementY) {
//			img.setRGB(j, i, ci.getColor(data[i][j]));
//		}
//	}
//}
//return img;
//}

///**
//*  Build an image directly from an array of boolean data
//* 
//* @param data
//* @param ci
//* @return
//*/
//@Deprecated
//public static Image buildPrimitiveImage(boolean[][] data,  ColorInterpolator ci)
//{ return buildPrimitiveImage(data, ci, ObjectImageFactory.RGB_TYPE); }
//
///**
//*  Build an image directly from an array of boolean data
//* @param data
//* @param ci
//* @param rgbType
//* @return
//*/
//@Deprecated
//public static Image buildPrimitiveImage(boolean[][] data,  ColorInterpolator ci, int rgbType)
//{
//BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
//for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
//	out.setRGB(i, j, ci.getColor(data[i][j]));
//return out;
//}