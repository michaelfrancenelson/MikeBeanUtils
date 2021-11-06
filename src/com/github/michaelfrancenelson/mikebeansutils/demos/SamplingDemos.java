package com.github.michaelfrancenelson.mikebeansutils.demos;

import java.util.ArrayList;
import java.util.List;

import com.github.michaelfrancenelson.mikebeansutils.sampling.MultiplicitySet;
import com.github.michaelfrancenelson.mikebeansutils.sampling.WeightedItem;
import com.github.michaelfrancenelson.mikebeansutils.sampling.WeightedRandomSample;

import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class SamplingDemos
{

	public static void main(String[] args) 
	{
		demoESStatic();
		testESStaticDest();
	}

	
	@SuppressWarnings("unused")
	private static void demoDblArraySampler()
	{
		
		
	}
	
	private static void demoESStatic()
	{
		RandomStream rs = new MRG31k3p();
		MultiplicitySet<WeightedItem<String>> resultsSet2 = new MultiplicitySet<>();

		List<WeightedItem<String>> items;

		items = new ArrayList<>();
		items.add(new WeightedItem<>("1", 1));
		items.add(new WeightedItem<>("2", 1));
		items.add(new WeightedItem<>("3", 1));
		items.add(new WeightedItem<>("4", 2));

		double[] weights = new double[] {1, 1, 1, 2};

		int nTests, m;

		nTests = 10;
		m = 2;
		nTests = 10000;

		System.out.println("ES Stream Sampling, non-destructive static method 2");
		for (int i = 0; i < nTests; i++)
		{
			for (int ii = 0; ii < weights.length; ii++)
				items.get(ii).setWeight(weights[ii]);

			resultsSet2.addAll(WeightedRandomSample.reservoirES2(items, m, rs));
		}

		for (WeightedItem<String> w : resultsSet2)
		{
			System.out.println("id = " + w.getItem() + " mult = " + resultsSet2.getMultiplicity(w));
		}
	}

	private static void testESStaticDest()
	{
		RandomStream rs = new MRG31k3p();
		MultiplicitySet<String> resultsSet2 = new MultiplicitySet<>();

		List<WeightedItem<String>> items;

		items = new ArrayList<>();
		items.add(new WeightedItem<>("1", 1));
		items.add(new WeightedItem<>("2", 1));
		items.add(new WeightedItem<>("3", 1));
		items.add(new WeightedItem<>("4", 2));

		double[] weights = new double[] {1, 1, 1, 2};

		int nTests, m;

		nTests = 10;
		m = 2;
		nTests = 10000;

		System.out.println("ES Stream Sampling, destructive static");
		for (int i = 0; i < nTests; i++)
		{
			for (int ii = 0; ii < weights.length; ii++)
				items.get(ii).setWeight(weights[ii]);

			resultsSet2.addAll(WeightedRandomSample.reservoirDestructiveES(items, m, rs));
		}

		for (String w : resultsSet2)
		{
			System.out.println("id = " + w + " mult = " + resultsSet2.getMultiplicity(w));
		}
	}
}