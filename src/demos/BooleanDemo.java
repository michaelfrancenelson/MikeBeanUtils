package demos;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.builder.RandomBeanBuilder;
import beans.sampleBeans.AllFlavorBean;
import imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import imaging.imagers.ImagerData;
import swing.SwingUtils;
import swing.stretchAndClick.PanelFactory;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ColorUtils;
import utils.FieldUtils;

public class BooleanDemo extends DemoConsts
{

	public static void main(String[] args) 
	{
		asBooleanDemo(1000, 1200, 10, 14, true, false);
		comboBoxDemo(500, 600, 14, 17, true, false);
	}
	
	
	static void asBooleanDemo(int width, int height, int nRow, int nCol, boolean show, boolean save)
	{
		List<List<AllFlavorBean>> listData = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.REDS, Color.white);
		listData = RandomBeanBuilder.randomFactory(AllFlavorBean.class, nRow, nCol, -4, 4);
		ImagerData<AllFlavorBean> dat = ImagerData.build(listData, false, false, false);
		List<String> fNames = FieldUtils.getFieldNames(AllFlavorBean.class, ParsedField.class, true, true, false);

		f1 = SwingUtils.frameFactory(width, height, "Boolean value demo", 6, 3);
		
		for (String st : fNames)
		{
			JPanel p = PanelFactory.buildPanel(
					dat, AllFlavorBean.class, null, 
					st, 
					gradCols, boolCols, 
					-Double.MAX_VALUE,
					Integer.MIN_VALUE,
					Color.gray,
					"%.3f", fNames,
					false,
					0, 0, ptSize);
			p.setBorder(border);
			f1.add(p);
		}
		f1.setVisible(show);
	}
	static void comboBoxDemo(int width, int height, int nRow, int nCol, boolean show, boolean save)
	{
		List<List<AllFlavorBean>> listData = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.REDS, Color.white);
		listData = RandomBeanBuilder.randomFactory(AllFlavorBean.class, nRow, nCol, -4, 4);
		ImagerData<AllFlavorBean> dat = ImagerData.build(listData, false, false, false);
		List<String> fNames = FieldUtils.getFieldNames(AllFlavorBean.class, ParsedField.class, true, true, false);
		
		f1 = SwingUtils.frameFactory(width, height, "Boolean combo box value demo");
		
		ObjectImagePanel<AllFlavorBean> p = PanelFactory.buildPanel(
				dat, AllFlavorBean.class, null, 
				"intPrim", 
				gradCols, boolCols, 
				-Double.MAX_VALUE,
				Integer.MIN_VALUE,
				Color.gray,
				"%.3f", fNames,
				false,
				0, 0, ptSize);
		p.setBorder(border);
	
		JComboBox<String> cb = p.getControlComboBox(font);
		
		f1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1; c.weighty = 1;
		c.gridy = 0; c.fill = GridBagConstraints.BOTH;
		f1.add(p, c);
		
		c.weightx = 0; c.weighty = 0;
		c.gridy = 1;
		f1.add(cb, c);
		f1.setVisible(show);
	}
}
