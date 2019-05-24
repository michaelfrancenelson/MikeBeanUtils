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

		beans = NetCDFObjBuilder.factory2D(AllFlavorBean.class, filename, false);
		NetCDFObjBuilder.factory2D(AllFlavorStaticBean.class, filename, false);

		assertTrue(AnnotatedBeanInitializer.enforceStaticInitialized(
				AllFlavorStaticBean.class, true)); 

		for (List<AllFlavorBean> l1 : beans)
		{
			for (AllFlavorBean b : l1)
			{
				assertTrue(AnnotatedBeanInitializer.enforceInstanceInitialized(
						AllFlavorBean.class, b, true));  
			}
		}
	}

	@Test 
	public void allFlavorBeanTest()
	{
		String[] filenames = new String[] {
				"testData/allFlavorBeans.xlsx",
				"testData/allFlavorBeansTransposed.xlsx",
				"testData/allFlavorBeansTransposed.csv",
				"testData/allFlavorBeans.csv"
		};

		List<List<AllFlavorBean>> allBeans = new ArrayList<>();

		for (String st : filenames) 
		{
			AnnotatedBeanReader.factory(
					AllFlavorStaticBean.class, st, false, 1);

			assertTrue(AnnotatedBeanInitializer.checkStaticInitialized(
					AllFlavorStaticBean.class)); 
			allBeans.add(
					AnnotatedBeanReader.factory(
							AllFlavorBean.class, st, false, -1)
					);
		}

		for (int index = 0; index < allBeans.get(0).size(); index++)
		{
			AllFlavorBean bb = allBeans.get(0).get(index);
			for (int i = 0; i < allBeans.size(); i++)
			{
				assertTrue(AnnotatedBeanInitializer.checkInstanceInitialized(
						AllFlavorBean.class,  allBeans.get(i).get(index)));
				assertTrue(AnnotatedBeanReader.equals(
						AllFlavorBean.class, bb, allBeans.get(i).get(index)));
			}
		}
	}
}
