package utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class SwingUtils 
{
	public static JFrame frameFactory(int width, int height) 
	{ return frameFactory(width, height, ""); }
	
	
	public static JFrame frameFactory(int width, int height, String title)
	{
		JFrame f = new JFrame(title);
		f.setSize(new Dimension(width, height));
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return f;
	}


	public static JFrame frameFactory(
			int width, int height,
			String title, int nRow, int nCol,
			double sizeMultiplier)
	{
		int wAdj = (int)(sizeMultiplier * (double) width);
		int hAdj = (int)(sizeMultiplier * (double) width);
		
		return frameFactory(wAdj, hAdj, title, nRow, nCol);
	}
	
	public static JFrame frameFactory(int width, int height, String title, int nRow, int nCol)
	{
		JFrame f = frameFactory(width, height, title);
		f.setLayout(new GridLayout(nRow, nCol));
		return f;
	}
	
	public static void saveFrameImage(JFrame frame, String fileName)
	
	{
		 BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
		 Graphics2D graphics2D = image.createGraphics(); 
		 frame.getContentPane().paint(graphics2D);
		
	     try {
			ImageIO.write(image, "png", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
