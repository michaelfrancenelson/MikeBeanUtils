package image;

import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.Test;

import beans.builder.RandomBeanBuilder;
import beans.sampleBeans.SimpleBean;
import image.ObjectArrayImager.SingleFieldFactory;
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

		beans = new SimpleBean[nRows][nCols];
		for (int i = 0; i < nRows; i++) for (int j = 0; j < nCols; j++)
		{
			beans[i][j] = RandomBeanBuilder.randomFactory(SimpleBean.class);
		}


		ObjectArrayImager oai = SingleFieldFactory.factory(
				SimpleBean.class, beans,
				"i", ColorUtils.HEAT_COLORS,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray);
		ObjectArrayImager oai2 = SingleFieldFactory.factory(
				SimpleBean.class, beans,
				"d", ColorUtils.TERRAIN_COLORS,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray);

		JFrame f = SwingUtils.frameFactory(600, 600);
		f.setLayout(new GridLayout(1, 1));
		JLabel lab = new JLabel();
		JLabel labD = new JLabel();
		lab.setIcon(new ImageIcon(oai.getImage()));
		labD.setIcon(new ImageIcon(oai2.getImage()));
//		lab.setPreferredSize(new Dimension(400, 500));Xj
		f.add(lab);
		f.add(labD);
		f.setVisible(true);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
