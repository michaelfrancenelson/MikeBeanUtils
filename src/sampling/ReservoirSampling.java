package sampling;

import java.util.ArrayList;
import java.util.List;

import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class ReservoirSampling 
{

	public static void main(String[] args) 
	{
		test3();
	}

	static void test3()
	{
		RandomStream rs = new MRG31k3p();
		List<WeightedItem<String>> items = new ArrayList<>();
		MultiplicitySet<WeightedItem<String>> resultsSet = new MultiplicitySet<>();
		
		items.add(new WeightedItem<>("1", 1));
		items.add(new WeightedItem<>("2", 1));
		items.add(new WeightedItem<>("3", 1));
		items.add(new WeightedItem<>("4", 2));

		int nTests, m;

		nTests = 10;
		m = 2;
		nTests = 10000;

		StreamSampleES<String> es =
				new StreamSampleES<String>(m, rs);
		System.out.println("ES Stream Sampling");
		for (int i = 0; i < nTests; i++)
		{
			es.initialte();
			for (WeightedItem<String> w : items)
			{
				es.feedItem(w);
			}
			resultsSet.addAll(es.getSample());

		}

		for (WeightedItem<String> w : resultsSet)
		{
			System.out.println("id = " + w.getItem() + 
					" weight = " + w.getWeight() + 
					" mult. = " + resultsSet.getMultiplicity(w));
		}
	}
}
