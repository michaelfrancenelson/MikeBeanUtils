package swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.lang.reflect.Field;

public interface ObjectArrayImagePanel<T>
{
	public String queryPixel(int i, int j);
	public String queryRelative(double relativeI, double relativeJ);
	public String queryDataArray(int i, int j);
	
	public int[] objArrayCoordsToPanelCoords(int objArrayI, int objArrayJ);

	public void setField(String name);
	public void setField(Field f);
	public void updateImage();
	
	public void addValueLabel(double relativeI, double relativeJ, Font font);
	public void addPoint(int i, int j, int size, Color color);
	public void addPoint(double relativeI, double relativeJ, int size, Color color);

	public Image getImg();

	public Class<T> getObjClass();

	public int getImgDisplayWidth(); 
	public int getImgDisplayHeight();

	
}
