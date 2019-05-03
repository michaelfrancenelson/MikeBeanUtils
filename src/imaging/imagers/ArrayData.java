package imaging.imagers;

import java.util.List;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import utils.ArrayUtils;
import utils.Sequences;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;

public class ArrayData<T> implements ImagerData<T>
{
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


	@Override public void setDataMinMax(FieldWatcher<T> w)
	{
		dataMin = Double.MAX_VALUE;
		dataMax = Double.MIN_VALUE;

		for (int i = 0; i < dataWidth; i++)
			for (int j = 0; j < dataHeight; j++)
			{
				double val = w.getDoubleVal(arrayData[i][j]);
				if (val < dataMin) dataMin = val;
				if (val > dataMax) dataMax = val;
			}
	}

	protected void setDataCoords(double relativeX, double relativeY)
	{
		setDataCoords(
				ArrayUtils.getRelativeIndex(relativeX, outputWidth),
				ArrayUtils.getRelativeIndex(relativeY, outputHeight)
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
		return w.getStringVal(currentObj);
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

		@Override public void setDataMinMax(FieldWatcher<T> w)
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
		}

		@Override protected void setCurrentObj() { currentObj = listData.get(dataX).get(dataY); }

	}

	@Override
	public IntArrayMinMax intLegendData(int nSteps, boolean loToHi, boolean horiz) 
	{
		int dataWidth = (int)Math.abs(dataMax - dataMin);
		int legMax, legMin;

		if ((dataMax > dataMin) & loToHi) {legMax = (int) dataMax; legMin = (int) dataMin; }
		else {legMax = (int) dataMin; legMin = (int) dataMin; }

		if (dataWidth < nSteps) nSteps = dataWidth;
		return new IntArrayMinMax(Sequences.spacedIntervals2D(
				legMin, legMax, nSteps, horiz), legMin, legMax);
	}

	@Override
	public DblArrayMinMax dblLegendData(int nSteps, boolean loToHi, boolean horiz)
	{
		double legMax, legMin;

		if ((dataMax > dataMin) & loToHi) {legMax = dataMax; legMin = dataMin; }
		else {legMax = dataMin; legMin = dataMin; }

		return new DblArrayMinMax(Sequences.spacedIntervals2D(
				legMin, legMax, nSteps, horiz), legMin, legMax);		
	}

	@Override
	public ByteArrayMinMax byteLegendData(int nSteps, boolean loToHi, boolean horiz)
	{
		int dataWidth = (int)Math.abs(dataMax - dataMin);
		byte legMax, legMin;

		if ((dataMax > dataMin) & loToHi) {legMax = (byte) (int) dataMax; legMin = (byte) (int) dataMin; }
		else {legMax = (byte) (int) dataMin; legMin = (byte) (int) dataMin; }

		if (dataWidth < nSteps) nSteps = dataWidth;
		return new ByteArrayMinMax(Sequences.spacedIntervals2D(
				legMin, legMax, nSteps, horiz), legMin, legMax);
	}

}
