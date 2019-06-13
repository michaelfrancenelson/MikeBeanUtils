package utils;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class MethodUtils
{
	/**
	 *  Get a method handler for a getter
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

	public static <T> ToIntFunction<T> getIntGetter(String getterName, Class<T> beanClass) throws Throwable
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
		ToIntFunction<T> intGetter = getIntGetter(intGetterName, clazz);
		ToDoubleFunction<T> dblGetter = getDoubleGetter(dblGetterName, clazz);
		System.out.println(intGetter.applyAsInt(t));
		System.out.println(dblGetter.applyAsDouble(t));
	}

	public static void main(String... args) throws Throwable {
		Class<Demo> clazz = Demo.class;
		String getterName = "getI1";
		Demo d1 = new Demo(1.1, 2.2, 3.3, 111, 222, 333);

		getterDemo(getterName, "getD2", clazz, d1);
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
