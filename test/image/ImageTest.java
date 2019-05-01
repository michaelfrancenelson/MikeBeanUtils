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
import beans.sampleBeans.AllFlavorBean;
import image.arrayImager.BeanImager;
import swing.SwingUtils;
import utils.ColorUtils;

public class ImageTest {

	static AllFlavorBean[][] beans;


	public static void main(String[] args) {
		testImage();
	}

	public static void testImage()
	{

		int nRows = 30;
		int nCols = 50;

		
		String dblFmt = "%.2f";
		
		beans = new AllFlavorBean[nRows][nCols];
		for (int i = 0; i < nRows; i++) for (int j = 0; j < nCols; j++)
		{
			beans[i][j] = RandomBeanBuilder.randomFactory(AllFlavorBean.class);
		}

		List<String> parsedB = Arrays.asList(new String[] { "i2", "d2" });

		BeanImager<AllFlavorBean> oai;
		
		
		
		
//		oai = SimpleArrayImager.factory(
//				AllFlavorBean.class, beans,
//				"i", ColorUtils.HEAT_COLORS, ColorUtils.TOPO_COLORS,
//				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
//				dblFmt, parsedB
//				);

		oai = SimpleArrayImagerWithLegend.factory(
				AllFlavorBean.class, beans,
				"i", ColorUtils.HEAT_COLORS, ColorUtils.TOPO_COLORS,
				Double.MIN_VALUE, Integer.MIN_VALUE, Color.gray,
				dblFmt, parsedB,
				true, 100, 100);
				
		
		JFrame f = SwingUtils.frameFactory(600, 600);
		f.setLayout(new GridLayout(1, 1));
		JLabel lab = new JLabel();
		JLabel labD = new JLabel();
		lab.setIcon(new ImageIcon(oai.getImage()));
		oai.setField("d2");
		labD.setIcon(new ImageIcon(((SimpleArrayImagerWithLegend<AllFlavorBean>) oai).getLegend().getImage()));
		f.add(lab);
		f.add(labD);
		f.setVisible(true);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
