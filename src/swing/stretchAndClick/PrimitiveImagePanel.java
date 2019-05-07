package swing.stretchAndClick;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import imaging.imagers.ObjectImager;
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
	@Override public ObjectImager<T> getImager() { return null; }
}
