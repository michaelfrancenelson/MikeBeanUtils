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
import image.ArrayImageFactory.SimpleImagePanel;
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
	static SimpleImagePanel iPan;
	static ObjectArrayImagePanel<TerrainBean> objPan;
	static ColorInterpolator c;

	static TerrainBean[][] cells;
	static int nRows;
	static int nCols;

	static Border border = BorderFactory.createLineBorder(Color.black, 2);
	static JFrame f;
	static int cellSize = 400;
	static List<SimpleImagePanel> panels;
	static List<ObjectArrayImagePanel<TerrainBean>> tPanels;
	static double ptSize;
	static ObjectArrayImager<TerrainBean> imagerAge, imagerElev;


	public static void setup(int nRow, int nCol, double elevGradient, int ageMod)
	{
		nRows = nRow; nCols = nCol;
		ptSize = 1.0 / ((double) Math.max(nRows, nCols));
		cells = TerrainBean.factory(nRows, nCols, elevGradient, ageMod);
		tPanels = new ArrayList<>();
		panels = new ArrayList<>();
	}

	public static void main(String[] args) 
	{
		SimpleImagePanelDemo();
		objectArrayImagePanelDemo();
	}


	public static void legendImagePanelDemo()
	{
	}

	public static void objectArrayImagePanelDemo()
	{

		setup(80, 70, 1.57, 7);

		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				TerrainBean.perturbElevations(cells, i * 10);
				TerrainBean.perturbAges(cells, (int) (j * 2.75));
				imagerAge = img(100, 1, "age");
				imagerElev = img(100, 1, "Elevation");

				objPan = ObjectArrayPanelFactory.buildPanel(imagerElev, "elevation", true, 0, 0, ptSize);
				objPan.addValueLabel(0.2, 0.4, font);
				tPanels.add(objPan);
				objPan = ObjectArrayPanelFactory.buildPanel(imagerAge, "age", true, 0, 0, ptSize);
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
				f.setVisible(true);

		JFrame f2 = SwingUtils.frameFactory(3 * cellSize, 4 * cellSize,
				"Terrain Bean Object Array Image Demo");
		f2.add(objPan.getLegendPanel());
		f2.setVisible(true);

	}

	static ObjectArrayImager<TerrainBean> img(int nLegendSteps, int legendDirection, String field)
	{
		return SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"age", 
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null,
				true, nLegendSteps, legendDirection);	
	}


	public static void SimpleImagePanelDemo()
	{
		setup(303, 504, 1.57, 75);
		imagerAge = img(100, 1, "elevation");
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2;  j++)
		{
			TerrainBean.perturbElevations(cells, 300.0);
			TerrainBean.perturbAges(cells, 20);
			panels.add(new SimpleImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imagerAge, "elevation", i, j, false, true)));
			panels.add(new SimpleImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imagerAge, "elevation", i, j, true, true)));
			panels.add(new SimpleImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imagerAge, "age", i, j, false, true)));
			panels.add(new SimpleImagePanel(ArrayImageFactory.buildArrayImage(
					cells, imagerAge, "age", i, j, true, true)));
		}

		f = SwingUtils.frameFactory(4 * cellSize, 4 * cellSize, "Terrain Bean Array Demo", 4, 4);

		for (SimpleImagePanel pan : panels) { pan.setBorder(border); f.add(pan); }
		f.setVisible(true);
	}


}
