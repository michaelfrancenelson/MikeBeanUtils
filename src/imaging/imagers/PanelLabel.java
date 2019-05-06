package imaging.imagers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swing.stretchAndClick.ObjectImagePanel;
import utils.ArrayUtils;

public class PanelLabel 
{
	private double relX, relY;
	private double relSize;
	private Font font;
	private Color color;
	private String label;

	static Logger logger = LoggerFactory.getLogger(PanelLabel.class);


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
			double size, Font font, Color color, String label)
	{
		return new PanelLabel(imgRelX, imgRelY, size, font, color, label);
	}

	private PanelLabel(
			double relWidth, double relHeight, double size,
			Font fnt, Color col, String lbl)
	{
		this.relX = relWidth; this.relY = relHeight;
		this.relSize = size; font = fnt; 
		color = col; 
		if (lbl != null)
			label = lbl.trim();
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
						imageCornerX + ArrayUtils.relToAbsCoord(relX, imageWidth),
						imageCornerY + ArrayUtils.relToAbsCoord(relY, imageHeight)
				};
		//		logger.debug(String.format("Cell width: %d, cell height: %d", cellWidth, cellHeight));
		//		logger.debug(String.format("Drawing annotation at (%d, %d) "
		//				+ "(%.0f%%, %.0f%%)", 
		//				coords[0], coords[1], 100 * relX, 100 * relY));
		if (label == null) 
		{
			int drawSize = (int) (relSize * (double) Math.min(imageWidth, imageHeight));
			
			int offset = drawSize / 2;
			g.setColor(color);
			g.fillOval(coords[0] - offset, coords[1] - offset, drawSize, drawSize);
		}
		else
		{

			FontMetrics metrics = g.getFontMetrics(font);
			int stringWidth = metrics.stringWidth(label);
			// Determine the X coordinate for the text
			int x = coords[0] + cellWidth / 2 -
					stringWidth / 2;
			//					2 * cellWidth +
			//				(cellWidth / 2) - 
			//					metrics.stringWidth(label);
			// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
			int y = coords[1] + (cellHeight / 2) + 
					//					metrics.getAscent()/2 +
					metrics.getHeight()/1 -
					font.getSize();
			//			+ metrics.getHeight()/ 2);
			//			+
			//					(cellHeight / 2) -
			//					2 * cellHeight +
			//					(cellHeight / 2) -
			//					metrics.getHeight() / 2;
			//					metrics.getAscent();
			// Set the font
			g.setFont(font);
			// Draw the String
			//			logger.debug(String.format("Drawing text label at (%d, %d)", x, y));
			g.drawString(label, x, y);
		}
	}
}
