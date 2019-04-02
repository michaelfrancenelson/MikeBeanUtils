package swing.stretchAndClick;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.LocalTime;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import image.ColorInterpolator;
import image.ObjectArrayImager;
import images.ArrayDataImageBundle;
import images.ArrayDataImageBundle.ArrayDataImageFactory;
import sequences.Sequences;


/** A JLabel displaying data that can be queried by coordinates in the original
 *  size of the data array. <br>
 *  
 *  Has the following drawing functionality: <br>
 *  
 * @author Home
 *
 */
public class StretchyClickyJLabel<T> extends JLabel implements MouseMotionListener
{
	private static final long serialVersionUID = 4027691138537175088L;
	public static final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 50);

//	static Logger logger = LoggerFactory.getLogger(StretchyClickyDataJLabel.class);
	
	private ObjectArrayImager<?> imager;
	protected StretchyClickyIcon icon;
	protected boolean verbose = false;

	
	
	
	
	
	
	
	
	
	
	protected boolean  addPoints = false;
	protected int[]    pointsDataArrayColumns, pointsDataArrayRows, pointsColorCodes;
	protected Color    pointColor;
	private   double   pointScale = 1.0;

	protected boolean  addText = false;
	protected int[]    textDataArrayColumns, textDataArrayRows;
	protected String[] textLabels;
	protected Color    textColor = Color.black;
	protected Font     font = DEFAULT_FONT;

	protected ColorInterpolator ciPoints;
	protected boolean           colorMap = false;

	protected boolean keepIconProportion;
	protected int     iconFixedWidth;

	protected Double textOffsetY = null, textOffsetX = null;

	protected MouseHoverData hover;

	protected MouseListener mmm = new MouseListener() {
		@Override public void mouseClicked(MouseEvent arg0) { 
			icon.setMouseEventCoords(arg0);

			if (verbose)
			{
				System.out.println();
				icon.printComponentClick();
				icon.printDisplayImageClick();
				icon.printComponentClickRatio();
				icon.printDisplayImageClickRatio();
				icon.printDataClick();
				System.out.println();
			}
			printIconQuery();
		}

		@Override public void mouseEntered(MouseEvent arg0) {}
		@Override public void mouseExited(MouseEvent arg0) {}
		@Override public void mousePressed(MouseEvent arg0) {}
		@Override public void mouseReleased(MouseEvent arg0) {}
	};

	/**
	 * @param ap
	 * @param keepIconProportion keep the image aspect ratio the same as the size of the data in ap? 
	 * @param iconFixedWidth Keep the width of the image constant (but adjust height and aspect ratio)?
	 * @param pointScale Scaling factor for the size of points drawn over the image
	 * @return
	 */
	public static StretchyClickyJLabel factory(
			ArrayDataImageBundle ap, boolean keepIconProportion, int iconFixedWidth)
	{ return factory(ap, keepIconProportion, iconFixedWidth, null, null); }
	/**
	 * @param ap
	 * @param keepIconProportion keep the image aspect ratio the same as the size of the data in ap? 
	 * @param iconFixedWidth Keep the width of the image constant (but adjust height and aspect ratio)?
	 * @param pointScale Scaling factor for the size of points drawn over the image
	 * @return
	 */
	public static StretchyClickyJLabel factory(
			ArrayDataImageBundle ap, boolean keepIconProportion, int iconFixedWidth,
			Double textOffsetX, Double textOffsetY)
	{
		StretchyClickyJLabel s = new StretchyClickyJLabel();
		s.imageDataBundle = ap; 
		s.keepIconProportion = keepIconProportion;
		s.iconFixedWidth = iconFixedWidth;
		s.textOffsetX = textOffsetX;
		s.textOffsetY = textOffsetY;
		s.setIcon();
		s.addMouseMotionListener(s);
		return s;
	}

	/** A label with fixed image aspect ratio and points at same size as data pixels. */
	public static StretchyClickyJLabel factory(ArrayDataImageBundle ap, boolean keepIconProportion)
	{	return factory(ap, keepIconProportion, Integer.MIN_VALUE);}
	
	public static StretchyClickyJLabel factory(
			ArrayDataImageBundle ap, boolean keepIconProportion,
			Double textOffsetX, Double textOffsetY)	
	{	return factory(ap, keepIconProportion, Integer.MIN_VALUE, textOffsetX, textOffsetY); }

	/** A label with adjusting image aspect ratio but fixed width and points stretched to be the same size as data pixels. */
	public static StretchyClickyJLabel factory(ArrayDataImageBundle ap, int iconFixedWidth)	
	{ return factory(ap, false, iconFixedWidth); }
	public static StretchyClickyJLabel factory(ArrayDataImageBundle ap, int iconFixedWidth, 
			Double textOffsetX, Double textOffsetY)	
	{ return factory(ap, false, iconFixedWidth, textOffsetX, textOffsetY); }

	public StretchyClickyJLabel() { addMouseListener(mmm); this.hover = new MouseHoverData(); }

	public void buildPointColorInterpolator(Color[] colors, int min)
	{	
		this.colorMap = true;
		ColorInterpolator ci = new ColorInterpolator(colors, min, min + colors.length - 1, Integer.MIN_VALUE, Color.gray); 
		this.ciPoints = ci;
	}

	public void updateBundle(ArrayDataImageBundle bundle)
	{ this.imageDataBundle = bundle; setIcon(); }

	public void updateBundle(ArrayDataImageBundle bundle, StretchyClickyIcon icon)
	{ 
		this.imageDataBundle = bundle; 
		setIcon(); icon.setMatchIcon(icon); }

	private void setIcon() { setIcon(imageDataBundle.createIcon(this.keepIconProportion, this.iconFixedWidth)); }

	/** Print the data value at the current query coordinates to the console. */
	public void printIconQuery()
	{
		System.out.println(String.format(
				"Value of %s at (%d, %d): %s", 
				imageDataBundle.getName(), icon.getClickedDataX(), icon.getClickedDataY(),
				queryIconDataArray()));
	}

	/** Get the string value of the data array at the current query coordinates. */
	public String queryIconDataArray() 
	{ return imageDataBundle.getDataArrayVal(icon.getClickedDataY(), icon.getClickedDataX()); }

	/** Get the string value of the data array at the input data array query coordinates. */
	public String queryIconDataArray(int dataArrayY, int dataArrayX)
	{ return imageDataBundle.getDataArrayVal(dataArrayY, dataArrayX); }

	/**
	 * NOTE:  The input coords are in relation to the size of the dat array,
	 * 			not absolute coordinates on the currently displayed image.
	 * @param dataArrayRows Row coordinates in terms of the underlying data array
	 * @param dataArrayColumns Column coordinates in terms of the underlying data array
	 * @param colorMapCodes
	 */
	public void drawPointsOnIcon(int[] dataArrayRows, int[] dataArrayColumns, int[] colorMapCodes, double pointScale)
	{
		this.addPoints = true;
		this.pointsDataArrayColumns = dataArrayColumns; 
		this.pointsDataArrayRows = dataArrayRows; 
		this.pointsColorCodes = colorMapCodes;
		this.pointScale = pointScale;
		repaint();
	}

	/**
	 * 
	 * @param dataArrayRows Row coordinates in terms of the underlying data array
	 * @param dataArrayColumns Column coordinates in terms of the underlying data array
	 * @param color
	 */
	public void drawPointsOnIcon(int[] dataArrayRows, int[] dataArrayColumns, Color color, double pointScale)
	{
		this.addPoints = true;
		this.pointsDataArrayColumns = dataArrayColumns; 
		this.pointsDataArrayRows = dataArrayRows;
		this.pointColor = color;
		this.pointScale = pointScale;
		repaint();
	}

	/**
	 * 
	 * @param dataArrayRows Row coordinates in terms of the underlying data array
	 * @param dataArrayColumns Column coordinates in terms of the underlying data array
	 * @param color
	 * @param font
	 */
	public void drawTextValuesOnIcon(int[] dataArrayRows, int[] dataArrayColumns, Color color, Font font)
	{
		this.addText = true;
		this.textDataArrayColumns = dataArrayColumns; this.textDataArrayRows = dataArrayRows; 
		this.textColor = color; this.font = font;

		String[] labels = new String[dataArrayRows.length];
		for (int i = 0; i < labels.length; i++) 
			labels[i] = imageDataBundle.getDataArrayVal(textDataArrayRows[i], textDataArrayColumns[i]);
		this.textLabels = labels;
		repaint();
	}


	public void drawTextValuesOnIcon(
			int[] dataArrayRows, int dataArrayColumn, Color color, Font font)
	{
		int[] dataArrayColumns = new int[dataArrayRows.length];
		for (int i = 0; i < dataArrayColumns.length; i++) {
			dataArrayColumns[i] = dataArrayColumn;}
		drawTextValuesOnIcon(dataArrayRows, dataArrayColumns, color, font);
	}

	/**
	 * 
	 * @param dataArrayRows Row coordinates in terms of the underlying data array
	 * @param dataArrayCols Column coordinates in terms of the underlying data array
	 * @param labels
	 * @param color
	 * @param font
	 */
	public void drawTextOnIcon(int[] dataArrayRows, int[] dataArrayColumns, String[] labels, Color color, Font font)
	{	
		this.addText = true;
		this.textColor = color; this.font = font;
		this.textDataArrayColumns = dataArrayColumns; 
		this.textDataArrayRows = dataArrayRows; 
		this.textLabels = labels;
		repaint();
	}

	/** Hide the text, but don't delete it (it can be redrawn later.) */
	public void hideText() { this.addText = false; repaint(); }
	/** Hide the points, but don't delete it (it can be redrawn later.) */
	public void hidePoints() { this.addPoints = false; repaint(); }
	/** Permanently remove the points form the image icon. */
	public void deletePoints() {this.pointsDataArrayRows = null; this.pointsDataArrayColumns = null; this.pointColor = null; this.addPoints = false; repaint(); }
	/** Permanently remove the points form the image icon. */
	public void deleteText() { this.addText = false; this.textDataArrayRows = null; this.textDataArrayColumns = null; repaint(); }	

	private void drawText(Graphics g)
	{
		if (addText)
		{
			int pixWidth = icon.getDataPixelWidth();
			int pixHeight = icon.getDataPixelHeight();

			if (verbose) System.out.println("StretchyClickyDataJLabel: drawing text");

			g.setColor(this.textColor);	
			g.setFont(this.font);

			int pixAdjX, pixAdjY;

			pixAdjX = pixWidth / 2;
			pixAdjY = pixHeight / 2;

			if (textOffsetX != null) pixAdjX += (int)(textOffsetX * ((double) pixWidth));
			if (textOffsetY != null) pixAdjY += (int)(textOffsetY * ((double) pixHeight));

			if (verbose) System.out.println("StretchyClickyDataJLabel: pixel adjust x: " + pixAdjX + ", pix adjust y: " + pixAdjY);

			for (int i = 0; i < textDataArrayRows.length; i++) 
			{
				int displayX = icon.componentXFromDataCol(textDataArrayColumns[i]) + pixAdjX;
				int displayY = icon.componentYFromDataRow(textDataArrayRows[i]) + pixAdjY;
				g.drawString(this.textLabels[i], displayX, displayY);
			}
		}
	}

	private void drawPoints(Graphics g)
	{
		if (addPoints)
		{
			if (verbose) System.out.println("StretchyClickyDataJLabel: drawing points");
			int pointWidth = Math.max(1, icon.getDataPixelWidth());
			int pointHeight = Math.max(1, icon.getDataPixelHeight());
			g.setColor(this.pointColor);	

			int scaledPointWidth = (int) (pointScale * (double) pointWidth);
			int scaledPointHeight = (int) (pointScale * (double) pointHeight);

//			logger.debug("point diameter: " + scaledPointWidth);
			
			int offsetX = (pointWidth - scaledPointWidth) / 2;
			int offsetY = (pointHeight - scaledPointHeight) / 2;

			for (int i = 0; i < pointsDataArrayRows.length; i++) 
			{
				if (colorMap)
				{
					Color c = new Color(ciPoints.getColor(pointsColorCodes[i]));
					g.setColor(c);
				}

				int displayX = icon.componentXFromDataCol(pointsDataArrayColumns[i]);
				int displayY = icon.componentYFromDataRow(pointsDataArrayRows[i]);
				g.fillOval(displayX + offsetX, displayY + offsetY, scaledPointWidth, scaledPointHeight);
			}	
		}
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		if (verbose) System.out.println("StretchyClickyDataJLabel repainting JLabel...");

		drawPoints(g);
		drawText(g);
	}

	public void setMatchIcon(StretchyClickyIcon i) { this.icon.setMatchIcon(i); }

	public void setVerbose(boolean b) { this.verbose = b; }
	private void setIcon(StretchyClickyIcon icon) { this.icon = icon; super.setIcon(icon);}
	public Graphics getIconGraphics() { return icon.getImage().getGraphics(); }


	public static void main(String[] args) {
		demo_legend();
		demo_draw_points_on_icon();
		//		demo_update_bundle();
	}

	public static void demo_legend()
	{
		JFrame frame = getFrame("ClickyStretchyLabel with legend demo");
		StretchyClickyJLabel map, legend;

		int legendWidth = 128;

		int nTicks = 5;

		int nRow, nCol;
		int[] ticksRows;

		nRow = 20; nCol = 30;


		map = StretchyClickyJLabel.factory(
				ArrayDataImageFactory.getRandomBundle(nRow, nCol),
				true, 
				-9999);

		legend = StretchyClickyJLabel.factory(
				ArrayDataImageFactory.getVerticalGradientBundle(
						map.imageDataBundle, 100),
				false, legendWidth);

		map.verbose = true; legend.verbose = true;

		map.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
		legend.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
		legend.setMaximumSize(new Dimension(legendWidth, Integer.MAX_VALUE));

		ticksRows = Sequences.spacedIntervals(0, legend.icon.getImageDataNRows() - 1, nTicks);
		legend.drawTextValuesOnIcon(ticksRows, 0, Color.black, new Font("serif", 2, 60));

		legend.addMouseMotionListener(legend);
		map.addMouseMotionListener(map);

		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		c.gridy = 0; c.gridx = 0;
		frame.add(map, c);
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0;

		c.gridx = 1;




		frame.add(Box.createHorizontalGlue(), c);
		frame.add(Box.createHorizontalStrut(legendWidth), c);
		frame.add(legend, c);

		frame.setVisible(true);



	}

	public static void demo_update_bundle()
	{
		JFrame frame; StretchyClickyJLabel label1;	Random r = new Random();
		int nRow, nCol, nLabels;
		int[] textRows; int[] textCols;

		frame = getFrame("Clicky stretchy data label - updating image demo");

		nRow = r.nextInt(100) + 12;	nCol = r.nextInt(100) + 12; nLabels = r.nextInt(10) + 5;
		textRows = new int[nLabels]; textCols = new int[nLabels];
		for (int i = 0; i < nLabels; i++) { textRows[i] = r.nextInt(nRow); textCols[i] = r.nextInt(nCol); }

		label1 = StretchyClickyJLabel.factory(
				ArrayDataImageFactory.getRandomBundle(nRow, nCol),
				true, -9999);

		label1.drawTextValuesOnIcon(textRows, textCols, Color.black, new Font("serif", 2, 40));

		frame.add(label1); frame.setVisible(true);

		long interval = 1;
		long second = LocalTime.now().getSecond();
		while (true)
		{
			if (LocalTime.now().getSecond() - second > interval)
			{
				nRow = r.nextInt(100) + 12;	nCol = r.nextInt(100) + 12;
				for (int i = 0; i < nLabels; i++) 
				{ textRows[i] = r.nextInt(nRow); textCols[i] = r.nextInt(nCol); }

				if (label1.verbose) System.out.println("Random data has " + nRow + " rows, " + nCol +  "columns.");

				second = LocalTime.now().getSecond() %60;
				label1.updateBundle(ArrayDataImageFactory.getRandomBundle(
						nRow, nCol, r.nextInt(10) + 1));
				label1.drawTextValuesOnIcon(textRows, textCols, Color.black, new Font("serif", 2, 40));
				label1.drawPointsOnIcon(new int[] {nRow - 2, nRow - 2},  new int[] {1, nCol - 2}, Color.black, r.nextDouble() * 2.25);
			}
		}
	}

	public static void demo_draw_points_on_icon()
	{
		JFrame frame = getFrame("Clicky stretchy drawing points/text on icon demo");
		StretchyClickyJLabel label1, label2;

		Toolkit.getDefaultToolkit().setDynamicLayout( false );

		label1 = StretchyClickyJLabel.factory(
				ArrayDataImageFactory.getRandomBundle(12, 17),
				true, -9999);
		label1.setBorder(BorderFactory.createLineBorder(Color.RED, 5));

		label2 = StretchyClickyJLabel.factory(
				ArrayDataImageFactory.getRandomBundle(12, 17),
				true, -9999);

		label2.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

		frame.setLayout(new GridLayout());
		frame.add(label1); frame.add(label2); frame.setVisible(true);
		label1.buildPointColorInterpolator(new Color[] {Color.black, Color.green}, 0);

		int[] columns = new int[] {1, 4, 6};
		int[] rows = new int[] {3, 4, 6};

		String[] labels = new String[] {"one/three", "four/four", "6/6" };

		label2.drawTextOnIcon(rows, columns, labels, Color.BLACK, StretchyClickyJLabel.DEFAULT_FONT);

		int[] colorCodes = new int[] {0, 1, 0};
		label1.drawPointsOnIcon(rows, columns, colorCodes, 1.5);

		label1.addMouseMotionListener(label1);
		label2.addMouseMotionListener(label2);
	}

	public static JFrame getFrame(String title)
	{
		JFrame frame;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(1600, 1400));
		frame.setFont(new Font("serif", 2, 50));
		return frame;
	}

	@Override public void mouseDragged(MouseEvent arg0) { /* do nothing */ }

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		hover.set(arg0);
		if (verbose)
		{
			String dataArrayCoordMessage = 
					String.format("(x = %d, y = %d): ", hover.mouseCoord[0], hover.mouseCoord[1] );
			String valMessage = imageDataBundle.getName() + " = " + hover.value;
			System.out.println(dataArrayCoordMessage + valMessage);
		}
	}

	public void setHoverPane(JTextPane pane) { hover.pane = pane; }

	class MouseHoverData
	{
		JTextPane pane;
		protected int[] mouseCoord;
		protected String value;

		public void set(MouseEvent arg0)
		{
			mouseCoord = StretchyClickyJLabel.this.icon.getDataMouseCoords(arg0);
			value = StretchyClickyJLabel.this.imageDataBundle.getDataArrayVal(mouseCoord);
			

			if (pane != null)
			{
				String dataArrayCoordMessage = 
						String.format("(x = %d, y = %d): ", mouseCoord[0], mouseCoord[1] );
				String valMessage = imageDataBundle.getName() + " = " + hover.value;
				pane.setText(dataArrayCoordMessage + System.lineSeparator() + valMessage);
				//				System.out.println(dataArrayCoordMessage + System.lineSeparator() + valMessage);
			}
		}
	}

}
