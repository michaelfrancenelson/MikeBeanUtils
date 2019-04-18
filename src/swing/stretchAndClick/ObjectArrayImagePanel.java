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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import beans.memberState.FieldWatcher;
import image.arrayImager.ObjectArrayImager;

/**
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
public class ObjectArrayImagePanel<T> extends JPanel 
{

	/**
	 * 
	 */
	static final long serialVersionUID = -2893196948005659813L;

	private List<ObjectTextLabel>  labels = new ArrayList<>();
	private List<ObjectTextLabel>  valueLabels = new ArrayList<>();
	private List<ObjectPointLabel> points = new ArrayList<>();

	private ObjectArrayImager<T> imager;
	double imageAspectRatio, compAspectRatio;
	boolean centerInPanel = true;

	boolean fixedAspectRatio;
	boolean fixedWidth, fixedHeight;
	boolean fixedImg;
	boolean decorate;


	boolean isLegend;

	FieldWatcher<T> watcher;

	Image img = null;
	double ptRelSize;

	int 
	panelWidth, panelHeight, 
	imgDisplayWidth, imgDisplayHeight, 
	imgCornerX, imgCornerY;

	/** 
	 *  If the image is derived form an <code>ObjectArrayImager</code>, refresh the image to
	 *  reflect any changes in the objects' state. 
	 */
	public void updateImage()
	{
		this.watcher = imager.getWatcher();
		if (!fixedImg) 
		{
			if (!isLegend) img = imager.getImage();
			else img = imager.getLegendImage();
		}
		paint(this.getGraphics());
	}

	public String queryDataArray(int i, int j)
	{
		if (!isLegend)
		{
			T t = getImager().getObjAt(i, j);
			return watcher.getStringVal(t);
		}
		else 
			return imager.queryLegendAt(i, j);	
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
		int relImgI = Math.max(0, Math.min(i - imgCornerX, imgDisplayWidth));;
		int relImgJ = Math.max(0, Math.min(j - imgCornerY, imgDisplayHeight));;

		double relX = ((double) relImgI) / ((double) imgDisplayWidth);
		double relY = ((double) relImgJ) / ((double) imgDisplayHeight);

		String out = queryRelative(relX, relY);
		System.out.println("ObjectArrayImager.queryPixel() Value of " + watcher.getFieldName() + ": " + out);

		return out;
	}

	public String queryRelative(double relativeI, double relativeJ)
	{
		if (fixedImg) return null;
		if (! isLegend)
		{
			T t = imager.getObjAt(relativeI, relativeJ);
			return watcher.getStringVal(t);
		}
		else
			System.out.println("ObjectArrayImager.queryRelative() legend value at (" + relativeI + ", " + relativeJ + ")");
			return imager.queryLegendAt(relativeI, relativeJ);
	}


	public ObjectArrayImagePanel<T> getLegendPanel()
	{

		int w = 0, h = 0;
		if (fixedWidth) w = imgDisplayWidth;
		if (fixedHeight) h = imgDisplayHeight;

		ObjectArrayImagePanel<T> out = ObjectArrayPanelFactory.buildPanel(
				imager, fixedAspectRatio, true, w, h, ptRelSize);
		return out;
	}

	/** Set the image and image scaling properties of the panel.
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio
	 */
	@Deprecated
	void init(Image img, int width, int height, 
			boolean keepAspectRatio, boolean fixedImage, ObjectArrayImager<T> imager) 
	{
		init(imager, width, height, keepAspectRatio, false, fixedImage);
	}
	
	void init(ObjectArrayImager<T> imager, 
			int width, int height, 
			//			void init(Image img, int width, int height, 
			boolean keepAspectRatio, boolean isLegend, boolean fixedImage)

	{
		this.setImager(imager);
		this.isLegend = isLegend;
		if (this.isLegend)
			this.img = imager.getLegendImage();
		else this.img = imager.getImage();
		this.fixedImg = fixedImage;
		this.fixedAspectRatio = keepAspectRatio;
		this.imageAspectRatio = ((double) img.getWidth(null)) / ((double) img.getHeight(null));

		this.watcher = imager.getWatcher();

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
				System.out.println("MouseListener: Mouse clicked at panel coordinate (" +
						arg0.getX() + ", " + arg0.getY() + ").");
				queryPixel(arg0.getX(), arg0.getY());
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

		imgI = (int) (((double) imgDisplayWidth) * imgRelativeI);
		imgJ = (int) (((double) imgDisplayHeight) * imgRelativeJ);

		return new int[] { imgI + imgCornerX, imgJ + imgCornerY };
	}

	@Override public void paintComponent(Graphics g)
	{
		Insets insets = getInsets();
		Graphics2D g2d = (Graphics2D) g.create();
		int fixedX = this.imgDisplayWidth, fixedY = this.imgDisplayHeight;

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
			int widthRemainder = panelWidth - imgDisplayWidth;
			int heightRemainder = panelHeight - imgDisplayHeight;
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
			drawLabels(g2d, imgDisplayWidth, imgDisplayHeight, imgCornerX, imgCornerY);
			drawPoints(g2d, imgDisplayWidth, imgDisplayHeight, imgCornerX, imgCornerY, ptRelSize);
		}

		if (this.getBorder() != null) paintBorder(g2d);
		g2d.dispose();
		g.dispose();
	}

	public void setField(String name)  { getImager().setField(name); updateImage(); }
	public void setField(Field f) { getImager().setField(f); updateImage(); }

	/**
	 * 
	 * @param label
	 * @param relI
	 * @param relJ
	 * @param font
	 */
	public void addTextLabel(String label, double relI, double relJ, Font font)
	{
		int[] coords = imager.getArrayCoords(relI, relJ);
		addLabel(coords[0], coords[1], label, font, Color.black, true, -1);
	}

	/**
	 * 
	 */
	public void addValueLabel(double relI, double relJ, Font font)
	{
		int[] coords = imager.getArrayCoords(relI, relJ);
		String label = queryDataArray(coords[0], coords[1]);
		addLabel(coords[0], coords[1], label, font, Color.black, true, -1);
	}

	/**
	 * 
	 */
	public void addPoint(int i, int j, int size, Color color)
	{ addLabel(i, j, null, null, color, true, size); }

	/**
	 * 
	 */
	public void addPoint(double relI, double relJ, int size, Color color)
	{
		int[] coords = imager.getArrayCoords(relI, relJ);
		addLabel(coords[0], coords[1], null, null, color, true, size);
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
	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize, ObjectArrayImagePanel<?> p)
	{
		addLabel(i, j, label, font, color, keep, pointSize); 
	}

	public Image getImg() { return this.img; }

	public RenderedImage getRenderedImage() { return (RenderedImage)this.img; }
	public Class<T> getObjClass() { return this.getImager().getObjClass(); }

