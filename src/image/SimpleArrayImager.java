package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.SimpleFieldWatcher;

/** Create images from numeric or boolean members of objects in 2D arrays.
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
public class SimpleArrayImager<T> implements ObjectArrayImager<T>
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
	SimpleFieldWatcher<T> watcher;

	Map<String, SimpleFieldWatcher<T>> watchers;
	Map<String, Boolean> parsedBooleanFields;

	/**
	 * 
	 * @param clazz
	 * @param objArray 
	 * @param fieldName the image will be built from values in this field.
	 * @param colors color gradient
	 * @param booleanColors Colors to use to plot a boolean member.  Only the first and last colors are used.
	 * @param naDouble pixels with this value will be plotted with the 'naColor'
	 * @param naInt pixels with this value will be plotted with the 'naColor'
	 * @param naColor color to plot for NA values.
	 * @param dblFmt
	 * @param parsedBooleanFields Which fields should be drawn as parsed boolean variables?
	 *                            This feature allows true/false and naColors to be shown.
	 *                            Can be null.
	 * @return
	 */
	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,
			String fieldName, 
			Color[] colors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields
			)
	{
		SimpleArrayImager<T> out = new SimpleArrayImager<T>();
		out.ci = SimpleColorInterpolator.factory(colors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naDouble, naInt, naColor);
		out.objArray = objArray;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		for (String s : out.watchers.keySet()) mp.put(s, false);
		if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
		out.parsedBooleanFields = mp;
		out.setField(fieldName);

		return out;
	}

	/**
	 * 
	 */
	private void buildDataArray()
	{
		switch (watcher.getField().getType().getSimpleName())
		{
		case("int"): buildIntDataArray(); break;
		case("double"): buildDoubleDataArray(); break;
		case("boolean"): buildBooleanDataArray(); break;
		}
	}

	/**
	 * 	
	 */
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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
	public void refresh() 
	{
		buildDataArray();
		ci.updateMinMax(datMin,  datMax);
		buildImage();
	}

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

	@Override public BufferedImage getImage() { return img; }
	@Override public String getCurrentFieldName() { return watcher.getFieldName(); }
	@Override public Field getCurrentField() { return watcher.getField(); }
	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public void setColors(Color[] colors) {	ci.updateColors(colors); }
}