package imaging.imagers;

import java.awt.Image;
import java.util.List;

import imaging.colorInterpolator.ColorInterpolator;

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
}
