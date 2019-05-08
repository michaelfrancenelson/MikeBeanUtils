package imaging.imagers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;

public class PrimitiveImager<T> implements Imager<T>
{

	protected ColorInterpolator ci, booleanCI;
	protected String currentFieldName;
	private String currentFieldType;
	int dataWidth, dataHeight;
	String dblFmt;

	private PrimitiveArrayData<?> imgData;
	ImageMinMax img;
	private List<String> parsedBooleanFieldNames = new ArrayList<String>();


	@Override public Image getImage() { return img.getImg(); }	
	@Override public void setData(ImagerData<T> dat) { this.imgData = (PrimitiveArrayData<T>)dat; }
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
			img = ImageFactory.buildPrimitiveImage(imgData, ci);
		else img = ImageFactory.buildPrimitiveImage(imgData, booleanCI);
	}

	@Override public String queryData(double relativeI, double relativeJ) 
	{
		return imgData.queryData(relativeI, relativeJ);
	}

	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ);
	}

	protected void initialize(
			String dblFmt, List<String> parsedBoolean,
			ColorInterpolator ci, ColorInterpolator boolCi, boolean asBoolean)
	{
		if (dblFmt == null) dblFmt = "%.2f";
		this.dblFmt = dblFmt;
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

	public PrimitiveArrayData<?> getImgData() { return imgData; }

//	public void setData(PrimitiveArrayData<?> dat) { this.data = dat; }

	public String getCurrentFieldName() { return currentFieldName; }
}
