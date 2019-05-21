package imaging.imagers.imagerData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.Imager;
import utils.ArrayUtils;
import utils.FieldUtils;
import utils.Sequences;

public class PrimitiveImagerData<T> extends ArrayImagerData<T>
{
	public static Logger logger = LoggerFactory.getLogger(PrimitiveImagerData.class);
	
	private String type;
	private boolean asBoolean = false;
	
	@Override public void setDataMinMax(FieldWatcher<T> w, ColorInterpolator ci)
	{
		double[] minmax = null;
		switch(type)
		{
		case("dbl"): minmax =  ArrayUtils.getArrMinMax(dblDat); break;
		case("flt"): minmax =  ArrayUtils.getArrMinMax(fltDat); break;
		case("byt"): minmax =  ArrayUtils.getArrMinMax(bytDat); break;
		case("sht"): minmax =  ArrayUtils.getArrMinMax(shtDat); break;
		case("int"): minmax =  ArrayUtils.getArrMinMax(intDat); break;
		case("lng"): minmax =  ArrayUtils.getArrMinMax(lngDat); break;
		case("chr"): minmax =  ArrayUtils.getArrMinMax(chrDat); break;
		case("boo"): minmax =  ArrayUtils.getArrMinMax(booDat); break;
		default: minmax = new double[] {0, 0};
		}
		dataMin = minmax[0];
		dataMax = minmax[1];
		if (ci != null) ci.updateMinMax(dataMin, dataMax);
	}

	@Override protected void setCurrentObj() {}

	@Override
	public int getRGBInt(int x, int y, ColorInterpolator ci, FieldWatcher<T> w) 
	{
		double val;
		setDataCoords(x, y);
		switch(type)
		{
		case("dbl"): val = dblDat[dataX][dataY]; break;
		case("flt"): val = ArrayUtils.doubleCaster(fltDat[dataX][dataY]); break;
		case("byt"): val = ArrayUtils.doubleCaster(bytDat[dataX][dataY]); break;
		case("sht"): val = ArrayUtils.doubleCaster(shtDat[dataX][dataY]); break;
		case("int"): val = ArrayUtils.doubleCaster(intDat[dataX][dataY]); break;
		case("lng"): val = ArrayUtils.doubleCaster(lngDat[dataX][dataY]); break;
		case("chr"): val = ArrayUtils.doubleCaster(chrDat[dataX][dataY]); break;
		case("boo"): val = ArrayUtils.doubleCaster(booDat[dataX][dataY]); break;
		default: val = Double.MIN_VALUE;
		}
		return ci.getColor(val);
	}

	public String queryData(double relativeX, double relativeY, String dblFmt)
	{
		setDataCoords(relativeX, relativeY);
		String val;
		switch(type)
		{
		case("dbl"): val = ArrayUtils.stringCaster(dblDat[dataX][dataY], dblFmt); break;
		case("flt"): val = ArrayUtils.stringCaster(fltDat[dataX][dataY], dblFmt); break;
		case("byt"): val = ArrayUtils.stringCaster(bytDat[dataX][dataY], dblFmt); break;
		case("sht"): val = ArrayUtils.stringCaster(shtDat[dataX][dataY], dblFmt); break;
		case("int"): val = ArrayUtils.stringCaster(intDat[dataX][dataY], dblFmt); break;
		case("lng"): val = ArrayUtils.stringCaster(lngDat[dataX][dataY], dblFmt); break;
		case("chr"): val = ArrayUtils.stringCaster(chrDat[dataX][dataY], dblFmt); break;
		case("boo"): val = ArrayUtils.stringCaster(booDat[dataX][dataY], dblFmt); break;
		default: val = null;
		}
		if (asBoolean) {
			String val2 = FieldUtils.toBoolean(val);
			logger.debug(String.format("Querying boolean value (from %s = %s) at relative coords: (%.2f, %.2f): %s",
					type, val, 100 * relativeX, 100 * relativeY, val2));
			return val2;
		}
		logger.debug(String.format("Querying %s value at relative coords: (%.0f, %.0f): %s ",
				type, 100 * relativeX, 100 * relativeY, val));
		return val;
	}
	
	public PrimitiveImagerData(double[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{  
		dblDat = dat;  type = "dbl";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(float[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{	
		fltDat = dat;  type = "flt";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(byte[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{
		bytDat = dat; type = "byt";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(short[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		shtDat = dat; type = "sht";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(int[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		intDat = dat; type = "int";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(long[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		lngDat = dat;	type = "lng";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(char[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		chrDat = dat; type = "chr";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(boolean[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		booDat = dat; type = "boo";  
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	
	public PrimitiveImagerData() {}

	private double[][]  dblDat;
	private float[][] fltDat;
	private byte[][]    bytDat; 
	private short[][] shtDat;
	private int[][] intDat; 
	private long[][] lngDat; 
	private char[][] chrDat;
	private boolean[][] booDat;

	public void setAsBoolean(boolean b) { this.asBoolean = b; }

	
	public static <T> PrimitiveImagerData<T> buildGradientData(
			Imager<?> imgr, 
			int nSteps, boolean horizontal, boolean loToHi, boolean includeBoolNA)
	{
		return buildGradientData(imgr.getFieldType(), imgr.getDataMin(), imgr.getDataMax(), nSteps, horizontal, loToHi, includeBoolNA);
	}
	
	public static <T> PrimitiveImagerData<T> buildGradientData(
			String type, double min, double max, 
			int nSteps, boolean horizontal, boolean loToHi, boolean includeBoolNA)
	{
		if (min > max && loToHi) { double t = min; min = max; max = t; }
		else if (min < max && !loToHi) { double t = min; min = max; max = t; }

		/* Integer types can all use integer data under the hood*/
		switch(type.toLowerCase())
		{
		case("int"): case("integer"): case("byte"): case("short"): case("long"): case("char"): case("character"):
		{
			return new PrimitiveImagerData<T>(
					Sequences.spacedIntervals2D((int) min, (int) max, nSteps, horizontal),
					false, false, false);
		}
		case("double"): case("float"): 
		{
			return new PrimitiveImagerData<T>(
					Sequences.spacedIntervals2D((double) min, (double) max, nSteps, horizontal), 
					false, false, false);
		}
		case("boolean"):
		{
			return new PrimitiveImagerData<T>(
					Sequences.spacedIntervals2D(-1, 1, 3, horizontal),
					false, false, false);
		}
		}
		throw new IllegalArgumentException("Could not build a gradient data set for data of type " + type + ".");
	}
}