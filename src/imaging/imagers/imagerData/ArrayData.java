package imaging.imagers.imagerData;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import utils.ArrayUtils;

public class ArrayData<T> implements ImagerData<T>
{
	static Logger logger = LoggerFactory.getLogger(ArrayData.class);

	private T[][] arrayData;

	protected T currentObj;
	protected int outputWidth, outputHeight, dataWidth, dataHeight, dataX, dataY;
	protected boolean invertX, invertY, transpose;
	protected double dataMin, dataMax;

	public ArrayData() {}

	public ArrayData(T[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		this.arrayData = dat; 
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}

	@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
	{
		dataMin = Double.MAX_VALUE;
		dataMax = Double.MIN_VALUE;

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

	protected void setDims(int dataWidth, int dataHeight, boolean flipX, boolean flipY, boolean transpose)
	{
		this.dataWidth = dataWidth; this.dataHeight = dataHeight;

		this.invertX = flipX; this.invertY = flipY; this.transpose = transpose;

		if (this.transpose) { outputWidth = dataHeight; outputHeight = dataWidth; }
		else { outputWidth = dataWidth; outputHeight = dataHeight; }
	}

	protected void setDataCoords(int inputX, int inputY)
	{
		if (!transpose)
		{
			if (invertX) dataX = dataWidth - inputX - 1;
			else dataX = inputX;

			if (invertY) dataY = dataHeight - inputY - 1;
			else dataY = inputY;
		}

		if (transpose)
		{
			if (invertX) dataX = dataWidth - inputY - 1;
			else dataX = inputY;

			if (invertY) dataY = dataHeight - inputX - 1;
			else dataY = inputX;
		}

		logger.trace(String.format("Input coords: (%d, %d) data coords: (%d, %d)",
				inputX, inputY, dataX, dataY));
		setCurrentObj();
	}

	protected void setCurrentObj() { currentObj = arrayData[dataX][dataY]; }

	@Override
	public int getRGBInt(double relativeX, double relativeY, ColorInterpolator ci, FieldWatcher<T> w) 
	{
		setDataCoords(relativeX, relativeY);
		return ci.getColor(w.getDoubleVal(currentObj));
	}

	@Override
	public int getRGBInt(int x, int y, ColorInterpolator ci, FieldWatcher<T> w) 
	{
		setDataCoords(x, y);
		return ci.getColor(w.getDoubleVal(currentObj));
	}

	@Override
	public String queryData(double relativeX, double relativeY, FieldWatcher<T> w) 
	{
		setDataCoords(relativeX, relativeY);
		String out =  w.getStringVal(currentObj);
		logger.trace(String.format("Querying object at relative coords: %.2f, %.2f "
				+ "field %s with value %s", relativeX, relativeY, w.getFieldName(), out));
		return out;
	}

	@Override
	public String queryData(int x, int y, FieldWatcher<T> w) 
	{
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
			setDims(listData.size(), listData.get(0).size(), flipX, flipY, transpose);
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
			logger.trace(String.format("Data min/max = (%.2f, %.2f)", dataMin, dataMax));
		}

		@Override protected void setCurrentObj() { currentObj = listData.get(dataX).get(dataY); }
	}
}