package beans.memberState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import beans.builder.GetterGetterGetter;
import beans.builder.GetterGetterGetter.BooleanGetter;
import beans.builder.GetterGetterGetter.DoubleGetter;
import beans.builder.GetterGetterGetter.IntGetter;
import beans.builder.GetterGetterGetter.ParsingBooleanGetter;
import beans.builder.GetterGetterGetter.StringValGetter;
import fields.FieldUtils;

public class SimpleFieldWatcher <T> implements FieldWatcher<T>
{
	/** 
	 *  A container for an int array with its min and max values
	 *  already calculated.
	 * @author michaelfrancenelson
	 *
	 */
	public static class IntArrayMinMax
	{
		public IntArrayMinMax(int[][] d, int mn, int mx)
		{this.data = d; this.min = mn; this.max = mx; }
		int[][] data; int min; int max;
		public int[][] getDat() { return this.data; }
		public int getMin() { return this.min; }
		public int getMax() { return this.max; }
	}

	/**
	 * A container for a double array with its min
	 * and max values precalculated.
	 * @author michaelfrancenelson
	 *
	 */
	public static class DblArrayMinMax
	{
		public DblArrayMinMax(double[][] d, double mn, double mx)
		{this.data = d; this.min = mn; this.max = mx; }
		double[][] data; double min; double max;
		public double[][] getDat() { return this.data; }
		public double getMin() { return this.min; }
		public double getMax() { return this.max; }
	}


	@Retention(RetentionPolicy.RUNTIME)
	public static @interface WatchField{ public String name(); }

	private String dblFmt;
	private String fieldName;
	private Field field;
	private Class<T> clazz;

	private IntGetter<T> intGetter;
	private DoubleGetter<T> dblGetter;
	private StringValGetter<T> stringGetter;
	private BooleanGetter<T> boolGetter;
	private ParsingBooleanGetter<T> parsingBoolGetter;

	private SimpleFieldWatcher() {}

	/**
	 * 
	 * @param clazz
	 * @param dblFmt
	 * @return
	 */
	public static <T> Map<String, FieldWatcher<T>> getWatcherMap(Class<T> clazz, String dblFmt)
	{
		Map<String, FieldWatcher<T>> out = new HashMap<>();

		Field[] fields = clazz.getDeclaredFields();

		for (Field f : fields)
		{
			f.setAccessible(true);
			out.put(f.getName(), factory(f.getName(), dblFmt, clazz));
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
			String fieldName, String dblFmt, Class<T> clazz) 
	{ 
		SimpleFieldWatcher<T> bw = new SimpleFieldWatcher<T>();
		bw.setClazz(clazz);
		bw.fieldName = fieldName; 
		bw.initField();
		if (dblFmt == null) dblFmt = "%.4f";
		bw.dblFmt = dblFmt;
		bw.buildGetters();
		return bw;
	}


	private void initField()
	{
		field = FieldUtils.getWatchedField(fieldName, null, clazz);
		field.setAccessible(true);
	}

	/**
	 * 
	 */
	private void buildGetters()
	{
		stringGetter = GetterGetterGetter.stringValGetterGetter(getClazz(), field, dblFmt);
		intGetter    = GetterGetterGetter.intGetterGetter(getClazz(), field);
		dblGetter    = GetterGetterGetter.doubleGetterGetter(getClazz(), field);
		boolGetter   = GetterGetterGetter.booleanGetterGetter(getClazz(), field);
		parsingBoolGetter   = GetterGetterGetter.parsingBooleanGetterGetter(getClazz(), field);
	}

	@Override public String  getStringVal(T t) { return stringGetter.get(t); }
	@Override public double  getDoubleVal(T t) { return dblGetter.get(t); }
	@Override public int     getIntVal(T t)    { return intGetter.get(t); }
	@Override public boolean getBoolVal(T t) { return boolGetter.get(t); }

	@Override public DblArrayMinMax  getDoubleVal(T[][] t) 
	{
		double[][] out = new double[t.length][t[0].length];
		double min = Double.MAX_VALUE;; double max = Double.MIN_VALUE;
		double val = 0.0;
		for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++)
		{		
			val = dblGetter.get(t[i][j]);
			if (val < min) min = val;
			else if (val > max) max = val;
			out[i][j] = val;
		}
		return new DblArrayMinMax(out, min, max);
	}

	@Override public IntArrayMinMax     getIntVal(T[][] t)    
	{
		int min = Integer.MAX_VALUE; int max = Integer.MIN_VALUE;
		int val = 0;
		int[][] out = new int[t.length][t[0].length];
		for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++)
		{
			val = intGetter.get(t[i][j]);
			if (val < min) min = val;
			else if (val > max) max = val;
			out[i][j] = val;
		}
		return new IntArrayMinMax(out, min, max);
	}

	@Override public boolean[][] getBoolVal(T[][] t) 
	{
		boolean[][] out = new boolean[t.length][t[0].length];
		for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++) 
			out[i][j] = boolGetter.get(t[i][j]); 
		return out;
	}

	@Override public boolean[][] getParsedBoolVal(T[][] t) 
	{ 
		boolean[][] out = new boolean[t.length][t[0].length];
		for (int i = 0; i < t.length; i++) for (int j = 0; j < t[0].length; j++) 
			out[i][j] = parsingBoolGetter.get(t[i][j]); 
		return out;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	@Override public boolean getParsedBoolVal(T t) { return parsingBoolGetter.get(t); }

	public static <T> Field getWatchedField(String fieldName, Class<T> clazz)
	{
		Field field = FieldUtils.getAnnotatedField(clazz, WatchField.class, fieldName);
		return field;
	}

	@Override public String getFieldName() { return field.getName(); }
	@Override public String getDisplayName() 
	{ 
		if (field.isAnnotationPresent(WatchField.class))
			return field.getAnnotation(WatchField.class).name();
		else return field.getName();
	}

	public Class<T> getClazz() { return clazz; }
	public void setClazz(Class<T> clazz) { this.clazz = clazz; }

	@Override public Field getField() { return field; }
	@Override public String getDblFmt() { return dblFmt; }
}
