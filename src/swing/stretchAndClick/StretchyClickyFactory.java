package swing.stretchAndClick;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import image.ObjectArrayImager;

public class StretchyClickyFactory
{

	/**
	 * Creates a <CODE>StretchIcon</CODE> from an array of bytes with the specified behavior.
	 *
	 * @param  imageData an array of pixels in an image format supported by
	 *             the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(byte[])
	 */
	public static StretchyClickyIcon buildIcon(byte[] imageData, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(imageData));
		out.keepAspectRatio = keepAspectRatio; 
		out.setImageAspectRatio();
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from an array of bytes.
	 *
	 * @param  imageData an array of pixels in an image format supported by
	 *             the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
	 * @param  description a brief textual description of the image
	 *
	 * @see ImageIcon#ImageIcon(byte[], java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(byte[] imageData, String description) 
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(imageData, description));
		out.setImageAspectRatio();
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from an array of bytes with the specified behavior.
	 *
	 * @see ImageIcon#ImageIcon(byte[])
	 * @param  imageData an array of pixels in an image format supported by
	 *             the AWT Toolkit, such as GIF, JPEG, or (as of 1.3) PNG
	 * @param  description a brief textual description of the image
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion o.f the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(byte[], java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(byte[] imageData, String description, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(imageData, description));
		out.keepAspectRatio = keepAspectRatio;
		out.setImageAspectRatio();
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the image.
	 *
	 * @param image the image
	 *
	 * @see ImageIcon#ImageIcon(java.awt.Image)
	 */
	public static StretchyClickyIcon buildIcon(Image image) 
	{ 
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(image));
		out.setImageAspectRatio(); 
		return out;
	} 

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the image with the specified behavior.
	 * 
	 * @param image the image
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 * 
	 * @see ImageIcon#ImageIcon(java.awt.Image) 
	 */
	public static StretchyClickyIcon buildIcon(Image image, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(image));
		out.setImageAspectRatio();
		out.keepAspectRatio = keepAspectRatio;
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the image.
	 * 
	 * @param image the image
	 * @param  description a brief textual description of the image
	 * 
	 * @see ImageIcon#ImageIcon(java.awt.Image, java.lang.String) 
	 */
	public static StretchyClickyIcon buildIcon(Image image, String description)
	{ 
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(image, description));
		out.setImageAspectRatio(); 
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the image with the specified behavior.
	 *
	 * @param image the image
	 * @param  description a brief textual description of the image
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(java.awt.Image, java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(Image image, String description, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(image, description));
		out.setImageAspectRatio();
		out.keepAspectRatio = keepAspectRatio; 
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified file.
	 *
	 * @param filename a String specifying a filename or path
	 *
	 * @see ImageIcon#ImageIcon(java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(String filename) 
	{ 
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(filename));
		out.setImageAspectRatio(); 
		return out;
	} 

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified file with the specified behavior.
	 * 
	 * @param filename a String specifying a filename or path
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(String filename, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(filename));
		out.setImageAspectRatio();
		out.keepAspectRatio = keepAspectRatio; 
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified file.
	 *
	 * @param filename a String specifying a filename or path
	 * @param  description a brief textual description of the image
	 *
	 * @see ImageIcon#ImageIcon(java.lang.String, java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(String filename, String description) 
	{ 
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(filename, description));
		out.setImageAspectRatio(); 
		return out;
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified file with the specified behavior.
	 * 
	 * @param filename a String specifying a filename or path
	 * @param  description a brief textual description of the image
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(java.awt.Image, java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(String filename, String description, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(filename, description));
		out.setImageAspectRatio();
		out.keepAspectRatio = keepAspectRatio; 
		return out;
	} 

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified URL.
	 *
	 * @param location the URL for the image
	 *
	 * @see ImageIcon#ImageIcon(java.net.URL)
	 */
	public static StretchyClickyIcon buildIcon(URL location) 
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(location));
		out.setImageAspectRatio(); 
		return out;
	} 


	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified URL with the specified behavior.
	 * 
	 * @param location the URL for the image
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(java.net.URL)
	 */
	public static StretchyClickyIcon buildIcon(URL location, boolean keepAspectRatio)
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(location));
		out.setImageAspectRatio();
		out.keepAspectRatio = keepAspectRatio; 
		return out; 
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified URL.
	 *
	 * @param location the URL for the image
	 * @param  description a brief textual description of the image
	 *
	 * @see ImageIcon#ImageIcon(java.net.URL, java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(URL location, String description) 
	{ 
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(location, description));
		out.setImageAspectRatio(); 
		return out; 
	}

	/**
	 * Creates a <CODE>StretchIcon</CODE> from the specified URL with the specified behavior.
	 * 
	 * @param location the URL for the image
	 * @param  description a brief textual description of the image
	 * @param keepAspectRatio <code>true</code> to retain the image's aspect ratio,
	 *        <code>false</code> to allow distortion of the image to fill the
	 *        component.
	 *
	 * @see ImageIcon#ImageIcon(java.net.URL, java.lang.String)
	 */
	public static StretchyClickyIcon buildIcon(URL location, String description, boolean keepAspectRatio) 
	{
		StretchyClickyIcon out = (StretchyClickyIcon) (new ImageIcon(location, description));
		out.setImageAspectRatio();
		out.keepAspectRatio = keepAspectRatio; 
		return out;
	}
	
	
	
	
	
	
	
	
	
	public static <T> StretchyClickyJLabel<T> buildLabel(
			ObjectArrayImager<T> imager, boolean, keepAspectRatio
			)
	{
		StretchyClickyJLabel<T> out = new StretchyClickyJLabel<T>();
		
		
		return out;
		
	}
	
	
	
	
	
	
	
	
}
