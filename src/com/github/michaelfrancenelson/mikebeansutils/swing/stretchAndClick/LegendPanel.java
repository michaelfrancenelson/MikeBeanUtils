package com.github.michaelfrancenelson.mikebeansutils.swing.stretchAndClick;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.github.michaelfrancenelson.mikebeansutils.imaging.imagers.decorators.PanelLabel;
import com.github.michaelfrancenelson.mikebeansutils.utils.Sequences;

public class LegendPanel<T> extends PrimitiveImagePanel<T>
{
	/**
	 */
	private static final long serialVersionUID = -3449414365832753932L;

	protected int nSteps;
	protected int nLabels;
	double offset1, offset2;
	double textOffset, pointOffset;
	Font legFont;
	Color textColor;
	double ptSize;

	protected boolean loToHi, horiz;
	String intFmt, dblFmt, strFmt;

	private int nSigFigs = 4;

	protected double[] labelPositions;

	public void roundLegendLabels()
	{
		if (legend != null)
			for (PanelLabel p : legend.labels)
				p.roundNumericLabel(nSigFigs, dblFmt);
	}

	@Override public void paintComponent(Graphics g)
	{
		roundLegendLabels();
		super.paintComponent(g);
	}

	public void initLegend(
			int nLabels, int nSteps,
			double offset1, double offset2, 
			double textOffset, double pointOffset, 
			boolean loToHi,
			boolean horiz, 
			Font font, Color color, double ptSize,
			String intFmt, String dblFmt, String strFmt
			)
	{

		/* java's image origin is in the screen's upper left corner, so 
		 * low-to-high vertical gradients must have low values at high 
		 * indices.
		 */
		this.nLabels = nLabels;
		this.nSteps = nSteps;
		this.offset1 = offset1; this.offset2 = offset2;
		this.textOffset = textOffset;
		this.pointOffset = pointOffset;	
		this.loToHi = loToHi; 
		this.horiz = horiz;
		this.legFont = font;
		this.textColor = color;
		this.ptSize = ptSize;

		this.intFmt = intFmt; 
		this.dblFmt = dblFmt; 
		this.strFmt = strFmt;
	}

	/**
	 * 
	 */
	public void buildLegendLabels()
	{
		buildLegendLabels(
				nLabels, 
				offset1, offset2,
				textOffset, pointOffset,
				legFont, textColor,	ptSize,
				intFmt, dblFmt, strFmt);
	}

	/**
	 * 
	 * @param nLabels
	 * @param offset1
	 * @param offset2
	 * @param textPositionOffset
	 * @param pointPositionOffset
	 * @param font
	 * @param color
	 * @param ptSize
	 * @param intFmt
	 * @param dblFmt
	 * @param strFmt
	 */
	public void buildLegendLabels(
			int nLabels, 
			double offset1, double offset2, 
			double textPositionOffset, double pointPositionOffset, 
			Font font, Color color, double ptSize, 
			String intFmt, String dblFmt, String strFmt)
	{
		valueLabels = new ArrayList<>();
		points = new ArrayList<>();

		if (intFmt == null) intFmt = "%d";
		if (dblFmt == null) dblFmt = imager.getDblFmt();
		if (strFmt == null) strFmt = "%s";

		labelPositions = Sequences.spacedIntervals(offset1, 1.0 - offset2, nLabels - 1);

		if ( imager.getImagerData().getType().toLowerCase().equals("boolean"))
		{
			nLabels = Math.max(imager.getImagerData().getHeight(), imager.getImagerData().getWidth());
			double offset = 1.0 / ((double) nLabels * 2.0);
			labelPositions = Sequences.spacedIntervals(offset, 1.0 - offset, nLabels - 1);
		}

		int valIndex, positionIndex;
		if (horiz) { valIndex = 0; positionIndex = 1; }
		else { valIndex = 1; positionIndex = 0; }

		double[] coords = new double[2];
		for (int i = 0; i < nLabels; i++)
		{
			coords[valIndex] = labelPositions[i];
			coords[positionIndex] = textPositionOffset;

			addValueLabelRelative(coords[0], coords[1], font, color, intFmt, dblFmt, strFmt);
			logger.trace(String.format("Label at x = %.2f%%, y = %.2f%%", 100.0 * coords[0], 100.0 * coords[1]));

			if (pointPositionOffset > 0)
			{
				coords[positionIndex] = pointPositionOffset;
				addPointRelative(coords[0], coords[1], ptSize, color);
				logger.trace(String.format("Point at x = %.2f%%, y = %.2f%%", 100.0 * coords[0], 100.0 * coords[1]));
			}
		}
	}

	@Override
	public void addValueLabelRelative(
			double relI, double relJ, Font font, Color color, String intFmt, String dblFmt, String strFmt)
	{
		if (font == null) font = this.getFont();
		String label = imager.queryData(relI, relJ, intFmt, dblFmt, strFmt);
		labelFromImageRelCoords(relI, relJ, label, font, color, -9999, "value label");

		logger.trace(String.format("Adding value "
				+ "label %s at coords (%.0f%%, %.0f%%)",
				label, 100 * relI, 100 * relJ));
	}

	@Override public void setField(String name) { this.currentFieldName = name; }
	@Override public void setField(Field f) { setField(f.getName());}
}
