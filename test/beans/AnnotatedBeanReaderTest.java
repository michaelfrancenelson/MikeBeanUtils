package beans;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import beans.builder.AnnotatedBeanInitializer;
import beans.builder.AnnotatedBeanReader;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.AllFlavorBean;
import beans.sampleBeans.AllFlavorStaticBean;

public class AnnotatedBeanReaderTest
{

	@Test
	public void allFlavorNCDFTest()
	{
		List<List<AllFlavorBean>> beans;

		String filename = "testData/AllFlavorBean.nc";

		beans = NetCDFObjBuilder.factory2D(AllFlavorBean.class, filename);

		for (List<AllFlavorBean> l1 : beans)
		{
			for (AllFlavorBean b : l1)
			{
				boolean tf = AnnotatedBeanInitializer.enforceInstanceInitialized(
						AllFlavorBean.class, b);
//				if (!tf) 
//				{ 
//					int a = 2; 
//				 tf = AnnotatedBeanInitializer.enforceInstanceInitialized(
//							AllFlavorBean.class, b);
//				}
				assertTrue(tf);  
			}
		}


	}

	////	@Test 
	//	public void allFlavorBeanTest()
	//	{
	//		String[] filenames = new String[] {
	//				"testData/allFlavorBeans.xlsx",
	//				"testData/allFlavorBeansTransposed.xlsx",
	//				"testData/allFlavorBeansTransposed.csv",
	//				"testData/allFlavorBeans.csv"
	//		};
	//
	//		List<List<AllFlavorBean>> allBeans = new ArrayList<>();
	//
	//		for (String st : filenames) 
	//		{
	//			AnnotatedBeanReader.factory(
	//					AllFlavorStaticBean.class, st, false, 1);
	//			System.out.println("AnnotatedBeanReaderTest: " + st);
	//
	//			assertTrue(AnnotatedBeanInitializer.checkStaticInitialized(
	//					AllFlavorStaticBean.class)); 
	//			allBeans.add(
	//					AnnotatedBeanReader.factory(
	//							AllFlavorBean.class, st, false, -1)
	//					);
	//		}
	//
	//		for (int index = 0; index < allBeans.get(0).size(); index++)
	//		{
	//			AllFlavorBean bb = allBeans.get(0).get(index);
	//			for (int i = 0; i < allBeans.size(); i++)
	//			{
	//				assertTrue(AnnotatedBeanInitializer.checkInstanceInitialized(
	//						AllFlavorBean.class,  allBeans.get(i).get(index)));
	//				assertTrue(AnnotatedBeanReader.equals(
	//						AllFlavorBean.class, bb, allBeans.get(i).get(index)));
	//			}
	//		}
	//	}

	//
	////	@Test
	//	public void testInitilize()
	//	{
	//		String fileXLSXT = "testData/TestBeanFactoryTransposed.xlsx";
	//		List<SimpleBean> lxT =	AnnotatedBeanReader.factory(SimpleBean.class, fileXLSXT, false);
	//		for (SimpleBean t : lxT)
	//		{
	//			assertTrue(AnnotatedBeanInitializer.checkInstanceInitialized(SimpleBean.class, t));
	//			AnnotatedBeanInitializer.initializeInstanceFieldsToNA(SimpleBean.class, t);
	//			assertFalse(AnnotatedBeanInitializer.checkInstanceInitialized(SimpleBean.class, t));
	//		}
	//
	//		String fileC = "testData/TestStaticFieldBean.csv";
	//		AnnotatedBeanReader.factory(SimpleStaticBean.class, fileC, false);
	//		/* Should be true since all the initialized fields are also @FieldColumn s. */
	//		assertTrue(AnnotatedBeanInitializer.checkStaticInitialized(SimpleStaticBean.class));
	//
	//		AnnotatedBeanInitializer.initializeStaticFieldsToNA(SimpleParamBean.class);
	//		assertFalse(AnnotatedBeanInitializer.checkStaticInitialized(SimpleParamBean.class));
	//
	//
	//		String filename = "testData/params.xlsx";
	//		AnnotatedBeanReader.factory(SimpleParamBean.class, filename, true);
	//		/* Should be false because the class has initialized params that aren't read from the file. 
	//		 * Some of the @Initialized are not @FieldColumn */
	//		assertFalse(AnnotatedBeanInitializer.checkStaticInitialized(SimpleParamBean.class));
	//
	//		SimpleParamBean.setFromFile(filename);
	//		assertTrue(AnnotatedBeanInitializer.checkStaticInitialized(SimpleParamBean.class));
	//	}
}
