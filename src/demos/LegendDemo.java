package demos;

import java.io.File;
import java.util.List;

import javax.swing.JComboBox;

import beans.builder.NetCDFObjBuilder;
import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.sampleBeans.ForestSubclass;
import imaging.imagers.Imager;
import imaging.imagers.PrimitiveImager;
import imaging.imagers.imagerData.ImagerData;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectImagePanel;
import swing.stretchAndClick.PanelFactory;
import utils.ColorUtils;

public class LegendDemo extends DemoConsts 
{

	static List<List<ForestSubclass>> subForest;
	static String filename = "testData" + File.separator + "blackHills1000m.nc";

	public static void main(String[] args) {

		legendDemo1(1200, 1000, 200, 200, 0);
	}

	public static void legendDemo1(
			int width, int height, int nLegSteps, int legWidth, int legHeight)
	{
		boolean horiz = false;
		f1 = SwingUtils.frameFactory(width, height, "Legend demo 1", 1, 2);
		
		subForest = NetCDFObjBuilder.factory2D(
				ForestSubclass.class, ParsedField.class, filename, null, true, true);
		
		ObjectImagePanel<ForestSubclass> subPan = PanelFactory.buildPanel(
				ImagerData.build(subForest, false, false, false),
				ForestSubclass.class, ParsedField.class, 
				"elevation",
				ColorUtils.TERRAIN_COLORS, ColorUtils.GREENS,
				null, null, null,
				"%.4f", null,
				true, 
				0, 0, ptSize);
		
		
		PrimitiveImager<ForestSubclass> imgr = 
				subPan.getImager().getLegendImager(nLegSteps, true, horiz);
		
		
//		PrimitiveArrayData<Object> legendDat = 
		
		ObjectImagePanel<ForestSubclass> legendPan2 = 
				PanelFactory.buildPrimitivePanel(
						imgr,
						subPan.getImager().getFieldName(), 
						false, legWidth, legHeight, ptSize);
				
				
//				subPan.getLegendPanel(nLegSteps, legWidth, legHeight);
		
//		Imager<ForestSubclass> imgr = subPan.getImager();
//		
//		ObjectImagePanel<ForestSubclass> legendPan2 =
//				PanelFactory.buildLegendPanel(
//				nLegSteps, imgr.getDataMin(), imgr.getDataMax(),
//				imgr.getFieldType(),
//				imgr.getDblFmt(),
//				imgr.getColorInterpolator(),
//				imgr.getBooleanColorInterpolator(),
//				true, true, true,
//				false, legWidth, legHeight, ptSize);
//				
//		
		f1.add(subPan);
		f1.add(legendPan2);
		f1.setVisible(true);
		
		
	}


}
