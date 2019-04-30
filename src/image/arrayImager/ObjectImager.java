package image.arrayImager;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import beans.memberState.FieldWatcher;
import image.colorInterpolator.ColorInterpolator;
import image.imageFactories.PrimitiveImageFactory;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;
import utils.Sequences;

public class ObjectImager<T> implements BeanImager<T>
{
	interface ImagerData<T>
	{
		public T getData(int x, int y);
		public IntArrayMinMax intMinMax();
		public DblArrayMinMax dblMinMax();
		public ByteArrayMinMax byteMinMax();
		public boolean[][] boolVal();
		public boolean[][] parsedBoolVal();
	}
	
	class ArrayData implements ImagerData<T>
	{
		public ArrayData(T[][] dat) { this.data = dat; }
		T[][] data;
		@Override public T getData(int x, int y) { return data[x][y]; }
		@Override public IntArrayMinMax intMinMax() { return currentWatcher.getIntVal(data); }
		@Override public DblArrayMinMax dblMinMax() { return currentWatcher.getDoubleVal(data); }
		@Override public ByteArrayMinMax byteMinMax() { return currentWatcher.getByteVal(data); }
		@Override public boolean[][] boolVal() { return currentWatcher.getBoolVal(data); }
		@Override public boolean[][] parsedBoolVal() { return currentWatcher.getParsedBoolVal(data); }
	}

	class ListData implements ImagerData<T>
	{
		public ListData(List<List<T>> dat) { this.data = dat; }
		List<List<T>> data;
		@Override public T getData(int x, int y) { return data.get(x).get(y); }
		@Override public IntArrayMinMax intMinMax() { return currentWatcher.getIntVal(data); }
		@Override public DblArrayMinMax dblMinMax() { return currentWatcher.getDoubleVal(data); }
		@Override public ByteArrayMinMax byteMinMax() { return currentWatcher.getByteVal(data); }
		@Override public boolean[][] boolVal() { return currentWatcher.getBoolVal(data); }
		@Override public boolean[][] parsedBoolVal() { return currentWatcher.getParsedBoolVal(data); }
	}
	
	int rgbType = BufferedImage.TYPE_3BYTE_BGR;
	ColorInterpolator ci, booleanCI;

	String dblFmt;

	boolean transposeImg, showBoolNA, flipAxisX, flipAxisY, horizLeg, legLoToHi;
	int nLegendSteps;
	int dataWidth, dataHeight;
	int legDatWidth, legDatHeight;
	int[] currentSelectionArrayCoords;

	Class<T> clazz;

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
	
	public void setData(T[][] dat)
	{
		this.objectData = new ArrayData(dat);
	}

	public void setData(List<List<T>> dat)
	{
		this.objectData = new ListData(dat);
	}

	void clearLegendData()
	{
		legDatInt = null;
		legDatDbl = null;
		legDatByte = null;
		legDatBool = null;
	}
	

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
	@Override public Image getLegendImage()
	{
		return legendImg; 
	}

	@Override public String getCurrentFieldName() { return getCurrentField().getName(); }
	@Override public Field getCurrentField() { return currentWatcher.getField(); }
	@Override public void setColors(Color[] colors) { ci.updateColors(colors); }
	@Override public void setField(String fieldName) 
	{ 
		this.currentWatcher = watchers.get(fieldName); 
		refresh();
	} 
	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public FieldWatcher<T> getWatcher() { return currentWatcher; }
	@Override public ColorInterpolator getInterpolator() { return ci; }
	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
	@Override public void setCurrentSelectedArrayCoords(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }
	@Override public int[] getCurrentSelectedArrayCoords() { return currentSelectionArrayCoords; }
	@Override public Class<T> getObjClass() { return clazz; }

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
			return objectData.getData(i, j);
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

	@Override public int getDataWidth() { return this.dataWidth; }
	@Override public int getDataHeight() { return this.dataHeight; }
}
