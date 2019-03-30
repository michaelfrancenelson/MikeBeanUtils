package watchers;

import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.junit.Test;

import beans.builder.AnnotatedBeanBuilder;
import beans.memberState.BeanStateSetterFactory;
import beans.memberState.BeanStateSetterFactory.BeanPrimitiveFieldSetter;
import beans.memberState.SingleFieldWatcher;
import beans.sampleBeans.SimpleBean;
import swing.MemberSetterTextField;
import swing.MemberWatcherJLabel;

public class WatchersTest {

	static String fileCSV = "testData/TestBeanFactory.csv";	
	static List<SimpleBean> lc = AnnotatedBeanBuilder.factory(SimpleBean.class, fileCSV);

	static BeanPrimitiveFieldSetter<SimpleBean> s1;
	static BeanPrimitiveFieldSetter<SimpleBean> s2; 

	static SingleFieldWatcher<SimpleBean> w1;
	static SingleFieldWatcher<SimpleBean> w2;
	static SingleFieldWatcher<SimpleBean> w3;
	
	static MemberWatcherJLabel<SimpleBean> lab;
	static MemberWatcherJLabel<SimpleBean> lab2;
	
	static Font font;
	
	static JFrame f;
	static MemberSetterTextField<SimpleBean> ms3;
	static int width;
	private static MemberWatcherJLabel<SimpleBean> lab3;
	private static SingleFieldWatcher<SimpleBean> w4;
	private static MemberWatcherJLabel<SimpleBean> lab4;
	private static MemberSetterTextField<SimpleBean> ms4;
	private static MemberSetterTextField<SimpleBean> ms1;
	private static MemberSetterTextField<SimpleBean> ms2;
	public static void main(String[] args) 
//	@Before
//	public void setup()
	{
		
		
		width = 20;
		
		
		font = new Font("times", 2, 45);
		fileCSV = "testData/TestBeanFactory.csv";	
		lc = AnnotatedBeanBuilder.factory(SimpleBean.class, fileCSV);

		s1 = BeanStateSetterFactory.factory(SimpleBean.class, "i");
		s2 = BeanStateSetterFactory.factory(SimpleBean.class, "d");

		w1 = SingleFieldWatcher.factory("i", null, null, SimpleBean.class);
		w2 = SingleFieldWatcher.factory(null, "Double field d", null, SimpleBean.class);
		w3 = SingleFieldWatcher.factory("iSt", null, null, SimpleBean.class);
		w4 = SingleFieldWatcher.factory("b", null, null, SimpleBean.class);

		
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(new Dimension(1000, 1000));
		
		lab = MemberWatcherJLabel.factory(
				SimpleBean.class,
				lc.get(1), "iSt", null, null, font);
		
		lab2 = MemberWatcherJLabel.factory(
				SimpleBean.class,
				lc.get(1), "d", null, null, font);
		
		lab3 = MemberWatcherJLabel.factory(
				SimpleBean.class,
				lc.get(1), "i", null, null, font);
		lab4 = MemberWatcherJLabel.factory(
				SimpleBean.class,
				lc.get(1), "b", null, null, font);

		lab.setBorder(BorderFactory.createEtchedBorder());
		lab2.setBorder(BorderFactory.createEtchedBorder());
		lab.setHorizontalAlignment(SwingConstants.CENTER);

		ms1 = MemberSetterTextField.factory(lab);
		ms2 = MemberSetterTextField.factory(lab2);
		ms3 = MemberSetterTextField.factory(lab3);
		ms4 = MemberSetterTextField.factory(lab4);
		
		f.setLayout(new GridLayout(2, 4));
		f.add(lab);
		f.add(lab2);
		f.add(lab3);
		f.add(lab4);
		f.add(ms1);
		f.add(ms2);
		f.add(ms3);
		f.add(ms4);
		
		f.setVisible(true);
		
		
		testFrames();
	}

	
	static void testFrames()
	{

		for (SimpleBean sb : lc)
		{

			System.out.println(w1.getDisplayName() + " = " + w1.getStringVal(sb));

			System.out.println(w2.getDisplayName() + " = " + w2.getStringVal(sb));
			System.out.println(w3.getDisplayName() + " = " + w3.getStringVal(sb));
			SimpleBean.iSt ++;
			System.out.println(w3.getDisplayName() + " = " + w3.getStringVal(sb));
			SimpleBean.iSt ++;
		}

		int i = 0;
		for (SimpleBean sb : lc)
		{

			s1.set(sb, i);
			i++;

			s2.set(sb, w2.getDoubleVal(sb) - 1000000.0);
			System.out.println(w1.getDisplayName() + " = " + w1.getStringVal(sb));
			System.out.println(w2.getDisplayName() + " = " + w2.getStringVal(sb));
		}

		lab.refresh();
	
	
	}

	
	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
