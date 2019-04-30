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
	@FunctionalInterface public interface CharGetter<T> { char get(T obj); }
	@FunctionalInterface public interface BooleanGetter<T> { boolean get(T obj); }
	@FunctionalInterface public interface ParsingBooleanGetter<T> { boolean get(T obj); }
	@FunctionalInterface public interface StringGetter<T> { String get(T obj); }
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
	toStringGetterGetter(Class<T> t, List<Field> ff, String dblFmt)
	{
		List<StringValGetter<T>> out = new ArrayList<>();
		for (Field f : ff) out.add(toStringGetterGetter(t, f, dblFmt));
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
	toStringGetterGetter(Class<T> t, Field f, String dblFmt)
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

		case("double"):  
		{
			DoubleGetter<T> d = doubleGetterGetter(t, f);
			out = (T tt) -> { return String.format(dblFmt, d.get(tt)); }; 
			break; 
		} 

		case("byte"): 
		{
			ByteGetter<T> d = byteGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 


		case("short"): 
		{
			IntGetter<T> d = intGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		} 

		case("long"): 
		{
			IntGetter<T> d = intGetterGetter(t, f);
			out = (T tt) -> { return String.format("%d", d.get(tt)); };	
			break; 
		}

		case("float"):  
		{
			DoubleGetter<T> d = doubleGetterGetter(t, f);
			out = (T tt) -> { return String.format(dblFmt, d.get(tt)); }; 
			break; 
		}

		case("boolean"): 
		{
			BooleanGetter<T> d = booleanGetterGetter(t, f);
			out = (T tt) -> {return Boolean.toString(d.get(tt)); };
			break; 
		}

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

	/** build a getter for int field
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
		IntGetter<T> out = null;

		switch(type)
		{
		case("int"):
			out = (T tt) -> 
		{
			try { return f.getInt(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		}; break;

		case("Integer"):
			out = (T tt ) -> {
				try { return (Integer) f.get(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			}; break;
		case("long"): out = (T tt) -> { return (int) (long) getFieldObj(t, f, tt); }; break;
		case("Long"): out = (T tt) -> { return (int) (long) getFieldObj(t, f, tt); }; break;
		case("short"): out = (T tt) -> { return (short) getFieldObj(t, f, tt); }; break;
		case("Short"): out = (T tt) -> { return  (short) getFieldObj(t, f, tt); }; break;
		case("byte"): out = (T tt) -> { return (byte) getFieldObj(t, f, tt); }; break;
		case("Byte"): out = (T tt) -> { return (byte) getFieldObj(t, f, tt); }; break;
		case("char"): out = (T tt) -> { return (int) (char) getFieldObj(t, f, tt); }; break;
		case("Character"): out = (T tt) -> { return (int) (char) getFieldObj(t, f, tt); }; break;
		case("double"): out = (T tt) -> { return (int) (double) getFieldObj(t, f, tt); }; break;
		case("Double"): out = (T tt) -> { return (int) (double) getFieldObj(t, f, tt); }; break;
		case("float"): out = (T tt) -> { return (int) (double) (float) getFieldObj(t, f, tt); }; break;
		case("Float"): out = (T tt) -> { return (int) (double) (float) getFieldObj(t, f, tt); }; break;
		case("boolean"): out = (T tt) -> { return (char) intFromBool((boolean) getFieldObj(t, f, tt)); }; break;
		case("Boolean"): out = (T tt) -> { return (char) intFromBool((boolean) getFieldObj(t, f, tt)); }; break;

		case("String"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> {return ((String) g.get(tt)).charAt(0); };
		} break;
		default: 
		}

//		if (out == null) throw new IllegalArgumentException("Unable to create an int getter for field " + f.getName());
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
		ByteGetter<T> out = null;
		switch(type)
		{
		case("byte"):
			out = (T tt) -> {
				try { return f.getByte(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			};
			break;
		case("Byte"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Byte) g.get(tt); };
			break;
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
		DoubleGetter<T> out = null; 
		//		System.out.println("GetterGetterGetter.getDoubleGetter() field name = " + f.getName());
		//		System.out.println("GetterGetterGetter.getDoubleGetter() field type = " + type);

		switch(type)
		{
		case("double"):
			out = (T tt) ->
		{
			try { return f.getDouble(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		};
		break;
		case("Double"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Double) g.get(tt); };
			break;
		}
		case("int"): out = (T tt) -> { return (double) getFieldObj(t, f, tt); }; break;
		case("Integer"): out = (T tt) -> { return (double) getFieldObj(t, f, tt); }; break;
		case("byte"): out = (T tt) -> { return (int) (byte) getFieldObj(t, f, tt); }; break;
		case("Byte"): out = (T tt) -> { return (int) (byte) getFieldObj(t, f, tt); }; break;
		case("long"): out = (T tt) -> { return (int) (long) getFieldObj(t, f, tt); }; break;
		case("Long"): out = (T tt) -> { return (int) (long) getFieldObj(t, f, tt); }; break;
		case("short"): out = (T tt) -> { return (double) getFieldObj(t, f, tt); }; break;
		case("Short"): out = (T tt) -> { return (double) getFieldObj(t, f, tt); }; break;
		case("float"): out = (T tt) -> { return (double) (float) getFieldObj(t, f, tt); }; break;
		case("Float"):	out = (T tt) -> { return (double) (float) getFieldObj(t, f, tt); }; break;
		case("char"): out = (T tt) -> { return (int) (char) getFieldObj(t, f, tt); }; break;
		case("Character"):	out = (T tt) -> { return (int) (char) getFieldObj(t, f, tt); }; break;
		case("String"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> {return ((String) g.get(tt)).charAt(0); };
		} break;
		}
		//		if (out == null) throw new IllegalArgumentException("Unable to create a double getter for field " + f.getName());
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
		BooleanGetter<T> out = null;
		switch(type)
		{
		case("boolean"):
			out = (T tt) -> {
				try { return f.getBoolean(tt);}
				catch (IllegalArgumentException | IllegalAccessException e) 
				{ e.printStackTrace(); throw new IllegalArgumentException();}
			}; break;
		case("Boolean"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Boolean) g.get(tt); };
			break;
		}
		case("int"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) getFieldObj(t, f, tt)); }; break;
		case("Integer"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) getFieldObj(t, f, tt)); }; break;
		case("long"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) (long) getFieldObj(t, f, tt)); }; break;
		case("Long"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) (long) getFieldObj(t, f, tt)); }; break;
		case("short"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) (short) getFieldObj(t, f, tt)); }; break;
		case("Short"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) (short) getFieldObj(t, f, tt)); }; break;
		case("byte"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) (byte) getFieldObj(t, f, tt)); }; break;
		case("Byte"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) (byte) getFieldObj(t, f, tt)); }; break;
		case("char"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) getFieldObj(t, f, tt)); }; break;
		case("double"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((double) getFieldObj(t, f, tt)); }; break;
		case("Double"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((double) getFieldObj(t, f, tt)); }; break;
		case("float"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((double) (float) getFieldObj(t, f, tt)); }; break;
		case("Float"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((double) (float) getFieldObj(t, f, tt)); }; break;
		case("Character"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) getFieldObj(t, f, tt)); }; break;
		case("String"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> {return AnnotatedBeanReader.parseBool(((String) g.get(tt)).charAt(0)); };
		} break;
		}	
//		if (out == null) throw new IllegalArgumentException("Unable to create a boolean getter for field " + f.getName());

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
		CharGetter<T> out = null; 
		switch(type)
		{
		case("char"):
			out = (T tt) ->
		{
			try { return f.getChar(tt);}
			catch (IllegalArgumentException | IllegalAccessException e) 
			{ e.printStackTrace(); throw new IllegalArgumentException();}
		}; break;
		case("Character"):
		{
			ObjGetter<T> g = objectGetterGetter(t, f);
			out = (T tt) -> { return (Character) g.get(tt); };
			break;
		}

		case("long"): out = (T tt) -> { return (char) getFieldObj(t, f, tt); }; break;
		case("Long"): out = (T tt) -> { return (char) getFieldObj(t, f, tt); }; break;
		case("short"): out = (T tt) -> { return (char) (int) (short) getFieldObj(t, f, tt); }; break;
		case("Short"): out = (T tt) -> { return (char) (int) (short) getFieldObj(t, f, tt); }; break;
		case("int"): out = (T tt) -> { return (char) (int) getFieldObj(t, f, tt); }; break;
		case("Integer"): out = (T tt) -> { return (char) (int) getFieldObj(t, f, tt); }; break;
		case("byte"): out = (T tt) -> { return (char) (byte) getFieldObj(t, f, tt); }; break;
		case("Byte"): out = (T tt) -> { return (char) (byte) getFieldObj(t, f, tt); }; break;
		case("double"): out = (T tt) -> { return (char) (double) getFieldObj(t, f, tt); }; break;
		case("Double"): out = (T tt) -> { return (char) (double) getFieldObj(t, f, tt); }; break;
		case("float"): out = (T tt) -> { return (char) (float) getFieldObj(t, f, tt); }; break;
		case("Float"): out = (T tt) -> { return (char) (float) getFieldObj(t, f, tt); }; break;
		case("boolean"): out = (T tt) -> { return (char) intFromBool((boolean) getFieldObj(t, f, tt)); }; break;
		case("Boolean"): out = (T tt) -> { return (char) intFromBool((boolean) getFieldObj(t, f, tt)); }; break;
		case("String"): out = (T tt) -> { return (char) ((String) getFieldObj(t, f, tt)).charAt(0); }; break;
		default: 
		}
//		if (out == null) throw new IllegalArgumentException("Unable to create a character getter for field " +
//				f.getName() + " of type " + type);
		
		return out;
	}

	static <T> Object getFieldObj(Class<T> t, Field f, T tt) 
	{
		ObjGetter<T> g = objectGetterGetter(t, f); 
		return g.get(tt);
	}

	static double dblFromBool(boolean b) { if (b) return 1.0; else return 0.0; }
	static int intFromBool(boolean b) { if (b) return 1; else return 0; }

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
		ParsingBooleanGetter<T> out = null;
		out = (T tt) ->
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



//
//out = (T tt) -> 
//{
//	try { return (Float) f.get(tt);}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//};
//}

//out = (T tt) -> 
//{
//
//try { return ((Float) f.get(tt)).doubleValue();}
//catch (IllegalArgumentException | IllegalAccessException e) 
//{ e.printStackTrace(); throw new IllegalArgumentException();}
//};
//break;
//case("int"): out = (T tt) -> { return (double) getOffType(t, f, tt); }; break;

////			out = (T tt) -> { return getBoxDouble(t, f, tt); }; break; 
//out = (T tt) -> { 
//
//	Object o = getOffType(t, f, tt);
//	System.out.println("GetterGetterGetter.getDoubleGetter() field type = " + o.getClass());
//
//	return (double) getOffType(t, f, tt); }; 
//	//			{
//	//				try { return (int) f.getInt(tt);}
//	//				catch (IllegalArgumentException | IllegalAccessException e) 
//	//				{ e.printStackTrace(); throw new IllegalArgumentException();}
//	//			}; 
//	//			break;
//	break;
//case("Integer"): out = (T tt) -> { return (double) getOffType(t, f, tt); }; break;
//case("Integer"): out = (T tt) -> { return getBoxInt(t, f, tt); }; break;
//		out = (T tt) -> 
//		{
//			try { return (Integer) f.get(tt);}
//			catch (IllegalArgumentException | IllegalAccessException e) 
//			{ e.printStackTrace(); throw new IllegalArgumentException();}
//		}; 
//			break;
//case("long"): out = (T tt) -> { return (double) getOffType(t, f, tt); }; break;

//out = (T tt) -> 
//{
//try { return (int)  f.getLong(tt);}
//catch (IllegalArgumentException | IllegalAccessException e) 
//{ e.printStackTrace(); throw new IllegalArgumentException();}
//};break;
//case("Long"): out = (T tt) -> { return (double) getOffType(t, f, tt); }; break;
//{
//out = (T tt) -> { return (int) getBoxLong(t, f, tt); };
////			ObjGetter<T> g = objectGetterGetter(t, f);
////			out = (T tt) -> { return (int) (long) g.get(tt); };
//} break;
//case("short"): out = (T tt) -> { return (double) getOffType(t, f, tt); }; break;
//out = (T tt) ->
//{
//try { return (int) f.getShort(tt);}
//catch (IllegalArgumentException | IllegalAccessException e) 
//{ e.printStackTrace(); throw new IllegalArgumentException();}
//}; break;
//case("Short"): out = (T tt) -> { return (double) getOffType(t, f, tt); }; break;
//{
//ObjGetter<T> g = objectGetterGetter(t, f);
//out = (T tt) -> 
//{ 
//	//				System.out.println("GetterGetterGetter.getIntGetter() field type = " + g.get(tt).getClass());
//	return ((Short) g.get(tt)).intValue();
//};
//break;
//}

//			//			out = (T tt) -> { return (int) getBoxLong(t, f, tt); };
//	out = (T tt) -> 
//{
//
//	try { return (int)  f.getLong(tt);}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//};
//break;
//case("Long"): out = (T tt) -> { return (int) getBoxLong(t, f, tt); }; break;
////			ObjGetter<T> g = objectGetterGetter(t, f);
////			out = (T tt) -> { return (int) (long) g.get(tt); };
////		} break;
//case("short"):
//	out = (T tt) ->
//{
//	try { return (int) f.getShort(tt);}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//}; break;
//case("Short"):
//{
//	ObjGetter<T> g = objectGetterGetter(t, f);
//	out = (T tt) -> 
//	{ 
//		//				System.out.println("GetterGetterGetter.getIntGetter() field type = " + g.get(tt).getClass());
//		return ((Short) g.get(tt)).intValue();
//	};
//	break;
//}
//case("char"):
//	out = (T tt) -> 
//{
//	try { return f.getChar(tt);}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//}; break;
//case("Character"):
//{
//	ObjGetter<T> g = objectGetterGetter(t, f);
//	out = (T tt) -> { return (char) g.get(tt); };
//} break;
//out = (T tt) -> 
//		{
//			try { return AnnotatedBeanReader.parseBool(f.getInt(tt));}
//			catch (IllegalArgumentException | IllegalAccessException e) 
//			{ e.printStackTrace(); throw new IllegalArgumentException();}
//		}; 
//	break;



//case("Short"): out = (T tt) -> {return AnnotatedBeanReader.parseBool((int) getOffType(t, f, tt)); }; break;
////	out = (T tt) -> 
////{
////	try { return AnnotatedBeanReader.parseBool((Integer) f.get(tt));}
////	catch (IllegalArgumentException | IllegalAccessException e) 
////	{ e.printStackTrace(); throw new IllegalArgumentException();}
////};
////break;
////case("long"):
////	out = (T tt) -> 
////{
////	try { return AnnotatedBeanReader.parseBool((int) f.getLong(tt));}
////	catch (IllegalArgumentException | IllegalAccessException e) 
////	{ e.printStackTrace(); throw new IllegalArgumentException();}
////};
////break;
////case("Long"):
////{
////	ObjGetter<T> g = objectGetterGetter(t, f);
////	out = (T tt) -> { return AnnotatedBeanReader.parseBool((int) (long) g.get(tt)); };
////} 
////break;
//case("short"):
//	out = (T tt) ->
//{
//	try { return AnnotatedBeanReader.parseBool((int) f.getShort(tt));}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//};
//break;
//case("Short"):
//{
//	ObjGetter<T> g = objectGetterGetter(t, f);
//	out = (T tt) -> 
//	{ 
//		//				System.out.println("GetterGetterGetter.getIntGetter() field type = " + g.get(tt).getClass());
//		return AnnotatedBeanReader.parseBool(((Short) g.get(tt)).intValue());
//	};
//}
//break;

//case("int"):
//	out = (T tt) ->
//{
//	try { return f.getChar(tt);}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//}; break;
//case("Integer"):
//{
//	out = (T tt) -> { return (Character) getOffType(t, f, tt); };
//	//			ObjGetter<T> g = objectGetterGetter(t, f);
//	//			out = (T tt) -> { return (Character) g.get(tt); };
//	break;
//}


//case("char"):
//	out = (T tt) -> 
//{
//	try { return AnnotatedBeanReader.parseBool(f.getChar(tt));}
//	catch (IllegalArgumentException | IllegalAccessException e) 
//	{ e.printStackTrace(); throw new IllegalArgumentException();}
//}; break;
//case("Character"):
//{
//	ObjGetter<T> g = objectGetterGetter(t, f);
//	out = (T tt) -> { return AnnotatedBeanReader.parseBool((char) g.get(tt)); };
//} break;