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
	
	
	static void test2()
	{
		RandomStream rs = new MRG31k3p();
		List<WeightedTestClass<Object>> items = new ArrayList<>();
		MultiplicitySet<WeightedItem<Object>> resultsSet = new MultiplicitySet<>();
		
		items.add(new WeightedTestClass<>(1, 1));
		items.add(new WeightedTestClass<>(2, 1));
		items.add(new WeightedTestClass<>(3, 1));
		items.add(new WeightedTestClass<>(4, 2));

		//		Collections.shuffle(items);

		int nTests, m;

		nTests = 10;
		m = 2;
		nTests = 10000;

		StreamSampleES<Object> es =
				new StreamSampleES<Object>(m, rs);
		System.out.println("ES Stream Sampling");
		for (int i = 0; i < nTests; i++)
		{
			es.initialte();
			for (WeightedTestClass<Object> w : items)
			{
				es.feedItem(w);
			}
			resultsSet.addAll(es.getSample());

		}

		for (WeightedItem<Object> w : resultsSet)
		{
			System.out.println("id = " + ((WeightedTestClass<Object>) w).id + 
					" weight = " + w.getWeight() + 
					" mult. = " + resultsSet.getMultiplicity(w));
		}

		//		System.out.println("Chao Sampling");
		//		for (int i = 0; i < nTests; i++)
		//		{
		//			resultsSet.addAll(chaoSample(items, m, rs));
		//		}
		////
		////		//		resultsSet.printMultiplicity();
		//		for (WeightedTestClass<Object> w : resultsSet)
		//		{
		//			System.out.println("id = " + w.id + 
		//					" weight = " + w.weight + 
		//					" mult. = " + resultsSet.getMultiplicity(w));
		//		}
		//
		//		System.out.println("\nEfraimidis Sampling");
		//		resultsSet = new MultiplicitySet<>();
		//		for (int i = 0; i < nTests; i++)
		//		{
		//			resultsSet.addAll(efraimidisSpirakisSample(items, m, rs));
		//		}
		//		for (WeightedTestClass<Object> w : resultsSet)
		//		{
		//			System.out.println("id = " + w.id + 
		//					" weight = " + w.weight + " mult. = " + resultsSet.getMultiplicity(w));
		//		}
		//		resultsSet.printMultiplicity();

		//		for (int i = 0; i < nTests; i++)
		//		{
		//			int index = rs.nextInt(0, items.size() - 1);
		//			resultsSet.add(items.get(index));
		//		}
		//
//		resultsSet.printMultiplicity();
	}
	
	static void test1()
	{
		RandomStream rs = new MRG31k3p();
		List<WeightedItem<String>> items = new ArrayList<>();
//		List<WeightedTestClass<Object>> items = new ArrayList<>();
		MultiplicitySet<WeightedItem<String>> resultsSet = new MultiplicitySet<>();
//		MultiplicitySet<WeightedItem<Object>> resultsSet = new MultiplicitySet<>();
		
		items.add(new WeightedItem<>("1", 1));
		items.add(new WeightedItem<>("2", 1));
		items.add(new WeightedItem<>("3", 1));
		items.add(new WeightedItem<>("4", 2));
//		items.add(new WeightedTestClass<>(1, 1));
//		items.add(new WeightedTestClass<>(2, 1));
//		items.add(new WeightedTestClass<>(3, 1));
//		items.add(new WeightedTestClass<>(4, 2));

		int nTests, m;

		nTests = 10;
		m = 1;
		nTests = 10000;

		StreamSampleES<String> es =
//				StreamSampleES<Object> es =
				new StreamSampleES<String>(m, rs);
//		new StreamSampleES<Object>(m, rs);
		System.out.println("ES Stream Sampling");
		for (int i = 0; i < nTests; i++)
		{
			es.initialte();
			for (WeightedItem<String> w : items)
//				for (WeightedTestClass<Object> w : items)
			{
				es.feedItem(w);
			}
			resultsSet.addAll(es.getSample());

		}

		for (WeightedItem<String> w : resultsSet)
//			for (WeightedItem<Object> w : resultsSet)
		{
			System.out.println("id = " + ((WeightedItem<String>) w).getItem() + 
//					System.out.println("id = " + ((WeightedTestClass<Object>) w).id + 
					" weight = " + w.getWeight() + 
					" mult. = " + resultsSet.getMultiplicity(w));
		}

		//		System.out.println("Chao Sampling");
		//		for (int i = 0; i < nTests; i++)
		//		{
		//			resultsSet.addAll(chaoSample(items, m, rs));
		//		}
		////
		////		//		resultsSet.printMultiplicity();
		//		for (WeightedTestClass<Object> w : resultsSet)
		//		{
		//			System.out.println("id = " + w.id + 
		//					" weight = " + w.weight + 
		//					" mult. = " + resultsSet.getMultiplicity(w));
		//		}
		//
		//		System.out.println("\nEfraimidis Sampling");
		//		resultsSet = new MultiplicitySet<>();
		//		for (int i = 0; i < nTests; i++)
		//		{
		//			resultsSet.addAll(efraimidisSpirakisSample(items, m, rs));
		//		}
		//		for (WeightedTestClass<Object> w : resultsSet)
		//		{
		//			System.out.println("id = " + w.id + 
		//					" weight = " + w.weight + " mult. = " + resultsSet.getMultiplicity(w));
		//		}
		//		resultsSet.printMultiplicity();

		//		for (int i = 0; i < nTests; i++)
		//		{
		//			int index = rs.nextInt(0, items.size() - 1);
		//			resultsSet.add(items.get(index));
		//		}
		//
		//		resultsSet.printMultiplicity();
	}


