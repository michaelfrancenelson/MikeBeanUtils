package image;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;

public class SimpleArrayImagerWithLegend<T> extends SimpleArrayImager<T>
{
	GradientLegendImager<T> legend;
	
	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,	String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeBooleanNA, int nLegendSteps, int legendDirection
			)
	{
		SimpleArrayImagerWithLegend<T> out = new SimpleArrayImagerWithLegend<T>();
		out.setClazz(clazz);
		out.ci = SimpleColorInterpolator.factory(gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
//		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naDouble, naInt, naColor);
		out.objArray = objArray;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		for (String s : out.watchers.keySet()) mp.put(s, false);
		if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
		out.setParsedBooleanFields(mp);
//		out.legend = (GradientLegendImager<T>) GradientLegendImager.factory(out, includeBooleanNA, nLegendSteps, legendDirection);
		out.setField(fieldName);

		return out;
	}

	public GradientLegendImager<T> getLegend() { return this.legend; }
	
	@Override
	public void refresh() 
	{
		buildDataArray();
		ci.updateMinMax(datMin,  datMax);
		buildImage();
		
		legend.refresh(datMin, datMax, booleanCI, ci, watcher, parsedBooleanFields);
	}
	
}
