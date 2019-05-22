package swing.stretchAndClick;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;

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


	public static <T> MapAndLegendPanel<T> mapLegendPanel(

			ImagerData<T> dat, Class<T> clazz,	Class<? extends Annotation> annClass,
			String fieldName,
			Color[] gradientColors, Color[] booleanColors,
			Double naDouble, Integer naInt, Color naColor, 
			String mapDblFmt, List<String> parsedBooleanFields,
			boolean mapAspectRatio, 
			int mapWidth, int mapHeight, double mapPointSize,

			String controlTitle, Font controlFont,

			int legPosition, int controlPosition,

			int nLegendLabels, int nLegendSteps, int legendWidth, int legendHeight, 
			double offset1, double offset2, 
			double textOffset, double pointOffset,
			boolean loToHi, boolean horiz, boolean legendAspectRatio,
			double legendPointSize, Font legendFont, Color legendAnnotationColor,
			String legIntFmt, String legDblFmt, String legStrFmt
			)
	{

		if (naDouble == null) naDouble = -Double.MAX_VALUE;
		if (naInt == null) naInt = Integer.MIN_VALUE;
		if (naColor == null) naColor = Color.gray;
		if (mapDblFmt == null) mapDblFmt = "%.2f";
		if (parsedBooleanFields == null) parsedBooleanFields = new ArrayList<String>();

		MapAndLegendPanel<T> out = new MapAndLegendPanel<T>();

		ObjectImager<T> imager = ImagerFactory.factory(
				dat, fieldName, 
				clazz, annClass,
				gradientColors, booleanColors,
				naDouble, naInt, naColor,
				mapDblFmt, parsedBooleanFields);

		ObjectImagePanel<T> map =  objectPanel(
				imager, fieldName,
				mapAspectRatio, 
				mapWidth, mapHeight, mapPointSize,
				clazz, annClass);

		LegendPanel<T> legend = legendPanel(imager, nLegendLabels, nLegendSteps, legendWidth, legendHeight, offset1, offset2, textOffset, pointOffset, loToHi, horiz, legendAspectRatio, legendPointSize, legendFont, legendAnnotationColor, legIntFmt, legDblFmt, legStrFmt);
		map.setLegend(legend);
		out.map = map; out.legend = legend;
		out.setLayout(legPosition, controlPosition, controlTitle, controlFont, true);
		return out;
	}


	/**
	 * 
	 * @param dat
	 * @param clazz
	 * @param annClass
	 * @param fieldName
	 * @param gradientColors
	 * @param booleanColors
	 * @param naDouble
	 * @param naInt
	 * @param naColor
	 * @param dblFmt
	 * @param parsedBooleanFields
	 * @param keepAspectRatio
	 * @param fixedWidth
	 * @param fixedHeight
	 * @param decoratorRelPointSize
	 * @return
	 */
	public static <T> ObjectImagePanel<T> objectPanel(
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

		return objectPanel(
				imager, fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight, decoratorRelPointSize,
				clazz, annClass);
	}

	/**
	 * 
	 * @param imager
	 * @param fieldName
	 * @param keepAspectRatio
	 * @param fixedWidth
	 * @param fixedHeight
	 * @param decoratorRelPointSize
	 * @param clazz
	 * @param annClass
	 * @return
	 */
	public static <T> ObjectImagePanel<T> objectPanel(
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

	/**
	 * 
	 * @param dat
	 * @param ci
	 * @param booleanCI
	 * @param fieldName
	 * @param dblFmt
	 * @param parsedBooleanFields
	 * @param keepAspectRatio
	 * @param fixedWidth
	 * @param fixedHeight
	 * @param decoratorRelPointSize
	 * @param asBoolean
	 * @return
	 */
	public static <T> PrimitiveImagePanel<T> primitivePanel(
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
		return primitivePanel(
				imager, fieldName,
				keepAspectRatio, 
				fixedWidth, fixedHeight, decoratorRelPointSize);
	}

	/**
	 * 
	 * @param imager
	 * @param fieldName
	 * @param keepAspectRatio
	 * @param fixedWidth
	 * @param fixedHeight
	 * @param decoratorRelPointSize
	 * @return
	 */
	public static <T> PrimitiveImagePanel<T> primitivePanel(
			Imager<T> imager, 
			String fieldName,
			boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight, double decoratorRelPointSize)
	{
		PrimitiveImagePanel<T> out = new PrimitiveImagePanel<T>();
		out.setLabelVisibility(true);
		out.setPtRelSize(decoratorRelPointSize);
		out.init(imager, fixedWidth, fixedHeight, keepAspectRatio);
		if (fieldName != null) out.setField(fieldName.toLowerCase());
		return out;
	}

	/**
	 * 
	 * @param imager
	 * @param nLabels
	 * @param nSteps
	 * @param legendWidth
	 * @param legendHeight
	 * @param offset1
	 * @param offset2
	 * @param textOffset
	 * @param pointOffset
	 * @param loToHi
	 * @param horiz
	 * @param keepAspectRatio
	 * @param ptSize
	 * @param font
	 * @param textColor
	 * @param intFmt
	 * @param dblFmt
	 * @param strFmt
	 * @return
	 */
	public static <T> LegendPanel<T> legendPanel(
			Imager<T> imager,
			int nLabels, int nSteps, int legendWidth, int legendHeight, 
			double offset1, double offset2, 
			double textOffset, double pointOffset,
			boolean loToHi, boolean horiz, boolean keepAspectRatio,
			double ptSize, Font font, Color textColor,
			String intFmt, String dblFmt, String strFmt
			)
	{





		LegendPanel<T> legend = new LegendPanel<T>();
		PrimitiveImager<T> imgr = imager.getLegendImager(nSteps, loToHi, horiz);

		legend.initLegend(
				nLabels, nSteps,
				offset1, offset2, 
				textOffset, pointOffset,
				loToHi,
				horiz, 
				font, textColor, ptSize,
				intFmt, dblFmt, strFmt);

		legend.setLabelVisibility(true);
		legend.setField(imager.getFieldName().toLowerCase());
		legend.init(imgr, legendWidth, legendHeight, keepAspectRatio);
		return legend;
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

	/**
	 * 
	 * @param panel
	 * @param fields
	 * @param menuNames
	 * @param font
	 * @param initialField
	 * @return
	 */
	public static <T> ImagePanelComboBox<T> buildComboBox(
			ObjectImagePanel<T> panel,
			List<String> fields, List<String> menuNames,
			Font font, String initialField)
	{
		int n = fields.size();
		int i = 0;
		String[] displayNames = new String[n];

		/* Set up the field and menu names. */
		if (menuNames == null)
		{
			i = 0;
			displayNames = new String[n];
			for (String st : fields) { displayNames[i] = st; i++; }
			i = 0;
			displayNames = new String[n];
			for (String st : fields) { displayNames[i] = st; i++; }
		}
		else if (menuNames.size() != n)
			throw new IllegalArgumentException("Length of menu names does not match the number of fields");
		else 
		{
			i = 0;
			displayNames = new String[n];
			for (String st : menuNames) { displayNames[i] = st; i++; }
		}
		ImagePanelComboBox<T> out = new ImagePanelComboBox<T>();
		out.fieldNames = fields;
		out.panel = panel;

		for (String st : displayNames) out.addItem(st);
		out.setFont(font);
		out.setSelectedIndex(fields.indexOf(initialField.toLowerCase()));

		logger.trace("Has legend? " + (panel.getLegend() != null));
		out.buildActionListener(panel.getLegend());

		return out;
	}

	/**
	 *  Methods to build combo boxes for choosing which field to display in an object array imager or panel.
	 * @author michaelfrancenelson
	 *
	 */
	public static class ImagePanelComboBox<T> extends JComboBox<String>
	{
		/** */
		private static final long serialVersionUID = 2409820165770045768L;
		List<String> fieldNames;
		String[] displayNames;

		ObjectImagePanel<T> panel;

		void buildActionListener(LegendPanel<T> legPanel)
		{
			addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(ActionEvent e)
				{
					String item = getSelectedItem().toString();
					String tmp = fieldNames.get(fieldNames.indexOf(item));
					panel.setField(item);
					logger.trace("ComboBox item = " + item.toString());
					logger.trace("Has legend? " + (legPanel != null));
					if (legPanel != null)
					{
						logger.debug("Updating legend image to " + tmp);
						legPanel.setField(tmp);

						Imager<T> imgr = panel.getImager();
						Imager<T> legImgr = legPanel.getImager();
						legImgr.setField(tmp);
						PrimitiveImagerData<T> legDat = imgr.getLegendData(
								legPanel.nSteps,
								legPanel.loToHi,
								legPanel.horiz);
						legImgr.setImagerData(legDat);
						legPanel.buildLegendLabels();
						legPanel.updateImage();
					}
				}
			});
		}
	}
}