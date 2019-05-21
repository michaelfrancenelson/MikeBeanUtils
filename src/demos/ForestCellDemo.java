package demos;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import beans.builder.AnnotatedBeanReader.ParsedField;
import beans.builder.NetCDFObjBuilder;
import beans.sampleBeans.ForestCell;
import beans.sampleBeans.ForestSubclass;
import imaging.imagers.imagerData.ImagerData;
import swing.SwingUtils;
import swing.stretchAndClick.ObjectImagePanel;
import swing.stretchAndClick.PanelFactory;
import utils.ColorUtils;

public class ForestCellDemo extends DemoConsts
{
	static List<List<ForestCell>> forest;
	static List<List<ForestSubclass>> subForest;
	static String filename = "testData" + File.separator + "blackHills1000m.nc";

	public static void main(String[] args) 
	{
		forestDemo(1800, 1100, true);
	}

	static void forestDemo(int width, int height, boolean show)
	{
		forest = NetCDFObjBuilder.factory2D(ForestCell.class, filename);
		subForest = NetCDFObjBuilder.factory2D(ForestSubclass.class, filename);
		f1 = SwingUtils.frameFactory(width, height, "Forest Cell Demo", 2, 4);
		f2 = SwingUtils.frameFactory(width, height, "Forest Cell Subclass Demo", 2, 4);

		GridBagConstraints c = new GridBagConstraints();

		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				for (int k = 0; k < 2; k++)
				{
					JPanel pp = new JPanel(); pp.setLayout(new GridBagLayout());
					c = new GridBagConstraints();
					
					ObjectImagePanel<ForestCell> pan = PanelFactory.buildPanel(
							ImagerData.build(forest, trueFalse[k], trueFalse[j], trueFalse[i]),
							ForestCell.class, ParsedField.class, 
							"elevation",
							ColorUtils.TERRAIN_COLORS, ColorUtils.GREENS,
							null, null, null,
							"%.4f", null,
							true, 
							0, 0, ptSize);
					JComboBox<String> cbox = pan.getControlComboBox(font);
					
					c.fill = GridBagConstraints.BOTH;
					c.gridy = 0; c.gridx = 0; c.weightx = 1; c.weighty = 1;
					pp.add(pan, c);

					c.weighty = 0; c.gridx = 0;	c.gridy = 1;
					pp.add(cbox, c);

					f1.add(pp);
					
					
					JPanel pp2 = new JPanel();
					pp2.setLayout(new GridBagLayout());
					c = new GridBagConstraints();
					
					ObjectImagePanel<ForestSubclass> subPan = PanelFactory.buildPanel(
							ImagerData.build(subForest, trueFalse[k], trueFalse[j], trueFalse[i]),
							ForestSubclass.class, ParsedField.class, 
							"elevation",
							ColorUtils.TERRAIN_COLORS, ColorUtils.GREENS,
							null, null, null,
							"%.4f", null,
							true, 
							0, 0, ptSize);
					JComboBox<String> cbox2 = subPan.getControlComboBox(font);

					c.gridy = 0; c.gridx = 0; c.weightx = 1; c.weighty = 1;
					c.fill = GridBagConstraints.BOTH;
					pp2.add(subPan, c);

					c.weighty = 0; c.gridx = 0;	c.gridy = 1;
					pp2.add(cbox2, c);
					
					f2.add(pp2);
				}
		f1.setVisible(show);
		f2.setVisible(show);
		f2.setLocation(0, f1.getHeight());
	}
}
