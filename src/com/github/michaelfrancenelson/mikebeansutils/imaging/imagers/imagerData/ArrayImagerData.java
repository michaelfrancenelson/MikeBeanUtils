package com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.imagerData;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.michaelfrancenelson.mikebeansutils.beans.memberState.FieldWatcher;
import com.github.michaelfrancenelson.mikebeansutils.imaging.colorInterpolator.ColorInterpolator;
import com.github.michaelfrancenelson.mikebeansutils.utils.ArrayUtils;

public class ArrayImagerData<T> implements ImagerData<T>
{
	static Logger logger = LoggerFactory.getLogger(ArrayImagerData.class);
	protected int naInt = Integer.MIN_VALUE;
	protected boolean isInt;
	private T[][] arrayData;

	protected T currentObj;
	protected int outputWidth, outputHeight, dataWidth, dataHeight, dataX, dataY;
	protected boolean invertX, invertY, transpose;
	protected double dataMin, dataMax;
	protected int datIntMin;
	protected int datIntMax;

	public ArrayImagerData() {}

	public ArrayImagerData(T[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		this.arrayData = dat; 
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}

	@Override public T getDataT(int x, int y)
	{
		return arrayData[x][y];
	}

	@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
	{
		dataMin = Double.MAX_VALUE;
		dataMax = -dataMin;

		isInt = false;
		T t = getDataT(0, 0);
		String simpleName = w.getField().getType().getSimpleName();
		if (simpleName.equals("int")) isInt = true;
		if (simpleName.equals("Integer")) isInt = true;

		//		logger.trace("simple name: " + simpleName);
		//		logger.trace("Is int? " + isInt);

		if (t.getClass().equals(Boolean.class))
		{
			dataMin = 0; dataMax = 1;
		}
		else if (isInt)
		{

			int intMin = Integer.MAX_VALUE;
			int intMax = Integer.MIN_VALUE;
			for (int i = 0; i < dataWidth; i++)
				for (int j = 0; j < dataHeight; j++)
				{

					int val = w.getIntVal(getDataT(i, j));
					if (val != naInt)
					{
						if (val < intMin) intMin = val;
						if (val > intMax) intMax = val;
					}
				}
			logger.trace("int data min: " + intMin + " int data max: " + intMax);
			ci.updateMinMax(intMin, intMax);
			datIntMin = intMin; datIntMax = intMax;
		}
		else
		{
			for (int i = 0; i < dataWidth; i++)
				for (int j = 0; j < dataHeight; j++)
				{
					double val = w.getDoubleVal(getDataT(i, j));
					if (val != -Double.MAX_VALUE)
					{
						if (val < dataMin) dataMin = val;
						if (val > dataMax) dataMax = val;
					}
				}
			ci.updateMinMax(dataMin, dataMax);
		}
		logger.trace(String.format("Data min/max = (%.2f, %.2f)", dataMin, dataMax));
	}

	protected void setDataCoords(double relativeX, double relativeY)
	{
		logger.trace(String.format("Setting data coordinates for relative positions = %.2f, %.2f" , relativeX, relativeY));
		int x = ArrayUtils.relToAbsCoord(relativeX, outputWidth);
		int y = ArrayUtils.relToAbsCoord(relativeY, outputHeight);
		setDataCoords(x, y);
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

		dataX = Math.max(0, Math.min(dataWidth - 1, dataX));
		dataY = Math.max(0, Math.min(dataHeight - 1, dataY));
		
		logger.trace(String.format("Input coords: (%d, %d) data coords: (%d, %d)", inputX, inputY, dataX, dataY));
		setCurrentObj();
	}

	protected void setCurrentObj() { currentObj = arrayData[dataX][dataY]; }

	@Override
	public int getRGBInt(double relativeX, double relativeY, ColorInterpolator ci, FieldWatcher<T> w) 
	{
		setDataCoords(relativeX, relativeY);
		if (isInt)
		{
			int val = w.getIntVal(currentObj);
			if (val == naInt) return ci.getNAColor();
			return ci.getColor(val);
		}
		return ci.getColor(w.getDoubleVal(currentObj));
	}

	@Override
	public int getRGBInt(int x, int y, ColorInterpolator ci, FieldWatcher<T> w) 
	{
		setDataCoords(x, y);
		if (isInt)
		{
			int val = w.getIntVal(currentObj);
			if (val == naInt) return ci.getNAColor();
			return ci.getColor(val);
		}
		return ci.getColor(w.getDoubleVal(currentObj));
	}

	@Override
	public String queryData(double relativeX, double relativeY, FieldWatcher<T> w, String intFmt, String dblFmt,
			String strFmt) {
		if (intFmt == null) intFmt = "%d";
		if (dblFmt == null) dblFmt = "%f";
		if (strFmt == null) strFmt = "%s";

		setDataCoords(relativeX, relativeY);
		if (isInt) if (w.getIntVal(currentObj) == Integer.MIN_VALUE) return "NA";
		return w.getFormattedStringVal(currentObj, intFmt, dblFmt, strFmt);
	}

	@Override
	public String queryData(int x, int y, FieldWatcher<T> w, String intFmt, String dblFmt, String strFmt) {
		setDataCoords(x, y);
		if (isInt) if (w.getIntVal(currentObj) == Integer.MIN_VALUE) return "NA";

		return w.getFormattedStringVal(currentObj, intFmt, dblFmt, strFmt);
	}

	@Override public double getDataMin() { return dataMin; }
	@Override public double getDataMax() { return dataMax; }
	@Override public int getWidth() { return outputWidth; }
	@Override public int getHeight() { return outputHeight; }

	@Override public String getType() { return arrayData.getClass().getComponentType().getSimpleName(); }

	public static class ListImagerData<T> extends ArrayImagerData<T>
	{
		private List<List<T>> listData;
		public ListImagerData(List<List<T>> dat, boolean flipX, boolean flipY, boolean transpose)
		{ 
			this.listData = dat; 
			setDims(listData.size(), listData.get(0).size(), flipX, flipY, transpose);
		}

		@Override public T getDataT(int x, int y) { return listData.get(x).get(y); }

		//		@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
		//		{
		//			dataMin = Double.MAX_VALUE;
		//			dataMax = -dataMin;
		//			
		//			T t = listData.get(0).get(0);
		//
		//			String simpleName = w.getField().getType().getSimpleName();
		//			if (simpleName.equals("int")) isInt = true;
		//			if (simpleName.equals("Integer")) isInt = true;
		//
		//			logger.trace("simple name: " + simpleName);
		//			logger.trace("Is int? " + isInt);
		//
		//			if (t.getClass().equals(Boolean.class))
		//			{
		//				dataMin = 0; dataMax = 1;
		//			}
		//			else if (isInt)
		//			{
		//
		//				int intMin = Integer.MAX_VALUE;
		//				int intMax = Integer.MIN_VALUE;
		//				for (int i = 0; i < dataWidth; i++)
		//					for (int j = 0; j < dataHeight; j++)
		//					{
		//						int val = w.getIntVal(listData.get(i).get(j));
		//						if (val != naInt)
		//						{
		//							if (val < intMin) intMin = val;
		//							if (val > intMax) intMax = val;
		//						}
		//					}
		//				Main.logger.trace("int data min: " + intMin + " int data max: " + intMax);
		//				ci.updateMinMax(intMin, intMax);
		//			}
		//			else
		//			{
		//				for (int i = 0; i < dataWidth; i++)
		//					for (int j = 0; j < dataHeight; j++)
		//					{
		//						double val = w.getDoubleVal(listData.get(i).get(j));
		//						if (val != -Double.MAX_VALUE)
		//						{
		//							if (val < dataMin) dataMin = val;
		//							if (val > dataMax) dataMax = val;
		//						}
		//					}
		//				ci.updateMinMax(dataMin, dataMax);
		//			}
		//			logger.trace(String.format("Data min/max = (%.2f, %.2f)", dataMin, dataMax));
		//		}

		//		@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
		//		{
		//			dataMin = Double.MAX_VALUE;
		//			dataMax = -dataMin;
		//
		//			for (int i = 0; i < dataWidth; i++)
		//				for (int j = 0; j < dataHeight; j++)
		//				{
		//					double val = w.getDoubleVal(listData.get(i).get(j));
		//					if (val != -Double.MAX_VALUE)
		//					{
		//						if (val < dataMin) dataMin = val;
		//						if (val > dataMax) dataMax = val;
		//					}
		//				}
		//			ci.updateMinMax(dataMin, dataMax);
		//			logger.trace(String.format("Data min/max = (%.2f, %.2f)", dataMin, dataMax));
		//		}

		@Override public String getType() { return  listData.get(0).get(0).getClass().getSimpleName(); }
		@Override protected void setCurrentObj() { currentObj = listData.get(dataX).get(dataY); }
	}



	//	public PrimitiveImagerData<T> getLegendData(int nSteps, boolean loToHi, boolean horiz);
	//	{
	//		
	//		/* Java's image origin coordinate is at the upper left of the screen.
	//		 * For vertical legends, high y-coordinates appear lower on the screen
	//		 */
	//		
	//		PrimitiveImagerData<T> legDat;
	//		String type = getFieldType();
	//
	//		ImagerData<T> dat = getImagerData();
	//
	//		double min = dat.getDataMin();
	//		double max = dat.getDataMax();
	//		
	//		double endpoint1, endpoint2;
	//		
	//		if (!horiz) loToHi = !loToHi;
	//		if (loToHi) { endpoint1 = min; endpoint2 = max; }
	//		else {endpoint1 = max; endpoint2 = min; }
	//		
	//		if (getParsedBooleanFields().contains(getFieldName().toLowerCase())) {
	//			legDat = PrimitiveImagerData.buildGradientData(
	//					"Boolean", 
	//					endpoint1, endpoint2,
	//					nSteps, horiz,
	//					true);
	//			legDat.setAsBoolean(true);
	//		}
	//		else
	//			legDat = PrimitiveImagerData.buildGradientData(
	//					type,
	//					endpoint1, endpoint2,
	//					nSteps, horiz, 
	//					false);
	//
	//		legDat.setDataMinMax(null, null);
	//		return legDat;
	//	}
	@Override
	public PrimitiveImagerData<?> getLegendData(
			int nSteps, boolean loToHi, 
			boolean horiz, boolean parsedBoolean, String type) 
	{

		/* Java's image origin coordinate is at the upper left of the screen.
		 * For vertical legends, high y-coordinates appear lower on the screen
		 */

		PrimitiveImagerData<T> legDat;

		double endpoint1, endpoint2;

		if (!horiz) loToHi = !loToHi;
		if (loToHi) { endpoint1 = getDataMin(); endpoint2 = getDataMax(); }
		else {endpoint1 = getDataMax(); endpoint2 = getDataMin(); }

		if (parsedBoolean)
		{
			legDat = PrimitiveImagerData.buildGradientData(
					"Boolean", 
					endpoint1, endpoint2,
					nSteps, horiz,
					true);
			legDat.setAsBoolean(true);
		}
		else if (type.equals("int") || type.toLowerCase().equals("integer"))
		{
			int i1 = getDataIntMin();
			int i2 = getDataIntMax();

			if (!horiz) loToHi = !loToHi;
			if (loToHi) { i1 = datIntMin; i2 = datIntMax; }
			else { i2 = datIntMin; i1 = datIntMax; }

			legDat = PrimitiveImagerData.buildGradientData("int", i1, i2, nSteps, horiz, false);
		}
		else {
			legDat = PrimitiveImagerData.buildGradientData(
					type,
					endpoint1, endpoint2,
					nSteps, horiz, 
					false);
		}
		legDat.setDataMinMax(null, null);
		return legDat;

	}

	@Override public int getDataIntMin() { return datIntMin; }
	@Override public int getDataIntMax() { return datIntMax; }
}