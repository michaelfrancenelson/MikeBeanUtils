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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.memberState.FieldWatcher;
import imaging.imagers.BeanImager;
import imaging.imagers.PanelLabel;
import swing.ObjectArrayImageComboBox;
import utils.ArrayUtils;
import utils.FieldUtils;

/**
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
public class ObjectImagePanel<T> extends JPanel 
{
	/**
	 * 
	 */
	static final long serialVersionUID = -2893196948005659813L;
	static Logger logger = LoggerFactory.getLogger(ObjectImagePanel.class);

	private List<PanelLabel>  labels = new ArrayList<>();
	private List<PanelLabel>  valueLabels = new ArrayList<>();
	private List<PanelLabel> points = new ArrayList<>();

	private String currentClickValue;

	private BeanImager<T> imager;
	private ObjectImagePanel<T> legendPanel;
	private FieldWatcher<T> watcher;
	private Image image = null;

	private JComboBox<String> controlComboBox;

	private double imageAspectRatio, compAspectRatio;

	private boolean centerInPanel = true;
	private boolean fixedAspectRatio, fixedWidth, fixedHeight, fixedImg, decorate;
	private boolean isLegend;
	double ptRelSize;
	int 
	panelWidth, panelHeight, 
	imgDisplayWidth, imgDisplayHeight, 
	imgCornerX, imgCornerY;


	void init(
			BeanImager<T> imgr, 
			int width, int height, 
			boolean keepAspectRatio, 
			boolean legend)
	{
		fixedWidth = false; fixedHeight = false;
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

		if (this.image != null)	this.fixedImg = true;
		else if (imgr == null)
			throw new IllegalArgumentException("Either an Image or a BeanImager must be passed to init()");
		else
		{
			this.imager = imgr;
			this.isLegend = legend;
			this.watcher = imager.getWatcher();
			if (isLegend) image = imager.getLegendImage();
			else image = imager.getImage();

			this.addMouseListener(new MouseListener() {
				@Override public void mouseClicked(MouseEvent click)
				{
					int mouseX = click.getX(); int mouseY = click.getY();

					double[] imgRelCoords = 
							new double[]
									{
											ArrayUtils.absToRelCoord(mouseX - imgCornerX, imgDisplayWidth),
											ArrayUtils.absToRelCoord(mouseY - imgCornerY, imgDisplayHeight)
									};
					currentClickValue = imager.queryDataAt(imgRelCoords[0], imgRelCoords[1]); 					logger.debug(String.format("Value of %s: %s", 
							imager.getCurrentFieldName(), currentClickValue));
				}
				@Override public void mouseEntered(MouseEvent arg0) {}
				@Override public void mouseExited(MouseEvent arg0) {}
				@Override public void mousePressed(MouseEvent arg0) {}
				@Override public void mouseReleased(MouseEvent arg0) {}
			});
		}

		if (this.image == null)
			throw new IllegalArgumentException("Not able to create image from field " 
					+ imgr.getCurrentFieldName() + " of type " +
					imgr.getCurrentField().getType());

		this.fixedAspectRatio = keepAspectRatio;
		this.imageAspectRatio = ((double) image.getWidth(null)) / ((double) image.getHeight(null));

		imgDisplayWidth = image.getWidth(null);
		imgDisplayHeight = image.getHeight(null);
	}

	/** 
	 *  If the image is derived form an <code>ObjectArrayImager</code>, refresh the image to
	 *  reflect any changes in the objects' state. 
	 */
	public void updateImage()
	{
		if (!fixedImg) 
		{
			this.watcher = imager.getWatcher();
			imager.refresh();
			image = imager.getImage();
			if (legendPanel != null)
			{
				logger.debug("ObjectArrayImagePanel: updating legend image to field " +
						watcher.getFieldName());
				legendPanel.watcher = watcher;
				legendPanel.image = imager.getLegendImage();
				legendPanel.paintComponent(legendPanel.getGraphics());
			}
			logger.debug("ObjectArrayImagePanel: updating array image to field " + watcher.getFieldName());
			paintComponent(this.getGraphics());
		}
	}

	public ObjectImagePanel<T> getLegendPanel()
	{
		int w = 0, h = 0;
		if (fixedWidth) w = imgDisplayWidth;
		if (fixedHeight) h = imgDisplayHeight;
		legendPanel = ObjectArrayPanelFactory.buildLegendPanel(
				imager, watcher.getFieldName(),
				fixedAspectRatio, 
				w, h, ptRelSize
				);
		return legendPanel;
	}

	public JComboBox<String> getControlComboBox(
			Font font
			)
	{
		final List<Field> f2;
		f2 = FieldUtils.getFields(
				imager.getObjClass(), imager.getAnnClass(), true, true);
		List<String> dispNames = FieldUtils.getFieldNames(
				f2, imager.getObjClass(), 
				imager.getAnnClass(), true
				);

		controlComboBox = ObjectArrayImageComboBox.comboBoxFactory(
				this, imager.getAnnClass(), 
				f2, dispNames, font);

		//		controlComboBox = new JComboBox<String>(dispNames);
		//
		//		controlComboBox.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e)
		//			{
		//				System.out.println("ObjectArrayImagePanel: setting field to " +
		//						f2.get(controlComboBox.getSelectedIndex()).getName());
		//
		//				setField(f2.get(controlComboBox.getSelectedIndex()));
		//			}
		//		});

		if (font != null) controlComboBox.setFont(font);
		return controlComboBox;
	}

	public String queryRelative(double relativeI, double relativeJ)
	{
		if (fixedImg) return null;
		if (! isLegend)
		{
			return imager.queryDataAt(relativeI, relativeJ);
		}
		else
		{
			logger.debug("ObjectArrayImagePanel.queryRelative()"
					+ " legend value at (" + relativeI + ", " + relativeJ + ")");
			return imager.queryLegendAt(relativeI, relativeJ);
		}
	}

	/** Set the image and image scaling properties of the panel.
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio
	 */
	void init(Image img, int width, int height, boolean keepAspectRatio) 
	{
		this.image = img;
		init(null, width, height, keepAspectRatio, false);
	}

	@Override public void paintComponent(Graphics g)
	{
		int datWidth  = imager.getImgData().getWidth();
		int datHeight = imager.getImgData().getHeight();
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

		g2d.drawImage(image, imgCornerX, imgCornerY, imgDisplayWidth, imgDisplayHeight, null);

		int cellWidth  = (int) ((double) datWidth  / (double) imgDisplayWidth);
		int cellHeight = (int) ((double) datHeight / (double) imgDisplayHeight);

		if (decorate)
		{
			for (PanelLabel p : labels)
				p.draw(g2d, imgDisplayWidth, imgDisplayHeight, 
						cellWidth, cellHeight, imgCornerX, imgCornerY);

			for (PanelLabel p : valueLabels)
				p.draw(g2d, imgDisplayWidth, imgDisplayHeight, 
						cellWidth, cellHeight, imgCornerX, imgCornerY);
			for (PanelLabel p : points)
				p.draw(g2d, imgDisplayWidth, imgDisplayHeight, 
						cellWidth, cellHeight, imgCornerX, imgCornerY);
		}


		if (this.getBorder() != null) paintBorder(g2d);
		g2d.dispose();
		g.dispose();
	}

	public void setField(String name)  { getImager().setField(name); updateImage(); }
	public void setField(Field f) { getImager().setField(f); updateImage(); }

	public void labelPixels(Font font, Color color)
	{
		double relI, relJ;
		int datWidth = imager.getImgData().getWidth();
		int datHeight = imager.getImgData().getHeight();
		for (int row = 0; row < imager.getImgData().getHeight(); row++)
			for (int col = 0; col < imager.getImgData().getWidth(); col++)
			{
				relI = ArrayUtils.absToRelCoord(col, datWidth);
				relJ = ArrayUtils.absToRelCoord(row, datHeight);
				addValueLabel(relI, relJ, font, color);
			}
	}

	private void labelFromImageRelCoords(
			double relI, double relJ,
			String label, Font font, Color color, 
			int pointSize, String type)
	{
		if (color == null) color = Color.black;
		PanelLabel p = PanelLabel.fromRelImgCoords(
				relI, relJ, pointSize,
				font, color, label);
		switch(type)
		{
		case("label"): labels.add(p); break;
		case("valueLabel"): valueLabels.add(p); break;
		case("point"): points.add(p); break;
		}
	}

	public void addLabel(
			double relI, double relJ, String label, Font font, Color color)
	{
		if (font == null) font = this.getFont();
		labelFromImageRelCoords(relI, relJ, label, font, color, -9999, "value label");
	}

	public void addValueLabel(
			double relI, double relJ, Font font, Color color)
	{
		if (font == null) font = this.getFont();
		String label = imager.getImgData().queryData(relI, relJ, imager.getWatcher());
		labelFromImageRelCoords(
				relI, relJ, label, font, color, -9999, "value label");
		//		addLabel(dataX, dataY, label, font, Color.black, true, -1);

		//		int[] coords = new int[] {
		//				ArrayUtils.getRelativeIndex(relI, imager.getImgData().getWidth()),
		//				ArrayUtils.getRelativeIndex(relJ, imager.getImgData().getHeight())
		//		};
		//		addValueLabel(coords[0], coords[1], font);
		//		addLabel(dataX, dataY, label, font, Color.black, true, -1);
		//		addValueLabel(coords[0], coords[1], font);
	}

	/**
	 * 
	 */
	public void addPoint(double relI, double relJ, int size, Color color)
	{
		labelFromImageRelCoords(relI, relJ, null, null, color, size, "point");
		//		addLabel(relI, relJ, null, null, color, true, size);
	}


	public Image getImg() { return this.image; }

	public RenderedImage getRenderedImage() { return (RenderedImage)this.image; }
	public Class<T> getObjClass() { return this.getImager().getObjClass(); }
	public Class<? extends Annotation> getAnnClass() { return this.getImager().getAnnClass(); }

	public void setLabelVisibility(boolean b) { this.decorate = b; repaint();}

	public double getPtRelSize() { return ptRelSize; }
	public void setPtRelSize(double ptRelSize) { this.ptRelSize = ptRelSize; }
	public BeanImager<T> getImager() { return imager; }
	public String getCurrentClickValue() { return currentClickValue; }
	public int getImgDisplayWidth() { return imgDisplayWidth;}
	public int getImgDisplayHeight() { return imgDisplayHeight; }


	/**
	 * This method makes a "deep clone" of any Java object it is given.
	 * @author Alvin Alexander, http://alvinalexander.com
	 */
	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
