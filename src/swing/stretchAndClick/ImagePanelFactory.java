package swing.stretchAndClick;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import beans.builder.AnnotatedBeanReader.ParsedField;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.ImagerData;
import imaging.imagers.ImagerFactory;
import imaging.imagers.ObjectImager;
import imaging.imagers.PrimitiveArrayData;
import imaging.imagers.PrimitiveImager;

/**
 * 
 * @author michaelfrancenelson
 *
 */
public class ImagePanelFactory 
{
	public static <T> ObjectImagePanel<T> buildPanel(
			ImagerData<T> dat,
			Class<T> clazz,	Class<? extends Annotation> annClass,
			String fieldName, ObjectImager<T> imager,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight,
			double decoratorRelPointSize)
	{
		return buildPanel(
				imager, fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight, decoratorRelPointSize,
				clazz, annClass);
	}

	public static <T> ObjectImagePanel<T> buildPanel(
			ImagerData<T> dat, Class<T> clazz,	Class<? extends Annotation> annClass,
			String fieldName,
			Color[] gradientColors, Color[] booleanColors,
			Double naDouble, Integer naInt, Color naColor, 
			String dblFmt, List<String> parsedBooleanFields,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight, double decoratorRelPointSize)
	{
		if (naDouble == null) naDouble = -Double.MAX_VALUE;
		if (naInt == null) naInt = Integer.MIN_VALUE;
		if (naColor == null) naColor = Color.gray;
		if (dblFmt == null) dblFmt = "%.2f";
		if (parsedBooleanFields == null) parsedBooleanFields = new ArrayList<String>();
		ObjectImager<T> imager = ImagerFactory.factory(
				dat, fieldName, 
				clazz, annClass,
				gradientColors, booleanColors,
				naDouble, naInt, naColor, 
				dblFmt, parsedBooleanFields);
		
		return buildPanel(
				imager, fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight, decoratorRelPointSize,
				clazz, annClass);
	}

	public static <T> PrimitiveImagePanel<T> buildPanel(
			PrimitiveArrayData<?> dat,
			ColorInterpolator ci,
			String fieldName,
			String dblFmt,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight,
			double decoratorRelPointSize,
			boolean asBoolean)
	{
		if (dblFmt == null) dblFmt = "%.2f";
		dat.setAsBoolean(asBoolean);
		PrimitiveImager imager = ImagerFactory.primitiveFactory(dat, ci, dblFmt);
		return buildPanel(
				imager, fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight, decoratorRelPointSize);
	}
	
	public static <T> PrimitiveImagePanel<T> buildPanel(
			PrimitiveImager imager, 
			String fieldName,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight, double decoratorRelPointSize)
	{
		PrimitiveImagePanel<T> out = new PrimitiveImagePanel<T>();
		if (fieldName != null) out.setField(fieldName.toLowerCase());
		out.setLabelVisibility(true);
		out.setPtRelSize(decoratorRelPointSize);
		out.init(imager, fixedWidth, fixedHeight, keepAspectRatio, false);
		return out;
	}
	
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
	public static ImagePanel imagePanel(
			String imageFile, boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight)
	{
		ImagePanel out = new ImagePanel();
		Image img = null;
		try {
			img = ImageIO.read(new File(imageFile));
		} catch (IOException e) { 
			e.printStackTrace();
		}
		out.init(img, fixedWidth, fixedHeight, keepAspectRatio);
		return out;
	}

	public static <T> ObjectImagePanel<T> buildPanel(
			ObjectImager<T> imager, String fieldName,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight, double decoratorRelPointSize,
			Class<T> clazz, Class<? extends Annotation> annClass)
	{
		ObjectImagePanel<T> out = new ObjectImagePanel<T>();
		if (fieldName != null) imager.setField(fieldName.toLowerCase());
		if (annClass == null) annClass = ParsedField.class;
		out.setLabelVisibility(true);
		out.setPtRelSize(decoratorRelPointSize);
		out.init(imager, fixedWidth, fixedHeight, keepAspectRatio, 
				clazz, annClass);
		return out;
	}
	
	
//	/**
//	 *  Build a panel using an already existing <code>ObjectArrayImager</code> to generate the image from the states of
//	 *  objects in a 2D array.
//	 * 
//	 * @param imager 
//	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
//	 *                        If false, the image will stretch to fill the window if it is resized.
//	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
//	 *                        this parameter is ignored.
//	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
//	 *                        if the window is resized. The image height may still adjust to resizing.
//	 *                        Values of 0 or less are ignored.
//	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
//	 *                        if the window is resized. The width may still adjust to resizing.
//	 *                        Values of 0 or less are ignored.
//	 * @return
//	 */
//	public static <T> ObjectImagePanel<T> buildLegendPanel(
//			BeanImager<T> imager, String fieldName,
//			boolean keepAspectRatio,
//			int fixedWidth, int fixedHeight, 
//			double decoratorRelPointSize,
//			Class<T> clazz, Class<? extends Annotation> annClass)
//	{
//		ObjectImagePanel<T> out = new ObjectImagePanel<T>();
//		out.setLabelVisibility(true);
//		out.setPtRelSize(decoratorRelPointSize);
//		out.init(imager, fixedWidth, fixedHeight, keepAspectRatio, true);
//		imager.setField(fieldName.toLowerCase());
//		return out;
//	}
}