package beans.sampleBeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.builder.AnnotatedBeanReader.ParsedField;
import swing.stretchAndClick.MapAndLegendPanel.MapLayer;


public class ForestCell
{
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface FeatureGetter { public String value(); }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface BooleanFeature{};

	static Logger logger = LoggerFactory.getLogger(ForestCell.class);
	
//	protected int row, col;
	@ParsedField @MapLayer(layerName = "aspect") protected double aspect;
	@ParsedField @MapLayer(layerName = "cell contains stream?") protected boolean has_stream;
	@ParsedField @MapLayer(layerName = "distance to stream") protected double  dist_to_stream;
	@ParsedField @MapLayer(layerName = "cell contains road?") protected boolean has_road;
	protected int standAge;
	protected double harvest_cost, timber_value, political_cost;

	@ParsedField @MapLayer(layerName = "elevation") protected double elevation;
	@ParsedField @MapLayer(layerName = "slope") protected double slope;
	@ParsedField @MapLayer(layerName = "distance to road") protected double dist_to_road;

	@ParsedField @MapLayer(layerName = "Within border?") protected boolean in_border;

	/** A counter used to place cells in a temporary 'embargo' status during which they cannot be harvested. */
	protected int embargo;

	protected static Method[] methods;
	
	public static void setSlopeOutsideBorder(List<List<ForestCell>> cells)
	{
		for (List<ForestCell> l : cells)
			for (ForestCell f : l)
			{
				if (!f.in_border) f.slope = Double.MAX_VALUE;
					
			}
	}
	
}