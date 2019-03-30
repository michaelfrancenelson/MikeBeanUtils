package beans.sampleBeans;

import beans.builder.AnnotatedBeanBuilder.FieldColumn;
import beans.builder.AnnotatedBeanBuilder.Initialized;

/** For testing reading/reporting of static fields
 * 
 * @author michaelfrancenelson
 *
 */
public class SimpleStaticBean {
	@FieldColumn @Initialized public static int i1;
	@FieldColumn @Initialized public static int i2;
	@FieldColumn @Initialized public static double d1;
	@FieldColumn @Initialized public static double d2;
}
