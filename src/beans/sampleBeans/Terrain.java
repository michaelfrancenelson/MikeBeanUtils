package beans.sampleBeans;

import java.util.Random;

import beans.builder.AnnotatedBeanReader.ParsedField;

/** Demonstration bean type. 
 * 
 * @author michaelfrancenelson
 *
 */
public class Terrain 
{
	static Random r = new Random();

	@ParsedField public int age;
	@ParsedField boolean stream = false;
	@ParsedField double elevation;
	@ParsedField byte road = 0;

	public Terrain() {}
	
	public Terrain(double elevation, int age) { this.elevation = elevation; this.age = age; }

	/**
	 * 
	 * @param cells
	 * @param probRight
	 * @param probDown
	 * @param probVertical
	 * @param probDiagonal
	 * @param nPath
	 */
	public static void randomPath(
			Terrain[][] cells,
			boolean road, boolean river,
			double probRight, double probDown, 
			double probVertical, double probDiagonal, double correlation, 
			int nPath)
	{
		int maxIter = 10000;
		for (int path = 0; path < nPath; path++)
		{
			int x = r.nextInt(cells.length - 1);
			int y = r.nextInt(cells[0].length - 1);

			byte[] roadVal = new byte[1];
			r.nextBytes(roadVal);
			boolean edge = false;
			int iter = 0,

					adjX = randomAdjustment(0, probRight, 0.0),
					adjY = randomAdjustment(0, probDown, 0.0);
			while (!edge && iter < maxIter)
			{
				iter++;
				if (road)  cells[x][y].road = roadVal[0];
				if (river) cells[x][y].stream = true;

				adjX = randomAdjustment(adjX, probRight, correlation);
				adjY = randomAdjustment(adjY, probDown, correlation);

				if (r.nextDouble() < probDiagonal) y += adjY;
				if (r.nextDouble() < probVertical) x += adjX;

				if (((x < 0) || (y < 0)) || ((x >= cells.length) || (y >= cells[0].length)))
					edge = true;
			}
		}
	}

	/**
	 * 
	 * @param current Current coordinate
	 * @param probIncrement Probability that the coordinate increases
	 * @return current coordinate +/- 1 depending on outcome of random draw
	 */
	public static int randomAdjustment(int current, double probIncrement, double correlation)
	{
		if (r.nextDouble() < correlation) return current;
		if (r.nextDouble() < probIncrement) return 1;
		else return -1;
	}

	/** Add a uniformly-distributed age perturbabion to the bean.
	 * 
	 * @param range ages will be perturbed up or down within this range
	 */
	public void perturbAge(int range) 
	{
		int adj = 0;
		if (range > 0) adj = r.nextInt(2 * range) - range;  
		this.age += adj;

		//		System.out.println("TerrainBean.perturbAge(): range = " + range + " adj = " + adj);
	}
	public void perturbElevation(double range) { this.elevation += r.nextDouble() * 2 * range - range; }

	/** Perturb the age values of a 2D array of beans.
	 *  A uniformly distributed perturbabion is applied to each bean in the array.
	 * 
	 * @param array input array
	 * @param range amount by which the age may be perturbed up or down
	 */
	public static void perturbAges(Terrain[][] array, int range)
	{
		for (int i = 0; i < array.length; i++) 
			for (int j = 0; j < array[0].length; j++) 
				array[i][j].perturbAge(range);
	}

	public static void perturbElevations(Terrain[][] array, double range)
	{
		for (int i = 0; i < array.length; i++) 
			for (int j = 0; j < array[0].length; j++) 
				array[i][j].perturbElevation(range);
	}

	public static Terrain[][] factory(int width, int height, double elevGradient, int ageMod)
	{
		Terrain[][] out = new Terrain[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				int age = ((int)Math.pow((int) Math.log(i * j), 2.4)  + j + i / 2 + (i / (1 + j))) % ageMod;
				double elev = (double) i + (elevGradient * (double) j); 
				out[i][j] = new Terrain(elev, age);
			}
		return out;
	}
	
	public double getElevation() { return this.elevation; }
}