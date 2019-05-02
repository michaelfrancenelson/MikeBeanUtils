package demos;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.ForestCell;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;

public class ForestCellDemo 
{

	static int nRow, nCol;
	static List<List<ForestCell>> forest;
	static String filename = "testData" + File.separator + "blackHills1000m.nc";
	static JFrame f;
	static ObjectImagePanel<?> objPan;
	public static void main(String[] args) 
	{
		
		forestDemo(800, 1100, true);
	}
	
	
	static void forestDemo(int width, int height, boolean show)
	{
		
		forest = NetCDFObjBuilder.factory2D(ForestCell.class, filename);
		f = SwingUtils.frameFactory(width, height, "Forest Cell Demo");
		
		objPan = ObjectArrayPanelFactory.buildPanel(
				ForestCell.class, ParsedField.class, 
				forest, null, "elevation",
				ColorUtils.TERRAIN_COLORS, ColorUtils.GREENS,
				null, null, null, "%.4f", null,
				true, true, false, false, 100, true, false, true, -1, -1, 0.1);

		f.add(objPan);
		f.setVisible(show);
		
	}
	
	
}