//	public static <T, W extends WeightedItem<T>> List<W> chaoSample(
//			List<W> items,
//			int m,
//			RandomStream rs)
//	{
//		List<W> output = new ArrayList<>(m);
//		int n = items.size();
//
//		double cumulativeWeight = 0.0;
//		double currentWeight   = 0.0;
//		double currentProb;
//		double testProb;
//
//		W currentItem;
//		int insertionIndex;
//
//		for (int i = 0; i < m; i++)
//		{
//			currentItem = items.get(i);
//			output.add(currentItem);
//			cumulativeWeight += currentItem.weight;
//		}
//
//		for (int i = m; i < n; i++)
//		{
//			currentItem = items.get(i);
//			currentWeight = currentItem.weight;
//
//			//			System.out.println("cumulative weight before:   " + cumulativeWeight);
//
//			cumulativeWeight += currentWeight;
//			currentProb = currentWeight / cumulativeWeight;
//			testProb = rs.nextDouble();
//
//			System.out.println("current item weight: " + currentItem.weight);
//			System.out.println("cumulative weight  : " + cumulativeWeight);
//			System.out.println("current item prob  : " + currentProb);
//			System.out.println("test         prob  : " + testProb);
//
//			//				if (probI <= testI) 
//			if (testProb <= currentProb)
//			{
//				insertionIndex = rs.nextInt(0, m - 1);
//				System.out.println("Insertion index: " + insertionIndex);
//				output.set(
//						insertionIndex, 
//						currentItem);
//			}
//			System.out.println();
//		}
//		return output;
//	}

//	public static <T, W extends WeightedItem<T>> List<W> efraimidisSpirakisSample(
//			List<W> items,
//			int m,
//			RandomStream rs)
//	{
//		int n = items.size();
//
//		double testI, testAdjI;
//
//		//		WeightedItem<T>
//		W item;
//		PriorityQueue<W> q = new PriorityQueue<W>(m);
//
//		for (int i = 0; i < m; i++)
//		{
//			item = items.get(i);
//			testI = rs.nextDouble();
//			testAdjI = Math.pow(testI, 1.0 / item.weight);
//			item.weight = testAdjI;
//			q.add(item);
//		}
//
//		for (int i = m; i < n; i++)
//		{
//			item = items.get(i);
//			testI = rs.nextDouble();
//			testAdjI = Math.pow(testI, 1.0 / item.weight);
//
//			if (q.peek().weight < testAdjI)
//			{
//				item.weight = testAdjI;
//				q.poll();
//				q.add(item);
//			}
//		}
//
//		List<W> out = new ArrayList<>(m);
//		for (W w : q) out.add(w);
//
//		return out;
//	}

	public static class WeightedTestClass<T> extends WeightedItem<T>
	{
		private int id;
		private double weight;
		
		@Override public double getWeight() { return this.weight; }
		
		public WeightedTestClass(int i, double d) 
		{ this.id = i; weight = d; }

		@Override
		public boolean equals(Object o) { 

			if (((WeightedTestClass<?>) o).id == this.id) return true;
//			if ((WeightedTestClass<?>)o.id == this.id) return true;
			return false;
		} 

		//		public static <T> Comparator<WeightedTestClass<T>> getComparator(Class<T> clazz, boolean loToHi)
		//		{
		//			
		//			if (loToHi)
		//			return new Comparator<WeightedTestClass<T>>() {
		//				@Override
		//				public int compare(WeightedTestClass<T> arg0, WeightedTestClass<T> arg1) {
		//					if (arg0.weight < arg1.weight) return  -1;
		//					if (arg0.weight > arg1.weight) return  1;
		//					return 0;
		//				}
		//			};
		//			else 
		//				return new Comparator<WeightedTestClass<T>>() {
		//				@Override
		//				public int compare(WeightedTestClass<T> arg0, WeightedTestClass<T> arg1) {
		//					if (arg0.weight < arg1.weight) return  1;
		//					if (arg0.weight > arg1.weight) return  -1;
		//					return 0;
		//				}
		//			};
		//		}
	}

}
