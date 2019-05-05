package imaging.imagers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import swing.stretchAndClick.ObjectImagePanel;
import utils.ArrayUtils;

public class PanelLabel 
{
	private double relX, relY;
	private int size;
	private Font font;
	private Color color;
	private String label;

	public static PanelLabel fromImgAbsoluteCoords(
			int imgX, int imgY, ObjectImagePanel<?> pan,
			int size, Font font, Color color, String label)
	{
		double coords[] = new double[] 
				{
						ArrayUtils.absToRelCoord(imgX, pan.getImgDisplayWidth()),
						ArrayUtils.absToRelCoord(imgY, pan.getImgDisplayHeight())
				};						

		return fromRelImgCoords(
				coords[0], coords[1],
				size, font, color, label);
	}

	public static PanelLabel fromDataAbsoluteCoords(
			int dataX, int dataY, ArrayData<?> dat, 
			int size, Font font, Color color, String label)
	{
		double coords[] = new double[] 
				{
						ArrayUtils.absToRelCoord(dataX, dat.getWidth()),
						ArrayUtils.absToRelCoord(dataY, dat.getHeight())
				};						
		return fromRelImgCoords(
				coords[0], coords[1],
				size, font, color, label);
	}

	public static PanelLabel fromRelImgCoords(
			double imgRelX, double imgRelY, 
			int size, Font font, Color color, String label)
	{
		return new PanelLabel(imgRelX, imgRelY, size, font, color, label);
	}

	private PanelLabel(double x, double y, int size, Font fnt, Color col, String lbl)
	{
		this.relX = x; this.relY = y;
		this.size = size; font = fnt; color = col; label = lbl;
	}

	public void draw(
			Graphics g, int imageWidth, int imageHeight,
			int cellWidth, int cellHeight,
//			int dataWidth, int dataHeight,
			int imageCornerX, int imageCornerY)
	{
		g.setFont(font);
		int[] coords = new int[]
				{
						ArrayUtils.relToAbsCoord(relX, imageWidth),
						ArrayUtils.relToAbsCoord(relY, imageHeight)
				};
		if (label == null) 
		{
			int offset = size / 2;
			g.setColor(color);
			g.drawOval(coords[0] - offset, coords[1] - offset, size, size);
		}
		else
		{
			
			FontMetrics metrics = g.getFontMetrics(font);
			// Determine the X coordinate for the text
			int x = coords[0] + (cellWidth - metrics.stringWidth(label)) / 2;
			// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
			int y = coords[1] + ((cellHeight - metrics.getHeight()) / 2) + metrics.getAscent();
			// Set the font
			g.setFont(font);
			// Draw the String
			g.drawString(label, x, y);
		}
	}
}
