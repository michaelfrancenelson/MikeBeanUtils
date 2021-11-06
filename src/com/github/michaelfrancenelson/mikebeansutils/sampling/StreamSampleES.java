package com.github.michaelfrancenelson.mikebeansutils.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import umontreal.ssj.rng.RandomStream;

/**
 * @author michaelfrancenelson
 *
 * adapted and simplified from the ES stream sampler at:
 * http://utopia.duth.gr/~pefraimi/projects/WRS/
 * 
 * An easy to understand explanation of WRS
 * https://gregable.com/2007/10/reservoir-sampling.html
 *
 * @param <T>
 */
public class StreamSampleES <T>
{
	private RandomStream rs;
	private PriorityQueue<SampledItem<T>> q;
	private int itemsProcessed;
	private int sampleSize;

	public StreamSampleES(int sampleSize, RandomStream rs)
	{
		this.sampleSize = sampleSize;
		this.rs = rs;
		q = new PriorityQueue<SampledItem<T>>(sampleSize);
	}

	public void initialte()
	{
		q.clear();
		itemsProcessed = 0;
	}

	public void feedItems(List<WeightedItem<T>> items)
	{
		for (WeightedItem<T> w : items) feedItem(w);
	}

	public void feedItem(WeightedItem<T> newItem)
	{
		double key = genKey(newItem, rs);
		if (itemsProcessed < sampleSize)
		{
			q.add(new SampledItem<>(newItem, key));
		}
		else
		{
			/* The head is the least element in the queue. */
			double currentThreshold = q.peek().getWeight();
			if (key > currentThreshold)
			{
				q.poll();
				q.add(new SampledItem<>(newItem, key));
			}
		}
		itemsProcessed++;
	}

	public List<WeightedItem<T>> getSample()
	{
		List<WeightedItem<T>> out = new ArrayList<>();
		for (SampledItem<T> w: q) out.add(w.getItem());
		return out;
	}

	public static <T> double genKey(WeightedItem<T> item, RandomStream rs)
	{
		return Math.pow(rs.nextDouble(), 1.0 / item.getWeight());
	}
}