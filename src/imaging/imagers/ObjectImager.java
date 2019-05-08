package imaging.imagers;

import java.awt.Image;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;
import utils.FieldUtils;

public class ObjectImager<T> implements Imager<T>
{
	private String dblFmt;
	double datMin, datMax;
	ImageMinMax img;

	protected ColorInterpolator ci;
	protected ColorInterpolator booleanCI;
	private Class<T> clazz;
	private Class<? extends Annotation> annClass;
	private Map<String, FieldWatcher<T>> watchers;
	private FieldWatcher<T> currentWatcher;
	private List<String> parsedBooleanFieldNames = new ArrayList<>();
	private ImagerData<T> imgData;

	private void buildWatchers()
	{
		watchers = SimpleFieldWatcher.getWatcherMap(
				clazz, annClass, getDblFmt(), true, true);
	}

	protected void initialize(
			Class<T> clazz, Class<? extends Annotation> annClass,
			String dblFmt, String fieldName,
			ColorInterpolator ci, ColorInterpolator booleanCI,
			List<String> parsedBooleanFields)
	{
		if (annClass == null) annClass = ParsedField.class;
		if (dblFmt == null) dblFmt = "%0.2f";
		this.clazz = clazz;
		this.annClass = annClass;
		this.setDblFmt(dblFmt);
		this.ci = ci; this.booleanCI = booleanCI;
		this.parsedBooleanFieldNames = parsedBooleanFields;
		buildWatchers();
		setField(fieldName.toLowerCase());
	}

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

	@Override public void buildImage()
	{
		ColorInterpolator interp;
		if ((parsedBooleanFieldNames.contains(currentWatcher.getFieldName().toLowerCase())))
			interp = booleanCI;
		else interp = ci;

		img = ImageFactory.buildPackageImage(imgData, interp, currentWatcher);
	}

	@Override public List<String> getParsedBooleanFields() { return this.parsedBooleanFieldNames; }
	@Override public Image getImage() { return img.getImg(); }
	@Override public void setData(ImagerData<T> dat) { imgData = dat; }
	@Override public int getDataWidth() { return imgData.getWidth(); }
	@Override public int getDataHeight() { return imgData.getHeight(); }
	@Override public double getDataMin() { return imgData.getDataMin(); }
	@Override public double getDataMax() { return imgData.getDataMax(); }
	@Override public String getDblFmt() { return dblFmt;}
	@Override public ColorInterpolator getColorInterpolator() { return this.ci; }
	@Override public ColorInterpolator getBooleanColorInterpolator() { return this.booleanCI; }

	
	@Override public String queryData(double relativeI, double relativeJ) 
	{
		String val = imgData.queryData(relativeI, relativeJ, currentWatcher); 
		if (parsedBooleanFieldNames.contains(currentWatcher.getFieldName().toLowerCase()))
			val = FieldUtils.toBoolean(val);
		return val;
	}
	
	public String getFieldName() { return currentWatcher.getFieldName(); }
	public String getFieldType() { return currentWatcher.getField().getType().getSimpleName(); }
	public void setField(String fieldName) { 
		this.currentWatcher = watchers.get(fieldName.toLowerCase()); refresh(); } 
	public void refresh() { buildImage(); }


	public void setField(Field field) { setField(field.getName()); }
	

	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryData(relativeI, relativeJ);
	}
}