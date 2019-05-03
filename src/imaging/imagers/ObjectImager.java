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
import imaging.imageFactories.ImageFactory;
import imaging.imageFactories.ImageFactory.ImageMinMax;
import imaging.imagers.ArrayData.ListData;
import utils.ArrayUtils.ByteArrayMinMax;
import utils.ArrayUtils.DblArrayMinMax;
import utils.ArrayUtils.IntArrayMinMax;
import utils.Sequences;

public class ObjectImager<T> implements BeanImager<T>
{
	private ColorInterpolator ci, booleanCI;
	private String dblFmt;
	private boolean transposeImg, showBoolNA, flipAxisX, flipAxisY, horizLeg, legLoToHi;
	private int nLegendSteps;
	
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
		img = ImageFactory.buildPackageImage(objectData, interp, currentWatcher);
		switch (type.toLowerCase())
		{
		case("int"): case("short"): case("long"): case("char"):	case("character"):
		case("string"): case("integer"):
		{
			legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg); 
			legendImg = ImageFactory.buildImage(legDatInt.getDat(), interp, flipAxisX, flipAxisY, transposeImg).getImg();
			break;
		}
		case("double"): case("float"):
		{
			legDatDbl = objectData.dblLegendData(nLegendSteps, legLoToHi, horizLeg); 
			legendImg = ImageFactory.buildImage(legDatDbl.getDat(), interp, flipAxisX, flipAxisY, transposeImg).getImg();
			break;
		}
		case("byte"):
		{
			legDatByte = objectData.byteLegendData(nLegendSteps, legLoToHi, horizLeg); 
			legendImg = ImageFactory.buildImage(legDatByte.getDat(), interp, flipAxisX, flipAxisY, transposeImg).getImg();
			break;
		}
		case("boolean"): 
		{
			legDatBool = Sequences.booleanGradient2D(showBoolNA, horizLeg);
			legendImg = ImageFactory.buildImage(legDatBool, interp, flipAxisX, flipAxisY, transposeImg).getImg();
			break;
//			legDatWidth = legDatBool.length; legDatHeight = legDatBool[0].length;
		}
		}
	}

	@Override public Image getImage() { return img.getImg(); }
	@Override public Image getLegendImage() { return legendImg; }
	@Override public String getCurrentFieldName() { return getCurrentField().getName(); }
	@Override public Field getCurrentField() { return currentWatcher.getField(); }
	@Override public void setColors(Color[] colors) { ci.updateColors(colors); }
	@Override public void setField(String fieldName) { this.currentWatcher = watchers.get(fieldName); refresh(); } 
	@Override public void refresh() { buildImage(); }



	@Override
	public String queryLegendAt(double relativeI, double relativeJ) 
	{
		//		int[] xy = BeanImager.getObjArrayCoords(relativeI, relativeJ, legDatWidth, legDatHeight);
		//
		//		if (legDatInt != null)
		//			return "" + legDatInt.getDat()[xy[0]][xy[1]];
		//		else if (legDatDbl != null)
		//			return String.format(ci.getDoubleFmt(), legDatDbl.getDat()[xy[0]][xy[1]]);
		//		else if (legDatByte != null)
		//			return "" + legDatByte.getDat()[xy[0]][xy[1]];
		//		else if (legDatBool != null)
		//		{
		//			if (legDatBool[xy[0]][xy[1]] == null) return "NA";
		//			else if (legDatBool[xy[0]][xy[1]]) return "true";
		//			else return "false";
		//		}
		return "value not found";
	}

	public void setData(ImagerData<T> dat)
	{
		if (dat instanceof ArrayData)
			this.objectData = (ArrayData<T>) dat;
		else if (dat instanceof ListData)
			this.objectData = (ListData<T>) dat;
		else throw new IllegalArgumentException("Input data type not yet implemented");
	}

	public void setData(T[][] dat) { this.objectData = new ArrayData<T>(dat, flipAxisX, flipAxisY, transposeImg); }
	public void setData(List<List<T>> dat) { this.objectData = new ListData<T>(dat, flipAxisX, flipAxisY, transposeImg); }

	@Override public void setField(Field field) { setField(field.getName()); }
	@Override public FieldWatcher<T> getWatcher() { return currentWatcher; }

	@Override public ColorInterpolator getInterpolator() { return ci; }
	@Override public ColorInterpolator getBooleanInterpolator() { return booleanCI; }
	@Override public void setInterpolator(ColorInterpolator ci) { this.ci = ci; }
	@Override public void setBooleanInterpolator(ColorInterpolator ci) { booleanCI = ci; }

	@Override public Class<T> getObjClass() { return clazz; }
	@Override public Class<? extends Annotation> getAnnClass() { return annClass; }

	@Override
	public String queryDataAt(double relativeI, double relativeJ) 
	{
		return objectData.queryData(relativeI, relativeJ, currentWatcher);
	}

	@Override
	public void setDataSelection(double relativeI, double relativeJ) 
	{	
		queryDataAt(relativeI, relativeJ);
	}
}

