package sampling;

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

//package sampling;
//
//
//public class SampledItem implements Comparable<SampledItem>
//{
//	private double weight;
//	private  item; 
//	
////	public SampledItem(T item, double d)
////	{
////		this.item = item; this.weight = d;
////	}
//
//	public SampledItem(T newItem, double key) 
//	{
//		this.item = newItem;
//		this.weight = key;
//	}
//
//	@Override
//	public int compareTo(SampledItem<T> t) 
//	{
//		if (t.weight < weight) return -1;
//		if (weight > t.weight) return 1;
//		return 0;
//	}
//	
//	public T getItem() { return item;}
//	public void setItem(T item) { this.item = item; }
//
//	public double getWeight() { return weight; }
//	public void setWeight(double weight) { this.weight = weight; }
//}
//
////public class SampledItem<T extends WeightedItem<?>> implements Comparable<SampledItem<T>>
////{
////	private double weight;
////	private T item; 
////	
//////	public SampledItem(T item, double d)
//////	{
//////		this.item = item; this.weight = d;
//////	}
////
////	public SampledItem(T newItem, double key) 
////	{
////		this.item = newItem;
////		this.weight = key;
////	}
////
////	@Override
////	public int compareTo(SampledItem<T> t) 
////	{
////		if (t.weight < weight) return -1;
////		if (weight > t.weight) return 1;
////		return 0;
////	}
////	
////	public T getItem() { return item;}
////	public void setItem(T item) { this.item = item; }
////
////	public double getWeight() { return weight; }
////	public void setWeight(double weight) { this.weight = weight; }
////}