/**

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

	private int[] getDataCellImageBoundingBox(
			int objDataI, int objDataJ,
			int imageWidth, int imageHeight, int imageCornerX, int imageCornerY
			)
	{
		objDataJ++;
		int[] out = new int[4];
		int[] xy0 = getImageCoords(objDataI, objDataJ, imageWidth, imageHeight, imageCornerX, imageCornerY, false);
		int[] xy1 = getImageCoords(objDataI + 1, objDataJ + 1, imageWidth, imageHeight, imageCornerX, imageCornerY, false);
		out = new int[] { xy0[0], xy0[1], 
				Math.max(xy0[0], xy1[0] - 1), Math.max(xy0[1], xy1[1] - 1) }; 
		return out;
	}

	private int[] getImageCoords(
			int objDataI, int objDataJ,
			int imageWidth, int imageHeight, int imageCornerX, int imageCornerY,
			boolean center)
	{
		double relX, relY, offset;
		if (center) offset = 0.5;
		else offset = 0.0;

		relX = ((double) objDataI + offset) / ((double) imager.getImgData().getWidth()); 
		relY = ((double) objDataJ + offset) / ((double) imager.getImgData().getHeight());

		return new int[] {
				imageCornerX + (int) (relX * ((double) imageWidth)), 
				imageCornerY + (int) (relY * ((double) imageHeight)) };
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
//	public String queryDataArray(int i, int j)
//	{
//		if (!isLegend)
//		{
//			T t = imager.getObjAt(i, j);
//			return watcher.getStringVal(t);
//		}
//		else return imager.queryLegendAt(i, j);	
//	}
//
//	/**
//	 * Ignored if the label image is not derived from an <code>ObjectArrayImager</code>
//	 * 
//	 * Truncates queries with coordinates outside the panel's dimensions.
//	 * 
//	 * @param i pixel coordinate within the panel.
//	 * @param j pixel coordinate within the panel.
//	 * @return a string representation of the data value of the object at the corresponding pixel.
//	 */
//	public String queryPixel(int i, int j)
//	{
//		if (fixedImg) return null;
//
//		/* determine which cell in the data array corresponds to the input pixel */
//		int relImgI = Math.max(0, Math.min(i - imgCornerX, imgDisplayWidth));;
//		int relImgJ = Math.max(0, Math.min(j - imgCornerY, imgDisplayHeight));;
//
//		double relX = ((double) relImgI) / ((double) imgDisplayWidth);
//		double relY = ((double) relImgJ) / ((double) imgDisplayHeight);
//
//		String out = queryRelative(relX, relY);
//		System.out.println("ObjectArrayImagePanel.queryPixel() Value of " +
//				watcher.getFieldName() + ": " + out);
//
//		return out;
//	}

