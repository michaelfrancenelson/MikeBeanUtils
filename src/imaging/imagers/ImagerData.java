package imaging.imagers;

import java.util.List;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.ArrayData.ListData;

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

	public PrimitiveArrayData<Object> getIntLegend(int nSteps, boolean loToHi, boolean horiz);
	public PrimitiveArrayData<Object> getByteLegend(int nSteps, boolean loToHi, boolean horiz);
	public PrimitiveArrayData<Object> getDoubleLegend(int nSteps, boolean loToHi, boolean horiz);

	void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci);
	Boolean[][] getBooleanLegendData(boolean includeNA, boolean horizontal);
	
	public static <T> ImagerData<T> build(T[][] arrayDat, boolean invertX, boolean invertY, boolean transpose)
	{
		return new ArrayData<T>(arrayDat, invertX, invertY, transpose);
	}
	
	public static <T> ImagerData<T> build(List<List<T>> listDat, boolean invertX, boolean invertY, boolean transpose)
	{
		return new ListData<T>(listDat, invertX, invertY, transpose);
	}
}
