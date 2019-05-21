package beans.memberState;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.builder.GetterGetterGetter;
import beans.builder.GetterGetterGetter.BooleanGetter;
import beans.builder.GetterGetterGetter.ByteGetter;
import beans.builder.GetterGetterGetter.CharGetter;
import beans.builder.GetterGetterGetter.DoubleGetter;
import beans.builder.GetterGetterGetter.FormattedStringValGetter;
import beans.builder.GetterGetterGetter.IntGetter;
import beans.builder.GetterGetterGetter.ParsingBooleanGetter;
import beans.builder.GetterGetterGetter.StringValGetter;
import utils.FieldUtils;

public class SimpleFieldWatcher <T> implements FieldWatcher<T>
{
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DisplayName{ public String name(); }

	private String dblFmt;
	private String fieldName;
	private Field field;
	private Class<T> clazz;

	private ByteGetter<T> byteGetter;
	private IntGetter<T> intGetter;
	private DoubleGetter<T> dblGetter;
	private StringValGetter<T> stringGetter;
	private FormattedStringValGetter<T> formattedStringGetter;
	private BooleanGetter<T> boolGetter;
	private ParsingBooleanGetter<T> parsingBoolGetter;
	private CharGetter<T> charGetter;
	private SimpleFieldWatcher() {}

	/**
	 * 
	 * @param clazz
	 * @param dblFmt
	 * @return
	 */
	public static <T> Map<String, FieldWatcher<T>> getWatcherMap(
			Class<T> clazz,
			Class<? extends Annotation> annClazz, 
			String dblFmt, 
			boolean getInstance, boolean getStatic)
	{
		Map<String, FieldWatcher<T>> out = new HashMap<>();
		List<Field> fields = FieldUtils.getFields(
				clazz, annClazz, getInstance, getStatic, true, false);
		
		if (fields.size() == 0) throw new IllegalArgumentException("No fields to watch.");
		
		for (Field f : fields)
		{
			f.setAccessible(true);
			out.put(f.getName().toLowerCase(), factory(clazz, f.getName(), dblFmt));
		}

		return out;
	}

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
	 * @param dblFmt format for displaying doubles;
	 * @param clazz Bean type to watch
	 * @return
	 */
	public static <T> SimpleFieldWatcher<T> factory(Class<T> clazz, String fieldName, String dblFmt) 
	{ 
		SimpleFieldWatcher<T> bw = new SimpleFieldWatcher<T>();
		bw.setClazz(clazz);
		bw.fieldName = fieldName; 
		bw.initField();
		if (dblFmt == null) dblFmt = "%.2f";
		bw.dblFmt = dblFmt;
		bw.buildGetters();
		return bw;
	}

	private void initField()
	{
		field = FieldUtils.getField(clazz, null, fieldName, true);
		field.setAccessible(true);
	}

	/**
	 * 
	 */
	public void buildGetters()
	{
		stringGetter = GetterGetterGetter.toStringGetterGetter(getClazz(), field, dblFmt);
		formattedStringGetter = GetterGetterGetter.toFormattedStringGetterGetter(getClazz(), field);
		charGetter = GetterGetterGetter.charGetterGetter(getClazz(), field);
		intGetter    = GetterGetterGetter.intGetterGetter(getClazz(), field);
		byteGetter    = GetterGetterGetter.byteGetterGetter(getClazz(), field);
		dblGetter    = GetterGetterGetter.doubleGetterGetter(getClazz(), field);
		boolGetter   = GetterGetterGetter.booleanGetterGetter(getClazz(), field);
		parsingBoolGetter   = GetterGetterGetter.parsingBooleanGetterGetter(getClazz(), field);
	}


	public static <T> Field getDisplayField(
			String fieldName, Class<T> clazz, boolean matchCase)
	{
		Field field = FieldUtils.getField(clazz, DisplayName.class, fieldName, matchCase);
		return field;
	}

	@Override public String  getFieldName() { return field.getName(); }
	@Override public String  getStringVal(T t) { return stringGetter.get(t); }
	@Override public String  getFormattedStringVal(T t, String intFmt, String dblFmt, String strFmt) { 
		return formattedStringGetter.get(t, intFmt, dblFmt, strFmt); }
	@Override public double  getDoubleVal(T t) { return dblGetter.get(t); }
	@Override public char    getCharVal(T t) { return charGetter.get(t); }
	@Override public int     getIntVal(T t)    { return intGetter.get(t); }
	@Override public byte    getByteVal(T t) { return byteGetter.get(t); }
	@Override public boolean getBoolVal(T t) { return boolGetter.get(t); }
	@Override public boolean getParsedBoolVal(T t) { return parsingBoolGetter.get(t); }
	@Override public String  getDisplayName() 
	{ 
		if (field.isAnnotationPresent(DisplayName.class))
			return field.getAnnotation(DisplayName.class).name();
		else return field.getName();
	}

	public Class<T> getClazz() { return clazz; }
	public void setClazz(Class<T> clazz) { this.clazz = clazz; }

	@Override public Field getField() { return field; }
	@Override public String getDblFmt() { return dblFmt; }
	@Override public void setDblFmt(String fmt) { dblFmt = fmt; }
}