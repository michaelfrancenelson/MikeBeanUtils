package imaging.imagers.imagerData;

import java.util.List;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.imagerData.ArrayImagerData.ListImagerData;

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
	
//	public PrimitiveImagerData<T> getLegendData(int nSteps, boolean loToHi, boolean horiz);
//	{
//		
//		/* Java's image origin coordinate is at the upper left of the screen.
//		 * For vertical legends, high y-coordinates appear lower on the screen
//		 */
//		
//		PrimitiveImagerData<T> legDat;
//		String type = getFieldType();
//
//		ImagerData<T> dat = getImagerData();
//
//		double min = dat.getDataMin();
//		double max = dat.getDataMax();
//		
//		double endpoint1, endpoint2;
//		
//		if (!horiz) loToHi = !loToHi;
//		if (loToHi) { endpoint1 = min; endpoint2 = max; }
//		else {endpoint1 = max; endpoint2 = min; }
//		
//		if (getParsedBooleanFields().contains(getFieldName().toLowerCase())) {
//			legDat = PrimitiveImagerData.buildGradientData(
//					"Boolean", 
//					endpoint1, endpoint2,
//					nSteps, horiz,
//					true);
//			legDat.setAsBoolean(true);
//		}
//		else
//			legDat = PrimitiveImagerData.buildGradientData(
//					type,
//					endpoint1, endpoint2,
//					nSteps, horiz, 
//					false);
//
//		legDat.setDataMinMax(null, null);
//		return legDat;
//	}
	
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
