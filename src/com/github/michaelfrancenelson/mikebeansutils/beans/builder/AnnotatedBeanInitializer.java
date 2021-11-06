package com.github.michaelfrancenelson.mikebeansutils.beans.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.github.michaelfrancenelson.mikebeansutils.utils.FieldUtils;

/** Tools for initializing and checking initialization status of annotated beans.
 *  This has gotten much more boilerplate-y than I had hoped.
 * 
 * @author michaelfrancenelson
 */
public class AnnotatedBeanInitializer extends AnnotatedBeanReader
{

	public static final int     NA_INT      = Integer.MIN_VALUE;
	public static final double  NA_DOUBLE   = -Double.MAX_VALUE;
	public static final String  NA_STRING   = "";
	public static final char    NA_CHAR     = (char)'0';

	/* With the massively overloaded methods below, there are lots of opportunities
	 *  for misunderstandings in the calls below so hopefully these will help clarify. */
	private static boolean yesEnforce = true, noEnforce = false;

	
	/* Initializers */
	/**
	 * Initialize all the static fields (public and private) to NA
	 * @param clazz
	 */
	public static <T> void initializeStaticFieldsToNA(Class<T> clazz)
	{ initializeStaticFieldsToNA(clazz, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, false);	}

	public static <T> void initializeInstanceFieldsToNA(Class<T> clazz, T t)
	{ initializeInstanceFieldsToNA(clazz, t, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, false); }

	/* Instance checkers. */
	public static <T> boolean checkInstanceInitialized(Class<T> clazz, T t)
	{ return isInstanceInitialized(clazz, t, noEnforce, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, false); }

	public static <T> boolean checkInstanceInitialized( Class<T> clazz, T t, int naInt, double naDouble, String naString, char naChar)
	{ return isInstanceInitialized(clazz, t, noEnforce, naInt, naDouble, naString, naChar, false); }

	public static <T> boolean checkInstanceInitialized(Class<T> clazz, Iterable<T> t)
	{ return areBeansInitialized(clazz, t, noEnforce, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, false); }

	public static <T> boolean checkInstanceInitialized( Class<T> clazz, Iterable<T> t, int naInt, double naDouble, String naString, char naChar)
	{ return areBeansInitialized(clazz, t, noEnforce, naInt, naDouble, naString, naChar, false); }

	
	/* Static checkers */
	public static <T> boolean checkStaticInitialized(Class<T> clazz)
	{ return isStaticInitialized(clazz, noEnforce, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, false); }

	public static <T> boolean checkStaticInitialized( Class<T> clazz, int naInt, double naDouble, String naString,char naChar)
	{ return isStaticInitialized(clazz, noEnforce, naInt, naDouble, naString, naChar, false); }


	
	/* Instance enforcers. */
	public static <T> boolean enforceInstanceInitialized(Class<T> clazz, T t, boolean errorIfNone)
	{ return isInstanceInitialized(clazz, t, yesEnforce, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, errorIfNone); }

