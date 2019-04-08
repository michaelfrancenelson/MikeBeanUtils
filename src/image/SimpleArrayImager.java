package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;

/** Create images from numeric or boolean members of objects in 2D arrays.
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
public class SimpleArrayImager<T> implements ObjectArrayImager<T>
{
	ColorInterpolator ci;
	ColorInterpolator booleanCI;
	int rgbType = BufferedImage.TYPE_3BYTE_BGR;

	double[][]  dataDouble = null;
	int[][]     dataInt = null;
	boolean[][] dataBool = null;

	double datMin, datMax;

	BufferedImage img;
	T[][] objArray;
	SimpleFieldWatcher<T> watcher;
	Class<T> clazz;

	Map<String, SimpleFieldWatcher<T>> watchers;
	Map<String, Boolean> parsedBooleanFields;

	int[] currentSelectionArrayCoords;

	/**
	 * 
	 * @param clazz
	 * @param objArray 
	 * @param fieldName the image will be built from values in this field.
	 * @param gradientColors color gradient
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
			Class<T> clazz, T[][] objArray,	String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields
			)
	{
		SimpleArrayImager<T> out = new SimpleArrayImager<T>();
		out.clazz = clazz;
		out.ci = SimpleColorInterpolator.factory(gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
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
	void buildDataArray()
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
	void buildImage()
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
					img.setRGB(row, col, booleanCI.getColor(watcher.getBoolVal(objArray[row][col])));
			//			img.setRGB(row, col, interp.getColor(watcher.getBoolVal(objArray[row][col])));
			break;
		}
		}
	}

	/**
	 * 
	 */
	void buildDoubleDataArray()
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
	void buildBooleanDataArray()
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
	void buildIntDataArray()
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

	@Override
	public String queryObjectAt(int i, int j) { return watcher.getStringVal(getObjAt(i, j)); }

	/** 
	 * Checks that the coordinates are valid.
	 * Sets the current selection coordinates.
	 */
	@Override
	public T getObjAt(int i, int j) 
	{

		if ((i > 0 && j > 0) &&  (i < objArray.length && j < objArray[0].length))
		{
			setCurrentSelection(i, j);
			return objArray[i][j];
		}
		else throw new IllegalArgumentException("Input coordinates + (" + i + ", " + j + 
				") are incompatible with the object array size (" + objArray.length + ", " + objArray[0].length+ ".");
	}

	/**
	 * Adjusts coordinates > 1.0 or < 0.0 to fall within range 1.0 - 0.0 <br>
	 * Sets the current selection coordinates.
	 */
	@Override
	public T getObjAt(double relativeI, double relativeJ) 
	{
		setCurrentSelection(relativeI, relativeJ);
		return getCurrentSelectedObj();
	}

	@Override
	public int[] getArrayCoords(double relativeI, double relativeJ)
	{
		int i = (int) (((double) (objArray.length)) * relativeI);
		int j = (int) (((double) (objArray[0].length)) * relativeJ);

		i = Math.min(i, objArray.length - 1);
		j = Math.min(j, objArray[0].length - 1);

		//		System.out.println("SimpleArrayImager.getObjAt():  relative coords are " + relativeI + ", " + relativeJ + ").");
		//		System.out.println("SimpleArrayImager.getObjAt():  array coords are    " + i + ", " + j + ").");

		i = Math.min(objArray.length - 1, Math.max(0, i));
		j = Math.min(objArray[0].length - 1, Math.max(0, j));
		return new int[] {i, j};
	}


	@Override
	public void setCurrentSelection(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }

	@Override
	public void setCurrentSelection(double relativeI, double relativeJ)
	{
		int[] coords = getArrayCoords(relativeI, relativeJ);
		currentSelectionArrayCoords = new int[] {coords[0], coords[1]};
	}

	@Override public FieldWatcher<T> getWatcher() { return this.watcher; }
	@Override public T[][] getData() { return this.objArray; }
	@Override public Class<T> getObjClass() { return clazz; }

	@Override public int[] getCurrentSelectedCoords() { return currentSelectionArrayCoords; }
	@Override public T getCurrentSelectedObj() { return objArray[currentSelectionArrayCoords[0]][currentSelectionArrayCoords[1]]; }


}





//	public BufferedImage getLegend(int nLabels, int nSteps, double offset1, double offset2)
//	{
//		BufferedImage leg;
//	
//		
//		
//		
//		
//		switch (watcher.getField().getType().getSimpleName())
//	case("int"):
//	{
//		for (int row = 0; row < objArray.length; row++)
//			for (int col = 0; col < objArray[0].length; col++)
//				img.setRGB(row, col, interp.getColor(watcher.getIntVal(objArray[row][col])));
//		break;
//	}
//	case("double"): 
//	{
//		for (int row = 0; row < objArray.length; row++)
//			for (int col = 0; col < objArray[0].length; col++)
//				img.setRGB(row, col, interp.getColor(watcher.getDoubleVal(objArray[row][col])));
//		break;
//	}
//	case("boolean"): 
//	{
//		for (int row = 0; row < objArray.length; row++)
//			for (int col = 0; col < objArray[0].length; col++)
//				img.setRGB(row, col, booleanCI.getColor(watcher.getBoolVal(objArray[row][col])));
//		//			img.setRGB(row, col, interp.getColor(watcher.getBoolVal(objArray[row][col])));
//		break;
//	}
//		
//		
//		return leg;
//	}
//	


