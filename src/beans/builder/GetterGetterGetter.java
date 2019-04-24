package beans.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** Tools for retrieving and saving fields in AnnotatedBean objects
 * 
 * @author michaelfrancenelson
 *
 */
public class GetterGetterGetter
{
	@FunctionalInterface public interface StringValGetter<T> { String get(T t);};

	@FunctionalInterface public interface IntGetter <T> { int get(T obj); }
	@FunctionalInterface public interface ByteGetter <T> { byte get(T obj); }
	@FunctionalInterface public interface DoubleGetter<T> { double get(T obj); }
	@FunctionalInterface public interface BooleanGetter<T> { boolean get(T obj); }
	@FunctionalInterface public interface ParsingBooleanGetter<T> { boolean get(T obj); }

	@FunctionalInterface public interface StringGetter<T> { String get(T obj); }
	@FunctionalInterface public interface CharGetter<T> { char get(T obj); }

	@FunctionalInterface interface BoxedDoubleGetter<T> { Double get(T obj); }
	@FunctionalInterface interface BoxedIntGetter<T> { Integer get(T obj); }
	@FunctionalInterface interface BoxedBooleanGetter<T> { Boolean get(T obj); }

	@FunctionalInterface interface ObjGetter <T> { Object get(T obj); }

	/** Retrieve the names of the fields for annotated bean IO
	 * 
	 * @param ff  list of annotated fields
	 * @return a getter names of all the annotated fields
	 */
	public static List<String> columnHeaderGetter(List<Field> ff)
	{
		List<String> out = new ArrayList<>();
		for (Field f : ff) out.add(f.getName());
		return out;
	}


	/** Get a list of getters for string representations
	 *  of all the bean's fields. 
	 * 
	 * @param ff list of annotated fields
	 * @param dblFmt how to print double values
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> List<StringValGetter<T>>
	stringValGetterGetter(Class<T> t, List<Field> ff, String dblFmt)
	{
		List<StringValGetter<T>> out = new ArrayList<>();
		for (Field f : ff) out.add(stringValGetterGetter(t, f, dblFmt));
		return out;
	}

	/** Create a getter function to return a string representation 
	 *  of an annotated bean's field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param dblFmt how to print double values
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> StringValGetter<T>
	stringValGetterGetter(Class<T> t, Field f, String dblFmt)
	{
		String type = f.getType().getSimpleName();
		StringValGetter<T> out = null;

		switch(type)
		{
		case("double"):  
		{
			DoubleGetter<T> d = doubleGetterGetter(t, f);
			out = (T tt) -> { return String.format(dblFmt, d.get(tt)); }; 
			break; 
		} 

		case("int"): 
		{
			IntGetter<T> d = intGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 

		case("String"):  
		{
			StringGetter<T> d = stringGetterGetter(t, f);
			out = (T tt) -> {return d.get(tt); };
			break; 
		}

		case("boolean"): 
		{
			BooleanGetter<T> d = booleanGetterGetter(t, f);
			out = (T tt) -> {return Boolean.toString(d.get(tt)); };
			break; 
		}

		case("Integer"):
		{
			BoxedIntGetter<T> d = boxedIntGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 

		case("Double"): 
		{
			BoxedDoubleGetter<T> d = boxedDoubleGetterGetter(t, f);
			out = (T tt) -> { return String.format(dblFmt, d.get(tt)); };	
			break; 
		}

		case("Boolean"): 
		{
			BoxedBooleanGetter<T> d = boxedBooleanGetterGetter(t, f);
			out = (T tt) -> {return Boolean.toString(d.get(tt)); };
			break; 
		}

		case("char"): {out = (T tt) ->
		{
			try { return String.valueOf(f.getChar(tt)); }
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); } throw new IllegalArgumentException();};
			break;
		}

		default:
		{
			ObjGetter<T> d = objectGetterGetter(t, f);
			out = (T tt) -> { return d.get(tt).toString(); };
			break; 
		}
		}
		return out;
	}

	/** build a getter for a primitive int field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field	 
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> IntGetter<T> 
	intGetterGetter(Class<T> t, Field f)
	{
		IntGetter<T> out = (T tt) -> 
		{
			try { return f.getInt(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		return out;
	}

	/** build a getter for a primitive int field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field	 
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> ByteGetter<T> 
	byteGetterGetter(Class<T> t, Field f)
	{
		ByteGetter<T> out = (T tt) -> 
		{
			switch(f.getType().getSimpleName())
			{
			case("byte"):
			{
				try { return f.getByte(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			}
			case("int"):
			{
				try { return (byte) f.getInt(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			}
			case("double"):
			{
				try { return (byte) f.getDouble(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();
			}
			}
			}
			throw new IllegalArgumentException("Input could not be parsed as a byte value.");
		};
		return out;
	}

	/** build a getter for a primitive double field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> DoubleGetter<T> 
	doubleGetterGetter(Class<T> t, Field f)
	{
		DoubleGetter<T> out = (T tt) ->
		{
			try { return f.getDouble(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		return out;
	}

	/** build a getter for a primitive boolean field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> BooleanGetter<T> 
	booleanGetterGetter(Class<T> t, Field f)
	{
		BooleanGetter<T> out = (T tt) ->
		{
			try { return f.getBoolean(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		return out;
	}

	/** build a getter for a primitive boolean field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> ParsingBooleanGetter<T> 
	parsingBooleanGetterGetter(Class<T> t, Field f)
	{
		ParsingBooleanGetter<T> out = (T tt) ->
		{
			try 
			{ 
				String val = (String) f.get(tt);
				return AnnotatedBeanBuilder.parseBool(val);
			}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		return out;
	}

	/** build a getter for a String field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> StringGetter<T> 
	stringGetterGetter(Class<T> t, Field f)
	{
		StringGetter<T> out = (T tt) ->
		{
			try { return f.get(tt).toString();}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		return out;
	}

	/** build a getter for a general field (to be cast later)
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> ObjGetter<T> 
	objectGetterGetter(Class<T> t, Field f)
	{
		ObjGetter<T> out = (T tt) -> 
		{
			try { return f.get(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		return out;
	}

	/** build a getter for a boxed double field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> BoxedDoubleGetter<T> 
	boxedDoubleGetterGetter(Class<T> t, Field f)
	{
		ObjGetter<T> g = objectGetterGetter(t, f);
		return (T tt) -> { return (Double) g.get(tt); };
	}

	/** Build a getter for a boxed int field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> BoxedIntGetter<T> 
	boxedIntGetterGetter(Class<T> t, Field f)
	{
		ObjGetter<T> g = objectGetterGetter(t, f);
		return  (T tt) -> { return (Integer) g.get(tt); };
	}

	/** Build a getter for a boxed boolean field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> BoxedBooleanGetter<T> 
	boxedBooleanGetterGetter(Class<T> t, Field f)
	{
		ObjGetter<T> g = objectGetterGetter(t, f);
		return (T tt) -> { return (Boolean) g.get(tt); };
	}
}
