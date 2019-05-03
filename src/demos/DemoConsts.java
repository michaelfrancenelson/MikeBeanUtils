package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JFrame;

import beans.sampleBeans.AllFlavorBean;
import utils.ColorUtils;

public class DemoConsts 
{
	public static Color[] gradCols = ColorUtils.TOPO_COLORS;
	public static Color[] boolCols = ColorUtils.YELLOWS;
	public static boolean[] trueFalse = new boolean[] { true, false };
	static AllFlavorBean[][] arrayDat;
	static List<List<AllFlavorBean>> listDat;
	static JFrame f1, f2, f3, f4;
	public static Font font = new Font("times", 2, 24);

}
