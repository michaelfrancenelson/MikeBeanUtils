package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import beans.memberState.FieldWatcher;

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
	
	
	/** Retrieve an object from the underlying array.
	 * 
	 * @param x first array index
	 * @param y second array index
	 * @return element of the data array at indices [x][y]
	 */
	public T getObjAt(int i, int j);
	
	/** Retrieve an object from the underlying array.
	 * 
	 * @param relativeX Relative position of the first index, in the interval 0.0 - 1.0, of the array.
	 * @param relativeY Relative position of the first index, in the interval 0.0 - 1.0, of the array.
	 * @return the element in the array nearest to the relative coordinates.
	 */
	public T getObjAt(double relativeI, double relativeJ);
	
	/**
	 * 
	 * @return a watcher for the currently selected field.
	 */
	FieldWatcher<T> getWatcher();
	
	/**
	 *  Retrieve the underlying data array.
	 * 
	 * @return
	 */
	public T[][] getData();
	
}