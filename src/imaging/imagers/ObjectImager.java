package imaging.imagers;

import java.awt.Image;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;
import utils.FieldUtils;

public class ObjectImager<T>
{
	String dblFmt;
	double datMin, datMax;
	ImageMinMax img;

	private ColorInterpolator ci, booleanCI;
	private Class<T> clazz;
	private Class<? extends Annotation> annClass;
	private Map<String, FieldWatcher<T>> watchers;
	private FieldWatcher<T> currentWatcher;
	private Map<String, Boolean> parsedBooleanFieldNames = new HashMap<String, Boolean>();
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
//			boolean showBoolNa, boolean transpose, 
//			boolean flipX, boolean flipY, 
//			int nLegendSteps, boolean loToHi, boolean horiz,
			Map<String, Boolean> parsedBooleanFields)
	{
		if (annClass == null) annClass = ParsedField.class;
		if (dblFmt == null) dblFmt = "%0.2f";
		this.clazz = clazz;
		this.annClass = annClass;
		this.dblFmt = dblFmt;
		this.ci = ci; this.booleanCI = booleanCI;
		this.parsedBooleanFieldNames = parsedBooleanFields;
		buildWatchers();
		setField(fieldName.toLowerCase());
		
//		this.legLoToHi = loToHi;
//		this.horizLeg = horiz;
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
		if ((parsedBooleanFieldNames.containsKey(currentWatcher.getFieldName().toLowerCase())))
			interp = booleanCI;
		else interp = ci;

		img = ImageFactory.buildPackageImage(objectData, interp, currentWatcher);
	}

	public Image getImage() { return img.getImg(); }
	public String getFieldName() { return currentWatcher.getFieldName(); }
	public String getFieldType() { return currentWatcher.getField().getType().getSimpleName(); }
	public void setField(String fieldName) { 
		this.currentWatcher = watchers.get(fieldName.toLowerCase()); refresh(); } 
	public void refresh() { buildImage(); }

	public void setData(ImagerData<T> dat)
	{
		objectData = dat;
	}

	public int getDataWidth() { return objectData.getWidth(); }
	public int getDataHeight() { return objectData.getHeight(); }
	public void setField(Field field) { setField(field.getName()); }

	public String queryData(double relativeI, double relativeJ) 
	{
		String val = objectData.queryData(relativeI, relativeJ, currentWatcher); 
		if (parsedBooleanFieldNames.containsKey(
				currentWatcher.getFieldName().toLowerCase()))
			val = FieldUtils.toBoolean(val);
		return val;
	}

	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ);
	}
}