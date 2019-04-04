package swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import swing.stretchAndClick.ObjectArrayJPanel;

/**
 * 
 * @author michaelfrancenelson
 *
 */
public class PanelDecorator 
{
	private List<TextLabel>  labels = new ArrayList<>();
	private List<TextLabel>  valueLabels = new ArrayList<>();
	private List<PointLabel> points = new ArrayList<>();
	private double pointRelativeSize;

	public PanelDecorator(double pointRelSize) {this.pointRelativeSize = pointRelSize; }

	/**
	 * 
	 * @param i x-coordinate (array index of data array) of the label
	 * @param j y-coordinate (array index of data array) of the label
	 * @param label text of the label.  If <code>null</code>, label will be 
	 *              either a point, or the text will display the value of the object at [i][j]
	 * @param font display font (if applicable)
	 * @param color color for text or point 
	 * @param keep  keep the decoration in the record of points so it will be redrawn later?
	 * @param p     Panel to decorate.  If null, the label will not be drawn.
	 */
	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize, ObjectArrayJPanel<?> p)
	{
		if (label != null && font != null)
		{
			TextLabel lab = new TextLabel(i, j, label, font);
			if (keep) labels.add(lab); 
			p.paintComponent(p.getGraphics());
		}
		else if (label == null && font != null)
		{
			TextLabel vab = new TextLabel(i, j, null, font);
			if (keep) valueLabels.add(vab);
			p.paintComponent(p.getGraphics());
		}
		else if (pointSize > 0)
		{
			PointLabel pab = new PointLabel(i, j, pointSize, color);
			System.out.println("PanelDecorator.addLabel() creating a point label");
			if (keep) 
			{
				points.add(pab);
			}
			p.paintComponent(p.getGraphics());
		}
		else throw new IllegalArgumentException("Could not add the label.  Make sure you have specified the necessary parameters");
	}

	public void drawLabels(ObjectArrayJPanel<?> p, Graphics g)
	{
		drawTextLabels(p, g); 
		drawValueLabels(p, g);
	}

	public void drawTextLabels(ObjectArrayJPanel<?> p, Graphics g) 
	{ for (TextLabel l : labels) l.draw(g, p); }
	public void drawValueLabels(ObjectArrayJPanel<?> p, Graphics g) 
	{ for (TextLabel l : valueLabels) l.draw(g, p); }

	public void drawPoints(ObjectArrayJPanel<?> p, Graphics g)
	{
		if (pointRelativeSize > 0) drawPointsRelSize(p, g);
		else
			for (PointLabel l : points) l.draw(g, p); }

	public void drawPointsRelSize(ObjectArrayJPanel<?> p, Graphics g)
	{
		/* Determine the number of pixels the relative size should be. */
		int minDim = Math.min(p.getImgWidth(), p.getImgHeight());
		int scaledSize = (int) (((double) minDim) * pointRelativeSize);
		for (PointLabel l : points) l.draw(g, p, scaledSize);
	}

	public void clearAll()
	{
		labels = new ArrayList<>();
		valueLabels = new ArrayList<>();
		points = new ArrayList<>();
	}

	public void clearLabels() { labels = new ArrayList<>(); }
	public void clearValueLabels() { valueLabels = new ArrayList<>(); }
	public void clearPoints() { points = new ArrayList<>(); }

	private static class PointLabel
	{
		int x, y;
		int size;
		Color color;

		PointLabel(int i, int j, int size, Color color)
		{
			this.x = i; this.y = j; this.size = size; this.color = color;
		}

		void draw(Graphics g, ObjectArrayJPanel<?> p)
		{
			/* Need to rescale coords for the current image size */
			g.setColor(color);
			int[] coords = p.objArrayCoordsToPanelCoords(x, y);
			g.fillOval(coords[0], coords[1], size, size);
		}

		void draw(Graphics g, ObjectArrayJPanel<?> p, int scaledSize)
		{
			/* Need to rescale coords for the current image size */
			g.setColor(color);
			int[] coords = p.objArrayCoordsToPanelCoords(x, y);
			g.fillOval(coords[0], coords[1], scaledSize, scaledSize);
		}

	}

	private static class TextLabel
	{
		int x, y;
		String label;
		Font font;

		TextLabel(int i, int j, String label, Font font)
		{
			this.x = i; this.y = j; 
			this.label = label; this.font = font;
		}

		void draw(Graphics g, ObjectArrayJPanel<?> p)
		{
			g.setFont(this.font);
			int[] coords = p.objArrayCoordsToPanelCoords(x, y);
			if (label != null)
				g.drawString(this.label, coords[0], coords[1]);
			else
			{
				String l = p.queryPixel(coords[0], coords[1]);
				g.drawString(l, coords[0], coords[1]);
			}
		}
	}
}
