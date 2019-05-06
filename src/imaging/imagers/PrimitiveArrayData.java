package imaging.imagers;

import beans.memberState.FieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import utils.ArrayUtils;

public class PrimitiveArrayData<T> extends ArrayData<T>
{
	private String type;

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
		ci.updateMinMax(dataMin, dataMax);
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

	public PrimitiveArrayData(double[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{  
		dblDat = dat;  type = "dbl";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(float[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{	
		fltDat = dat;  type = "flt";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(byte[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{
		bytDat = dat; type = "byt";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(short[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		shtDat = dat; type = "sht";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(int[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		intDat = dat; type = "int";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(long[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		lngDat = dat;	type = "lng";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(char[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		chrDat = dat; type = "chr";
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}
	public PrimitiveArrayData(boolean[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		booDat = dat; type = "boo";  
		setDims(dat.length, dat[0].length, flipX, flipY, transpose);
	}

	private double[][]  dblDat;
	private float[][] fltDat;
	private byte[][]    bytDat; 
	private short[][] shtDat;
	private int[][] intDat; 
	private long[][] lngDat; 
	private char[][] chrDat;
	private boolean[][] booDat;



}

//	@Override public T getObjectAt(int x, int y) { return null; }
//@Override
//public IntArrayMinMax intMinMax() {
//	// TODO Auto-generated method stub
//	return null;
//}
//
//@Override
//public DblArrayMinMax dblMinMax() {
//	// TODO Auto-generated method stub
//	return null;
//}
//
//@Override
//public ByteArrayMinMax byteMinMax() {
//	// TODO Auto-generated method stub
//	return null;
//}
//
//@Override
//public boolean[][] boolVal() {
//	// TODO Auto-generated method stub
//	return null;
//}
//
//@Override
//public boolean[][] parsedBoolVal() {
//	// TODO Auto-generated method stub
//	return null;
//}
