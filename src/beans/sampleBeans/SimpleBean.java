package beans.sampleBeans;

import beans.builder.AnnotatedBeanBuilder.FieldColumn;
import beans.builder.AnnotatedBeanBuilder.Initialized;
import beans.memberState.SimpleFieldWatcher.WatchField;

/** Test class for the bean reader/writers
 * 
 * @author michaelfrancenelson
 *
 */
@SuppressWarnings("unused")
public class SimpleBean
{
	@WatchField (name = "Static integer")
	@FieldColumn @Initialized public static int iSt = -12345;
	@FieldColumn @Initialized private static double dSt = 0.987654321;
	
	@WatchField (name = "Integer field i")
	@FieldColumn @Initialized private int i;
	@FieldColumn private int i2;
	@WatchField (name = "Double field d")
	@FieldColumn @Initialized private double d;
	@FieldColumn private double d2;
	@FieldColumn @Initialized private Integer ii;
	@FieldColumn @Initialized private Double dd;
	@FieldColumn @Initialized private char c;
	@WatchField (name = "String field s")
	@FieldColumn @Initialized private String s;
	@FieldColumn @Initialized private boolean b;
	@FieldColumn @Initialized private Boolean bb;
}
