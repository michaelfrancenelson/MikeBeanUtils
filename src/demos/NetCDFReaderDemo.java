package demos;

import java.util.ArrayList;
import java.util.List;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import imaging.imagers.BeanImager;
import imaging.imagers.ImagerFactory;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import utils.FieldUtils;

public class NetCDFReaderDemo extends BeanImageDemo
{
	public static void main(String[] args) 
	{
		int width = 300;
		int height =240;
		boolean show = true;
		netCDFDemo(width, height, show);
	}

	static void netCDFDemo(int cellWidth, int cellHeight, boolean show)
	{

		allFlavList = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF);

		List<String> fieldNames = FieldUtils.getFieldNames(
						AllFlavorBean.class, 
						ParsedField.class,
						true, false, false);
		int nFields = fieldNames.size(); 
		System.out.println("NetCDFReaderDemo.netCDFDemo() n fields = " + nFields);

		int gridRows = 5; int gridCols = 3;

		f1 = SwingUtils.frameFactory(gridCols * cellWidth, gridRows * cellHeight, "NetCDF panels demo.", gridCols, gridRows);

		List<BeanImager<AllFlavorBean>> imagers = new ArrayList<>(nFields);
		for (int i = 0; i < nFields; i++)
		{
			imagers.add(ImagerFactory.quickFactory(allFlavList, null, 12, true, true, fieldNames.get(i),
					AllFlavorBean.class,
					gradCols, boolCols));
			objPan = ObjectArrayPanelFactory.buildPanel(
					imagers.get(i), fieldNames.get(i), true, 0, 0, ptSize);

			f1.add(ObjectArrayPanelFactory.buildPanel(
					imagers.get(i), fieldNames.get(i), true, 0, 0, ptSize));
		}

		f1.setVisible(show);


	}

}
