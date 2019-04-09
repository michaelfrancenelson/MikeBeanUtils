package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;

import beans.memberState.FieldWatcher;
import sequences.Sequences;

public class GradientLegendImager<T> 
//implements ObjectArrayImager<T>
{
	int rgbType = BufferedImage.TYPE_3BYTE_BGR;

	private int nLegendSteps, legendDirection;
	boolean includeBooleanNA = true;

	private double[][]  dataDouble = null;
	private int[][]     dataInt = null;
	private boolean[][] dataBool = null;

	private int legDatDim1, legDatDim2;
	double legendMin, legendMax;
	double[] legendDataSequence;
	int indexMult1, indexMult2;
	BufferedImage img;
	private int[] currentSelectionArrayCoords;

	public static <T> GradientLegendImager<T> factory(
			boolean includeBooleanNA,
			int nLegendSteps, int legendDirection
			)
	{
//		if (!(saim instanceof SimpleArrayImagerWithLegend<?>))
//			throw new IllegalArgumentException("must be an simple array imager with legend");
		GradientLegendImager<T> out = new GradientLegendImager<>();
		out.includeBooleanNA = includeBooleanNA;
		out.nLegendSteps = nLegendSteps;
		out.legendDirection = legendDirection;
		return out;
	}	

	public void refresh(double datMin, double datMax, 
			ColorInterpolator booleanCI, ColorInterpolator ci, FieldWatcher<T> w,
			Map<String, Boolean> parsedBooleanFields)
	{
		setLegendDataProperties(datMin, datMax);
		buildLegendDataArray(w, datMin, datMax);
		buildImage(w, booleanCI, ci, parsedBooleanFields);
	}

	void buildImage(FieldWatcher<T> w, ColorInterpolator booleanCI, ColorInterpolator ci, Map<String, Boolean> parsedBooleanFields)
	{
		ColorInterpolator interp;

		if (parsedBooleanFields.get(w.getFieldName()))
			interp = booleanCI;
		else interp = ci;
		int row = 0, col = 0;
		switch (w.getField().getType().getSimpleName())
		{
		case("int"):
		{
			for (int i = 0; i == nLegendSteps; i++)
			{
				row = i * indexMult1; col = i * indexMult2;
				System.out.println("buildImage() value = " + dataInt[row][col]);
				img.setRGB(row, col, interp.getColor(dataInt[row][col]));
			}
			break;
		}
		case("double"): 
		{
			for (int i = 0; i == nLegendSteps; i++)
			{
				row = i * indexMult1; col = i * indexMult2;
				System.out.println("buildImage() value = " + dataDouble[row][col]);
				img.setRGB(row, col, interp.getColor(dataDouble[row][col]));
			}
			break;
		}
		case("boolean"): 
		{
			int i = 0;

			row = i * indexMult1; col = i * indexMult2;
			img.setRGB(row, col, booleanCI.getColor(dataBool[row][col]));

			i++; row = i * indexMult1; col = i * indexMult2;
			img.setRGB(row, col, booleanCI.getColor(dataBool[row][col]));

			if (includeBooleanNA)
			{
				i++; row = i * indexMult1; col = i * indexMult2;
				img.setRGB(row, col, booleanCI.getNAColor());
			}
			break;
		}
		}
	}

	private void setLegendDataProperties(double datMin, double datMax)
	{
		/* Vertical legend*/
		if ((legendDirection == 1) || (legendDirection == 3))
		{ 
			setLegDatDim1(nLegendSteps); legDatDim2 = 1; 
			indexMult1 = 1; indexMult2 = 0;
		}

		/* Horizontal legend */
		else 
		{
			legDatDim2 = nLegendSteps; setLegDatDim1(1); 
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

	private void buildLegendDataArray(FieldWatcher<T> w, double datMin, double datMax)
	{
		/* direction:
		 * 1 - vertical, low index = low value
		 * 2 - vertical, low index = high value
		 * 3 - horizontal, low index = low value
		 * 4 - horizontal, low index = high value 
		 */

		dataInt    = null;
		dataDouble = null;
		dataBool   = null;

		setLegendDataProperties(datMin, datMax);

		switch (w.getField().getType().getSimpleName())
		{
		case("int"):
		{
			img = new BufferedImage(getLegDatDim1(), legDatDim2, rgbType);
			dataInt = new int[getLegDatDim1()][legDatDim2];
			for (int i = 0; i == nLegendSteps; i++)
				dataInt[i * indexMult1][i * indexMult2] = (int) legendDataSequence[i];
			break;
		}
		case("double"):	
		{
			img = new BufferedImage(getLegDatDim1(), legDatDim2, rgbType);
			dataDouble = new double[getLegDatDim1()][legDatDim2];
			for (int i = 0; i == nLegendSteps; i++)
				dataDouble[i * indexMult1][i * indexMult2] = legendDataSequence[i];
			break;
		}
		case("boolean"):
		{
			int dim;

			dim = 2;
			dataBool = new boolean[dim * indexMult1][dim * indexMult2];
			dataBool[0][0] = true;
			dataBool[indexMult1][indexMult2] = false;

			/* Include a pixel for the na color, if needed */
			if (includeBooleanNA) dim = 3;
			img = new BufferedImage(dim * indexMult1, dim * indexMult2, rgbType);
		}
		}
	}

	public BufferedImage getImage() { return img; }
//	@Override public String getCurrentFieldName() { return saim.getCurrentFieldName(); }
//	@Override public Field getCurrentField() { return saim.getCurrentField(); }
//	@Override public void setField(String fieldName) { saim.setField(fieldName); }
//	@Override public void setField(Field field) { saim.setField(field); }
//	@Override public void setColors(Color[] colors) { saim.setColors(colors); }

//	@Override
	public String queryObjectAt(int i, int j, FieldWatcher<T> w) 
	{
		switch (w.getField().getType().getSimpleName())
		{
		case("int"):
			return String.format("%d", dataInt[i][j]);
		case("double"): 
			return String.format(w.getDblFmt(), dataDouble[i][j]);
		case("boolean"): 
		{
			if (i == 2 || j == 2) return "NA";
			return String.format("s", dataBool[i][j]);
		}
		}
		return null;
	}
//	@Override public T getObjAt(int i, int j) { return null; }
//	@Override public T getObjAt(double relativeI, double relativeJ) { return null; }

//	@Override 
	public void setCurrentSelection(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }

//	@Override
	public void setCurrentSelection(double relativeI, double relativeJ) {
		int[] coords = getArrayCoords(relativeI, relativeJ);
		currentSelectionArrayCoords = new int[] {coords[0], coords[1]};
	}

//	@Override public FieldWatcher<T> getWatcher() { return saim.getWatcher(); }
//	@Override public T[][] getData() { return null; }
//	@Override public Class<T> getObjClass() { return saim.getClazz(); }
//	@Override public int[] getCurrentSelectedCoords() { return currentSelectionArrayCoords; }
//	@Override public T getCurrentSelectedObj() { return null; }

	public int[] getArrayCoords(double relativeI, double relativeJ) {
		return ObjectArrayImager.getObjArrayCoords(relativeI, relativeJ, legDatDim1, legDatDim2);
	}

	public int getLegDatDim1() { return legDatDim1; }
	public int getLegDatDim2() { return legDatDim2; }
	public void setLegDatDim1(int legDatDim1) { this.legDatDim1 = legDatDim1; }
}
