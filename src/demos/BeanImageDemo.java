package demos;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import beans.sampleBeans.TerrainBean;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.PrimitiveImageFactory.SimpleImagePanel;
import imaging.imagers.BeanImager;
import imaging.imagers.ImagerFactory;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;

public class BeanImageDemo 
{
	static Font font = new Font("times", 2, 20);
	static Color[] boolCol = new Color[] {Color.gray, Color.green};
	static SimpleImagePanel iPan;
	static ObjectImagePanel<?> objPan;
	static ColorInterpolator c;

	static String inputNCDF = "testData/AllFlavorBean.nc";


	static TerrainBean[][] cells;
	static int nRows;
	static int nCols;

	static String dblFmt = "%.1f";

	static Color[] gradCols = ColorUtils.TOPO_COLORS;
	static Color[] boolCols = ColorUtils.YELLOWS;

	static Border border = BorderFactory.createLineBorder(Color.black, 2);
	static JFrame f;
	static int cellSize = 400;
	static List<SimpleImagePanel> panels;
	static List<ObjectImagePanel<?>> tPanels;
	static double ptSize;
	static BeanImager<TerrainBean> imagerAge, imagerElev;
	static boolean[] test = new boolean[] {true, false};
	static List<List<AllFlavorBean>> beans;

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
		netcdfDemo(600, 475, true, save);
//		SimpleImagePanelDemo(22, 17, 50, true, save);
//		objectArrayImageMultiPanelDemo(50, 60, true, save);
	}

	static void netcdfDemo(int width, int height, boolean show, boolean save)
	{
		beans = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF);

		BeanImager<AllFlavorBean> imager1 = ImagerFactory.quickFactory(beans, 100, true, true, "intPrim", AllFlavorBean.class, gradCols, boolCols);
		objPan = ObjectArrayPanelFactory.buildPanel(
				imager1, "boolPrim", true, 0, 0, ptSize);

		f = SwingUtils.frameFactory(width, height,
				"All Flavor Bean Object List Image Demo");
		f.add(objPan);
		f.setVisible(show);
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
				imagerAge = ImagerFactory.quickFactory(cells, 100, true, true, "age", TerrainBean.class, gradCols, boolCols);
				imagerElev = ImagerFactory.quickFactory(cells, 100, true, true, "elevation", TerrainBean.class, gradCols, boolCols);

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

		for (ObjectImagePanel<?> p : tPanels) p.setBorder(border); 

		f = SwingUtils.frameFactory(3 * cellSize, 4 * cellSize,
				"Terrain Bean Object Array Image Demo", 4, 3);
		for (ObjectImagePanel<?> p : tPanels) 
		{
			f.add(p);
		}
		f.setVisible(show);

		if (show && saveFile) SwingUtils.saveFrameImage(f, "sampleOutput/array_image_multi_panel_demo.png");

	}


	public static void SimpleImagePanelDemo(int width, int height,
			double mult, boolean show, boolean save)
	{
		setup(width, height, 1.57, 	1000);
		for (int i = 0; i < width ; i++) for (int j = 0; j < height; j++) cells[i][j].age = i + j;

		imagerAge = ImagerFactory.quickFactory(
				cells, 100, true, true, "age", TerrainBean.class,
				gradCols, boolCols);

		objPan = ObjectArrayPanelFactory.buildPanel(
				imagerAge, "elevation", true, 0, 0, ptSize);

		for (int i = 0; i < width ; i++) for (int j = 0; j < height; j++)
			objPan.addValueLabel(i, j, font);
		objPan.setBorder(border); 

		f = SwingUtils.frameFactory((int) (mult * width), (int) (mult * height),
				"Terrain Bean Simple Image Demo", 1, 1);
		f.add(objPan);
		f.setVisible(show);

		if (show && save) SwingUtils.saveFrameImage(f, 
				"sampleOutput/array_image_multi_panel_demo.png");
	}


}
