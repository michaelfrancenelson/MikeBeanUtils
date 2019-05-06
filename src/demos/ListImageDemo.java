package demos;

import java.awt.Color;

import javax.swing.JFrame;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import imaging.imagers.BeanImager;
import imaging.imagers.ImagerFactory;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayPanelFactory;

public class ListImageDemo extends DemoConsts
{

	public static void main(String[] args) 
	{
		cellSize = 400;
		demo1(1600, 1450, true, false);
	}
	
	static void demo1(int width, int height, boolean show, boolean save)
	{
		String filename = "testData/AllFlavorBean.nc";
		allFlavList = NetCDFObjBuilder.factory2D(AllFlavorBean.class, filename);
		
		im1 = img(100, true, true, "boolPrim");
		objPan = ObjectArrayPanelFactory.buildPanel(
				im1, "boolPrim", true, 0, 0, ptSize);

		f1 = SwingUtils.frameFactory(width, height,
				"All Flavor Bean Object List Image Demo", 1, 2);
		
		
		JFrame controlFrame = SwingUtils.frameFactory(300, 300, "control panel", 1, 1);
		controlFrame.add(objPan.getControlComboBox(font));
		
		f1.add(objPan);
		f1.add(objPan.getLegendPanel());
		f1.setVisible(show);
		controlFrame.setVisible(show);
	}
	
	static BeanImager<AllFlavorBean> img(int nLegendSteps, 
			boolean lToH, boolean horz, String field)
	{

		return ImagerFactory.factory(
				AllFlavorBean.class,
				ParsedField.class,
				allFlavList, null, field,
				gradCols, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				"%.4f",  null,
				true,
				false, false, false,
				nLegendSteps, lToH, horz
				);
	}

}
