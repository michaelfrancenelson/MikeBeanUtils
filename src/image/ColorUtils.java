package image;

import java.awt.Color;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ColorUtils {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ColorGradient{ String name();};

	@ColorGradient(name = "terrain") public static final Color[]
			TERRAIN_COLORS = webToColor(new String[]{"#00A600", "#2DB600", "#63C600", "#A0D600", "#E6E600", "#E8C32E", "#EBB25E", "#EDB48E", "#F0C9C0", "#F2F2F2"});
	@ColorGradient(name = "heat") public static final Color[]
			HEAT_COLORS = webToColor(new String[]{"#FF0000", "#FF2400", "#FF4900", "#FF6D00", "#FF9200", "#FFB600", "#FFDB00", "#FFFF00", "#FFFF40", "#FFFFBF"});
	@ColorGradient(name = "grays") public static final Color[]
			GRAYS = webToColor(new String[]{"#4D4D4D", "#6C6C6C", "#838383", "#969696", "#A7A7A7", "#B5B5B5", "#C3C3C3", "#CFCFCF", "#DBDBDB", "#E6E6E6"});
	@ColorGradient(name = "greens") public static final Color[]
			GREENS = webToColor(new String[]{"#F7FCF5", "#E5F5E0", "#C7E9C0", "#A1D99B", "#74C476", "#41AB5D", "#238B45", "#006D2C", "#00441B"});
	@ColorGradient(name = "topo") public static final Color[]
			TOPO_COLORS = webToColor(new String[]{"#4C00FF", "#0019FF", "#0080FF", "#00E5FF", "#00FF4D", "#4DFF00", "#E6FF00", "#FFFF00", "#FFDE59", "#FFE0B3"});
	
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
	
	public static Color[] intsToRGBColors(int... ints)
	{
		Color[] out = new Color[ints.length];
		for (int i = 0; i < out.length; i++) 
			out[i] = new Color(ints[i]);
		return out;
	}

	static int countBits(int number) 
	{  
		return (int)(Math.log(Math.abs(number)) /  
				Math.log(2) + 1); 
	} 

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

	public static void _main(String[] args) {
		System.out.println(Integer.parseInt("00A600", 16));
		System.out.println(0x00A600);
		String[] terrainStrings = new String[]{"#00A600"};//, "#2DB600", "#63C600", "#A0D600", "#E6E600", "#E8C32E", "#EBB25E", "#EDB48E", "#F0C9C0", "#F2F2F2"};
		webToColor(terrainStrings);
		System.out.println(TERRAIN_COLORS[0].toString());
	}
}
