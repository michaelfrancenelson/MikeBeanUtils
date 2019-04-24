package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import beans.sampleBeans.TerrainBean;
import image.arrayImager.ArrayImager;
import image.arrayImager.ObjectArrayImager;
import image.colorInterpolator.ColorInterpolator;
import image.imageFactories.PrimitiveImageFactory.SimpleImagePanel;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayImagePanel;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import utils.ColorUtils;

public class ArrayImageDemo 
{
	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};
	static SimpleImagePanel iPan;
	static ObjectArrayImagePanel<TerrainBean> objPan;
	static ColorInterpolator c;

	static TerrainBean[][] cells;
	static int nRows;
	static int nCols;

	static Color[] gradCols = ColorUtils.TOPO_COLORS;
	static Color[] boolCols = ColorUtils.YELLOWS;
	
	static Border border = BorderFactory.createLineBorder(Color.black, 2);
	static JFrame f;
	static int cellSize = 400;
	static List<SimpleImagePanel> panels;
	static List<ObjectArrayImagePanel<TerrainBean>> tPanels;
	static double ptSize;
	static ObjectArrayImager<TerrainBean> imagerAge, imagerElev;
	static boolean[] test = new boolean[] {true, false};

	public static void setup(int width, int height, double elevGradient, int ageMod)
	{
		nRows = height; nCols = width;
		ptSize = 1.0 / ((double) Math.max(nRows, nCols));
		cells = TerrainBean.factory(nCols, nRows, elevGradient, ageMod);
		tPanels = new ArrayList<>();
		panels = new ArrayList<>();
	}

	public static void main(String[] args) 
	{

		boolean save = false;
		SimpleImagePanelDemo(10, 13, 50, true, save);
		objectArrayImageMultiPanelDemo(50, 60, true, save);
	}


	public static void legendImagePanelDemo()
	{
	}

	public static void objectArrayImageMultiPanelDemo(
			int width, int height, boolean show, boolean saveFile)
	{
		setup(width, height, 1.57, 7);

		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				TerrainBean.perturbElevations(cells, i * 10);
				TerrainBean.perturbAges(cells, (int) (j * 2.75));
				imagerAge = img(100, true, true, "age");
				imagerElev = img(100, true, true, "elevation");

				objPan = ObjectArrayPanelFactory.buildPanel(
						imagerElev, "elevation", true, 0, 0, ptSize);
				objPan.addValueLabel(0.2, 0.4, font);
				tPanels.add(objPan);
				objPan = ObjectArrayPanelFactory.buildPanel(
						imagerAge, "age", true, 0, 0, ptSize);
				objPan.addValueLabel(0.1 , 0.7, font);
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
		f.setVisible(show);

		if (show && saveFile) SwingUtils.saveFrameImage(f, "sampleOutput/array_image_multi_panel_demo.png");

	}

	static ObjectArrayImager<TerrainBean> img(int nLegendSteps, 
			boolean lToH, boolean horz, String field)
	{

		return ArrayImager.factory(
				TerrainBean.class, cells, field,
				gradCols, boolCols,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				null,  null,
				true,
				false, false, false,
				nLegendSteps, lToH, horz
				);
				
//				ArrayImager.factory(
//				TerrainBean.class, cells, field,
//				gradCols, boolCols,
//				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
//				null,  null,
//				true,
//				false, false, false,
//				nLegendSteps, lToH, horz
//
//				);
	}


	public static void SimpleImagePanelDemo(int width, int height,
			double mult, boolean show, boolean save)
	{
		setup(width, height, 1.57, 	1000);
		for (int i = 0; i < width ; i++) for (int j = 0; j < height; j++) cells[i][j].age = i + j;
		
		imagerAge = img(100, true, true, "age");
		objPan = ObjectArrayPanelFactory.buildPanel(
				imagerAge, "elevation", true, 0, 0, ptSize);
		tPanels.add(objPan);

		for (int i = 0; i < width ; i++) for (int j = 0; j < height; j++)
			objPan.addLabel(i, j, "" + (i + j), font, Color.black, true, 1);

		
		
		
		
		for (ObjectArrayImagePanel<TerrainBean> p : tPanels) p.setBorder(border); 

		f = SwingUtils.frameFactory((int) (mult * width), (int) (mult * height),
				"Terrain Bean Object Array Image Demo", 1, 1);
		for (ObjectArrayImagePanel<TerrainBean> p : tPanels) 
		{
			f.add(p);
		}
		f.setVisible(show);

		if (show && save) SwingUtils.saveFrameImage(f, "sampleOutput/array_image_multi_panel_demo.png");

	}


}
