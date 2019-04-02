package swing.stretchAndClick;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import image.ObjectArrayImager;

public class ObjectArrayJPanel<T> extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2893196948005659813L;

	ObjectArrayImager<T> imager;
	private double imageAspectRatio, compAspectRatio;
	private boolean centerInPanel = true;

	private boolean fixedAspectRatio;
	private boolean fixedWidth, fixedHeight;
	private boolean fixedImg;

	private Image img = null;

	private int panelWidth, panelHeight, imgX, imgY, imgWidth, imgHeight, cornerX, cornerY;

	/** 
	 *  If the image is derived form an <code>ObjectArrayImager</code>, refresh the image to
	 *  reflect any changes in the objects' state. 
	 */
	public void updateImage()
	{
		if (!fixedImg) img = imager.getImage();
		repaint();
	}

	/**
	 * Ignored if the label image is not derived from an <code>ObjectArrayImager</code>
	 * 
	 * @param i pixel coordinate within the panel.
	 * @param j pixel coordinate within the panel.
	 * @return a string representation of the data value of the object at the corresponding pixel.
	 */
	public String queryPixel(int i, int j)
	{
		if (fixedImg) return null;

		/* determine which cell in the data array corresponds to the input pixel */
		int relImgI = Math.max(0, Math.min(i - cornerX, imgWidth));;
		int relImgJ = Math.max(0, Math.min(j - cornerY, imgHeight));;

		//		System.out.println("queryPixel():  image corner: " + imgX + ", " + imgY);
		//		System.out.println("queryPixel():  image width:  " + imgWidth + ", height: " + imgHeight);
		//		System.out.println("queryPixel():  panel width:  " + compWidth + ", height: " + compHeight);
		//		System.out.println("queryPixel():  image coords: (" + relImgI + ", " + relImgJ + ").");

		double relX = ((double) relImgI) / ((double) imgWidth);
		double relY = ((double) relImgJ) / ((double) imgHeight);

		String out = query(relX, relY);
		System.out.println("Value of " + imager.getCurrentFieldName() + ": " + out);

		return out;
	}

	public String query(double relativeI, double relativeJ)
	{
		if (fixedImg) return null;
		T t = imager.getObjAt(relativeI, relativeJ);
		String out = imager.getWatcher().getStringVal(t);
		return out;
	}

	/** Set the image and image scaling properties of the panel.
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio
	 */
	private void init(Image img, int width, int height, boolean keepAspectRatio) 
	{
		this.img = img;
		this.fixedAspectRatio = keepAspectRatio;
		this.imageAspectRatio = ((double) img.getWidth(null)) / ((double) img.getHeight(null));
		//		this.imageAspectRatio = ((double) img.getHeight(null)) / ((double) img.getWidth(null));

		fixedWidth = false; fixedHeight = false;
		imgWidth = img.getWidth(null);
		imgHeight = img.getHeight(null);

		if (width > 0)  
		{
			fixedAspectRatio = false;
			fixedWidth = true; 
			imgWidth = width;
		} 

		if (height > 0)
		{
			fixedAspectRatio = false;
			fixedHeight = true; 
			imgHeight = height;
		}

		this.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent arg0)
			{
				queryPixel(arg0.getX(), arg0.getY());
				System.out.println("MouseListener: Mouse clicked at panel coordinate (" +
						arg0.getX() + ", " + arg0.getY() + ").");
			}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseReleased(MouseEvent arg0) {}
		});
	}

	/**
	 *  Build a panel using an image file.  The panel won't return any values when clicked
	 *  and refresh methods will have no effect.
	 *  
	 * @param imageFile
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
	public static <T> ObjectArrayJPanel<T> buildPanel(
			String imageFile, boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight)
	{
		ObjectArrayJPanel<T> out = new ObjectArrayJPanel<T>();
		Image img = null;
		try {
			img = ImageIO.read(new File(imageFile));
		} catch (IOException e) { 
			e.printStackTrace();
		}
		out.fixedImg = true;
		out.init(img, fixedWidth, fixedHeight, keepAspectRatio);

		return out;
	}

	/**
	 *  Build a panel using an <code>ObjectArrayImager</code> to generate the image from the states of
	 *  objects in a 2D array.
	 * 
	 * @param imager 
	 * @param keepAspectRatio Should the aspect ratio of the image be maintained if the window is resized?
	 *                        If false, the image will stretch to fill the window if it is resized.
	 *                        If <code>fixedWidth</code> or <code>fixedHeight</code> are greater than 0, 
	 *                        this parameter is ignored.
	 * @param fixedWidth      If greater than 0, the width of the image will remain constant 
	 *                        if the window is resized. The image height may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @param fixedHeight     If greater than 0, the height of the image will remain constant 
	 *                        if the window is resized. The width may still adjust to resizing.
	 *                        Values of 0 or less are ignored.
	 * @return
	 */
	public static <T> ObjectArrayJPanel<T> buildPanel(
			ObjectArrayImager<T> imager, boolean keepAspectRatio, 
			int fixedWidth, int fixedHeight)
	{
		ObjectArrayJPanel<T> out = new ObjectArrayJPanel<T>();
		out.imager = imager;
		out.fixedImg = false;
		out.init(imager.getImage(), fixedWidth, fixedHeight, keepAspectRatio);

		return out;
	}

	@Override public void paint(Graphics g)
	{
		Insets insets = getInsets();

		int fixedX = this.imgWidth, fixedY = this.imgHeight;

		this.panelWidth = getWidth() - imgX - insets.right;
		this.panelHeight = getHeight() - imgY - insets.bottom;

		this.imgWidth = panelWidth; 
		this.imgHeight = panelHeight;

		compAspectRatio = (double) panelWidth / (double) panelHeight;

		/* If keeping the original aspect ratio. */
		if (fixedAspectRatio)
		{
			if (imageAspectRatio < compAspectRatio)	
			{
				double w = (((double) panelHeight) * imageAspectRatio);
				this.imgWidth = (int) w;
			}
			else this.imgHeight = (int) (((double) panelWidth) / imageAspectRatio);
		}
		else
		{
			if (this.fixedHeight) this.imgHeight = fixedY;
			if (this.fixedWidth) this.imgWidth = fixedX;
		}

		if (centerInPanel)
		{
			int widthRemainder = panelWidth - imgWidth;
			int heightRemainder = panelHeight - imgHeight;
			cornerX = imgX + (int)(0.5 * ((double) widthRemainder));
			cornerY = imgY + (int)(0.5 * ((double) heightRemainder));

		}
		else 
		{
			cornerX = imgX;
			cornerY = imgY;
		}
		g.drawImage(img, cornerX, cornerY, imgWidth, imgHeight, null);
	}
}
