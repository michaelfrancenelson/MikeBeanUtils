package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public interface ObjectArrayImager<T> 
{

	/** Return the current image, generated from the currently active field. */
	public BufferedImage getImage();
	public String getCurrentFieldName();
	public Field getCurrentField();

	/** Set or update the color scale used in the image 
	 * 
	 * @param colors
	 */
	public void setColors(Color[] colors);

	/** Set the field to be imaged:
	 *  Read data from field and build image.
	 * @param fieldName
	 */
	public void setField(String fieldName);

	/** Set the field to be imaged:
	 *  Read data from field and build image.
	 * @param field
	 */
	public void setField(Field field);

	/**
	 *  Refresh data from the current field and rebuild the image;
	 */
	public void refresh();
}