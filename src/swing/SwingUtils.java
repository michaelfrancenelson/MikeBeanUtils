package swing;

import java.awt.Dimension;

import javax.swing.JFrame;

public class SwingUtils 
{

	public static JFrame frameFactory(int width, int height)
	{
		JFrame f = new JFrame();
		f.setSize(new Dimension(width, height));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return f;
	}
	
}
