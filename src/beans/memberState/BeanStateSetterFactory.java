package beans.memberState;

import java.lang.reflect.Field;


public class BeanStateSetterFactory
{

	public interface BeanPrimitiveFieldSetter<T> 
	{
		public void set(T t, int val);
		public void set(T t, double val);
		public void set(T t, boolean val);
		public void set(T t, String val);
		public String getType();
	}

	private static class BeanSetter<T> implements BeanPrimitiveFieldSetter<T>
	{
		Field f;
		String type;

		@Override
		public void set(T t, int val) 
		{ throw new IllegalArgumentException("Bean setter cannot accept integer input"); }

		@Override
		public void set(T t, String val) 
		{ throw new IllegalArgumentException("Bean setter cannot accept string input"); }

		@Override
		public void set(T t, double val) 
		{ throw new IllegalArgumentException("Bean setter cannot accept double input"); }

		@Override
		public void set(T t, boolean val) 
		{ throw new IllegalArgumentException("Bean setter cannot accept boolean input"); }

		@Override public String getType() { return type; }
	}

	private static class BeanIntSetter<T> extends BeanSetter<T> 
	{
		public BeanIntSetter(Field f) 
		{
			super.type = "int";
			this.f = f;
		}
		@Override
		public void set(T t, int val) {	try {
			f.setInt(t, val);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} }
	}

	private static class BeanDoubleSetter<T> extends BeanSetter<T> 
	{
		public BeanDoubleSetter(Field f)
		{
			super.type = "double";
			this.f = f; 
		}
		@Override
		public void set(T t, double val) { try {
			f.setDouble(t, val);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} }
	}

	private static class BeanBooleanSetter<T> extends BeanSetter<T>
	{
		public BeanBooleanSetter(Field f) 
		{	
			super.type = "boolean";
			this.f = f;
		}
		@Override
		public void set(T t, boolean val) { try {
			f.setBoolean(t, val);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} }
	}

	private static class BeanStringSetter<T> extends BeanSetter<T> 
	{
		public BeanStringSetter(Field f)
		{
			super.type = "String";
			this.f = f; 
		}
		
		@Override
		public void set(T t, String val) { try {
			f.set(t, val);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} }
	}

	public static <T> BeanPrimitiveFieldSetter<T> factory(
			Class<T> clazz, String fieldName
			)
	{
		Field f = null;

		BeanPrimitiveFieldSetter<T> bss = null;
		try {
			f = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		f.setAccessible(true);

		String type = f.getType().getSimpleName();

		switch(type)
		{
		case("int"):     bss = new BeanIntSetter<T>(f); break;
		case("double"):  bss = new BeanDoubleSetter<T>(f); break;
		case("boolean"): bss = new BeanBooleanSetter<T>(f); break;
		case("String"):  bss = new BeanStringSetter<T>(f); break;
		}

		return bss;
	}
}
