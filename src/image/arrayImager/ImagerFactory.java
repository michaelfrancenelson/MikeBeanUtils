package image.arrayImager;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.memberState.SimpleFieldWatcher;
import image.colorInterpolator.SimpleBooleanColorInterpolator;
import image.colorInterpolator.SimpleColorInterpolator;

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
				clazz, beans, field,
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
		return ImagerFactory.factory(
				clazz, beans, field,
				gradColors, boolColors,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt,  null,
				true,
				false, false, false,
				nLegendSteps, lToH, horz
				);
	}
	
	public static <T> BeanImager<T> factory(
			Class<T> clazz, List<List<T>> lists,	
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
		out.dataWidth = lists.size();
		out.dataHeight = lists.get(0).size();
		out.clazz = clazz;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		out.ci = SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naColor);
		out.showBoolNA = includeNABoolean;
		out.transposeImg = transpose; out.flipAxisX = flipX; out.flipAxisY = flipY;
		out.nLegendSteps = nLegendSteps; out.legLoToHi = legLowToHi; out.horizLeg = horizLegend;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		if (!(parsedBooleanFields == null)) for (String s : parsedBooleanFields) mp.put(s, true);
		out.parsedBooleanFieldNames = mp;
		out.setField(fieldName);
		out.buildImage();
		return out;
	}
	
	public static <T> BeanImager<T> factory(
			Class<T> clazz, T[][] objArray,	String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeNABoolean,
			boolean transpose, boolean flipX, boolean flipY,
			int nLegendSteps, boolean legLowToHi, boolean horizLegend)
	{
		if (dblFmt == null)
			throw new IllegalArgumentException("Double precision format string cannot be null");
		
		ObjectImager<T> out = new ObjectImager<>();
		out.clazz = clazz; 
		out.setData(objArray);
		out.dataWidth = objArray.length;
		out.dataHeight = objArray[0].length;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		out.ci = SimpleColorInterpolator.factory(
				gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naColor);
		out.showBoolNA = includeNABoolean;
		out.transposeImg = transpose; out.flipAxisX = flipX; out.flipAxisY = flipY;
		out.nLegendSteps = nLegendSteps; out.legLoToHi = legLowToHi; out.horizLeg = horizLegend;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		if (!(parsedBooleanFields == null)) for (String s : parsedBooleanFields) mp.put(s, true);
		out.parsedBooleanFieldNames = mp;
		out.setField(fieldName);
		out.buildImage();
		return out;
	}
	
	
	
	
	
	
}
