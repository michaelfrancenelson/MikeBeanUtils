package beans.memberState;

import java.lang.reflect.Field;
import java.util.List;

import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;


/** Monitor the state of beans.
 * 
 * @author michaelfrancenelson
 *
 * @param <T> Type of bean to watch.
 */
public interface FieldWatcher<T>
{
	
	/**
	 * 
	 * @param t
	 * @return a String representation of the field.
	 *         If the field is numeric or boolean, the value is parsed to a String.
	 */
	public String  getStringVal(T t);

	/**
	 * 
	 * @param t bean instance
	 * @return a byte representation of the field.  
	 *         If the field is not 'byte', may throw an exception.
	 */
	public byte            getByteVal(T t);
	/**
	 * 
	 * @param t array of bean instances
	 * @return a byte array representation of the field.  
	 *         If the field is not 'byte', may throw an exception.
	 */
	public ByteArrayMinMax getByteVal(T[][] t);
	/**
	 * 
	 * @param t collection of bean instances
	 * @return a byte array representation of the field.  
	 *         If the field is not 'byte', may throw an exception.
	 */
	public ByteArrayMinMax getByteVal(List<List<T>> t);
	

	/**
	 * 
	 * @param t bean instance
	 * @return an int representation of the field.  
	 *         If the field is not 'int', may throw an exception.
	 */
	public int     getIntVal(T t);
	
	
	/**
	 * 
	 * @param t array of bean instances
	 * @return an int array of the field.  
	 *         If the field is not of type int, may throw an exception.
	 */
	public IntArrayMinMax getIntVal(T[][] t);
	/**
	 * 
	 * @param t collection of bean instances
	 * @return an int array of the field.  
	 *         If the field is not of type int, may throw an exception.
	 */
	public IntArrayMinMax getIntVal(List<List<T>> t);
	
	
	/**
	 * 
	 * @param t bean instance
	 * @return a double representation of the field. 
	 *         If the field is not 'double' may throw an exception
	 */
	public double  getDoubleVal(T t);
	/**
	 * 
	 * @param t 2D array of bean instances
	 * @return
	 */
	public DblArrayMinMax getDoubleVal(T[][] t);
	/**
	 * 
	 * @param t collection of bean instances
	 * @return
	 */
	public DblArrayMinMax getDoubleVal(List<List<T>> t);
	
	/**
	 * 
	 * @param t bean instance
	 * @return a boolean representation of the field.  
	 *         If the field is not 'boolean' may throw an exception
	 */
	public boolean getBoolVal(T t);
	/**
	 * 
	 * @param t 2D array of beans
	 * @return
	 */
	public boolean[][] getBoolVal(T[][] t);
	/**
	 * 
	 * @param t a collection of beans
	 * @return
	 */
	public boolean[][] getBoolVal(List<List<T>> t);
	
	/**
	 * 
	 * @param t bean instance
	 * @return a boolean representation of the field.
	 *           If the field is not 'boolean' an attempt is made to parse it as boolean.
	 */
	public boolean getParsedBoolVal(T t);
	/**
	 * 
	 * @param t 2D array of beans
	 * @return 2D array of booleans.
	 *           If the field is not 'boolean' an attempt is made to parse it as boolean.
	 */
	public boolean[][] getParsedBoolVal(T[][] t);
	/**
	 * 
	 * @param t a collection of beans
	 * @return
	 */
	public boolean[][] getParsedBoolVal(List<List<T>> t);

	/**
	 * 
	 * @return get the name of the field
	 */
	public String getFieldName();
	
	/**
	 * 
	 * @return get the display name (for menus, etc.) of the watched field.
	 */
	public String getDisplayName();
	
	/**
	 * 
	 * @return The field currently being watched.
	 */
	public Field getField();

	/**
	 * 
	 * @return
	 */
	String getDblFmt();

	char getCharVal(T t);
}
