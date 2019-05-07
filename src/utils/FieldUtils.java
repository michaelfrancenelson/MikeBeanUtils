package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import beans.memberState.SimpleFieldWatcher.DisplayName;

public class FieldUtils 
{

	private static <A extends Annotation> boolean hasAnnotation(Field f, Class<A> ann)
	{
		if (ann == null) return false;
		else return f.isAnnotationPresent(ann);
	}

	private static <A extends Annotation> String annString(Class<A> ann)
	{
		if (ann != null) return " with annotation " + ann.getSimpleName() + " ";
		return "";
	}

	private static <T> String typString(Class<T> c)
	{
		return "in type " + c.getSimpleName();
	}

	private static <T> String modString(Class<T> clazz, boolean getInstance, boolean getStatic)
	{
		if (getInstance && getStatic) return " instance or static ";
		if (getInstance) return " instance ";
		else return " static ";
	}

	/**
	 *  Get all the fields with the matching annotation from a type.
	 * @param clazz Type of object to search for fields.
	 * @param annClazz Annotation class.  If null, all fields are returned.
	 * @param getInstance Should instance fields be retrieved?
	 * @param getStatic Should static fields be retrieved?  
	 *        If getInstance and getStatic are both false, an exception is thrown.
	 * @return List of matching fields.  If none were found, throws an exception.
	 */
	public static <T, A extends Annotation> List<Field> getFields(
			Class<T> clazz, Class<A> annClazz, 
			boolean getInstance, boolean getStatic) throws IllegalArgumentException
	{
		if (!getInstance && !getStatic)
			throw new IllegalArgumentException("At least one of the "
					+ "'getInstance' or 'getStatic' parameters must be true.");
		Field[] fields = clazz.getDeclaredFields();
		List<Field> ff = new ArrayList<>();
		boolean okStatic = false;
		boolean isStatic = false;
		boolean okInstance = false;
		boolean okAnnotation = false;

		for (Field f: fields)
		{
			f.setAccessible(true);

			isStatic = Modifier.isStatic(f.getModifiers());
			okAnnotation = hasAnnotation(f, annClazz) || annClazz == null;
			okStatic = okAnnotation && (getStatic && isStatic);
			okInstance = okAnnotation && (getInstance && (!isStatic));

			if (okStatic || okInstance)	ff.add(f);
		}

		/* Throw exception in case no suitable fields were found. */
		if (ff.size() == 0)
		{
			throw new IllegalArgumentException("Could not find any" + 
					modString(clazz, getInstance, getStatic) + "fields" +
					annString(annClazz) + typString(clazz));
		}
		return ff;
	}

	/**
	 *  Get all the names of fields with the matching annotation from a type.
	 *  
	 * @param clazz Type of object to search for fields.
	 * @param annClass Annotation class.  If null, all field names are returned.
	 * @param getInstance Should instance fields be retrieved?
	 * @param getStatic Should static fields be retrieved?  
	 *        If getInstance and getStatic are both false, an exception is thrown.
	 * @param toLowerCase should field names be converted to lower case?
	 *        useful for trying to match case insensitive text later.
	 * @return List of names for the matching fields.
	 */
	public static <T> List<String> getFieldNames(
			Class<T> clazz,
			Class<? extends Annotation> annClass,
			boolean getInstance, 
			boolean getStatic,
			boolean toLowerCase) throws IllegalArgumentException
	{
		//		System.out.println("FieldUtils: getting instance field names from class " + clazz.getName());
		List<Field> ff = getFields(clazz, annClass, getInstance, getStatic);
		return getFieldNames(ff, clazz, annClass, toLowerCase);
	}

	/**
	 *  Get the names of the fields contained in an iterable collection.
	 *  
	 * @param fields list of fields whose names to return
	 * @param clazz type to check
	 * @param annClass Annotation class.  If null, all field names are returned.
	 * @param toLowerCase should field names be converted to lower case?
	 *        useful for trying to match case insensitive text later.
	 * @return a list of field names. Throws an exception of no names could be matched
	 */
	public static <T> List<String> getFieldNames(
			Iterable<Field> fields, 
			Class<T> clazz,
			Class<? extends Annotation> annClass,
			boolean toLowerCase) throws IllegalArgumentException
	{
		//		System.out.println("FieldUtils: getting instance field names" );
		List<String> fNames = new ArrayList<>();

		for (Field f: fields)
		{
			f.setAccessible(true);
			if (hasAnnotation(f, annClass) || annClass == null)
				fNames.add(f.getName());
		}

		if (toLowerCase) for (int i = 0; i < fNames.size(); i++)
			fNames.set(i, fNames.get(i).toLowerCase()); 

		if (fNames.size() == 0)
		{
			throw new IllegalArgumentException("Could not retrieve the names of fields " +
					annString(annClass) + typString(clazz) + ".");
		}
		return fNames;
	}

