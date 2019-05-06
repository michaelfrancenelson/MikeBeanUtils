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
import beans.builder.GetterGetterGetter.IntGetter;
import beans.builder.GetterGetterGetter.ParsingBooleanGetter;
import beans.builder.GetterGetterGetter.StringValGetter;
import utils.FieldUtils;

public class SimpleFieldWatcher <T> implements FieldWatcher<T>
//public class SimpleFieldWatcher <T, A extends Annotation> implements FieldWatcher<T>
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
			//			public static <T, A extends Annotation> Map<String, FieldWatcher<T>> getWatcherMap(
			Class<T> clazz,
			Class<? extends Annotation> annClazz, 
			String dblFmt, 
			boolean getInstance, boolean getStatic)
	{
		Map<String, FieldWatcher<T>> out = new HashMap<>();
		List<Field> fields = FieldUtils.getFields(
				clazz, annClazz, getInstance, getStatic);

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
	public static <T> SimpleFieldWatcher<T> factory(
			//					public static <T, A extends Annotation> SimpleFieldWatcher<T, A> factory(
			Class<T> clazz,
			//			Class<? extends Annotation> annClass,
			String fieldName, String dblFmt
			) 
	{ 
		SimpleFieldWatcher<T> bw = new SimpleFieldWatcher<T>();
		//		SimpleFieldWatcher<T, A> bw = new SimpleFieldWatcher<T, A>();
		bw.setClazz(clazz);
		//		bw.annClass = annClass;
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
//public static <T, A extends Annotation> Map<String, FieldWatcher<T>> getWatcherMap(
//Class<T> clazz, Class<A> annClazz, String dblFmt)
//{
//Map<String, FieldWatcher<T>> out = new HashMap<>();
////Field[] fields = clazz.getDeclaredFields();
//List<Field> fields = FieldUtils.getAnnotatedFields(clazz, annClazz);
//
//for (Field f : fields)
//{
//System.out.println("SimpleFieldWatcher.getWatcherMap() field: " + f.getName());
//f.setAccessible(true);
//out.put(f.getName(), factory(f.getName(), dblFmt, clazz));
//}
//
//return out;
//}
//
//@Override public DblArrayMinMax  getDoubleVal(T[][] t) 
//{
//	double[][] out = new double[t.length][t[0].length];
//	double min = Double.MAX_VALUE;; double max = Double.MIN_VALUE;
//	double val = 0.0;
//	for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++)
//	{		
//		val = dblGetter.get(t[i][j]);
//		if (val < min) min = val;
//		else if (val > max) max = val;
//		out[i][j] = val;
//	}
//	return new DblArrayMinMax(out, min, max);
//}
//
//@Override public DblArrayMinMax  getDoubleVal(List<List<T>> t) 
//{
//	int width = t.size(); int height = t.get(0).size();
//	double[][] out = new double[width][height];
//	double min = Double.MAX_VALUE;; double max = Double.MIN_VALUE;
//	double val = 0.0;
//	for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
//	{		
//		val = dblGetter.get(t.get(i).get(j));
//		if (val < min) min = val;
//		else if (val > max) max = val;
//		out[i][j] = val;
//	}
//	return new DblArrayMinMax(out, min, max);
//}
//
//@Override public IntArrayMinMax     getIntVal(List<List<T>> t)
//{
//	int min = Integer.MAX_VALUE; int max = Integer.MIN_VALUE;
//	int width = t.size(); int height = t.get(0).size();
//	int val = 0;
//	int[][] out = new int[width][height];
//		for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
//	{
//		val = intGetter.get(t.get(i).get(j));
//		if (val < min) min = val;
//		else if (val > max) max = val;
//		out[i][j] = val;
//	}
//	return new IntArrayMinMax(out, min, max);
//}
//
//@Override public IntArrayMinMax     getIntVal(T[][] t)    
//{
//	int min = Integer.MAX_VALUE; int max = Integer.MIN_VALUE;
//	int val = 0;
//	int[][] out = new int[t.length][t[0].length];
//	for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++)
//	{
//		val = intGetter.get(t[i][j]);
//		if (val < min) min = val;
//		else if (val > max) max = val;
//		out[i][j] = val;
//	}
//	return new IntArrayMinMax(out, min, max);
//}
//
//@Override public ByteArrayMinMax  getByteVal(T[][] t)    
//{
//	byte min = Byte.MAX_VALUE; byte max = Byte.MIN_VALUE;
//	byte val = 0;
//	byte[][] out = new byte[t.length][t[0].length];
//	for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++)
//	{
//		val = byteGetter.get(t[i][j]);
//		if (val < min) min = val;
//		else if (val > max) max = val;
//		out[i][j] = val;
//	}
//	return new ByteArrayMinMax(out, min, max);
//}
//
//@Override public ByteArrayMinMax  getByteVal(List<List<T>> t)    

////	{
//	byte min = Byte.MAX_VALUE; byte max = Byte.MIN_VALUE;
//	int width = t.size(); int height = t.get(0).size();
//	byte val = 0;
//	byte[][] out = new byte[width][height];
//	for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
//	{
//		val = byteGetter.get(t.get(i).get(j));
//		if (val < min) min = val;
//		else if (val > max) max = val;
//		out[i][j] = val;
//	}
//	return new ByteArrayMinMax(out, min, max);
//}

//@Override public boolean[][] getBoolVal(List<List<T>> t) 
//{
//	int width = t.size(); int height = t.get(0).size();
//	boolean[][] out = new boolean[width][height];
//	for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
//		out[i][j] = boolGetter.get(t.get(i).get(j)); 
//	return out;
//}
//
//@Override public boolean[][] getParsedBoolVal(List<List<T>> t) 
//{
//	int width = t.size(); int height = t.get(0).size();
//	boolean[][] out = new boolean[width][height];
//	for (int i = 0; i < width; i++) for (int j = 0; j < height; j++)
//		out[i][j] = parsingBoolGetter.get(t.get(i).get(j)); 
//	return out;
//}
//
//@Override public boolean[][] getBoolVal(T[][] t) 
//{
//	boolean[][] out = new boolean[t.length][t[0].length];
//	for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++) 
//		out[i][j] = boolGetter.get(t[i][j]); 
//	return out;
//}
//
//@Override public boolean[][] getParsedBoolVal(T[][] t) 
//{ 
//	boolean[][] out = new boolean[t.length][t[0].length];
//	for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++) 
//		out[i][j] = parsingBoolGetter.get(t[i][j]); 
//	return out;
//}