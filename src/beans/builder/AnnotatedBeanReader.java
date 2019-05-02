package beans.builder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.CSVHelper;
import io.XLSXHelper;
import utils.FieldUtils;

public class AnnotatedBeanReader 
{
	//	static Logger logger = LoggerFactory.getLogger(AnnotatedBeanBuilder.class);

	/**
	 * Marker to show which fields to read and/or reported
	 * 
	 * @author michaelfrancenelson
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ParsedField {};

	/**
	 * Marker for fields that are checked for initialized status
	 * 
	 * @author michaelfrancenelson
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface InitializedField {};


	/**
	 * Build list of beans from an input csv or xlsx.
	 * 
	 * @param clazz    class of bean
	 * @param filename name of input file
	 * @param ignoreIncomplete should rows or columns with incomplete
	 *                           data be ignored?  If no, incomplete
	 *                           data results in an exception.
	 * @param          <T> bean type
	 * @return list of bean objects
	 */
	public static <T> List<T> factory(
			Class<T> clazz,
			String filename, 
			boolean ignoreIncomplete,
			int maxObjects) 
	{
		List<List<String>> data = getData(filename);
		return factory(clazz, data, ignoreIncomplete, filename, maxObjects);
	}

	/**
	 * Create annotated bean instances
	 * 
	 * @param clazz class of bean
	 * @param       <T> bean type
	 * @param data  data for the beans, the first row must contain the headers.
	 * @param ignoreIncomplete should rows or columns with incomplete
	 *                           data be ignored?  If no, incomplete
	 *                           data results in an exception.
	 * @param filename name of input file.  If 
	 *   
	 * @return list of bean objects
	 */
	public static <T> List<T> factory(
			Class<T> clazz,
			List<List<String>> data,
			boolean ignoreIncomplete,
			String filename,
			int maxObjects)
	{
		boolean dataInRows = checkFileDataOrientation(clazz, data, filename);

		/* If data is in columns, transpose for parsing */
		if (!dataInRows) data = CSVHelper.transpose(data, ignoreIncomplete);
		int nObjects = (data.size() - 1);

		if (maxObjects > 0)
		{
			if (nObjects > maxObjects)
				nObjects = maxObjects;
		}


		List<Field> ff = FieldUtils.getFields(clazz, ParsedField.class, true, true);
		List<String> headers = data.get(0);
		List<Integer> colNumbers = new ArrayList<>();
		for (Field f : ff)
		{
			colNumbers.add(headers.indexOf(f.getName().trim()));
		}

		/* Get the column numbers: */


		List<String> row;
		List<T> out = new ArrayList<>();
		T o = null;
		try {
			for (int r = 1; r <= nObjects; r++) {
				try {
					row = data.get(r);
					o = clazz.newInstance();
					for (int i = 0; i < ff.size(); i++) {
						Field f = ff.get(i);
						String val = row.get(colNumbers.get(i));
						setVal(f, val, o);
					}
				} catch (NumberFormatException e) {
				} catch (IndexOutOfBoundsException e) { // In case of an incomplete row of data
				} catch (IllegalArgumentException e) {
				}
				out.add(o);
			}
		} catch (InstantiationException | IllegalAccessException e) {
		}
		return out;
	}

	/**
	 * Parse an integer to a boolean value, in the style of R
	 * 
	 * @param i if i is greater than zero returns true, false otherwise
	 * @return i if i is greater than zero returns true, false otherwise
	 */
	public static boolean parseBool(int i) 
	{
		if (i > 0) return true;
		return false;
	}

	/**
	 * Parse an integer to a boolean value, in the style of R
	 * 
	 * @param i if i is greater than zero returns true, false otherwise
	 * @return i if i is greater than zero returns true, false otherwise
	 */
	public static boolean parseBool(double d) 
	{
		if (d > 0) return true;
		return false;
	}

