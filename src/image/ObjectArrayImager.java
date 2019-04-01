package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.memberState.SingleFieldWatcher;

public interface ObjectArrayImager<T> 
{

	/** Return the current image, generated from the currently active field. */
	public BufferedImage getImage();
	public String getCurrentFieldName();
	public Field getCurrentField();

	/** Set or update the color scale used in the image 
	 * 
	 * @param colors
	 */
	public void setColors(Color[] colors);

	/** Set the field to be imaged:
	 *  Read data from field and build image.
	 * @param fieldName
	 */
	public void setField(String fieldName);

	/** Set the field to be imaged:
	 *  Read data from field and build image.
	 * @param field
	 */
	public void setField(Field field);

	/**
	 *  Refresh data from the current field and rebuild the image;
	 */
	public void refresh();

	public static class ObjectArrayImageSingleField1D<T> implements ObjectArrayImager<T>
	{
		public ColorInterpolator ci;
		public ColorInterpolator booleanCI;
		public Color naColor;
		private int rgbType = BufferedImage.TYPE_3BYTE_BGR;

		private double[][] dataDouble = null;
		private int[][] dataInt = null;
		private boolean[][] dataBool = null;

		double datMin, datMax;
		public BufferedImage img;
		T[][] objArray;
		SingleFieldWatcher<T> watcher;

		Map<String, SingleFieldWatcher<T>> watchers;
		Map<String, Boolean> parsedBooleanFields;

		public static <T> ObjectArrayImager<T> factory(
				Class<T> clazz, T[][] objArray,
				String fieldName, 
				Color[] colors, Color[] booleanColors,
				double naDouble, int naInt, Color naColor,
				String dblFmt, List<String> parsedBooleanFields
				)
		{
			ObjectArrayImageSingleField1D<T> out = new ObjectArrayImageSingleField1D<T>();

			ColorInterpolator booleanCI = ColorInterpolatorBooleanSingleField.factory(booleanColors, naDouble, naInt, naColor);
			ColorInterpolator ci = ColorInterpolatorSingleField.factory(colors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);

			out.ci = ci;
			out.booleanCI = booleanCI;

			out.objArray = objArray;
			out.watchers = SingleFieldWatcher.getWatcherMap(clazz, dblFmt);

			Map<String, Boolean> mp = new HashMap<>();
			for (String s : out.watchers.keySet()) mp.put(s, false);
			if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
			out.parsedBooleanFields = mp;

			out.setField(fieldName);


			return out;
		}

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
			ColorInterpolator interp;
			if (parsedBooleanFields.get(watcher.getFieldName()))
				interp = booleanCI;
			else interp = ci;

			switch (watcher.getField().getType().getSimpleName())
			{
			case("int"):
			{
				for (int row = 0; row < objArray.length; row++)
					for (int col = 0; col < objArray[0].length; col++)
						img.setRGB(row, col, interp.getColor(watcher.getIntVal(objArray[row][col])));
				break;
			}
			case("double"): 
			{
				for (int row = 0; row < objArray.length; row++)
					for (int col = 0; col < objArray[0].length; col++)
						img.setRGB(row, col, interp.getColor(watcher.getDoubleVal(objArray[row][col])));
				break;
			}
			case("boolean"): 
			{
				for (int row = 0; row < objArray.length; row++)
					for (int col = 0; col < objArray[0].length; col++)
						img.setRGB(row, col, interp.getColor(watcher.getBoolVal(objArray[row][col])));
				break;
			}
			}
		}

		private void buildDoubleDataArray()
		{
			dataBool = null;
			dataInt = null;

			/* don't rebuild an already existing array. */
			if (dataDouble == null)
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
			dataDouble = null;
			dataInt = null;

			/* don't rebuild an already existing array. */
			if (dataBool == null)
				dataBool = new boolean[objArray.length][objArray[0].length];
			img = new BufferedImage(objArray.length, objArray[0].length, rgbType);
			for (int row = 0; row < objArray.length; row++)
				for (int col = 0; col < objArray[0].length; col++)
					dataBool[row][col] = watcher.getBoolVal(objArray[row][col]);
		}

		private void buildIntDataArray()
		{
			dataBool = null;
			dataDouble = null;

			/* don't rebuild an already existing array. */
			if (dataInt == null)
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
				}
		}

		@Override
		public BufferedImage getImage() { return img; }

		@Override
		public void refresh() 
		{
			buildDataArray();
			ci.updateMinMax(datMin,  datMax);
			buildImage();
		}

		@Override
		public String getCurrentFieldName() { return watcher.getFieldName(); }

		@Override
		public Field getCurrentField() { return watcher.getField(); }

		/**      
		 * 1: Select the watcher for the field. <br>
		 * 2: Build the data array and update the color interpolators. <br>
		 * 3: Build the image.
		 */
		@Override
		public void setField(String fieldName) 
		{
			this.watcher = watchers.get(fieldName);
			refresh();
		}

		@Override
		public void setField(Field field) 
		{ setField(field.getName()); }

		@Override
		public void setColors(Color[] colors)
		{	ci.updateColors(colors); }

	}

}

//private void buildIntImage(ColorInterpolator interp)
//{
//	System.out.println("ObjectArrayImager.buildIntImage():  building image from int data.");
//	for (int row = 0; row < objArray.length; row++)
//		for (int col = 0; col < objArray[0].length; col++)
//			img.setRGB(row, col, interp.getColor(watcher.getIntVal(objArray[row][col])));
//}
//
//
//private void buildBooleanImage(ColorInterpolator interp)
//{
//	for (int row = 0; row < objArray.length; row++)
//		for (int col = 0; col < objArray[0].length; col++)
//			img.setRGB(row, col, interp.getColor(watcher.getBoolVal(objArray[row][col])));
//}
//
//private void buildDoubleImage(ColorInterpolator interp)
//{
//	System.out.println("ObjectArrayImager.buildIntImage():  building image from int data.");
//	for (int row = 0; row < objArray.length; row++)
//		for (int col = 0; col < objArray[0].length; col++)
//			img.setRGB(row, col, interp.getColor(watcher.getDoubleVal(objArray[row][col])));
//}