	/**
	 * Retrieve a specific field from a class, if it exists and has the 
	 * appropriate annotation.
	 * 
	 * @param clazz type to search for field
	 * @param annClass only fields with this annotation will be checked.
	 *        If null all fields are considered.
	 * @param fieldName name of the member within the type
	 * @param matchCase perform a case-insensitive search?
	 * @return the appropriate Field, if found.  Otherwise throws an exception.
	 */
	public static <T, A extends Annotation> Field getField(
			Class<T> clazz, Class<A> annClass,
			String fieldName, boolean matchCase) 
					throws IllegalArgumentException
	{
		List<Field> ll = getFields(clazz, annClass, true, true);
		if (matchCase) fieldName = fieldName.toLowerCase();
		String nameTemp;
		Field out = null;
		boolean hasAnnotation = false;
		for (Field f : ll)
		{
			f.setAccessible(true);

			/* Proceed if either the annotation class parameter is null, 
			 * or if the field has the correct annotation. */
			if (annClass == null) hasAnnotation = false;
			else hasAnnotation = f.isAnnotationPresent(annClass);

			if (annClass == null || hasAnnotation)
			{
				nameTemp = f.getName();
				if (matchCase) nameTemp = nameTemp.toLowerCase(); 
				if (nameTemp.equals(fieldName))	out = f;
			}
		}

		if (out == null)
		{
			throw new IllegalArgumentException("Could not retrieve the field " +
					fieldName + annString(annClass) + typString(clazz));

		}
		return out;
	}

	/**
	 * Search for a specific field having the DisplayName annotation
	 * @param fieldName 
	 * @param fieldName name of the member within the type
	 * @param displayName name attribute of the DisplayName annotation.  If null, all fields are searched by 'fieldName'
	 * @param clazz type to search
	 * @param ignoreCase perform a case-insensitive search?
	 * @return a field matching the search criteria, if it exists.
	 */
	public static <T> Field getDisplayField(
			String fieldName, String displayName, 
			Class<T> clazz, boolean ignoreCase)
					throws IllegalArgumentException
	{
		if (displayName == null && fieldName == null)
			throw new IllegalArgumentException("fieldName and displayName"
					+ " cannot both be null.");

		Field out = null;
		String annDispName = "";
		String matchDisplayName = "";
		String matchFieldName = "";

		List<Field> fields = getFields(clazz, DisplayName.class, true, true);

		/* Get field names converted to lower case. */
		List<String> fieldNames = getFieldNames(
				fields, clazz, DisplayName.class, ignoreCase);
		if (ignoreCase) matchFieldName = fieldName.toLowerCase();
		/* First search for the name of the member within the class. */
		if (fieldName != null)
		{
			int index = fieldNames.indexOf(matchFieldName);
			out = fields.get(index);
			
		}
		/* Otherwise try to match the display name in the WatchField annotation. */
		else if (displayName != null)
			for (Field f : fields)
			{
				f.setAccessible(true);
				matchDisplayName = displayName;
				annDispName = f.getAnnotation(DisplayName.class).name().toLowerCase();
				if (ignoreCase) 
				{
					annDispName = annDispName.toLowerCase();
					matchDisplayName = displayName.toLowerCase();
				}

				if (annDispName.equals(matchDisplayName))
				{
					if (out != null)
						throw new IllegalArgumentException("At least two fields exist"
								+ typString(clazz) + " with the same display name: " +
								displayName);
					out = f;
				}
			}
		if (out == null && fieldName == null)
			throw new IllegalArgumentException("Could not retrieve the field " +
					annString(DisplayName.class) + " attribute " + displayName + 
					typString(clazz));
		else if (out == null)
			throw new IllegalArgumentException("Could not retrieve the field " + 
					fieldName +	typString(clazz));
		return out;
	}
	
	
	public static String toBoolean(String val)
	{
		try
		{
			Double val2 = Double.parseDouble(val); 
			if (val2 < 0) return "NA";
			else if (val2 >= 1) return "true";
			return "false";
		} catch(Exception e) { return stringToBoolean(val); }
	}
	
	public static String stringToBoolean(String val)
	{
		String test = val.toLowerCase().trim();
		
		switch(test)
		{
		case("f"): case("false"): case("-1"): return "false";
		case("t"): case("true"): case("1"): return "true";
		}
		return "NA";
	}
	
}

