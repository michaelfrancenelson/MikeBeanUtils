package swing.stretchAndClick;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.lang.reflect.Field;

import javax.swing.JPanel;

import image.ObjectArrayImager;
import swing.ObjectArrayImagePanel;
import swing.ObjectArrayImageDecorator;

public class ObjectArrayJPanel<T> extends JPanel implements ObjectArrayImagePanel<T>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2893196948005659813L;

	private ObjectArrayImageDecorator decorator;
	private ObjectArrayImager<T> imager;
	private double imageAspectRatio, compAspectRatio;
	private boolean centerInPanel = true;

	private boolean fixedAspectRatio;
	private boolean fixedWidth, fixedHeight;
	private boolean fixedImg;
	private boolean decorate;

	private Image img = null;
	private double ptRelSize;

	private int 
	panelWidth, panelHeight, 
	imgDisplayWidth, imgDisplayHeight, 
	imgCornerX, imgCornerY;

	/** 
	 *  If the image is derived form an <code>ObjectArrayImager</code>, refresh the image to
	 *  reflect any changes in the objects' state. 
	 */
	public void updateImage()
	{
		if (!fixedImg) img = imager.getImage();
		paint(this.getGraphics());
	}

	public String queryDataArray(int i, int j)
	{
		T t = imager.getObjAt(i, j);
		String out = imager.getWatcher().getStringVal(t);
		return out;	
	}

	/**
	 * Ignored if the label image is not derived from an <code>ObjectArrayImager</code>
	 * 
	 * Truncates queries with coordinates outside the panel's dimensions.
	 * 
	 * @param i pixel coordinate within the panel.
	 * @param j pixel coordinate within the panel.
	 * @return a string representation of the data value of the object at the corresponding pixel.
	 */
	public String queryPixel(int i, int j)
	{
		if (fixedImg) return null;

		/* determine which cell in the data array corresponds to the input pixel */
		int relImgI = Math.max(0, Math.min(i - imgCornerX, getImgDisplayWidth()));;
		int relImgJ = Math.max(0, Math.min(j - imgCornerY, getImgDisplayHeight()));;

		double relX = ((double) relImgI) / ((double) getImgDisplayWidth());
		double relY = ((double) relImgJ) / ((double) getImgDisplayHeight());

		String out = queryRelative(relX, relY);
		System.out.println("Value of " + imager.getCurrentFieldName() + ": " + out);

		return out;
	}

	public String queryRelative(double relativeI, double relativeJ)
	{
		if (fixedImg) return null;
		T t = imager.getObjAt(relativeI, relativeJ);
		String out = imager.getWatcher().getStringVal(t);
		return out;
	}

	/** Set the image and image scaling properties of the panel.
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio
	 */
	void init(Image img, int width, int height, boolean keepAspectRatio, boolean fixedImage, ObjectArrayImager<T> imager) 
	{
		this.img = img;
		this.imager = imager;
		this.fixedImg = fixedImage;
		this.fixedAspectRatio = keepAspectRatio;
		this.imageAspectRatio = ((double) img.getWidth(null)) / ((double) img.getHeight(null));

		fixedWidth = false; fixedHeight = false;
		imgDisplayWidth = img.getWidth(null);
		imgDisplayHeight = img.getHeight(null);

		if (width > 0)  
		{
			fixedAspectRatio = false;
			fixedWidth = true;
			imgDisplayWidth = width;
		} 

		if (height > 0)
		{
			fixedAspectRatio = false;
			fixedHeight = true; 
			imgDisplayHeight = height;
		}

		this.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent arg0)
			{
				queryPixel(arg0.getX(), arg0.getY());
				System.out.println("MouseListener: Mouse clicked at panel coordinate (" +
						arg0.getX() + ", " + arg0.getY() + ").");
			}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseReleased(MouseEvent arg0) {}
		});
	}

	public int[] objArrayCoordsToPanelCoords(int objArrayI, int objArrayJ)
	{
		int imgI, imgJ;
		double imgRelativeI, imgRelativeJ;

		imgRelativeI = ((double) objArrayI ) / ((double) imager.getData().length);
		imgRelativeJ = ((double) objArrayJ ) / ((double) imager.getData()[0].length);

		imgI = (int) (((double) getImgDisplayWidth()) * imgRelativeI);
		imgJ = (int) (((double) getImgDisplayHeight()) * imgRelativeJ);

		return new int[] { imgI + imgCornerX, imgJ + imgCornerY };
	}

	@Override public void paintComponent(Graphics g)
	{
		Insets insets = getInsets();
		Graphics2D g2d = (Graphics2D) g.create();
		int fixedX = this.getImgDisplayWidth(), fixedY = this.getImgDisplayHeight();

		this.panelWidth = getWidth() - insets.right;
		this.panelHeight = getHeight() - insets.bottom;

		this.imgDisplayWidth = panelWidth; 
		this.imgDisplayHeight = panelHeight;

		compAspectRatio = (double) panelWidth / (double) panelHeight;

		/* If keeping the original aspect ratio. */
		if (fixedAspectRatio)
		{
			if (imageAspectRatio < compAspectRatio)	
			{
				double w = (((double) panelHeight) * imageAspectRatio);
				this.imgDisplayWidth = (int) w;
			}
			else this.imgDisplayHeight = (int) (((double) panelWidth) / imageAspectRatio);
		}
		else
		{
			if (this.fixedHeight) this.imgDisplayHeight = fixedY;
			if (this.fixedWidth) this.imgDisplayWidth = fixedX;
		}

		if (centerInPanel)
		{
			int widthRemainder = panelWidth - getImgDisplayWidth();
			int heightRemainder = panelHeight - getImgDisplayHeight();
			imgCornerX = (int)(0.5 * ((double) widthRemainder));
			imgCornerY = (int)(0.5 * ((double) heightRemainder));
		}
		else 
		{
			imgCornerX = 0;
			imgCornerY = 0;
		}

		g2d.drawImage(img, imgCornerX, imgCornerY, imgDisplayWidth, imgDisplayHeight, null);

		if (this.decorate)
		{
			decorator.drawLabels(g2d, imgDisplayWidth, imgDisplayHeight, imgCornerX, imgCornerY);
			decorator.drawPoints(g2d, imgDisplayWidth, imgDisplayHeight, imgCornerX, imgCornerY, ptRelSize);
		}

		g2d.dispose();
		g.dispose();
	}

	public void setField(String name)  { imager.setField(name); updateImage(); }
	public void setField(Field f) { imager.setField(f); updateImage(); }

	/**
	 * 
	 * @param label
	 * @param relI
	 * @param relJ
	 * @param font
	 */
	public void addLabel(String label, double relI, double relJ, Font font)
	{
		int[] coords = imager.getArrayCoords(relI, relJ);
		getDecorator().addLabel(coords[0], coords[1], label, font, Color.black, true, -1);
		paintComponent(this.getGraphics());
	}

	/**
	 * 
	 */
	public void addValueLabel(double relI, double relJ, Font font)
	{
		int[] coords = imager.getArrayCoords(relI, relJ);
		decorator.addLabel(coords[0], coords[1], null, font, Color.black, true, -1);
		paintComponent(this.getGraphics());
	}

	/**
	 * 
	 */
	public void addPoint(int i, int j, int size, Color color) 
	{
		decorator.addLabel(i, j, null, null, color, true, size); 
		paintComponent(this.getGraphics());
	}

	/**
	 * 
	 */
	public void addPoint(double relI, double relJ, int size, Color color)
	{
		int[] coords = imager.getArrayCoords(relI, relJ);
		decorator.addLabel(coords[0], coords[1], null, null, color, true, size);
		paintComponent(this.getGraphics());
	}

	/**
	 * 
	 * @param i x-coordinate (array index of data array) of the label
	 * @param j y-coordinate (array index of data array) of the label
	 * @param label text of the label.  If <code>null</code>, label will be 
	 *              either a point, or the text will display the value of the object at [i][j]
	 * @param font display font (if applicable)
	 * @param color color for text or point 
	 * @param keep  keep the decoration in the record of points so it will be redrawn later?
	 */
	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize, ObjectArrayJPanel<?> p)
	{
		decorator.addLabel(i, j, label, font, color, keep, pointSize); 
		paintComponent(this.getGraphics());
	}

	public Image getImg() { return this.img; }

	public RenderedImage getRenderedImage() { return (RenderedImage)this.img; }
	public Class<T> getObjClass() { return this.imager.getObjClass(); }

	public int getImgDisplayWidth() { return imgDisplayWidth; }
	public int getImgDisplayHeight() { return imgDisplayHeight; }
	public ObjectArrayImageDecorator getDecorator() { return decorator; }
	public void setDecorator(ObjectArrayImageDecorator decorator) { this.decorator = decorator; }
	public void setLabelVisibility(boolean b) { this.decorate = b; repaint();}

	public double getPtRelSize() { return ptRelSize; }
	public void setPtRelSize(double ptRelSize) { this.ptRelSize = ptRelSize; }
}
