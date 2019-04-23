package image.arrayImager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import beans.memberState.SimpleFieldWatcher.DblArrayMinMax;
import beans.memberState.SimpleFieldWatcher.IntArrayMinMax;
import image.colorInterpolator.ColorInterpolator;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;
import image.imageFactories.GradientImageFactory;
import image.imageFactories.PrimitiveImageFactory;
import utils.Sequences;

/** Create images from int, double, or boolean members of objects in 2D arrays.
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
@Deprecated
public class SimpleArrayImager<T> implements ObjectArrayImager<T>
{
	T[][] objArray;
	ColorInterpolator ci, booleanCI;
	int rgbType = BufferedImage.TYPE_3BYTE_BGR;

//	double[][]  dataDouble = null;
//	double[][] legDatDouble = null;
//	int[][]     dataInt = null; 
//	int[][]     legDatInt = null;
	
	
	IntArrayMinMax datInt, legDatInt;
	DblArrayMinMax datDbl, legDatDbl;
	
	boolean[][] dataBool = null, legDatBool = null;
	double datMin, datMax;
	
	BufferedImage img, legImg;
	SimpleFieldWatcher<T> watcher;
	Class<T> clazz;
	private int legDatDim1, legDatDim2;
	double legendMin, legendMax;
	
	int legIndexMult1, legIndexMult2;
	boolean includeNABoolean, buildLegend;

	Map<String, FieldWatcher<T>> watchers;
	Map<String, Boolean> parsedBooleanFields;

	int[] currentSelectionArrayCoords;
	int nLegendSteps;
	private boolean transpose = false, flipAxisX = false, flipAxisY = false;
	private boolean horizontalLegend = false, legendLowToHigh = true;

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
		out.setClazz(clazz);
		out.ci = SimpleColorInterpolator.factory(gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naColor);
		out.objArray = objArray;

		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		for (String s : out.watchers.keySet()) mp.put(s, false);
		if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
		out.setParsedBooleanFields(mp);
		out.setField(fieldName);
		return out;
	}

	/**
	 * 
	 * @param clazz
	 * @param objArray
	 * @param fieldName
	 * @param gradientColors
	 * @param booleanColors
	 * @param naDouble
	 * @param naInt
	 * @param naColor
	 * @param dblFmt
	 * @param parsedBooleanFields
	 * @param includeNABoolean
	 * @param nLegendSteps
	 * @param legendDirection
	 * @return
	 */
	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,
			String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean,
			int nLegendSteps, boolean legLowToHi, boolean horizLegend
			)
	{
		SimpleArrayImager<T> out = new SimpleArrayImager<T>();
		out.setClazz(clazz);
		out.ci = SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naColor);
		out.objArray = objArray;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		for (String s : out.watchers.keySet()) mp.put(s, false);
		if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
		out.setParsedBooleanFields(mp);

		if (nLegendSteps > 0)
			out.buildLegend = true;
		out.nLegendSteps = nLegendSteps;
		out.horizontalLegend = horizLegend;
		out.legendLowToHigh = legLowToHi;
		out.includeNABoolean = includeNABoolean;

		out.setField(fieldName);
		out.refresh();
		return out;
	}

	@Deprecated // This still needs work
	/**
	 * Create a data array from which to build a legend for the main object array image.
	 */
	void buildLegendData()
	{
		switch (getWatcher().getField().getType().getSimpleName())
		{
		case("int"): 
		{
			legDatBool = null; legDatDbl = null;
			
			/* Assumes that the int data has already been updated. */
			int legMin = datInt.getMin(), legMax = datInt.getMax();
			if (legendLowToHigh && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }
			
			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizontalLegend),
					legMin, legMax);
			break;
		}

		case("double"): 
		{
			legDatBool = null; legDatInt = null; 
			double legMin = datDbl.getMin(), legMax = datInt.getMax();
			if (legendLowToHigh && legMin > legMax) { double t = legMin; legMax = legMin; legMin = t; }
			
			legDatDbl = new DblArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizontalLegend),
					legMin, legMax);
			break;
		}

		case("boolean"):
		{
			
			break;
		}

		}
	}

	/**
	 * 
	 */
	void buildLegendImage()
	{
		ColorInterpolator interp;
		if (getParsedBooleanFields().get(getWatcher().getFieldName()))
			interp = booleanCI;
		else interp = ci;
		int row = 0, col = 0;
		String type = watcher.getField().getType().getSimpleName();

		switch (type)
		{

		case("int"):
		{
			legDatBool = null; legDatDbl = null;
			int intMin = (int) legendMin;
			int intMax = (int) legendMax;

			legDatInt = new IntArrayMinMax(
					Sequences.spacedIntervals2D(
					intMin, intMax, 
					nLegendSteps, horizontalLegend),
					intMin, intMax);
			legImg = (BufferedImage) PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp);
			break;
		}
		case("double"): 
		{
			legDatDbl = new DblArrayMinMax(
					Sequences.spacedIntervals2D(
							legendMin, legendMax,
							nLegendSteps, horizontalLegend),
							legendMin, legendMax);
			legImg = (BufferedImage) 
					PrimitiveImageFactory.buildImage(legDatDbl.getDat(), interp);
			break;
		}
		case("boolean"): 
		{

			
			legImg = (BufferedImage) GradientImageFactory.buildBooleanGradient(interp, horizontalLegend, includeNABoolean);
			break;
		}
		}	
	}

	/**
	 * 	
	 */
	void buildImage()
	{
		ColorInterpolator interp;
		if (getParsedBooleanFields().get(getWatcher().getFieldName()))
			interp = booleanCI;
		else interp = ci;


		switch (getWatcher().getField().getType().getSimpleName())
		{
		case("int"):
		{
			dataBool = null;
			datDbl = null;
			datInt = watcher.getIntVal(objArray);
			interp.updateMinMax(datInt.getMin(), datInt.getMax());
			img = (BufferedImage) PrimitiveImageFactory.buildImage(
					datInt.getDat(), interp);
			break;
		}
		case("double"): 
		{
			dataBool = null;
			datInt = null;
			datDbl = watcher.getDoubleVal(objArray);
			interp.updateMinMax(datDbl.getMin(), datDbl.getMax());
			img = (BufferedImage) PrimitiveImageFactory.buildImage(datDbl.getDat(), interp);
			break;
		}
		case("boolean"): 
		{
			
			
			
			for (int row = 0; row < objArray.length; row++)
				for (int col = 0; col < objArray[0].length; col++)
					img.setRGB(row, col, booleanCI.getColor(watcher.getBoolVal(objArray[row][col])));
			break;
		}
		}
	}

	/**
	 * 
	 */
