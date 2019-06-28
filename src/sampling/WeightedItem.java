package sampling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import utils.MethodUtils.DoubleGetter;

/**
 * A struct to hold an item and an associated weight.
 * Useful for weighted selection algorithms.
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 * @param <S>
 */
public class WeightedItem<T> implements Comparable<WeightedItem<T>>
{
	private T item;
	private double weight;

	public WeightedItem() {;}
	
	public WeightedItem(T t, double w) { item = t; weight = w; }

	/** Convert a list of unweighted items to a list of weighted items
	 *  using an input getter to set weights.
	 * 
	 * @param l
	 * @param g if null, assigns every WeightedItem in the output a weight of 1.0
	 * @return
	 */
	public static <T> List<WeightedItem<T>> getWeightedList(
			List<T> l, DoubleGetter<T> g)
	{
		List<WeightedItem<T>> out = new ArrayList<>();
		if (g == null)
			for (T t : l) out.add (new WeightedItem<T>(t, 1.0));

		else for (T t : l) 
			out.add (new WeightedItem<T>(t, g.get(t)));
		return out;
	}
	
	public static <T> List<T> getItemList(List<WeightedItem<T>> l)
	{
		List<T> out = new ArrayList<>();
		for (WeightedItem<T> w : l)
			out.add(w.item);
		return out;
	}
	
	@Override
	public int compareTo(WeightedItem<T> t)
	{
		return Double.compare(weight, t.weight);
	}

	public T getItem() { return item;}
	public void setItem(T item) { this.item = item; }

	public double getWeight() { return weight; }
	public void setWeight(double weight) { this.weight = weight; }

	public static <T> Comparator<WeightedItem<T>> getComparator(Class<T> clazz, boolean loToHi)
{
	
	if (loToHi)
	return new Comparator<WeightedItem<T>>() {
		@Override
		public int compare(WeightedItem<T> item1, WeightedItem<T> item2) {
			if (item1.weight < item2.weight) return  -1;
			if (item1.weight > item2.weight) return  1;
			return 0;
		}
	};
	else 
		return new Comparator<WeightedItem<T>>() {
		@Override
		public int compare(WeightedItem<T> item1, WeightedItem<T> item2) {
			if (item1.weight < item2.weight) return  1;
			if (item1.weight > item2.weight) return  -1;
			return 0;
		}
	};
}
}



