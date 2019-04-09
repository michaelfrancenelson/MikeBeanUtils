package demos;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import beans.sampleBeans.TerrainBean;
import image.ColorUtils;
import image.GradientLegendImager;
import image.ObjectArrayImager;
import image.SimpleArrayImager;
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
		ObjectArrayJPanel<TerrainBean> pan1, pan2;
		int nLegendSteps, legendDirection;

		nLegendSteps = 100;
		legendDirection = 2;


		double ptSize = 1.0 / ((double) Math.max(nRow, nCol));
		JFrame f1 = SwingUtils.frameFactory(1100, 1100);
		JFrame f2 = SwingUtils.frameFactory(1100, 1100);

		GradientLegendImager<TerrainBean> legImager;
		ObjectArrayImager<TerrainBean> mainImager;
		
		mainImager = SimpleArrayImager.factory(TerrainBean.class, cells1, "elevation",
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null
				);
		
		
		
		pan1 = ObjectArrayPanelFactory.buildPanel(
				mainImager,
				true, 0, 0, ptSize);

		//		pan2 = ObjectArrayPanelFactory.getLegendPanel(
//				pan1, 
//				((SimpleArrayImagerWithLegend<TerrainBean>) (pan1.getImager())).getLegend(),
//				false, 100, 0);

		mainImager = pan1.getImager();
		legImager = GradientLegendImager.factory(true, nLegendSteps, legendDirection);
//		legImager = (GradientLegendImager<TerrainBean>) GradientLegendImager.factory(mainImager, true, nLegendSteps, legendDirection);
		
		

		f1.setLayout(new GridLayout(1, 1));
		JLabel lab = new JLabel();

//		lab.setIcon(new ImageIcon(mainImager.getImage()));
//		lab.setIcon(new ImageIcon(mainImager.getImage()));
		lab.setIcon(new ImageIcon(pan1.getImg()));
		
//		JPanel pan = new JPanel();
//		pan.setSize(1000, 1000);
		
		f1.setLayout(new GridLayout(1, 2));
		f1.add(pan1);
		f1.add(lab);
		f1.setVisible(true);
//		f2.add(lab);
//		f2.setVisible(true);
//		pan1.getGraphics().drawImage(mainImager.getImage(), 0, 0, 1000, 1000, pan1);


	}


}
