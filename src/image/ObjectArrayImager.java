package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import beans.memberState.SingleFieldWatcher;
import beans.sampleBeans.SimpleBean;
import image.ColorInterpolator.ColorInterpolatorSingleField;

public interface ObjectArrayImager<T> 
{

	/** Return an image generated from the currently active field. */
	public BufferedImage getImage();
	public String getCurrentFieldName();
	public Field getCurrentField();
	public void setField(String fieldName);
	public void setField(Field field);

	public void refresh();


	public static class SingleFieldFactory
	{
		public static <T> ObjectArrayImager<T> factory(
				Class<T> clazz, T[][] objArray, 
				String fieldName, Color[] colors,
				double naDouble, int naInt, Color naColor)
		{

			ObjectArrayImageSingleField1D<T> out = new ObjectArrayImageSingleField1D<T>();
			out.objArray = objArray;

			SingleFieldWatcher<T> w = SingleFieldWatcher.factory(fieldName, null, null, clazz);
			out.watcher = w;
			out.buildDataArray();

			ColorInterpolator ci = ColorInterpolatorSingleField.factory(
					colors, out.datMin, out.datMax, naInt, naColor);			
			out.ci = ci;
			out.buildImage();

			return out;
		}
	}

	public static class ObjectArrayImageSingleField1D<T> implements ObjectArrayImager<T>
	{

		public ColorInterpolator ci;
		public Color naColor;
		private int rgbType = BufferedImage.TYPE_3BYTE_BGR;
		private double[][] dataDouble = null;
		private int[][] dataInt = null;
		private boolean[][] dataBool = null;
		double datMin, datMax;
		public BufferedImage img;
		T[][] objArray;
		SingleFieldWatcher<T> watcher;

		private void buildDataArray()
		{
			switch (watcher.getField().getType().getSimpleName())
			{
			case("int"): buildIntDataArray(); break;
			case("double"): buildDoubleDataArray(); break;
			case("boolean"): buildBooleanDataArray(); break;
			}
		}


		private void buildImage()
		{
			switch (watcher.getField().getType().getSimpleName())
			{
			case("int"): buildIntImage(); break;
			case("double"): buildDoubleImage(); break;
			case("boolean"): break;
			}



		}

		private void buildIntImage()
		{
			System.out.println("ObjectArrayImager.buildIntImage():  building image from int data.");
			for (int row = 0; row < objArray.length; row++)
				for (int col = 0; col < objArray[0].length; col++)
				{
					img.setRGB(row, col, ci.getColor(watcher.getIntVal(objArray[row][col])));
				}
		}
		private void buildDoubleImage()
		{
			System.out.println("ObjectArrayImager.buildIntImage():  building image from int data.");
			for (int row = 0; row < objArray.length; row++)
				for (int col = 0; col < objArray[0].length; col++)
				{
					img.setRGB(row, col, ci.getColor(watcher.getDoubleVal(objArray[row][col])));
				}
		}


		private void buildDoubleDataArray()
		{
			dataDouble = new double[objArray.length][objArray[0].length];
			datMin = Double.MAX_VALUE; datMax = Double.MIN_VALUE;
			double val;
			img = new BufferedImage(objArray.length, objArray[0].length, rgbType);
			for (int row = 0; row < objArray.length; row++)
				for (int col = 0; col < objArray[0].length; col++)
				{
					val = watcher.getDoubleVal(objArray[row][col]);
					dataDouble[row][col] = val;
					if (val < datMin) datMin = val;
					if (val > datMax) datMax = val;
				}
		}

		private void buildBooleanDataArray()
		{

		}
		private void buildIntDataArray()
		{
			System.out.println("ObjectArrayImager.buildIntDataArray(): building int data array.");
			dataInt = new int[objArray.length][objArray[0].length];
			datMin = Double.MAX_VALUE; datMax = Double.MIN_VALUE;
			int val;
			double dVal;
			img = new BufferedImage(objArray.length, objArray[0].length, rgbType);
			for (int row = 0; row < objArray.length; row++)
				for (int col = 0; col < objArray[0].length; col++)
				{
					val = watcher.getIntVal(objArray[row][col]);
					dVal = (double) val;

					dataInt[row][col] = val;
					if (dVal < datMin) datMin = dVal;
					if (dVal > datMax) datMax = dVal;


					//					img.setRGB(row, col, ci.getColor(watcher.getIntVal(objArray[row][col])));
				}
		}


		@Override
		public BufferedImage getImage() { return img; }

		@Override
		public void refresh() {
			// TODO Auto-generated method stub

		}

		@Override
		public String getCurrentFieldName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Field getCurrentField() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setField(String fieldName) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setField(Field field) {
			// TODO Auto-generated method stub

		}

	}

}

