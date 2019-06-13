package beans.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import utils.FieldUtils;

public class RandomBeanBuilder extends AnnotatedBeanReader 
{

	private static RandomStream r = new MRG31k3p();
	
	@Deprecated	public static <T> List<List<T>> randomFactory(Class<T> clazz, int nRow, int nCol, int min, int max) { return randomFactory(clazz, nRow, nCol, min, max, r); }
	@Deprecated public static <T> T randomFactory(Class<T> clazz, int min, int max) { return randomFactory(clazz, min, max, r); }
	@Deprecated public static <T> List<T> randomFactory(Class<T> clazz, int n, int min, int max) { return randomFactory(clazz, n, min, max, r); }
	@Deprecated	public static int     randInt(int min, int max) { return randInt(min, max, r); }
	@Deprecated public static double  randDouble(double min, double max) { return randDouble(min, max, r); }
	@Deprecated public static char    randChar(char min, char max) { return randChar(min, max, r); }
	@Deprecated	public static boolean randBool(double prob) { return randBool(prob, r); }
	@Deprecated	public static String randomString(String shortName, int min, int max) {	return randomString(shortName, min, max, r);}
	@Deprecated	public static String randomString(int nChars, char min, char max) { return randomString(nChars, min, max, r);}

	
	/** Build a bean instance with randomized values for the annotated fields. 
	 * 
	 * @param clazz bean class
	 * @param n number of beans to make
	 * @param <T> bean type
	 * @return list of beans
	 */
	/**
	 * 
	 * @param clazz
	 * @param nRow
	 * @param nCol
	 * @param min
	 * @param max
	 * @param rs
	 * @return
	 */
	public static <T> List<List<T>> randomFactory(Class<T> clazz, int nRow, int nCol, int min, int max, RandomStream rs)
	{
		List<List<T>> l = new ArrayList<>();
		for (int i = 0; i < nCol; i++) l.add(randomFactory(clazz, nRow, min, max, rs));
		return l;
	}
	
	
	/** Build a bean instance with randomized values for the annotated fields. 
	 * 
	 * @param clazz bean class
	 * @param n number of beans to make
	 * @param <T> bean type
	 * @return list of beans
	 */
	
	/**
	 * 
	 * @param clazz
	 * @param n
	 * @param min
	 * @param max
	 * @param rs
	 * @return
	 */
	public static <T> List<T> randomFactory(Class<T> clazz, int n, int min, int max, RandomStream rs)
	{
		List<T> l = new ArrayList<>();
		for (int i = 0; i < n; i++) l.add(randomFactory(clazz, min, max, rs));
		return l;
	}

	/** Build a list of bean instances with randomized values for the annotated fields. 
	 * 
	 * @param clazz bean class
	 * @param <T> bean type
	 * @return list of beans
	 */
	public static <T> T randomFactory(Class<T> clazz, int min, int max, RandomStream rs)
	{
		List<Field> ff = FieldUtils.getFields(clazz, ParsedField.class, true, true, true, false);
		T o = null;
		try 
		{
			o = clazz.newInstance();

			for (int i = 0; i < ff.size(); i++) 
			{
				Field f = ff.get(i);
				String shortName = f.getType().getSimpleName();
				String val = randomString(shortName, min, max, rs);
				setVal(f, val, o);
			}
		}
		catch (InstantiationException | IllegalAccessException e) 
		{e.printStackTrace();}
		return o;
	}

	/** Random int convenience generator. 
	 * 
	 * @param min min value
	 * @param max max value
	 * @return random value
	 */
	public static int     randInt(int min, int max, RandomStream rs) {return rs.nextInt(0, max - min) + min; }
	/** Random double convenience generator.
	 * 
	 * @param min min value
	 * @param max max value
	 * @return random double
	 */
	public static double  randDouble(double min, double max, RandomStream rs) { return (max - min) * rs.nextDouble() + min; }
	/** Random char convenience generator.  
	 * 
	 * @param min min char
	 * @param max highest char
	 * @return random char
	 */
	public static char    randChar(char min, char max, RandomStream rs) { return (char)(randInt(min, max, rs)); }
	/** Random bool convenience generator. 
	 * 
	 * @param prob probability of true
	 * @return true/false
	 */
	public static boolean randBool(double prob, RandomStream rs) { if (rs.nextDouble() < prob) return true; return false; }


	/** Random generator for primitive or boxed primitive types. 
	 * 
	 * @param shortName short name of type
	 * @return String representation of the random data. 
	 */
	public static String randomString(String shortName, int min, int max, RandomStream rs)
	{
		String val = "";
		switch (shortName.toLowerCase()) {
		case("int"):     val = String.format("%d", randInt(min, max, rs)); break;
		case("byte"):     val = String.format("%d", randInt(min, max, rs)); break;
		case("short"):     val = String.format("%d", randInt(min, max, rs)); break;
		case("long"):     val = String.format("%d", randInt(min, max, rs)); break;
		case("double"):  val = String.format("%f", randDouble(min, max)); break;
		case("float"):  val = String.format("%f", randDouble(min, max)); break;
		case("boolean"): val = Boolean.toString(randBool(0.5)); break;
		case("string"):	 val = randomString(rs.nextInt(0, 12) + 1, '9', 'Z'); break; 
		case("char"):	 val =  String.format("%s", randomString(rs.nextInt(0, 12) + 1, '9', 'Z').charAt(0)); break; 
		case("character"):	 val =  String.format("%s", randomString(rs.nextInt(0, 12) + 1, '9', 'Z').charAt(0)); break; 
		case("integer"): val = String.format("%d", randInt(min, max, rs)); break;

		default: throw new IllegalArgumentException("Input value for field of type " 
				+ shortName + " could not be parsed");
		}	
		return val;
	}

	/** Random String generator
	 *  
	 * @param nChars how long shoudl the string be?
	 * @param min what is the highest character?
	 * @param max what is the lowest character
	 * @param r random generator
	 * @return random string
	 */
	public static String randomString(int nChars, char min, char max, RandomStream rs)
	{
		String s = "";
		for (int i = 0; i < nChars; i++) s += (char)(rs.nextInt(0, max - min) + min);
		return s;
	}

}
