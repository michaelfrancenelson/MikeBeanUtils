package imaging.imagers.imagerData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
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
		case("double"): minmax =  ArrayUtils.getArrMinMax(dblDat); break;
		case("float"): minmax =  ArrayUtils.getArrMinMax(fltDat); break;
		case("byte"): minmax =  ArrayUtils.getArrMinMax(bytDat); break;
		case("short"): minmax =  ArrayUtils.getArrMinMax(shtDat); break;
		case("int"): minmax =  ArrayUtils.getArrMinMax(intDat); break;
		case("long"): minmax =  ArrayUtils.getArrMinMax(lngDat); break;
		case("char"): minmax =  ArrayUtils.getArrMinMax(chrDat); break;
		case("boolean"): minmax =  ArrayUtils.getArrMinMax(booDat); break;
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
		case("double"): val = dblDat[dataX][dataY]; break;
		case("float"): val = ArrayUtils.doubleCaster(fltDat[dataX][dataY]); break;
		case("byte"): val = ArrayUtils.doubleCaster(bytDat[dataX][dataY]); break;
		case("short"): val = ArrayUtils.doubleCaster(shtDat[dataX][dataY]); break;
		case("int"): val = ArrayUtils.doubleCaster(intDat[dataX][dataY]); break;
		case("long"): val = ArrayUtils.doubleCaster(lngDat[dataX][dataY]); break;
		case("char"): val = ArrayUtils.doubleCaster(chrDat[dataX][dataY]); break;
		case("boolean"): val = ArrayUtils.doubleCaster(booDat[dataX][dataY]); break;
		case("Boolean"): val = ArrayUtils.doubleCaster(booleanDat[dataX][dataY]); break;
		default: val = Double.MIN_VALUE;
		}
		return ci.getColor(val);
	}

	public String queryData(double relativeX, double relativeY)
	{
		return (queryData(relativeX, relativeY, "%d", "%f", "%s"));
	}
	
	public String queryData(double relativeX, double relativeY, String intFmt, String dblFmt, String strFmt)
	{
		if (intFmt == null) intFmt = "%d";
		if (dblFmt == null) dblFmt = "%f";
		if (strFmt == null) strFmt = "%s";

		setDataCoords(relativeX, relativeY);
		String val;
		switch(type)
		{
		case("double"): val = ArrayUtils.stringCaster(dblDat[dataX][dataY], dblFmt); break;
		case("float"): val = ArrayUtils.stringCaster(fltDat[dataX][dataY], dblFmt); break;
		case("byte"): val = ArrayUtils.stringCaster(bytDat[dataX][dataY], intFmt); break;
		case("short"): val = ArrayUtils.stringCaster(shtDat[dataX][dataY], intFmt); break;
		case("int"): val = ArrayUtils.stringCaster(intDat[dataX][dataY], intFmt); break;
		case("long"): val = ArrayUtils.stringCaster(lngDat[dataX][dataY], intFmt); break;
		case("char"): val = ArrayUtils.stringCaster(chrDat[dataX][dataY], strFmt); break;
		case("boolean"): val = ArrayUtils.stringCaster(booDat[dataX][dataY], strFmt); break;
		case("Boolean"): val = ArrayUtils.stringCaster(booleanDat[dataX][dataY], strFmt); break;
		default: val = null;
		}
		if (asBoolean) {
			String val2 = FieldUtils.toBoolean(val);
			logger.debug(String.format("Querying boolean value (from %s = %s) at relative coords: (%.2f, %.2f): %s",
					type, val, 100 * relativeX, 100 * relativeY, val2));
			return val2;
		}
		logger.trace(String.format("Querying %s value at relative coords: (%.0f%%, %.0f%%): %s ", type, 100 * relativeX, 100 * relativeY, val));
		return val;
	}
	
	public PrimitiveImagerData(double[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{  
		dblDat = dat;  type = "double";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(float[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{	
		fltDat = dat;  type = "float";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(byte[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{
		bytDat = dat; type = "byte";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(short[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		shtDat = dat; type = "short";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(int[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		intDat = dat; type = "int";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(long[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		lngDat = dat;	type = "long";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(char[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		chrDat = dat; type = "char";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	public PrimitiveImagerData(boolean[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		booDat = dat; type = "boolean";  
		setDims(dat.length, dat[0].length, flipX, flipY, transpose); setDataMinMax(null, null);
	}
	
	public PrimitiveImagerData(Boolean[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		booleanDat = dat; type = "Boolean";  
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
	private Boolean[][] booleanDat;

	public void setAsBoolean(boolean b) { this.asBoolean = b; }

	
//	public static <T> PrimitiveImagerData<T> buildGradientData(
//			Imager<?> imgr, 
//			int nSteps, boolean horizontal, 
////			boolean loToHi, 
//			boolean includeBoolNA)
//	{
//		return buildGradientData(imgr.getFieldType(), imgr.getDataMin(), imgr.getDataMax(), nSteps, horizontal, loToHi, includeBoolNA);
//	}
	
	
	
	public static <T> PrimitiveImagerData<T> buildGradientData(
			String type, double endpoint1, double endpoint2, 
			int nSteps, boolean horizontal, 
//			boolean loToHi, 
			boolean includeBoolNA)
	{
		
//		if (!horizontal)
//		{
//			
//		}
//		if (endpoint1 > endpoint2 && loToHi) 
//		{ double t = endpoint1; endpoint1 = endpoint2; endpoint2 = t; }
//		else if (endpoint1 < endpoint2 && !loToHi) 
//		{ double t = endpoint1; endpoint1 = endpoint2; endpoint2 = t; }
//
//		
		
		
		/* Integer types can all use int primitives under the hood*/
		switch(type.toLowerCase())
		{
		case("int"): case("integer"): case("byte"): case("short"): case("long"): case("char"): case("character"):
		{
			return new PrimitiveImagerData<T>(
					Sequences.spacedIntervals2D((int) endpoint1, (int) endpoint2, nSteps, horizontal),
					false, false, false);
		}
		case("double"): case("float"): 
		{
			return new PrimitiveImagerData<T>(
					Sequences.spacedIntervals2D((double) endpoint1, (double) endpoint2, nSteps, horizontal), 
					false, false, false);
		}
		case("Boolean"): case("boolean"):
		{
			return new PrimitiveImagerData<T>(
					Sequences.booleanGradient2D(includeBoolNA, horizontal),
//					Sequences.booleanGradient2D(includeBoolNA, horizontal, loToHi),
					false, false, false);
		}
		}
		throw new IllegalArgumentException("Could not build a gradient data set for data of type " + type + ".");
	}
	@Override public String getType() { return type; }

}