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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import imaging.imagers.Imager;
import imaging.imagers.ObjectImager;
import imaging.imagers.decorators.PanelLabel;
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

	protected List<PanelLabel> labels = new ArrayList<>();
	protected List<PanelLabel> valueLabels = new ArrayList<>();
	protected List<PanelLabel> points = new ArrayList<>();

	protected String currentClickValue;

	protected Imager<T> imager;
	protected Class<T> clazz;
	private Class<? extends Annotation> annClass;

	LegendPanel<T> legend;
	
	protected Image image = null;

	protected double imageAspectRatio, compAspectRatio;
	protected boolean centerInPanel = true;
	protected boolean fixedAspectRatio, fixedWidth, fixedHeight, decorate;
	protected double ptRelSize;
	protected int 
	panelWidth, panelHeight, 
	imgDisplayWidth, imgDisplayHeight, 
	imgCornerX, imgCornerY;
	
	
	public void setLegend(LegendPanel<T> legend) { this.legend = legend; }
	public LegendPanel<T> getLegend() { return this.legend; }
	
	void init(
			ObjectImager<T> imgr, 
			int width, int height, 
			boolean keepAspectRatio, 
			Class<T> clazz, Class<? extends Annotation> annClass)
	{
		this.clazz = clazz; this.annClass = annClass;
		fixedWidth = false; fixedHeight = false;
		this.imageAspectRatio = -1;

		if ((width > 0) && (height > 0))
		{
			keepAspectRatio = true;
			this.imageAspectRatio = ((double) width) / ((double) height);
		}
		else
		{
			if (width > 0)  
			{ fixedAspectRatio = false; fixedWidth = true; imgDisplayWidth = width; } 

			if (height > 0)
			{ fixedAspectRatio = false; fixedHeight = true; imgDisplayHeight = height; }
		}

		this.imager = imgr;
		image = imager.getImage();

		this.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent click)
			{
				int mouseX = click.getX(); int mouseY = click.getY();

				double[] imgRelCoords = 
						new double[] {
								ArrayUtils.absToRelCoord(mouseX - imgCornerX, imgDisplayWidth),
								ArrayUtils.absToRelCoord(mouseY - imgCornerY, imgDisplayHeight) };
				currentClickValue = imager.queryData(imgRelCoords[0], imgRelCoords[1], null, null, null); 					
				logger.info(String.format("Value of %s: %s", 
						imager.getFieldName(), currentClickValue));
			}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseReleased(MouseEvent arg0) {}
		});

		if (this.image == null)
			throw new IllegalArgumentException("Not able to create image from field " 
					+ imgr.getFieldName() + ".");

		this.fixedAspectRatio = keepAspectRatio;

		if (this.imageAspectRatio < 0)
			this.imageAspectRatio = ((double) image.getWidth(null)) / ((double) image.getHeight(null));

		imgDisplayWidth = image.getWidth(null); imgDisplayHeight = image.getHeight(null);
	}

	/** 
	 *  If the image is derived form an <code>ObjectArrayImager</code>, refresh the image to
	 *  reflect any changes in the objects' state. 
	 */
	public void updateImage()
	{
		imager.refresh();
		image = imager.getImage();
		logger.debug("ObjectArrayImagePanel: updating array image to field " + imager.getFieldName());
		
		paintComponent(this.getGraphics());
	}

	
	public JComboBox<String> getMapLayerComboBox(Font font)
	{
		return PanelFactory.buildMapComboBox(clazz, this, font, this.imager.getFieldName());
	}

	
	public JComboBox<String> getControlComboBox(Font font)
	{
		List<Field> f2;
		f2 = FieldUtils.getFields(clazz, annClass, true, true, true, true);
		List<String> f3 = FieldUtils.getFieldNames(f2, clazz, annClass, true);
		List<String> dispNames = FieldUtils.getFieldNames(f2, clazz, annClass, true); 
		
		logger.trace(String.format("%s", "Building control combox.  With legend = " + (legend != null)));
		
		return PanelFactory.buildComboBox(this, f3, dispNames, font, this.imager.getFieldName());
	}

	public String queryRelative(double relativeI, double relativeJ, String intFmt, String dblFmt, String strFmt)
	{ return imager.queryData(relativeI, relativeJ, intFmt, dblFmt, strFmt);}

	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		logger.trace(String.format("Data width: %d, data height: %d", imager.getDataWidth(), imager.getDataHeight()));
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

		int cellWidth  = (int) ((double) imgDisplayWidth / (double) imager.getDataWidth());
		int cellHeight = (int) ((double) imgDisplayHeight / (double) imager.getDataHeight());

		if (fixedAspectRatio)
		{
			cellWidth = Math.min(cellWidth, cellHeight);
			cellHeight = cellWidth;
		}

		if (decorate)
		{
			for (PanelLabel p : labels)
				p.draw(
						g,
						imgDisplayWidth, imgDisplayHeight, 
						cellWidth, cellHeight, 
						imgCornerX, imgCornerY);

			for (PanelLabel p : valueLabels)
				p.draw(g,
						imgDisplayWidth, imgDisplayHeight, 
						cellWidth, cellHeight,
						imgCornerX, imgCornerY);
			for (PanelLabel p : points)
				p.draw(g, imgDisplayWidth, imgDisplayHeight, 
						cellWidth, cellHeight, imgCornerX, imgCornerY);
		}

		if (this.getBorder() != null) paintBorder(g);
		g2d.dispose();
		g.dispose();
	}

	/**
	 * Label all pixels with a String representation of the
	 *  current value of the underlying data field.
	 *  Mostly useful for debugging on small 2D data sets.
	 *  
	 * @param font
	 * @param color
	 */
	public void labelPixels(Font font, Color color)
	{
		double relI, relJ;
		int datWidth = imager.getDataWidth();
		int datHeight = imager.getDataHeight();
		for (int row = 0; row < datHeight; row++)
			for (int col = 0; col < datWidth; col++)
			{
				relI = ArrayUtils.absToRelCoord(col, datWidth);
				relJ = ArrayUtils.absToRelCoord(row, datHeight);
				addValueLabelRelative(relI, relJ, font, color, "%d", imager.getDblFmt(), "%s");
			}
	}

	protected void labelFromImageRelCoords(
			double relI, double relJ,
			String label, Font font, Color color, 
			double pointSize, String type)
	{
		if (color == null) color = Color.black;
		PanelLabel p = PanelLabel.fromRelImgCoords(
				relI, relJ, pointSize,
				font, color, label);
		switch(type)
		{
		case("label"): labels.add(p); break;
		case("value label"): valueLabels.add(p); break;
		case("point"): points.add(p); break;
		}
	}

	public void addLabelRelative(
			double relI, double relJ, String label, Font font, Color color)
	{
		if (font == null) font = this.getFont();
		labelFromImageRelCoords(relI, relJ, label, font, color, -9999, "label");
	}

	public void addLabel(
			int dataX, int dataY, String label, Font font, Color color)
	{
		if (font == null) font = this.getFont();
		double relI = ArrayUtils.absToRelCoord(dataX, imager.getDataWidth());
		double relJ = ArrayUtils.absToRelCoord(dataY, imager.getDataHeight());
		labelFromImageRelCoords(relI, relJ, label, font, color, -9999, "label");
	}

	public void addValueLabelRelative(
			double relI, double relJ, Font font, Color color)
	{
		addValueLabelRelative(relI, relJ, font, color, "%d", imager.getDblFmt(), "%s");
	}
	public void addValueLabelRelative(
			double relI, double relJ, Font font, Color color, String intFmt, String dblFmt, String strFmt)
	{
		if (font == null) font = this.getFont();
		String label = imager.queryData(relI, relJ, intFmt, dblFmt, strFmt);
		labelFromImageRelCoords(relI, relJ, label, font, color, -9999, "value label");

		logger.trace(String.format("Adding value label %s at coords (%.0f%%, %.0f%%)", label, 100 * relI, 100 * relJ));
	}

	public void addValueLabel(
			int dataX, int dataY, Font font, Color color)
	{
		double relI = ArrayUtils.absToRelCoord(dataX, imager.getDataWidth());
		double relJ = ArrayUtils.absToRelCoord(dataY, imager.getDataHeight());
		addValueLabelRelative(relI, relJ, font, color, "%d", "%f", "%s");
	}

	/**
	 * 
	 */
	public void addPointRelative(double relI, double relJ, double size, Color color)
	{ labelFromImageRelCoords(relI, relJ, null, null, color, size, "point"); }
	
	/**
	 * 
	 */
	public void addPoint(int dataX, int dataY, double size, Color color)
	{
		double relI = ArrayUtils.absToRelCoord(dataX + 0.5, imager.getDataWidth());
		double relJ = ArrayUtils.absToRelCoord(dataY + 0.5, imager.getDataHeight());
		labelFromImageRelCoords(relI, relJ, null, null, color, size, "point");
	}
	
	public void setField(String name)  { getImager().setField(name); updateImage(); }
	public void setField(Field f) { getImager().setField(f); updateImage(); }

	public Image getImg() { return this.image; }
	
	public RenderedImage getRenderedImage() { return (RenderedImage)this.image; }
	
	public void setLabelVisibility(boolean b) { this.decorate = b; repaint();}
	
	public double getPtRelSize() { return ptRelSize; }
	public void setPtRelSize(double ptRelSize) { this.ptRelSize = ptRelSize; }
	
	public Imager<T> getImager() { return imager; }
	
	public String getCurrentClickValue() { return currentClickValue; }
	
	public int getImgDisplayWidth() { return imgDisplayWidth;}
	public int getImgDisplayHeight() { return imgDisplayHeight; }
}