package swing.stretchAndClick;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.builder.AnnotatedBeanReader.ParsedField;
import imaging.colorInterpolator.ColorInterpolator;
import imaging.imagers.Imager;
import imaging.imagers.ImagerFactory;
import imaging.imagers.ObjectImager;
import imaging.imagers.PrimitiveImager;
import imaging.imagers.imagerData.ImagerData;
import imaging.imagers.imagerData.PrimitiveImagerData;

/**
 *  
 * @author michaelfrancenelson
 *
 */
public class PanelFactory 
{
	public static Logger logger = LoggerFactory.getLogger(PanelFactory.class);
	
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
			PrimitiveImagerData<T> dat,
			ColorInterpolator ci,
			ColorInterpolator booleanCI,
			String fieldName,
			String dblFmt,
			List<String> parsedBooleanFields,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight,
			double decoratorRelPointSize,
			boolean asBoolean)
	{
		dat.setAsBoolean(asBoolean);
		PrimitiveImager<T> imager = ImagerFactory.primitiveFactory(
				dat,
				ci,
				booleanCI,
				dblFmt, 
				fieldName,
				parsedBooleanFields);
		return buildPrimitivePanel(
				imager, fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight, decoratorRelPointSize);
	}

	public static <T> PrimitiveImagePanel<T> buildPrimitivePanel(
			Imager<T> imager, 
//			PrimitiveImager<T> imager, 
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

	
	
	
	
	public static <T> PrimitiveImagePanel<T> buildLegendPanel(
			int nSteps, double min, double max, String type,
			String dblFmt,
			ColorInterpolator ci, ColorInterpolator bi,
			boolean horizontal, boolean loToHi, boolean booleanNA,
			boolean keepAspectRatio, int width, int height, double ptSize)
	{
		
		
		logger.debug(String.format("build logger panel: min = %"));
		PrimitiveImagerData<T> datArr = PrimitiveImagerData.buildGradientData(
				type, min, max, nSteps, horizontal, loToHi, booleanNA);
		
		return buildPanel(
				datArr, 
				ci, bi,
				type, 
				dblFmt,
				null, false, width, height, ptSize, false);
		
//		return buildPanel(
//				legDat,
//				imgr.getColorInterpolator(), imgr.getBooleanColorInterpolator(),
//				imgr.getFieldType(), imgr.getDblFmt(),
//				null, false, width, height, ptSize, false);
				
//		
//		PrimitiveImager<T> imgrPrim = ImagerFactory.primitiveFactory(
//				legDat, imgr.getColorInterpolator(), 
//				imgr.getBooleanColorInterpolator(), imgr.getDblFmt(), 
//				false, null);
//
//		return buildPanel(
//				imgrPrim, 
//				imgr.getFieldName(),
//				keepAspectRatio, 
//				width, height, ptSize);
		
	}
	
	public static <T> PrimitiveImagePanel<T> buildLegendPanel(
			int nSteps, String fieldType, String fieldName,
			double dataMin, double dataMax,
			ColorInterpolator ci, ColorInterpolator booleanCI, 
			String dblFmt, List<String> parsedBooleanFields,
			boolean horizontal, boolean loToHi, boolean booleanNA,
			boolean keepAspectRatio, int fixedWidth, int fixedHeight,
			double decoratorRelPointSize)
//	, boolean asBoolean)
	{
		PrimitiveImagerData<T> legDat = PrimitiveImagerData.buildGradientData(
				fieldType, dataMin, dataMax, nSteps,
				horizontal, loToHi, booleanNA);

		PrimitiveImager<T> imgr = ImagerFactory.primitiveFactory(
				legDat, ci, booleanCI, dblFmt, fieldName, parsedBooleanFields);
//		PrimitiveImager<T> imgr = ImagerFactory.primitiveFactory(
//				legDat, ci, booleanCI, dblFmt, asBoolean, parsedBooleanFields);

		return buildPrimitivePanel(
				imgr, 
				fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight ,decoratorRelPointSize);
	}
}