	/**
	 * Parse a string to a boolean value
	 * 
	 * @param s string to parse
	 * @return matches {"true", "t", "1", and any numeric value greater than 0} to true and 
	 *                 {"false", "f", "0", or any numeric value less than 0} to false.  
	 */
	public static boolean parseBool(String s) {
		String ss = s.trim();
		if (ss.equalsIgnoreCase("true"))
			return true;
		if (ss.equalsIgnoreCase("false"))
			return false;
		if (ss.equalsIgnoreCase("t"))
			return true;
		if (ss.equalsIgnoreCase("f"))
			return false;
		if (ss.equalsIgnoreCase("1"))
			return true;
		if (ss.equalsIgnoreCase("0"))
			return false;
		try 
		{
			double d = Double.parseDouble(s);
			if (d > 0) return true;
			else return false;
		}
		catch(Exception e) { return false; }

//		throw new IllegalArgumentException("Input: " + s + " "
//				+ "could not be parsed to a boolean value");
	}

	/**
	 * 
	 * @param filename data file
	 * @return data rows
	 */
	protected static List<List<String>> getData(String filename) {
		if (filename.endsWith("xlsx"))
			return XLSXHelper.readXLSX(filename);
		else if (filename.endsWith(".csv"))
			return CSVHelper.readFile(filename);
		throw new IllegalArgumentException(
				"Input file " + filename + " does not appear " + 
				"to be in either the XLSX or CSV format.");
	}

	/**
	 * Simple check that input data contains entries for all the annotated fields.
	 * Throws an exception if all the field names from clazz are not found in either
	 * the first row or column of the data.
	 * 
	 * @param clazz class of bean
	 * @param data  input data rows
	 * @param       <T> bean type
	 * @param filename data file.  Can be null.
	 * @return true = data oriented in rows, 
	 *         false = data oriented in columns
	 *         returns a (hopefully) helpful exception if it cannot 
	 *         be determined or if file is incomplete.
	 */
	protected static <T> boolean checkFileDataOrientation(
			Class<T> clazz, 
			List<List<String>> data,
			String filename)
	{
		if (filename == null) filename = "input data list";

		/* Fields without the annotation are ignored. */
		List<Field> ff = FieldUtils.getFields(clazz, ParsedField.class, true, true);

		List<String> fieldNames = new ArrayList<>();
		for (Field f : ff) fieldNames.add(f.getName());

		List<String> absentInFirstRow = new ArrayList<>();
		List<String> absentInFirstCol = new ArrayList<>();

		List<String> presentInFirstRow = new ArrayList<>();
		List<String> presentInFirstCol = new ArrayList<>();

		/* Test row orientation by determining whether all the field names
		 * have corresponding entries in the first row (outer list element) */
		List<String> firstRow = data.get(0);
		for (String f : fieldNames) 
		{
			if (firstRow.contains(f)) presentInFirstRow.add(f);
			else if (!firstRow.contains(f)) absentInFirstRow.add(f);
		}

		/* Test whether the first column of data (the first elements
		 * of each inner list) contains all the field names. */
		List<String> firstCol = new ArrayList<>();
		for (List<String> l : data) { if (l.size() > 0) 
			firstCol.add(l.get(0)); }

		for (String f : fieldNames) 
		{
			if (firstCol.contains(f)) presentInFirstCol.add(f);
			else if (!firstCol.contains(f)) absentInFirstCol.add(f);
		}

		if ((presentInFirstCol.size() == fieldNames.size())) return false;
		if ((presentInFirstRow.size() == fieldNames.size())) return true;

		/* Otherwise there was a parsing issue: */

		String message1 = "Problem parsing input (" + filename + ")";
		String message2 = "Data appears to be oriented in "; 
		String message3 = " but headings are missing for the following fields: "; 
		String message4 = "";

		if (presentInFirstRow.size() >= presentInFirstCol.size())
		{
			message2 += "rows,";
			message4 = AnnotatedBeanReporter.concat(absentInFirstRow, ", ");
			throw new IllegalArgumentException(
					message1 + message2 + message3 + message4);
		}

		else if (presentInFirstRow.size() < presentInFirstCol.size())
		{
			message2 += "columns, ";
			message4 = AnnotatedBeanReporter.concat(absentInFirstCol, " ,");
			throw new IllegalArgumentException(
					message1 + message2 + message3 + message4);
		}

		else message1 += " Could not determine orientation of data"
				+ " in input (" + filename + ")";
		throw new IllegalArgumentException(message1);
	}

