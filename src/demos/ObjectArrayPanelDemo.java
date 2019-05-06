package demos;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import beans.sampleBeans.TerrainBean;
import imaging.imagers.BeanImager;
import swing.SwingUtils;
import swing.stretchAndClick.ImagePanelFactory;
import swing.stretchAndClick.ObjectImagePanel;

public class ObjectArrayPanelDemo extends DemoConsts
{
	static List<ObjectImagePanel<?>> panels1;
	static BeanImager<TerrainBean> imagerAge, imagerElev;
	
	static int constX, constY;
	static boolean aspRat;

	public static void setup(int width, int height, double elevGradient, int ageMod)
	{
		terrainArray = TerrainBean.factory(width, height, elevGradient, ageMod);
		constX = -1; constY = -1; nLegendSteps = 100;
		horiz = true; loToHi = true; aspRat = false;
		ptSize = 1.2;
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
		
		panels.add(ImagePanelFactory.buildPanel(
				TerrainBean.class,
//				ParsedField.class,
				null,
				null, terrainArray, "elevation",
				gradCols, boolCols,
				null, null, null, 
				null, null,
				true,
				false, false, false,
				nLegendSteps,
				loToHi, horiz,
				aspRat, 
				constX, constY,
				ptSize));
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
