package beans.builder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import beans.builder.AnnotatedBeanReader.ParsedField;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import utils.ArrayUtils;
import utils.FieldUtils;

public class NetCDFObjBuilder 
{

	static class Java2DArrayPackage
	{
		Object array;
		int width;
		int height;
		String varName;
		String type;
		public Java2DArrayPackage(Object array, int w, int h, String name, String type)
		{
			this.array = array; this.width = w; this.height = h; this.varName = name; this.type = type;
		}
	}

	/**
	 * Build a collection of beans from a NetCDF file. 
	 * Will attempt to read data corresponding to all the fields with the @ParsedField
	 *   annotation.
	 * @param clazz Bean class type
	 * @param filename data file
	 * @return
	 */
	public static <T> List<List<T>> factory2D(
			Class<T> clazz,
			Class<? extends Annotation> annClass,
			String filename,
			boolean transpose,
			boolean ignoreIncomplete
			)
	{
		List<Field> ff = FieldUtils.getFields(
				clazz, annClass, true, true, true, false);
		return factory2D(clazz, annClass, filename, ff, transpose, true, ignoreIncomplete);
	}


	/**
	 * Build a collection of beans from a NetCDF file. 
	 * Will attempt to read data corresponding to all the fields with the @ParsedField
	 *   annotation.
	 * @param clazz Bean class type
	 * @param filename data file
	 * @return
	 */
	public static <T> List<List<T>> factory2D(
			Class<T> clazz,
			String filename,
			boolean ignoreIncomplete
			)
	{
		List<Field> ff = FieldUtils.getFields(
				clazz, ParsedField.class, true, true, true, false);
		return factory2D(clazz, filename, ff, ignoreIncomplete);
	}


	public static <T> List<List<T>> factory2D(
			Class<T> clazz,
			String filename,
			List<Field> ff, 
			boolean ignoreIncomplete)
	{
		return factory2D(clazz, null, filename, ff, false, true, ignoreIncomplete);
	}


	public static <T> String getGlobalAttribute(String filename, String attName)
	{
		NetcdfFile ncfile = null;
		try { ncfile = NetcdfFile.open(filename); }
		catch (IOException e) { e.printStackTrace(); }

		List<Attribute> attList = ncfile.getGlobalAttributes();

		String attName2 = attName.trim().toLowerCase();
		
		for (Attribute att : attList)
		{
			String name = att.getFullName().trim().toLowerCase();
		
			if (attName2.equals(name))
				return att.getValue(0).toString();
		}
		throw new IllegalArgumentException(
				String.format("Unable to find global attribute"
						+ " '%s' in file '%s", attName, filename));
	}
	
	
	public static <T> void setStaticFromGlobalAttributes(NetcdfFile ncfile, Class<T> clazz)
	{
		List<Attribute> attList = ncfile.getGlobalAttributes();

		AnnotatedBeanReader.logger.trace("NetCDFObjBuilder setStaticFromGlobalAttribute() attempting to set static fields"
				+ "from netCDF global attributes for class " + clazz.getName());
		AnnotatedBeanReader.logger.trace("NetCDFObjBuilder setStaticFromGlobalAttribute() found " + attList.size() + " global attributes.");

		Map<String, Field> fMap = new HashMap<>();

		/* record the field names in all lowercase so that matches between fields
		 * and NCDF variables are case-insensitive. */

		List<Field> ff = FieldUtils.getFields(clazz, null, false, true, true, false);
		AnnotatedBeanReader.logger.trace(" found " + ff.size() + " static fields.");

		for (Field f : ff) fMap.put(f.getName().toLowerCase(), f);

		for (Attribute att : attList)
		{
			String attName = att.getFullName().toLowerCase();
			AnnotatedBeanReader.logger.trace(" atribute name: " + attName); 
			if (fMap.containsKey(attName))
			{
				Field f = fMap.get(attName);
				String val = att.getValue(0).toString(); 
				AnnotatedBeanReader.logger.trace("NetCDFObjBuilder setStaticFromGlobalAttribute()"
						+ " atribute name: " + att + " value: " + val);

				AnnotatedBeanReader.setVal(f, null, val, null);
			}
		}
	}

