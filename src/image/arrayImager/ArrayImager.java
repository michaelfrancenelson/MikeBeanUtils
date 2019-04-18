package image.arrayImager;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;

import beans.memberState.FieldWatcher;
import image.ArrayImageFactory;
import image.colorInterpolator.ColorInterpolator;

public class ArrayImager<T> implements ObjectArrayImager<T>
{

	ColorInterpolator ci;
	ColorInterpolator booleanCI;

	Map<String, FieldWatcher<T>> watchers;
	FieldWatcher<T> currentWatcher;
	T[][] objArray;


	int[] currentSelectionArrayCoords;
	
	Class<T> clazz;
	
	int orientation1, orientation2;
	boolean transposeImg, showBoolNA;

	int rgbType = BufferedImage.TYPE_3BYTE_BGR;
	
	
	@Override
	public Image getImage() 
	{ 
		return ArrayImageFactory.buildArrayImage(
				objArray, currentWatcher, ci,
				orientation1, orientation2, transposeImg, showBoolNA);
	}

	@Override
	public Image getLegendImage() 
	{
		Image leg;
		leg = ArrayImageFactory.bui
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
	public void refresh() {
		// TODO Auto-generated method stub

	}
	@Override
	public T getObjAt(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public T getObjAt(double relativeI, double relativeJ) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String queryObjectAt(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}
	

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
