package demos;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;

import beans.sampleBeans.TerrainBean;
import image.ColorUtils;
import image.SimpleArrayImagerWithLegend;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayJPanel;
import swing.stretchAndClick.ObjectArrayPanelFactory;

public class LegendDemo 
{

	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};
		
	public static void main(String[] args) {
		legend1();
	}
	
	public static void legend1()
	{
		
		
		int nRow = 8, nCol = 8;
		
		TerrainBean[][] cells1 = TerrainBean.factory(nRow, nCol, 1.5);
		TerrainBean.randomRivers(cells1, 0.32, 0.528, 0.5, 0.5, 1);
//		TerrainBean.randomRivers(cells1, 0.52, 0.628, 0.5, 0.5, 2);
		ObjectArrayJPanel<TerrainBean> pan1;
		
		
		double ptSize = 1.0 / ((double) Math.max(nRow, nCol));
		JFrame f1 = SwingUtils.frameFactory(1100, 1100);
		
		pan1 = ObjectArrayPanelFactory.buildLegendPanel(
				TerrainBean.class, cells1, "elevation", 
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null,
				false, 0, 0, ptSize, false, 20, 3);
		
		
		
		
		
		f1.add(pan1);
		f1.setVisible(true);
		
		
	}
	
	
}