	/**
	 * Set the value of the field to the (appropriately casted) value.
	 * 
	 * @param f   annotated field
	 * @param val value to set
	 * @param o   bean
	 * @param     <T> bean type
	 */
	public static <T> void setVal(Field f, String val, T o) 
	{

		if (f.isAnnotationPresent(ParsedField.class)) {
			String shortName = f.getType().getSimpleName();
			try {
				switch (shortName) {

				case ("int"): f.setInt(o, Integer.parseInt(val)); break;
				case ("short"):	f.setShort(o, Short.parseShort(val)); break;
				case ("long"): f.setLong(o, Long.parseLong(val)); break;
				case ("byte"): f.setByte(o, Byte.parseByte(val)); break;
				case ("double"): f.setDouble(o, Double.parseDouble(val)); break;
				case ("float"): f.setFloat(o, Float.parseFloat(val)); break;
				case ("boolean"): f.setBoolean(o, parseBool(val)); break;
				case ("char"): f.setChar(o, val.charAt(0));	break;

				case ("String"): f.set(o, val); break;

				case ("Integer"): f.set(o, (Integer) Integer.parseInt(val)); break;
				case ("Short"): f.set(o, (Short) Short.parseShort(val)); break;
				case ("Long"): f.set(o, (Long) Long.parseLong(val)); break;
				case ("Byte"): f.set(o, (Byte) Byte.parseByte(val)); break;
				case ("Double"): f.set(o, (Double) Double.parseDouble(val)); break;
				case ("Float"): f.set(o, (Float) Float.parseFloat(val)); break;
				case ("Boolean"): f.set(o, (Boolean) parseBool(val)); break;
				case ("Character"): f.set(o, new Character(val.charAt(0))); break;
				default:
					throw new IllegalArgumentException(
							"Input value for field of type " + shortName + " could not be parsed");
				}
				//				logger.trace("Field " + f.getName() + "(" + shortName + ")" + " set to " + val + ".");
			} catch (
					NumberFormatException |	IllegalAccessException e) {
				throw new IllegalArgumentException("Could not parse the input " + val +
						"as a " + shortName + " value.");
			} 
		}
	}
	
	/**
	 * Set the value of the field to the (appropriately casted) value.
	 * 
	 * @param f   annotated field
	 * @param val value to set
	 * @param o   bean
	 * @param     <T> bean type
	 */
	public static <T> void setVal(Field f, int val, T o) 
	{

		if (f.isAnnotationPresent(ParsedField.class)) {
			String shortName = f.getType().getSimpleName();
			try {
				switch (shortName) {

				case ("int"): f.setInt(o, val); break;
				case ("short"):	f.setShort(o, (short) val); break;
				case ("long"): f.setLong(o, (long) val); break;
				case ("byte"): f.setByte(o, (byte) val); break;
				case ("double"): f.setDouble(o, (double) val); break;
				case ("float"): f.setFloat(o, (float) val); break;
				case ("boolean"): f.setBoolean(o, parseBool(val)); break;
				case ("char"): f.setChar(o, (char) val);	break;

				case ("String"): f.set(o, String.format("%d", val)); break;

				case ("Integer"): f.set(o, (Integer) val); break;
				case ("Short"): f.set(o, (Short) (short) val); break;
				case ("Long"): f.set(o, (Long) (long) val); break;
				case ("Byte"): f.set(o, (Byte) (byte) val); break;
				case ("Double"): f.set(o, (Double) (double) val); break;
				case ("Float"): f.set(o, (Float) (float) val); break;
				case ("Boolean"): f.set(o, (Boolean) parseBool(val)); break;
				case ("Character"): f.set(o, new Character((char) val)); break;
				default:
					throw new IllegalArgumentException(
							"Input value for field of type " + shortName + " could not be parsed");
				}
				//				logger.trace("Field " + f.getName() + "(" + shortName + ")" + " set to " + val + ".");
			} catch (
					NumberFormatException |	IllegalAccessException e) {
				throw new IllegalArgumentException("Could not parse the input " + val +
						"as a " + shortName + " value.");
			} 
		}
	}
	
