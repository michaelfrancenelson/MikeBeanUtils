package fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import beans.memberState.SingleFieldWatcher.WatchField;

public class FieldUtils 
{

	/**
	 * 
	 * @param clazz    bean class
	 * @param annClazz annotation class
	 * @param          <T> bean type
	 * @return
	 */
	public static <T, A extends Annotation> List<Field> getAnnotatedFields(Class<T> clazz, Class<A> annClazz) {
		List<Field> ll = new ArrayList<>();
		for (Field f : clazz.getDeclaredFields()) {
			f.setAccessible(true);
			if (f.isAnnotationPresent(annClazz)) ll.add(f);
		}
		return ll;
	}

	public static <T, A extends Annotation> Field getAnnotatedField(Class<T> clazz, Class<A> annClazz, String fieldName)
	{
		List<Field> ll = getAnnotatedFields(clazz, annClazz);
		
		Field out = null;
		for (Field f : ll)
		{
			f.setAccessible(true);
			if (f.getName().equals(fieldName))
			{
				/* This shouldn't be possible, but including it just in case. */
				if (out != null) throw new
				IllegalArgumentException("more than one field with name " + fieldName + " found.");
				else out = f;
			}
		}
		return out;
	}
	
	public static <T> Field getWatchedField(String fieldName, String displayName, Class<T> clazz)
	{
		Field field = null;
		if (fieldName == null)
		{
			if (displayName == null)
				throw new IllegalArgumentException("fieldName and displayName cannot both be null.");
			List<Field> fields = FieldUtils.getAnnotatedFields(clazz, WatchField.class);
			for (Field f : fields)
			{
				f.setAccessible(true);
				if (f.getAnnotation(WatchField.class).name().equals(displayName))
					{
					field = f;
					}
			}
			if (field == null) 
				throw new IllegalArgumentException("Field not found.");
		}
		else 
		{
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}	
		
		return field;
	}
	
	
}