///** 
// * Checks that the coordinates are valid.
// * Sets the current selection coordinates.
// */
//@Override
//public T getObjAt(int i, int j) 
//{
//	if ((i >= 0 && j >= 0) &&  (i < dataWidth && j < dataHeight))
//	{
//		setCurrentSelectedArrayCoords(i, j);
//		return getCurrentSelectedObj();
//	}
//	else throw new IllegalArgumentException("Input coordinates + (" + i + ", " + j + 
//			") are incompatible with the object array size (" + dataWidth + ", " + dataHeight+ ".");
//}
//
///**
// * Adjusts coordinates > 1.0 or < 0.0 to fall within range 1.0 - 0.0 <br>
// * Sets the current selection coordinates.
// */
//@Override
//public T getObjAt(double relativeI, double relativeJ) 
//{
//	
//	setCurrentSelection(relativeI, relativeJ);
//	return getCurrentSelectedObj();
//}
//@Override
//public void setCurrentSelection(double relativeI, double relativeJ) 
//{ currentSelectionArrayCoords = getArrayCoords(relativeI, relativeJ); }
//
//@Override
//public int[] getArrayCoords(double relativeI, double relativeJ) 
//{
//	if (transposeImg) { double t = relativeI; relativeJ = relativeI; relativeI = t; }
//	if (flipAxisX) relativeI = 1.0 - relativeI;
//	if (flipAxisY) relativeJ = 1.0 - relativeJ;
//	return BeanImager.getObjArrayCoords(relativeI, relativeJ, dataWidth, dataHeight);
//}
//@Override public void setCurrentSelectedArrayCoords(int i, int j) { currentSelectionArrayCoords = new int[] {i, j}; }
//@Override public int[] getCurrentSelectedArrayCoords() { return currentSelectionArrayCoords; }
//case("int"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("double"): type2 = "dbl";
//{
//	legDatDbl = objectData.dblLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("byte"): type2 = "byt"; 
//
//{
//	legDatByte = objectData.byteLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("char"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//
//
//case("boolean"): 
//{
//	legDatBool = Sequences.booleanGradient2D(showBoolNA, horizLeg);
//	legDatWidth = legDatBool.length; legDatHeight = legDatBool[0].length;
//	legendImg = ImageFactory.buildImage(legDatBool, interp, flipAxisX, flipAxisY, transposeImg).getImg();
//	break;
//}
//case("short"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("long"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("float"): type2 = "dbl";
//{
//	legDatDbl = objectData.dblLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("integer"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("character"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//case("string"): type2 = "int";
//{
//	legDatInt = objectData.intLegendData(nLegendSteps, legLoToHi, horizLeg);
//	break;
//}
//}

//	@Override public String queryObjectAt(int i, int j) { 
//		return currentWatcher.getStringVal(getObjAt(i, j)); }
//
//	@Override
//	public T getCurrentSelectedObj() 
//	{ return objectData.getObjectAt(
//			currentSelectionArrayCoords[0],
//			currentSelectionArrayCoords[1]); 
//	}
//int dataWidth, dataHeight;
//private int legDatWidth, legDatHeight;
//private int[] currentSelectionArrayCoords;