	/**
	 * Set the value of the field to the (appropriately casted) value.
	 * 
	 * @param f   annotated field
	 * @param val value to set
	 * @param o   bean
	 * @param     <T> bean type
	 */
	public static <T> void setVal(Field f, double val, T o) 
	{

		if (f.isAnnotationPresent(ParsedField.class)) {
			String shortName = f.getType().getSimpleName();
			try {
				switch (shortName) {

				case ("int"): f.setInt(o, (int)val); break;
				case ("short"):	f.setShort(o, (short) val); break;
				case ("long"): f.setLong(o, (long) val); break;
				case ("byte"): f.setByte(o, (byte) val); break;
				case ("double"): f.setDouble(o, (double) val); break;
				case ("float"): f.setFloat(o, (float) val); break;
				case ("boolean"): f.setBoolean(o, parseBool(val)); break;
				case ("char"): f.setChar(o, (char) val);	break;

				case ("String"): f.set(o, val); break;

				case ("Integer"): f.set(o, (Integer) (int) val); break;
				case ("Short"): f.set(o, (Short) (short) val); break;
				case ("Long"): f.set(o, (Long) (long) val); break;
				case ("Byte"): f.set(o, (Byte) (byte) val); break;
				case ("Double"): f.set(o, (Double) (double) val); break;
				case ("Float"): f.set(o, (Float) (float) val); break;
				case ("Boolean"): f.set(o, (Boolean) parseBool(val)); break;
				case ("Character"): f.set(o, new Character((char) val)); break;
				default:
					throw new IllegalArgumentException(
							"Input value for field of type " + shortName + " could not be parsed");
				}
				//				logger.trace("Field " + f.getName() + "(" + shortName + ")" + " set to " + val + ".");
			} catch (
					NumberFormatException |	IllegalAccessException e) {
				throw new IllegalArgumentException("Could not parse the input " + val +
						"as a " + shortName + " value.");
			} 
		}
	}

	/**
	 * Test that all the annotated fields of two beans are the same.
	 * 
	 * @param clazz class of beans
	 * @param t1    bean 1
	 * @param t2    bean 1
	 * @param       <T> type of bean
	 * @return are they equal?
	 */
	public static <T> boolean equals(Class<T> clazz, T t1, T t2) {
		AnnotatedBeanReporter<T> rep = AnnotatedBeanReporter.factory(clazz, "%.4f", ",");
		List<String> r1 = rep.stringValReport(t1);
		List<String> r2 = rep.stringValReport(t2);
		if (r1.size() != r2.size())
			return false;
		for (int i = 0; i < r1.size(); i++)
			if (!(r1.get(i).equals(r2.get(i)))) {
				return false;
			}
		return true;
	}
}


///**
//* Build list of beans from an input csv or xlsx file with individual bean data
//* arranged in rows. Incomplete rows/columns will result in an error
//* 
//* @param clazz    class of bean
//* @param filename name of input file
//* @param          <T> bean type
//* @return list of bean objects
//*/
//public static <T> List<T> factory(
//		Class<T> clazz,
//		String filename) 
//{
//	return factory(clazz, filename, false);
//}

//
///**
// * Create annotated bean instances, don't check for incomplete data.
// * 
// * @param clazz class of bean
// * @param       <T> bean type
// * @param data  data for the beans, the first row must contain the headers.
// * @return list of bean objects
// */
//public static <T> List<T> factory(
//		Class<T> clazz,
//		List<List<String>> data, 
//		boolean transposed) 
//{
//	return factory(clazz, data, transposed, false);
//}