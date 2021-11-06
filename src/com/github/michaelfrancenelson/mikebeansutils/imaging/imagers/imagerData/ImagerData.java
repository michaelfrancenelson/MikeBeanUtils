package com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.imagerData;

import java.util.List;

import com.github.michaelfrancenelson.mikebeansutils.beans.memberState.FieldWatcher;
import com.github.michaelfrancenelson.mikebeansutils.imaging.colorInterpolator.ColorInterpolator;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.imagerData.ArrayImagerData.ListImagerData;

public interface ImagerData <T>  
{
	public int getRGBInt(double relativeX, double relativeY, ColorInterpolator ci, FieldWatcher<T> w);
	public int getRGBInt(int x, int y, ColorInterpolator ci, FieldWatcher<T> w);
	public String queryData(double relativeX, double relativeY, FieldWatcher<T> w, String intFmt, String dblFmt, String strFmt);
	public String queryData(int x, int y, FieldWatcher<T> w, String intFmt, String dblFmt, String strFmt);
	
	public T getDataT(int x, int y);
	
	public double getDataMin();
	public double getDataMax();
	public int getDataIntMin();
	public int getDataIntMax();
	public int getWidth();
	public int getHeight();

	void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci);
	
	public PrimitiveImagerData<?> getLegendData(int nSteps, boolean loToHi, boolean horiz, boolean parsedBoolean, String type);
	
	public static <T> ImagerData<T> build(T[][] arrayDat, boolean invertX, boolean invertY, boolean transpose)
	{
		return new ArrayImagerData<T>(arrayDat, invertX, invertY, transpose);
	}
	
	public static <T> ImagerData<T> build(List<List<T>> listDat, boolean invertX, boolean invertY, boolean transpose)
	{
		return new ListImagerData<T>(listDat, invertX, invertY, transpose);
	}
	
	public String getType();
}
