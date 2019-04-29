package beans.builder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.builder.AnnotatedBeanReader.ParsedField;
import fields.FieldUtils;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import utils.ArrayUtils;

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

	@SuppressWarnings("unchecked")
	public static <T> List<List<T>> factory2D(
			//			public static <T> T[][] factory2D(
			Class<T> clazz,
			String filename

			)
	{
		List<List<T>> out = null;
		//		T[][] out = null;
		int width, height;

		List<Field> ff = FieldUtils.getAnnotatedFields(clazz, ParsedField.class);
		List<String> fNames;
		List<String> varNames = new ArrayList<>();
		fNames = Arrays.asList(FieldUtils.getInstanceFieldNames(clazz));

		for (int i = 0; i < fNames.size(); i++) fNames.set(i, fNames.get(i).toLowerCase());

		NetcdfFile ncfile = null;
		try {ncfile = NetcdfFile.open(filename);
		} catch (IOException e) { e.printStackTrace(); }

		Map<String, Java2DArrayPackage> javaArrayMap = new HashMap<>();

		List<Variable> vars = ncfile.getVariables();
		boolean transpose = false;
		//		for (Variable v : vars)
		//			System.out.println("NetCDFObjBuilder.factory2D() read variable " + v.getFullName());
		for (Variable v : vars)
		{
			Java2DArrayPackage pack;
			String vType = v.getDataType().toString();
			String ncVarName = v.getFullName();
			String lowCName = v.getFullName().toLowerCase();

			System.out.println("NetCDFObjBuilder.factory2D() read "
					+ "variable " + v.getFullName() + " type " + vType);
			if (fNames.contains(lowCName))
			{
				varNames.add(ncVarName);
				switch(vType)
				{
				case("int"):
				{
					int[][] ii = get2DIntArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(
							ii, ii.length, ii[0].length, ncVarName, vType);
					javaArrayMap.put(lowCName, pack);
					break;
				}
				case("double"):
				{
					double[][] dd = get2DDoubleArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(
							dd, dd.length, dd[0].length, lowCName, vType);
					javaArrayMap.put(lowCName, pack);
					break;
				}
				case("char"):
				{
					char[][] cc = get2DCharArray(ncfile, ncVarName, transpose);
					pack = new Java2DArrayPackage(
							cc, cc.length, cc[0].length, lowCName, vType);
					javaArrayMap.put(lowCName, pack);
					break;
				}
				}
			}
		}

		for (String st : varNames)
		{
			if (!javaArrayMap.containsKey(st.toLowerCase()))
				throw new IllegalArgumentException("Trouble parsing netCDF variable: " + st);


			//			System.out.println("NetCDFObjBulder.factory2D() map has entry for var " + st + " " + javaArrayMap.containsKey(st));
		}
		if (javaArrayMap.size() == 0)
			throw new IllegalArgumentException("No variables found in input file " + filename);
		Java2DArrayPackage pack = javaArrayMap.get(javaArrayMap.keySet().iterator().next());
		width = pack.width; height = pack.height;

		//		out = (T[][]) new Object[width][height];
		//		out = (T[][]) new Object[width][height];
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
		return out;
	}


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
				System.out.println("NetCDFObjBuilder.buildObject() field name: " + name);
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
				}
			}
		}

		return t;
	}


	//	
	//	/* Giant try block is probably not an ideal coding style. */
	//	try {
	//
	//		boolean transpose = true;
	//
	//		/* Grab the variables from the file and verify that they are all the same size: */
	//		/* the NetCDF data is transposed from the orientation that we want. */
	//		double[][]  elevation      = NetCDFUtils.get2DDoubleArray(ncfile, "elevation", transpose);
	//		double[][]  slope          = NetCDFUtils.get2DDoubleArray(ncfile, "slope", transpose);
	//		double[][]  aspect         = NetCDFUtils.get2DDoubleArray(ncfile, "aspect", transpose);
	//		double[][]  dist_to_road   = NetCDFUtils.get2DDoubleArray(ncfile, "dist_to_road", transpose);
	//		double[][]  dist_to_stream = NetCDFUtils.get2DDoubleArray(ncfile, "dist_to_stream", transpose);
	//		int[][]     in_border      = NetCDFUtils.get2DIntArray(ncfile, "in_border", transpose);
	//		int[][]     has_road       = NetCDFUtils.get2DIntArray(ncfile, "has_road", transpose);
	//		int[][]     has_stream     = NetCDFUtils.get2DIntArray(ncfile, "has_stream", transpose);
	//
	//		height = elevation[0].length;
	//		width = elevation.length; 
	//		
	//		cellWidth_meters = ncfile.findGlobalAttribute("cellWidth").getNumericValue().doubleValue();
	//		cellGrid = new ManagedCell[width][height];
	//		
	//		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
	//		{
	//			if (in_border[x][y] != 1) cellGrid[x][y] = null;
	//			else
	//			{
	//				ManagedCell c = new ManagedCell();
	//				c.initialize(
	//						elevation[x][y], 
	//						slope[x][y], 
	//						aspect[x][y],
	//						Bool.parseBool(has_stream[x][y]), 
	//						dist_to_stream[x][y], 
	//						Bool.parseBool(has_road[x][y]),
	//						dist_to_road[x][y],
	//						-99999);
	//				c.setCoords(x, y);
	//				managedCells.add(c);
	//				cellGrid[x][y] = c;
	//				
	//				if (Bool.parseBool(in_border[x][y]) && Double.isNaN(elevation[x][y]))
	//					throw new IllegalArgumentException("Error in input NetCDF file. "
	//							+ " Cell at (" + x + ", " + y + ") has no elevation data.");
	//			}
	//		}
	//	} catch (IOException e) { e.printStackTrace(); }
	//	
	//	
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

	/** Get a 2D array of ints from a netCDF variable.
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

	/** Get a 2D array of ints from a netCDF variable.
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

	/** Get a 2D array of floats from a netCDF variable.
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
