package beans.sampleBeans;

import java.util.Random;

import beans.builder.AnnotatedBeanReader.InitializedField;
import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.memberState.SimpleFieldWatcher.DisplayName;

public class AllFlavorBean
{

	Random r;
	int notParsed;
	
	@ParsedField @InitializedField @DisplayName(name = "Primitive int member") private int     intPrim;
	@ParsedField @InitializedField @DisplayName(name = "Primitive short member") private short   shortPrim;
	@ParsedField @InitializedField private long    longPrim;
	@ParsedField @InitializedField private byte    bytePrim;
	@ParsedField @InitializedField private double  doublePrim;
	@ParsedField @InitializedField @DisplayName(name = "Primitive float member") private float   floatPrim;
	@ParsedField @InitializedField private boolean boolPrim;
	@ParsedField @InitializedField private char    charPrim;
	
	@ParsedField @InitializedField @DisplayName(name = "Autoboxed int member") private Integer   intBox;
	@ParsedField @InitializedField @DisplayName(name = "Autoboxed short member")private Short     shortBox;
	@ParsedField @InitializedField private Long      longBox;
	@ParsedField @InitializedField private Byte      byteBox;
	@ParsedField @InitializedField private Double    doubleBox;
	@ParsedField @InitializedField @DisplayName(name = "Autoboxed float member") private Float     floatBox;
	@ParsedField @InitializedField private Boolean   boolBox;
	@ParsedField @InitializedField private Character charBox;
	
	@ParsedField @InitializedField private String strng;
}
