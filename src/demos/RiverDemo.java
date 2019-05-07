package demos;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.sampleBeans.Terrain;
import imaging.imagers.ImagerData;
import swing.SwingUtils;
import swing.stretchAndClick.ImagePanelFactory;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;

public class RiverDemo extends DemoConsts
{


	public static void main(String[] args) 
	{

		//		pointLabelDemo();

		arrayPanelDemo();
	}

	public static void pointLabelDemo()
	{

		int nRow = 8, nCol = 8;

		Terrain[][] cells1 = Terrain.factory(nRow, nCol, 1.5, 13);
		Terrain.randomPath(cells1, false, true, 0.32, 0.528, 0.5, 0.5, 0.9, 1);
		Terrain.randomPath(cells1, true, false, 0.32, 0.528, 0.5, 0.5, 0.1, 1);
		//		TerrainBean.randomRivers(cells1, 0.52, 0.628, 0.5, 0.5, 2);
		ObjectImagePanel<Terrain> pan1;

		ImagerData<Terrain> imDat = ImagerData.build(cells1, false, false, false);

		double ptSize = 1.0 / ((double) Math.max(nRow, nCol));

		JFrame f1 = SwingUtils.frameFactory(1100, 1100);

		pan1 = ImagePanelFactory.buildPanel(
				imDat,
				Terrain.class, 
				ParsedField.class,
				"age", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				"%.2f", null, 
				true,
				0, 0, ptSize);
		//		pan1.setField("elevation");

		JComboBox<String> cbox = pan1.getControlComboBox(font);
		f1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.anchor = c.CENTER;
		f1.add(pan1, c);

		c.weighty = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		f1.add(cbox, c);



		f1.setVisible(true);

		for (int i = 0; i < nRow; i++)
		{
			pan1.addPoint(i, 0, 0.05, Color.blue);
			pan1.addPoint(i, 1, 0.08, Color.green);
		}

	}

