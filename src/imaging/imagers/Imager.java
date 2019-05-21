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
	public String queryData(double relativeI, double relativeJ);
	public Image getImage();
	public void setData(ImagerData<T> data);
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
	
	default PrimitiveImagerData<T> getLegendData(
			int nSteps, boolean loToHi, boolean horiz)
	{
		PrimitiveImagerData<T> legDat;
		String type = getFieldType();
		
		ImagerData<T> dat = getImagerData();
		
		switch(type.toLowerCase())
		{
		case("int"): legDat = dat.getIntLegendData(nSteps, loToHi, horiz); break;
		case("byte"): legDat = dat.getByteLegendData(nSteps, loToHi, horiz);
		default: legDat = dat.getDoubleLegendData(nSteps, loToHi, horiz);
		}
		
		legDat.setDataMinMax(null, null);
		return legDat;
	}
	
	default PrimitiveImager<T> getLegendImager(
			int nSteps, boolean loToHi, boolean horiz)
	{
		return ImagerFactory.primitiveFactory(
				getLegendData(nSteps, loToHi, horiz),
				getColorInterpolator(), getBooleanColorInterpolator(), 
				getDblFmt(), getFieldName(), getParsedBooleanFields());
	}
}
