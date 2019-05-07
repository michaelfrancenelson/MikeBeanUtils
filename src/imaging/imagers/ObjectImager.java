package imaging.imagers;

import java.awt.Image;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;
import imaging.imagers.ArrayData.ListData;

public class ObjectImager<T>
//implements BeanImager<T>
{
	String dblFmt;
	double datMin, datMax;
	ImageMinMax img;

	private ColorInterpolator ci, booleanCI;
	private boolean 
//	transposeImg, showBoolNA, flipAxisX, flipAxisY, 
	horizLeg, legLoToHi;
	private Class<T> clazz;
	private Class<? extends Annotation> annClass;
	private Map<String, FieldWatcher<T>> watchers;
	private FieldWatcher<T> currentWatcher;
	private Map<String, Boolean> parsedBooleanFieldNames;
	private ImagerData<T> objectData;

	private void buildWatchers()
	{
		watchers = SimpleFieldWatcher.getWatcherMap(
				clazz, annClass, dblFmt, true, true);
	}

	protected void initialize(
			Class<T> clazz, Class<? extends Annotation> annClass,
			String dblFmt, String fieldName,
			ColorInterpolator ci, ColorInterpolator booleanCI,
			boolean showBoolNa, boolean transpose, 
			boolean flipX, boolean flipY, 
			int nLegendSteps, boolean loToHi, boolean horiz,
			Map<String, Boolean> parsedBooleanFields)
	{
		this.clazz = clazz;
		this.annClass = annClass;
		this.dblFmt = dblFmt;
		this.ci = ci; this.booleanCI = booleanCI;
//		this.showBoolNA = showBoolNa;
//		this.transposeICmg = transpose;
//		this.flipAxisX = flipX;
//		this.flipAxisY = flipY;
		this.legLoToHi = loToHi;
		this.horizLeg = horiz;
		buildWatchers();
		setField(fieldName.toLowerCase());
	}

//	@Override 
	public void setDblFmt(String fmt) 
	{
		this.dblFmt = fmt; 
		for (String key : watchers.keySet())
		{
			FieldWatcher<T> w = watchers.get(key);
			w.setDblFmt(fmt);
			((SimpleFieldWatcher<T>) w).buildGetters();
		}
	}

	void buildImage()
	{
		ColorInterpolator interp;
		if ((parsedBooleanFieldNames != null) && 
				(parsedBooleanFieldNames.containsKey(
						currentWatcher.getFieldName().toLowerCase())))
			interp = booleanCI;
		else interp = ci;

		img = ImageFactory.buildPackageImage(objectData, interp, currentWatcher);
	}

//	@Override 
	public Image getImage() { return img.getImg(); }
//	@Override
	public String getFieldName() { return currentWatcher.getFieldName(); }
	public String getFieldType() { return currentWatcher.getField().getType().getSimpleName(); }
//	@Override 
	public void setField(String fieldName) { 
		this.currentWatcher = watchers.get(fieldName.toLowerCase()); refresh(); } 
//	@Override 
	public void refresh() { buildImage(); }

	public void setData(ImagerData<T> dat)
	{
		objectData = dat;
//		if (dat instanceof ArrayData)
//			this.objectData = (ArrayData<T>) dat;
//		else if (dat instanceof ListData)
//			this.objectData = (ListData<T>) dat;
//		else throw new IllegalArgumentException("Input data type not yet implemented");
	}

//	public void setData(T[][] dat) { this.objectData = new ArrayData<T>(dat, flipAxisX, flipAxisY, transposeImg); }
//	public void setData(List<List<T>> dat) { this.objectData = new ListData<T>(dat, flipAxisX, flipAxisY, transposeImg); }

	
//	@Override 
	public int getDataWidth() { return objectData.getWidth(); }
//	@Override 
	public int getDataHeight() { return objectData.getHeight(); }
	
//	@Override 
	public void setField(Field field) { setField(field.getName()); }



//	@Override
	public String queryData(double relativeI, double relativeJ) 
	{
		return objectData.queryData(relativeI, relativeJ, currentWatcher);
	}

//	@Override
	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ);
	}

//	@Override public FieldWatcher<T> getWatcher() { return currentWatcher; }
//	@Override public ColorInterpolator getInterpolator() { return ci; }
//	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
//	@Override public void setInterpolator(ColorInterpolator ci) { this.ci = ci; }
//	@Override public void setBooleanInterpolator(ColorInterpolator ci) { booleanCI = ci; }
//	@Override public Class<T> getObjClass() { return clazz; }
//	@Override public Class<? extends Annotation> getAnnClass() { return annClass; }
//	@Override public ImagerData<T> getImgData() { return this.objectData; }
	//	@Override public Field getCurrentField() { return currentWatcher.getField(); }
//	@Override public void setColors(Color[] colors) { ci.updateColors(colors); }
}