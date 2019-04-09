package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import beans.memberState.FieldWatcher;

public interface ObjectArrayImager<T> 
{

	/**
	 * 
	 * @return the class of objects from which the images are generated
	 */
	public Class<T> getObjClass();
	
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
	
	
	public String queryObjectAt(int i, int j);
	
	
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
	
	/**
	 * 
	 * @return the array indices of the currently selected object
	 */
	public int[] getCurrentSelectedCoords();
	
	/**
	 * 
	 * @return the currently selected object
	 */
	public T getCurrentSelectedObj();
	
	
	public void setCurrentSelection(int i, int j);
	public void setCurrentSelection(double relativeI, double relativeJ);
	public int[] getArrayCoords(double relativeI, double relativeJ);
	
	public static int[] getObjArrayCoords(double relativeI, double relativeJ, int arrayDim1, int arrayDim2)
	{
		int i, j; 
		i = relativeIntCoord(relativeI, arrayDim1); 
		j = relativeIntCoord(relativeJ, arrayDim2);
		return new int[] {i, j};
		//		i = (int) (((double) (objArray.length)) * relativeI);
		//		j = (int) (((double) (objArray[0].length)) * relativeJ);
		//
		//		i = Math.min(i, objArray.length - 1);
		//		j = Math.min(j, objArray[0].length - 1);
		//
		//		//		System.out.println("SimpleArrayImager.getObjAt():  relative coords are " + relativeI + ", " + relativeJ + ").");
		//		//		System.out.println("SimpleArrayImager.getObjAt():  array coords are    " + i + ", " + j + ").");
		//
		//		i = Math.min(objArray.length - 1, Math.max(0, i));
		//		j = Math.min(objArray[0].length - 1, Math.max(0, j));
	}
	
	public static int relativeIntCoord(double relative, int length)
	{
		int i = (int)(((double) length) * relative);
		i = Math.min(Math.max(0, i), length - 1);
		return i;
	}
	
}