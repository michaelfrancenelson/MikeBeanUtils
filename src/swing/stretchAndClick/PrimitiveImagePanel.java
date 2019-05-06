package swing.stretchAndClick;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import imaging.imagers.BeanImager;
import imaging.imagers.PrimitiveImager;
import utils.ArrayUtils;

public class PrimitiveImagePanel<T> extends ObjectImagePanel<T>
{

	/**	 */
	private static final long serialVersionUID = -7971761341095269963L;
	static Logger logger = LoggerFactory.getLogger(PrimitiveImagePanel.class);
	private PrimitiveImager imager;
	private String currentFieldName;
	
	void init(
			PrimitiveImager imgr, 
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

		if (imgr == null)
			throw new IllegalArgumentException("Either an Image or a BeanImager must be passed to init()");
		else
		{
			this.imager = imgr;
			image = imager.getImage();

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
					currentClickValue = imager.queryData(imgRelCoords[0], imgRelCoords[1]); 					
					logger.debug(String.format("Value of %s: %s", 
							currentFieldName, currentClickValue));
				}
				@Override public void mouseEntered(MouseEvent arg0) {}
				@Override public void mouseExited(MouseEvent arg0) {}
				@Override public void mousePressed(MouseEvent arg0) {}
				@Override public void mouseReleased(MouseEvent arg0) {}
			});
		}

		if (this.image == null)
			throw new IllegalArgumentException("Not able to create image"); 
		this.fixedAspectRatio = keepAspectRatio;
		this.imageAspectRatio = ((double) image.getWidth(null)) / ((double) image.getHeight(null));

		imgDisplayWidth = image.getWidth(null);
		imgDisplayHeight = image.getHeight(null);
	}

	@Override public void setField(String name) { this.currentFieldName = name; }
	@Override public void setField(Field f) { this.currentFieldName = f.getName();}
	@Override public BeanImager<T> getImager() { return null; }
	
//	@Override public void paintComponent(Graphics g)
//	{
//		int datWidth  = imager.getImgData().getWidth();
//		int datHeight = imager.getImgData().getHeight();
//		logger.trace(String.format("Data width: %d, data height: %d", datWidth, datHeight));
//		Insets insets = getInsets();
//		Graphics2D g2d = (Graphics2D) g.create();
//		int fixedX = this.imgDisplayWidth, fixedY = this.imgDisplayHeight;
//
//		this.panelWidth = getWidth() - insets.right;
//		this.panelHeight = getHeight() - insets.bottom;
//
//		this.imgDisplayWidth = panelWidth; 
//		this.imgDisplayHeight = panelHeight;
//
//		compAspectRatio = (double) panelWidth / (double) panelHeight;
//
//		/* If keeping the original aspect ratio. */
//		if (fixedAspectRatio)
//		{
//			if (imageAspectRatio < compAspectRatio)	
//			{
//				double w = (((double) panelHeight) * imageAspectRatio);
//				this.imgDisplayWidth = (int) w;
//			}
//			else this.imgDisplayHeight = (int) (((double) panelWidth) / imageAspectRatio);
//		}
//		else
//		{
//			if (this.fixedHeight) this.imgDisplayHeight = fixedY;
//			if (this.fixedWidth) this.imgDisplayWidth = fixedX;
//		}
//
//		if (centerInPanel)
//		{
//			int widthRemainder = panelWidth - imgDisplayWidth;
//			int heightRemainder = panelHeight - imgDisplayHeight;
//			imgCornerX = (int)(0.5 * ((double) widthRemainder));
//			imgCornerY = (int)(0.5 * ((double) heightRemainder));
//		}
//		else 
//		{
//			imgCornerX = 0;
//			imgCornerY = 0;
//		}
//
//		g2d.drawImage(image, imgCornerX, imgCornerY, imgDisplayWidth, imgDisplayHeight, null);
//
//		int cellWidth  = (int) ((double) imgDisplayWidth / (double) datWidth);
//		int cellHeight = (int) ((double) imgDisplayHeight / (double) datHeight);
//
//		if (fixedAspectRatio)
//		{
//			cellWidth = Math.min(cellWidth, cellHeight);
//			cellHeight = cellWidth;
//		}
//		
//		
//		if (decorate)
//		{
//			logger.trace("Adding labels and points.");
//			for (PanelLabel p : labels)
//				p.draw(
//						g,
//						imgDisplayWidth, imgDisplayHeight, 
//						cellWidth, cellHeight, 
//						imgCornerX, imgCornerY);
//
//			for (PanelLabel p : valueLabels)
//				p.draw(g,
//						imgDisplayWidth, imgDisplayHeight, 
//						cellWidth, cellHeight,
//						imgCornerX, imgCornerY);
//			for (PanelLabel p : points)
//				p.draw(g, imgDisplayWidth, imgDisplayHeight, 
//						cellWidth, cellHeight, imgCornerX, imgCornerY);
//		}
//
//
//		if (this.getBorder() != null) paintBorder(g2d);
//		g2d.dispose();
//		g.dispose();
//	}	



}
