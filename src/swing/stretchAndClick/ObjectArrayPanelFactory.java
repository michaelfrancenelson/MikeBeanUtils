package swing.stretchAndClick;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import image.arrayImager.ObjectArrayImager;
import image.arrayImager.SimpleArrayImager;
import image.arrayImager.SimpleArrayImagerWithLegend;
import swing.ObjectArrayImageDecorator;

/**
 * 
 * @author michaelfrancenelson
 *
 */
public class ObjectArrayPanelFactory 
{

	
	/**
	 *  Build a panel using an image file.  The panel won't return any values when clicked
	 *  and refresh methods will have no effect.
	 *  
	 * @param imageFile
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
	public static <T> ObjectArrayImagePanel<T> buildPanel(
			String imageFile, boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight)
	{
		ObjectArrayImagePanel<T> out = new ObjectArrayImagePanel<T>();
		Image img = null;
		try {
			img = ImageIO.read(new File(imageFile));
		} catch (IOException e) { 
			e.printStackTrace();
		}
//		out.fixedImg = true;
		out.init(img, fixedWidth, fixedHeight, keepAspectRatio, true, null);

		return out;
	}

	
	

	
	/** 
	 * Build a panel to generate the image from
	 * thie states of objects in a 2D array
	 * 
	 * @param clazz
	 * @param objArray
	 * @param fieldName the image will be built from values in this field.
	 * @param gradientColors color gradient
	 * @param booleanColors Colors to use to plot a boolean member.  Only the first and last colors are used.
	 * @param naDouble pixels with this value will be plotted with the 'naColor'
	 * @param naInt pixels with this value will be plotted with the 'naColor'
	 * @param naColor color to plot for NA values.
	 * @param dblFmt
	 * @param parsedBooleanFields Which fields should be drawn as parsed boolean variables?
	 *                            This feature allows true/false and naColors to be shown.
	 *                            Can be null.
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
//	public static <T> ObjectArrayImager<T> buildLegendImager(
//			SimpleArrayImager<T> saim,
//			boolean includeBooleanNA, int nLegendSteps,
//			int legendDirection)
//	{
//		ObjectArrayImager<T> imager = GradientLegendImager.factory(
//				saim, includeBooleanNA, nLegendSteps, legendDirection);
//		return imager;
//	}
//	
	/** 
	 * Build a panel to generate the image from
	 * thie states of objects in a 2D array
	 * 
	 * @param clazz
	 * @param objArray
	 * @param fieldName the image will be built from values in this field.
	 * @param gradientColors color gradient
	 * @param booleanColors Colors to use to plot a boolean member.  Only the first and last colors are used.
	 * @param naDouble pixels with this value will be plotted with the 'naColor'
	 * @param naInt pixels with this value will be plotted with the 'naColor'
	 * @param naColor color to plot for NA values.
	 * @param dblFmt
	 * @param parsedBooleanFields Which fields should be drawn as parsed boolean variables?
	 *                            This feature allows true/false and naColors to be shown.
	 *                            Can be null.
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
	public static <T> ObjectArrayImagePanel<T> buildPanel(
			Class<T> clazz, T[][] objArray, String fieldName,
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor, 
			String dblFmt, List<String> parsedBooleanFields,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight,
			double decoratorRelPointSize,
			boolean includeBooleanNA, int nLegendSteps, int legendDirection)
	{
		ObjectArrayImager<T> imager = SimpleArrayImagerWithLegend.factory(
				clazz, objArray, fieldName, 
				gradientColors, booleanColors,
				naDouble, naInt, naColor, 
				dblFmt, parsedBooleanFields,
				includeBooleanNA, nLegendSteps, legendDirection
				);
		return buildPanel(imager, keepAspectRatio, fixedWidth, fixedHeight, decoratorRelPointSize);
	}
	
	
	/** 
	 * Build a panel to generate the image from
	 * thie states of objects in a 2D array
	 * 
	 * @param clazz
	 * @param objArray
	 * @param fieldName the image will be built from values in this field.
	 * @param gradientColors color gradient
	 * @param booleanColors Colors to use to plot a boolean member.  Only the first and last colors are used.
	 * @param naDouble pixels with this value will be plotted with the 'naColor'
	 * @param naInt pixels with this value will be plotted with the 'naColor'
	 * @param naColor color to plot for NA values.
	 * @param dblFmt
	 * @param parsedBooleanFields Which fields should be drawn as parsed boolean variables?
	 *                            This feature allows true/false and naColors to be shown.
	 *                            Can be null.
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
	public static <T> ObjectArrayImagePanel<T> buildPanel(
			Class<T> clazz, T[][] objArray, String fieldName,
			Color[] gradientColors, Color[] booleanColors,
			double naDouble, int naInt, Color naColor, 
			String dblFmt, List<String> parsedBooleanFields,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight,
			double decoratorRelPointSize)
	{
		ObjectArrayImager<T> imager = SimpleArrayImager.factory(
				clazz, objArray, fieldName, 
				gradientColors, booleanColors,
				naDouble, naInt, naColor, 
				dblFmt, parsedBooleanFields);
		return buildPanel(imager, keepAspectRatio, fixedWidth, fixedHeight, decoratorRelPointSize);
	}

	public static <T> ObjectArrayImagePanel<T> buildPanel(
			ObjectArrayImager<T> imager, String fieldName,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight, double decoratorRelPointSize)
	{
		ObjectArrayImagePanel<T> out = new ObjectArrayImagePanel<T>();
		out.setLabelVisibility(true);
		out.setPtRelSize(decoratorRelPointSize);
		imager.setField(fieldName);
		out.init(imager.getImage(), fixedWidth, fixedHeight, keepAspectRatio, false, imager);
		out.setDecorator(new ObjectArrayImageDecorator(imager));
		return out;
	}
	
	/**
	 *  Build a panel using an already existing <code>ObjectArrayImager</code> to generate the image from the states of
	 *  objects in a 2D array.
	 * 
	 * @param imager 
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
	public static <T> ObjectArrayImagePanel<T> buildPanel(
			ObjectArrayImager<T> imager,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight, double decoratorRelPointSize)
	{
		ObjectArrayImagePanel<T> out = new ObjectArrayImagePanel<T>();
		out.setLabelVisibility(true);
		out.setPtRelSize(decoratorRelPointSize);
		out.init(imager.getImage(), fixedWidth, fixedHeight, keepAspectRatio, false, imager);
		out.setDecorator(new ObjectArrayImageDecorator(imager));
//		out.setDecorator(new ObjectArrayImageDecorator(decoratorRelPointSize));
		return out;
	}
	
//	public static <T> ObjectArrayLegendPanel<T> getLegendPanel(
//			ObjectArrayJPanel<T> pan,
//			GradientLegendImager<T> imager,
//			boolean keepAspectRatio, 
//			int fixedWidth, int fixedHeight
//			)
//	{
//		ObjectArrayLegendPanel<T> out = new ObjectArrayLegendPanel<T>();
//		
//		out.setLabelVisibility(true);
//		out.setPtRelSize(pan.getPtRelSize());
//		out.init(imager.getImage(), fixedWidth, fixedHeight, keepAspectRatio, false, imager);
//		out.setDecorator(new ObjectArrayImageDecorator(
//				imager, imager.getLegDatDim1(), imager.getLegDatDim2()));
//		return out;
//	}
	
	
}
