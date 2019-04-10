package utils;

public class Slope 
{

	
	
	public static double[][] slope4(double[][] data)
	{
		double[][] out = new double[data.length][data[0].length];
		
		double dx = 0.5, dy = 0.5;
		double wx = 0.0, wy = 0.0;
		for (int i = 0; i < data.length; i++) 
		{
			for (int j = 0; j < data.length; j++)
			{
				/* non-edge cases */
				if ((i > 0 && i < data.length - 1) && (j < 0 && j < data[0].length))
				{
					wx = dx * (data[i - 1][j] - data[i + 1][j]);
					wy  = dy * (data[i][j - 1] - data[i][j + 1]);
				}
			}
		}
		
		return out;
	}
}
