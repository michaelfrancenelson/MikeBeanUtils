package com.github.michaelfrancenelson.mikebeansutils.demos;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.michaelfrancenelson.mikebeansutils.beans.builder.AnnotatedBeanReader.ParsedField;
import com.github.michaelfrancenelson.mikebeansutils.beans.sampleBeans.Terrain;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.ImagerFactory;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.ObjectImager;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.imagerData.ImagerData;
import com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick.ObjectImagePanel;
import com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick.PanelFactory;
import com.github.michaelfrancenelson.mikebeansutils.utils.ColorUtils;
import com.github.michaelfrancenelson.mikebeansutils.utils.SwingUtils;

public class RiverDemo extends DemoConsts
{
	public static void main(String[] args) 
	{
//		pointLabelDemo();
//		arrayPanelDemo();
		demo1();
		riverDemo();
	}

	
	@SuppressWarnings("unused")
	public static void riverDemo()
	{
		int nRow = 800, nCol = 800;

		JFrame f1 = SwingUtils.frameFactory(1100, 1100);

		ImagerData<Terrain> imgDat;
		ObjectImager<Terrain> imager1;
		
		Terrain[][] cells1 = Terrain.factory(nRow, nCol, 1.5, 13);
		Terrain.randomPath(
				cells1, false, true,
				0.32, 0.528, 
				0.5, 0.5,
				0.9,
				4);
		ObjectImagePanel<Terrain> pan1;

		Color[] cols = new Color[2];
		cols[0] = new Color(0, 0, 0, 255);
		cols[1] = new Color(255, 255, 255, 30);
		
		
		imgDat = ImagerData.build(cells1, false, false, false);
		imager1 = ImagerFactory.factory(
				imgDat, "stream", Terrain.class, 
				cols, cols);
		
		
	}
	
	public static void pointLabelDemo()
	{

		int nRow = 8, nCol = 8;

		Terrain[][] cells1 = Terrain.factory(nRow, nCol, 1.5, 13);
		Terrain.randomPath(cells1, false, true, 0.32, 0.528, 0.5, 0.5, 0.9, 4);
		Terrain.randomPath(cells1, true, false, 0.32, 0.528, 0.5, 0.5, 0.1, 3);
		ObjectImagePanel<Terrain> pan1;

		ImagerData<Terrain> imDat = ImagerData.build(cells1, false, false, false);

		double ptSize = 1.0 / ((double) Math.max(nRow, nCol));

		JFrame f1 = SwingUtils.frameFactory(1100, 1100);

		pan1 = PanelFactory.objectPanel(
				imDat,
				Terrain.class, 
				ParsedField.class,
				"stream", 
				ColorUtils.HEAT_COLORS, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				"%.2f", null, 
				true,
				0, 0, ptSize);

		JComboBox<String> cbox = pan1.getControlComboBox(font);
		f1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridy = 0; c.gridx = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		f1.add(pan1, c);

		c.weighty = 0;
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
		Terrain.randomPath(terrainArray, !road, stream, streamRight, streamDown, streamVertical, 0.5, riverCorr, 15);


		f1 = SwingUtils.frameFactory(800, 1500);
		f1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		ImagerData<Terrain> imDat = ImagerData.build(terrainArray, false, false, false);

		List<JPanel> pans = new ArrayList<>();
		List<JComboBox<String>> combs = new ArrayList<>();

		for (int i = 0; i < 2; i++)
		{
			c.gridx = i;
			for (int j = 0; j < 2; j++)
			{
				int x = (2 * j);
				ObjectImagePanel<Terrain> pan1 = PanelFactory.objectPanel(
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
				pan1.setBorder(border);
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
		f1.setLocation(1200, 0);

	}

		@SuppressWarnings("unused")
		public static void demo1()
		{
			int nRows = 4000;
			int nCols = 3000;
			int nRivers = 10;
	
			double probRight= 0.508;
			double probDown = 0.75;
			double probDiagonal = 0.9;
			double probVertical = 0.7;
			double curvature = 0;
			
			Color[] cols = new Color[2];
			cols[0] = new Color(0, 0, 0, 255);
			cols[1] = new Color(255, 255, 255, 1);
			

			
			
			String filename = String.format("riverDemo_diag_%.2f_vert_%.2f_right_%.2f_up_%.2f_rivers_%d_%d_by_%d.png",
					probDiagonal, probVertical, probRight, probDown, nRivers, nRows, nCols);
	
			Terrain[][] cells = Terrain.factory(nRows, nCols, 0, 13);
			Terrain.randomPath(
					cells, 
					false, 
					true,
					probRight, probDown, 
					probVertical, probDiagonal, curvature, 
					nRivers);
			File imgFile = new File("testImages/" + filename);
			JFrame f1 = SwingUtils.frameFactory(nRows, nCols);
			ObjectImagePanel<Terrain> p1;
			
			ImagerData<Terrain> imgDat = ImagerData.build(cells, false, false, false);
			
			
			ObjectImager<Terrain> imager1 = ImagerFactory.factory(
					imgDat, "stream", Terrain.class, 
					cols, cols);
					
//			ObjectImager<Terrain> imager1 = ImagerFactory.factory(
//					imgDat, "stream", Terrain.class, 
//					gradCols, boolCols);
//			
	
//			p1 = ImagePanelFactory.buildPanel(imager1, "stream", true, 0, 0, 0.1);
//			f1.setLayout(new GridLayout(1, 1));
//			f1.add(p1);
//			f1.setVisible(true);
//	
			try {
				ImageIO.write((RenderedImage) imager1.getImage(), "png", imgFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}