package swing.stretchAndClick;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MapAndLegendPanel<T> extends JPanel
{

	/**
	 */
	private static final long serialVersionUID = -3342833816567614979L;

	protected ObjectImagePanel<T> map;
	protected LegendPanel<T> legend;
	protected JComboBox<String> layerChooser;
	protected JLabel layerChooserTitle;

	private int legendPosition;
	private int coltrolPosition;

	public void setLayout(int legPosition, int controlPosition, String controlTitle, Font controlFont, boolean border)
	{
		if (controlTitle != null)
		{
			layerChooserTitle = new JLabel(controlTitle);
			layerChooserTitle.setFont(controlFont);
		}
		
		layerChooser = map.getControlComboBox(controlFont);
		
		JPanel controlPanel = new JPanel();
		if (layerChooserTitle != null)
		{
			controlPanel.setLayout(new GridLayout(1, 2));
			controlPanel.add(layerChooserTitle);
		}
		else controlPanel.setLayout(new GridLayout(1, 1));
		controlPanel.add(layerChooser);
		
		JPanel mapPanel = new JPanel();
		mapPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		int mapX = 0, mapY = 0;
		int legendX = 0, legendY = 0;
		int controlY = 0;

		double mapWeight = 0.5;
		double legendWeight = 0.1;
		double controlWeight = 0.0;
		
		
		legendWeight = 0.1;
		
		/* if legPosition is 1 or 3, legend map/legent arrangement is vertical */
		if (legPosition == 1)  /* legend on left */
		{
			legendY = 0; mapY = 0;
			legendX = 0; mapX = 1;
		}
		else if (legPosition == 3)  /* legend on right */
		{
			legendY = 0; mapY = 0;
			legendX = 1; mapX = 0;
		}
		else if (legPosition == 2) /* legend on top */
		{
			legendX = 0; mapX = 0;
			legendY = 1; mapY = 0;
		}
		else if (legPosition == 4) /* legend on bottom */
		{
			legendX = 0; mapX = 0;
			legendY = 0; mapY = 1;
		}
		
		c.weightx = mapWeight; c.weighty = mapWeight;
		c.gridx = mapX; c.gridy = mapY;
		mapPanel.add(map, c);
		
		c.weightx = legendWeight; c.weighty = legendWeight;
		c.gridx = legendX; c.gridy = legendY;
		mapPanel.add(legend, c);
		
		
		setLayout(new GridBagLayout());
		
		if (controlPosition == 1)
		{
			mapY = 0;
			controlY = 1;
		}
		else if (controlPosition == 2)
		{
			mapY = 1; controlY = 0;
		}
		
		
		if (border)
		{
			Border b1 = BorderFactory.createLineBorder(Color.black);
			Border b2 = BorderFactory.createLineBorder(Color.red);
			map.setBorder(b2);
			legend.setBorder(b2);
			
			mapPanel.setBorder(b1);
			controlPanel.setBorder(b1);
			layerChooser.setBorder(b2);
			if (layerChooserTitle != null) layerChooserTitle.setBorder(b2);
			
		}
		
		
		c.weightx = mapWeight; c.weighty = mapWeight;
		c.gridx = 0;
		c.gridy = mapY;
		add(mapPanel, c);
		
		
		c.weightx = controlWeight; c.weighty = controlWeight;
		c.gridy = controlY;
		add(controlPanel, c);
		
	}
	
	
	
	
	
	

}
