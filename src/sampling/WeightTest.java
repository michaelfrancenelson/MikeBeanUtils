package sampling;

import java.util.List;
import java.util.function.ToDoubleFunction;

import beans.builder.RandomBeanBuilder;
import beans.sampleBeans.Terrain;
import umontreal.ssj.rng.RandomStream;
import utils.MethodUtils;
import utils.MethodUtils.GetterComparator;

public class WeightTest 
{
 	
		static boolean hiToLo = true, loToHi = false;
		static RandomStream rs;
	
	public static void main(String[] args) throws Throwable 
	{
	
		
		
		rs = Sampling.getDefaultRs();
		
				int length = 10;
				double prop = .01;
				int k = 2;
		
				int reps = 4;
		
				List<Terrain> lHiLoChao = null, lLoHiChao = null, lHiLoRes = null, lLoHiRes = null;
				List<Terrain> s = RandomBeanBuilder.randomFactory(
						Terrain.class, length, 99999990, 99999990, rs);
		
				GetterComparator<Terrain> elevCompHiLo = MethodUtils.getGetterComparator(Terrain.class, "getElevation", WeightedRandomSample.HI_TO_LO);
				GetterComparator<Terrain> elevCompLoHi = MethodUtils.getGetterComparator(Terrain.class, "getElevation", WeightedRandomSample.LO_TO_HI);
		
				ToDoubleFunction<Terrain> getter = MethodUtils.getDoubleGetter("getElevation", Terrain.class);
		
				long start, end, tChao, tRes;
				// starting time 
		
				start = System.currentTimeMillis(); 
				for (int rep = 0; rep < reps; rep++)
				{
					lHiLoChao = WeightedRandomSample.chaoSample(
							rs, 
							s, k, 
							elevCompHiLo,
							elevCompHiLo.getGetter(),
							0, prop);
					lLoHiChao = WeightedRandomSample.chaoSample(
							rs, 
							s, k, 
							elevCompLoHi,
							elevCompLoHi.getGetter(),
							0, prop);
				}
				end = System.currentTimeMillis(); 
				tChao = end - start;
		
				start = System.currentTimeMillis(); 
				for (int rep = 0; rep < reps; rep++)
				{
					lHiLoRes = WeightedRandomSample.efraimidisWeightedSample(
							rs, s, k, elevCompHiLo, elevCompHiLo.getGetter(), 0, prop);
					lLoHiRes = WeightedRandomSample.efraimidisWeightedSample(
							rs, s, k, elevCompLoHi, elevCompHiLo.getGetter(), 0, prop);
				}
		
				end = System.currentTimeMillis(); 
				tRes = end - start;
		
		
				int toPrint = 2;
		
				MethodUtils.printListWithGetter(lHiLoChao, getter, toPrint, "Chao: hi to lo ");
				MethodUtils.printListWithGetter(lHiLoRes, getter, toPrint, "Res : hi to lo ");
		
				MethodUtils.printListWithGetter(lLoHiChao, getter, toPrint, "Chao: lo to hi ");
				MethodUtils.printListWithGetter(lLoHiRes, getter, toPrint, "Res : lo to hi ");
		
				System.out.println("Chao time: " + tChao + "ms");
				System.out.println("Res time : " + tRes + "ms");
		
		
						MethodUtils.printListWithGetter(s, getter, toPrint, "unsorted ");
		
		
		
	}
	
}
