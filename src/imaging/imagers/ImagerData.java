package imaging.imagers;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;

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
//	public void setDataMinMax(FieldWatcher<T> w);
	
//	public int[][] relativeToDataCoords(double relI, double relJ);

	public PrimitiveArrayData<Object> getIntLegend(int nSteps, boolean loToHi, boolean horiz);
	public PrimitiveArrayData<Object> getByteLegend(int nSteps, boolean loToHi, boolean horiz);
	public PrimitiveArrayData<Object> getDoubleLegend(int nSteps, boolean loToHi, boolean horiz);
//	public ImagerData<Boolean> getBooleanLegend(boolean includeNA, boolean horiz);
	
//	public IntArrayMinMax  intLegendData(int nSteps, boolean loToHi, boolean horiz);
//	public DblArrayMinMax  dblLegendData(int nSteps, boolean loToHi, boolean horiz);
//	public ByteArrayMinMax byteLegendData(int nSteps, boolean loToHi, boolean horiz);
	void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci);
	Boolean[][] getBooleanLegendData(boolean includeNA, boolean horizontal);
	
}
