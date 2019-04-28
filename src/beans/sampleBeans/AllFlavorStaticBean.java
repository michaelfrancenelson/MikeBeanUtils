package beans.sampleBeans;

import beans.builder.AnnotatedBeanReader.InitializedField;
import beans.builder.AnnotatedBeanReader.ParsedField;

public class AllFlavorStaticBean
{

	@ParsedField @InitializedField private static int     intPrim;
	@ParsedField @InitializedField private static short   shortPrim;
	@ParsedField @InitializedField private static long    longPrim;
	@ParsedField @InitializedField private static byte    bytePrim;
	@ParsedField @InitializedField private static double  doublePrim;
	@ParsedField @InitializedField private static float   floatPrim;
	@ParsedField @InitializedField private static boolean boolPrim;
	@ParsedField @InitializedField private static char    charPrim;
	
	@ParsedField @InitializedField private static Integer   intBox;
	@ParsedField @InitializedField private static Short     shortBox;
	@ParsedField @InitializedField private static Long      longBox;
	@ParsedField @InitializedField private static Byte      byteBox;
	@ParsedField @InitializedField private static Double    doubleBox;
	@ParsedField @InitializedField private static Float     floatBox;
	@ParsedField @InitializedField private static Boolean   boolBox;
	@ParsedField @InitializedField private static Character charBox;
	
	@ParsedField @InitializedField private static String strng;
}
