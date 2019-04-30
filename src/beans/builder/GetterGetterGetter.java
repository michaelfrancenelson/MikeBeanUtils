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
	@FunctionalInterface public interface ShortGetter <T> { short get(T obj); }
	@FunctionalInterface public interface LongGetter <T> { long get(T obj); }
	@FunctionalInterface public interface ByteGetter <T> { byte get(T obj); }
	@FunctionalInterface public interface DoubleGetter<T> { double get(T obj); }
	@FunctionalInterface public interface FloatGetter<T> { float get(T obj); }
	@FunctionalInterface public interface CharGetter<T> { char get(T obj); }
	@FunctionalInterface public interface BooleanGetter<T> { boolean get(T obj); }

	@FunctionalInterface public interface ParsingBooleanGetter<T> { boolean get(T obj); }

	@FunctionalInterface public interface StringGetter<T> { String get(T obj); }

	//	@FunctionalInterface public interface BoxedIntGetter<T> { Integer get(T obj); }
	//	@FunctionalInterface public interface BoxedShortGetter<T> { Short get(T obj); }
	//	@FunctionalInterface public interface BoxedLongGetter<T> { Long get(T obj); }
	//	@FunctionalInterface public interface BoxedByteGetter <T> { Byte get(T obj); }
	//	@FunctionalInterface public interface BoxedDoubleGetter<T> { Double get(T obj); }
	//	@FunctionalInterface public interface BoxedFloatGetter<T> { Float get(T obj); }
	//	@FunctionalInterface public interface BoxedChararacterGetter<T> { Character get(T obj); }
	//	@FunctionalInterface public interface BoxedBooleanGetter<T> { Boolean get(T obj); }

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

		switch(type.toLowerCase())
		{
		case("int"): 
		{
			IntGetter<T> d = intGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 

		case("short"): 
		{
			ShortGetter<T> d = shortGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 
		case("long"): 
		{
			LongGetter<T> d = longGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 
		case("byte"): 
		{
			ByteGetter<T> d = byteGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 
		case("double"):  
		{
			DoubleGetter<T> d = doubleGetterGetter(t, f);
			out = (T tt) -> { return String.format(dblFmt, d.get(tt)); }; 
			break; 
		} 
		case("float"):  
		{
			FloatGetter<T> d = floatGetterGetter(t, f);
			out = (T tt) -> { return String.format(dblFmt, d.get(tt)); }; 
			break; 
		}
		case("boolean"): 
		{
			BooleanGetter<T> d = booleanGetterGetter(t, f);
			out = (T tt) -> {return Boolean.toString(d.get(tt)); };
			break; 
		}
		//		case("char"): {out = (T tt) ->
		//		{
		//			CharGetter<T> d = charGetterGetter(t, f);
		//			out = (T tt) -> { return String.valueOf(d.get(tt)); };
		////			try { return String.valueOf(f.getChar(tt)); }
		////			catch (IllegalArgumentException | IllegalAccessException e) 
		////			{ e.printStackTrace(); } throw new IllegalArgumentException();};
		////			break;
		//		}

		case("char"): 
		{
			CharGetter<T> d = charGetterGetter(t, f);
			out = (T tt) -> { return String.valueOf(d.get(tt)); };
		}

		case("Character"): 
		{
			CharGetter<T> d = charGetterGetter(t, f);
			out = (T tt) -> { return String.valueOf(d.get(tt)); };
		}
		case("String"):  
		{
			StringGetter<T> d = stringGetterGetter(t, f);
			out = (T tt) -> {return d.get(tt); };
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
		String type = f.getType().getSimpleName();
		IntGetter<T> out;

		switch(type)
		{
		default:
			out = (T tt) -> 
			{
				try { return f.getInt(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Integer"):
			out = (T tt) -> 
		{
			try { return (Integer) f.get(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		}
		return out;
	}

	/** build a getter for a primitive char field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> ShortGetter<T> 
	shortGetterGetter(Class<T> t, Field f)
	{
		String type = f.getType().getSimpleName();
		ShortGetter<T> out;
		switch(type)
		{
		default:
			out = (T tt) ->
			{
				try { return f.getShort(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Short"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Short) g.get(tt); };
		}
		}
		return out;
	}

	/** build a getter for a primitive long field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field	 
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> LongGetter<T> 
	longGetterGetter(Class<T> t, Field f)
	{

		String type = f.getType().getSimpleName();
		LongGetter<T> out;
		switch(type)
		{
		default:
			out = (T tt) -> 
			{
				try { return f.getLong(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Long"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Long) g.get(tt); };
		}
		}
		return out;
	}

	/** build a getter for a primitive byte field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field	 
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> ByteGetter<T> 
	byteGetterGetter(Class<T> t, Field f)
	{

		String type = f.getType().getSimpleName();
		ByteGetter<T> out;
		switch(type)
		{
		default:
			out = (T tt) -> {
				try { return f.getByte(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Byte"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Byte) g.get(tt); };
		}
		}
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
		String type = f.getType().getSimpleName();
		DoubleGetter<T> out; 
		switch(type)
		{
		default:
			out = (T tt) ->
			{
				try { return f.getDouble(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Double"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Double) g.get(tt); };
		}
		}
		return out;
	}

	/** build a getter for a primitive float field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> FloatGetter<T> 
	floatGetterGetter(Class<T> t, Field f)
	{
		String type = f.getType().getSimpleName();
		FloatGetter<T> out; 
		switch(type)
		{
		default:
			out = (T tt) ->
			{
				try { return f.getFloat(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Float"):
			out = (T tt) -> 
		{
			try { return (Float) f.get(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		}
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
		String type = f.getType().getSimpleName();
		BooleanGetter<T> out;
		switch(type)
		{
		default:
			out = (T tt) -> {
				try { return f.getBoolean(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Boolean"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Boolean) g.get(tt); };
		}
		}

		return out;
	}

	/** build a getter for a primitive char field
	 * 
	 * @param t annotated bean object
	 * @param f annotated field
	 * @param <T> type of bean
	 * @return a getter
	 */
	public static <T> CharGetter<T> 
	charGetterGetter(Class<T> t, Field f)
	{
		String type = f.getType().getSimpleName();
		CharGetter<T> out; 
		switch(type)
		{
		default:
			out = (T tt) ->
			{
				try { return f.getChar(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
		case("Character"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Character) g.get(tt); };
		}
		}
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
				return AnnotatedBeanReader.parseBool(val);
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

}
