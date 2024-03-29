package com.github.michaelfrancenelson.mikebeansutils.watchers;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.michaelfrancenelson.mikebeansutils.beans.builder.NetCDFObjBuilder;
import com.github.michaelfrancenelson.mikebeansutils.beans.builder.RandomBeanBuilder;
import com.github.michaelfrancenelson.mikebeansutils.beans.builder.AnnotatedBeanReader.ParsedField;
import com.github.michaelfrancenelson.mikebeansutils.beans.memberState.FieldWatcher;
import com.github.michaelfrancenelson.mikebeansutils.beans.memberState.SimpleFieldWatcher;
import com.github.michaelfrancenelson.mikebeansutils.beans.sampleBeans.AllFlavorBean;
import com.github.michaelfrancenelson.mikebeansutils.sampling.Sampling;

import umontreal.ssj.rng.RandomStream;

public class WatchersTest {

	static RandomStream rs = Sampling.getDefaultRs();
	
	@Test
	public void testInputs() 
	{
		String filename = "testData/AllFlavorBean.nc";
		List<List<AllFlavorBean>> beans;

		beans = NetCDFObjBuilder.factory2D(AllFlavorBean.class, filename, false);
		Map<String, FieldWatcher<AllFlavorBean>> watcherMap = 
				SimpleFieldWatcher.getWatcherMap(
						AllFlavorBean.class, 
						ParsedField.class,
						"%.2f", 
						true, true);
		
		AllFlavorBean[][] bArray = new AllFlavorBean[20][20];
		for (int i = 0; i < bArray.length; i++) for (int j = 0; j < bArray[0].length; j++) {
			bArray[i][j] = RandomBeanBuilder.randomFactory(AllFlavorBean.class, -100, 100, rs);
		}
		
		/* Passes if no exceptions are thrown. */		
		for (String st : watcherMap.keySet())
		{
			watcherMap.get(st).getStringVal(beans.get(0).get(0));
			watcherMap.get(st).getDoubleVal(beans.get(0).get(0));
			watcherMap.get(st).getCharVal(beans.get(0).get(0));
			watcherMap.get(st).getIntVal(beans.get(0).get(0));
			watcherMap.get(st).getByteVal(beans.get(0).get(0));
			watcherMap.get(st).getBoolVal(beans.get(0).get(0));
			watcherMap.get(st).getParsedBoolVal(beans.get(0).get(0));
			
			watcherMap.get(st).getStringVal(bArray[0][1]);
			watcherMap.get(st).getDoubleVal(bArray[0][1]);
			watcherMap.get(st).getCharVal(bArray[0][1]);
			watcherMap.get(st).getIntVal(bArray[0][1]);
			watcherMap.get(st).getByteVal(bArray[0][1]);
			watcherMap.get(st).getBoolVal(bArray[0][1]);
			watcherMap.get(st).getParsedBoolVal(bArray[0][1]);
		}
	}
}
