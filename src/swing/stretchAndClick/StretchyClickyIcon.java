package swing.stretchAndClick;
/**
 * @(#)StretchIcon.java	1.0 03/27/12
 */


import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import images.ArrayDataImageBundle.ArrayDataImageFactory;

/**
 * An extension of the StretchIcon from: <br> 
 * {@link https://tips4java.wordpress.com/2012/03/31/stretch-icon/}
 *
 *  <br> 
 *  With the added functionalities: <br> 
 *  process map mouse clicks to the corresponding pixel of the original image. <br>
 *  draw 
 *   
 *   
 *   <br> Original Description from Darryl: <br>
 * An <CODE>Icon</CODE> that scales its image to fill the component area,
 * excluding any border or insets, optionally maintaining the image's aspect
 * ratio by padding and centering the scaled image horizontally or vertically.
 * <P>
 * The class is a drop-in replacement for <CODE>ImageIcon</CODE>, except that
 * the no-argument constructor is not supported.
 * <P>
 * As the size of the Icon is determined by the size of the component in which
 * it is displayed, <CODE>StretchIcon</CODE> must only be used in conjunction
 * with a component and layout that does not depend on the size of the
 * component's Icon.
 * 
 * 
 * @version 1.0 03/27/12
 * @author Darryl, modified by michaelfrancenelson
 */
public class StretchyClickyIcon extends ImageIcon
{
	
	/**	 */
	private static final long serialVersionUID = -8759874525253988985L;
	
	private double aspectRatio;
	protected boolean constantWidth;
	private int width = 0;

	protected int 
	iconImgX, iconImgY, 
	iconImgWidth, iconImgHeight, 
	componentWidth, componentHeight;

	protected double[] mouseIconImgRatios;
	protected double[] mouseComponentRatios;

	protected int[] mouseCompCoord;
	protected int[] mouseIconImgCoord;
	protected int[] mouseDatArrayCoord;

	protected int sourceImgWidth, sourceImgHeight;
	private int currentDataPixelWidth, currentDataPixelHeight;

	private StretchyClickyIcon iconToMatch;
	private boolean matchHeight;

	public void setMatchIcon(StretchyClickyIcon i)
	{
		matchHeight = true;
		iconToMatch = i;
	}

	/** Use the factory to instantiate. */
	private StretchyClickyIcon() {}

	/**
	 * Determines whether the aspect ratio of the image is maintained when resizing.
	 * Set to <code>false</code> to allow the image to distort to fill the component.
	 */
	protected boolean keepAspectRatio = true;
	private boolean verbose;
	private double compAspectRatio;

	/** Print inner working messages to the console?  For testing. */
	public void setVerbose(boolean b) { this.verbose = b; }

	/** Calculate the aspect ratio of the original icon image. */
	void setImageAspectRatio()
	{
		Image source = getImage(); aspectRatio = getAspectRatio(source.getWidth(null), source.getHeight(null));
		sourceImgHeight = source.getHeight(null);
		sourceImgWidth = source.getWidth(null);
	}

	public void setFixedWidth(int width) 
	{ this.width = width; this.constantWidth = true; this.keepAspectRatio = false; }

	/**
	 * Paints the icon.  The image is reduced or magnified to fit the component to which
	 * it is painted.
	 * <P>
	 * If the proportion has not been specified, or has been specified as <code>true</code>,
	 * the aspect ratio of the image will be preserved by padding and centering the image
	 * horizontally or vertically.  Otherwise the image may be distorted to fill the
	 * component it is painted to.
	 * <P>
	 * If this icon has no image observer,this method uses the <code>c</code> component
	 * as the observer.
	 *
	 * @param c the component to which the Icon is painted.  This is used as the
	 *          observer if this icon has no image observer
	 * @param g the graphics context
	 * @param x not used.
	 * @param y not used.
	 *
	 * @see ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y)
	{
		Image image = getImage(); 
		ImageObserver io = getImageObserver();

		if (image == null) { return; }
		Insets insets = ((Container) c).getInsets();
		iconImgX = insets.left; iconImgY = insets.top;

		this.componentWidth = c.getWidth() - iconImgX - insets.right;
		this.componentHeight = c.getHeight() - iconImgY - insets.bottom;
		this.iconImgWidth = componentWidth; this.iconImgHeight = componentHeight;

		compAspectRatio = (double) componentWidth / (double) componentHeight;

		/* If keeping the original aspect ratio. */
		if (keepAspectRatio) {
			if (aspectRatio < compAspectRatio)
				iconImgWidth = (int) ((double) componentHeight * aspectRatio);
			else iconImgHeight = (int) ((double) componentWidth / aspectRatio);
		}

		if (constantWidth)
		{ 
			if (verbose) System.out.println("fixed width of " + width);
			this.iconImgWidth = width; this.iconImgHeight = componentHeight;
		}

