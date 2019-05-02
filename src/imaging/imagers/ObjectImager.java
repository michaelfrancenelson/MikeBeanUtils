package imaging.imagers;

import java.awt.Color;
import java.awt.Image;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imageFactories.PrimitiveImageFactory;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;
import utils.Sequences;

public class ObjectImager<T> implements BeanImager<T>
//public class ObjectImager<T, A extends Annotation> implements BeanImager<T, A>
{

	private ColorInterpolator ci, booleanCI;

	String dblFmt;

	boolean transposeImg, showBoolNA, flipAxisX, flipAxisY, horizLeg, legLoToHi;
	int nLegendSteps, dataWidth, dataHeight, legDatWidth, legDatHeight;
	int[] currentSelectionArrayCoords;

	Class<T> clazz;
	Class<? extends Annotation> annClass;

	IntArrayMinMax legDatInt;
	DblArrayMinMax legDatDbl;
	ByteArrayMinMax legDatByte;
	Boolean[][] legDatBool = null;

	double datMin, datMax;

	Image img, legendImg;

	Map<String, FieldWatcher<T>> watchers;
	FieldWatcher<T> currentWatcher;
	Map<String, Boolean> parsedBooleanFieldNames;
	private ImagerData<T> objectData;

	protected void buildWatchers()
	{
		watchers = SimpleFieldWatcher.getWatcherMap(
				clazz, annClass, dblFmt, true, true);
	}

	protected void initialize(
			Class<T> clazz, 
			Class<? extends Annotation> annClass,
			String dblFmt)
	{
		this.dataHeight = objectData.getHeight();
		this.dataWidth = objectData.getWidth();
		this.clazz = clazz;
		this.annClass = annClass;
		this.dblFmt = dblFmt;
		buildWatchers();
	}

	void clearLegendData()
	{
		legDatInt = null;
		legDatDbl = null;
		legDatByte = null;
		legDatBool = null;
	}

	/**
	 * NOTE: It would be nice to simplify the large switch, there's a lot of repeated similar code...
	 */
	void buildImage()
	{
		ColorInterpolator interp;
		if ((parsedBooleanFieldNames != null) && 
				(parsedBooleanFieldNames.containsKey(currentWatcher.getFieldName())))
			interp = booleanCI;
		else interp = ci;

		String type = currentWatcher.getField().getType().getSimpleName();
		//		System.out.println("ObjectImager.buildImage() type = " + type);
		clearLegendData();
		switch (type.toLowerCase())
		{
		case("int"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);
			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("double"):
		{
			DblArrayMinMax dat = objectData.dblMinMax(); 
			double legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { double t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatDbl = new DblArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatDbl.getDat().length; legDatHeight = legDatDbl.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatDbl.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("byte"):
		{
			ByteArrayMinMax dat = objectData.byteMinMax();
			byte legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { byte t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatByte = new ByteArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatByte.getDat().length; legDatHeight = legDatByte.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatByte.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			break;
		}
		case("char"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			break;
		}
		case("boolean"):
		{
			boolean[][] dat = objectData.boolVal();
			img = PrimitiveImageFactory.buildImage(dat, interp, flipAxisX, flipAxisY, transposeImg);
			legDatBool = Sequences.booleanGradient2D(showBoolNA, horizLeg);
			legDatWidth = legDatBool.length; legDatHeight = legDatBool[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatBool, interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("short"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);
			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("long"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);
			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("float"):
		{
			DblArrayMinMax dat = objectData.dblMinMax(); 
			double legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { double t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatDbl = new DblArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatDbl.getDat().length; legDatHeight = legDatDbl.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatDbl.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("integer"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);
			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			break;
		}
		case("character"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			break;
		}
		case("string"):
		{
			IntArrayMinMax dat = objectData.intMinMax();
			int legMin = dat.getMin(), legMax = dat.getMax();
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			break;
		}
		}
	}

	@Override public Image getImage() { return img; }
	@Override public Image getLegendImage() { return legendImg; }

	@Override public String getCurrentFieldName() { return getCurrentField().getName(); }
	@Override public Field getCurrentField() { return currentWatcher.getField(); }
	@Override public void setColors(Color[] colors) { ci.updateColors(colors); }
	@Override public void setField(String fieldName) { this.currentWatcher = watchers.get(fieldName); refresh(); } 

	@Override public void refresh() { buildImage(); }

	/** 
	 * Checks that the coordinates are valid.
	 * Sets the current selection coordinates.
	 */
	@Override
	public T getObjAt(int i, int j) 
	{
		if ((i >= 0 && j >= 0) &&  (i < dataWidth && j < dataHeight))
		{
			setCurrentSelectedArrayCoords(i, j);
			return getCurrentSelectedObj();
			//			return objectData.getData(i, j);
		}
		else throw new IllegalArgumentException("Input coordinates + (" + i + ", " + j + 
				") are incompatible with the object array size (" + dataWidth + ", " + dataHeight+ ".");
	}

	/**
	 * Adjusts coordinates > 1.0 or < 0.0 to fall within range 1.0 - 0.0 <br>
	 * Sets the current selection coordinates.
	 */
	@Override
	public T getObjAt(double relativeI, double relativeJ) 
	{
		setCurrentSelection(relativeI, relativeJ);
		return getCurrentSelectedObj();
	}

	@Override public String queryObjectAt(int i, int j) { return currentWatcher.getStringVal(getObjAt(i, j)); }

	@Override
	public T getCurrentSelectedObj() 
	{ return objectData.getData(currentSelectionArrayCoords[0], currentSelectionArrayCoords[1]); 
	}

	@Override
	public void setCurrentSelection(double relativeI, double relativeJ) 
	{ currentSelectionArrayCoords = getArrayCoords(relativeI, relativeJ); }

	@Override
	public int[] getArrayCoords(double relativeI, double relativeJ) 
	{
		if (transposeImg) { double t = relativeI; relativeJ = relativeI; relativeI = t; }
		if (flipAxisX) relativeI = 1.0 - relativeI;
		if (flipAxisY) relativeJ = 1.0 - relativeJ;
		return BeanImager.getObjArrayCoords(
				relativeI, relativeJ, dataWidth, dataHeight);




	}

	@Override
	public String queryLegendAt(double relativeI, double relativeJ) 
	{
		int[] xy = BeanImager.getObjArrayCoords(relativeI, relativeJ, legDatWidth, legDatHeight);

		if (legDatInt != null)
			return "" + legDatInt.getDat()[xy[0]][xy[1]];
		else if (legDatDbl != null)
			return String.format(ci.getDoubleFmt(), legDatDbl.getDat()[xy[0]][xy[1]]);
		else if (legDatByte != null)
			return "" + legDatByte.getDat()[xy[0]][xy[1]];
		else if (legDatBool != null)
		{
			if (legDatBool[xy[0]][xy[1]] == null) return "NA";
			else if (legDatBool[xy[0]][xy[1]]) return "true";
			else return "false";
		}
		return "value not found";
	}

	public void setData(ImagerData<T> dat)
	{
		if (dat instanceof ObjectImager.ArrayData)
			this.objectData = (ArrayData) dat;
		else if (dat instanceof ObjectImager.ListData)
			this.objectData = (ListData) dat;
		else throw new IllegalArgumentException("Input data type not yet implemented");
	}

	public void setData(T[][] dat) { this.objectData = new ArrayData(dat);	}
	public void setData(List<List<T>> dat) { this.objectData = new ListData(dat); }
	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public FieldWatcher<T> getWatcher() { return currentWatcher; }

	@Override public ColorInterpolator getInterpolator() { return ci; }
	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
	@Override public void setInterpolator(ColorInterpolator ci) { this.ci = ci; }
	@Override public void setBooleanInterpolator(ColorInterpolator ci) { booleanCI = ci; }

	@Override public void setCurrentSelectedArrayCoords(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }
	@Override public int[] getCurrentSelectedArrayCoords() { return currentSelectionArrayCoords; }

	@Override public Class<T> getObjClass() { return clazz; }
	@Override public Class<? extends Annotation> getAnnClass() { return annClass; }

	@Override public int getDataWidth() { return this.dataWidth; }
	@Override public int getDataHeight() { return this.dataHeight; }


	public interface ImagerData<T>
	{
		public T getData(int x, int y);


		public double getInterpolatorData(int x, int y);
		public IntArrayMinMax intMinMax();
		public DblArrayMinMax dblMinMax();
		public ByteArrayMinMax byteMinMax();
		public boolean[][] boolVal();
		public boolean[][] parsedBoolVal();
		public int getWidth();
		public int getHeight();
	}







	public static class PrimitiveArrayData<T> implements ImagerData<T>
	{

		private int width, height;
		private int startX, startY;
		private int endX, endY;
		private int incrementX, incrementY;
		private int offsetX, offsetY;
		boolean transpose;


		String type;
		int arrayX, arrayY;

		private void setOrientation(boolean flipX, boolean flipY, boolean transpose)
		{
			this.transpose = transpose;
			incrementX = 1; incrementY = 1; offsetX = 0; offsetY = 0;
			startX = 0; startY = 0;
			endY = height; endX = width;

			if (transpose)
			{
//				int t = height; height = width; width = t;
				boolean b = flipX; 
				flipX = flipY;
				flipY = b;
			}
			
			if (flipX) 
			{
				int t = endX; endX = startX - 1; startX = t - 1; incrementX = -1; 
				offsetX = width - 1;
			} 
			if (flipY) {
				int t = endY; endY = startY - 1; startY = t - 1; incrementY = -1; 
				offsetY = height - 1;
			}

			if (transpose)
			{
				int t = height; height = width; width = t;
			}

			//				t = startY; startY = startX; startX = t;
			//				t = endY; endY = endX; endX = t;
			//				t = incrementY; incrementY = incrementX; incrementX = t;
			//				t = offsetY; offsetY = offsetX; offsetX = t; 
			//			}

		}

		private void setDataCoords(int inputX, int inputY)
		{
			//			arrayX = offsetX + (incrementX * inputX);
			//			arrayY = offsetY + (incrementY * inputY);

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
			//			arrayX = offsetX + (incrementX * x);
			//			arrayY = offsetY + (incrementY * y);

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
		{ dblDat = dat; width = dat.length; type = "dbl"; height = dat[0].length; setOrientation(flipX, flipY, transpose); }
		public PrimitiveArrayData(float[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ fltDat = dat; width = dat.length; type = "flt"; height = dat[0].length; setOrientation(flipX, flipY, transpose); }

		public PrimitiveArrayData(byte[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{
			bytDat = dat;  
			type = "byt";
			width = dat.length; 
			height = dat[0].length; 
			setOrientation(flipX, flipY, transpose); 
		}
		public PrimitiveArrayData(short[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ shtDat = dat; width = dat.length; type = "sht"; height = dat[0].length; setOrientation(flipX, flipY, transpose); }
		public PrimitiveArrayData(int[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ 
			intDat = dat;  
			type = "int";
			width = dat.length; 
			height = dat[0].length; 
			setOrientation(flipX, flipY, transpose); 
		}

		public PrimitiveArrayData(long[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ lngDat = dat; width = dat.length; type = "lng"; height = dat[0].length;setOrientation(flipX, flipY, transpose); }
		public PrimitiveArrayData(char[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ chrDat = dat; width = dat.length; type = "chr"; height = dat[0].length;setOrientation(flipX, flipY, transpose); }
		public PrimitiveArrayData(boolean[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ booDat = dat; width = dat.length; type = "boo"; height = dat[0].length;setOrientation(flipX, flipY, transpose); }


		double[][]  dblDat; float[][] fltDat;
		byte[][]    bytDat; short[][] shtDat; int[][] intDat; long[][] lngDat; char[][] chrDat;
		boolean[][] booDat;







		int i = 4;




		@Override
		public T getData(int x, int y) {
			// TODO Auto-generated method stub

			System.out.println(((Object) i).toString());

			return null;


		}

		@Override
		public IntArrayMinMax intMinMax() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DblArrayMinMax dblMinMax() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ByteArrayMinMax byteMinMax() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean[][] boolVal() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean[][] parsedBoolVal() {
			// TODO Auto-generated method stub
			return null;
		}

	}


	private class ArrayData implements ImagerData<T>
	{
		//		boolean flipX, flipY, transpose;
		//		int width, height;
		public ArrayData(T[][] dat)
		//		boolean flipX, boolean flipY, boolean transpose) 
		{ 
			this.data = dat; 
			//			this.flipX = flipX; this.flipY = flipY; this.transpose = transpose;
		}




		private T[][] data;
		@Override public T getData(int x, int y) { 

			return data[x][y]; 
		}


		@Override public IntArrayMinMax intMinMax() { return currentWatcher.getIntVal(data); }
		@Override public DblArrayMinMax dblMinMax() { return currentWatcher.getDoubleVal(data); }
		@Override public ByteArrayMinMax byteMinMax() { return currentWatcher.getByteVal(data); }
		@Override public boolean[][] boolVal() { return currentWatcher.getBoolVal(data); }
		@Override public boolean[][] parsedBoolVal() { return currentWatcher.getParsedBoolVal(data); }
		@Override public int getWidth() { return data.length; }
		@Override public int getHeight() { return data[0].length; }


		@Override
		public double getInterpolatorData(int x, int y) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private class ListData implements ImagerData<T>
	{
		boolean flipX, flipY, transpose;
		public ListData(List<List<T>> dat) { this.data = dat; }
		List<List<T>> data;
		@Override public T getData(int x, int y) { return data.get(x).get(y); }
		@Override public IntArrayMinMax intMinMax() { return currentWatcher.getIntVal(data); }
		@Override public DblArrayMinMax dblMinMax() { return currentWatcher.getDoubleVal(data); }
		@Override public ByteArrayMinMax byteMinMax() { return currentWatcher.getByteVal(data); }
		@Override public boolean[][] boolVal() { return currentWatcher.getBoolVal(data); }
		@Override public boolean[][] parsedBoolVal() { return currentWatcher.getParsedBoolVal(data); }
		@Override public int getWidth() { return data.size(); }
		@Override public int getHeight() { return data.get(0).size(); }
		@Override
		public double getInterpolatorData(int x, int y) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public static double doubleCaster(float f) { return (double) f; }
	public static double doubleCaster(byte f) { return (double) f; }
	public static double doubleCaster(char f) { return (double) f; }
	public static double doubleCaster(short f) { return (double) f; }
	public static double doubleCaster(int f) { return (double) f; }
	public static double doubleCaster(long f) { return (double) f; }
	public static double doubleCaster(boolean f) { if (f) return 1; return 0; } 

}