	/**
	 * Build a collection of beans from a NetCDF file. 
	 * Will attempt to read data corresponding to all the specified fields
	 * 
	 * @param clazz Bean class type
	 * @param filename data file
	 * @param ff the input file will be searched for variables corresponding to these
	 * @return
	 */
	public static <T> List<List<T>> factory2D(
			Class<T> clazz,
			Class<? extends Annotation> annClass,
			String filename,
			List<Field> ff, 
			boolean transpose,
			boolean attributesAsStaticfields,
			boolean ignoreIncomplete)
	{
		NetcdfFile ncfile = null; 
		List<List<T>> out = null;
		int width, height;
		List<String> varNamesLower = new ArrayList<>();
		List<String> varNames = new ArrayList<>();

		if (ff == null)
			ff = FieldUtils.getFields(clazz, annClass, true, true, true, true);

		/* record the field names in all lowercase so that matches between fields
		 * and NCDF variables are case-insensitive. */
		List<String> parsedFieldNames = FieldUtils.getFieldNames(ff, clazz, null, false);
		List<String> parsedFieldNamesLC = FieldUtils.getFieldNames(ff, clazz, null, true);
		for (int i = 0; i < parsedFieldNames.size(); i++) 
			parsedFieldNamesLC.set(i, parsedFieldNamesLC.get(i).toLowerCase());

		try { ncfile = NetcdfFile.open(filename); }
		catch (IOException e) { e.printStackTrace(); }

		Map<String, Java2DArrayPackage> javaArrayMap = new HashMap<>();

		List<Variable> vars = ncfile.getVariables();
		//		boolean transpose = false;
		for (Variable v : vars)
		{
			Java2DArrayPackage pack;
			String vType = v.getDataType().toString();
			String ncVarName = v.getFullName();
			String lowCName = ncVarName.toLowerCase();

			if (parsedFieldNamesLC.contains(lowCName))
			{
				varNamesLower.add(lowCName);
				varNames.add(ncVarName);
				switch(vType)
				{
				case("int"):
				{
					int[][] ii = get2DIntArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(ii, ii.length, ii[0].length, ncVarName, vType);
					javaArrayMap.put(lowCName, pack); break;
				}
				case("double"):
				{
					double[][] dd = get2DDoubleArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(dd, dd.length, dd[0].length, lowCName, vType);
					javaArrayMap.put(lowCName, pack); break;
				}
				case("char"):
				{
					char[][] cc = get2DCharArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(cc, cc.length, cc[0].length, lowCName, vType);
					javaArrayMap.put(lowCName, pack); break;
				}
				case("byte"):
				{
					byte[][] cc = get2DByteArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(cc, cc.length, cc[0].length, lowCName, vType);
					javaArrayMap.put(lowCName, pack); break;
				}
				}
			}
		}

		for (String st : varNames)
		{
			if (!javaArrayMap.containsKey(st.toLowerCase()))
				throw new IllegalArgumentException("Trouble parsing netCDF variable: " + st);
		}
		if (javaArrayMap.size() == 0)
			throw new IllegalArgumentException("No variables found in input file " + filename);

		/* Get the width and height of the data arrays and verify that they are all the same. */
		Iterator<String> it = javaArrayMap.keySet().iterator();
		String name = it.next();
		width = javaArrayMap.get(name).width; height = javaArrayMap.get(name).height;
		while(it.hasNext())
		{
			name = it.next();
			Java2DArrayPackage pack = javaArrayMap.get(name);
			if (width != pack.width)
				throw new IllegalArgumentException("NetCDF 2D data arrays are not all the same size");
			if (height != pack.height)
				throw new IllegalArgumentException("NetCDF 2D data arrays are not all the same size");
		}

		/* Check that all the required fields were found in the NCDF file. */
		List<String> nnnn = new ArrayList<>();
		for (String st : parsedFieldNames)
		{
			if (!ignoreIncomplete)
			{
				if (!javaArrayMap.containsKey(st.toLowerCase()))
					throw new IllegalArgumentException("Bean field " + st + " does not have a matching "
							+ "variable int he netCDF input file " + filename);
				nnnn.add(st.toLowerCase());
			}
			else
			{
				if (javaArrayMap.containsKey(st.toLowerCase()))
				{
					nnnn.add(st.toLowerCase());
				}
			}
		}

		parsedFieldNames = FieldUtils.getFieldNames(ff, clazz, null, false);
		parsedFieldNamesLC = FieldUtils.getFieldNames(ff, clazz, null, true);
		for (int i = 0; i < parsedFieldNames.size(); i++) 
			parsedFieldNamesLC.set(i, parsedFieldNamesLC.get(i).toLowerCase());

		List<Field> fff = new ArrayList<>();

		for (String st : nnnn)
		{
			fff.add(ff.get(parsedFieldNamesLC.indexOf(st)));
		}

		ff = fff;

		out = new ArrayList<List<T>>(width);
		for (int x = 0; x < width; x++)
		{
			List<T> l1 = new ArrayList<T>(height);
			for (int y = 0; y < height; y++)
			{
				T t = buildObject(javaArrayMap, x, y, clazz, ff);
				l1.add(t);
			}
			out.add(l1);
		}

		/* Read static field values from global attributes. */
		if (attributesAsStaticfields) setStaticFromGlobalAttributes(ncfile, clazz);

		return out;
	}

