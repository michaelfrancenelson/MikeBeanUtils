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
import imaging.imageFactories.PrimitiveImageFactory.ImageMinMax;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;
import utils.Sequences;

public class ObjectImager<T> implements BeanImager<T>
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

	ImageMinMax img;
	Image legendImg;

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
		img = PrimitiveImageFactory.buildPackageImage(objectData, interp);
		switch (type.toLowerCase())
		{
		case("int"):
		{
//			IntArrayMinMax dat = objectData.intMinMax();
//			int legMin = dat.getMin(), legMax = dat.getMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
//			img = PrimitiveImageFactory.buildPackageImage(objectData, interp);
			int legMin = (int) img.min, legMax = (int) img.max;
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }
			ci.updateMinMax(legMin, legMax);
			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("double"):
		{
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
//			DblArrayMinMax dat = objectData.dblMinMax(); 
//			img = PrimitiveImageFactory.buildPackageImage(objectData, interp);
			
			double legMin = img.min, legMax = img.max;
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { double t = legMin; legMax = legMin; legMin = t; }

			legDatDbl = new DblArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatDbl.getDat().length; legDatHeight = legDatDbl.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatDbl.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("byte"):
		{
//			ByteArrayMinMax dat = objectData.byteMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
//			img = PrimitiveImageFactory.buildPackageImage(objectData, interp);

			byte legMin = (byte) img.min, legMax = (byte) img.max;
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { byte t = legMin; legMax = legMin; legMin = t; }

			legDatByte = new ByteArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatByte.getDat().length; legDatHeight = legDatByte.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatByte.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;

			break;
		}
		case("char"):
		{
//			IntArrayMinMax dat = objectData.intMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
//			img = PrimitiveImageFactory.buildPackageImage(objectData, interp);

			int legMin = (int) img.min, legMax = (int) img.max;
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;

			break;
		}
		case("boolean"):
		{
//			img = PrimitiveImageFactory.buildImage(dat, interp, flipAxisX, flipAxisY, transposeImg);
//			boolean[][] dat = objectData.boolVal();
//			img = PrimitiveImageFactory.buildPackageImage(objectData, interp);
//			int legMin = (int) img.min, legMax = (int) img.max;
			
			legDatBool = Sequences.booleanGradient2D(showBoolNA, horizLeg);
			legDatWidth = legDatBool.length; legDatHeight = legDatBool[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatBool, interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("short"):
		{
//			img = PrimitiveImageFactory.buildPackageImage(objectData, interp);
//			IntArrayMinMax dat = objectData.intMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			int legMin = (int) img.min, legMax = (int) img.max;
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }
			ci.updateMinMax(legMin, legMax);
			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("long"):
		{
//			IntArrayMinMax dat = objectData.intMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);
//			int legMin = dat.getMin(), legMax = dat.getMax();

			int legMin = (int) img.min, legMax = (int) img.max;
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("float"):
		{
//			DblArrayMinMax dat = objectData.dblMinMax(); 
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			double legMin = img.min, legMax = img.max;
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { double t = legMin; legMax = legMin; legMin = t; }

			legDatDbl = new DblArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatDbl.getDat().length; legDatHeight = legDatDbl.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatDbl.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("integer"):
		{
//			IntArrayMinMax dat = objectData.intMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			int legMin = (int) img.min, legMax = (int) img.max;
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			ci.updateMinMax(legMin, legMax);

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;
			break;
		}
		case("character"):
		{
//			IntArrayMinMax dat = objectData.intMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			int legMin = (int) img.min, legMax = (int) img.max;
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;

			break;
		}
		case("string"):
		{
//			IntArrayMinMax dat = objectData.intMinMax();
//			img = PrimitiveImageFactory.buildImage(dat.getDat(), interp, flipAxisX, flipAxisY, transposeImg);

			int legMin = (int) img.min, legMax = (int) img.max;
			ci.updateMinMax(legMin, legMax);
			if (legLoToHi && legMin > legMax) { int t = legMin; legMax = legMin; legMin = t; }

			legDatInt = new IntArrayMinMax( 
					Sequences.spacedIntervals2D(legMin, legMax, nLegendSteps, horizLeg),
					legMin, legMax);
			legDatWidth = legDatInt.getDat().length; legDatHeight = legDatInt.getDat()[0].length;
			legendImg = PrimitiveImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).img;

			break;
		}
		}
	}

	@Override public Image getImage() { return img.img; }
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
		return BeanImager.getObjArrayCoords(relativeI, relativeJ, dataWidth, dataHeight);
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

	public void setData(T[][] dat) { this.objectData = new ArrayData(dat, flipAxisX, flipAxisY, transposeImg); }
	public void setData(List<List<T>> dat) { this.objectData = new ListData(dat, flipAxisX, flipAxisY, transposeImg); }
	
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
		public int getWidth();
		public int getHeight();
	}

	public class ArrayData implements ImagerData<T>
	{
		int width, height;
		int startX, startY;
		int endX, endY;
		int incrementX, incrementY;
		int offsetX, offsetY;
		boolean transpose;
		int arrayX, arrayY;
		
		protected void setOrientation(boolean flipX, boolean flipY, boolean transpose)
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
		
		protected void setDataCoords(int inputX, int inputY)
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
			return currentWatcher.getDoubleVal(arrayData[arrayX][arrayY]);
		}
		
		public ArrayData() {}
		public ArrayData(T[][] dat, boolean flipX, boolean flipY, boolean transpose)
		{ 
			this.arrayData = dat; 
			width = dat.length; height = dat[0].length; 
			setOrientation(flipX, flipY, transpose); 
		}

		private T[][] arrayData;
		
		@Override public T getData(int x, int y) { 
			setDataCoords(x, y);
			return arrayData[arrayX][arrayY]; 
		}

		@Override public int getWidth() { return arrayData.length; }
		@Override public int getHeight() { return arrayData[0].length; }
	}

	public class ListData extends ArrayData 
	{
		List<List<T>> data;
		public ListData(List<List<T>> dat, boolean flipX, boolean flipY, boolean transpose)
		{ 
			
			this.data = dat; 
			width = dat.size(); height = dat.get(0).size();
			setOrientation(flipX, flipY, transpose); 
		}
		
		@Override public T getData(int x, int y) { 
			setDataCoords(x, y);
			return data.get(arrayX).get(arrayY); 
		}
		@Override
		public double getInterpolatorData(int x, int y) 
		{
			setDataCoords(x, y);
			return currentWatcher.getDoubleVal(data.get(arrayX).get(arrayY));
		}
		@Override public int getWidth() { return data.size(); }
		@Override public int getHeight() { return data.get(0).size(); }
	}
}