//	/**
//	 * 
//	 * @param i x-coordinate (array index of data array) of the label
//	 * @param j y-coordinate (array index of data array) of the label
//	 * @param label text of the label.  If <code>null</code>, label will be 
//	 *              either a point, or the text will display the value of the object at [i][j]
//	 * @param font display font (if applicable)
//	 * @param color color for text or point 
//	 * @param keep  keep the decoration in the record of points so it will be redrawn later?
//	 */
//	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize, ObjectImagePanel<?> p)
//	{
//		addLabel(i, j, label, font, color, keep, pointSize); 
//	}

//	public void addValueLabel(int dataX, int dataY, Font font)
//	{
//		String label = imager.getImgData().queryData(dataX, dataY, imager.getWatcher());
//		addLabel(dataX, dataY, label, font, Color.black, true, -1);
//	}

/**
 * 
 */
//	public void addPoint(int i, int j, int size, Color color)
//	{ addLabel(i, j, null, null, color, true, size); }

//	public int[] objArrayCoordsToPanelCoords(int objArrayI, int objArrayJ)
//	{
//		int imgI, imgJ;
//		double imgRelativeI, imgRelativeJ;
//
//		imgRelativeI = ((double) objArrayI ) / ((double) imager.getDataWidth());
//		imgRelativeJ = ((double) objArrayJ ) / ((double) imager.getDataHeight());
//
//		imgI = (int) (((double) imgDisplayWidth) * imgRelativeI);
//		imgJ = (int) (((double) imgDisplayHeight) * imgRelativeJ);
//
//		return new int[] { imgI + imgCornerX, imgJ + imgCornerY };
//	}
/**
 * A label whose coordinates are calculated relative to the dimensions of the object array.
 * @author michaelfrancenelson
 *
 */