//	public int getImgDisplayWidth() { return imgDisplayWidth; }
//	public int getImgDisplayHeight() { return imgDisplayHeight; }
	public void setLabelVisibility(boolean b) { this.decorate = b; repaint();}

	public double getPtRelSize() { return ptRelSize; }
	public void setPtRelSize(double ptRelSize) { this.ptRelSize = ptRelSize; }

	public ObjectArrayImager<T> getImager() { return imager; }
	public void setImager(ObjectArrayImager<T> imager) { this.imager = imager; }


	/**
	 * A label whose coordinates are calculated relative to the dimensions of the object array.
	 * @author michaelfrancenelson
	 *
	 */
	private class ObjectPointLabel
	{
		int objArrayI, objArrayJ;
		int size;
		Color color;

		ObjectPointLabel(int i, int j, int size, Color color)
		{ this.objArrayI = i; this.objArrayJ = j; this.size = size; this.color = color;	}

		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY, int scaledSize)
		{
			g.setColor(color);
			int[] coords = getImageCoords(this.objArrayI, this.objArrayJ, imageWidth, imageHeight, imageCornerX, imageCornerY);
			if (this.size > 0)
				g.fillOval(coords[0], coords[1], size, size);
			else
				g.fillOval(coords[0], coords[1], scaledSize, scaledSize);
		}

		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
		{
			int[] coords = getImageCoords(this.objArrayI, this.objArrayJ, imageWidth, imageHeight, imageCornerX, imageCornerY);
			g.fillOval(coords[0], coords[1], size, size);
		}
	}

	/**
	 * A label whose coordinates are calculated relative to the dimensions of the object array.
	 * @author michaelfrancenelson
	 *
	 */
	private class ObjectTextLabel
	{
		int objArrayI, objArrayJ;
		String label;
		Font font;

		ObjectTextLabel(int i, int j, String label, Font font)
		{
			this.objArrayI = i; this.objArrayJ = j; 
			this.label = label; this.font = font;
		}

		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
		{
			g.setFont(font);
			int[] coords = getImageCoords(this.objArrayI, this.objArrayJ, imageWidth, imageHeight, imageCornerX, imageCornerY);
			if (label != null)
				g.drawString(this.label, coords[0], coords[1]);
			else
			{
				String l = imager.queryObjectAt(objArrayI, objArrayJ);
				g.drawString(l, coords[0], coords[1]);
			}
		}
	}

	public void drawPoints(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY) 
	{
		for (ObjectPointLabel l : points)
		{
			l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY);
		}
	}


	public void drawPoints(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY, double relSize) 
	{
		int minDim = Math.min(imgWidth, imgHeight);
		int scaledSize = (int) (((double) minDim) * relSize);
		for (ObjectPointLabel l : points)
			l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY, scaledSize);
	}

	private int[] getImageCoords(int objArrayI, int objArrayJ, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
	{
		double relX = ((double) objArrayI) / ((double) imager.getData().length); 
		double relY = ((double) objArrayJ) / ((double) imager.getData()[0].length);

		return new int[] {
				imageCornerX + (int) (relX * ((double) imageWidth)), 
				imageCornerY + (int) (relY * ((double) imageHeight)) };
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
	 * @param p     Panel to decorate.  If null, the label will not be drawn.
	 */
	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize)
	{
		if (label != null && font != null)
		{
			ObjectTextLabel lab = new ObjectTextLabel(i, j, label, font);
			if (keep) labels.add(lab); 
		}
		else if (label == null && font != null)
		{
			ObjectTextLabel vab = new ObjectTextLabel(i, j, null, font);
			if (keep) valueLabels.add(vab);
		}
		else 
		{
			ObjectPointLabel pab = new ObjectPointLabel(i, j, pointSize, color);
			System.out.println("PanelDecorator.addLabel() creating a point label");
			if (keep) 
				points.add(pab);
		}
	}

	public void drawLabels(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY)
	{
		for (ObjectTextLabel l : labels) l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
		for (ObjectTextLabel l : valueLabels) l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
		drawPoints(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
	}

	public void drawTextLabels(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY) 
	{ for (ObjectTextLabel l : labels) l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY); }

	public void drawValueLabels(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY) 
	{ for (ObjectTextLabel l : valueLabels) l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY);	}
}
