package utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import beans.builder.AnnotatedBeanReader;
import beans.builder.RandomBeanBuilder;
import beans.sampleBeans.AllFlavorBean;

public class MethodUtils
{

	public static void main(String[] args) throws Throwable 
	{
		Class<Demo> clazz = Demo.class;
		String getterName = "getI1";
		Demo d1 = new Demo(1.1, 2.2, 3.3, 111, 222, 333);

		getterDemo(getterName, "getD2", clazz, d1);

		AllFlavorBean b = RandomBeanBuilder.randomFactory(AllFlavorBean.class, 1, 10, RandomBeanBuilder.r);

		Getter<AllFlavorBean> byteGetter = buildFieldGetter(AllFlavorBean.class, "bytePrim");
		Getter<AllFlavorBean> intGetter = getIntGetter(AllFlavorBean.class, "getIntPrim");
		Getter<AllFlavorBean> doubleGetter = getDoubleGetter(AllFlavorBean.class, "getDoublePrim");

		System.out.println(byteGetter.getByte(b));
		System.out.println(intGetter.getInt(b));
		System.out.println(doubleGetter.getDouble(b));
	}


	public static interface DoubleGetter<T>
	{
		public double get(T t);
	}

	public static interface Getter<T>
	{
		public double  getDouble(T t);
		public int     getInt(T t);
		public boolean getBool(T t);
		public byte    getByte(T t);
		public String  getString(T t);
	}

	public static <T> Getter<T> getIntGetter(Class<T> beanClass, String getterName) throws Throwable
	{
		Getter<T> g = new Getter<T>() 
		{
			ToIntFunction<T> i = getIntGetter(getterName, beanClass);
			@Override public double  getDouble(T t) { return (double) i.applyAsInt(t); }
			@Override public int     getInt(T t)    { return i.applyAsInt(t); }
			@Override public byte    getByte(T t)   { return (byte) i.applyAsInt(t); }
			@Override public boolean getBool(T t)   { return AnnotatedBeanReader.parseBool(i.applyAsInt(t)); }
			@Override public String  getString(T t) { return String.format("%d", i.applyAsInt(t)); }
		};
		return g;
	}

	public static <T> Getter<T> getDoubleGetter(Class<T> beanClass, String getterName) throws Throwable
	{
		Getter<T> g = new Getter<T>() 
		{
			ToDoubleFunction<T> i = getDoubleGetter(getterName, beanClass);
			@Override public double  getDouble(T t) { return i.applyAsDouble(t); }
			@Override public int     getInt(T t)    { return (int) i.applyAsDouble(t); }
			@Override public byte    getByte(T t)   { return (byte) i.applyAsDouble(t); }
			@Override public boolean getBool(T t)   { return AnnotatedBeanReader.parseBool(i.applyAsDouble(t)); }
			@Override public String  getString(T t) { return String.format("%f", i.applyAsDouble(t)); }
		};
		return g;
	}

	public static <T> Getter<T> buildFieldGetter(Class<T> beanClass, String fieldName) throws Throwable
	{
		Getter<T> g;
		PropertyDescriptor valueProperty;
		final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		final Function<String, PropertyDescriptor> property = name -> 
		Stream.of(beanInfo.getPropertyDescriptors())
		.filter(p -> name.equals(p.getName()))
		.findFirst()
		.orElseThrow(() -> new IllegalStateException("Not found: " + name));
		valueProperty = property.apply(fieldName);

		final MethodHandles.Lookup lookup = MethodHandles.lookup();


		//		switch(typ)
		//		{
		//		case("int"):
		//			final ToIntFunction<T> valueGetter = (ToIntFunction<T>) createGetter(lookup, lookup.unreflect(valueProperty.getReadMethod()));
		//			g = new Getter<T>() 
		//		{
		//			ToIntFunction<T> i = getIntGetter(getterName, beanClass);
		//			@Override public double  getDouble(T t) { return (double) i.applyAsInt(t); }
		//			@Override public int     getInt(T t)    { return i.applyAsInt(t); }
		//			@Override public byte    getByte(T t)   { return (byte) i.applyAsInt(t); }
		//			@Override public boolean getBool(T t)   { return AnnotatedBeanReader.parseBool(i.applyAsInt(t)); }
		//			@Override public String  getString(T t) { return String.format("%d", i.applyAsInt(t)); }
		//		};
		//		return g;
		//		//		break;
		//		case("double"):
		//			g = new Getter<T>() 
		//		{
		//			ToDoubleFunction<T> i = getDoubleGetter(getterName, beanClass);
		//			@Override public double  getDouble(T t) { return i.applyAsDouble(t); }
		//			@Override public int     getInt(T t)    { return (int) i.applyAsDouble(t); }
		//			@Override public byte    getByte(T t)   { return (byte) i.applyAsDouble(t); }
		//			@Override public boolean getBool(T t)   { return AnnotatedBeanReader.parseBool(i.applyAsDouble(t)); }
		//			@Override public String  getString(T t) { return String.format("%f", i.applyAsDouble(t)); }
		//		};
		//		return g;
		//		}	

		final Function<T, ?> valueGetter = createGetter(lookup, lookup.unreflect(valueProperty.getReadMethod()));
		//		final Function<T, ?> nameGetter = createGetter(lookup, lookup.unreflect(nameProperty.getReadMethod()));

		g = new Getter<T>() 
		{
			@Override public double  getDouble(T t) { return (double) valueGetter.apply(t); }
			@Override public int     getInt(T t)    { return (int)    valueGetter.apply(t); }
			@Override public byte    getByte(T t)   { return (byte)   valueGetter.apply(t); }
			@Override public boolean getBool(T t)   { return AnnotatedBeanReader.parseBool((double) valueGetter.apply(t)); }
			@Override public String  getString(T t) { return String.format("%d", valueGetter.apply(t)); }
		};
		return g;
	}


