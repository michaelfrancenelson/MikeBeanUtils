package image.arrayImager;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import image.colorInterpolator.ColorInterpolator;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;
import image.imageFactories.ObjectImageFactory;

public class ArrayImager<T> implements ObjectArrayImager<T>
{

	int rgbType = BufferedImage.TYPE_3BYTE_BGR;
	ColorInterpolator ci;
	ColorInterpolator booleanCI;

	Map<String, FieldWatcher<T>> watchers;
	FieldWatcher<T> currentWatcher;
	T[][] objArray;
	int[] currentSelectionArrayCoords;
	Class<T> clazz;
	
	boolean transposeImg, showBoolNA, flipAxisX, flipAxisY, horizLeg, legLoToHi;
	int nLegendSteps;
	
	
	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,
			String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean,
			boolean transpose, boolean flipX, boolean flipY,
			int nLegendSteps, boolean legLowToHi, boolean horizLegend)
	{
		ArrayImager<T> out = new ArrayImager<>();
		out.clazz = clazz; out.objArray = objArray;
		out.ci = SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naColor);
		out.showBoolNA = includeNABoolean;
		out.transposeImg = transpose; out.flipAxisX = flipX; out.flipAxisY = flipY;
		out.nLegendSteps = nLegendSteps; out.legLoToHi = legLowToHi; out.horizLeg = horizLegend;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		
		return out;
	}
	
	
	@Override
	public Image getImage() 
	{ 
		return ObjectImageFactory.buildArrayImage(
				objArray, currentWatcher, ci,
				flipAxisX, flipAxisY, 
				transposeImg, showBoolNA);
	}

	
	
	
	@Override
	public Image getLegendImage() 
	{
		Image leg;
//		leg = ObjectImageFactory.bui
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override public String getCurrentFieldName() { return getCurrentField().getName(); }
	@Override public Field getCurrentField() { return currentWatcher.getField(); }
	@Override public void setColors(Color[] colors) { ci.updateColors(colors); }
	@Override public void setField(String fieldName) { this.currentWatcher = watchers.get(fieldName); } 
	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public FieldWatcher<T> getWatcher() { return currentWatcher; }
	@Override public T[][] getData() { return objArray; }
	@Override public ColorInterpolator getInterpolator() { return ci; }
	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
	@Override public void setCurrentSelectedArrayCoords(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }
	@Override public int[] getCurrentSelectedArrayCoords() { return currentSelectionArrayCoords; }
	@Override public Class<T> getObjClass() { return clazz; }
	

	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub

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
	
	@Override public String queryObjectAt(int i, int j) { return currentWatcher.getStringVal(getObjAt(i, j)); }

	@Override
	public T getCurrentSelectedObj() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCurrentSelection(double relativeI, double relativeJ) {
		// TODO Auto-generated method stub

	}
	@Override
	public int[] getArrayCoords(double relativeI, double relativeJ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String queryLegendAt(double relativeI, double relativeJ) {
		// TODO Auto-generated method stub
		return null;
	}







}
