package demos;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;

import beans.sampleBeans.TerrainBean;
import image.ColorUtils;
import image.ObjectArrayImager;
import image.SimpleArrayImager;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayJPanel;

public class RiverDemo
{

	
	public static void main(String[] args) {
		
		Color[] bCol = new Color[] {Color.white, Color.black};
		double left = 0.503;
		double up = 0.6;
		int nRows = 3000;
		int nCols = 3000;
		int nRivers = 1500;
		
		demo1(nRows, nCols, nRivers, bCol, left, up);
		
	}
	
	public static void demo1(int nRows, int nCols, int nRivers, Color[] boolColors, double left, double up)
	{

		TerrainBean[][] cells = TerrainBean.factory(nRows, nCols, 0);
		TerrainBean.randomRivers(cells, left, up, nRivers);

		ObjectArrayJPanel<TerrainBean> p1;

		ObjectArrayImager<TerrainBean> imager1 = SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"stream", 
				ColorUtils.HEAT_COLORS, boolColors,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);

		JFrame f1 = SwingUtils.frameFactory(nRows, nCols);
		p1 = ObjectArrayJPanel.buildPanel(imager1, true, 0, 0);

		f1.setLayout(new GridLayout(1, 1));
		f1.add(p1);

		f1.setVisible(true);
		
	}
	
}
