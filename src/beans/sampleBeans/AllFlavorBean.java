package beans.sampleBeans;

import java.util.Random;

import beans.builder.AnnotatedBeanReader.InitializedField;
import beans.builder.AnnotatedBeanReader.ParsedField;

public class AllFlavorBean
{

	Random r;
	int notParsed;
	
	@ParsedField @InitializedField private int     intPrim;
	@ParsedField @InitializedField private short   shortPrim;
	@ParsedField @InitializedField private long    longPrim;
	@ParsedField @InitializedField private byte    bytePrim;
	@ParsedField @InitializedField private double  doublePrim;
	@ParsedField @InitializedField private float   floatPrim;
	@ParsedField @InitializedField private boolean boolPrim;
	@ParsedField @InitializedField private char    charPrim;
	
	@ParsedField @InitializedField private Integer   intBox;
	@ParsedField @InitializedField private Short     shortBox;
	@ParsedField @InitializedField private Long      longBox;
	@ParsedField @InitializedField private Byte      byteBox;
	@ParsedField @InitializedField private Double    doubleBox;
	@ParsedField @InitializedField private Float     floatBox;
	@ParsedField @InitializedField private Boolean   boolBox;
	@ParsedField @InitializedField private Character charBox;
	
	@ParsedField @InitializedField private String strng;
}
