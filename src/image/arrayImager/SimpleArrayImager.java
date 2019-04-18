package image.arrayImager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import image.ArrayImageFactory;
import image.colorInterpolator.ColorInterpolator;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;

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

	private double[][]  legDatDouble = null;
	private int[][]     legDatInt = null;
	private boolean[][] legDatBool = null;

	double datMin, datMax;

	BufferedImage img, legImg;

	T[][] objArray;
	SimpleFieldWatcher<T> watcher;

	Class<T> clazz;
	private int legDatDim1, legDatDim2;
	double legendMin, legendMax;
	double[] legendDoubleSequence;
	int[]    legendIntSequence;
	int legIndexMult1, legIndexMult2;
	boolean includeNABoolean, buildLegend;

	Map<String, FieldWatcher<T>> watchers;
	Map<String, Boolean> parsedBooleanFields;

	int[] currentSelectionArrayCoords;
	int nLegendSteps, nLegendStepsAdj, legendDirection;

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
		out.initImage();

		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		for (String s : out.watchers.keySet()) mp.put(s, false);
		if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
		out.setParsedBooleanFields(mp);
		out.setField(fieldName);
		return out;
	}

	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,	String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean, int nLegendSteps, int legendDirection
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

		if (nLegendSteps > 0)
		{
			out.buildLegend = true;
		}
		out.nLegendSteps = nLegendSteps;
		out.legendDirection = legendDirection;
		out.includeNABoolean = includeNABoolean;

		out.initImage();
		//		out.initLegendImage();

		out.setField(fieldName);
		out.refresh();
		return out;
	}


	private void initImage() { img = new BufferedImage(objArray.length, objArray[0].length, rgbType);}

	/**
	 * 
	 */
	void buildDataArray()
	{
		switch (getWatcher().getField().getType().getSimpleName())
		{
		case("int"): buildIntDataArray(); break;
		case("double"): buildDoubleDataArray(); break;
		case("boolean"): buildBooleanDataArray(); break;
		}

		if (buildLegend) buildLegendData();

	}

	void buildLegendData()
	{
		buildLegendSequenceDirection();
		switch (getWatcher().getField().getType().getSimpleName())
		{
		case("int"): 
		{
			legDatBool = null; legDatDouble = null;	legendDoubleSequence = null;
			int intMin = (int) legendMin;
			int intMax = (int) legendMax;
			legendIntSequence = ArrayImageFactory.spacedIntervals(intMin, intMax, nLegendStepsAdj);
			nLegendStepsAdj = legendIntSequence.length;

			buildLegendDimensions(nLegendStepsAdj);
//			System.out.println("SimpleArrayImager.buildLegendData() int min = " + intMin + " int max = " + intMax);

			legDatInt = new int[legDatDim1][legDatDim2];

			for (int i = 0; i < legendIntSequence.length; i++)
				legDatInt[i * legIndexMult1][i * legIndexMult2] = legendIntSequence[i];
			break;
		}

		case("double"): 
		{
			legDatBool = null; legDatInt = null; legendIntSequence = null;
			buildLegendDimensions(nLegendSteps + 1);
			legendDoubleSequence = ArrayImageFactory.spacedIntervals(legendMin, legendMax, nLegendSteps);
			legDatDouble = new double[legDatDim1][legDatDim2];
			for (int i = 0; i < legendDoubleSequence.length; i++)
				legDatDouble[i * legIndexMult1][i * legIndexMult2] = legendDoubleSequence[i];
			break;
			
			
		}

		case("boolean"):
		{
			legDatBool = null; legDatInt = null; legendIntSequence = null;
			if (includeNABoolean) buildLegendDimensions(3);
			else buildLegendDimensions(2);
			break;
		}
			
		}
	}

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
			int n = Math.max(legDatInt.length, legDatInt[0].length);
			for (int i = 0; i < n; i++)
			{
				row = i * legIndexMult1; col = i * legIndexMult2;
				legImg.setRGB(row, col, interp.getColor(legDatInt[row][col]));
			}
			break;
		}
		case("double"): 
		{
			for (int i = 0; i < nLegendSteps; i++)
			{
				row = i * legIndexMult1; col = i * legIndexMult2;
				legImg.setRGB(row, col, interp.getColor(legDatDouble[row][col]));
			}
			break;
		}
		case("boolean"): 
		{
			int i = 0;

			row = i * legIndexMult1; col = i * legIndexMult2;
			legImg.setRGB(row, col, booleanCI.getColor(legDatBool[row][col]));

			i++; row = i * legIndexMult1; col = i * legIndexMult2;
			legImg.setRGB(row, col, booleanCI.getColor(legDatBool[row][col]));

			if (includeNABoolean)
			{
				i++; row = i * legIndexMult1; col = i * legIndexMult2;
				legImg.setRGB(row, col, booleanCI.getNAColor());
			}
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
				val = getWatcher().getDoubleVal(objArray[row][col]);
				dataDouble[row][col] = val;
				if (val < datMin) datMin = val;
				if (val > datMax) datMax = val;
			}