//	void buildDoubleDataArray()
//	{
//		dataBool = null;
//		datInt = null;
//
//		datDbl = watcher.getDoubleVal(objArray);
//	}

//	/**
//	 * 
//	 */
//	void buildBooleanDataArray()
//	{
//		datDbl = null;
//		datInt = null;
//
//		/* If not using 
//		
//		/* don't rebuild an already existing array. */
//		if (dataBool == null)
//			dataBool = new boolean[objArray.length][objArray[0].length];
//		img = new BufferedImage(objArray.length, objArray[0].length, rgbType);
//		for (int row = 0; row < objArray.length; row++)
//			for (int col = 0; col < objArray[0].length; col++)
//				dataBool[row][col] = getWatcher().getBoolVal(objArray[row][col]);
//
//		//		if (buildLegend)
//		//		{
//		//			legDatDouble = null;
//		//			legDatInt = null;
//		//
//		//			int dim = 2;
//		//			legDatBool = new boolean[dim * legIndexMult1][dim * legIndexMult2];
//		//			legDatBool[0][0] = true;
//		//			legDatBool[legIndexMult1][legIndexMult2] = false;
//		//
//		//			/* Include a pixel for the na color, if needed */
//		//			if (includeNABoolean) dim = 3;
//		//			legImg = new BufferedImage(dim * legIndexMult1, dim * legIndexMult2, rgbType);
//		//		}
//	}

	/**
	 * 
	 */
