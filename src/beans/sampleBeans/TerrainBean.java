package beans.sampleBeans;

import java.util.Random;

public class TerrainBean 
{
	
	int elevation;
	boolean stream = false;
	static Random r = new Random();

	public TerrainBean(int elevation) { this.elevation = elevation; }

	public void perturbElevation(int range) { this.elevation += r.nextInt(range) - range / 2; }

	/**
	 * 
	 * @param cells
	 * @param rightProb
	 * @param upProb
	 * @param nRivers
	 */
	public static void randomRivers(
			TerrainBean[][] cells, 
			double rightProb, double upProb, 
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

				
				int newX = randomCoord(x, rightProb);
				int newY = randomCoord(y, upProb);
				
				
				double draw1 = r.nextDouble();
				
				if (draw1 < probVertical)
				{
					x = newX; 
					y = newY;
				}
				else (if r.nextDouble() < probVertical) x = newX;
				else y = newY;

				if (((x < 0) || (y < 0)) || ((x >= cells.length) || (y >= cells[0].length)))
					edge = true;
			}

		}
	}

	public static int randomCoord(int current, double prob)
	{
		if (r.nextDouble() < prob) return current + 1;
		else return current - 1;
	}
	
	

	public static void perturbElevation(TerrainBean[][] array, int range)
	{
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				array[i][j].perturbElevation(range);
			}
		}
	}

	public static TerrainBean[][] factory(int width, int height, double gradient)
	{
		TerrainBean[][] out = new TerrainBean[width][height];

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				out[i][j] = new TerrainBean(i + (int)(gradient * (double) j));

		return out;
	}
	
}
