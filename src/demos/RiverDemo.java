package demos;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.sampleBeans.TerrainBean;
import imaging.imagers.ImagerFactory;
import imaging.imagers.ObjectImager;
import swing.ObjectArrayImageComboBox;
import swing.SwingUtils;
import swing.stretchAndClick.ImagePanelFactory;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;

public class RiverDemo extends DemoConsts
{


	public static void main(String[] args) 
	{

		pointLabelDemo();
		
		arrayPanelDemo();
	}

	public static void pointLabelDemo()
	{
		
		int nRow = 8, nCol = 8;
		
		TerrainBean[][] cells1 = TerrainBean.factory(nRow, nCol, 1.5, 13);
		TerrainBean.randomPath(cells1, false, true, 0.32, 0.528, 0.5, 0.5, 0.9, 1);
		TerrainBean.randomPath(cells1, true, false, 0.32, 0.528, 0.5, 0.5, 0.1, 1);
//		TerrainBean.randomRivers(cells1, 0.52, 0.628, 0.5, 0.5, 2);
		ObjectImagePanel<TerrainBean> pan1;
		
		
		double ptSize = 1.0 / ((double) Math.max(nRow, nCol));
		
		JFrame f1 = SwingUtils.frameFactory(1100, 1100);
		
		pan1 = ImagePanelFactory.buildPanel(
				TerrainBean.class, 
				ParsedField.class,
				null, cells1, "age", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				"%.2d", null, true, false, false, false,
				100, true, true, true, 0, 0, ptSize);
//		pan1.setField("elevation");
		f1.add(pan1);
		f1.setVisible(true);
		
		for (int i = 0; i < nRow; i++)
		{
			pan1.addPoint(1, i, 30, Color.blue);
			pan1.addPoint(i, 2, 0, Color.green);
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
		terrainArray = TerrainBean.factory(150, 230, 1.5, 13);
		TerrainBean.randomPath(terrainArray, road, !stream, roadRight, roadDown, roadVertical, 0.5, roadCorr, 10);
		
		TerrainBean.randomPath(terrainArray, !road, stream, streamRight, streamDown, streamVertical, 0.5, riverCorr, 1);
		
		ObjectImagePanel<TerrainBean> pan1, pan2, pan3, pan4;
		ptSize = 0.15;
		
		f1 = SwingUtils.frameFactory(800, 1500);

		pan1 = ImagePanelFactory.buildPanel(
				TerrainBean.class, ParsedField.class,
				null, terrainArray, "stream", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt, null, true, false, false, false,
				100, true, true, true, 0, 0, ptSize);
		pan2 = 
				
				
				ImagePanelFactory.buildPanel(
				TerrainBean.class, ParsedField.class,
				null, terrainArray, "elevation", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt, null, true, false, false, false,
				100, true, true, true, 0, 0, ptSize);
		pan3 = ImagePanelFactory.buildPanel(
				TerrainBean.class, ParsedField.class,
				null, terrainArray, "age", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt, null, true, false, false, false,
				100, true, true, true, 0, 0, ptSize);
		pan4 = ImagePanelFactory.buildPanel(
				TerrainBean.class, ParsedField.class,
				null, terrainArray, "elevation", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt, null, true, false, false, false,
				100, true, true, true, 0, 0, ptSize);

		f1.setLayout(new GridLayout(2, 2));
		f1.setPreferredSize(f1.getSize());
		f1.add(pan1);
		f1.add(pan2);
		f1.add(pan3);
		f1.add(pan4);
		
		f1.pack();
		f1.setVisible(true);

		pan1.setField("elevation");

		pan1.addTextLabel("label", 0.5, 0.5, font);
		pan1.addValueLabel(0.1, 0.1, font);

		pan2.addTextLabel("center", 0.5, 0.5, font);
		pan2.addValueLabel(0.1, 0.1, font);
		
		pan3.addTextLabel("right", 1.1, 0.5, font);
		pan3.addPoint(0.51, 0.2, 0, Color.black);
		pan3.addPoint(20, 10, 0, Color.black);

		pan1.addValueLabel(0.1, 0.5, font);
		pan2.addValueLabel(0.1, 0.5, font);
		pan3.addValueLabel(0.1, 0.5, font);
		pan4.addValueLabel(0.01, 0.6, font);
		pan4.addValueLabel(0.2, 0.5, font);
		
		pan1.addPoint(0.1, 0.25, 49, Color.blue);
		pan1.addPoint(0.1, 0.35, 0, Color.blue);
		pan4.addPoint(0.1, 0.75, 48, Color.white);
		
		pan2.addPoint(0.1, 0.25, 46, Color.blue);
		pan2.addPoint(0.2, 0.25, 40, Color.blue);

		JFrame f2 = SwingUtils.frameFactory(800, 800);
		f2.setLayout(new GridLayout(2, 2));

		JComboBox<String> jcb = ObjectArrayImageComboBox.comboBoxFactory(pan1);
		JComboBox<String> jcb2 = ObjectArrayImageComboBox.comboBoxFactory(pan2);
		JComboBox<String> jcb3= ObjectArrayImageComboBox.comboBoxFactory(pan3);
		JComboBox<String> jcb4 = ObjectArrayImageComboBox.comboBoxFactory(pan4);
		jcb.setFont(font);
		jcb2.setFont(font);
		jcb3.setFont(font);
		jcb4.setFont(font);

		f2.add(jcb);
		f2.add(jcb2);
		f2.add(jcb3);
		f2.add(jcb4);
		f2.setVisible(true);
	}

	public static void demo1()
	{
		int nRows = 300;
		int nCols = 300;
		int nRivers = 10;

		double probRight= 0.508;
		double probDown = 0.75;
		double probDiagonal = 0.9;
		double probVertical = 0.7;
		double curvature = 0;
		
		String filename = String.format("riverDemo_diag_%.2f_vert_%.2f_right_%.2f_up_%.2f_rivers_%d_%d_by_%d.png",
				probDiagonal, probVertical, probRight, probDown, nRivers, nRows, nCols);

		TerrainBean[][] cells = TerrainBean.factory(nRows, nCols, 0, 13);
		TerrainBean.randomPath(
				cells, 
				false, 
				true,
				probRight, probDown, 
				probVertical, probDiagonal, curvature, 
				nRivers);
		File imgFile = new File("sampleOutput/" + filename);
		JFrame f1 = SwingUtils.frameFactory(nRows, nCols);
		ObjectImagePanel<TerrainBean> p1;
		ObjectImager<TerrainBean> imager1 = ImagerFactory.quickFactory(
				null, cells, 100,
				true, false,
				"stream", TerrainBean.class, 
				
				ColorUtils.HEAT_COLORS, boolCols);

		p1 = ImagePanelFactory.primitivePanel(imager1, "stream", true, 0, 0, 0.1);
		f1.setLayout(new GridLayout(1, 1));
		f1.add(p1);
		f1.setVisible(true);

		try {
			ImageIO.write((RenderedImage) imager1.getImage(), "png", imgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}