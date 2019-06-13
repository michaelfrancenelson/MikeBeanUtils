package utils;

import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import beans.memberState.SimpleFieldWatcher.DisplayName;

public class FieldUtils 
{

	public static <T> T getMatchingItem(List<T> items, Class<T> clazz, String fieldName, String valToMatch)
	{
		Field f = getField(clazz, null, fieldName, false);
		return getMatchingItem(items, f, valToMatch);
	}

	public static <T> T getMatchingItem(List<T> items, Field f, String valToMatch)
	{
		f.setAccessible(true);
		T out = null;

		for (int i = 0; i < items.size(); i++)
		{
			String val = null;
			try {
				T item = items.get(i);
				val = f.get(item).toString();
				if (val.equals(valToMatch))
				{
					out = item;
					break;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} 
		}
		if (out == null) throw new IllegalArgumentException("No matching item found");
		return out;
	}

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

	private static <T> String typString(Class<T> c) { return " in type " + c.getSimpleName(); }

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
	 * @param getSuperclassFields
	 * @param errorIfNone
	 * @return List of matching fields.  If none were found, throws an exception.
	 * @throws IllegalArgumentException
	 */
	public static <T, A extends Annotation> List<Field> getFields(
			Class<T> clazz, Class<A> annClazz, 
			boolean getInstance, boolean getStatic,
			boolean getSuperclassFields, boolean errorIfNone) throws IllegalArgumentException
	{
		if (!getInstance && !getStatic)
			throw new IllegalArgumentException("At least one of the "
					+ "'getInstance' or 'getStatic' parameters must be true.");
		Field[] fields = clazz.getDeclaredFields();
		if (getSuperclassFields) 
		{
			Field[] superFields = clazz.getSuperclass().getDeclaredFields();
			Field[] tmp = new Field[superFields.length + fields.length];

			int index = 0;
			for (int i = 0; i < fields.length; i++) { tmp[index] = fields[i]; index++; }

			for (int i = 0; i < superFields.length; i++) { tmp[index] = superFields[i]; index++;	}
			fields = tmp;
		}

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
		if (errorIfNone && ff.size() == 0)
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
			boolean getSuperclassFields,
			boolean errorIfNone,
			boolean toLowerCase
			) throws IllegalArgumentException
	{
		List<Field> ff = getFields(clazz, annClass, getInstance, getStatic, getSuperclassFields, errorIfNone);
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
		List<Field> ll = getFields(clazz, annClass, true, true, true, true);
		if (!matchCase) fieldName = fieldName.toLowerCase();
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
				if (!matchCase) nameTemp = nameTemp.toLowerCase(); 
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

		List<Field> fields = getFields(clazz, DisplayName.class, true, true, true, true);

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


	//	public static <T> IntBinaryOperator getIntAccessor(Class<T> clazz, String methodName) throws LambdaConversionException, Throwable
	//	{
	//		Method reflected = clazz.getDeclaredMethod(methodName, int.class, int.class);
	//		final MethodHandles.Lookup lookup = MethodHandles.lookup();
	//		MethodHandle mh = lookup.unreflect(reflected);
	//		IntBinaryOperator lambda = (IntBinaryOperator) LambdaMetafactory.metafactory(
	//				lookup,
	//				"applyAsInt", 
	//				MethodType.methodType(IntBinaryOperator.class),
	//				mh.type(),
	//				mh, 
	//				mh.type()).getTarget().invokeExact();
	//	
	//		
	//		
	//		
	//		return lambda;
	//
	//	}
	//	
	//	public static <T> Function getDoubleGetter(Class<T> clazz, String methodName) throws Throwable
	//	{
	//        MethodHandles.Lookup lookup = MethodHandles.lookup();
	//        CallSite site = LambdaMetafactory.metafactory(lookup,
	//                "apply",
	//                MethodType.methodType(Function.class),
	//                MethodType.methodType(Object.class, Object.class),
	//                lookup.findVirtual(clazz, methodName, MethodType.methodType(String.class)),
	//                MethodType.methodType(double.class, clazz));
	//        return (Function) site.getTarget().invokeExact();
	//	}
	//	
	//	public static <T> Function getIntGetter(Class<T> clazz, String methodName) throws Throwable
	//	{
	//		MethodHandles.Lookup lookup = MethodHandles.lookup();
	//		CallSite site = LambdaMetafactory.metafactory(lookup,
	//				"apply",
	//				MethodType.methodType(Function.class),
	//				MethodType.methodType(Object.class, Object.class),
	//				lookup.findVirtual(clazz, methodName, MethodType.methodType(String.class)),
	//				MethodType.methodType(int.class, clazz));
	//		return (Function) site.getTarget().invokeExact();
	//	}


	@FunctionalInterface 
	interface DoubleGetter<T>
	{
		double invoke(final T t);
	}


	@FunctionalInterface
	interface GetterFunction
	{
		double invoke(final Person callable);
	}


	
	
	public static void main(String[] args) throws Throwable 
	{
Class<Person> clazz = Person.class;
	      GetterFunction getterFunction;
	        final MethodHandles.Lookup lookup = MethodHandles.lookup();
	        MethodType methodType = MethodType.methodType(double.class, clazz);
	        final CallSite site = LambdaMetafactory.metafactory(lookup,
	                "invoke",
	                MethodType.methodType(GetterFunction.class),
	                methodType,
	                lookup.findVirtual(Person.class, "getD", 
	                		MethodType.methodType(double.class)),
	                methodType);
	        getterFunction = (GetterFunction) site.getTarget().invokeExact();
	        System.out.println(getterFunction.invoke(new Person(4.5)));



	}

	static class Person
	{
		double d;
		public Person(double d) { this.d = d; }
		public double getD() { return d; }
	}


	public static final class DoubleAccessor<T>
	{
		private final DoubleGetter<T> getterFunction;
		//		private final Function<T, Double> getterFunction;



		public DoubleAccessor(Class<T> clazz, String getterName) throws Throwable
		{
			final MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodType methodType = MethodType.methodType(Double.class, clazz);


			//			MethodType func = MethodType.methodType(double.class);
			//			Method m = clazz.getDeclaredMethod(getterName);
			//			MethodHandle getter = lookup.unreflect(m);

			CallSite site = LambdaMetafactory.metafactory(lookup,
					"invoke",
					MethodType.methodType(DoubleGetter.class),
					methodType,
					lookup.findVirtual(clazz, getterName, MethodType.methodType(double.class)),
					methodType);
			//					func,
			//					getter,
			//					invokedType);
			//					getter.type());
			//					MethodType.methodType(Function.class),
			//					MethodType.methodType(Double.class, clazz),
			//					lookup.findVirtual(clazz, getterName,
			//							MethodType.methodType(double.class)),
			//					MethodType.methodType(double.class, clazz));
			getterFunction = (DoubleGetter<T>) site.getTarget().invokeExact();
		}

		public double executeGetter(T bean) {
			return getterFunction.invoke(bean);
		}	
	}

	public final class IntAccessor<T>
	{
		private final Function<T, Integer> getterFunction;

		public IntAccessor(Class<T> clazz, String getterName) throws Throwable
		{
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			CallSite site = LambdaMetafactory.metafactory(lookup,
					"apply",
					MethodType.methodType(Function.class),
					MethodType.methodType(Object.class, Object.class),
					lookup.findVirtual(clazz, getterName, MethodType.methodType(String.class)),
					MethodType.methodType(int.class, clazz));
			getterFunction = (Function<T, Integer>) site.getTarget().invokeExact();
		}

		public int executeGetter(T bean) {
			return getterFunction.apply(bean);
		}	
	}

	public interface Node {}

	@FunctionalInterface
	interface NodeGetter {
		Node apply (Node node);
	}

	
	
	


	static <T> T produceLambda( //
			final Lookup caller, //
			final Class<T> functionKlaz, //
			final String functionName, //
			final Class<?> functionReturn, //
			final Class<?>[] functionParams, //
			final MethodHandle implementationMethod //
	) throws Throwable {

		final MethodType factoryMethodType = MethodType
				.methodType(functionKlaz);
		final MethodType functionMethodType = MethodType.methodType(
				functionReturn, functionParams);

		final CallSite lambdaFactory = LambdaMetafactory.metafactory( //
				caller, // Represents a lookup context.
				functionName, // The name of the method to implement.
				factoryMethodType, // Signature of the factory method.
				functionMethodType, // Signature of function implementation.
				implementationMethod, // Function method implementation.
				implementationMethod.type() // Function method type signature.
				);

		final MethodHandle factoryInvoker = lambdaFactory.getTarget();

		// FIXME
		/**
		 * <pre>
		 * Exception in thread "main" java.lang.invoke.WrongMethodTypeException: expected ()ToIntFunction but found ()Object
		 * 	at java.lang.invoke.Invokers.newWrongMethodTypeException(Invokers.java:340)
		 * 	at java.lang.invoke.Invokers.checkExactType(Invokers.java:351)
		 * 	at design.PrivateTargetLambdaGeneric.produceLambda(PrivateTargetLambdaGeneric.java:64)
		 * 	at design.PrivateTargetLambdaGeneric.getterLambda(PrivateTargetLambdaGeneric.java:71)
		 * 	at design.PrivateTargetLambdaGeneric.main(PrivateTargetLambdaGeneric.java:100)
		 * </pre>
		 */
		final T lambda = (T) factoryInvoker.invokeExact();

		return lambda;
	}


}

