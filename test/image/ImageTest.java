package image;

import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.Test;

import beans.builder.RandomBeanBuilder;
import beans.sampleBeans.SimpleBean;
import swing.SwingUtils;

public class ImageTest {

	static SimpleBean[][] beans;


	public static void main(String[] args) {
		testImage();
	}

	public static void testImage()
	{

		int nRows = 300;
		int nCols = 500;

		
		String dblFmt = "%.2f";
		
		beans = new SimpleBean[nRows][nCols];
		for (int i = 0; i < nRows; i++) for (int j = 0; j < nCols; j++)
		{
			beans[i][j] = RandomBeanBuilder.randomFactory(SimpleBean.class);
		}

		List<String> parsedB = Arrays.asList(new String[] { "i2", "d2" });

		ObjectArrayImager<SimpleBean> oai = SimpleArrayImager.factory(
				SimpleBean.class, beans,
				"i", ColorUtils.HEAT_COLORS, ColorUtils.TOPO_COLORS,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt, parsedB);

		JFrame f = SwingUtils.frameFactory(600, 600);
		f.setLayout(new GridLayout(1, 1));
		JLabel lab = new JLabel();
		JLabel labD = new JLabel();
		lab.setIcon(new ImageIcon(oai.getImage()));
		oai.setField("d2");
		labD.setIcon(new ImageIcon(oai.getImage()));
		f.add(lab);
		f.add(labD);
		f.setVisible(true);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
