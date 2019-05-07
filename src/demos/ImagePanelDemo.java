package demos;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import beans.sampleBeans.TerrainBean;
import imaging.imagers.ImagerFactory;
import imaging.imagers.ObjectImager;
import swing.SwingUtils;
import swing.stretchAndClick.ImagePanelFactory;
import swing.stretchAndClick.ObjectImagePanel;

public class ImagePanelDemo extends DemoConsts
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
		boolean save = false;
		netcdfDemo(1600, 1475, true, save);
//		objectImagePanelDemo(12, 13, 150, show, save);
//		objectArrayImageMultiPanelDemo(100, 117, true, save);
//		franceDemo(1000, 1200, true);
	}

	static void franceDemo(int width, int height, boolean show)
	{
		f1 = SwingUtils.frameFactory(width, height, "Image panel demo", 2, 2);
		f1.add(ImagePanelFactory.imagePanel(france, false, -1, -1));
		f1.add(ImagePanelFactory.imagePanel(france, true, -1, -1));
		f1.add(ImagePanelFactory.imagePanel(france, false, 300, 400));
		f1.add(ImagePanelFactory.imagePanel(france, false, -1, 400));
		f1.setVisible(show);
	}
	
	
	static void netcdfDemo(int width, int height, boolean show, boolean save)
	{
		allFlavList = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF);

		List<ObjectImager<AllFlavorBean>> imagers = new ArrayList<>();
		imagers.add(ImagerFactory.quickFactory(
				allFlavList, null, 100, true, true, "intPrim", AllFlavorBean.class, gradCols, boolCols));
		imagers.add(ImagerFactory.quickFactory(
				allFlavList, null, 100, true, true, "boolPrim", AllFlavorBean.class, gradCols, boolCols));
		imagers.add(ImagerFactory.quickFactory(
				allFlavList, null, 100, true, true, "floatBox", AllFlavorBean.class, gradCols, boolCols));
		imagers.add(ImagerFactory.quickFactory(
				allFlavList, null, 100, true, true, "strng", AllFlavorBean.class, gradCols, boolCols));

		panels1 = new ArrayList<>();
		panels1.add(ImagePanelFactory.buildPanel(
				imagers.get(0), "intPrim", 
				true, 0, 0, ptSize, 
				AllFlavorBean.class, null));
		panels1.add(ImagePanelFactory.buildPanel(
				imagers.get(1), "boolPrim", 
				false, 0, 0, ptSize, 
				AllFlavorBean.class, null));
		panels1.add(ImagePanelFactory.buildPanel(
				imagers.get(2), "charPrim", 
				false, 100, 0, ptSize, 
				AllFlavorBean.class, null));
		panels1.add(ImagePanelFactory.buildPanel(
				imagers.get(3), "floatPrim", 
				true, 400, 700, ptSize, 
				AllFlavorBean.class, null));

		panels1.get(0).labelPixels(font, color);
		panels1.get(3).labelPixels(font, color);

		
		
		double nPtsX = allFlavList.size();
		double nPtsY = allFlavList.get(0).size();
		
		ptSize = 0.01;
		for (int i = 0; i < (int) nPtsX; i++) 
			for (int j = 0; j < (int) nPtsY; j++)
		{
			panels1.get(1).addPoint(
					(double) i / (nPtsX) + 0.5/nPtsX,
					(double) j / (nPtsY) + 0.5/nPtsY, ptSize, color);
			panels1.get(2).addPoint(
					(double) i / (nPtsX) + 0.5/nPtsX,
					(double) j / (nPtsY) + 0.5/nPtsY, ptSize, color);
		}
			
		f1 = SwingUtils.frameFactory(width, height, "Read from netCDF demo", 2, 2);
		for (JPanel j : panels1) f1.add(j);
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
				imT1 = ImagerFactory.quickFactory(null, terrainArray, 100, true, true, "age", TerrainBean.class, gradCols, boolCols);
				imT2 = ImagerFactory.quickFactory(null, terrainArray, 100, true, true, "elevation", TerrainBean.class, gradCols, boolCols);

				objPan = ImagePanelFactory.buildPanel(
						imT2, "elevation", true, 0, 0, ptSize, TerrainBean.class, null);
				objPan.addValueLabel(0.2, 0.4, font, color);
				panels1.add(objPan);
				objPan = ImagePanelFactory.buildPanel(
						imT1, "age", true, 0, 0, ptSize, TerrainBean.class, null);
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

	public static void objectImagePanelDemo(int width, int height,
			double mult, boolean show, boolean save)
	{
		setup(width, height, 1.57, 	1000);
		for (int i = 0; i < width ; i++) for (int j = 0; j < height; j++) terrainArray[i][j].age = i + j;

		
		ObjectImager<TerrainBean> im1 = ImagerFactory.quickFactory(
				null, terrainArray, 100, true, true, "age", TerrainBean.class,
				gradCols, boolCols);
		im1.setDblFmt("%.1f");
		panels1 = new ArrayList<>();
		
		f1 = SwingUtils.frameFactory((int) (mult * width), (int) (mult * height),
				"Terrain Bean Panel Demo", 2, 2);
		panels1.add(ImagePanelFactory.buildPanel(
				im1, "elevation", 
				true, 0, 0, ptSize, 
				TerrainBean.class, null));
		panels1.add(ImagePanelFactory.buildPanel(
				im1, "elevation", 
				false, 0, 0, ptSize, 
				TerrainBean.class, null));
		panels1.add(ImagePanelFactory.buildPanel(
				im1, "elevation", 
				false, 100, 0, ptSize, 
				TerrainBean.class, null));
		panels1.add(ImagePanelFactory.buildPanel(
				im1, "elevation", 
				true, 400, 700, ptSize, 
				TerrainBean.class, null));

		panels1.get(0).labelPixels(font, color);
		panels1.get(1).labelPixels(font, color);
		panels1.get(3).labelPixels(font, color);

		for (JPanel j : panels1) f1.add(j);
		f1.setVisible(show);

		if (show && save) SwingUtils.saveFrameImage(f1, 
				"sampleOutput/array_image_multi_panel_demo.png");
	}


}