//	private class ObjectPointLabel
//	{
//		int objArrayI, objArrayJ;
//		int size;
//		Color color;
//
//		ObjectPointLabel(int i, int j, int size, Color color)
//		{ this.objArrayI = i; this.objArrayJ = j; this.size = size; this.color = color;	}
//
//		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY, int scaledSize)
//		{
//			g.setColor(color);
//			int[] coords = getImageCoords(
//					this.objArrayI, this.objArrayJ,
//					imageWidth, imageHeight, imageCornerX, imageCornerY, true);
//			if (this.size > 0)
//				g.fillOval(coords[0], coords[1], size, size);
//			else
//				g.fillOval(coords[0], coords[1], scaledSize, scaledSize);
//		}
//
//		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
//		{
//			int[] coords = getImageCoords(
//					this.objArrayI, this.objArrayJ, 
//					imageWidth, imageHeight, imageCornerX, imageCornerY, true);
//			g.fillOval(coords[0], coords[1], size, size);
//		}
//	}
/**
//	 * 
//	 * @param label
//	 * @param relI
//	 * @param relJ
//	 * @param font
//	 */
//	public void addTextLabel(String label, double relI, double relJ, Font font)
//	{
//		//		int[] coords = new int[] {
//		//				ArrayUtils.getRelativeIndex(relI, imager.getImgData().getWidth()),
//		//				ArrayUtils.getRelativeIndex(relJ, imager.getImgData().getHeight())
//		//		};
//
//		//				imager.getArrayCoords(relI, relJ);
//
//		addLabel(relI, relJ, label, font, Color.black, true, -1);
//	}

