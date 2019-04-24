package image.imageFactories;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import image.colorInterpolator.ColorInterpolator;

public class PrimitiveImageFactory 
{

	/**
	 *  Build an image directly from an array of double data

	 * @param data
	 * @param ci
	 * @param flipAxisX
	 * @param flipAxisY
	 * @param transpose
	 * @return
	 */
	public static Image buildImage(
			double[][] data, ColorInterpolator ci,
			boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		int
		startX = 0, startY = 0,
		endX = data.length, endY = data[0].length, 
		incrementX = 1, incrementY = 1;

		if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
		if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }

		BufferedImage img;

		if (!transpose)
		{
			img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(i, j, ci.getColor(data[i][j]));
				}
			}
		}
		else
		{
			img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(j, i, ci.getColor(data[i][j]));
				}
			}
		}
		return img;
	}

	/** 
	 *  Build an image directly from an array of double data
	 * @param data
	 * @param ci
	 * @param rgbType
	 * @return
	 */
	public static Image buildImage(double[][] data,  ColorInterpolator ci, int rgbType)
	{
		BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
		for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
			out.setRGB(i, j, ci.getColor(data[i][j]));
		return out;
	}

	/**
	 *  Build an image directly from an array of double data
	 * 
	 * @param data
	 * @param ci
	 * @return
	 */
	public static Image buildImage(double[][] data,  ColorInterpolator ci)
	{ return buildImage(data, ci, ObjectImageFactory.RGB_TYPE); }

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
	public static Image buildImage(int[][] data, ColorInterpolator ci, boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		int
		startX = 0, startY = 0,
		endX = data.length, endY = data[0].length, 
		incrementX = 1, incrementY = 1;

		if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
		if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }

		BufferedImage img;

		if (!transpose)
		{
			img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(i, j, ci.getColor(data[i][j]));
				}
			}
		}
		else
		{
			img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(j, i, ci.getColor(data[i][j]));
				}
			}
		}

		return img;
	}

	/**
	 *  Build an image directly from an array of int data
	 * @param data
	 * @param ci
	 * @param rgbType
	 * @return
	 */
	public static Image buildImage(int[][] data,  ColorInterpolator ci, int rgbType)
	{
		BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
		for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
			out.setRGB(i, j, ci.getColor(data[i][j]));
		return out;
	}

	/**
	 * 
	 *  Build an image directly from an array of int data
	 * @param data
	 * @param ci
	 * @return
	 */
	public static Image buildImage(int[][] data,  ColorInterpolator ci)
	{ return buildImage(data, ci, ObjectImageFactory.RGB_TYPE); }

	
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
	public static Image buildImage(byte[][] data, ColorInterpolator ci, boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		int
		startX = 0, startY = 0,
		endX = data.length, endY = data[0].length, 
		incrementX = 1, incrementY = 1;

		if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
		if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }

		BufferedImage img;

		if (!transpose)
		{
			img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(i, j, ci.getColor(data[i][j]));
				}
			}
		}
		else
		{
			img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(j, i, ci.getColor(data[i][j]));
				}
			}
		}

		return img;
	}

	/**
	 *  Build an image directly from an array of byte data
	 * @param data
	 * @param ci
	 * @param rgbType
	 * @return
	 */
	public static Image buildImage(byte[][] data,  ColorInterpolator ci, int rgbType)
	{
		BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
		for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
			out.setRGB(i, j, ci.getColor(data[i][j]));
		return out;
	}

	/**
	 *  Build an image directly from an array of byte data
	 * 
	 * @param data
	 * @param ci
	 * @return
	 */
	public static Image buildImage(byte[][] data,  ColorInterpolator ci)
	{ return buildImage(data, ci, ObjectImageFactory.RGB_TYPE); }


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
	public static Image buildImage(boolean[][] data, ColorInterpolator ci, boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		int
		startX = 0, startY = 0,
		endX = data.length, endY = data[0].length, 
		incrementX = 1, incrementY = 1;

		if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
		if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }

		BufferedImage img;

		if (!transpose)
		{
			img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(i, j, ci.getColor(data[i][j]));
				}
			}
		}
		else
		{
			img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(j, i, ci.getColor(data[i][j]));
				}
			}
		}
		return img;
	}
	
	

	/**
	 *  Build an image directly from an array of boolean data
	 * 
	 * @param data
	 * @param ci
	 * @return
	 */
	public static Image buildImage(boolean[][] data,  ColorInterpolator ci)
	{ return buildImage(data, ci, ObjectImageFactory.RGB_TYPE); }

	/**
	 *  Build an image directly from an array of boolean data
	 * @param data
	 * @param ci
	 * @param rgbType
	 * @return
	 */
	public static Image buildImage(boolean[][] data,  ColorInterpolator ci, int rgbType)
	{
		BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
		for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
			out.setRGB(i, j, ci.getColor(data[i][j]));
		return out;
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
	public static Image buildImage(Boolean[][] data, ColorInterpolator ci, boolean flipAxisX, boolean flipAxisY, boolean transpose)
	{
		int
		startX = 0, startY = 0,
		endX = data.length, endY = data[0].length, 
		incrementX = 1, incrementY = 1;

		if (flipAxisX) {int t = startX; endX = startX; startX = t; incrementX = -1; }
		if (flipAxisY) {int t = startY; endY = startY; startY = t; incrementY = -1; }

		BufferedImage img;

		if (!transpose)
		{
			img = new BufferedImage(data.length, data[0].length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(i, j, ci.getColor(data[i][j]));
				}
			}
		}
		else
		{
			img = new BufferedImage(data[0].length, data.length, ObjectImageFactory.RGB_TYPE);
			for (int i = startX; i != endX; i += incrementX) {
				for (int j = startY; j != endY; j += incrementY) {
					img.setRGB(j, i, ci.getColor(data[i][j]));
				}
			}
		}
		return img;
	}

	/**
	 *  Build an image directly from an array of boolean data
	 * 
	 * @param data
	 * @param ci
	 * @return
	 */
	public static Image buildImage(Boolean[][] data,  ColorInterpolator ci)
	{ return buildImage(data, ci, ObjectImageFactory.RGB_TYPE); }

	/**
	 *  Build an image directly from an array of boolean data
	 * @param data
	 * @param ci
	 * @param rgbType
	 * @return
	 */
	public static Image buildImage(Boolean[][] data,  ColorInterpolator ci, int rgbType)
	{
		BufferedImage out = new BufferedImage(data.length, data[0].length, rgbType);
		for (int i = 0; i < data.length; i++) for (int j = 0; j < data[0].length; j++)
			out.setRGB(i, j, ci.getColor(data[i][j]));
		return out;
	}
	
	/** A simple panel, filled with a resizing image. */
	public static class SimpleImagePanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		public SimpleImagePanel(Image img) { this.img = img; }
		Image img;

		@Override public void paintComponent(Graphics g)
		{
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
	}






}