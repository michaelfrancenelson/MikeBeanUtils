package watchers;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import beans.sampleBeans.AllFlavorBean;

public class WatchersTest {


	
	@Test
	public void test() 
	{
		String filename = "testData/AllFlavorBean.nc";
		List<List<AllFlavorBean>> beans;

		beans = NetCDFObjBuilder.factory2D(AllFlavorBean.class, filename);
		Map<String, FieldWatcher<AllFlavorBean>> watcherMap = 
				SimpleFieldWatcher.getWatcherMap(
						AllFlavorBean.class, 
						ParsedField.class,
						"%.2f", 
						true, true);
		
		
		watcherMap.get("boolPrim").getBoolVal(beans.get(0).get(0));
		
		
		
		
		
		
		
		fail("Not yet implemented");
	}

}
