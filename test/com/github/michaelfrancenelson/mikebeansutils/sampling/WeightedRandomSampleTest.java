package com.github.michaelfrancenelson.mikebeansutils.sampling;

import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.github.michaelfrancenelson.mikebeansutils.beans.builder.RandomBeanBuilder;
import com.github.michaelfrancenelson.mikebeansutils.beans.sampleBeans.Terrain;
import com.github.michaelfrancenelson.mikebeansutils.utils.MethodUtils;
import com.github.michaelfrancenelson.mikebeansutils.utils.MethodUtils.DoubleGetter;
import com.github.michaelfrancenelson.mikebeansutils.utils.MethodUtils.GetterComparator;

import umontreal.ssj.rng.RandomStream;

@SuppressWarnings("unused")
@Deprecated
public class WeightedRandomSampleTest {

	static boolean hiToLo = true, loToHi = false;
	static RandomStream rs;

	@After
	public void tearDown() throws Exception 
	{
	}

	@Test
	public void test() throws Throwable 
	{
		/*
		rs = Sampling.getDefaultRs();

		int length = 4000;
		double prop = 1;
		int k = 50;

		int reps = 1;

		DoubleGetter<Terrain> getter = MethodUtils.buildDoubleGetter(
				Terrain.class, "getElevation"); 

		List<Terrain> l = RandomBeanBuilder.randomFactory(Terrain.class, length, -1, 100, rs);

		List<Terrain> lHiLoChao = null, lLoHiChao = null;
		List<WeightedItem<Terrain>> lHiLoRes = null, lLoHiRes = null;
		List<WeightedItem<Terrain>> s = WeightedItem.getWeightedList(l, getter);

		GetterComparator<Terrain> elevCompHiLo = MethodUtils.getGetterComparator(Terrain.class, "getElevation", WeightedRandomSampling.HI_TO_LO);
		GetterComparator<Terrain> elevCompLoHi = MethodUtils.getGetterComparator(Terrain.class, "getElevation", WeightedRandomSampling.LO_TO_HI);

		Comparator<WeightedItem<Terrain>> compLoHi = WeightedItem.getComparator(Terrain.class, true); 
		Comparator<WeightedItem<Terrain>> compHiLo = WeightedItem.getComparator(Terrain.class, false); 

		boolean chao = true;

		long start, end, tChao, tRes;

		start = System.currentTimeMillis(); 
		for (int rep = 0; rep < reps; rep++)
		{
			lHiLoChao = 
					WeightedRandomSampling.chaoSample(
							rs,
							l, k, 
							elevCompLoHi,
							getter, 
							0.0, prop);

			lLoHiChao = 
					WeightedRandomSampling.chaoSample(
							rs,
							l, k, 
							elevCompHiLo,
							getter, 
							0.0, prop);


					WeightedRandomSample.weightedRandomSample(
					rs, s, compHiLo, k, prop, chao);
			lLoHiChao = WeightedRandomSample.weightedRandomSample(
					rs, s, compLoHi, k, prop, chao);
		}
		end = System.currentTimeMillis(); 
		tChao = end - start;

		start = System.currentTimeMillis(); 
		for (int rep = 0; rep < reps; rep++)
		{
			lHiLoRes = WeightedRandomSample.weightedRandomSample(
					rs, s, compHiLo, k, prop, !chao);
			lLoHiRes = WeightedRandomSample.weightedRandomSample(
					rs, s, compLoHi, k, prop, !chao);
		}

		end = System.currentTimeMillis(); 
		tRes = end - start;


		int toPrint = 2;


//		MethodUtils.printListWithGetter(WeightedItem.getList(lHiLoChao), getter, toPrint, "Chao: hi to lo ");
		MethodUtils.printListWithGetter(lHiLoChao, getter, toPrint, "Chao: hi to lo ");
		MethodUtils.printListWithGetter(WeightedItem.getItemList(lHiLoRes), getter, toPrint, "Res : hi to lo ");

		MethodUtils.printListWithGetter(lLoHiChao, getter, toPrint, "Chao: lo to hi ");
//		MethodUtils.printListWithGetter(WeightedItem.getList(lLoHiChao), getter, toPrint, "Chao: lo to hi ");
		MethodUtils.printListWithGetter(WeightedItem.getItemList(lLoHiRes), getter, toPrint, "Res : lo to hi ");

		System.out.println("Chao time: " + tChao + "ms");
		System.out.println("Res time : " + tRes + "ms");

		//		fail("Not yet implemented");
		 */
	}

}
