package com.github.michaelfrancenelson.mikebeansutils.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/** Some utilities for working with Java colors.
 * 
 * @author michaelfrancenelson
 *
 *
 * Use the code in R to make new color gradients:

	require(clipr)
 	# Color code formatter
 	makeJavaHexColorStrings = function(colCodes)
	{
  		colStrings = rgb(t(col2rgb(colCodes)), max = 255)
  		cols = paste0(
	    	"new String[] {\"",
	    	paste0(colStrings, collapse = "\", \""),
	    	"\"};"
	  	)
	  (cat(cols))
	  write_clip(cols)
  	}
	# Example with terrain colors
	makeJavaHexColorStrings(terrain.colors(10))
 *
 *
 */
public class ColorUtils {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ColorGradient{ String name();};
	
	public static final int RGB_TYPE = BufferedImage.TYPE_3BYTE_BGR;
	
	public static enum Palettes
	{
		TERRAIN, TOPO, HEAT, RAINBOW, GRAYS, GREENS, BLUES, REDS, YELLOWS, CM_COLORS;
		public static Color[] palette(Palettes chooser)
		{
			switch(chooser) 
			{
			case TERRAIN: return TERRAIN_COLORS;
			case BLUES:   return ColorUtils.BLUES;
			case CM_COLORS: return ColorUtils.CM_COLORS;
			case GRAYS: return ColorUtils.GRAYS;
			case GREENS: return ColorUtils.GREENS;
			case HEAT: return ColorUtils.HEAT_COLORS;
			case RAINBOW: return ColorUtils.RAINBOW;
			case REDS: return ColorUtils.REDS;
			case TOPO: return ColorUtils.TOPO_COLORS;
			case YELLOWS: return ColorUtils.YELLOWS;
			default: return null;
			}
		}
	}
	
	
	/* makeJavaHexColorStrings(terrain.colors(10)) */
	@ColorGradient(name = "terrain") public static final Color[]
			TERRAIN_COLORS = webToColor(new String[]{"#00A600", "#2DB600", "#63C600", "#A0D600", "#E6E600", "#E8C32E", "#EBB25E", "#EDB48E", "#F0C9C0", "#F2F2F2"});

	/* makeJavaHexColorStrings(heat.colors(10)) */
	@ColorGradient(name = "heat") public static final Color[]
			HEAT_COLORS = webToColor(new String[]{"#FF0000", "#FF2400", "#FF4900", "#FF6D00", "#FF9200", "#FFB600", "#FFDB00", "#FFFF00", "#FFFF40", "#FFFFBF"});

	@ColorGradient(name = "grays") public static final Color[]
			GRAYS = webToColor(new String[]{"#4D4D4D", "#6C6C6C", "#838383", "#969696", "#A7A7A7", "#B5B5B5", "#C3C3C3", "#CFCFCF", "#DBDBDB", "#E6E6E6"});

	@ColorGradient(name = "greens") public static final Color[]
			GREENS = webToColor(new String[]{"#F7FCF5", "#E5F5E0", "#C7E9C0", "#A1D99B", "#74C476", "#41AB5D", "#238B45", "#006D2C", "#00441B"});

	/* makeJavaHexColorStrings(topo.colors(10)) */
	@ColorGradient(name = "topo") public static final Color[]
			TOPO_COLORS = webToColor(new String[]{"#4C00FF", "#0019FF", "#0080FF", "#00E5FF", "#00FF4D", "#4DFF00", "#E6FF00", "#FFFF00", "#FFDE59", "#FFE0B3"});

	/* makeJavaHexColorStrings(rainbow(12)) */
	@ColorGradient(name = "rainbow") public static final Color[]
			RAINBOW = webToColor(new String[] {"#FF0000", "#FF8000", "#FFFF00", "#80FF00", "#00FF00", "#00FF80", "#00FFFF", "#0080FF", "#0000FF", "#8000FF", "#FF00FF", "#FF0080"});

	/* makeJavaHexColorStrings(cm.colors(10)) */
	@ColorGradient(name = "cm colors") public static final Color[]
			CM_COLORS = webToColor(new String[] {"#80FFFF", "#99FFFF", "#B3FFFF", "#CCFFFF", "#E6FFFF", "#FFE6FF", "#FFCCFF", "#FFB3FF", "#FF99FF", "#FF80FF"});

	/* makeJavaHexColorStrings(c(rgb(20, 0, 0, max = 255), rgb(10, 0, 0, max = 255))) */
	@ColorGradient(name = "reds") public static final Color[]
			REDS = webToColor(new String[] {"#140000", "#0A0000"});

	/* makeJavaHexColorStrings(c(rgb(100, 100, 0, max = 255), rgb(240, 240, 0, max = 255))) */
	@ColorGradient(name = "yellows") public static final Color[]
			YELLOWS = webToColor(new String[] {"#646400", "#F0F000"});

	/* makeJavaHexColorStrings(c(rgb(0, 0, 20, max = 255), rgb(0, 0, 255, max = 255))) */
	@ColorGradient(name = "blues") public static final Color[]
			BLUES = webToColor(new String[] {"#000014", "#0000FF"});

	/** 
	 * 
	 * @return A map of color gradients, indexed by their @ColorGradient name attribute.
	 */
	public static Map<String, Color[]> getGradientMap()
	{
		Map<String, Color[]> out = new HashMap<>();
		Field[] fields = ColorUtils.class.getFields();

		for (Field f : fields)
		{
			if (f.getAnnotation(ColorGradient.class) != null)
			{
				try {
					String name = f.getAnnotation(ColorGradient.class).name();
					out.put(name, (Color[])f.get(null));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return out;
	}

	/**
	 * 
	 * @param ints
	 * @return
	 */
	public static Color[] intsToRGBColors(int... ints)
	{
		Color[] out = new Color[ints.length];
		for (int i = 0; i < out.length; i++) 
			out[i] = new Color(ints[i]);
		return out;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	static int countBits(int number) 
	{  
		return (int)(Math.log(Math.abs(number)) /  
				Math.log(2) + 1); 
	} 

	/** Parse hexadecimal color codes to Java Color objects.
	 * 
	 * @param args
	 * @return
	 */
	public static Color[] webToColor(String... args)
	{
		Color[] out = new Color[args.length];
		for (int i = 0; i < out.length; i++) {
			String st = args[i].replace("#", "");
			int int1 = Integer.parseInt(st, 16);
			out[i] = new Color(int1);
		}
		return out;
	}
}
