package beans.sampleBeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.builder.AnnotatedBeanReader.ParsedField;


@SuppressWarnings("unused")
public class ForestCell
{
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface FeatureGetter { public String value(); }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface BooleanFeature{};

	static Logger logger = LoggerFactory.getLogger(ForestCell.class);
	protected int row, col;
	@ParsedField protected double aspect;
	@ParsedField protected boolean has_stream;
	@ParsedField protected double  dist_to_stream;
	@ParsedField protected boolean has_road;
	protected int standAge;
	protected double harvest_cost, timber_value, political_cost;

	@ParsedField protected double elevation;
	@ParsedField protected double slope;
	@ParsedField protected double dist_to_road;

	/** A counter used to place cells in a temporary 'embargo' status during which they cannot be harvested. */
	protected int embargo;

	protected static Method[] methods;
	protected static Map<String, Method> methodMap;

//	public ForestCell() { setMethodMap(); }
//	public static Method getMethod(String feature) { return methodMap.get(feature);}
//	protected static void setMethodMap()
//	{
//		methods = ForestCell.class.getDeclaredMethods();
//		methodMap = new HashMap<String, Method>();
//		for (Method m : methods) 
//		{
//			if (m.isAnnotationPresent(FeatureGetter.class))
//				methodMap.put(m.getAnnotation(FeatureGetter.class).value(), m);
//		}
//	}
//
//	public void initialize(
//			double  elevation,
//			double  slope,
//			double  aspect,
//			boolean has_stream,
//			double  dist_to_stream,
//			boolean has_road,
//			double  dist_to_road,
//			int     stand_age)
//	{
//		this.setElevation(elevation);
//		this.setSlope(slope);
//		this.setAspect(aspect);
//		this.setHasStream(has_stream);
//		this.setDist_to_stream(dist_to_stream);
//		this.setHas_road(has_road);
//		this.setDist_to_road(dist_to_road);
//		this.standAge      = stand_age;
//		this.embargo        = 0;
//	}
//
//	public void initializeNull()
//	{
//		this.setElevation(ModelParameters.NON_INITIALIZED_DOUBLE);
//		this.setSlope(ModelParameters.NON_INITIALIZED_DOUBLE);
//		this.setAspect(ModelParameters.NON_INITIALIZED_DOUBLE);
//		this.setHasStream(false);
//		this.setDist_to_stream(ModelParameters.NON_INITIALIZED_DOUBLE);
//		this.setHas_road(false);
//		this.setDist_to_road(ModelParameters.NON_INITIALIZED_DOUBLE);
//		this.standAge = ModelParameters.NON_INITIALIZED_INT;
//	}
//
//	public HarvestRecord harvestMe(int harvestPhase, DistrictRanger ranger, int simID, int year)
//	{
//		HarvestRecord harvestRecord = HarvestRecord.factory(
//				row, col, harvestPhase, standAge,
//				this.harvest_cost, timber_value, political_cost);
//
//		logger.debug("Cell's political cost = " + this.political_cost);
//		logger.debug("Cell's harvest cost   = " + this.harvest_cost);
//
//		ranger.incrementPoliticalBudgetRemaining(-this.political_cost);
//		ranger.incrementTimberBudgetRemaining(-this.harvest_cost);
//
//		ranger.incrementHarvestCellCount(harvestPhase, 1);
//		ranger.incrementYearlyProfit(this.timber_value, harvestPhase);
//
//		resetCellState();
//		return harvestRecord;
//	}
//
//	/** Recalculate the harvest cost, political cost, and timber value for this cell. */
//	public void updateCostsValues(Model2DWorld world)
//	{
//		this.harvest_cost   = world.calculateHarvestCost(this);
//		this.political_cost = world.calculatePoliticalCost(this);
//		this.timber_value   = world.calculateTimberValue(this);
//	}
//
//	/** Is this cell available for harvesting? */
//	public boolean isAvailable(int regenTime)
//	{
//		if (standAge > regenTime && embargo < 1) return true;
//		return false;
//	}
//
//	/** Generate a random harvest record, for testing. */
//	public static HarvestRecord getRandomHarvest(int runID, int timeStep)
//	{
//		Random r = new Random();
//
//		int row = r.nextInt(200), col = r.nextInt(300);
//		int standAge = r.nextInt(500);
//		double timber_value = r.nextDouble() * 10.0;
//		double harvest_cost = r.nextDouble() * 19.0;
//		double political_cost = r.nextDouble() * 45.6;
//		int harvestType = r.nextInt(2);
//
//		HarvestRecord harvestRecord = HarvestRecord.factory(
//				row, col, harvestType, standAge,
//				harvest_cost, timber_value, political_cost);
//
//		return harvestRecord;
//	}
//
//	/** Reset the age, timber value, harvest cost, political cost, and stand age */
//	public void resetCellState()
//	{
//		standAge = 0;
//		timber_value = 0.0;
//		political_cost = 0.0;
//		harvest_cost = 0.0;
//	}
//
//	/** increment the embargo period and update the cell's timber/political costs and values. */
//	public void initializeYearly(Model2DWorld world)
//	{
//		embargo = Math.max(0, embargo - 1);
//		updateCostsValues(world);
//	}
//
//	public static double[] getTimbVals(List<ForestCell> cells)
//	{
//		double[] vals = new double[cells.size()];
//		for (int i = 0; i < cells.size(); i++) vals[i] = cells.get(i).getTimberValue();
//		return vals;
//	}
//
//	public static double[] getSlopes(List<ForestCell> cells)
//	{
//		double[] vals = new double[cells.size()];
//		for (int i = 0; i < cells.size(); i++) vals[i] = cells.get(i).getSlope();
//		return vals;
//	}
//
//	public static double[] getHarvestCosts(List<ForestCell> cells)
//	{
//		double[] vals = new double[cells.size()];
//		for (int i = 0; i < cells.size(); i++) vals[i] = cells.get(i).getHarvestCost();
//		return vals;
//	}
//
//	public static double[] getPoliticalCosts(List<ForestCell> cells)
//	{
//		double[] vals = new double[cells.size()];
//		for (int i = 0; i < cells.size(); i++) vals[i] = cells.get(i).getPoliticalCost();
//		return vals;
//	}
//
//	@BooleanFeature @FeatureGetter("Has stream") public int    hasStream() { return boolToInt(has_stream); } // public boolean hasStream() { return has_stream; }
//	@BooleanFeature @FeatureGetter("Has road")   public int    hasRoad() { return boolToInt(has_road); } //public boolean hasRoad() { return has_road;	}
//	@FeatureGetter("Elevation")          public double getElevation() { return elevation; }
//	@FeatureGetter("Slope")              public double getSlope() { return slope; }				
//	@FeatureGetter("Aspect")             public double getAspect() { return aspect; }
//	@FeatureGetter("Distance to stream") public double getDistToStream() { return getDist_to_stream(); }
//	@FeatureGetter("Distance to road")   public double getDistToRoad() { return getDist_to_road(); }
//	@FeatureGetter("Stand age")          public int    getStandAge() {	return standAge; }
////	@FeatureGetter("Harvest cost")       public double getHarvestCost() { return Math.max(ModelParameters_static.min_harv_cost, harvest_cost);	}
////	@FeatureGetter("Timber value")       public double getTimberValue() { return Math.max(ModelParameters_static.min_timb_val, timber_value);}
////	@FeatureGetter("Political cost")     public double getPoliticalCost() { return Math.max(ModelParameters_static.min_poli_cost, political_cost); }
//	@FeatureGetter("Embargo")            public int    getEmbargo() { return embargo; }
//	@FeatureGetter("Harvest cost")       public double getHarvestCost() { return harvest_cost; }
//	@FeatureGetter("Timber value")       public double getTimberValue() { return timber_value; }
//	@FeatureGetter("Political cost")     public double getPoliticalCost() { return political_cost; }
//	
//
//	/** @return (row, column) */ public int[] getCoord() { return new int[] {row, col}; }
//	public void setCoords(int row, int col) { this.row = row;	this.col = col; }
//	public boolean isManaged() { return true; }
//
//	protected int boolToInt(boolean b ) { if (b) return 1; return 0; }
//
//	public void setStandAge(int standAge) { this.standAge = standAge; }
//	public void setEmbargo(int embargo_length) { this.embargo = embargo_length; }
//	public void incrementStandAge() { standAge++; }
//
//	@BooleanFeature @FeatureGetter("Available to harvest")
//	public int getCanHarvest(DistrictRanger ranger, int regenTime) {
//		if (ranger.canHarvestCell(this, regenTime)) return 1;
//		return 0;
//	}
//
//	/** 
//	 * @param adj height adjustment, added to the cell's height
//	 */
//	@BetaOrder(predictors = {Predictor.SLOPE, Predictor.ELEV, Predictor.AGE, Predictor.ROAD}) 
//	public double[] getDoubles() { return new double[] { getSlope(), getElevation(), getStand_age(), getDist_to_road() }; }
//
//	public void setElevation(double elevation) {
//		this.elevation = elevation;
//	}
//
//	public void setSlope(double slope) { this.slope = slope;}
//	public void setAspect(double aspect) { this.aspect = aspect; }
//	
//	public void setHasStream(boolean has_stream) {	this.has_stream = has_stream; }
//	public double getDist_to_stream() {	return dist_to_stream; }
//	public void setDist_to_stream(double dist_to_stream) { this.dist_to_stream = dist_to_stream; }
//	public void setHas_road(boolean has_road) { this.has_road = has_road; }
//	public double getDist_to_road() { return dist_to_road; }
//	public void setDist_to_road(double dist_to_road) { this.dist_to_road = dist_to_road; }
//	public int getStand_age() { return stand_age; }
//	public void setStand_age(int stand_age) { this.stand_age = stand_age;}
}