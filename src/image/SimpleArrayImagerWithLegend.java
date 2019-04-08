package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import beans.memberState.SimpleFieldWatcher;
import sequences.Sequences;

public class SimpleArrayImagerWithLegend<T> extends SimpleArrayImager<T>
{

	private int nLegendSteps, legendDirection;
	private double[][]  legendDataDouble = null;
	private int[][]     legendDataInt = null;
	private boolean[][] legendDataBool = null;

	//	int startIndex = 0, endIndex = nLegendSteps;
	int datDim1 = 1, datDim2 = nLegendSteps;

	double legendMin, legendMax;
	double[] legendDataSequence;

	int indexMult1, indexMult2;
	boolean includeBooleanNA = true;

	BufferedImage legendImg;


	/**
	 * 
	 * @param clazz
	 * @param objArray 
	 * @param fieldName the image will be built from values in this field.
	 * @param gradientColors color gradient
	 * @param booleanColors Colors to use to plot a boolean member.  Only the first and last colors are used.
	 * @param naDouble pixels with this value will be plotted with the 'naColor'
	 * @param naInt pixels with this value will be plotted with the 'naColor'
	 * @param naColor color to plot for NA values.
	 * @param dblFmt
	 * @param parsedBooleanFields Which fields should be drawn as parsed boolean variables?
	 *                            This feature allows true/false and naColors to be shown.
	 *                            Can be null.
	 * @return
	 */
	public static <T> ObjectArrayImager<T> factory(
			Class<T> clazz, T[][] objArray,	String fieldName, 
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor,
			String dblFmt, Iterable<String> parsedBooleanFields,
			boolean includeBooleanNA,
			int nLegendSteps, int legendDirection
			)
	{
		SimpleArrayImagerWithLegend<T> out = new SimpleArrayImagerWithLegend<>();
		out.clazz = clazz;
		out.ci = SimpleColorInterpolator.factory(gradientColors, 0.0, 1.0, naDouble, naInt, naColor, dblFmt);
		out.booleanCI = SimpleBooleanColorInterpolator.factory(booleanColors, naDouble, naInt, naColor);
		out.objArray = objArray;
		out.watchers = SimpleFieldWatcher.getWatcherMap(clazz, dblFmt);
		Map<String, Boolean> mp = new HashMap<>();
		for (String s : out.watchers.keySet()) mp.put(s, false);
		if (parsedBooleanFields != null) for (String s : parsedBooleanFields)   mp.put(s, true);
		out.parsedBooleanFields = mp;
		out.includeBooleanNA = includeBooleanNA;
		out.nLegendSteps = nLegendSteps;
		out.legendDirection = legendDirection;
		out.setField(fieldName);

		return out;
	}	
	
	
	
	@Override
	public void refresh()
	{
		super.refresh();
		setLegendDataProperties();
		buildLegendDataArray();
	}

	void buildLegendImage()
	{
		ColorInterpolator interp;
		if (parsedBooleanFields.get(watcher.getFieldName()))
			interp = booleanCI;
		else interp = ci;
		int row = 0, col = 0;
		switch (watcher.getField().getType().getSimpleName())
		{
		case("int"):
		{
			for (int i = 0; i == nLegendSteps; i++)
			{
				row = i * indexMult1; col = i * indexMult2;
				img.setRGB(row, col, interp.getColor(legendDataInt[row][col]));
			}
			break;
		}
		case("double"): 
		{
			for (int i = 0; i == nLegendSteps; i++)
			{
				row = i * indexMult1; col = i * indexMult2;
				img.setRGB(row, col, interp.getColor(legendDataDouble[row][col]));
			}
			break;
		}
		case("boolean"): 
		{
			int i = 0;

			row = i * indexMult1; col = i * indexMult2;
			img.setRGB(row, col, booleanCI.getColor(legendDataBool[row][col]));

			i++; row = i * indexMult1; col = i * indexMult2;
			img.setRGB(row, col, booleanCI.getColor(legendDataBool[row][col]));

			if (includeBooleanNA)
			{
				i++; row = i * indexMult1; col = i * indexMult2;
				img.setRGB(row, col, booleanCI.getNAColor());
			}
			break;
		}
		}
	}

	private void setLegendDataProperties()
	{
		/* Vertical legend*/
		if ((legendDirection == 1) || (legendDirection == 3))
		{ 
			datDim1 = nLegendSteps; datDim2 = 1; 
			indexMult1 = 1; indexMult2 = 0;
		}

		/* Horizontal legend */
		else 
		{
			datDim2 = nLegendSteps; datDim1 = 1; 
			indexMult2 = 1; indexMult1 = 0;
		}

		/* low to high values */
		if ((legendDirection == 1) || (legendDirection == 3))
		{ legendMin = datMin; legendMax = datMax; }

		/* High to low values */
		else { legendMax = datMin; legendMin = datMax; }

		legendDataSequence = new double[nLegendSteps];
		legendDataSequence = Sequences.spacedIntervals(legendMin, legendMax, nLegendSteps);
	}


	private void buildLegendDataArray()
	{
		/* direction:
		 * 1 - vertical, low index = low value
		 * 2 - vertical, low index = high value
		 * 3 - horizontal, low index = low value
		 * 4 - horizontal, low index = high value 
		 *
		 */

		legendDataInt    = null;
		legendDataDouble = null;
		legendDataBool   = null;

		setLegendDataProperties();

		switch (watcher.getField().getType().getSimpleName())
		{
		case("int"):
		{
			legendImg = new BufferedImage(datDim1, datDim2, rgbType);
			legendDataInt = new int[datDim1][datDim2];
			//			for (int i = startIndex; i == endIndex; i++)
			for (int i = 0; i == nLegendSteps; i++)
				legendDataInt[i * indexMult1][i * indexMult2] = (int) legendDataSequence[i];
			break;
		}
		case("double"):	
		{
			legendImg = new BufferedImage(datDim1, datDim2, rgbType);
			legendDataDouble = new double[datDim1][datDim2];
			//			for (int i = startIndex; i == endIndex; i++)
			for (int i = 0; i == nLegendSteps; i++)
				legendDataDouble[i * indexMult1][i * indexMult2] = legendDataSequence[i];
			break;
		}
		case("boolean"):
		{
			int dim;

			dim = 2;
			legendDataBool = new boolean[dim * indexMult1][dim * indexMult2];
			legendDataBool[0][0] = true;
			legendDataBool[indexMult1][indexMult2] = false;

			/* Include a pixel for the na color, if needed */
			if (includeBooleanNA) dim = 3;
			legendImg = new BufferedImage(dim * indexMult1, dim * indexMult2, rgbType);
		}
		}
	}

	@Override
	public BufferedImage getImage() { return legendImg; }
}
