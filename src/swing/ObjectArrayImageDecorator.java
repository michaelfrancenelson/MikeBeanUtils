package swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import image.ObjectArrayImager;
import swing.stretchAndClick.ObjectArrayJPanel;

/**
 * 
 * @author michaelfrancenelson
 *
 */
public class ObjectArrayImageDecorator 
{
	private List<ObjectTextLabel>  labels = new ArrayList<>();
	private List<ObjectTextLabel>  valueLabels = new ArrayList<>();
	private List<ObjectPointLabel> points = new ArrayList<>();
	//	private double pointRelativeSize;

	private int objArrayWidth, objArrayHeight;
	private ObjectArrayImager<?> imager;



	public ObjectArrayImageDecorator(ObjectArrayImager<?> imager) 
	{
		this.imager = imager;
		this.objArrayWidth  = imager.getData().length;
		this.objArrayHeight = imager.getData()[0].length;
		//		this.pointRelativeSize = pointRelSize; 
	}

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
	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize)
	//	public void addLabel(int i, int j, String label, Font font, Color color, boolean keep, int pointSize, ObjectArrayJPanel<?> p)
	{
		if (label != null && font != null)
		{
			ObjectTextLabel lab = new ObjectTextLabel(i, j, label, font);
			if (keep) labels.add(lab); 
			//			p.paintComponent(p.getGraphics());
			//			p.paintComponent(p.getGraphics());
		}
		else if (label == null && font != null)
		{
			ObjectTextLabel vab = new ObjectTextLabel(i, j, null, font);
			if (keep) valueLabels.add(vab);
			//			p.paintComponent(p.getGraphics());
		}
		else 
			//			if (pointSize > 0)
		{
			ObjectPointLabel pab = new ObjectPointLabel(i, j, pointSize, color);
			System.out.println("PanelDecorator.addLabel() creating a point label");
			if (keep) 
			{
				points.add(pab);
			}
			//			p.paintComponent(p.getGraphics());
		}
		//		else throw new IllegalArgumentException("Could not add the label.  Make sure you have specified the necessary parameters");
	}

	public void drawLabels(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY)
	{
		drawTextLabels(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
		drawValueLabels(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
		drawPoints(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
	}

	public void drawTextLabels(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY) 
	{ 
		for (ObjectTextLabel l : labels)
			l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
	}

	public void drawValueLabels(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY) 
	{ 
		for (ObjectTextLabel l : valueLabels)
			l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY); 
	}

	public void drawPoints(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY) 
	{
		for (ObjectPointLabel l : points)
		{
			l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY);
		}
	}

	public void drawPoints(Graphics g, int imgWidth, int imgHeight, int imgCornerX, int imgCornerY, double relSize) 
	{

		//		/* Determine the number of pixels the relative size should be. */
		int minDim = Math.min(imgWidth, imgHeight);
		int scaledSize = (int) (((double) minDim) * relSize);
		for (ObjectPointLabel l : points)
		{
			l.draw(g, imgWidth, imgHeight, imgCornerX, imgCornerY, scaledSize);
		}
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


	private int[] getImageCoords(int objArrayI, int objArrayJ, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
	{
		double relX = ((double) objArrayI) / ((double) objArrayWidth - 1); 
		double relY = ((double) objArrayJ) / ((double) objArrayHeight - 1);

		return new int[] {
				imageCornerX + (int) (relX * ((double) imageWidth)), 
				imageCornerY + (int) (relY * ((double) imageHeight)) };
	}

	/**
	 * A label whose coordinates are calculated relative to the dimensions of the object array.
	 * @author michaelfrancenelson
	 *
	 */
	private class ObjectPointLabel
	{
		int objArrayI, objArrayJ;
		int size;
		Color color;

		ObjectPointLabel(int i, int j, int size, Color color)
		{ this.objArrayI = i; this.objArrayJ = j; this.size = size; this.color = color;	}

		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY, int scaledSize)
		{
			g.setColor(color);
			int[] coords = getImageCoords(this.objArrayI, this.objArrayJ, imageWidth, imageHeight, imageCornerX, imageCornerY);
			if (this.size > 0)
				g.fillOval(coords[0], coords[1], size, size);
			else
				g.fillOval(coords[0], coords[1], scaledSize, scaledSize);
		}

		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
		{
			int[] coords = getImageCoords(this.objArrayI, this.objArrayJ, imageWidth, imageHeight, imageCornerX, imageCornerY);
			g.fillOval(coords[0], coords[1], size, size);
		}

		//		void draw(Graphics g, ObjectArrayJPanel<?> p)
		//		{
		//			/* Need to rescale coords for the current image size */
		//			g.setColor(color);
		//			int[] coords = p.objArrayCoordsToPanelCoords(objArrayI, objArrayJ);
		//			g.fillOval(coords[0], coords[1], size, size);
		//		}
		//
		//		void draw(Graphics g, ObjectArrayJPanel<?> p, int scaledSize)
		//		{
		//			/* Need to rescale coords for the current image size */
		//			g.setColor(color);
		//			int[] coords = p.objArrayCoordsToPanelCoords(objArrayI, objArrayJ);
		//			g.fillOval(coords[0], coords[1], scaledSize, scaledSize);
		//		}

	}



	/**
	 * A label whose coordinates are calculated relative to the dimensions of the object array.
	 * @author michaelfrancenelson
	 *
	 */
	private class ObjectTextLabel
	{
		int objArrayI, objArrayJ;
		String label;
		Font font;

		ObjectTextLabel(int i, int j, String label, Font font)
		{
			this.objArrayI = i; this.objArrayJ = j; 
			this.label = label; this.font = font;
		}


		void draw(Graphics g, int imageWidth, int imageHeight, int imageCornerX, int imageCornerY)
		{
			g.setFont(font);
			int[] coords = getImageCoords(this.objArrayI, this.objArrayJ, imageWidth, imageHeight, imageCornerX, imageCornerY);
			if (label != null)
				g.drawString(this.label, coords[0], coords[1]);
			else
			{
				String l = imager.queryObjectAt(objArrayI, objArrayJ);
				g.drawString(l, coords[0], coords[1]);
			}
		}

		//		void draw(Graphics g, ObjectArrayJPanel<?> p)
		//		{
		//			g.setFont(this.font);
		//			int[] coords = p.objArrayCoordsToPanelCoords(objArrayI, objArrayJ);
		//			if (label != null)
		//				g.drawString(this.label, coords[0], coords[1]);
		//			else
		//			{
		//				String l = p.queryPixel(coords[0], coords[1]);
		//				g.drawString(l, coords[0], coords[1]);
		//			}
		//		}
	}

	/**
//	 * A label whose coordinates are calculated relative to the dimensions of the
//	 * image created from the object array.
//	 * 
//	 * 
//	 * @author michaelfrancenelson
//	 *
//	 */
	//	private static class ImagePointLabel
	//	{
	//		double imageRelX, imageRelY;
	//		int size;
	//		Color color;
	//
	//		ImagePointLabel(double relX, double relY, int size, Color color)
	//		{ this.imageRelX = relX; this.imageRelY = relY; this.size = size; this.color = color; }
	//
	//	}
	//
	//	/**
	//	 * A label whose coordinates are calculated relative to the dimensions of the
	//	 * image created from the object array.
	//	 * 
	//	 * 
	//	 * @author michaelfrancenelson
	//	 *
	//	 */
	//	private class ImageTextLabel
	//	{
	//		double imageRelX, imageRelY;
	//		String label;
	//		Font font;
	//
	//		ImageTextLabel(double relX, double relY, String label, Font font)
	//		{this.imageRelX = relX; this.imageRelY = relY; this.label = label; this.font = font;}
	//
	//
	//
	//
	//	}
}






//	public void drawTextLabels(ObjectArrayJPanel<?> p, Graphics g) 
//	{ for (ObjectTextLabel l : labels) l.draw(g, p); }
//	public void drawValueLabels(ObjectArrayJPanel<?> p, Graphics g) 
//	{ for (ObjectTextLabel l : valueLabels) l.draw(g, p); }
//	public void drawValueLabels(ObjectArrayJPanel<?> p, Graphics g) 
//	{ for (ObjectTextLabel l : valueLabels) l.draw(g, p); }

//	public void drawPoints(ObjectArrayJPanel<?> p, Graphics g)
//	{
//		if (pointRelativeSize > 0) drawPointsRelSize(p, g);
//		else
//			for (ObjectPointLabel l : points) l.draw(g, p); }
//
//	public void drawPointsRelSize(ObjectArrayJPanel<?> p, Graphics g)
//	{
//		/* Determine the number of pixels the relative size should be. */
//		int minDim = Math.min(p.getImgDisplayWidth(), p.getImgDisplayHeight());
//		int scaledSize = (int) (((double) minDim) * pointRelativeSize);
//		for (ObjectPointLabel l : points) l.draw(g, p, scaledSize);
//	}