	public static <T> boolean enforceInstanceInitialized(Class<T> clazz, T t, int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{ return isInstanceInitialized(clazz, t, yesEnforce, naInt, naDouble, naString, naChar, errorIfNone); }

	public static <T> boolean enforceInstanceInitialized(Class<T> clazz, Iterable<T> t, boolean errorIfNone)
	{ return areBeansInitialized(clazz, t, yesEnforce, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, errorIfNone); }

	public static <T> boolean enforceInstanceInitialized( Class<T> clazz, Iterable<T> t, int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{ return areBeansInitialized(clazz, t, yesEnforce, naInt, naDouble, naString, naChar, errorIfNone); }



	/* Static enforcers */
	public static <T> boolean enforceStaticInitialized(Class<T> clazz, boolean errorIfNone)
	{ return isStaticInitialized(clazz, yesEnforce, NA_INT, NA_DOUBLE, NA_STRING, NA_CHAR, errorIfNone); }

	public static <T> boolean enforceStaticInitialized(Class<T> clazz, int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{ return isStaticInitialized(clazz, yesEnforce, naInt, naDouble, naString, naChar, errorIfNone); }

	
	
	/**
	 * 
	 * @param clazz
	 * @param t
	 * @param naInt
	 * @param naDouble
	 * @param naString
	 * @param naChar
	 */
	private static <T> void initializeInstanceFieldsToNA(Class<T> clazz, T t, int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{
		for (Field f : FieldUtils.getFields(clazz, InitializedField.class, true, false, true, errorIfNone))
		{
			try { if (!Modifier.isStatic(f.getModifiers())) setNA(t, f, naInt, naDouble, naString, naChar); }
			catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace();}
		}
	}

	private static <T> void initializeStaticFieldsToNA(Class<T> clazz, int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{
		for (Field f : FieldUtils.getFields(clazz, InitializedField.class, false, true, true, errorIfNone))
		{
			try { if (Modifier.isStatic(f.getModifiers())) setNA(null, f, naInt, naDouble, naString, naChar); }
			catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace();}
		}
	}

	/**
	 * @param t instance of T
	 * @param f
	 * @param naInt NA for integers
	 * @param naDouble NA for doubles
	 * @param naString NA for strings
	 * @param naChar NA for char
	 * @param <T> generic type parameter
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static <T> void setNA(
			T t, Field f, 
			int naInt, double naDouble, 
			String naString, char naChar)
			throws IllegalArgumentException, IllegalAccessException
	{
		f.setAccessible(true);
		String type = f.getType().getSimpleName();

		switch(type)
		{
		case("int"):     { f.setInt(t, naInt); break;}
		case("double"):  { f.setDouble(t, naDouble); break; }
		/* It doesn't make sense to have a na value for the primitive boolean 
		 * since true and false are both reasonable values. 
		 * Also, ignore the less-used primitive types*/
		case("boolean"): case("byte"): case("short"):
		case("long"): case("float"): { break; } 

		case("String"):  { f.set(t, naString); break; }
		case("char"):    { f.setChar(t, naChar); break;}

		case("Integer"): { f.set(t, (Integer) naInt); break; }
		case("Double"):  { f.set(t, (Double) naDouble); break; }
		case("Boolean"): { f.set(t, (Boolean) null); break; }
		default:         { f.set(t, null); break; }
		}
	}

	/**
	 * 
	 * @param clazz type of the bean.
	 * @param bean instance of T
	 * @param checkInstance
	 * @param checkStatic
	 * @param enforce should an exception be thrown if field is not initialized?
	 * @param naInt NA for integers
	 * @param naDouble NA for doubles
	 * @param naString NA for strings
	 * @param naChar NA for char
	 * @param <T> generic type parameter
	 * @return
	 */
	private static <T> boolean isInstanceInitialized(
			Class<T> clazz, T bean, boolean enforce, 
			int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{
		String message, typeName;
		typeName = clazz.getSimpleName();

		for (Field f : FieldUtils.getFields(clazz, InitializedField.class, true, false, true, errorIfNone))
		{
			if (!Modifier.isStatic(f.getModifiers()))
			{
				message = "Instance Field: " + f.getName() + " in type " + typeName + " is not initialized.";
				try {
					if (AnnotatedBeanReporter.isNA(
							clazz, bean, f,
//							naInt, naDouble, 
							naString, naChar)) 
					{
						//						logger.debug(message);
						if (enforce) throw new IllegalArgumentException(message);
						return false;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {	e.printStackTrace(); }
			}
		}
		return true;
	}

	private static <T> boolean isStaticInitialized(
			Class<T> clazz, boolean enforce,
			int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{
		String message, typeName;
		typeName = clazz.getSimpleName();

		for (Field f : FieldUtils.getFields(clazz, InitializedField.class, false, true, true, errorIfNone))
		{
			if (Modifier.isStatic(f.getModifiers()))
			{
				message = "Static Field: " + f.getName() + " in type " + typeName + " is not initialized.";
				try {
					if (AnnotatedBeanReporter.isNA(clazz, null, f, 
//							naInt, naDouble,
							naString, naChar)) 
					{
						//						logger.debug(message);
						if (enforce) throw new IllegalArgumentException(message);
						return false;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {	e.printStackTrace(); }
			}
		}
		return true;
	}
	
	private static <T> boolean areBeansInitialized(
			Class<T> clazz, Iterable<T> beans, boolean enforce, 
			int naInt, double naDouble, String naString, char naChar, boolean errorIfNone)
	{
		String message, typeName;
		typeName = clazz.getSimpleName();

		for (Field f : FieldUtils.getFields(clazz, InitializedField.class, true, true, true, errorIfNone))
		{
			if (!Modifier.isStatic(f.getModifiers()))
			{
				message = "Instance Field: " + f.getName() + " in type " + typeName + " is not initialized.";
				for (T bean : beans)
				{
				try {
					if (AnnotatedBeanReporter.isNA(clazz, bean, f,
//							naInt, naDouble, 
							naString, naChar)) 
					{
						//						logger.debug(message);
						if (enforce) throw new IllegalArgumentException(message);
						return false;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {	e.printStackTrace(); }
				}
			}
		}
		return true;
	}
}