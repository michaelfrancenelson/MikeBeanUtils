package fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import beans.memberState.SimpleFieldWatcher.WatchField;

public class FieldUtils 
{


	public static <T> Field[] getInstanceFields(Class<T> clazz)
	{
		Field[] fields = clazz.getDeclaredFields();
		List<Field> ff = new ArrayList<>();

		for (Field f: fields)
		{
			f.setAccessible(true);
			if (!Modifier.isStatic(f.getModifiers()))
			{
				ff.add(f);
//				System.out.println("FieldUtils: field name is " + f.getName());
			}
		}
		Field[] fff = new Field[ff.size()];
		for (int i = 0; i < fff.length; i++) { fff[i] = ff.get(i); }
		return fff;
	}
	
	public static <T> String[] getInstanceFieldNames(Class<T> clazz) 
	{
		System.out.println("FieldUtils: getting instance field names from class " + clazz.getName());
		return getInstanceFieldNames(Arrays.asList(getInstanceFields(clazz)));
	}
	
	public static <T> String[] getInstanceFieldNames(Iterable<Field> fields)
	{
		
		System.out.println("FieldUtils: getting instance field names" );
		List<String> fNames = new ArrayList<>();
		
		for (Field f: fields)
		{
			f.setAccessible(true);
			if (!Modifier.isStatic(f.getModifiers()))
				{
				fNames.add(f.getName());
				System.out.println("FieldUtils: field name is " + f.getName());
				}
		}
		
		String[] out = new String[fNames.size()];
		for (int i = 0; i < out.length; i++) { out[i] = fNames.get(i); }
		return out;
	}
	
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