	public static <T> Function<T, ?> createGetter(
			final MethodHandles.Lookup lookup,
			final MethodHandle getter) throws Exception
	{
		final CallSite site = LambdaMetafactory.metafactory(
				lookup, "apply",
				MethodType.methodType(Function.class),
				MethodType.methodType(Object.class, Object.class), //signature of method Function.apply after type erasure
				getter,
				getter.type()); //actual signature of getter
		try {
			return (Function<T, ?>) site.getTarget().invokeExact();
		} catch (final Exception e) {
			throw e;
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}
	public static <T> ToIntFunction<T> createIntGetter(
			final MethodHandles.Lookup lookup,
			final MethodHandle getter) throws Exception
	{
		final CallSite site = LambdaMetafactory.metafactory(
				lookup, "apply",
				MethodType.methodType(Function.class),
				MethodType.methodType(Object.class, Object.class), //signature of method Function.apply after type erasure
				getter,
				getter.type()); //actual signature of getter
		try {
			return (ToIntFunction<T>) site.getTarget().invokeExact();
		} catch (final Exception e) {
			throw e;
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static <T> ToDoubleFunction<T> createDoubleGetter(
			final MethodHandles.Lookup lookup,
			final MethodHandle getter) throws Exception
	{
		final CallSite site = LambdaMetafactory.metafactory(
				lookup, "apply",
				MethodType.methodType(Function.class),
				MethodType.methodType(Object.class, Object.class), //signature of method Function.apply after type erasure
				getter,
				getter.type()); //actual signature of getter
		try {
			return (ToDoubleFunction<T>) site.getTarget().invokeExact();
		} catch (final Exception e) {
			throw e;
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	public static interface GetterComparator <T> extends Comparator<T>
	{
		public double get(T t);
		public DoubleGetter<T> getGetter();
	}

	public static <T> DoubleGetter<T> buildDoubleGetter(Class<T> clazz, String getterName) throws Throwable
	{
		DoubleGetter<T> g;
		Method m = clazz.getMethod(getterName);
		String typ = m.getReturnType().getSimpleName();

		switch(typ)
		{
		case("int"):
			g = new DoubleGetter<T>() 
		{
			ToIntFunction<T> i = getIntGetter(getterName, clazz);
			@Override public double get(T t) { return (double) i.applyAsInt(t); }
		};
		break;
		case("double"):
			g = new DoubleGetter<T>() 
		{
			ToDoubleFunction<T> d = getDoubleGetter(getterName, clazz);
			@Override public double get(T t) { return d.applyAsDouble(t); }
		};
		break;
		default: throw new IllegalArgumentException("Could not create a getter");
		}	
		return g;
	}

	public static <T> void printListWithGetter(List<T> l, DoubleGetter<T> getter, int n, String message)
	{
		n = Math.min(n, l.size());
		for (int i = 0; i < n; i++)
		{
			System.out.println(message + "Element " + i + ": " + getter.get(l.get(i)));
		}
	}

	@Deprecated
	public static <T> void printListWithGetter(List<T> l, ToDoubleFunction<T> getter, int n, String message)
	{
		n = Math.min(n, l.size());
		for (int i = 0; i < n; i++)
		{
			System.out.println(message + "Element " + i + ": " + getter.applyAsDouble(l.get(i)));
		}
	}

	public static <T> GetterComparator<T> getGetterComparator(
			Class<T> clazz, String getterName, boolean hiToLo) throws Throwable 
	{
		DoubleGetter<T> g = buildDoubleGetter(clazz, getterName);

		if (hiToLo)
			return new GetterComparator<T>() {
			double v1, v2;
			@Override public double get(T t) { return g.get(t); }
			@Override public int compare(T t1, T t2)
			{
				v2 = g.get(t1); v1 = g.get(t2);

				if (v1 > v2) return 1;
				if (v1 < v2) return -1;
				return 0;
			}
			@Override public DoubleGetter<T> getGetter() { return g; }
		};

		else
			return new GetterComparator<T>() {
			double v1, v2;
			@Override public double get(T t) { return g.get(t); }
			@Override public int compare(T t1, T t2)
			{
				v2 = g.get(t1); v1 = g.get(t2);

				if (v1 > v2) return -1;
				if (v1 < v2) return  1;
				return 0;
			}
			@Override public DoubleGetter<T> getGetter() { return g; }
		};
	}

	/**
	 *  Get a method handler for a getter
	 * 
	 *  Adapted from:
	 *  Java 8 generic LambdaMetafactory?
	 *  http://stackoverflow.com/questions/28196829/java-8-generic-lambdametafactory
	 *  Black magic solution for: "Java 8 access private member with lambda?"
	 *  http://stackoverflow.com/questions/28184065/java-8-access-private-member-with-lambda
	 * 
	 * @param beanClass
	 * @param functionClass
	 * @param getterName
	 * @param functionName
	 * @return
	 * @throws Throwable
	 */
	static <T, S> MethodHandle buildHandler(
			Class<T> beanClass, Class<S> functionClass,
			String getterName, String functionName) throws Throwable 
	{
		Lookup trusted;
		try {
			Lookup original = MethodHandles.lookup();
			Field internal = Lookup.class.getDeclaredField("IMPL_LOOKUP");
			internal.setAccessible(true);
			trusted = (Lookup) internal.get(original);
		} catch ( Throwable e) { throw new RuntimeException("Missing trusted lookup", e); }

		Lookup beanCaller = trusted.in(beanClass);
		Method getterMethod = beanClass.getDeclaredMethod(getterName);
		MethodHandle implementationMethod = beanCaller.unreflect(getterMethod);

		MethodType factoryMethodType = MethodType.methodType(functionClass);
		MethodType functionMethodType = functionType(functionClass, functionName);

		CallSite lambdaFactory = LambdaMetafactory.metafactory( //
				beanCaller, // Represents a lookup context.
				functionName, // The name of the method to implement.
				factoryMethodType, // Signature of the factory method.
				functionMethodType, // Signature of function implementation.
				implementationMethod, // Function method implementation.
				implementationMethod.type() // Function method type signature.
				);

		MethodHandle factoryInvoker = lambdaFactory.getTarget();
		return factoryInvoker;
	}

	private static <T> ToIntFunction<T> getIntGetter(String getterName, Class<T> beanClass) throws Throwable
	{
		MethodHandle mh = buildHandler(beanClass, ToIntFunction.class, getterName, "applyAsInt");
		return (ToIntFunction<T>) mh.invoke();
	}

	public static <T> ToDoubleFunction<T> getDoubleGetter(String getterName, Class<T> beanClass) throws Throwable
	{
		MethodHandle mh = buildHandler(beanClass, ToDoubleFunction.class, getterName, "applyAsDouble");
		return (ToDoubleFunction<T>) mh.invoke();
	}

	
	
	public static <T> void getterDemo(String intGetterName, String dblGetterName, Class<T> clazz, T t) throws Throwable
	{
		//		ToIntFunction<T> intGetter = getIntGetter(intGetterName, clazz);
		//		ToDoubleFunction<T> dblGetter = getDoubleGetter(dblGetterName, clazz);

		GetterComparator<T> intComp = getGetterComparator(clazz, intGetterName, false);
		GetterComparator<T> dblComp = getGetterComparator(clazz, dblGetterName, false);
		System.out.println(intComp.getGetter().get(t));
		System.out.println(intComp.get(t));
		System.out.println(dblComp.getGetter().get(t));
		System.out.println(dblComp.get(t));
	}

	/**
	 * 
	 * @param klaz
	 * @param name
	 * @return
	 */
	public static Method functionMethod( Class<?> klaz,  String name) {
		Method result = null;
		Method[] methodList = klaz.getDeclaredMethods();
		for ( Method method : methodList) 
		{
			if (method.getName().equals(name)) 
				if (result == null) result = method;
				else throw new RuntimeException("Duplicate method: " + name);
		}
		if (result == null)
			throw new RuntimeException("Missing method: " + name);
		else return result;
	}

	/**
	 *  Utility function to get a method type 
	 * @param klaz
	 * @param name
	 * @return
	 */
	public static MethodType functionType( Class<?> klaz,  String name) {
		Method method = functionMethod(klaz, name);
		Class<?> methodReturn = method.getReturnType();
		Class<?>[] methodParams = method.getParameterTypes();
		return MethodType.methodType(methodReturn, methodParams);
	}

	public static class Demo
	{
		private double d1, d2, d3;
		private int i1, i2, i3;

		public Demo(double d1, double d2, double d3, int i1, int i2, int i3)
		{
			this.d1 = d1; this.d2 = d2; this.d3 = d3;
			this.i1 = i1; this.i2 = i2; this.i3 = i3;
		}

		public void setI1(int i) { this.i1 = i; }

		public int getI1() { return i1; }
		public int getI2() { return i2; }
		public int getI3() { return i3; }
		public double getD1() { return d1; }
		public double getD2() { return d2; }
		public double getD3() { return d3; }
	}

}
