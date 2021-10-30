package imaging.imagers;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import beans.builder.AnnotatedBeanReader.ParsedField;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import imaging.colorInterpolator.SimpleColorInterpolator;
import imaging.imagers.imagerData.ImagerData;
import imaging.imagers.imagerData.PrimitiveImagerData;

public class ImagerFactory 
{
	public static final String defaultDblFmt = "%.4g";
	public static final Class<ParsedField> defaultAnnClass = ParsedField.class;

	/**
	 * Build an imager with some default options:
	 * <li> axes not inverted
	 * <li> default na values/colors
	 * <li> default double format = "%.2d"
	 * <li> no parsed boolean fields
	 * <li> uses <code>ParsedField</code> as the field annotation
	 * 
	 * 
	 * @param arrayDat
	 * @param nLegendSteps
	 * @param lToH
	 * @param horz
	 * @param field
	 * @param clazz
	 * @param gradColors
	 * @param boolColors
	 * @return
	 */
	public static <T> ObjectImager<T> factory(
			ImagerData<T> dat,
			String field, 
			Class<T> clazz, 
			Color[] gradColors,
			Color[] boolColors)
	{
		return  ImagerFactory.factory(
				dat,
				field,
				clazz, null, 
				gradColors, boolColors,
//				-Double.MAX_VALUE, Integer.MIN_VALUE,
				Color.gray,
				defaultDblFmt,  null
				);
	}
	
	public static <T> ObjectImager<T> factory(
			ImagerData<T> dat,
			String fieldName, 
			Class<T> clazz, Class<? extends Annotation> annClass, 
			Color[] gradientColors, Color[] booleanColors,
//			double naDouble, int naInt,
			Color naColor,
			String dblFmt, List<String> parsedBooleanFields
			)
	{
		
		if (parsedBooleanFields == null) parsedBooleanFields = new ArrayList<String>();
		if (dblFmt == null) dblFmt = defaultDblFmt;
		if (annClass == null) annClass = defaultAnnClass;
		ObjectImager<T> out = new ObjectImager<T>();

		out.setImagerData(dat);
		
		out.initialize(
				clazz, annClass, 
				dblFmt, fieldName,
				SimpleColorInterpolator.factory(gradientColors, 0.0, 1.0, 
						naColor, dblFmt),
				SimpleBooleanColorInterpolator.factory(booleanColors, naColor),
				parsedBooleanFields);
		return out;
	}

	public static <T> PrimitiveImager<T> primitiveFactory(
			PrimitiveImagerData<T> dat,
			ColorInterpolator ci,
			ColorInterpolator booleanCI,
			String dblFmt,
			String fieldName,
			List<String> parsedBooleanFields)
	{
		if (parsedBooleanFields == null) parsedBooleanFields = new ArrayList<String>();
		if (dblFmt == null) dblFmt = defaultDblFmt;
		PrimitiveImager<T> out = new PrimitiveImager<T>();
		out.setImagerData(dat);
		out.initialize(dblFmt, parsedBooleanFields, ci, booleanCI, fieldName);
		return out;
	}
}