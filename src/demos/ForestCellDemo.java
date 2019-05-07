package demos;

import java.io.File;
import java.util.List;

import javax.swing.JPanel;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.ForestCell;
import imaging.imagers.ImagerData;
import swing.SwingUtils;
import swing.stretchAndClick.ImagePanelFactory;
import utils.ColorUtils;

public class ForestCellDemo extends DemoConsts
{

	static List<List<ForestCell>> forest;
	static String filename = "testData" + File.separator + "blackHills1000m.nc";

	public static void main(String[] args) 
	{

		forestDemo(800, 1100, true);
	}


	static void forestDemo(int width, int height, boolean show)
	{

		forest = NetCDFObjBuilder.factory2D(ForestCell.class, filename);
//		f1 = SwingUtils.frameFactory(width, height, "Forest Cell Demo", 2, 4);
		f1 = SwingUtils.frameFactory(width, height, "Forest Cell Demo", 2, 2);

		for (int i = 0; i < 1; i++)
			for (int j = 1; j < 2; j++)
				for (int k = 1; k < 2; k++)
				{
					JPanel pan = ImagePanelFactory.buildPanel(
							ImagerData.build(forest, trueFalse[k], trueFalse[j], trueFalse[i]),
							ForestCell.class, ParsedField.class, 
							"elevation",
							ColorUtils.TERRAIN_COLORS, ColorUtils.GREENS,
							null, null, null,
							"%.4f", null,
							true, 
							0, 0, ptSize);
					f1.add(pan);
				}
		f1.setVisible(show);
	}
}