//	void buildIntDataArray()
//	{
//		dataBool = null;
//		datDbl = null;
//
//		datInt = watcher.getIntVal(objArray);
//	}		

	@Override
	public void refresh() 
	{
		buildImage();

		if (buildLegend)
		{
			buildLegendImage();
		}
	}

	/**      
	 * 1: Select the watcher for the field. <br>
	 * 2: Build the data array and update the color interpolators. <br>
	 * 3: Build the image.
	 */

	@Override public BufferedImage getImage() { return img; }
	@Override public BufferedImage getLegendImage() { return legImg; }
	@Override public String getCurrentFieldName() { return getWatcher().getFieldName(); }
	@Override public Field getCurrentField() { return getWatcher().getField(); }
	@Override public void setField(String fieldName) { this.setWatcher((SimpleFieldWatcher<T>) watchers.get(fieldName)); refresh(); }
	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public void setColors(Color[] colors) {	ci.updateColors(colors); }
	@Override public String queryObjectAt(int i, int j) { return getWatcher().getStringVal(getObjAt(i, j)); }


	@Override public String queryLegendAt(double relativeI, double relativeJ)
	{
		int[] coords = ObjectArrayImager.getObjArrayCoords(relativeI, relativeJ, legDatDim1, legDatDim2);
		System.out.println("SimpleArrayImager.queryLegendAt() legend data coords = " + coords[0] + " " + coords[1]);
		return queryLegendAt(coords[0], coords[1]);
	}

	public String queryLegendAt(int i, int j) 
	{
		System.out.println("SimpleArrayImager.queryLegendAt() legend data coords = " + i + " " + j);
		switch (watcher.getField().getType().getSimpleName())
		{
		case("int"):
			return String.format("%d", legDatInt.getDat()[i][j]);
		case("double"): 
			return String.format(watcher.getDblFmt(), legDatDbl.getDat()[i][j]);
		case("boolean"): 
		{
			if (i == 2 || j == 2) return "NA";
			return String.format("s", legDatBool[i][j]);
		}
		}
		return null;
	}


	/** 
	 * Checks that the coordinates are valid.
	 * Sets the current selection coordinates.
	 */
	@Override
	public T getObjAt(int i, int j) 
	{
		if ((i > 0 && j > 0) &&  (i < objArray.length && j < objArray[0].length))
		{
			setCurrentSelectedArrayCoords(i, j);
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
		return ObjectArrayImager.getObjArrayCoords(relativeI, relativeJ, objArray.length, objArray[0].length);
		//		int i, j; 
		//		i = (int) (((double) (objArray.length)) * relativeI);
		//		j = (int) (((double) (objArray[0].length)) * relativeJ);
		//
		//		i = Math.min(i, objArray.length - 1);
		//		j = Math.min(j, objArray[0].length - 1);
		//
		//		//		System.out.println("SimpleArrayImager.getObjAt():  relative coords are " + relativeI + ", " + relativeJ + ").");
		//		//		System.out.println("SimpleArrayImager.getObjAt():  array coords are    " + i + ", " + j + ").");
		//
		//		i = Math.min(objArray.length - 1, Math.max(0, i));
		//		j = Math.min(objArray[0].length - 1, Math.max(0, j));

		//		i = ObjectArrayImager.relativeIntCoord(relativeI, objArray.length); 
		//		j = ObjectArrayImager.relativeIntCoord(relativeJ, objArray[0].length);

		//		return new int[] {i, j};
	}


	@Override public void setCurrentSelectedArrayCoords(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }

	@Override
	public void setCurrentSelection(double relativeI, double relativeJ)
	{
		int[] coords = getArrayCoords(relativeI, relativeJ);
		currentSelectionArrayCoords = new int[] {coords[0], coords[1]};
	}

	@Override public FieldWatcher<T> getWatcher() { return this.watcher; }
	@Override public T[][] getData() { return this.objArray; }
	@Override public Class<T> getObjClass() { return getClazz(); }
	@Override public int[] getCurrentSelectedArrayCoords() { return currentSelectionArrayCoords; }
	@Override public T getCurrentSelectedObj() { return objArray[currentSelectionArrayCoords[0]][currentSelectionArrayCoords[1]]; }

	public Map<String, Boolean> getParsedBooleanFields() {
		return parsedBooleanFields;
	}

	public void setParsedBooleanFields(Map<String, Boolean> parsedBooleanFields) {
		this.parsedBooleanFields = parsedBooleanFields;
	}

	public void setWatcher(SimpleFieldWatcher<T> watcher) {
		this.watcher = watcher;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override public ColorInterpolator getInterpolator() { return ci; }
	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
}
