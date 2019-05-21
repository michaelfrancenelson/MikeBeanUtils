package swing.stretchAndClick;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 *  Panel with resizeable image.
 * @author michaelfrancenelson
 *
 */
public class ImagePanel extends JPanel
{

	/** */
	private static final long serialVersionUID = -5720171621052728282L;

	protected Image image = null;
	protected double imageAspectRatio, compAspectRatio;

	protected boolean centerInPanel = true;
	protected boolean fixedAspectRatio, fixedWidth, fixedHeight;

	int 
	panelWidth, panelHeight, 
	imgDisplayWidth, imgDisplayHeight, 
	imgCornerX, imgCornerY;

	void init(Image img, int width, int height, boolean keepAspectRatio) 
	{
		this.image = img;
		fixedWidth = false; fixedHeight = false;
		this.imageAspectRatio = -1;

		if ((width > 0) && (height > 0))
		{
			keepAspectRatio = true;
			this.imageAspectRatio = 
					((double) width) / ((double) height);
		}
		else
		{
			if (width > 0)  
			{
				fixedAspectRatio = false;
				fixedWidth = true;
				imgDisplayWidth = width;
			} 

			if (height > 0)
			{
				fixedAspectRatio = false;
				fixedHeight = true; 
				imgDisplayHeight = height;
			}
		}

		this.fixedAspectRatio = keepAspectRatio;
		if (this.imageAspectRatio < 0)
			this.imageAspectRatio = ((double) image.getWidth(null)) / ((double) image.getHeight(null));

		imgDisplayWidth = image.getWidth(null);
		imgDisplayHeight = image.getHeight(null);
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

		/* If keeping the original aspect ratio. */
		if (fixedAspectRatio)
		{
			if (imageAspectRatio < compAspectRatio)	
			{
				double w = (((double) panelHeight) * imageAspectRatio);
				this.imgDisplayWidth = (int) w;
			}
			else this.imgDisplayHeight = (int) (((double) panelWidth) / imageAspectRatio);
		}
		else
		{
			if (this.fixedHeight) this.imgDisplayHeight = fixedY;
			if (this.fixedWidth) this.imgDisplayWidth = fixedX;
		}

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

		g2d.drawImage(image, imgCornerX, imgCornerY, imgDisplayWidth, imgDisplayHeight, null);

		if (this.getBorder() != null) paintBorder(g2d);
		g2d.dispose();
		g.dispose();
	}
}