	public static void arrayPanelDemo()
	{

		double riverCorr = 0.05;
		double roadCorr = 0.7;
		boolean road = true;
		boolean stream = true;

		double streamRight = 0.5;
		double roadRight = 0.3;

		double streamDown = 0.5;
		double roadDown = 0.45;

		double streamVertical = 0.5;
		double roadVertical = 0.5;

		dblFmt = "%.2f";
		terrainArray = Terrain.factory(150, 230, 1.5, 13);

		Terrain.randomPath(terrainArray, road, !stream, roadRight, roadDown, roadVertical, 0.5, roadCorr, 10);
		Terrain.randomPath(terrainArray, !road, stream, streamRight, streamDown, streamVertical, 0.5, riverCorr, 1);

		ObjectImagePanel<Terrain> pan1, pan2, pan3, pan4;
		ptSize = 0.15;

		f1 = SwingUtils.frameFactory(800, 1500);
		f1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		ImagerData<Terrain> imDat = ImagerData.build(terrainArray, false, false, false);

		List<JPanel> pans = new ArrayList<>();
		List<JComboBox> combs = new ArrayList<>();

		for (int i = 0; i < 2; i++)
		{
			c.gridx = i;
			for (int j = 0; j < 2; j++)
			{
				int x = (2 * j);
				pan1 = ImagePanelFactory.buildPanel(
						imDat,
						Terrain.class, 
						ParsedField.class,
						"stream", 
						ColorUtils.HEAT_COLORS, boolCols,
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
						null, null, 
						true,
						0, 0, ptSize);
				JComboBox<String> jcb = pan1.getControlComboBox(font);
				pans.add(pan1);
				combs.add(jcb);
				c.fill = GridBagConstraints.BOTH;
	
				c.weighty = 1; c.weightx = 1;
				c.gridy = x;
				f1.add(pan1, c);
				
				x++;
				c.gridy = x;
				c.weighty = 0;
				f1.add(jcb, c);
			}
		}

		f1.setVisible(true);

	}
	//		pan2 = ImagePanelFactory.buildPanel(
	//				imDat,
	//				Terrain.class, 
	//				ParsedField.class,
	//				"elevation", 
	//				ColorUtils.HEAT_COLORS, boolCols,
	//				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
	//				null, null, 
	//				true,
	//				0, 0, ptSize);
	//		pan3 = ImagePanelFactory.buildPanel(
	//				imDat,
	//				Terrain.class, 
	//				ParsedField.class,
	//				"age", 
	//				ColorUtils.HEAT_COLORS, boolCols,
	//				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
	//					null, null, 
	//				true,
	//				0, 0, ptSize);
	//		pan4 = ImagePanelFactory.buildPanel(
	//				imDat,
	//				Terrain.class, 
	//				ParsedField.class,
	//				"elevation", 
	//				ColorUtils.HEAT_COLORS, boolCols,
	//				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
	//				"%.2f", null, 
	//				true,
	//				0, 0, ptSize);
	//
	//		f1.setLayout(new GridLayout(2, 2));
	//		f1.setPreferredSize(f1.getSize());
	//		f1.add(pan1);
	//		f1.add(pan2);
	//		f1.add(pan3);
	//		f1.add(pan4);
	//		
	//		f1.pack();
	//		f1.setVisible(true);
	//
	//		pan1.setField("elevation");
	//
	////		pan1.addTextLabel("label", 0.5, 0.5, font);
	////		pan1.addValueLabel(0.1, 0.1, font);
	////
	////		pan2.addTextLabel("center", 0.5, 0.5, font);
	////		pan2.addValueLabel(0.1, 0.1, font);
	////		
	////		pan3.addTextLabel("right", 1.1, 0.5, font);
	////		pan3.addPoint(0.51, 0.2, 0, Color.black);
	////		pan3.addPoint(20, 10, 0, Color.black);
	////
	////		pan1.addValueLabel(0.1, 0.5, font);
	////		pan2.addValueLabel(0.1, 0.5, font);
	////		pan3.addValueLabel(0.1, 0.5, font);
	////		pan4.addValueLabel(0.01, 0.6, font);
	////		pan4.addValueLabel(0.2, 0.5, font);
	//		
	//		pan1.addPointRelative(0.1, 0.25, 49, Color.blue);
	//		pan1.addPointRelative(0.1, 0.35, 0, Color.blue);
	//		pan4.addPointRelative(0.1, 0.75, 48, Color.white);
	//		
	//		pan2.addPointRelative(0.1, 0.25, 46, Color.blue);
	//		pan2.addPointRelative(0.2, 0.25, 40, Color.blue);
	//
	//		JFrame f2 = SwingUtils.frameFactory(800, 800);
	//		f2.setLayout(new GridLayout(2, 2));
	//
	//		JComboBox<String> jcb = pan1.getControlComboBox(font);
	//		JComboBox<String> jcb2 =pan2.getControlComboBox(font);
	//		JComboBox<String> jcb3 = pan3.getControlComboBox(font);
	//		JComboBox<String> jcb4 = pan4.getControlComboBox(font);
	//
	//		f2.add(jcb);
	//		f2.add(jcb2);
	//		f2.add(jcb3);
	//		f2.add(jcb4);
	//		f1.setVisible(true);
	//	}

	//	public static void demo1()
	//	{
	//		int nRows = 300;
	//		int nCols = 300;
	//		int nRivers = 10;
	//
	//		double probRight= 0.508;
	//		double probDown = 0.75;
	//		double probDiagonal = 0.9;
	//		double probVertical = 0.7;
	//		double curvature = 0;
	//		
	//		String filename = String.format("riverDemo_diag_%.2f_vert_%.2f_right_%.2f_up_%.2f_rivers_%d_%d_by_%d.png",
	//				probDiagonal, probVertical, probRight, probDown, nRivers, nRows, nCols);
	//
	//		Terrain[][] cells = Terrain.factory(nRows, nCols, 0, 13);
	//		Terrain.randomPath(
	//				cells, 
	//				false, 
	//				true,
	//				probRight, probDown, 
	//				probVertical, probDiagonal, curvature, 
	//				nRivers);
	//		File imgFile = new File("sampleOutput/" + filename);
	//		JFrame f1 = SwingUtils.frameFactory(nRows, nCols);
	//		ObjectImagePanel<Terrain> p1;
	//		ObjectImager<Terrain> imager1 = ImagerFactory.factory(
	//				null, cells, 100,
	//				true, false,
	//				"stream", Terrain.class, 
	//				
	//				ColorUtils.HEAT_COLORS, boolCols);
	//
	//		p1 = ImagePanelFactory.buildPanel(imager1, "stream", true, 0, 0, 0.1);
	//		f1.setLayout(new GridLayout(1, 1));
	//		f1.add(p1);
	//		f1.setVisible(true);
	//
	//		try {
	//			ImageIO.write((RenderedImage) imager1.getImage(), "png", imgFile);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
}