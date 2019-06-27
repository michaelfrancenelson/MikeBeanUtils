package sampling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sampling.WeightedRandomSample.WeightedItem;
import utils.MethodUtils.DoubleGetter;


/**
 * A set of Objects, each associated with a multiplicity representing
 * the number of times each appears in the set.
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
public class MultiplicitySet<T> implements Set<T>
{
	private HashMap<T, Integer> map;		

	private int maxMult = Integer.MAX_VALUE;
	public void setMaxMult(int i) { this.maxMult = i; }

	public MultiplicitySet() { map = new HashMap<T, Integer>(); }

	@Override
	public boolean add(T t)
	{
		if (map.containsKey(t))
		{
			Integer mult = map.get(t);
			map.put(t, Math.min(mult + 1, maxMult));
			return true;
		}
		else map.put(t, 1);
		return false;
	}

	/**
	 * @param t
	 * @return the number of times item 't' appears in the set,
	 *         i.e. its multiplicity.  
	 *         if the item is not in the set, returns 0. 
	 */
	public int getMultiplicity(T t)
	{
		Integer i = map.get(t);
		if (i != null) return i;
		return 0;
	}

	/**
	 * Calculates weights for objects in a MultiplicitySet. <br>
	 * Weights may be calculated using the objects' multiplicities,
	 * and/or the values retrieved from an input getter.
	 * 
	 * @param weightBenefit the amount of extra weight an object receives
	 *                      for appearing in the set more than once. <br>
	 *                      If > 0.0, greater multiplicity increases the
	 *                      item's weight. <br>
	 *                      If < 0.0, greater multiplicity decreases item's weight.
	 *                      If = 0.0, all items are weighted equally, regardless
	 *                      of multiplicity within this set.
	 *                      
	 * @param getter if <code> null </code>, weights are calculated only from
	 *               multiplicities, otherwise the values retrieved from
	 *               the getter are used in the calculation. 
	 * @return a list of WeightedItem objects.
	 */
	public List<WeightedItem<T>> getWeightedItems(
			double weightBenefit, DoubleGetter<T> getter)
	{
		List<WeightedItem<T>> out = new ArrayList<>();
		WeightedItem<T> wi;
		if (getter == null)
		{
			for (T t : map.keySet())
			{
				double mult = map.get(t);
				double wAdj = 1 + (mult - 1) * weightBenefit;
				out.add(new WeightedItem<>(t, wAdj));
			}
		}
		else {
			for (T t : map.keySet())
			{
				double w = getter.get(t);
				double mult = map.get(t);

				double wAdj = w + weightBenefit * (w * (mult - 1));
				wi = new WeightedItem<T>(t, wAdj);
				out.add(wi);
			}
		}
		return out;
	}

	/**
	 * 
	 * @return a list of items.  Items that appear more than once in the set
	 *           will be repeated in the list.
	 */
	public List<T> getItems()
	{
		List<T> out = new ArrayList<>();
		for (T tt : map.keySet())
		{
			int mult = map.get(tt);
			for (int i = 0; i < mult; i++)
			{
				out.add(tt);
			}
		}
		return out;
	}

	public void printMultiplicity()
	{
		for (T t : map.keySet())
			System.out.println("Item " + t.toString() + " multiplicity " + map.get(t));
	}
	public void printContents()
	{
		for (T t : getItems()) System.out.println(t.toString());
	}

	@Override public boolean addAll(Collection<? extends T> arg0) { for (T t : arg0) add(t); return false; }
	@Override public void clear() {map = new HashMap<T, Integer>(); }
	@Override public boolean contains(Object arg0) { return map.containsKey(arg0); }
	@Override public boolean containsAll(Collection<?> arg0) { for (Object t : arg0) if (!map.containsKey(t)) return false; return true; }
	@Override public boolean isEmpty() { return map.size() == 0; }
	@Override public Iterator<T> iterator() { return map.keySet().iterator(); }
	@Override public boolean remove(Object arg0) { return map.remove(arg0) > 0; }
	@Override public boolean removeAll(Collection<?> arg0) { for (Object t : arg0) map.remove(t); return false; }
	@Override public boolean retainAll(Collection<?> arg0) { return false; }
	@Override public int size() { return map.size(); }
	@Override
	public Object[] toArray() 
	{
		List<T> l = getItems();
		Object[] out = new Object[l.size()];
		for (int i = 0; i < l.size(); i++) out[i] = l.get(i);
		return out;
	}
	@SuppressWarnings("hiding")
	@Override public <T> T[] toArray(T[] arg0) {	return null;}
}
