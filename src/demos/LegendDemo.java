package demos;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import beans.sampleBeans.TerrainBean;
import image.LegendPanel;
import image.arrayImager.BeanImager;
import image.arrayImager.SimpleArrayImager;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectImagePanel;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import utils.ColorUtils;

public class LegendDemo 
{

	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};

	static JFrame f1, f2;

	public static void main(String[] args) {
		legend1();
	}

	public static void legend1()
	{

		boolean includeNABoolean = true;
		int nRow = 8, nCol = 8;

		TerrainBean[][] cells1 = TerrainBean.factory(nRow, nCol, 1.5, 13);
		TerrainBean.randomRivers(cells1, 0.32, 0.528, 0.5, 0.5, 1);
		//		TerrainBean.randomRivers(cells1, 0.52, 0.628, 0.5, 0.5, 2);
		ObjectImagePanel<TerrainBean> pan1, pan2;
		LegendPanel<TerrainBean> leg1, leg2;
		int nLegendSteps, legendDirection;

		nLegendSteps = 100;
		legendDirection = 4;

		double ptSize = 1.0 / ((double) Math.max(nRow, nCol));
		f1 = SwingUtils.frameFactory(1100, 1100, "", 1, 1);
		f2 = SwingUtils.frameFactory(1100, 1100);

		BeanImager<TerrainBean> mainImager;

		mainImager = SimpleArrayImager.factory(
				TerrainBean.class, cells1, 
				"elevation",
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null
				);

		pan1 = ObjectArrayPanelFactory.buildPanel(
				mainImager,
				true, 0, 0, ptSize);
		f1.add(pan1);
		f1.setVisible(true);
//
////		leg1 = pan1.getLegendPanel(200, 0); 
////		leg2 = pan1.getLegendPanel(0, 750);
//
//		f1.setLayout(new GridLayout(2, 2));
//		
//		
//		
//		
//		JLabel lab = new JLabel();
//
//		//		lab.setIcon(new ImageIcon(mainImager.getImage()));
//		lab.setIcon(new ImageIcon(mainImager.getImage()));
//		//		lab.setIcon(new ImageIcon(mainImager.getLegendImage()));
//
//		lab.setSize(1000, 1000);
//
//		//		JPanel pan = new JPanel();
//		//		pan.setSize(1000, 1000);
//		//		f1.setLayout(new GridLayout(1, 1));
//
//		f1.setLayoutnew GridLayout(1, 2));
//		f1.add(pan1);
//		f1.add(leg1);
//		f1.add(leg2);
//		//		f1.pack();
//		f1.setVisible(true);
//		//		f2.add(lab);
//		//		f2.setVisible(true);
//		//		pan1.getGraphics().drawImage(mainImager.getImage(), 0, 0, 1000, 1000, pan1);


	}


}
