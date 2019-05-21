package imaging.imagers.imagerData;

import java.util.List;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.imagerData.ArrayImagerData.ListImagerData;
import utils.Sequences;

public interface ImagerData <T>  
{
	public int getRGBInt(double relativeX, double relativeY, ColorInterpolator ci, FieldWatcher<T> w);
	public int getRGBInt(int x, int y, ColorInterpolator ci, FieldWatcher<T> w);
	public String queryData(double relativeX, double relativeY, FieldWatcher<T> w);
	public String queryData(int x, int y, FieldWatcher<T> w);
	
	public double getDataMin();
	public double getDataMax();
	public int getWidth();
	public int getHeight();

	default PrimitiveImagerData<T> getIntLegendData(int nSteps, boolean loToHi, boolean horiz)
	{
		double[] endpoints = getLegendEndpoints(loToHi, horiz);
		int dataWidth = (int)Math.abs(endpoints[0] - endpoints[1]);
		if (dataWidth < nSteps) nSteps = dataWidth;
		int[][] data = Sequences.spacedIntervals2D(
				(int) endpoints[0], (int) endpoints[1], nSteps, horiz);
		return new PrimitiveImagerData<T>(data, false, false, false);
	}
	
	default PrimitiveImagerData<T> getByteLegendData(int nSteps, boolean loToHi, boolean horiz)
	{
		double[] endpoints = getLegendEndpoints(loToHi, horiz);
		int dataWidth = (int)Math.abs(endpoints[0] - endpoints[1]);
		if (dataWidth < nSteps) nSteps = dataWidth;
		byte[][] data = Sequences.spacedIntervals2D(
				(byte) endpoints[0], (byte) endpoints[1], nSteps, horiz);
		return new PrimitiveImagerData<T>(data, false, false, false);
	}
	
	default double[] getLegendEndpoints(boolean loToHi, boolean horiz)
	{
		/* Java image origin (x = 0, y = 0) is in upper left hand of monitor.*/
		if (!horiz) loToHi = !loToHi;
		double dataMax = getDataMax(); double dataMin = getDataMin();
		
		double legStart = Math.min(dataMin, dataMax);
		double legEnd = Math.max(dataMin, dataMax);
		
		if (!loToHi)
		{
			legStart = (int)Math.max(dataMin, dataMax);
			legEnd = (int)Math.min(dataMin, dataMax);
		}
		return new double[] { legStart, legEnd };
	}
	default PrimitiveImagerData<T> getDoubleLegendData(
			int nSteps, boolean loToHi, boolean horiz)
	{
		double[] endpoints = getLegendEndpoints(loToHi, horiz);
		int dataWidth = (int)Math.abs(endpoints[0] - endpoints[1]);
		if (dataWidth < nSteps) nSteps = dataWidth;
		double[][] data = Sequences.spacedIntervals2D(endpoints[0], endpoints[1], nSteps, horiz);
		return new PrimitiveImagerData<T>(data, false, false, false);
	}
	
	default Boolean[][] getBooleanLegendData(boolean includeNA, boolean horizontal)
	{
		Boolean[][] data = Sequences.booleanGradient2D(includeNA, horizontal);
		return data;
	}
	
	void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci);
	
	public static <T> ImagerData<T> build(T[][] arrayDat, boolean invertX, boolean invertY, boolean transpose)
	{
		return new ArrayImagerData<T>(arrayDat, invertX, invertY, transpose);
	}
	
	public static <T> ImagerData<T> build(List<List<T>> listDat, boolean invertX, boolean invertY, boolean transpose)
	{
		return new ListImagerData<T>(listDat, invertX, invertY, transpose);
	}
}
