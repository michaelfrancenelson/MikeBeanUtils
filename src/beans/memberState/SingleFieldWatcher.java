package beans.memberState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;

import beans.builder.GetterGetterGetter;
import beans.builder.GetterGetterGetter.BooleanGetter;
import beans.builder.GetterGetterGetter.DoubleGetter;
import beans.builder.GetterGetterGetter.IntGetter;
import beans.builder.GetterGetterGetter.StringValGetter;
import fields.FieldUtils;

public class SingleFieldWatcher <T> implements FieldWatcher<T>
{
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface WatchField{ public String name(); }

	private String dblFmt;
	private String fieldName;
	private String displayName;
	private Field field;
	private Class<T> clazz;

	private IntGetter<T> intGetter;
	private DoubleGetter<T> dblGetter;
	private StringValGetter<T> stringGetter;
	private BooleanGetter<T> boolGetter;

	private SingleFieldWatcher() {}

	/**
	 *  Note: at least one of fieldName or displayName must be provided. <br>
	 * 	Case 1:  fieldName !=  null and displayName == null: <br>
	 * 	<li> The factory will search the bean type for a field
	 *       that exactly matches the input name.
	 *       The display name will be taken from the field's
	 *    @WatchField annotation attribute.
	 *   Case 2:  fieldName != null and displayName != null:
	 *   <li> like case 1, except that the display name will be taken from the factory method
	 *        parameter 'displayName'.  The field does not need to have the @WatchField annotation. 
	 *   Case 3:  fieldName == null and displayName != null;
	 *   <li> The factory will search for a field with the @WatchField name attribute that
	 *        exactly matches the factory method display name parameter.
	 *   Case 4:  both are  null:
	 *   <li> throws an exception - there is no way to determine the desired field;
	 *    
	 * @param fieldName name of the field in the bean, must match exactly.
	 * @param displayName name to print for the field values
	 * @param dblFmt format for displaying doubles;
	 * @param clazz Bean type to watch
	 * @return
	 */
	public static <T> SingleFieldWatcher<T> factory(
			String fieldName, String displayName, String dblFmt, Class<T> clazz) 
	{ 
		SingleFieldWatcher<T> bw = new SingleFieldWatcher<T>();
		Field field = FieldUtils.getWatchedField(fieldName, displayName, clazz);
		bw.field = field; 
		bw.field.setAccessible(true);

		if (displayName == null) 
		{
			if (bw.field.isAnnotationPresent(WatchField.class))
			displayName = bw.field.getAnnotation(WatchField.class).name();
			else displayName = bw.field.getName();
		}
		bw.fieldName = fieldName; bw.displayName = displayName; 
		bw.setClazz(clazz);
		if (dblFmt == null) dblFmt = "%.4f";
		bw.dblFmt = dblFmt;
		bw.buildGetters();
		return bw;
	}

	private void buildGetters()
	{
		intGetter    = GetterGetterGetter.intGetterGetter(getClazz(), field);
		dblGetter    = GetterGetterGetter.doubleGetterGetter(getClazz(), field);
		boolGetter   = GetterGetterGetter.booleanGetterGetter(getClazz(), field);
		stringGetter = GetterGetterGetter.stringValGetterGetter(getClazz(), field, dblFmt);
	}

	public String  getStringVal(T t) { return stringGetter.get(t); }
	public double  getDoubleVal(T t) { return dblGetter.get(t); }
	public int     getIntVal(T t)    { return intGetter.get(t); }
	public boolean getBoolVal(T t) { return boolGetter.get(t); }

	public static <T> Field getWatchedField(String fieldName, Class<T> clazz)
	{
		Field field = FieldUtils.getAnnotatedField(clazz, WatchField.class, fieldName);
		return field;
	}

	public String getFieldName() { return fieldName; }
	public String getDisplayName() { return displayName; }

	public Class<T> getClazz() { return clazz; }
	public void setClazz(Class<T> clazz) { this.clazz = clazz; }

	@Override
	public void setWatchedField(Field f) {
	}

	@Override
	public void setWatchedField(String fieldName) {
	}

	@Override
	public Field getField() { return field; }

}
