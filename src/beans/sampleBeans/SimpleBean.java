//package beans.sampleBeans;
//
//import beans.builder.AnnotatedBeanReader.ParsedField;
//import beans.builder.AnnotatedBeanReader.InitializedField;
//import beans.memberState.SimpleFieldWatcher.WatchField;
//
///** Test class for the bean reader/writers
// * 
// * @author michaelfrancenelson
// *
// */
//public class SimpleBean
//{
//	@WatchField (name = "Static int")
//	@ParsedField @InitializedField public static int iSt = -12345;
//	@WatchField (name = "Static final int")
//	@ParsedField @InitializedField public static final int iStFinal = 666;
//	
//	@WatchField (name = "Static double")
//	@ParsedField @InitializedField private static double dSt = 0.987654321;
//	@WatchField (name = "Static final double")
//	@ParsedField @InitializedField private static final double dStFinal = 666.666;
//	
//	
//	@WatchField (name = "int field i")
//	@ParsedField @InitializedField private int i;
//	@WatchField (name = "int field i2")
//	@ParsedField              private int i2;
//	@WatchField (name = "Integer field i")
//	@ParsedField @InitializedField private Integer ii;
//
//	@WatchField (name = "double field d")
//	@ParsedField @InitializedField private double d;
//
//	@WatchField (name = "double field d2")
//	@ParsedField              private double d2;
//	
//	@WatchField (name = "Double field dd")
//	@ParsedField @InitializedField private Double dd;
//
//	@WatchField (name = "char field c")
//	@ParsedField @InitializedField private char c;
//	
//	@WatchField (name = "String field s")
//	@ParsedField @InitializedField private String s;
//	
//	@WatchField (name = "boolean field b")
//	@ParsedField @InitializedField private boolean b;
//	@WatchField (name = "Boolean field bb")
//	@ParsedField @InitializedField private Boolean bb;
//	
//	@WatchField (name = "byte field bt")
//	@ParsedField @InitializedField private byte bt;
//	@WatchField (name = "Byte field btt")
//	@ParsedField @InitializedField private Byte btt;
//	
//	
//}