//

/**
 * 
 */
//	public void addValueLabel(double relI, double relJ, Font font)
//	{
//		String label = imager.getImgData().queryData(relI, relJ, imager.getWatcher());
//		addLabel(relI, relJ, label, font, Color.black, true, -1);
//
//		//		int[] coords = new int[] {
//		//				ArrayUtils.getRelativeIndex(relI, imager.getImgData().getWidth()),
//		//				ArrayUtils.getRelativeIndex(relJ, imager.getImgData().getHeight())
//		//		};
//		//		addValueLabel(coords[0], coords[1], font);
//		//		addLabel(dataX, dataY, label, font, Color.black, true, -1);
//		//		addValueLabel(coords[0], coords[1], font);
//	}

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


//private void labelFromImageCoords(
//		double datI, double datJ,
//		String label, Font font, Color color, 
//		int pointSize)
//{
//	
//}

//private double[] mouseClickToImageRelativeCoord(int clicX, int clicY)
//{
//	logger.debug(String.format("Image corners at: (%d, %d) and (%d, %d)", 
//			imgCornerX, imgCornerY, 
//			imgCornerX + imgDisplayWidth, 
//			imgCornerY + imgDisplayHeight));
//	logger.debug(String.format("Panel dims: (%d, %d), "
//			+ "image dims: (%d, %d)",
//			panelWidth, panelHeight, imgDisplayWidth, imgDisplayHeight
//			));
//
//	int imgClicX = Math.min(imgDisplayWidth, Math.max(0, clicX - imgCornerX));
//	int imgClicY = Math.min(imgDisplayHeight, Math.max(0, clicY - imgCornerY));
//
//	double clicRelX = ArrayUtils.absToRelCoord(clicX, panelWidth);
//	double clicRelY = ArrayUtils.absToRelCoord(clicY, panelHeight);
//
//	logger.debug(String.format("Mouse at panel coords (%d, %d) (%.2f, %.2f)",
//			clicX, clicY, clicRelX, clicRelY)); 
//
//	return new double[] {imgClicX, imgClicY};
//}

//public double[] dataAbsCoorsToRelative(int dataClicX, int dataClicY)
//{
//	double datRelX = ArrayUtils.absToRelCoord(dataClicX, imgDisplayWidth);
//	double datRelY = ArrayUtils.absToRelCoord(dataClicY, imgDisplayHeight);
//
//	logger.debug(String.format("Data coords (%d, %d) (%.2f, %.2f)",
//			dataClicX, dataClicY, datRelX, datRelY));
//
//	return new double[] {datRelX, datRelY};
//}

//public double[] imgAbsCoordsToRelative(int imgClicX, int imgClicY)
//{
//
//	double imgClicRelX = Math.min(1.0, Math.max(0.0, 
//			((double) imgClicX) / ((double) imgDisplayWidth)));
//	double imgClicRelY = Math.min(1.0, Math.max(0.0, 
//			((double) imgClicY) / ((double) imgDisplayHeight)));
//	logger.debug(String.format("Mouse at image coords (%d, %d) (%.2f, %.2f)",
//			imgClicX, imgClicY, imgClicRelX, imgClicRelY));
//
//	return new double[] {imgClicRelX, imgClicRelY};
//}
