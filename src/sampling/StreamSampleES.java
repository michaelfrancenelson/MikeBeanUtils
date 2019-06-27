package sampling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import sampling.WeightedRandomSample.WeightedItem;
import umontreal.ssj.rng.RandomStream;

/**
 * 
 * @author michaelfrancenelson
 *
 * adapted from the ES stream sampler at:
 * http://utopia.duth.gr/~pefraimi/projects/WRS/
 * 
 * An easy to understand explanation of WRS
 * https://gregable.com/2007/10/reservoir-sampling.html
 *
 * @param <T>
 */

public class StreamSampleES <T>
{
	private class SampledItem implements Comparable<SampledItem>
	{
		double weight;
		
		public WeightedItem<T> wItem; // The corresponding weighted item
		
		public SampledItem(WeightedItem<T> item, double d)
		{
			this.wItem = item; this.weight = d;
		}
		@Override
		public int compareTo(StreamSampleES<T>.SampledItem arg0) {
			return Double.compare(weight, arg0.weight);
		}


	}

	private RandomStream rs;
	private PriorityQueue<SampledItem> q;
	private int itemsProcessed;
	private int numOfInsertions;
	private int sampleSize;

	public StreamSampleES(
			int sampleSize, 
			RandomStream rs)
	{
		this.sampleSize = sampleSize;
		this.rs = rs;
		q = new PriorityQueue<SampledItem>(sampleSize);
	}

	public void initialte()
	{
		q.clear();
		itemsProcessed = 0;
		numOfInsertions = 0;
	}

	public void feedItems(List<WeightedItem<T>> items)
	{
		for (WeightedItem<T> w : items) feedItem(w);
	}
	
	public void feedItem(WeightedItem<T> newItem)
	{
		double key = genKey(newItem);
		if (itemsProcessed < sampleSize)
		{
			q.add(new SampledItem(newItem, key));
		}
		else
		{
			/* The head is the least element in the queue. */
			double currentThreshold = q.peek().weight;
			if (key > currentThreshold)
			{
				q.poll();
				q.add(new SampledItem(newItem, key));
				numOfInsertions++;
			}
		}
		itemsProcessed++;
	}

	public List<WeightedItem<T>> getSample()
	{
		List<WeightedItem<T>> out = new ArrayList<>();
		for (SampledItem w: q) out.add(w.wItem);
		return out;
	}

	public double genKey(WeightedItem<T> item)
	{
		return Math.pow(rs.nextDouble(), 1.0 / item.weight);
	}
}
