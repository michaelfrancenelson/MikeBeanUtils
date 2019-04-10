package demos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import beans.sampleBeans.TerrainBean;
import image.ArrayImageFactory;
import image.ColorInterpolator;
import image.ColorUtils;
import image.ObjectArrayImager;
import image.SimpleArrayImager;
import image.SimpleBooleanColorInterpolator;
import image.SimpleColorInterpolator;
import swing.SwingUtils;

public class ArrayImageFactoryDemo 
{
	static Font font = new Font("times", 2, 45);
	static Color[] bCol = new Color[] {Color.gray, Color.green};

static Border border = BorderFactory.createLineBorder(Color.black, 4);

	public static void main(String[] args) 
	{
		arrayImageDemo();
		gradientImageDemo();
	}

	/** A simple panel, filled with a resizing image. */
	public static class ImagePanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		public ImagePanel(Image img) { this.img = img; }
		Image img;

		@Override public void paintComponent(Graphics g)
		{
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
		
		
		
		
	}

	public static void arrayImageDemo()
	{
		int nRows = 303;
		int nCols = 504;

		TerrainBean[][] cells = TerrainBean.factory(nRows, nCols, 1.57, 75);

		Map<String, FieldWatcher<TerrainBean>> watchers = SimpleFieldWatcher.getWatcherMap(TerrainBean.class, null);
		
		ObjectArrayImager<TerrainBean> imager1 = SimpleArrayImager.factory(
				TerrainBean.class, cells, 
				"elevation", 
				ColorUtils.HEAT_COLORS, bCol,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null, null);		
		

		ImagePanel pp;
		List<ImagePanel> panels = new ArrayList<>();
		List<ImagePanel> panelsAge = new ArrayList<>();
		List<ImagePanel> panelsT = new ArrayList<>();
		List<ImagePanel> panelsAgeT = new ArrayList<>();
		for (int i = 0; i < 2; i++) for (int j = 0; j < 2;  j++)
		{
			imager1.setField("elevation");
			pp = new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, watchers.get("elevation"), imager1.getInterpolator(), i, j, false, true));
			pp.setBorder(border);
			panels.add(pp);
			pp = new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, watchers.get("elevation"), imager1.getInterpolator(), i, j, true, true));
			pp.setBorder(border);
			panelsT.add(pp);
			imager1.setField("age");
			pp = new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, watchers.get("age"), imager1.getInterpolator(), i, j, false, true));
			pp.setBorder(border);
			panelsAge.add(pp);
			imager1.setField("age");
			pp = new ImagePanel(ArrayImageFactory.buildArrayImage(
					cells, watchers.get("age"), imager1.getInterpolator(), i, j, true, true));
			pp.setBorder(border);
			panelsAgeT.add(pp);
		}
		
				
		JFrame f = SwingUtils.frameFactory(1500, 1500);

		f.setLayout(new GridLayout(4, 4));
		for (ImagePanel pan : panels) f.add(pan);
		for (ImagePanel pan : panelsAge) f.add(pan);
		for (ImagePanel pan : panelsAgeT) f.add(pan);
		for (ImagePanel pan : panelsT) f.add(pan);
		f.setVisible(true);
	}
	
	
	public static void gradientImageDemo()
	{
		int[] datInt = ArrayImageFactory.spacedIntervals(-50, 307, 5);
		

		int nStepsD = 100, nStepsI = 6;
		double minD = -150, maxD = 307;
		int minI = -5, maxI = -3;
		
		
		Border border = BorderFactory.createLineBorder(Color.black, 4);

		ColorInterpolator ci1 = SimpleColorInterpolator.factory(ColorUtils.TERRAIN_COLORS, -50, 307, Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null); 
		ColorInterpolator ciI = SimpleColorInterpolator.factory(ColorUtils.TERRAIN_COLORS, minI, maxI, Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null); 
		ColorInterpolator ci2 = SimpleBooleanColorInterpolator.factory(ColorUtils.BLUES, Color.gray); 
		ColorInterpolator ci3 = SimpleColorInterpolator.factory(ColorUtils.TOPO_COLORS, -50, 307, Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray, null); 

		List<ImagePanel> intImg = new ArrayList<>();
		List<ImagePanel> dblImg = new ArrayList<>();
		List<ImagePanel> boolImg = new ArrayList<>();
		for (int i = 1; i < 3; i++) for (int j = 1; j < 3; j++)
		{
			ImagePanel pp = new ImagePanel(ArrayImageFactory.buildGradientImage(minI, maxI, nStepsI, ciI, i, j));
			pp.setBorder(border);
			intImg.add(pp);
			pp = new ImagePanel(ArrayImageFactory.buildGradientImage(minD, maxD, nStepsD, ci3, i, j));
			pp.setBorder(border);
			dblImg.add(pp);
			pp = new ImagePanel(ArrayImageFactory.buildGradientImage(true, ci2, i, j));
			pp.setBorder(border);
			boolImg.add(pp);
		}

		JFrame f = SwingUtils.frameFactory(1200, 1200);
		f.setLayout(new GridLayout(3, 4));
		for (ImagePanel p : intImg) f.add(p);
		for (ImagePanel p : dblImg) f.add(p);
		for (ImagePanel p : boolImg) f.add(p);
		
		f.setVisible(true);
	}
	
	
	
	
}
