package imaging.imagers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import utils.ArrayUtils;
import utils.Sequences;

public class ArrayData<T> implements ImagerData<T>
{
	static Logger logger = LoggerFactory.getLogger(ArrayData.class);
	
	private T[][] arrayData;
	protected T currentObj;
	protected int outputWidth, outputHeight;
	protected int dataWidth, dataHeight;
	protected boolean invertX, invertY, transpose;
	protected int dataX, dataY;
	protected double dataMin, dataMax;

	public ArrayData() {}

	public ArrayData(T[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		this.arrayData = dat; 
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}

	protected void setDims(int dataWidth, int dataHeight, boolean flipX, boolean flipY, boolean transpose)
	{
		this.dataWidth = dataWidth; 
		this.dataHeight = dataHeight;

		this.invertX = flipX;
		this.invertY = flipY;
		this.transpose = transpose;

		if (this.transpose) { outputWidth = dataWidth; outputHeight = dataWidth; }
		else { outputWidth = dataWidth; outputHeight = dataHeight; }

	}

	@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
	{
		dataMin = Double.MAX_VALUE;
		dataMax = Double.MIN_VALUE;

//		if (ci instanceof SimpleBooleanColorInterpolator)
			if (arrayData[0][0].getClass().equals(Boolean.class))
		{
			dataMin = 0; dataMax = 1;
			
		}
		else {
			
		for (int i = 0; i < dataWidth; i++)
			for (int j = 0; j < dataHeight; j++)
			{
				double val = w.getDoubleVal(arrayData[i][j]);
				if (val < dataMin) dataMin = val;
				if (val > dataMax) dataMax = val;
			}
		ci.updateMinMax(dataMin, dataMax);
		}
		logger.trace(String.format("Data min/max = (%.2f, %.2f)", dataMin, dataMax));
		
	}

	protected void setDataCoords(double relativeX, double relativeY)
	{
		logger.trace(String.format("Setting data coordinates for relative positions = %.2f, %.2f" , relativeX, relativeY));
		setDataCoords(
				ArrayUtils.relToAbsCoord(relativeX, outputWidth),
				ArrayUtils.relToAbsCoord(relativeY, outputHeight)
				);
	}

	protected void setDataCoords(int inputX, int inputY)
	{
		if (invertX) dataX = outputWidth - inputX - 1;
		else dataX = inputX;
		if (invertY) dataY = outputHeight - inputY - 1;
		else dataY = inputY;

		if (transpose)
		{
			int t = dataX; dataX = dataY; dataY = t;
		}
		
		logger.trace(String.format("Input coords: (%d, %d) data coords: (%d, %d)",
				inputX, inputY, dataX, dataY));
		
		setCurrentObj();
	}

	protected void setCurrentObj() { currentObj = arrayData[dataX][dataY]; }

	@Override
	public int getRGBInt(
			double relativeX, double relativeY,
			ColorInterpolator ci, FieldWatcher<T> w) 
	{
		setDataCoords(relativeX, relativeY);
		return ci.getColor(w.getDoubleVal(currentObj));
	}

	@Override
	public int getRGBInt(
			int x, int y,
			ColorInterpolator ci, FieldWatcher<T> w) 
	{
		setDataCoords(x, y);
		return ci.getColor(w.getDoubleVal(currentObj));
	}


	@Override
	public String queryData(double relativeX, double relativeY, FieldWatcher<T> w) {
		setDataCoords(relativeX, relativeY);
		String out =  w.getStringVal(currentObj);
		logger.trace(String.format("Querying object at relative coords: %.2f, %.2f "
				+ "field %s with value %s", relativeX, relativeY, w.getFieldName(), out));
		return out;
	}

	@Override
	public String queryData(int x, int y, FieldWatcher<T> w) {
		setDataCoords(x, y);
		return w.getStringVal(currentObj);
	}

	@Override public double getDataMin() { return dataMin; }
	@Override public double getDataMax() { return dataMax; }
	@Override public int getWidth() { return outputWidth; }
	@Override public int getHeight() { return outputHeight; }

	public static class ListData<T> extends ArrayData<T>
	{
		private List<List<T>> listData;
		public ListData(List<List<T>> dat, boolean flipX, boolean flipY, boolean transpose)
		{ 
			this.listData = dat; 
			setDims(dat.size(), dat.get(0).size(), flipX, flipY, transpose);
		}

		@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
		{
			dataMin = Double.MAX_VALUE;
			dataMax = Double.MIN_VALUE;

			for (int i = 0; i < dataWidth; i++)
				for (int j = 0; j < dataHeight; j++)
				{
					double val = w.getDoubleVal(listData.get(i).get(j));
					if (val < dataMin) dataMin = val;
					if (val > dataMax) dataMax = val;
				}
			ci.updateMinMax(dataMin, dataMax);
			logger.debug(String.format("Data min/max = (%.2f, %.2f)", dataMin, dataMax));
		}

		@Override protected void setCurrentObj() { currentObj = listData.get(dataX).get(dataY); }

	}

	@Override
	public PrimitiveArrayData<Object> getIntLegend(
//			public ImagerData<Object> getIntLegend(
			int nSteps, boolean loToHi, boolean horiz) 
	{
		int dataWidth = (int)Math.abs(dataMax - dataMin);
		int legMax, legMin;

		if ((dataMax > dataMin) & loToHi) {legMax = (int) dataMax; legMin = (int) dataMin; }
		else {legMax = (int) dataMin; legMin = (int) dataMin; }

		if (dataWidth < nSteps) nSteps = dataWidth;
		int[][] data = Sequences.spacedIntervals2D(legMin, legMax, nSteps, horiz);
		return new PrimitiveArrayData<Object>(data, false, false, false);
	}
	
	@Override
	public PrimitiveArrayData<Object> getDoubleLegend(
//			public ImagerData<Object> getDoubleLegend(
			int nSteps, boolean loToHi, boolean horiz)
	{
		double legMax, legMin;

		if ((dataMax > dataMin) & loToHi) {legMax = dataMax; legMin = dataMin; }
		else {legMax = dataMin; legMin = dataMin; }
double[][] data = Sequences.spacedIntervals2D(legMin, legMax, nSteps, horiz);
return new PrimitiveArrayData<Object>(data, false, false, false);
	}

	@Override
	public PrimitiveArrayData<Object> getByteLegend(
//			public ImagerData<Object> getByteLegend(
			int nSteps, boolean loToHi, boolean horiz) 
	{

		int dataWidth = (int)Math.abs(dataMax - dataMin);
		byte legMax, legMin;

		if ((dataMax > dataMin) & loToHi) {legMax = (byte) (int) dataMax; legMin = (byte) (int) dataMin; }
		else {legMax = (byte) (int) dataMin; legMin = (byte) (int) dataMin; }

		if (dataWidth < nSteps) nSteps = dataWidth;
		byte[][] data = Sequences.spacedIntervals2D(
				legMin, legMax, nSteps, horiz);
		return new PrimitiveArrayData<Object>(data, false, false, false);
	}
	
	@Override
//	public PrimitiveArrayData<Object> getBooleanLegend(boolean includeNA, boolean horizontal)
	public Boolean[][] getBooleanLegendData(boolean includeNA, boolean horizontal)
//	public ImagerData<Boolean> getBooleanLegend(boolean includeNA, boolean horizontal)
	{
		Boolean[][] data = Sequences.booleanGradient2D(includeNA, horizontal);
//		return new PrimitiveArrayData<Object>(data, false, false, false);
//		return new ArrayData<Boolean>(data, false, false, false);
		return data;
	}
	
	
	
	
//	@Override
//	public IntArrayMinMax intLegendData(int nSteps, boolean loToHi, boolean horiz) 
//	{
//		int dataWidth = (int)Math.abs(dataMax - dataMin);
//		int legMax, legMin;
//
//		if ((dataMax > dataMin) & loToHi) {legMax = (int) dataMax; legMin = (int) dataMin; }
//		else {legMax = (int) dataMin; legMin = (int) dataMin; }
//
//		if (dataWidth < nSteps) nSteps = dataWidth;
//		return new IntArrayMinMax(Sequences.spacedIntervals2D(
//				legMin, legMax, nSteps, horiz), legMin, legMax);
//	}
//
//	@Override
//	public DblArrayMinMax dblLegendData(int nSteps, boolean loToHi, boolean horiz)
//	{
//		double legMax, legMin;
//
//		if ((dataMax > dataMin) & loToHi) {legMax = dataMax; legMin = dataMin; }
//		else {legMax = dataMin; legMin = dataMin; }
//
//		return new DblArrayMinMax(Sequences.spacedIntervals2D(
//				legMin, legMax, nSteps, horiz), legMin, legMax);		
//	}
//
//	@Override
//	public ByteArrayMinMax byteLegendData(int nSteps, boolean loToHi, boolean horiz)
//	{
//		int dataWidth = (int)Math.abs(dataMax - dataMin);
//		byte legMax, legMin;
//
//		if ((dataMax > dataMin) & loToHi) {legMax = (byte) (int) dataMax; legMin = (byte) (int) dataMin; }
//		else {legMax = (byte) (int) dataMin; legMin = (byte) (int) dataMin; }
//
//		if (dataWidth < nSteps) nSteps = dataWidth;
//		return new ByteArrayMinMax(Sequences.spacedIntervals2D(
//				legMin, legMax, nSteps, horiz), legMin, legMax);
//	}

}
