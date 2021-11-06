package com.github.michaelfrancenelson.mikebeansutils.demos;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.github.michaelfrancenelson.mikebeansutils.beans.builder.NetCDFObjBuilder;
import com.github.michaelfrancenelson.mikebeansutils.beans.builder.RandomBeanBuilder;
import com.github.michaelfrancenelson.mikebeansutils.beans.builder.AnnotatedBeanReader.ParsedField;
import com.github.michaelfrancenelson.mikebeansutils.beans.sampleBeans.AllFlavorBean;
import com.github.michaelfrancenelson.mikebeansutils.imaging.colorInterpolator.SimpleBooleanColorInterpolator;
import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.imagerData.ImagerData;
import com.github.michaelfrancenelson.mikebeansutils.sampling.Sampling;
import com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick.ObjectImagePanel;
import com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick.PanelFactory;
import com.github.michaelfrancenelson.mikebeansutils.utils.ColorUtils;
import com.github.michaelfrancenelson.mikebeansutils.utils.FieldUtils;
import com.github.michaelfrancenelson.mikebeansutils.utils.SwingUtils;

import umontreal.ssj.rng.RandomStream;

public class BooleanDemo extends DemoConsts
{

	static RandomStream rs = Sampling.getDefaultRs();
	public static void main(String[] args) 
	{
		asBooleanDemo(1000, 1200, 10, 14, true, false);
		comboBoxDemo(500, 600, 14, 17, true, false);
	}
	
	static void asBooleanDemo(int width, int height, int nRow, int nCol, boolean show, boolean save)
	{
		List<List<AllFlavorBean>> listData = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF, false);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.REDS, Color.white);
		listData = RandomBeanBuilder.randomFactory(AllFlavorBean.class, nRow, nCol, -4, 4, rs);
		ImagerData<AllFlavorBean> dat = ImagerData.build(listData, false, false, false);
		List<String> fNames = FieldUtils.getFieldNames(AllFlavorBean.class, ParsedField.class, true, true, false, true, true);

		f1 = SwingUtils.frameFactory(width, height, "Boolean value demo", 6, 3);
		
		for (String st : fNames)
		{
			JPanel p = PanelFactory.objectPanel(
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
		List<List<AllFlavorBean>> listData = NetCDFObjBuilder.factory2D(AllFlavorBean.class, inputNCDF, false);
		c = SimpleBooleanColorInterpolator.factory(ColorUtils.REDS, Color.white);
		listData = RandomBeanBuilder.randomFactory(AllFlavorBean.class, nRow, nCol, -4, 4, rs);
		ImagerData<AllFlavorBean> dat = ImagerData.build(listData, false, false, false);
		List<String> fNames = FieldUtils.getFieldNames(AllFlavorBean.class, ParsedField.class, true, true, false, true, true);
		
		f1 = SwingUtils.frameFactory(width, height, "Boolean combo box value demo");
		
		ObjectImagePanel<AllFlavorBean> p = PanelFactory.objectPanel(
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
		GridBagConstraints cc = new GridBagConstraints();
		
		cc.weightx = 1; cc.weighty = 1;
		cc.gridy = 0; cc.fill = GridBagConstraints.BOTH;
		f1.add(p, cc);
		
		cc.weightx = 0; cc.weighty = 0;
		cc.gridy = 1;
		f1.add(cb, cc);
		f1.setVisible(show);
	}
}
