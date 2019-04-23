package image.imageFactories;

import java.awt.Image;
import java.awt.image.BufferedImage;

import beans.memberState.FieldWatcher;
import image.arrayImager.ObjectArrayImager;
import image.colorInterpolator.ColorInterpolator;


public class ObjectImageFactory 
{
	public static final int RGB_TYPE = BufferedImage.TYPE_3BYTE_BGR;

	/**
	 *  Build an image from a 2D array of objects using an already existing imager.
	 *  Wrapper for {@link #buildArrayImage(Object[][], FieldWatcher, ColorInterpolator, int, int, boolean, boolean)}
	 *  
	 * @param data
	 * @param imager
	 * @param field optional field name.  If null, the image corresponding to the currently selected field in the imager will be build.
	 * @param orientation1
	 * @param orientation2
	 * @param transpose
	 * @param boolNA
	 * @return
	 */
	public static <T> Image buildArrayImage(
			T[][] data, ObjectArrayImager<T> imager, String field,
			boolean flipAxisX, boolean flipAxisY,
			boolean transpose, boolean boolNA)
	{
		ColorInterpolator ci;
		if (field != null) imager.setField(field);
		if (imager.getWatcher().getField().getType().getSimpleName().equals("boolean"))
			ci = imager.getBooleanInterpolator();
		else ci = imager.getInterpolator();

		return buildArrayImage(
				data, imager.getWatcher(), ci,
				flipAxisX, flipAxisY, transpose, boolNA);
	}

	/**
	 *  Build an image from a 2D array of objects using a field watcher and color interpolator.<br>
	 *  This is the method that actually builds the image.
	 * 
	 * @param data
	 * @param w
	 * @param ci
	 * @param orientation1
	 * @param orientation2
	 * @param transpose
	 * @param boolNA
	 * @return
	 */
	public static <T> Image buildArrayImage(
			T[][] data, FieldWatcher<T> w, ColorInterpolator ci, 
			boolean flipAxisX, boolean flipAxisY,
			boolean transpose, boolean boolNA)
	{
		int imgType = RGB_TYPE;
		ImageDimensions<T> dim = new ImageDimensions<>(data, flipAxisX, flipAxisY, transpose);
		BufferedImage img = new BufferedImage(dim.lengthX, dim.lengthY, imgType);
		String fieldType = w.getField().getType().getSimpleName();
		int datIndex1 = 0, datIndex2 = 0;
		switch(fieldType)
		{
		case("int"):
		{
			if (!transpose)
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						int val = w.getIntVal(data[datIndex1][datIndex2]);
						img.setRGB(i, j, ci.getColor(val));
						datIndex2++;
					}
					datIndex1++;
					datIndex2 = 0;
				}
			else
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						img.setRGB(i, j, ci.getColor(w.getIntVal(data[datIndex1][datIndex2])));
						datIndex1++; 
					}
					datIndex2++;
					datIndex1 = 0;
				}
			break;
		}
		case("double"):
			if (!transpose)
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						T t = data[datIndex1][datIndex2];
						double val = w.getDoubleVal(t);
						img.setRGB(i, j, ci.getColor(val));
						datIndex2++;
					}
					datIndex1++;
					datIndex2 = 0;
				}
			else
				for (int i = dim.startX; i != dim.endX; i += dim.incrementX)
				{
					for (int j = dim.startY; j != dim.endY; j += dim.incrementY)
					{
						img.setRGB(i, j, ci.getColor(w.getDoubleVal(data[datIndex1][datIndex2])));
						datIndex1++; 
					}
					datIndex2++;
					datIndex1 = 0;
				}
		break;
		case("boolean"):
		{

			break;
		}

		}

		return img;
	}



	public static class ImageDimensions<T>
	{
		public int lengthX, lengthY;
		public int startX, startY;
		public int endX, endY;
		public int incrementX, incrementY;

		private void transposeDimensions()
		{
			int t1;

			t1 = lengthX; lengthX = lengthY; lengthY = t1;
			t1 = startX; startX = startY; startY = t1;
			t1 = endX; endX = endY; endY = t1;
			t1 = incrementX; incrementX = incrementY; incrementY = t1;
		}

		ImageDimensions(double[][] data)
		{
			lengthX = data.length; lengthY = data[0].length;
			startX = 0; startY = 0;
			endX = lengthX; endY = lengthY;
			incrementX = 1; incrementY = 1;
		}
		ImageDimensions(int[][] data)
		{
			lengthX = data.length; lengthY = data[0].length;
			startX = 0; startY = 0;
			endX = lengthX; endY = lengthY;
			incrementX = 1; incrementY = 1;
		}

		ImageDimensions(
				T[][] dat,
				boolean flipAxisX, boolean flipAxisY,
				boolean transpose)
		{
			lengthX = dat.length; lengthY = dat[0].length;
			if (! flipAxisX)
			{
				startX = 0; endX = lengthX;
				incrementX = 1;
			}
			else 
			{
				startX = lengthX - 1; endX = -1;
				incrementX = -1;
			}

			if (! flipAxisY)
			{
				startY = 0; endY = lengthY;
				incrementY = 1;
			}
			else 
			{
				startY = lengthY - 1; endY = -1;
				incrementY = -1;
			}
			if (transpose) this.transposeDimensions();
		}
	}

}