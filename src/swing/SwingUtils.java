package swing;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;

public class SwingUtils 
{

	public static JFrame frameFactory(int width, int height) 
	{ return frameFactory(width, height, ""); }
	public static JFrame frameFactory(int width, int height, String title)
	{
		JFrame f = new JFrame(title);
		f.setSize(new Dimension(width, height));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return f;
	}
	public static JFrame frameFactory(int width, int height, String title, int nRow, int nCol)
	{
		JFrame f = frameFactory(width, height, title);
		f.setLayout(new GridLayout(nRow, nCol));
		return f;
	}
	
}
