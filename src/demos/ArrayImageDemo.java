package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import beans.sampleBeans.TerrainBean;
import image.ArrayImageFactory;
import image.ArrayImageFactory.ImagePanel;
import image.arrayImager.ObjectArrayImager;
import image.arrayImager.SimpleArrayImager;
import image.colorInterpolator.ColorInterpolator;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayImagePanel;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import utils.ColorUtils;

public class ArrayImageDemo 
{
	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};
	static ImagePanel iPan;
	static ObjectArrayImagePanel<TerrainBean> objPan;
	static ColorInterpolator c;

	static TerrainBean[][] cells;
	static int nRows = 303;
	static int nCols = 504;

	static Border border = BorderFactory.createLineBorder(Color.black, 2);
	static JFrame f;
	static int cellSize = 400;
	static List<ImagePanel> panels;
	static List<ObjectArrayImagePanel<TerrainBean>> tPanels;

	static ObjectArrayImager<TerrainBean> imager1, imager2;

	public static void main(String[] args) 
	{
		//		ImagePanelDemo();
		objectArrayImagePanelDemo();
	}

	public static void objectArrayImagePanelDemo()
	{
		nRows = 80; nCols = 70;
		double ptSize = 1.0 / ((double) Math.max(nRows, nCols));
		cells = TerrainBean.factory(nRows, nCols, 1.57, 7);
		imager1 = SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"age", 
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);	

		imager2 = SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"elevation", 
				ColorUtils.TERRAIN_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);	



		tPanels = new ArrayList<>();
double dd = 0.1;
		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				TerrainBean.perturbElevations(cells, i * 10);
				TerrainBean.perturbAges(cells, (int) (j * 2.75));
				imager1 = SimpleArrayImager.factory(
						TerrainBean.class, cells, 
						"age", 
						ColorUtils.HEAT_COLORS, bCol,
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);	

				imager2 = SimpleArrayImager.factory(
						TerrainBean.class, cells, 
						"elevation", 
						ColorUtils.TERRAIN_COLORS, bCol,
						Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);	
				objPan = ObjectArrayPanelFactory.buildPanel(imager2, "elevation", true, 0, 0, ptSize);
				objPan.addValueLabel(0.5, 0.7 - dd, font);
				tPanels.add(objPan);
				objPan = ObjectArrayPanelFactory.buildPanel(imager1, "age", true, 0, 0, ptSize);
				objPan.addValueLabel(dd , 0.7, font);
//				dd += 0.05;
				tPanels.add(objPan);
			}
		}

		for (ObjectArrayImagePanel<TerrainBean> p : tPanels) p.setBorder(border); 

		f = SwingUtils.frameFactory(3 * cellSize, 4 * cellSize,
				"Terrain Bean Object Array Image Demo", 4, 3);
		for (ObjectArrayImagePanel<TerrainBean> p : tPanels) 
		{
			f.add(p);
		}
		f.setVisible(true);



	}




	public static void ImagePanelDemo()
	{
		nRows = 303; nCols = 504;

		cells = TerrainBean.factory(nRows, nCols, 1.57, 75);

		imager1 = SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"elevation", 
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);		

		panels = new ArrayList<>();

		for (int i = 0; i < 2; i++) for (int j = 0; j < 2;  j++)
		{
			TerrainBean.perturbElevations(cells, 300.0);
			TerrainBean.perturbAges(cells, 20);
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "elevation", i, j, false, true)));
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "elevation", i, j, true, true)));
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "age", i, j, false, true)));
			panels.add(new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imager1, "age", i, j, true, true)));
		}


		f = SwingUtils.frameFactory(4 * cellSize, 4 * cellSize, "Terrain Bean Array Demo", 4, 4);

		for (ImagePanel pan : panels) { pan.setBorder(border); f.add(pan); }
		f.setVisible(true);
	}


}