//
//		if (buildLegend)
//		{
//			buildLegendSequenceDirection();
//			//			buildLegendDoubleSequence();
//			buildLegendDimensions(nLegendSteps);
//			legImg = new BufferedImage(legDatDim1, legDatDim2, rgbType);
//			legDatDouble = new double[legDatDim1][legDatDim2];
//			for (int i = 0; i < nLegendSteps; i++)
//				legDatDouble[i * legIndexMult1][i * legIndexMult2] = legendDoubleSequence[i];
//		}
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
				dataBool[row][col] = getWatcher().getBoolVal(objArray[row][col]);

//		if (buildLegend)
//		{
//			legDatDouble = null;
//			legDatInt = null;
//
//			int dim = 2;
//			legDatBool = new boolean[dim * legIndexMult1][dim * legIndexMult2];
//			legDatBool[0][0] = true;
//			legDatBool[legIndexMult1][legIndexMult2] = false;
//
//			/* Include a pixel for the na color, if needed */
//			if (includeNABoolean) dim = 3;
//			legImg = new BufferedImage(dim * legIndexMult1, dim * legIndexMult2, rgbType);
//		}
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
		for (int row = 0; row < objArray.length; row++)
			for (int col = 0; col < objArray[0].length; col++)
			{
				val = getWatcher().getIntVal(objArray[row][col]);
				dVal = (double) val;

				dataInt[row][col] = val;
				if (dVal < datMin) datMin = dVal;
				if (dVal > datMax) datMax = dVal;
			}
	}


	private void buildLegendDimensions(int nSteps)
	{
		/* Vertical legend*/
		if ((legendDirection == 1) || (legendDirection == 3))
		{ 
			legDatDim1 = nSteps; legDatDim2 = 1; 
			legIndexMult1 = 1; legIndexMult2 = 0;
		}

		/* Horizontal legend */
		else 
		{
			legDatDim2 = nSteps; legDatDim1 = 1; 
			legIndexMult2 = 1; legIndexMult1 = 0;
		}
		legImg = new BufferedImage(legDatDim1, legDatDim2, rgbType);
	}

	//	private void buildLegendIntSequence()
	//	{
	//		legDatBool = null;
	//		legDatDouble = null;
	//		legendDoubleSequence = null;
	//		nLegendStepsAdj = (int) datMax - (int) datMin + 1;
	//		nLegendStepsAdj = Math.min(nLegendStepsAdj, nLegendSteps);
	//		legendIntSequence = Sequences.spacedIntervals((int) legendMin, (int) legendMax, nLegendStepsAdj);
	//	}

	//	private void buildLegendDoubleSequence()
	//	{
	//		legDatBool = null;
	//		legDatInt = null;
	//		legendIntSequence = null;
	//
	//		legendDoubleSequence = new double[nLegendSteps];
	//		legendDoubleSequence = Sequences.spacedIntervals(legendMin, legendMax, nLegendSteps);
	//	
	//	}

	private void buildLegendSequenceDirection()
	{
		/* low to high values */
		if ((legendDirection == 1) || (legendDirection == 2))
		{ 
			legendMin = datMin;
			legendMax = datMax;

		}

		/* High to low values */
		else { legendMax = datMin; legendMin = datMax; }

	}


	@Override
	public void refresh() 
	{
		buildDataArray();
		ci.updateMinMax(datMin,  datMax);
		buildImage();

		if (buildLegend)
		{
			buildLegendSequenceDirection();
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
			return String.format("%d", legDatInt[i][j]);
		case("double"): 
			return String.format(watcher.getDblFmt(), legDatDouble[i][j]);
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


