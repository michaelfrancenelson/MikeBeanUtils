package image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import swing.ObjectArrayImageDecorator;

public class LegendPanel<T> extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4519017220282206654L;
	Image img;
	GradientLegendImager<T> imager;
	ObjectArrayImageDecorator decorator;
	
	double imageAspectRatio, compAspectRatio;

	boolean fixedWidth, fixedHeight, decorate;
	boolean centerInPanel = true;

	int 
	panelWidth, panelHeight, 
	imgDisplayWidth, imgDisplayHeight, 
	imgCornerX, imgCornerY;
	private double ptRelSize;

	/** Set the image and image scaling properties of the panel.
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @param keepAspectRatio
	 */
	void init(Image img, int width, int height, 
			boolean keepAspectRatio, 
			boolean fixedImage, ObjectArrayImager<T> imager) 
	{
		this.img = img;
		this.imageAspectRatio = ((double) img.getWidth(null)) / ((double) img.getHeight(null));

		fixedWidth = false; fixedHeight = false;
		imgDisplayWidth = img.getWidth(null);
		imgDisplayHeight = img.getHeight(null);

		if (width > 0)  
		{
			fixedWidth = true;
			imgDisplayWidth = width;
		} 

		if (height > 0)
		{
			fixedHeight = true; 
			imgDisplayHeight = height;
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
	
	public String queryPixel(int i, int j)
	{

		/* determine which cell in the data array corresponds to the input pixel */
		int relImgI = Math.max(0, Math.min(i - imgCornerX, imgDisplayWidth));;
		int relImgJ = Math.max(0, Math.min(j - imgCornerY, imgDisplayHeight));;

		double relX = ((double) relImgI) / ((double) imgDisplayWidth);
		double relY = ((double) relImgJ) / ((double) imgDisplayHeight);

		String out = queryRelative(relX, relY);
		System.out.println("Value of " + imager.getCurrentFieldName() + ": " + out);

		return out;
	}

	public String queryRelative(double relativeI, double relativeJ)
	{
		T t = imager.getObjAt(relativeI, relativeJ);
		String out = imager.getWatcher().getStringVal(t);
		return out;
	}

	
	
	@Override public void paintComponent(Graphics g)
	{
		Insets insets = getInsets();
		Graphics2D g2d = (Graphics2D) g.create();
		int fixedX = this.imgDisplayWidth, fixedY = this.imgDisplayHeight;

		this.panelWidth = getWidth() - insets.right;
		this.panelHeight = getHeight() - insets.bottom;

		this.imgDisplayWidth = panelWidth; 
		this.imgDisplayHeight = panelHeight;

		compAspectRatio = (double) panelWidth / (double) panelHeight;

//		/* If keeping the original aspect ratio. */
//		if (fixedAspectRatio)
//		{
//			if (imageAspectRatio < compAspectRatio)	
//			{
//				double w = (((double) panelHeight) * imageAspectRatio);
//				this.imgDisplayWidth = (int) w;
//			}
//			else this.imgDisplayHeight = (int) (((double) panelWidth) / imageAspectRatio);
//		}
//		else
//		{
			if (this.fixedHeight) this.imgDisplayHeight = fixedY;
			if (this.fixedWidth) this.imgDisplayWidth = fixedX;
//		}

		if (centerInPanel)
		{
			int widthRemainder = panelWidth - imgDisplayWidth;
			int heightRemainder = panelHeight - imgDisplayHeight;
			imgCornerX = (int)(0.5 * ((double) widthRemainder));
			imgCornerY = (int)(0.5 * ((double) heightRemainder));
		}
		else 
		{
			imgCornerX = 0;
			imgCornerY = 0;
		}

		g2d.drawImage(img, imgCornerX, imgCornerY, imgDisplayWidth, imgDisplayHeight, null);

		if (this.decorate)
		{
			decorator.drawLabels(g2d, imgDisplayWidth, imgDisplayHeight, imgCornerX, imgCornerY);
			decorator.drawPoints(g2d, imgDisplayWidth, imgDisplayHeight, imgCornerX, imgCornerY, ptRelSize);
		}

		g2d.dispose();
		g.dispose();
	}
	
	
}
