package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import beans.sampleBeans.AllFlavorBean;
import beans.sampleBeans.TerrainBean;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.ImageFactory.SimpleImagePanel;
import imaging.imagers.BeanImager;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;

public class DemoConsts 
{
	public static Color[] gradCols = ColorUtils.TOPO_COLORS;
	public static Color[] boolCols = ColorUtils.YELLOWS;
	public static boolean[] trueFalse = new boolean[] { true, false };
	static Object[][] objArray;
	static List<List<Object>> objList;
	static JFrame f1, f2, f3, f4;
	public static Font font = new Font("times", 2, 24);
	static Color color = Color.black;
	static Border border = BorderFactory.createLineBorder(Color.black, 2);
	static String dblFmt = "%.1f";
	static int cellSize = 400;
	static double ptSize;
	static boolean save, show;
	static List<SimpleImagePanel> simplePanels;
	static List<ObjectImagePanel<?>> panels1, panels2;

	static Random r;
	
	static BeanImager<?> im1, im2;

	static SimpleImagePanel simplePan;
	static ObjectImagePanel<?> objPan;
	
	static int nLegendSteps;
	static TerrainBean[][] terrainArray;
	static List<List<TerrainBean>> terrainList;
	static List<List<AllFlavorBean>> allFlavList;

	static ColorInterpolator c;
	static boolean horiz, loToHi;

	static int nRows, nCols;
}
