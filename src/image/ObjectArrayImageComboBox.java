package image;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.swing.JComboBox;

import fields.FieldUtils;
import swing.stretchAndClick.ObjectArrayJPanel;

/**
 *  Methods to build combo boxes for choosing which field to display in an object array imager or panel.
 * @author michaelfrancenelson
 *
 */
public class ObjectArrayImageComboBox
{

	public static <T> JComboBox<String> comboBoxFactory(ObjectArrayJPanel<T> panel)
	{ return comboBoxFactory(panel, null, null, null); }

	public static <T> JComboBox<String> comboBoxFactory(
			ObjectArrayJPanel<T> panel,
			Field[] fields, 
			String[] dispNames,
			Font font
			)
	{
		final Field[] f2;
		
		if (fields == null) f2 = FieldUtils.getInstanceFields(panel.getObjClass());
		else f2 = fields;

		if (dispNames == null)
			dispNames = FieldUtils.getInstanceFieldNames(Arrays.asList(f2));

		/* Verify that there are the same number of display names and fields: */
		if (f2.length != dispNames.length) 
			throw new IllegalArgumentException("Number of fields must be equal to the number of display names");
		JComboBox<String> out;
		out = new JComboBox<>(dispNames);
		
		out.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				panel.setField(f2[out.getSelectedIndex()]);
			}
		});

		if (font != null) out.setFont(font);
		return out;
	}

}