		iconImgX += (componentWidth - iconImgWidth) / 2;
		iconImgY += (componentHeight - iconImgHeight) / 2;

		if (verbose)
		{
			System.out.println("component aspect ratio: " + compAspectRatio);
			System.out.println("image aspect ratio: " + aspectRatio);
			System.out.println("old comp. width  = " + componentWidth);
			System.out.println("new image. width  = " + iconImgWidth);
			System.out.println("old comp height = " + componentHeight);
			System.out.println("new image. height = " + iconImgHeight);
		}

		if (matchHeight)
		{
			iconImgHeight = iconToMatch.iconImgHeight;
			iconImgY = iconToMatch.iconImgY;
		}

		this.currentDataPixelWidth = this.iconImgWidth / sourceImgWidth;
		this.currentDataPixelHeight = this.iconImgHeight / sourceImgHeight;
		g.drawImage(image, iconImgX, iconImgY, iconImgWidth, iconImgHeight, io == null ? c : io);
	}

	public int getImageDataNRows() { return sourceImgHeight; }
	public int getImageDataNCols() { return sourceImgWidth; }
	public int getClickedDataX() { return mouseDatArrayCoord[0]; }
	public int getClickedDataY() { return mouseDatArrayCoord[1]; }
	private double getAspectRatio(int width, int height) { return (double) width / (double) height; }
	public void setConstantWidth(int width) { this.width = width; this.constantWidth = true; this.keepAspectRatio = false;}
	public int getImageHeight() { return iconImgHeight; }
	public int getImageWidth() { return iconImgWidth; }
	public int getX() { return iconImgX; } 
	public int getY() { return iconImgY; }
	public int getDataPixelWidth() { return currentDataPixelWidth;}
	public int getDataPixelHeight() { return currentDataPixelHeight;}

	
	/** Determine the relative position of a coordinate within a range. 
	 * 
	 * @param test coordinate - can be greater than max
	 * @param max maximum lenght of dimension
	 * @return the relative position of test in the interval (0, max).  Truncated to the range 0.0 - 1.0
	 */
	private double boundedRatio (int test, int max)
	{
		double ratio = ((double) test) / ((double) max);
		return Math.max(0.0, Math.min(1.0, ratio));
	}

	/** Set the coordinate values for the location of the mouse click.
	 * 
	 * @param arg0
	 */
	public void setMouseEventCoords(MouseEvent arg0)
	{
		mouseCompCoord = componentClickCoords(arg0);
		mouseIconImgCoord = imgClickCoords(mouseCompCoord);

		mouseIconImgRatios = new double[]
				{
						boundedRatio(mouseIconImgCoord[0], iconImgWidth),
						boundedRatio(mouseIconImgCoord[1], iconImgHeight)
				};

		mouseDatArrayCoord = new int[]
				{
						Math.min(
								sourceImgWidth - 1,
								(int) (mouseIconImgRatios[0] * (double) sourceImgWidth)),
						Math.min(
								sourceImgHeight - 1,
								(int) (mouseIconImgRatios[1] * (double) sourceImgHeight))
				};

		mouseComponentRatios = new double[]
				{
						boundedRatio(mouseCompCoord[0], componentWidth),
						boundedRatio(mouseCompCoord[1], componentHeight)
				};
	}

	/**
	 * @param arg0
	 * @return The coordinates of the mouse click within the icon
	 */
	private int[] componentClickCoords(MouseEvent arg0)
	{
		return new int[] { arg0.getX(), arg0.getY() };
	}

	
	private int[] imgClickCoords(int[] xy)
	{ return (new int[] {xy[0] - this.getX(), xy[1] - this.getY()}); }

	public int[] getDataMouseCoords(MouseEvent arg0)
	{
		int[] compCoord = componentClickCoords(arg0);
		int[] iconImgCoord = imgClickCoords(compCoord);

		double[] iconImgRatios = new double[]
				{		boundedRatio(iconImgCoord[0], iconImgWidth),
						boundedRatio(iconImgCoord[1], iconImgHeight)
				};

		int[] datArrayCoord = new int[]
				{ 	Math.min(
						sourceImgWidth - 1,
						(int) (iconImgRatios[0] * (double) sourceImgWidth)),
						Math.min(
								sourceImgHeight - 1,
								(int) (iconImgRatios[1] * (double) sourceImgHeight))
				};

		return datArrayCoord;
	}

	public int componentYFromDataRow(int dataRow)
	{
		double rowRatio = boundedRatio(dataRow, sourceImgHeight);
		int displayImageRow = iconImgY + (int) ((double) iconImgHeight * rowRatio);
		return displayImageRow;
	}

	public int componentXFromDataCol(int dataColumn)
	{
		double rowRatio = boundedRatio(dataColumn, sourceImgWidth);
		int displayImageColumn = iconImgX + (int) ((double) iconImgWidth * rowRatio);
		return displayImageColumn;
	}

	public void printComponentClick()
	{	System.out.println(String.format("Component mouse click at (%d, %d)", 
			mouseCompCoord[0], mouseCompCoord[1]));
	}

	public void printDisplayImageClick()
	{	System.out.println(String.format("Displayed image mouse click at (%d, %d)", 
			mouseIconImgCoord[0], mouseIconImgCoord[1]));
	}

	public void printComponentClickRatio()
	{ System.out.println(String.format("Component mouse click at %.2f%% of component width.", 
			100.0 * mouseComponentRatios[0]));
	System.out.println(String.format("Component mouse click at %.2f%% of component height.", 
			100.0 * mouseComponentRatios[1]));
	}

	public void printDisplayImageClickRatio()
	{
		System.out.println(String.format("Icon image mouse click at %.2f%% of image width.", 
				100.0 * mouseIconImgRatios[0]));
		System.out.println(String.format("Icon image mouse click at %.2f%% of image height.",
				100.0 * mouseIconImgRatios[1]));
	}

	public void printDataClick()
	{
		System.out.println(String.format("Source mouse click at (%d, %d)", 
				mouseDatArrayCoord[0], mouseDatArrayCoord[1]));
	}

	public int getDisplayedColumn(int dataColumn)
	{
		double xProportion =
				((double) dataColumn + 0.5) / (double) sourceImgWidth;

		int proportionalX = (int)((double)iconImgWidth * xProportion);
		if (verbose) printDisplayImageDimensions();
		return proportionalX;
	}

	void printDisplayImageDimensions()
	{
		System.out.println("icon image display width: " + iconImgWidth);
		System.out.println("icon image display height: " + iconImgHeight);
		System.out.println("icon image origin X " + iconImgX);
		System.out.println("icon image origin Y " + iconImgY);
	}

	void printDisplayImageCornerCoords()
	{
		int x0 = iconImgX, x1 = iconImgX + iconImgWidth;
		int y0 = iconImgY, y1 = iconImgY + iconImgHeight;
		System.out.println(String.format("image coords: (%d, %d), (%d, %d), (%d, %d), (%d, %d)",
				x0, y0, x0, y1, x1, y0, x1, y1));
	}
	void printSourceImageDimensions()
	{
		System.out.println(String.format("source image height: %d, source image width: %d",
				sourceImgHeight, sourceImgWidth));	
	}

	public Graphics getIconGraphics() { return getImage().getGraphics(); }

	/**
	 * Overridden to return 0.  The size of this Icon is determined by
	 * the size of the component.
	 * 
	 * @return 0
	 */
	@Override
	public int getIconWidth() {
		return 0;
	}

	/**
	 * Overridden to return 0.  The size of this Icon is determined by
	 * the size of the component.
	 *
	 * @return 0
	 */
	@Override
	public int getIconHeight() {
		return 0;
	}

	public static void main(String[] args) 
	{
		franceDemo();
		randomDataDemo();
	}

	public static void randomDataDemo()
	{
		JFrame frame = StretchyClickyJLabel.getFrame("StretchyClickyIcon random data demo");

		StretchyClickyIcon icon1 = ArrayDataImageFactory.getRandomBundle(11, 18).createIcon(true);
		StretchyClickyIcon icon2 = ArrayDataImageFactory.getRandomBundle(11, 18).createIcon(false);
		StretchyClickyIcon icon3 = ArrayDataImageFactory.getRandomBundle(11, 18).createIcon(false);
		icon3.setConstantWidth(68);
		JLabel label1 = new JLabel(icon1);
		JLabel label2 = new JLabel(icon2);
		JLabel label3 = new JLabel(icon3);

		//		icon3.forceHeight = true;
		//		icon3.forcedHeight = 45;

		//		label3.setForceHeight(true);
		//		label3.setForcedHeight(45);

		frame.setLayout(new GridLayout());
		frame.add(label1);
		frame.add(label2);
		frame.add(label3);
		frame.setVisible(true);
	}

	public static void franceDemo()
	{
		JFrame frame = StretchyClickyJLabel.getFrame("StretchyClickyIcon image file demo");

		StretchyClickyIcon icon1 = new StretchyClickyIcon("testData/france.jpg");
		icon1.setFixedWidth(500);
		StretchyClickyIcon icon2 = new StretchyClickyIcon("testData/france.jpg");

		JLabel label1 = new JLabel(icon1);
		JLabel label2 = new JLabel(icon2);

		frame.setLayout(new GridLayout());
		frame.add(label1);
		frame.add(label2);
		frame.setVisible(true);
	}
}
