package sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import umontreal.ssj.rng.RandomStream;

public class StreamSampleChao<T> 
{
	private RandomStream rs;
	private PriorityQueue<SampledItem> q;
	private int itemsProcessed;
	private int numOfInsertions;
	private int sampleSize;
	
	private List<WeightedItem<T>> itemsA;
	private List<WeightedItem<T>> itemsB;
//	private List<SampledItem<T>> itemsA;
//	private List<SampledItem<T>> itemsB;
	private double accumulatedWeight;
	private int numOfItemsInA;
	
	public StreamSampleChao(int sampleSize, RandomStream rs)
	{
		this.sampleSize = sampleSize; this.rs = rs;
		initiate();
//		itemsA = new ArrayList<>(sampleSize);
//		itemsB = new ArrayList<>(sampleSize);
	}

	
	public void initiate()
	{
		itemsA = new ArrayList<>(sampleSize);
		itemsB = new ArrayList<>(sampleSize);

	
	}
	
	public void feedItem(WeightedItem<T> newItem)
	{
		if (itemsProcessed < sampleSize) 
		{
			itemsA.add(newItem);
			accumulatedWeight += newItem.weight;
		}
		else
		{
			double probWk = 0; // The probability that the new item is inserted
			// into the sample (defined in Chao)

			boolean newItemInA = false;
			boolean newItemInSample = false;
			boolean itemsAOldEmpty = itemsA.isEmpty();

			// check if there are "overItems"
			// and prepare the sets A and B (defined in Chao's paper)

			numOfItemsInA = 0;
			
			if (itemsAOldEmpty)
			{
				
			}
		}
	}
	
	/**
	 * 
	 * @param wi
	 *            the weighted item
	 * @param effectiveAccumulatedWeight
	 *            the total weight of items that are not in the "overItems" set
	 * @param effectiveReservoirSize
	 *            the size of the reservoir minus the positions that are
	 *            occupied by "over" items
	 * @return
	 */
	public static <T> double calculateProbLevel(WeightedItem<T> wi,
			double effectiveAccumulatedWeight, double effectiveReservoirSize) {
		double probLevel = (wi.weight * effectiveReservoirSize)
				/ effectiveAccumulatedWeight;
		return probLevel;
	}
}
