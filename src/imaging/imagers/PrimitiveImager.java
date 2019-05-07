package imaging.imagers;

import java.awt.Image;

import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;

public class PrimitiveImager
{
	
	private ColorInterpolator ci;
	private String currentFieldName;
	int dataWidth, dataHeight;
	String dblFmt;
	
	private PrimitiveArrayData<?> data;
	ImageMinMax img;

	void buildImage()
	{
		img = ImageFactory.buildPrimitiveImage(data, ci);
	}
	
	public String queryData(double relativeI, double relativeJ) 
	{
		return data.queryData(relativeI, relativeJ);
	}

	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ);
	}
	
	protected void initialize(String dblFmt, ColorInterpolator ci)
	{
		this.dblFmt = dblFmt;
		this.ci = ci;
		this.dataWidth = data.getWidth(); 
		this.dataHeight = data.getHeight();
		buildImage();
	}
	
	public Image getImage() { return img.getImg(); }	
	public ColorInterpolator getInterpolator() { return ci; };
	void setInterpolator(ColorInterpolator ci) { this.ci = ci; }
	void setDblFmt(String fmt) { this.dblFmt = fmt; }

	public PrimitiveArrayData<?> getImgData() { return data; }

	public void setData(PrimitiveArrayData<?> dat) { this.data = dat; }
	
	public String getCurrentFieldName() { return currentFieldName; }
}
