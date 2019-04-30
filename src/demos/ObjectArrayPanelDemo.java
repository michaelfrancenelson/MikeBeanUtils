package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import beans.sampleBeans.TerrainBean;
import image.arrayImager.ObjectImager;
import image.arrayImager.BeanImager;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectImagePanel;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import utils.ColorUtils;

public class ObjectArrayPanelDemo
{

	static List<ObjectImagePanel<?>> panelList;
	static TerrainBean[][] cells;
	static JFrame f1, f2;
	static ObjectImagePanel<TerrainBean> objPan, objPan2;
	static BeanImager<TerrainBean> imagerAge, imagerElev;
	static Color[] gradCols = ColorUtils.TOPO_COLORS;
	static Color[] boolCols = ColorUtils.YELLOWS;

	static Font font = new Font("times", 4, 60);
	
	static int constX, constY, nLegendSteps;
	static boolean horz, lToH, aspRat;
	static Double naDouble;
	static Integer naInt;
	static Color naCol;
	static double annPtSize;

	public static void setup(int width, int height, double elevGradient, int ageMod)
	{
		cells = TerrainBean.factory(width, height, elevGradient, ageMod);
		constX = -1; constY = -1; nLegendSteps = 100;
		horz = true; lToH = true; aspRat = false;
		annPtSize = 1.2;
		nLegendSteps = 10;
	}

	public static void main(String[] args) 
	{
		String dblFmt = "%.2f";
		System.out.println(String.format(dblFmt, 9.556611));		
		demoFactoryBuilders(15, 31, 40.5, true, false);
	}

	public static void demoFactoryBuilders(int width, int height, double sizeFactor,
			boolean show, boolean save)
	{

		List<String> names = new ArrayList<>();
		List<ObjectImagePanel<TerrainBean>> panels = new ArrayList<>();
		List<JFrame> frames = new ArrayList<>();
		names.add("ObjectArrayPanelDemo_imager_specified_builder");
		names.add("ObjectArrayPanelDemo_fully_specified_builder");
		names.add("ObjectArrayPanelDemo_factory_builder_legend");
		names.add("ObjectArrayPanelDemo_factory_builder_legend");
		setup(width, height, 0.5, Integer.MAX_VALUE);
		
//		imagerAge = ArrayImager.factory(
//				TerrainBean.class, cells, "age",
//				gradCols, boolCols,
//				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
//				"%.2f",  null,
//				true,
//				false, false, false,
//				nLegendSteps, lToH, horz
//				);
//
//		panels.add(ObjectArrayPanelFactory.buildPanel(
//				imagerAge, "elevation",
//				aspRat, constX, constY, annPtSize
//				));


		panels.add(ObjectArrayPanelFactory.buildPanel(
				TerrainBean.class, cells, "elevation",
				gradCols, boolCols,
				null, null, null, 
				null, null,
				true,
				false, false, false,
				nLegendSteps,
				lToH, horz,
				aspRat, 
				constX, constY,
				annPtSize));
		panels.add(panels.get(0).getLegendPanel());
//		panels.add(panels.get(1).getLegendPanel());

		
		JFrame controlFrame = SwingUtils.frameFactory(300, 300, "control panel", 1, 1);
		controlFrame.add(panels.get(0).getControlComboBox(font));
		
		for (int i = 0; i < panels.size(); i++)
		{
			frames.add(SwingUtils.frameFactory((int) (width * sizeFactor) , (int) (height * sizeFactor), names.get(i)));
			frames.get(i).setLocation((int) (width * sizeFactor * i), 100);
			frames.get(i).add(panels.get(i));
		}

		for (JFrame f : frames) f.setVisible(show);
		controlFrame.setVisible(show);
		controlFrame.setLocation((int) (width * sizeFactor * panels.size()), 100);
	}


}
