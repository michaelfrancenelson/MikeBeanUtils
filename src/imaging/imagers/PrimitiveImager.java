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
import utils.FieldUtils;

public class PrimitiveImager<T> implements Imager<T>
{
	protected ColorInterpolator ci, booleanCI;
	protected String currentFieldName;
	private String currentFieldType;
	int dataWidth, dataHeight;
	String doublePrintFmt;

	private PrimitiveImagerData<T> imgData;
	ImageMinMax img;
	private List<String> parsedBooleanFieldNames = new ArrayList<String>();

	@Override public Image getImage() { return img.getImg(); }	
	@Override public void setImagerData(ImagerData<T> dat) { this.imgData = (PrimitiveImagerData<T>)dat; }
	@Override public List<String> getParsedBooleanFields() { return this.parsedBooleanFieldNames; }
	@Override public String getFieldName() { return this.currentFieldName; }
	@Override public String getFieldType() { return this.currentFieldType; }
	@Override public int getDataWidth()    { return imgData.getWidth(); }
	@Override public int getDataHeight()   { return imgData.getHeight(); }
	@Override public double getDataMin()   { return imgData.getDataMin(); }
	@Override public double getDataMax()   { return imgData.getDataMax(); }
	@Override public String getDblFmt()    { return doublePrintFmt;}
	@Override public ColorInterpolator getColorInterpolator() { return this.ci; }
	@Override public ColorInterpolator getBooleanColorInterpolator() { return this.booleanCI; }

	@Override public void buildImage()
	{
		if (parsedBooleanFieldNames.contains(currentFieldName))
		{
			imgData.setDataMinMax(null, booleanCI);
			img = ImageFactory.buildPrimitiveImage(imgData, booleanCI);
		}
		else
		{
			imgData.setDataMinMax(null, ci);
			img = ImageFactory.buildPrimitiveImage(imgData, ci);
		}
	}

	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ, null, null, null);
	}
	
	public String queryData(double relativeX, double relativeY, String intFmt, String dblFmt,
			String strFmt) {

		String val = imgData.queryData(relativeX, relativeY, intFmt, dblFmt, strFmt);
		
		if (parsedBooleanFieldNames.contains(currentFieldName.toLowerCase()))
			val = FieldUtils.toBoolean(val);
		
		return val;
	}

	protected void initialize(
			String dblFmt, List<String> parsedBoolean,
			ColorInterpolator ci, ColorInterpolator boolCi,
			String fieldName)
	{
		this.doublePrintFmt = dblFmt;
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
	void setDblFmt(String fmt) { this.doublePrintFmt = fmt; }

	public PrimitiveImagerData<?> getImgData() { return imgData; }

	public void updateImageData(PrimitiveImagerData<T> dat, String fieldName) 
	{
		this.imgData = dat; 
		this.currentFieldName = fieldName;
		this.dataWidth = imgData.getWidth(); 
		this.dataHeight = imgData.getHeight();
		imgData.setDataMinMax(null, booleanCI);
		buildImage();
	}

	public String getCurrentFieldName() { return currentFieldName; }
	
	@Override public void refresh() { buildImage(); }

	@Override public void setField(String name) { this.currentFieldName = name; }
	@Override public void setField(Field f) { this.currentFieldName = f.getName(); }

	@Override public ImagerData<T> getImagerData() { return imgData; }
}
