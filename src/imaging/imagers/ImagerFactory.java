package imaging.imagers;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import beans.builder.AnnotatedBeanReader.ParsedField;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import imaging.colorInterpolator.SimpleColorInterpolator;

public class ImagerFactory 
{
	
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
			String field, Class<T> clazz, 
			Color[] gradColors, Color[] boolColors)
	{
		String dblFmt = "%.2f";
		return  ImagerFactory.factory(
				dat,
				field,
				clazz, null, 
				gradColors, boolColors,
				-Double.MAX_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt,  null
				);
	}
	
	public static <T> ObjectImager<T> factory(
			ImagerData<T> dat,
			String fieldName, 
			Class<T> clazz, Class<? extends Annotation> annClass, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields
			)
	{
		
		if (dblFmt == null) dblFmt = "%.2f";
		if (annClass == null) annClass = ParsedField.class;
		ObjectImager<T> out = new ObjectImager<T>();

		out.setData(dat);
		Map<String, Boolean> mp = new HashMap<>();
		if (!(parsedBooleanFields == null)) for (String s : parsedBooleanFields) mp.put(s.toLowerCase(), true);
		
		out.initialize(
				clazz, annClass, 
				dblFmt, fieldName,
				SimpleColorInterpolator.factory(gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt),
				SimpleBooleanColorInterpolator.factory(booleanColors, naColor),
				mp);
		return out;
	}

	
	public static <T> PrimitiveImager primitiveFactory(
			PrimitiveArrayData<T> dat,
			ColorInterpolator ci,
			String dblFmt
			)
	{
		if (dblFmt == null) dblFmt = "%.2f";
		PrimitiveImager out = new PrimitiveImager();
		out.setData(dat);
		out.initialize(dblFmt, ci);
		return out;
	}
}