package beans.memberState;

import java.lang.reflect.Field;


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
	 * @return an int representation of the field.  
	 *         If the field is not 'int', may throw an exception.
	 */
	public int     getIntVal(T t);

	/**
	 * 
	 * @param t bean instance
	 * @return a double representation of the field. 
	 *         If the field is not 'double' may throw an exception
	 */
	public double  getDoubleVal(T t);
	
	/**
	 * 
	 * @param t bean instance
	 * @return a boolean representation of the field.  
	 *         If the field is not 'boolean' may throw an exception
	 */
	public boolean getBoolVal(T t);
	
	/**
	 * 
	 * @param t bean instance
	 * @return a boolean representation of the field.
	 *           If the field is not 'boolean' an attempt is made to parse it as boolean.
	 */
	public boolean getParsedBoolVal(T t);

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
//
//	/**
//	 * 
//	 * @param f Set the watched field to this
//	 */
//	public void setWatchedField(Field f);
	
//	/**
//	 * 
//	 * @param fieldName Set the watched field to this
//	 */
//	public void setWatchedField(String fieldName);
	
	/**
	 * 
	 * @return The field currently being watched.
	 */
	public Field getField();

	String getDblFmt();
}
