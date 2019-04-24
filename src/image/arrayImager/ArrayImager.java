package image.arrayImager;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import image.colorInterpolator.ColorInterpolator;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;
import image.imageFactories.PrimitiveImageFactory;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;
import utils.Sequences;

public class ArrayImager<T> implements ObjectArrayImager<T>
{
	int rgbType = BufferedImage.TYPE_3BYTE_BGR;
	ColorInterpolator ci, booleanCI;

	Map<String, FieldWatcher<T>> watchers;
	FieldWatcher<T> currentWatcher;
	Map<String, Boolean> parsedBooleanFieldNames;

	T[][] objArray;
	int[] currentSelectionArrayCoords;
	Class<T> clazz;

	IntArrayMinMax legDatInt;
	DblArrayMinMax legDatDbl;
	ByteArrayMinMax legDatByte;

	int legDatWidth, legDatHeight;

	Image img, legendImg;

	Boolean[][] legDatBool = null;
	double datMin, datMax;
	
	String dblFmt;

	boolean transposeImg, showBoolNA, flipAxisX, flipAxisY, horizLeg, legLoToHi;
	int nLegendSteps;

	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,	String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean,
			boolean transpose, boolean flipX, boolean flipY,
			int nLegendSteps, boolean legLowToHi, boolean horizLegend)
	{
		ArrayImager<T> out = new ArrayImager<>();
		out.clazz = clazz; out.objArray = objArray;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		out.ci = SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naColor);
		out.showBoolNA = includeNABoolean;
		out.transposeImg = transpose; out.flipAxisX = flipX; out.flipAxisY = flipY;
		out.nLegendSteps = nLegendSteps; out.legLoToHi = legLowToHi; out.horizLeg = horizLegend;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		if (!(parsedBooleanFields == null)) for (String s : parsedBooleanFields) mp.put(s, true);
		out.parsedBooleanFieldNames = mp;
		out.setField(fieldName);
		out.buildImage();
		return out;
	}

	void buildImage()
	{
		ColorInterpolator interp;
		if (parsedBooleanFieldNames.containsKey(currentWatcher.getFieldName()))
			interp = booleanCI;
		else interp = ci;

		switch (currentWatcher.getField().getType().getSimpleName())
		{
		case("int"):
		{
			legDatDbl = null; legDatByte = null;
			IntArrayMinMax dat = currentWatcher.getIntVal(objArray);
			int legMin = dat.getMin(), legMax = dat.getMax();
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);
			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("double"):
		{
			legDatInt = null; legDatByte = null;
			DblArrayMinMax dat = currentWatcher.getDoubleVal(objArray);
			double legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { double t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatDbl = new DblArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatDbl.getDat().length; legDatHeight = legDatDbl.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatDbl.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("byte"):
		{
			legDatInt = null; legDatDbl = null; 
			ByteArrayMinMax dat = currentWatcher.getByteVal(objArray);
			byte legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { byte t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatByte = new ByteArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatByte.getDat().length; legDatHeight = legDatByte.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatByte.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			break;
		}
		case("boolean"):
		{
			boolean[][] dat = currentWatcher.getBoolVal(objArray);

			img = PrimitiveImageFactory.buildImage(dat, interp, flipAxisX, flipAxisY, transposeImg);

			legDatBool = Sequences.booleanGradient2D(showBoolNA, horizLeg);
			legDatWidth = legDatBool.length; legDatHeight = legDatBool[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatBool, interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		}
	}

	@Override public Image getImage() { return img; }
	@Override public Image getLegendImage()
	{
		return legendImg; 
	}

	@Override public String getCurrentFieldName() { return getCurrentField().getName(); }
	@Override public Field getCurrentField() { return currentWatcher.getField(); }
	@Override public void setColors(Color[] colors) { ci.updateColors(colors); }
	@Override public void setField(String fieldName) 
	{ 
		this.currentWatcher = watchers.get(fieldName); 
		refresh();
	} 
	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public FieldWatcher<T> getWatcher() { return currentWatcher; }
	@Override public T[][] getData() { return objArray; }
	@Override public ColorInterpolator getInterpolator() { return ci; }
	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
	@Override public void setCurrentSelectedArrayCoords(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }
	@Override public int[] getCurrentSelectedArrayCoords() { return currentSelectionArrayCoords; }
	@Override public Class<T> getObjClass() { return clazz; }

	@Override public void refresh() { buildImage(); }

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

	@Override public String queryObjectAt(int i, int j) { return currentWatcher.getStringVal(getObjAt(i, j)); }

	@Override
	public T getCurrentSelectedObj() 
	{ return objArray[currentSelectionArrayCoords[0]][currentSelectionArrayCoords[1]]; }

	@Override
	public void setCurrentSelection(double relativeI, double relativeJ) 
	{ currentSelectionArrayCoords = getArrayCoords(relativeI, relativeJ); }

	@Override
	public int[] getArrayCoords(double relativeI, double relativeJ) 
	{
		if (transposeImg) { double t = relativeI; relativeJ = relativeI; relativeI = t; }
		if (flipAxisX) relativeI = 1.0 - relativeI;
		if (flipAxisY) relativeJ = 1.0 - relativeJ;
		return ObjectArrayImager.getObjArrayCoords(
				relativeI, relativeJ, objArray.length, objArray[0].length);
	}

	@Override
	public String queryLegendAt(double relativeI, double relativeJ) 
	{
		int[] xy = ObjectArrayImager.getObjArrayCoords(relativeI, relativeJ, legDatWidth, legDatHeight);

		if (legDatInt != null)
			return "" + legDatInt.getDat()[xy[0]][xy[1]];
		else if (legDatDbl != null)
			return String.format(ci.getDoubleFmt(), legDatDbl.getDat()[xy[0]][xy[1]]);
		else if (legDatByte != null)
			return "" + legDatByte.getDat()[xy[0]][xy[1]];
		else if (legDatBool != null)
		{
			if (legDatBool[xy[0]][xy[1]] == null) return "NA";
			else if (legDatBool[xy[0]][xy[1]]) return "true";
			else return "false";
		}
		return "value not found";
	}
}
