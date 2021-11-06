package com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.Imager;
import com.github.michaelfrancenelson.mikebeansutils.utils.ArrayUtils;

public class PrimitiveImagePanel<T> extends ObjectImagePanel<T>
{
	/**	 */
	private static final long serialVersionUID = -7971761341095269963L;
	static Logger logger = LoggerFactory.getLogger(PrimitiveImagePanel.class);
	protected String currentFieldName;
//	Imager<T> imager;
	
	@SuppressWarnings("unchecked")
	void init(
			Imager<?> imgr, 
			int width, int height, 
			boolean keepAspectRatio)
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
			this.imager = (Imager<T>) imgr;
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
					currentClickValue = imager.queryData(imgRelCoords[0], imgRelCoords[1], null, null, null); 					
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
		repaint();
	}

	@Override public void setField(String name) { this.currentFieldName = name;}
	@Override public void setField(Field f) { this.currentFieldName = f.getName();}
}
