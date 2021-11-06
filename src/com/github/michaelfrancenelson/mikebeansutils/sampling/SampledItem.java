package com.github.michaelfrancenelson.mikebeansutils.sampling;

public class SampledItem <T> implements Comparable<SampledItem<T>>
{
	private double weight;
	private WeightedItem<T> wItem; // The corresponding weighted item

	public SampledItem(WeightedItem<T> item, double d)
	{
		this.wItem = item; this.weight = d;
	}
	
	
	@Override
	public int compareTo(SampledItem<T> arg0) {
		return Double.compare(weight, arg0.weight);
	}
	
	public double getWeight() { return weight; }
	public WeightedItem<T> getItem() { return wItem; }
	
}