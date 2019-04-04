package beans.sampleBeans;

import java.util.Random;

public class TerrainBean 
{

	int elevation;
	boolean stream = false;
	static Random r = new Random();

	public TerrainBean(int elevation) { this.elevation = elevation; }

	public void perturbElevation(int range) { this.elevation += r.nextInt(range) - range / 2; }

	public static void randomRiver(TerrainBean[][] cells, int nRivers)
	{
		randomRivers(cells, 0.5, 0.5, 0.5, 0.5, nRivers);
	}
	
	/**
	 * 
	 * @param cells
	 * @param probRight
	 * @param probDown
	 * @param probVertical
	 * @param probDiagonal
	 * @param nRivers
	 */
	public static void randomRivers(
			TerrainBean[][] cells, 
			double probRight, double probDown, 
			double probVertical, double probDiagonal, 
			int nRivers)
	{
		for (int river = 0; river < nRivers; river++)
		{
			int x = r.nextInt(cells.length - 1);
			int y = r.nextInt(cells[0].length - 1);

			boolean edge = false;
			while (!edge)
			{
				cells[x][y].stream = true;

				int newX = randomCoord(x, probRight);
				int newY = randomCoord(y, probDown);

				double draw1 = r.nextDouble();

				if (draw1 < probDiagonal)
				{
					x = newX; y = newY;
				}
				else if (r.nextDouble() < probVertical) 
					y = newY;
				else
					x = newX;

				if (((x < 0) || (y < 0)) || ((x >= cells.length) || (y >= cells[0].length)))
					edge = true;
			}
		}
	}

	/**
	 * 
	 * @param current
	 * @param prob
	 * @return
	 */
	public static int randomCoord(int current, double prob)
	{
		if (r.nextDouble() < prob) return current + 1;
		else return current - 1;
	}

	/**
	 * 
	 * @param array
	 * @param range
	 */
	public static void perturbElevation(TerrainBean[][] array, int range)
	{
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				array[i][j].perturbElevation(range);
			}
		}
	}

	/**
	 * 
	 * @param width
	 * @param height
	 * @param gradient
	 * @return
	 */
	public static TerrainBean[][] factory(int width, int height, double gradient)
	{
		TerrainBean[][] out = new TerrainBean[width][height];

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				out[i][j] = new TerrainBean(i + (int)(gradient * (double) j));

		return out;
	}

}
