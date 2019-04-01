package swing;

import java.awt.Font;

import javax.swing.JLabel;

import beans.memberState.SimpleFieldWatcher;

public class MemberWatcherJLabel<T> extends JLabel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4970053063569717554L;

	private SimpleFieldWatcher<T> watcher;
	private T t;
	
	
	
	
	private MemberWatcherJLabel(T t, SimpleFieldWatcher<T> watcher)
	{
		this.t = t; this.watcher = watcher;
	}
	
	public void refresh()
	{
		setText(watcher.getStringVal(t));
	}
	
	public T getObject() { return t; }
	public SimpleFieldWatcher<T> getWatcher() { return this.watcher; }
	
	public static <T> MemberWatcherJLabel<T> factory(
			Class<T> clazz, T t, String fieldName, String displayName, String dblFmt,
			Font font)
	{
		SimpleFieldWatcher<T> watcher = SimpleFieldWatcher.factory(fieldName, dblFmt, clazz);
//		SimpleFieldWatcher<T> watcher = SimpleFieldWatcher.factory(fieldName, displayName, dblFmt, clazz);
		MemberWatcherJLabel<T> label = new MemberWatcherJLabel<T>(t, watcher);
		label.setFont(font);
		label.refresh();
		return label;
	}
	
	public String getDisplayName() { return watcher.getDisplayName(); }
	
}
