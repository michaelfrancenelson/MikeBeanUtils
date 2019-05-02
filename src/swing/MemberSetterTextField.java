package swing;

import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.annotation.Annotation;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import beans.memberState.BeanStateSetterFactory;
import beans.memberState.BeanStateSetterFactory.BeanPrimitiveFieldSetter;
import beans.memberState.FieldWatcher;
import beans.memberState.SimpleFieldWatcher;

/** Text input field for setting values of fields in an object
 * 
 * @author michaelfrancenelson
 *
 * @param <T>
 */
public class MemberSetterTextField<T> extends JTextField
{
	/**
	 */
	private static final long serialVersionUID = 3521358047052455671L;

	BeanPrimitiveFieldSetter<T> fieldSetter;
	FieldWatcher<T> watcher;
	T t;
	Font font;
	boolean entryOK;
	AbstractAction a;
	KeyAdapter k;
	MemberWatcherJLabel<T> watcherLabel;
	
	public static <T, A extends Annotation> MemberSetterTextField<T> factory(
			MemberWatcherJLabel<T> watcherLabel)
	{
		return factory(
				watcherLabel.getWatcher().getClazz(),
				watcherLabel.getWatcher().getFieldName(),
				watcherLabel.getObject(),
				watcherLabel.getFont(),
				(int) (watcherLabel.getText().length() * 1.75),
				watcherLabel.getWatcher(),
				watcherLabel
				);
	}
	
	public static <T> MemberSetterTextField<T> factory(
			Class<T> clazz,
			String fieldName,
			T t,
			Font font,
			int width, 
			SimpleFieldWatcher<T> watcher,
			MemberWatcherJLabel<T> watcherLabel
			)
	{
		MemberSetterTextField<T> m = new MemberSetterTextField<T>();
		m.setColumns(width);
		m.setFocusable(true);
		m.font = font;
		m.t = t;
		m.fieldSetter = BeanStateSetterFactory.factory(clazz, fieldName);
		
		if (watcher == null)
		m.watcher = SimpleFieldWatcher.factory(clazz, fieldName, null);
		else m.watcher = watcher;
		
		if (watcherLabel != null) m.watcherLabel = watcherLabel;
		
		m.initialize();
		return m;
	}
	
	private void initialize()
	{
		setFocusTraversalKeysEnabled(false);
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK), "myCode");
		setToCurrentValue();

		setFont(font);
		a = getSetAction();
		k = getKeyAdapter();
		addActionListener(a);
		addKeyListener(k);
	}
	public void setToCurrentValue()
	{
		setText(watcher.getStringVal(t)); 
	}

	/** Build the key adapter to respond to tab presses */
	private KeyAdapter getKeyAdapter()
	{
		KeyAdapter k = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_TAB) 
				{
					a.actionPerformed(new ActionEvent(this, 1, ""));
					if (entryOK) transferFocus();
					e.consume();
				}
			}
		};
		return k;
	}

	/** Build the setter to accept input of an appropriate type. */
	private AbstractAction getSetAction()
	{
		AbstractAction a = null;
		switch(fieldSetter.getType())
		{
		case("int"):
			a = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent e)
			{ entryOK = setInt(); }
		}; break;
		case("double"):
			a = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent e)
			{ entryOK = setDouble(); }
		}; break;
		case("String"):
			a = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent e)
			{ entryOK = setString(); }
		}; break;
		case("boolean"):
			a = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent e)
			{ entryOK = setBoolean(); }
		}; break;
		}
		if (a == null) throw new IllegalArgumentException("Field to set must be int, double, boolean, or String...");
		return a;
	}

	/** Check that input can be parsed to an int value and set field to input.*/
	private boolean setInt()
	{
		String val = getText();
		if (isCorrectFormat("int", val))
		{
			int i = Integer.parseInt(val);
			System.out.println("setting to int: " + i);
			fieldSetter.set(t, i);
			setForeground(Color.black);
			if (watcherLabel != null) watcherLabel.refresh();
			return true;
		}
		else 
		{
			infoBox("Input '" + val + "' cannot be parsed as a number.", "Input Error", font);
			setForeground(Color.red);
			return false;
		}
	}

	/** Check that input can be parsed to a double value and set field to input.*/
	private boolean setDouble()
	{
		String val = getText();

		if (isCorrectFormat("double", val))
		{
			double d = Double.parseDouble(val);
			fieldSetter.set(t, d);
			System.out.println("setting to double: " + d);
			setForeground(Color.black);
			if (watcherLabel != null) watcherLabel.refresh();
			return true;
		}
		else 
		{
			infoBox("Input '" + val + "' cannot be parsed as a number.", "Input Error", font);
			setForeground(Color.red);
			return false;
		}
	}

	/** Check that input can be parsed to a boolean value and set field to input.*/
	private boolean setBoolean()
	{
		String val = getText();

		if (isCorrectFormat("boolean", val))
		{
			boolean d = parseBool(val);
			fieldSetter.set(t, d);
			System.out.println("setting to boolean: " + d);
			setForeground(Color.black);
			if (watcherLabel != null) watcherLabel.refresh();
			return true;
		}
		else 
		{
			infoBox("Input '" + val + "' cannot be parsed as a boolean value.", "Input Error", font);
			setForeground(Color.red);
			return false;
		}

	}

	/** Set the field to a string value. */
	private boolean setString()
	{
		String val = getText();
		fieldSetter.set(t, val);
		setForeground(Color.black);
		if (watcherLabel != null) watcherLabel.refresh();
		return true;
	}

	/** Show an informational popup message
	 * 
	 *  https://stackoverflow.com/questions/7080205/popup-message-boxes
	 *  user Troyseph
	 * 
	 * @param infoMessage
	 * @param titleBar
	 * @param headerMessage
	 */
	public static void infoBox(String infoMessage, String titleBar, Font font)
	{
		JLabel label = new JLabel(infoMessage);
		label.setFont(font);
		JOptionPane.showMessageDialog(null, label, titleBar, JOptionPane.INFORMATION_MESSAGE);
	}


	/** Check that the text in the input field can be parsed to the correct type.
	 * 
	 * @param type if "int" attempts to parse an int value, "double" attempts to parse a double, "boolean" parses a boolean, default returns true.
	 * @param input string to attempt to parse
	 * @return true if input could be parsed to the 'type' parameter.
	 */
	public static boolean isCorrectFormat(String type, String input)
	{
		switch(type)
		{
		case("int"):
		{
			try {Integer.parseInt(input); return true; }
			catch (NumberFormatException e) { return false; }
		}
		case("double"):
		{
			try { Double.parseDouble(input);	return true; }
			catch (NumberFormatException e) { return false; }
		}
		case("boolean"):
		{
			try { parseBool(input); return true; }
			catch (IllegalArgumentException e) { return false; }
		}
		}
		return true;
	}

	/** Parse a string to a boolean value
	 * @param s string to parse
	 * @return matches {"true", "t", "1"} to true and {"false", "f", "0"} to false.   
	 */
	public static boolean parseBool(String s)
	{
		String ss = s.trim();
		System.out.println("parseBool(): input = " + s + ", trimmed input = " + ss);
		if (ss.equalsIgnoreCase("true")) return true;
		if (ss.equalsIgnoreCase("false")) return false;
		if (ss.equalsIgnoreCase("t")) return true;
		if (ss.equalsIgnoreCase("f")) return false;
		if (ss.equalsIgnoreCase("1")) return true;
		if (ss.equalsIgnoreCase("0")) return false;
		throw new IllegalArgumentException("Input: " + s + " could not be parsed to a boolean value");
	}

}
