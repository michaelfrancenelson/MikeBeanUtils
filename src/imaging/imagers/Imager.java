package imaging.imagers;

import java.awt.Image;
import java.lang.reflect.Field;
import java.util.List;

import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.imagerData.ImagerData;
import imaging.imagers.imagerData.PrimitiveImagerData;

public interface Imager<T>
{
	public void buildImage();
	public String queryData(double relativeI, double relativeJ, String intFmt, String dblFmt, String strFmt);
	public Image getImage();
	public void setImagerData(ImagerData<T> data);
	public String getFieldType();
	public String getFieldName();
	public double getDataMin();
	public double getDataMax();
	public ColorInterpolator getColorInterpolator();
	ColorInterpolator getBooleanColorInterpolator();
	public String getDblFmt();
	public List<String> getParsedBooleanFields();
	int getDataHeight();
	int getDataWidth();
	public void refresh();
	public void setField(String name);
	public void setField(Field f);
	public ImagerData<T> getImagerData();

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

	default public PrimitiveImagerData<?> getLegendData(int nSteps, boolean loToHi, boolean horiz)
	{
		PrimitiveImagerData<?> dat =
				getImagerData().getLegendData(
						nSteps, loToHi, horiz,
						getParsedBooleanFields().contains(getFieldName().toLowerCase()),
						getFieldType()
						);
		return dat;
	}

	default PrimitiveImager<?> getLegendImager(
			int nSteps, boolean loToHi, boolean horiz)
	{
		return ImagerFactory.primitiveFactory(
				getLegendData(nSteps, loToHi, horiz),
				getColorInterpolator(), getBooleanColorInterpolator(), 
				getDblFmt(), getFieldName(), getParsedBooleanFields());
	}
}