	/**
	 * Build a single bean instance from coordinates within a netCDF file
	 * 
	 * @param packs data arrays from a netCDF file
	 * @param x array coordinate
	 * @param y array coordinate
	 * @param clazz bean class type
	 * @param ff list of fields to set in the bean
	 * @return A new instance of T with data from the netCDF file.
	 */
	static <T> T buildObject(
			Map<String, Java2DArrayPackage> packs,
			int x, int y, Class<T> clazz, List<Field> ff)
	{
		T t = null;
		try {
			t = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (t != null)
		{
			Java2DArrayPackage pack;
			for (int i = 0; i < ff.size(); i++) {
				Field f = ff.get(i);
				String name = f.getName();
				pack = packs.get(name.toLowerCase());
				switch(pack.type)
				{
				case("int"):
				{
					AnnotatedBeanReader.setVal(f, ((int[][]) pack.array)[x][y], t);
					break;
				}
				case("double"):
				{
					AnnotatedBeanReader.setVal(f, ((double[][]) pack.array)[x][y], t);
					break;
				}
				case("char"):
				{
					AnnotatedBeanReader.setVal(f, ((char[][]) pack.array)[x][y], t);
					break;
				}
				case("byte"):
				{
					AnnotatedBeanReader.setVal(f, ((byte[][]) pack.array)[x][y], t);
					break;
				}
				default: throw new IllegalArgumentException("Trouble parsing data of type "
						+ pack.type + " for field " + name);
				}
			}
		}
		return t;
	}

	/** Get a 2D array of bytes from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static byte[][] get2DByteArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		byte[][] out = null;
		try {
			out = (byte[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose) out = ArrayUtils.transpose(out);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
		return out;
	}

	/** Get a 2D array of ints from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static int[][] get2DIntArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		int[][] out = null;
		try {
			out = (int[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose) out = ArrayUtils.transpose(out);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
		return out;
	}

	/** Get a 2D array of shorts from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static short[][] get2DShortArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		short[][] out = null;
		try {
			out = (short[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose) out = ArrayUtils.transpose(out);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
		return out;
	}

	/** Get a 2D array of longs from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static long[][] get2DLongArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		long[][] out = null;
		try {
			out = (long[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose) out = ArrayUtils.transpose(out);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
		return out;
	}

	/** Get a 2D array of doubles from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static double[][] get2DDoubleArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		double[][] out = null;
		try {
			out = (double[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose) out = ArrayUtils.transpose(out);
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
		return out; 
	}

	/** Get a 2D array of floats from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static float[][] get2DFloatArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		try {
			float[][] out = (float[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose)
				return ArrayUtils.transpose(out);
			else return out; 
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
	}

	/** Get a 2D array of chars from a netCDF variable.
	 * 
	 * @param ncfile
	 * @param variable
	 * @param transpose
	 * @return
	 */
	public static char[][] get2DCharArray(NetcdfFile ncfile, String variable, boolean transpose)
	{
		try {
			char[][] out = (char[][]) ncfile.findVariable (variable).read().copyToNDJavaArray();
			if (transpose)
				return ArrayUtils.transpose(out);
			else return out; 
		} catch (IOException e) {
			throw new IllegalArgumentException("Problem finding variable " + variable + " in the netcdf file.");
		}
	}
}
