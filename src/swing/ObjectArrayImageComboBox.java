package swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.JComboBox;

import swing.stretchAndClick.ObjectImagePanel;
import utils.FieldUtils;

/**
 *  Methods to build combo boxes for choosing which field to display in an object array imager or panel.
 * @author michaelfrancenelson
 *
 */
public class ObjectArrayImageComboBox
{

	public static <T> JComboBox<String> comboBoxFactory(ObjectImagePanel<T> panel)
	{ return comboBoxFactory(
			panel, 
			panel.getAnnClass(), 
			null, 
			null, null); }

	public static <T> JComboBox<String> comboBoxFactory(
			ObjectImagePanel<T> panel,
			Class<? extends Annotation> annClass,
			List<Field> fields, 
			List<String> dispNames,
			Font font
			)
	{
		final List<Field> f2;
		
		if (fields == null) f2 = FieldUtils.getFields(
				panel.getObjClass(), annClass, true, true);
		else f2 = fields;

		
		
		
		if (dispNames == null)
			dispNames = FieldUtils.getFieldNames(
					f2, panel.getObjClass(), annClass, false);

		/* Verify that there are the same number of display names and fields: */
		if (f2.size() != dispNames.size()) 
			throw new IllegalArgumentException("Number of fields must be equal to the number of display names");
		JComboBox<String> out;
		
		String[] dNames = new String[dispNames.size()];
		for (int i = 0; i < dNames.length; i++) {dNames[i] = dispNames.get(i); }
		out = new JComboBox<>(dNames);

		out.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				panel.setField(f2.get(out.getSelectedIndex()));
			}
		});

		if (font != null) out.setFont(font);
		return out;
	}

}
