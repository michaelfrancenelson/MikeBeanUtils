package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.PrimitiveImageFactory.SimpleImagePanel;
import imaging.imagers.BeanImager;
import imaging.imagers.ImagerFactory;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;

public class ListImageDemo 
{

	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};
	static SimpleImagePanel iPan;
	static ObjectImagePanel<AllFlavorBean> objPan;
	static ColorInterpolator c;

	static int nRows;
	static int nCols;

	static Color[] gradCols = ColorUtils.TOPO_COLORS;
	static Color[] boolCols = ColorUtils.YELLOWS;
	
	static Border border = BorderFactory.createLineBorder(Color.black, 2);
	static JFrame f;
	static int cellSize = 400;
	static List<SimpleImagePanel> panels;
	static List<ObjectImagePanel<AllFlavorBean>> tPanels;
	static double ptSize;
	static boolean[] test = new boolean[] {true, false};

	static BeanImager<AllFlavorBean> imager1;
	static List<List<AllFlavorBean>> beans;
	
	public static void main(String[] args) 
	{
		demo1(600, 450, true, false);
	}
	
	static void demo1(int width, int height, boolean show, boolean save)
	{
		String filename = "testData/AllFlavorBean.nc";
		beans = NetCDFObjBuilder.factory2D(AllFlavorBean.class, filename);
		
		imager1 = img(100, true, true, "boolPrim");
		objPan = ObjectArrayPanelFactory.buildPanel(
				imager1, "boolPrim", true, 0, 0, ptSize);

		f = SwingUtils.frameFactory(width, height,
				"All Flavor Bean Object List Image Demo", 1, 2);
		
		
		JFrame controlFrame = SwingUtils.frameFactory(300, 300, "control panel", 1, 1);
		controlFrame.add(objPan.getControlComboBox(font));
		
		f.add(objPan);
		f.add(objPan.getLegendPanel());
		f.setVisible(show);
		controlFrame.setVisible(show);
	}
	
	static BeanImager<AllFlavorBean> img(int nLegendSteps, 
			boolean lToH, boolean horz, String field)
	{

		return ImagerFactory.factory(
				AllFlavorBean.class,
				ParsedField.class,
				beans, null, field,
				gradCols, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				"%.4f",  null,
				true,
				false, false, false,
				nLegendSteps, lToH, horz
				);
	}

}
