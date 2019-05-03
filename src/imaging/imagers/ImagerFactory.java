package imaging.imagers;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.memberState.SimpleFieldWatcher;
import imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import imaging.colorInterpolator.SimpleColorInterpolator;

public class ImagerFactory 
{
	public static <T> BeanImager<T> quickFactory(
			List<List<T>> beans, int nLegendSteps, 
			boolean lToH, boolean horz, 
			String field, Class<T> clazz, 
			Color[] gradColors, Color[] boolColors)
	{
		String dblFmt = "%.2f";
		return ImagerFactory.factory(
				clazz,
				ParsedField.class, 
				beans, null, field,
				gradColors, boolColors,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt,  null,
				true,
				false, false, false,
				nLegendSteps, lToH, horz
				);
	}

	public static <T> BeanImager<T> quickFactory(
			T[][] beans, int nLegendSteps, 
			boolean lToH, boolean horz, 
			String field, Class<T> clazz, 
			Color[] gradColors, Color[] boolColors)
	{
		String dblFmt = "%.2f";
		return  ImagerFactory.factory(
				clazz, ParsedField.class, 
				null, beans, field,
				gradColors, boolColors,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt,  null,
				true,
				false, false, false,
				nLegendSteps, lToH, horz
				);
	}
	
	public static <T> BeanImager<T> factory(
			Class<T> clazz, 
			Class<? extends Annotation> annClass, 
			List<List<T>> lists,
			T[][] objArray,
			String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean,
			boolean transpose, boolean flipX, boolean flipY,
			int nLegendSteps, boolean legLowToHi, boolean horizLegend)
	{
		
		if (dblFmt == null)
			throw new IllegalArgumentException("Double precision format string cannot be null");
		
		ObjectImager<T> out = new ObjectImager<T>();

		if (lists != null && objArray != null) 
			throw new IllegalArgumentException("Only one of the 'lists', or 'objArray' parameters can be non-null.");
		
		if (lists != null)
		{
			/* First make sure all the lists are the same length. */
			int len = lists.get(0).size();
			for (List<T> ll : lists)
			{
				if (ll.size() != len)
					throw new IllegalArgumentException("Data rows/columns are not all the same length.");
			}
			out.setData(lists);
		}
		else out.setData(objArray);
		
		out.initialize(clazz, annClass, dblFmt);
		
		out.setInterpolator(SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt));
	
		out.setBooleanInterpolator(SimpleBooleanColorInterpolator.factory(
				booleanColors, naColor));

		out.showBoolNA = includeNABoolean;
		out.transposeImg = transpose;
		out.flipAxisX = flipX; 
		out.flipAxisY = flipY;
		out.nLegendSteps = nLegendSteps; out.legLoToHi = legLowToHi; out.horizLeg = horizLegend;
		Map<String, Boolean> mp = new HashMap<>();
		if (!(parsedBooleanFields == null)) for (String s : parsedBooleanFields) mp.put(s, true);
		out.parsedBooleanFieldNames = mp;
		out.setField(fieldName);
		return out;
	}
	
	@Deprecated
	public static <T> BeanImager<T> factory(
			Class<T> clazz, 
			Class<? extends Annotation> annClass, 
			List<List<T>> lists,	
			String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean,
			boolean transpose, boolean flipX, boolean flipY,
			int nLegendSteps, boolean legLowToHi, boolean horizLegend)
	{
		/* First make sure all the lists are the same length. */
		int len = lists.get(0).size();
		for (List<T> ll : lists)
		{
			if (ll.size() != len)
				throw new IllegalArgumentException("Data rows/columns are not all the same length.");
		}
		
		if (dblFmt == null)
			throw new IllegalArgumentException("Double precision format string cannot be null");
		
		ObjectImager<T> out = new ObjectImager<T>();
		out.setData(lists);
		out.initialize(clazz, annClass, dblFmt);
		
		out.setInterpolator(SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt));
	
		out.setBooleanInterpolator(SimpleBooleanColorInterpolator.factory(
				booleanColors, naColor));

		out.showBoolNA = includeNABoolean;
		out.transposeImg = transpose;
		out.flipAxisX = flipX; 
		out.flipAxisY = flipY;
		out.nLegendSteps = nLegendSteps; out.legLoToHi = legLowToHi; out.horizLeg = horizLegend;
		Map<String, Boolean> mp = new HashMap<>();
		if (!(parsedBooleanFields == null)) for (String s : parsedBooleanFields) mp.put(s, true);
		out.parsedBooleanFieldNames = mp;
		out.setField(fieldName);
		return out;
	}
}