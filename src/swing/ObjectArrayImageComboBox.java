package swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
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
	public static class BeanComboBox<T> extends JComboBox<String>
	{
		/** */
		private static final long serialVersionUID = 2409820165770045768L;
		List<String> fieldNames;
		String[] displayNames;

		ObjectImagePanel<T> panel;

		public static <T> BeanComboBox<T> build(
				ObjectImagePanel<T> panel, List<String> fields, List<String> menuNames, Font font, String initialField)
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
			BeanComboBox<T> out = new BeanComboBox<T>();
			out.fieldNames = fields;
			out.panel = panel;

			for (String st : displayNames) out.addItem(st);
			out.setFont(font);
			out.setSelectedIndex(fields.indexOf(initialField.toLowerCase()));
			out.buildActionListener();
			
			return out;
		}
		void buildActionListener()
		{
			addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(ActionEvent e)
				{
					String item = getSelectedItem().toString();
					System.out.println("ComboBox item = " + item.toString());
					panel.setField(fieldNames.get(fieldNames.indexOf(item)));
				}
			});
		}
	}
}