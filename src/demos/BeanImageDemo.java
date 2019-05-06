package demos;

import java.util.ArrayList;

import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import beans.sampleBeans.TerrainBean;
import imaging.imagers.BeanImager;
import imaging.imagers.ImagerFactory;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectArrayPanelFactory;
import swing.stretchAndClick.ObjectImagePanel;

public class BeanImageDemo extends DemoConsts
{

	static String inputNCDF = "testData/AllFlavorBean.nc";

	public static void setup(int width, int height, double elevGradient, int ageMod)
	{
		nRows = height; nCols = width;
		ptSize = 1.0 / ((double) Math.max(nRows, nCols));
		terrainArray = TerrainBean.factory(nCols, nRows, elevGradient, ageMod);
		panels1 = new ArrayList<>();
	}

	public static void main(String[] args) 
	{
		save = false;
		show = true;
//		boolean save = false;
//		netcdfDemo(600, 475, true, save);
		SimpleImagePanelDemo(12, 13, 150, show, save);
//		objectArrayImageMultiPanelDemo(100, 117, true, save);
	}

	static void netcdfDemo(int width, int height, boolean show, boolean save)
	{
		allFlavList = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF);

		BeanImager<AllFlavorBean> imager1 = ImagerFactory.quickFactory(
				allFlavList, null, 100, true, true, "intPrim", AllFlavorBean.class, gradCols, boolCols);
		objPan = ObjectArrayPanelFactory.buildPanel(
				imager1, "intPrim", true, 0, 0, ptSize);

		f1 = SwingUtils.frameFactory(width, height,
				"All Flavor Bean Object List Image Demo");
		
		double nPtsX = allFlavList.size();
		double nPtsY = allFlavList.get(0).size();
		
		ptSize = 0.01;
		for (int i = 0; i < (int) nPtsX; i++) 
			for (int j = 0; j < (int) nPtsY; j++)
		{
			objPan.addPoint(
					(double) i / (nPtsX) + 0.5/nPtsX,
					(double) j / (nPtsY) + 0.5/nPtsY, ptSize, color);
		}
			
		f1.add(objPan);
		f1.setVisible(show);
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
				TerrainBean.perturbElevations(terrainArray, i * 10);
				TerrainBean.perturbAges(terrainArray, (int) (j * 2.75));
				im1 = ImagerFactory.quickFactory(null, terrainArray, 100, true, true, "age", TerrainBean.class, gradCols, boolCols);
				im2 = ImagerFactory.quickFactory(null, terrainArray, 100, true, true, "elevation", TerrainBean.class, gradCols, boolCols);

				objPan = ObjectArrayPanelFactory.buildPanel(
						im2, "elevation", true, 0, 0, ptSize);
				objPan.addValueLabel(0.2, 0.4, font, color);
				panels1.add(objPan);
				objPan = ObjectArrayPanelFactory.buildPanel(
						im1, "age", true, 0, 0, ptSize);
				objPan.addValueLabel(0.1 , 0.7, font, color);
				panels1.add(objPan);
			}
		}

		for (ObjectImagePanel<?> p : panels1) p.setBorder(border); 

		f1 = SwingUtils.frameFactory(3 * cellSize, 4 * cellSize,
				"Terrain Bean Object Array Image Demo", 4, 3);
		for (ObjectImagePanel<?> p : panels1) 
		{
			f1.add(p);
		}
		f1.setVisible(show);

		if (show && saveFile) SwingUtils.saveFrameImage(f1, "sampleOutput/array_image_multi_panel_demo.png");

	}


	public static void SimpleImagePanelDemo(int width, int height,
			double mult, boolean show, boolean save)
	{
		setup(width, height, 1.57, 	1000);
		for (int i = 0; i < width ; i++) for (int j = 0; j < height; j++) terrainArray[i][j].age = i + j;

		im1 = ImagerFactory.quickFactory(
				null, terrainArray, 100, true, true, "age", TerrainBean.class,
				gradCols, boolCols);
		im1.setDblFmt("%.1f");
		objPan = ObjectArrayPanelFactory.buildPanel(
				im1, "elevation", true, 0, 0, ptSize);

		objPan.labelPixels(font, color);
		objPan.setBorder(border); 

//		objPan.addValueLabel(0.2, 0.8, null, null);
		
		f1 = SwingUtils.frameFactory((int) (mult * width), (int) (mult * height),
				"Terrain Bean Simple Image Demo", 1, 1);
		f1.add(objPan);
		f1.setVisible(show);

		if (show && save) SwingUtils.saveFrameImage(f1, 
				"sampleOutput/array_image_multi_panel_demo.png");
	}


}
