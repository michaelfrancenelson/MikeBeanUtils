package imaging.imagers;

import imaging.imagers.ObjectImager.ImagerData;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;

public class PrimitiveArrayData<T> implements ImagerData<T>
{
	private int width, height;
	private int startX, startY;
	private int endX, endY;
	private int incrementX, incrementY;
	private int offsetX, offsetY;
	boolean transpose;
	int arrayX, arrayY;

	String type;

	private void setOrientation(boolean flipX, boolean flipY, boolean transpose)
	{
		this.transpose = transpose;
		incrementX = 1; incrementY = 1; offsetX = 0; offsetY = 0;
		startX = 0; startY = 0;
		endY = height; endX = width;

		if (transpose) { boolean b = flipX; flipX = flipY; flipY = b; }

		if (flipX) 
		{
			int t = endX; endX = startX - 1; startX = t - 1; incrementX = -1; 
			offsetX = width - 1;
		} 
		if (flipY) {
			int t = endY; endY = startY - 1; startY = t - 1; incrementY = -1; 
			offsetY = height - 1;
		}
		if (transpose) { int t = height; height = width; width = t; }
	}

	private void setDataCoords(int inputX, int inputY)
	{
		if(!transpose)
		{
			arrayX = offsetX + (incrementX * inputX);
			arrayY = offsetY + (incrementY * inputY);
		}
		else
		{
			arrayX = offsetX + (incrementX * inputY);
			arrayY = offsetY + (incrementY * inputX);
		}
	}

	@Override
	public double getInterpolatorData(int x, int y) 
	{
		setDataCoords(x, y);
		switch(type)
		{
		case("dbl"): return dblDat[arrayX][arrayY];
		case("flt"): return doubleCaster(fltDat[arrayX][arrayY]);
		case("byt"): return doubleCaster(bytDat[arrayX][arrayY]);
		case("sht"): return doubleCaster(shtDat[arrayX][arrayY]);
		case("int"): return doubleCaster(intDat[arrayX][arrayY]);
		case("lng"): return doubleCaster(lngDat[arrayX][arrayY]);
		case("chr"): return doubleCaster(chrDat[arrayX][arrayY]);
		case("boo"): return doubleCaster(booDat[arrayX][arrayY]);
		}
		return Double.MIN_VALUE;			
	}

	@Override public int getWidth() { return this.width; }
	@Override public int getHeight() { return this.height; }

	public PrimitiveArrayData(double[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{  
		dblDat = dat;  type = "dbl";
		width = dat.length; height = dat[0].length; 
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(float[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{	
		fltDat = dat;  type = "flt";
		width = dat.length; height = dat[0].length;
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(byte[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{
		bytDat = dat; type = "byt";
		width = dat.length; height = dat[0].length; 
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(short[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		shtDat = dat; type = "sht";
		width = dat.length; height = dat[0].length;
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(int[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		intDat = dat; type = "int";
		width = dat.length; height = dat[0].length; 
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(long[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		lngDat = dat;	type = "lng";
		width = dat.length; height = dat[0].length;
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(char[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		chrDat = dat; type = "chr";
		width = dat.length; height = dat[0].length;
		setOrientation(flipX, flipY, transpose); 
	}
	public PrimitiveArrayData(boolean[][] dat, boolean flipX, boolean flipY, boolean transpose)
	{ 
		booDat = dat; type = "boo";  
		width = dat.length;	height = dat[0].length;
		setOrientation(flipX, flipY, transpose); 
	}

	double[][]  dblDat; float[][] fltDat;
	byte[][]    bytDat; short[][] shtDat; int[][] intDat; long[][] lngDat; char[][] chrDat;
	boolean[][] booDat;

	int i = 4;

	@Override public T getData(int x, int y) { return null; }

//	@Override
//	public IntArrayMinMax intMinMax() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public DblArrayMinMax dblMinMax() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ByteArrayMinMax byteMinMax() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean[][] boolVal() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean[][] parsedBoolVal() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public static double doubleCaster(float f) { return (double) f; }
	public static double doubleCaster(byte f) { return (double) f; }
	public static double doubleCaster(char f) { return (double) f; }
	public static double doubleCaster(short f) { return (double) f; }
	public static double doubleCaster(int f) { return (double) f; }
	public static double doubleCaster(long f) { return (double) f; }
	public static double doubleCaster(boolean f) { if (f) return 1; return 0; } 

	
}