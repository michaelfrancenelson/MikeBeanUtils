package imaging.imagers;

import java.awt.Image;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;
import imaging.imagers.imagerData.ImagerData;
import imaging.imagers.imagerData.PrimitiveImagerData;

public class PrimitiveImager<T> implements Imager<T>
{
	protected ColorInterpolator ci, booleanCI;
	protected String currentFieldName;
	private String currentFieldType;
	int dataWidth, dataHeight;
	String dblFmt;

	private PrimitiveImagerData<T> imgData;
	ImageMinMax img;
	private List<String> parsedBooleanFieldNames = new ArrayList<String>();

	@Override public Image getImage() { return img.getImg(); }	
	@Override public void setData(ImagerData<T> dat) { this.imgData = (PrimitiveImagerData<T>)dat; }
	@Override public List<String> getParsedBooleanFields() { return this.parsedBooleanFieldNames; }
	@Override public String getFieldName() { return this.currentFieldName; }
	@Override public String getFieldType() { return this.currentFieldType; }
	@Override public int getDataWidth()    { return imgData.getWidth(); }
	@Override public int getDataHeight()   { return imgData.getHeight(); }
	@Override public double getDataMin()   { return imgData.getDataMin(); }
	@Override public double getDataMax()   { return imgData.getDataMax(); }
	@Override public String getDblFmt()    { return dblFmt;}
	@Override public ColorInterpolator getColorInterpolator() { return this.ci; }
	@Override public ColorInterpolator getBooleanColorInterpolator() { return this.booleanCI; }

	@Override public void buildImage()
	{
		if (parsedBooleanFieldNames.contains(currentFieldName))
			img = ImageFactory.buildPrimitiveImage(imgData, booleanCI);
		else img = ImageFactory.buildPrimitiveImage(imgData, ci);
	}

	@Override public String queryData(double relativeI, double relativeJ) 
	{
		return imgData.queryData(relativeI, relativeJ, dblFmt);
	}

	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ);
	}

	protected void initialize(
			String dblFmt, List<String> parsedBoolean,
			ColorInterpolator ci, ColorInterpolator boolCi,
			String fieldName)
	{
		this.dblFmt = dblFmt;
		this.currentFieldName = fieldName;
		this.parsedBooleanFieldNames = parsedBoolean;
		this.ci = ci;
		this.booleanCI = boolCi;
		this.dataWidth = imgData.getWidth(); 
		this.dataHeight = imgData.getHeight();
		buildImage();
	}


	public ColorInterpolator getInterpolator() { return ci; };
	void setInterpolator(ColorInterpolator ci) { this.ci = ci; }
	void setDblFmt(String fmt) { this.dblFmt = fmt; }

	public PrimitiveImagerData<?> getImgData() { return imgData; }

//	public void setData(PrimitiveArrayData<?> dat) { this.data = dat; }

	public String getCurrentFieldName() { return currentFieldName; }
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setField(String name) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setField(Field f) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ImagerData<T> getImagerData() { return imgData; }
}
