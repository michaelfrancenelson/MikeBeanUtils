package imaging.imagers;

import java.awt.Color;
import java.awt.Image;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;

public interface BeanImager<T> 
{
	/**
	 * 
	 * @return the class of objects from which the images are generated
	 */
	public Class<T> getObjClass();
	public Class<? extends Annotation> getAnnClass();

	/** Return the current image, generated from the currently active field. */
	public Image getImage();
	public Image getLegendImage();

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

	/**
	 * 
	 * @return a watcher for the currently selected field.
	 */
	public FieldWatcher<T> getWatcher();

	String queryLegendAt(double relativeI, double relativeJ);
	String queryDataAt(double relativeI, double relativeJ);
	
	public void setDataSelection(double relativeI, double relativeJ);
	
	public ColorInterpolator getInterpolator();
	public ColorInterpolator getBooleanInterpolator();
	void setInterpolator(ColorInterpolator ci);
	void setBooleanInterpolator(ColorInterpolator ci);
}


///**
//* 
//* @return the array indices of the currently selected object
//*/
//public int[] getCurrentSelectedArrayCoords();
/**
 *  Retrieve the underlying data array.
 * 
 * @return
 */
//public T[][] getData();



//public void setCurrentSelectedArrayCoords(int i, int j);
//public void setCurrentSelection(double relativeI, double relativeJ);
//public int[] getArrayCoords(double relativeI, double relativeJ);

//public static int[] getObjArrayCoords(double relativeI, double relativeJ, int arrayDim1, int arrayDim2)
//{
//	int i, j; 
//	i = relativeIntCoord(relativeI, arrayDim1); 
//	j = relativeIntCoord(relativeJ, arrayDim2);
//	return new int[] {i, j};
//}

//public static int relativeIntCoord(double relative, int length)
//{
//	int i = (int)(((double) length) * relative);
//	i = Math.min(Math.max(0, i), length - 1);
//	return i;
//}


///** Retrieve an object from the underlying data.
// * 
// * @param x first array index
// * @param y second array index
// * @return element of the data array at indices [x][y]
// */
//public T getObjAt(int i, int j);

///** Retrieve an object from the underlying array.
// * 
// * @param relativeX Relative position of the first index, in the interval 0.0 - 1.0, of the array.
// * @param relativeY Relative position of the first index, in the interval 0.0 - 1.0, of the array.
// * @return the element in the array nearest to the relative coordinates.
// */
//public T getObjAt(double relativeI, double relativeJ);

///**
// *  Get a string representation of the value of the cell for the currently 
// *   selected field watcher.
// * @param i
// * @param j
// * @return
// */
//public String queryObjectAt(int i, int j);
