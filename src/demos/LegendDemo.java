package demos;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.ForestSubclass;
import imaging.imagers.imagerData.ImagerData;
import swing.stretchAndClick.LegendPanel;
import swing.stretchAndClick.ObjectImagePanel;
import swing.stretchAndClick.PanelFactory;
import utils.ColorUtils;
import utils.SwingUtils;

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
		boolean legKeepAsp = false;
		loToHi = true;
		
		f1 = SwingUtils.frameFactory(width, height, "Legend demo 1", 1, 2);
		
		subForest = NetCDFObjBuilder.factory2D(
				ForestSubclass.class, ParsedField.class, filename, null, true, true);
		
		ObjectImagePanel<ForestSubclass> subPan = PanelFactory.buildPanel(
				ImagerData.build(subForest, false, false, false),
				ForestSubclass.class, ParsedField.class, 
				"elevation",
				ColorUtils.TERRAIN_COLORS, ColorUtils.GREENS,
				null, null, null,
				"%.4f", Arrays.asList("has_stream"),
				true, 
				0, 0, ptSize);
				
		int nLegLabels = 5;
		double offset1 = 0.05, offset2 = 0.05;
		double textOffset = 0.9;
		double pointOffset = 0.85;
		double legPtSize = 0.025;
		
		LegendPanel<ForestSubclass> legendPan1 =  
				PanelFactory.buildLegendPanel(
						subPan.getImager(),
						nLegLabels, nLegSteps, legWidth, legHeight,
						offset1, offset2,
						textOffset, pointOffset,
						loToHi, horiz, legKeepAsp,
						legPtSize, font, Color.black,
						null, "%.1f", null
						);
				
				subPan.setLegend(legendPan1);
//				ObjectImagePanel<ForestSubclass> legendPan1 =  
//				subPan.buildLegendPanel(nLegSteps, legWidth, legHeight, loToHi, horiz, legKeepAsp);
		
		legendPan1.buildLegendLabels(
				nLegLabels, 
				offset1, offset2, 
				textOffset, pointOffset,
				font, Color.black, legPtSize, 
				null, "%.0f", null);
		
		
		
		JComboBox<String> cbox = subPan.getControlComboBox(font);
		
		JPanel pp = new JPanel(); pp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 0; c.gridx = 0; c.weightx = 1; c.weighty = 1;
		pp.add(subPan, c);

		c.weighty = 0; c.gridx = 0;	c.gridy = 1;
		pp.add(cbox, c);

		
		
		
		
//		f1.add(subPan);
		f1.add(pp);
		f1.add(legendPan1);
		f1.setVisible(true);
	}
}

//		PrimitiveImager<ForestSubclass> imgr = 
//				subPan.getImager().getLegendImager(nLegSteps, true, horiz);
//		PrimitiveArrayData<Object> legendDat = 
//		
//		ObjectImagePanel<ForestSubclass> legendPan2 = 
//				PanelFactory.buildPrimitivePanel(
//						imgr,
//						subPan.getImager().getFieldName(), 
//						false, legWidth, legHeight, ptSize);
//				
//				
////				subPan.getLegendPanel(nLegSteps, legWidth, legHeight);
//		
////		Imager<ForestSubclass> imgr = subPan.getImager();
////		
////		ObjectImagePanel<ForestSubclass> legendPan2 =
////				PanelFactory.buildLegendPanel(
////				nLegSteps, imgr.getDataMin(), imgr.getDataMax(),
////				imgr.getFieldType(),
////				imgr.getDblFmt(),
////				imgr.getColorInterpolator(),
////				imgr.getBooleanColorInterpolator(),
////				true, true, true,
////				false, legWidth, legHeight, ptSize);
////				
////		