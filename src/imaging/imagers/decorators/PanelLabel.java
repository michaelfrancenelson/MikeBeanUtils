package imaging.imagers.decorators;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import imaging.imagers.imagerData.ArrayImagerData;
import swing.stretchAndClick.ObjectImagePanel;
import utils.ArrayUtils;

public class PanelLabel 
{
	private double relX, relY;
	private double relSize;
	private String dblFmt = "%.2f";
	private Font font;
	private Color color;
	private String label;


	public void setDblFmt(String d) { dblFmt = d; } 
	public String getDblFmt() { return dblFmt; } 

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
			int dataX, int dataY, int dataWidth, int dataHeight, 
			double size, Font font, Color color, String label)
	{
		double coords[] = new double[] 
				{
						ArrayUtils.absToRelCoord(dataX, dataWidth),
						ArrayUtils.absToRelCoord(dataY, dataHeight)
				};						
		return fromRelImgCoords(
				coords[0], coords[1],
				size, font, color, label);
	}

	public static PanelLabel fromDataAbsoluteCoords(
			int dataX, int dataY, ArrayImagerData<?> dat, 
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
			int imageCornerX, int imageCornerY)
	{
		g.setFont(font);
		int[] coords = new int[]
				{
						imageCornerX + ArrayUtils.relToAbsCoord(relX, imageWidth),
						imageCornerY + ArrayUtils.relToAbsCoord(relY, imageHeight)
						//				};
						//		logger.debug(String.format("Cell width: %d, cell height: %d", cellWidth, cellHeight));
						//		logger.debug(String.format("Drawing annotation at (%d, %d) "
						//				+ "(%.0f%%, %.0f%%)", coords[0], coords[1], 100 * relX, 100 * relY));
				};
		if (label == null) 
		{
			logger.trace(String.format("Cell width: %d, cell height: %d", cellWidth, cellHeight));
			logger.trace(String.format("Drawing point annotation at (%d, %d) "
					+ "(%.0f%%, %.0f%%)", coords[0], coords[1], 100 * relX, 100 * relY));			
			int drawSize = (int) (relSize * (double) Math.min(imageWidth, imageHeight));
			int offset = drawSize / 2;
			int x = coords[0] - offset;
			int y = coords[1] - offset;
			logger.trace(String.format("Point offset = %d", offset));
			logger.trace(String.format("Drawing point at (%d, %d)", x, y));
			g.setColor(color);
			g.fillOval(x, y, drawSize, drawSize);
		}
		else
		{
			logger.trace(String.format("Cell width: %d, cell height: %d", cellWidth, cellHeight));
			logger.trace(String.format("Drawing text annotation at (%d, %d) "
					+ "(%.0f%%, %.0f%%)", coords[0], coords[1], 100 * relX, 100 * relY));
			FontMetrics metrics = g.getFontMetrics(font);
			int stringWidth = metrics.stringWidth(label);
			int stringHeight = metrics.getHeight();

			int heightOffset = (cellHeight / 2);
			int widthOffset = (cellWidth / 2);
			int fontHeightOffset = font.getSize() / 2;

			heightOffset = 0;
			widthOffset = 0;
			
			logger.trace(String.format("\ncellHeight = %d\nstringWidth = %d\n stringHeight = %d\n heightOffset = %d\n widthOffset = %d\n fontHeightOffset = %d", cellHeight,
					stringWidth, stringHeight, heightOffset, widthOffset, fontHeightOffset));

			int x = coords[0] + 
//					widthOffset -
					stringWidth / 2;
			int y = coords[1] +
//					heightOffset + 
					stringHeight / 2 - fontHeightOffset;
			//			-
			//					font.getSize();
			g.setFont(font);
			logger.trace(String.format("Drawing text label '%s' at (%d, %d)", label, x, y));
			g.drawString(label, x, y);
		}
	}
}
