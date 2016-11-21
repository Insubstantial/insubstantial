/*
 * Copyright (c) 2005-2010 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.substance.internal.utils;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.pushingpixels.lafwidget.LafWidgetUtilities;
import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.SubstanceConstants.Side;
import org.pushingpixels.substance.api.painter.border.FlatBorderPainter;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;
import org.pushingpixels.substance.internal.colorscheme.ShiftColorScheme;
import org.pushingpixels.substance.internal.painter.SimplisticFillPainter;
import org.pushingpixels.substance.internal.utils.filters.*;

/**
 * Provides utility functions for creating various images for <b>Substance </b>
 * look and feel. This class is <b>for internal use only</b>.
 *
 * @author Kirill Grouchnikov
 */
public final class SubstanceImageCreator {
	/**
	 * Custom fill painter for filling the checkmarks of checkboxes and radio
	 * buttons.
	 *
	 * @author Kirill Grouchnikov
	 */
	public static class SimplisticSoftBorderReverseFillPainter extends
			SimplisticFillPainter {
		/**
		 * Singleton instance.
		 */
		public static final SubstanceFillPainter INSTANCE = new SimplisticSoftBorderReverseFillPainter();

		/**
		 * Private constructor.
		 */
		private SimplisticSoftBorderReverseFillPainter() {
		}

		@Override
		public String getDisplayName() {
			return "Simplistic Soft Border Reverse";
		}

		@Override
		public Color getTopFillColor(SubstanceColorScheme fillScheme) {
			return super.getBottomFillColor(fillScheme);
		}

		@Override
		public Color getBottomFillColor(SubstanceColorScheme fillScheme) {
			return super.getTopFillColor(fillScheme);
		}
	}

	/**
	 * Paints border instance of specified dimensions and status.
	 *
	 * @param c
	 *            Component.
	 * @param graphics
	 *            Graphics context.
	 * @param x
	 *            Component left X (in graphics context).
	 * @param y
	 *            Component top Y (in graphics context).
	 * @param width
	 *            Border width.
	 * @param height
	 *            Border height.
	 * @param radius
	 *            Border radius.
	 * @param borderScheme
	 *            border color scheme.
	 */
	public static void paintBorder(Component c, Graphics2D graphics, int x,
			int y, int width, int height, float radius,
			SubstanceColorScheme borderScheme) {

		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(c);
		graphics.translate(x, y);
		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(c);
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize) / 2.0);
		Shape contour = SubstanceOutlineUtilities.getBaseOutline(width, height,
				radius, null, borderDelta);
		int borderThickness = (int) SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize);
		boolean skipInnerBorder = (c instanceof JTextComponent)
				|| ((SwingUtilities.getAncestorOfClass(CellRendererPane.class,
						c) != null) && (SwingUtilities.getAncestorOfClass(
						JFileChooser.class, c) != null));
		GeneralPath contourInner = skipInnerBorder ? null
				: SubstanceOutlineUtilities.getBaseOutline(width, height,
						radius - borderThickness, null, borderThickness
								+ borderDelta);
		borderPainter.paintBorder(graphics, c, width, height, contour,
				contourInner, borderScheme);
		graphics.translate(-x, -y);
	}

	/**
	 * Paints border instance of specified dimensions and status.
	 *
	 * @param c
	 *            Component.
	 * @param graphics
	 *            Graphics context.
	 * @param x
	 *            Component left X (in graphics context).
	 * @param y
	 *            Component top Y (in graphics context).
	 * @param width
	 *            Border width.
	 * @param height
	 *            Border height.
	 * @param radius
	 *            Border radius.
	 * @param borderScheme
	 *            Border color scheme.
	 */
	public static void paintTextComponentBorder(JComponent c,
			Graphics graphics, int x, int y, int width, int height,
			float radius, SubstanceColorScheme borderScheme) {

		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(c);
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize) / 2.0);
		Shape contour = SubstanceOutlineUtilities
				.getBaseOutline(width, height, radius,
						SubstanceCoreUtilities.getSides(c,
								SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY),
						borderDelta);
		int borderThickness = (int) SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize);
		GeneralPath contourInner = SubstanceOutlineUtilities.getBaseOutline(
				width, height, radius - borderThickness, null, borderThickness
						+ borderDelta);

		Graphics2D g2d = (Graphics2D) graphics.create();
		g2d.translate(x, y);

		ComponentState stateForOuterBorder = c.isEnabled() ? ComponentState.ENABLED
				: ComponentState.DISABLED_UNSELECTED;
		Color lightColor = SubstanceColorUtilities.getDefaultBackgroundColor(c,
				stateForOuterBorder);

		if (stateForOuterBorder.isDisabled()) {
			float alpha = SubstanceColorSchemeUtilities.getAlpha(c,
					stateForOuterBorder);
			lightColor = SubstanceColorUtilities.getAlphaColor(lightColor,
					(int) (255 * alpha));
		}

		Color outerColor = SubstanceColorUtilities
				.getOuterTextComponentBorderColor(lightColor);
		float[] hsb = Color.RGBtoHSB(lightColor.getRed(),
				lightColor.getGreen(), lightColor.getBlue(), null);
		// hsb[1] = hsb[1] * 0.5f;
		// System.out.println(lightColor + " -> " + hsb[1] + ":" + hsb[2]);
		double bottomInnerBlend = 0.85;
		double topInnerBlend = 0.8;
		if (hsb[2] < 0.3f) {
			bottomInnerBlend = 0.6;
			topInnerBlend = 0.95;
		} else if (hsb[2] < 0.5f) {
			bottomInnerBlend = 0.8;
		} else if (hsb[2] < 0.75f) {
			bottomInnerBlend = 0.7;
		} else {
		}
		Color darkColor = borderScheme.getDarkColor();

		Color topInnerColor = SubstanceColorUtilities.getInterpolatedColor(
				darkColor, lightColor, topInnerBlend);
		Color bottomInnerColor = SubstanceColorUtilities.getInterpolatedColor(
				lightColor, darkColor, bottomInnerBlend);

		// darkColor = SubstanceColorUtilities.getAlphaColor(darkColor, 196);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);

		float strokeWidth = SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(c));
		g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND));

		g2d.setPaint(new GradientPaint(0, 0, outerColor, 0, height, outerColor));
		// g2d.setColor(Color.red);
		g2d.draw(contour);
		g2d.setPaint(new GradientPaint(0, 0, topInnerColor, 0, height,
				bottomInnerColor));
		// g2d.setColor(Color.green);
		g2d.draw(contourInner);

		// System.out.println("Outer : " + outerColor + "[" + lightColor
		// + "] from " + borderScheme.getDisplayName());

		g2d.dispose();
	}

	/**
	 * Retrieves check mark image.
	 *
	 * @param dimension
	 *            Check mark dimension.
	 * @param isEnabled
	 *            Enabled status.
	 * @param scheme
	 *            Color scheme for the check mark.
	 * @param checkMarkVisibility
	 *            Checkmark visibility in 0.0-1.0 range.
	 * @return Check mark image.
	 */
	private static BufferedImage getCheckMark(int dimension, boolean isEnabled,
			SubstanceColorScheme scheme, float checkMarkVisibility) {
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(dimension,
				dimension);

		// get graphics and set hints
		Graphics2D graphics = (Graphics2D) result.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// create curved checkbox path
		GeneralPath path = new GeneralPath();

		path.moveTo(0.25f * dimension, 0.5f * dimension);
		path.quadTo(0.37f * dimension, 0.6f * dimension, 0.47f * dimension,
				0.8f * dimension);
		path.quadTo(0.55f * dimension, 0.5f * dimension, 0.85f * dimension, 0f);

		// compute the x-based clip for the visibility
		float xClipStart = 0.15f * dimension;
		float xClipEnd = 0.95f * dimension;

		float xClipRealEnd = xClipStart + (xClipEnd - xClipStart)
				* checkMarkVisibility;

		graphics.setClip(0, 0, (int) Math.ceil(xClipRealEnd), dimension);

		graphics.setColor(SubstanceColorUtilities.getMarkColor(scheme,
				isEnabled));
		Stroke stroke = new BasicStroke((float) 0.15 * dimension,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		graphics.setStroke(stroke);
		graphics.draw(path);

		return result;
	}

	/**
	 * Returns arrow icon for the specified parameters.
	 *
	 * @param fontSize
	 *            Font size.
	 * @param direction
	 *            Arrow direction.
	 * @param colorScheme
	 *            Arrow icon color scheme.
	 * @return Arrow icon.
	 */
	public static Icon getArrowIcon(int fontSize, int direction,
			SubstanceColorScheme colorScheme) {
		float width = SubstanceSizeUtils.getArrowIconWidth(fontSize);
		float height = SubstanceSizeUtils.getArrowIconHeight(fontSize);
		if (direction == SwingConstants.CENTER)
			height *= 2;
		float strokeWidth = SubstanceSizeUtils.getArrowStrokeWidth(fontSize);
		return new ImageIcon(getArrow(width, height, strokeWidth, direction,
				colorScheme));
	}

	/**
	 * Retrieves arrow icon.
	 *
	 * @param width
	 *            Arrow width.
	 * @param height
	 *            Arrow height.
	 * @param strokeWidth
	 *            Stroke width.
	 * @param direction
	 *            Arrow direction.
	 * @param scheme
	 *            Color scheme for the arrow.
	 * @return Arrow image.
	 * @see SwingConstants#NORTH
	 * @see SwingConstants#WEST
	 * @see SwingConstants#SOUTH
	 * @see SwingConstants#EAST
	 */
	public static Icon getArrowIcon(float width, float height,
			float strokeWidth, int direction, SubstanceColorScheme scheme) {
		return new ImageIcon(getArrow(width, height, strokeWidth, direction,
				scheme));
	}

	/**
	 * Retrieves arrow image.
	 *
	 * @param width
	 *            Arrow width.
	 * @param height
	 *            Arrow height.
	 * @param strokeWidth
	 *            Stroke width.
	 * @param direction
	 *            Arrow direction.
	 * @param scheme
	 *            Color scheme for the arrow.
	 * @return Arrow image.
	 * @see SwingConstants#NORTH
	 * @see SwingConstants#WEST
	 * @see SwingConstants#SOUTH
	 * @see SwingConstants#EAST
	 * @see SwingConstants#CENTER
	 */
	public static BufferedImage getArrow(float width, float height,
			float strokeWidth, int direction, SubstanceColorScheme scheme) {
		BufferedImage arrowImage = SubstanceCoreUtilities.getBlankImage(
				(int) width, (int) height);

		// System.out.println(width + ":" + height + ":" + strokeWidth);

		// get graphics and set hints
		Graphics2D graphics = (Graphics2D) arrowImage.getGraphics();

		// graphics.setColor(Color.red);
		// graphics.fillRect(0, 0, width, height);

		graphics.translate(1, 1);
		width -= 2;
		height -= 2;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color arrowColor = SubstanceColorUtilities.getMarkColor(scheme, true);

		graphics.setColor(arrowColor);
		int cap = (width < 15) ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
		Stroke stroke = new BasicStroke(strokeWidth, cap,
				BasicStroke.JOIN_MITER);

		graphics.setStroke(stroke);

		int cushion = (int) strokeWidth / 2;
		if (direction == SwingConstants.CENTER) {
			BufferedImage top = getArrow(width, height / 2, strokeWidth,
					SwingConstants.NORTH, scheme);
			BufferedImage bottom = getArrow(width, height / 2, strokeWidth,
					SwingConstants.SOUTH, scheme);
			graphics.drawImage(top, 0, 1, null);
			graphics.drawImage(bottom, 0, (int) height / 2 - 1, null);
			return arrowImage;
		} else {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(cushion, strokeWidth);
			gp.lineTo((float) 0.5 * (width - 1), height - 1 - cushion);
			gp.lineTo(width - 1 - cushion, strokeWidth);
			graphics.draw(gp);

			// graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_OFF);
			// graphics.setStroke(new BasicStroke(1.0f, cap,
			// BasicStroke.JOIN_MITER));
			// graphics.setColor(Color.red);
			// graphics.drawRect(0, 0, (int)width - 1, (int)height - 1);

			int quadrantCounterClockwise = 0;
			switch (direction) {
			case SwingConstants.NORTH:
				quadrantCounterClockwise = 2;
				break;
			case SwingConstants.WEST:
				quadrantCounterClockwise = 1;
				break;
			case SwingConstants.SOUTH:
				quadrantCounterClockwise = 0;
				break;
			case SwingConstants.EAST:
				quadrantCounterClockwise = 3;
				break;
			}
			BufferedImage rotatedImage = SubstanceImageCreator.getRotated(
					arrowImage, quadrantCounterClockwise);

			return rotatedImage;
		}
	}

	/**
	 * Returns double arrow icon for the specified parameters.
	 *
	 * @param fontSize
	 *            Font size.
	 * @param deltaWidth
	 *            Arrow width delta.
	 * @param deltaHeight
	 *            Arrow height delta.
	 * @param deltaStrokeWidth
	 *            Arrow stroke width delta.
	 * @param direction
	 *            Arrow direction.
	 * @param colorScheme
	 *            Color scheme for the arrow.
	 * @return Double arrow icon.
	 */
	public static Icon getDoubleArrowIconDelta(int fontSize, float deltaWidth,
			float deltaHeight, float deltaStrokeWidth, int direction,
			SubstanceColorScheme colorScheme) {
		float arrowWidth = SubstanceSizeUtils.getArrowIconWidth(fontSize)
				+ deltaWidth;
		float arrowHeight = SubstanceSizeUtils.getArrowIconHeight(fontSize)
				+ deltaHeight;
		float arrowStrokeWidth = SubstanceSizeUtils
				.getDoubleArrowStrokeWidth(fontSize) + deltaStrokeWidth;
		return getDoubleArrowIcon(fontSize, arrowWidth, arrowHeight,
				arrowStrokeWidth, direction, colorScheme);
	}

	/**
	 * Retrieves arrow icon.
	 *
	 * @param width
	 *            Arrow width.
	 * @param height
	 *            Arrow height.
	 * @param strokeWidth
	 *            Stroke width.
	 * @param direction
	 *            Arrow direction.
	 * @param colorScheme
	 *            Color scheme for the arrow.
	 * @return Arrow image.
	 * @see SwingConstants#NORTH
	 * @see SwingConstants#WEST
	 * @see SwingConstants#SOUTH
	 * @see SwingConstants#EAST
	 */
	public static Icon getDoubleArrowIcon(int fontSize, float width,
			float height, float strokeWidth, int direction,
			SubstanceColorScheme colorScheme) {
		int delta = 3 + 2 * SubstanceSizeUtils.getExtraPadding(fontSize) / 3;
		boolean toggle = (direction == SwingConstants.WEST)
				|| (direction == SwingConstants.EAST);
		if (toggle) {
			float temp = width;
			width = height;
			height = temp;
		}
		BufferedImage downArrowImage = SubstanceCoreUtilities.getBlankImage(
				(int) width, (int) height);

		BufferedImage singleArrow = getArrow(width,
				Math.max(1, height - delta), strokeWidth, SwingConstants.SOUTH,
				colorScheme);

		// get graphics and set hints
		Graphics2D graphics = (Graphics2D) downArrowImage.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int arrowHeight = singleArrow.getHeight();
		int arrowWidth = singleArrow.getWidth();
		int offset = toggle ? (int) (height - arrowHeight - delta) / 2
				: (int) (width - arrowWidth - delta) / 2;
		graphics.drawImage(singleArrow, 0, offset, null);
		graphics.drawImage(singleArrow, 0, offset + delta, null);

		int quadrantCounterClockwise = 0;
		switch (direction) {
		case SwingConstants.NORTH:
			quadrantCounterClockwise = 2;
			break;
		case SwingConstants.WEST:
			quadrantCounterClockwise = 1;
			break;
		case SwingConstants.SOUTH:
			quadrantCounterClockwise = 0;
			break;
		case SwingConstants.EAST:
			quadrantCounterClockwise = 3;
			break;
		}
		BufferedImage arrowImage = SubstanceImageCreator.getRotated(
				downArrowImage, quadrantCounterClockwise);

		return new ImageIcon(arrowImage);
	}

	/**
	 * Returns rotated image.
	 *
	 * @param bi
	 *            Image to rotate.
	 * @param quadrantClockwise
	 *            Amount of quadrants to rotate in clockwise directio. The
	 *            rotation angle is 90 times this value.
	 * @return Rotated image.
	 */
	public static BufferedImage getRotated(BufferedImage bi,
			int quadrantClockwise) {
		quadrantClockwise = quadrantClockwise % 4;
		int width = bi.getWidth();
		int height = bi.getHeight();
		if ((quadrantClockwise == 1) || (quadrantClockwise == 3)) {
			width = bi.getHeight();
			height = bi.getWidth();
		}
		BufferedImage biRot = SubstanceCoreUtilities.getBlankImage(width,
				height);
		AffineTransform at = null;
		switch (quadrantClockwise) {
		case 1:
			at = AffineTransform.getTranslateInstance(width, 0);
			at.rotate(Math.PI / 2);
			break;
		case 2:
			at = AffineTransform.getTranslateInstance(width, height);
			at.rotate(Math.PI);
			break;
		case 3:
			at = AffineTransform.getTranslateInstance(0, height);
			at.rotate(-Math.PI / 2);
		}
		Graphics2D rotg = biRot.createGraphics();
		if (at != null)
			rotg.setTransform(at);
		rotg.drawImage(bi, 0, 0, null);
		rotg.dispose();
		return biRot;
	}

	/**
	 * Returns rotated image.
	 *
	 * @param bi
	 *            Image to rotate.
	 * @param quadrantClockwise
	 *            Amount of quadrants to rotate in clockwise directio. The
	 *            rotation angle is 90 times this value.
	 * @return Rotated image.
	 */
	public static VolatileImage getRotated(final VolatileImage bi,
			int quadrantClockwise) {
		quadrantClockwise = quadrantClockwise % 4;
		int width = bi.getWidth();
		int height = bi.getHeight();
		if ((quadrantClockwise == 1) || (quadrantClockwise == 3)) {
			width = bi.getHeight();
			height = bi.getWidth();
		}
		VolatileImage biRot = SubstanceCoreUtilities.getBlankVolatileImage(
				width, height);
		AffineTransform at = null;
		switch (quadrantClockwise) {
		case 1:
			at = AffineTransform.getTranslateInstance(width, 0);
			at.rotate(Math.PI / 2);
			break;
		case 2:
			at = AffineTransform.getTranslateInstance(width, height);
			at.rotate(Math.PI);
			break;
		case 3:
			at = AffineTransform.getTranslateInstance(0, height);
			at.rotate(-Math.PI / 2);
		}
		Graphics2D rotg = biRot.createGraphics();
		if (at != null)
			rotg.setTransform(at);
		rotg.drawImage(bi, 0, 0, null);
		rotg.dispose();
		return biRot;
	}

	/**
	 * Translated the specified icon to grey scale.
	 *
	 * @param icon
	 *            Icon.
	 * @return Greyscale version of the specified icon.
	 */
	public static Icon toGreyscale(Icon icon) {
		if (icon == null) {
			return null;
		}

		int width = icon.getIconWidth();
		int height = icon.getIconHeight();

		BufferedImage result = SubstanceCoreUtilities.getBlankImage(width,
				height);

		icon.paintIcon(null, result.getGraphics(), 0, 0);
		return new ImageIcon(new GrayscaleFilter().filter(result, null));
	}

	/**
	 * Makes the specified icon transparent.
	 *
	 * @param c
	 *            Component.
	 * @param icon
	 *            Icon.
	 * @param alpha
	 *            The alpha of the resulting image. The closer this value is to
	 *            0.0, the more transparent resulting image will be.
	 * @return Transparent version of the specified icon.
	 */
	public static Icon makeTransparent(Component c, Icon icon, double alpha) {
		if (icon == null) {
			return null;
		}

		int width = icon.getIconWidth();
		int height = icon.getIconHeight();

		BufferedImage result = SubstanceCoreUtilities.getBlankImage(width,
				height);
		icon.paintIcon(c, result.getGraphics(), 0, 0);
		return new ImageIcon(new TranslucentFilter(alpha).filter(result, null));
	}

	// /**
	// * Retrieves radio button of the specified size that matches the specified
	// * parameters.
	// *
	// * @param component
	// * Component.
	// * @param dimension
	// * Radio button dimension.
	// * @param componentState
	// * Component state.
	// * @param prevState
	// * Previous component state.
	// * @param offsetX
	// * Offset on X axis - should be positive in order to see the
	// * entire radio button.
	// * @param scheme1
	// * First color scheme.
	// * @param scheme2
	// * Second color scheme.
	// * @param interpolationCyclePos
	// * Interpolation cycle.
	// * @param checkMarkVisibility
	// * Checkmark visibility in 0.0-1.0 range.
	// * @return Radio button of the specified size that matches the specified
	// * parameters.
	// */
	// public static BufferedImage getRadioButton(JComponent component,
	// SubstanceFillPainter fillPainter,
	// SubstanceBorderPainter borderPainter, int dimension,
	// ComponentState componentState, ComponentState prevState,
	// int offsetX, SubstanceColorScheme currFillColorScheme,
	// SubstanceColorScheme prevFillColorScheme,
	// SubstanceColorScheme currMarkColorScheme,
	// SubstanceColorScheme prevMarkColorScheme,
	// SubstanceColorScheme currBorderColorScheme,
	// SubstanceColorScheme prevBorderColorScheme,
	// float interpolationCyclePos, float checkMarkVisibility) {
	//
	// // ComponentState.ColorSchemeKind kind = componentState
	// // .getColorSchemeKind();
	//
	// float cyclePos = (currFillColorScheme != prevFillColorScheme) ?
	// interpolationCyclePos
	// : componentState.getCyclePosition();
	// float borderCyclePos = (currBorderColorScheme != prevBorderColorScheme) ?
	// interpolationCyclePos
	// : componentState.getCyclePosition();
	// float markCyclePos = (currMarkColorScheme != prevMarkColorScheme) ?
	// interpolationCyclePos
	// : componentState.getCyclePosition();
	//
	// if (componentState.getColorSchemeKind() != ColorSchemeKind.CURRENT) {
	// fillPainter = SimplisticSoftBorderReverseFillPainter.INSTANCE;
	// }
	//
	// float borderThickness = SubstanceSizeUtils
	// .getBorderStrokeWidth(dimension);
	// int delta = (int) (borderThickness - 0.6);
	// // System.out.println(dimension + ":" + borderThickness + ":" + delta);
	//
	// // float fDelta = borderThickness / 2.0f;
	// Shape contourBorder = new Ellipse2D.Float(delta, delta, dimension - 2
	// * delta - 1, dimension - 2 * delta - 1);
	//
	// BufferedImage offBackground = SubstanceCoreUtilities.getBlankImage(
	// dimension + offsetX, dimension);
	// Graphics2D graphics = (Graphics2D) offBackground.getGraphics();
	// float alpha = SubstanceColorSchemeUtilities.getAlpha(component,
	// componentState);
	// graphics.setComposite(AlphaComposite.getInstance(
	// AlphaComposite.SRC_OVER, alpha));
	//
	// graphics.translate(offsetX, 0);
	// fillPainter.paintContourBackground(graphics, component, dimension,
	// dimension, contourBorder, false, currFillColorScheme,
	// prevFillColorScheme, cyclePos, true,
	// currFillColorScheme != prevFillColorScheme);
	//
	// Shape contourInner = new Ellipse2D.Float(delta + borderThickness, delta
	// + borderThickness, dimension - 2 * delta - 2 * borderThickness,
	// dimension - 2 * delta - 2 * borderThickness);
	//
	// borderPainter.paintBorder(graphics, component, dimension, dimension,
	// contourBorder, contourInner, currBorderColorScheme,
	// prevBorderColorScheme, borderCyclePos,
	// currBorderColorScheme != prevBorderColorScheme);
	// graphics.setComposite(AlphaComposite.SrcOver);
	// // graphics.translate(-offsetX, 0);
	//
	// float rc = dimension / 2.0f;
	// float radius = dimension / 4.5f;
	// // graphics.translate(offsetX, 0);
	//
	// graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	//
	// if (componentState.isFacetActive(AnimationFacet.SELECTION)
	// || (checkMarkVisibility > 0.0)) {
	// // mark
	// Shape markOval = new Ellipse2D.Double(rc - radius, rc - radius,
	// 2 * radius, 2 * radius);
	//
	// graphics.setComposite(AlphaComposite.getInstance(
	// AlphaComposite.SRC_OVER, alpha * checkMarkVisibility));
	// drawRadioMark(graphics, SubstanceColorUtilities
	// .getMarkColor(currMarkColorScheme, componentState
	// .getColorSchemeKind() != ColorSchemeKind.DISABLED),
	// markOval);
	// graphics.setComposite(AlphaComposite.getInstance(
	// AlphaComposite.SRC_OVER, alpha * checkMarkVisibility
	// * markCyclePos));
	// drawRadioMark(graphics, SubstanceColorUtilities
	// .getMarkColor(prevMarkColorScheme, componentState
	// .getColorSchemeKind() != ColorSchemeKind.DISABLED),
	// markOval);
	// } else {
	// // draw ghost mark holder
	// graphics.setPaint(new GradientPaint(rc + radius, rc - radius,
	// currFillColorScheme.getDarkColor(), rc - radius, rc
	// + radius, currFillColorScheme.getLightColor()));
	// Shape markOval = new Ellipse2D.Double(rc - radius, rc - radius,
	// 2 * radius, 2 * radius);
	// graphics.setComposite(AlphaComposite.getInstance(
	// AlphaComposite.SRC_OVER, alpha * 0.3f));
	// graphics.fill(markOval);
	// }
	// graphics.translate(-offsetX, 0);
	//
	// return offBackground;
	// }

	/**
	 * Retrieves radio button of the specified size that matches the specified
	 * parameters.
	 *
	 * @param component
	 *            Component.
	 * @param dimension
	 *            Radio button dimension.
	 * @param componentState
	 *            Component state.
	 * @param offsetX
	 *            Offset on X axis - should be positive in order to see the
	 *            entire radio button.
	 * @param fillColorScheme
	 *            Color scheme for the inner fill.
	 * @param markColorScheme
	 *            Color scheme for the check mark.
	 * @param borderColorScheme
	 *            Color scheme for the border.
	 * @param checkMarkVisibility
	 *            Check mark visibility in 0.0-1.0 range.
	 * @return Radio button of the specified size that matches the specified
	 *         parameters.
	 */
	public static BufferedImage getRadioButton(JComponent component,
			SubstanceFillPainter fillPainter,
			SubstanceBorderPainter borderPainter, int dimension,
			ComponentState componentState, int offsetX,
			SubstanceColorScheme fillColorScheme,
			SubstanceColorScheme markColorScheme,
			SubstanceColorScheme borderColorScheme, float checkMarkVisibility) {

		// ComponentState.ColorSchemeKind kind = componentState
		// .getColorSchemeKind();

		if (!componentState.isActive()) {
			fillPainter = SimplisticSoftBorderReverseFillPainter.INSTANCE;
		}

		float borderThickness = SubstanceSizeUtils
				.getBorderStrokeWidth(dimension);
		int delta = (int) (borderThickness - 0.6);
		// System.out.println(dimension + ":" + borderThickness + ":" + delta);

		// float fDelta = borderThickness / 2.0f;
		Shape contourBorder = new Ellipse2D.Float(delta, delta, dimension - 2
				* delta - 1, dimension - 2 * delta - 1);

		BufferedImage offBackground = SubstanceCoreUtilities.getBlankImage(
				dimension + offsetX, dimension);
		Graphics2D graphics = (Graphics2D) offBackground.getGraphics();
		float alpha = SubstanceColorSchemeUtilities.getAlpha(component,
				componentState);
		graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha));

		graphics.translate(offsetX, 0);
		fillPainter.paintContourBackground(graphics, component, dimension,
				dimension, contourBorder, false, fillColorScheme, true);

		Shape contourInner = new Ellipse2D.Float(delta + borderThickness, delta
				+ borderThickness, dimension - 2 * delta - 2 * borderThickness,
				dimension - 2 * delta - 2 * borderThickness);

		borderPainter.paintBorder(graphics, component, dimension, dimension,
				contourBorder, contourInner, borderColorScheme);
		graphics.setComposite(AlphaComposite.SrcOver);
		// graphics.translate(-offsetX, 0);

		float rc = dimension / 2.0f;
		float radius = dimension / 4.5f;
		// graphics.translate(offsetX, 0);

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (checkMarkVisibility > 0.0) {
			// mark
			Shape markOval = new Ellipse2D.Double(rc - radius, rc - radius,
					2 * radius, 2 * radius);

      // Avoid exception by making sure the alpha value is in the correct range.
      // https://github.com/Insubstantial/insubstantial/issues/138
      float alphaTimesVisibility = Math.max(0.0f, Math.min(1.0f, alpha * checkMarkVisibility));

			graphics.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, alphaTimesVisibility));
			drawRadioMark(graphics, SubstanceColorUtilities.getMarkColor(
					markColorScheme, !componentState.isDisabled()), markOval);
		} else {
			// draw ghost mark holder
			graphics.setPaint(new GradientPaint(rc + radius, rc - radius,
					fillColorScheme.getDarkColor(), rc - radius, rc + radius,
					fillColorScheme.getLightColor()));
			Shape markOval = new Ellipse2D.Double(rc - radius, rc - radius,
					2 * radius, 2 * radius);
			graphics.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, alpha * 0.3f));
			graphics.fill(markOval);
		}
		graphics.translate(-offsetX, 0);

		return offBackground;
	}

	/**
	 * Draws radio mark.
	 *
	 * @param graphics
	 *            Graphics context.
	 * @param color
	 *            Radio mark color.
	 * @param markOval
	 *            Radio mark shape.
	 */
	private static void drawRadioMark(Graphics2D graphics, Color color,
			Shape markOval) {
		graphics.setColor(color);
		graphics.fill(markOval);
	}

	/**
	 * Retrieves check box of the specified size that matches the specified
	 * component state.
	 *
	 * @param button
	 *            Button for the check mark.
	 * @param dimension
	 *            Check box size.
	 * @param componentState
	 *            Component state.
	 * @param fillColorScheme
	 *            Color scheme for the inner fill.
	 * @param markColorScheme
	 *            Color scheme for the check mark.
	 * @param borderColorScheme
	 *            Color scheme for the border.
	 * @param checkMarkVisibility
	 *            Check mark visibility in 0.0-1.0 range.
	 * @param isCheckMarkFadingOut
	 *            if <code>true</code>, the value of
	 *            <code>interpolationCyclePos10</code> is used as the alpha
	 *            channel.
	 * @return Check box of the specified size that matches the specified
	 *         component state.
	 */
	public static BufferedImage getCheckBox(AbstractButton button,
			SubstanceFillPainter fillPainter,
			SubstanceBorderPainter borderPainter, int dimension,
			ComponentState componentState,
			SubstanceColorScheme fillColorScheme,
			SubstanceColorScheme markColorScheme,
			SubstanceColorScheme borderColorScheme, float checkMarkVisibility,
			boolean isCheckMarkFadingOut) {

		// int checkMarkSize = SubstanceSizeUtils
		// .getCheckBoxMarkSize(SubstanceSizeUtils
		// .getComponentFontSize(button));
		int offset = SubstanceSizeUtils
				.getAdjustedSize(
						SubstanceSizeUtils.getComponentFontSize(button), 3, 9,
						1, false);
		int delta = offset;
		float cornerRadius = SubstanceSizeUtils
				.getClassicButtonCornerRadius(SubstanceSizeUtils
						.getComponentFontSize(button));
		if (dimension <= 10) {
			offset = 2;
			cornerRadius = 2;
		}

		int contourDim = dimension - delta;
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(button)) / 2.0);
		GeneralPath contour = SubstanceOutlineUtilities.getBaseOutline(
				contourDim, contourDim, cornerRadius, null, borderDelta);

		if (!componentState.isActive()) {
			fillPainter = SimplisticSoftBorderReverseFillPainter.INSTANCE;
		}

		BufferedImage offBackground = SubstanceCoreUtilities.getBlankImage(
				dimension, dimension);
		Graphics2D graphics = (Graphics2D) offBackground.getGraphics();
		float alpha = SubstanceColorSchemeUtilities.getAlpha(button,
				componentState);
		graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha));

		graphics.translate(delta - 1, delta - 1);
		fillPainter.paintContourBackground(graphics, button, contourDim,
				contourDim, contour, false, fillColorScheme, true);

		// graphics.drawImage(background, 0, 0, null);
		int borderThickness = (int) SubstanceSizeUtils
				.getBorderStrokeWidth(dimension);
		GeneralPath contourInner = SubstanceOutlineUtilities.getBaseOutline(
				contourDim, contourDim, cornerRadius - borderThickness, null,
				borderThickness + borderDelta);
		borderPainter.paintBorder(graphics, button, contourDim, contourDim,
				contour, contourInner, borderColorScheme);
		graphics.translate(-delta, 1 - delta);
		if (checkMarkVisibility > 0.0) {
			if (isCheckMarkFadingOut) {
				graphics.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha * checkMarkVisibility));
				checkMarkVisibility = 1.0f;
			}

			BufferedImage checkMark = SubstanceImageCreator.getCheckMark(
					dimension - 2 * offset / 3, !componentState.isDisabled(),
					markColorScheme, checkMarkVisibility);
			graphics.drawImage(checkMark, 1 + 2 * offset / 3,
					(dimension < 14) ? 0 : -1, null);
		}

		return offBackground;
	}

	/**
	 * Retrieves composite background for the specified parameters. The
	 * composite background consists of three layers:
	 * <ol>
	 * <li>Layer that matches the <code>increased</code> state.
	 * <li>Layer that matches the <code>decreased</code> state.
	 * <li>Regular layer with rounded background.
	 * </ol>
	 * The layers are drawn in the following order:
	 * <ol>
	 * <li>The left half of the first layer
	 * <li>The right half of the second layer
	 * <li>The third layer
	 * </ol>
	 * Combined together, the layers create the image for scrollbar track with
	 * continuation of the arrow increase and decrease buttons.
	 *
	 * @param component
	 *            Component.
	 * @param width
	 *            Image width.
	 * @param height
	 *            Image height.
	 * @param cornerRadius
	 *            Corner radius.
	 * @param decrButton
	 *            The <code>decrease</code> button.
	 * @param incrButton
	 *            The <code>increase</code> button.
	 * @param flipSides
	 *            If <code>true</code>, the drawn halves of the first and the
	 *            second layers above will be swapped.
	 */
	public static void paintCompositeRoundedBackground(JComponent component,
			Graphics g, int width, int height, int cornerRadius,
			AbstractButton decrButton, AbstractButton incrButton,
			boolean flipSides) {

		int delta = 3;

		if (decrButton != null) {
			Graphics2D graphics = (Graphics2D) g.create();
			if (!flipSides) {
				graphics.clip(new Rectangle(-delta, 0, width / 2, height));
				graphics.translate(-delta, 0);
			} else {
				graphics.clip(new Rectangle(width / 2, 0, width / 2 + 1, height));
			}
			PairwiseButtonBackgroundDelegate.updatePairwiseBackground(graphics,
					decrButton, width + 2 * delta, height,
					flipSides ? Side.RIGHT : Side.LEFT, true);
			graphics.dispose();
		}

		if (incrButton != null) {
			Graphics2D graphics = (Graphics2D) g.create();
			if (!flipSides) {
				graphics.clip(new Rectangle(width / 2, 0, width / 2 + 1, height));
			} else {
				graphics.clip(new Rectangle(-delta, 0, width / 2, height));
				graphics.translate(-delta, 0);
			}
			PairwiseButtonBackgroundDelegate.updatePairwiseBackground(graphics,
					incrButton, width + 2 * delta, height,
					flipSides ? Side.LEFT : Side.RIGHT, true);
			graphics.dispose();
		}
	}

	/**
	 * Overlays light-colored echo below the specified image.
	 *
	 * @param image
	 *            The input image.
	 * @param echoAlpha
	 *            Alpha channel for the echo image.
	 * @param offsetX
	 *            X offset of the echo.
	 * @param offsetY
	 *            Y offset of the echo.
	 * @return Image with overlayed echo.
	 */
	private static BufferedImage overlayEcho(BufferedImage image,
			float echoAlpha, int offsetX, int offsetY) {
		int width = image.getWidth();
		int height = image.getHeight();

		// blur the original image
		// ConvolveOp convolve = new ConvolveOp(new Kernel(3, 3, new float[] {
		// .4f, .4f, .4f, .4f, .0f, .4f, .4f, .4f, .4f }),
		// ConvolveOp.EDGE_NO_OP, null);
		offsetX = offsetY = 0;
		BufferedImage negated = getNegated(image);
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(width,
				height);
		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.2f * echoAlpha * echoAlpha
						* echoAlpha));
		graphics.drawImage(negated, offsetX - 1, offsetY - 1, null);
		graphics.drawImage(negated, offsetX + 1, offsetY - 1, null);
		graphics.drawImage(negated, offsetX - 1, offsetY + 1, null);
		graphics.drawImage(negated, offsetX + 1, offsetY + 1, null);
		graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.7f * echoAlpha * echoAlpha
						* echoAlpha));
		graphics.drawImage(negated, offsetX, offsetY - 1, null);
		graphics.drawImage(negated, offsetX, offsetY + 1, null);
		graphics.drawImage(negated, offsetX - 1, offsetY, null);
		graphics.drawImage(negated, offsetX + 1, offsetY, null);

		graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f));
		graphics.drawImage(image, 0, 0, null);

		graphics.dispose();
		return result;
	}

	/**
	 * Returns <code>minimize</code> icon.
	 *
	 * @param scheme
	 *            Color scheme for the icon.
	 * @return <code>Minimize</code> icon.
	 */
	public static Icon getMinimizeIcon(SubstanceColorScheme scheme,
			SubstanceColorScheme backgroundScheme) {
		int iSize = SubstanceSizeUtils.getTitlePaneIconSize();
		return getMinimizeIcon(iSize, scheme, backgroundScheme);
	}

	/**
	 * Returns <code>minimize</code> icon.
	 *
	 * @param iSize
	 *            Icon dimension.
	 * @param scheme
	 *            Color scheme for the icon.
	 * @return <code>Minimize</code> icon.
	 */
	public static Icon getMinimizeIcon(int iSize, SubstanceColorScheme scheme,
			SubstanceColorScheme backgroundScheme) {
		BufferedImage image = SubstanceCoreUtilities
				.getBlankImage(iSize, iSize);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		int start = (iSize / 4) - 2;
		int end = (3 * iSize / 4);// - 1;
		int size = end - start - 3;
		Color color = SubstanceColorUtilities.getMarkColor(scheme, true);
		graphics.setColor(color);
		graphics.fillRect(start + 2, end - 2, size, 3);

		int fgStrength = SubstanceColorUtilities.getColorBrightness(color
				.getRGB());
		int fgNegativeStrength = SubstanceColorUtilities
				.getColorBrightness(SubstanceColorUtilities
						.getNegativeColor(color.getRGB()));
		int bgStrength = SubstanceColorUtilities
				.getColorBrightness(backgroundScheme.getLightColor().getRGB());
		boolean noEcho = (fgStrength > fgNegativeStrength)
				&& (fgStrength < bgStrength);

		return new ImageIcon(SubstanceImageCreator.overlayEcho(image,
				noEcho ? 0 : SubstanceColorUtilities.getColorStrength(color),
				1, 1));
	}

	/**
	 * Returns <code>restore</code> icon.
	 *
	 * @param scheme
	 *            Color scheme for the icon.
	 * @return <code>Restore</code> icon.
	 */
	public static Icon getRestoreIcon(SubstanceColorScheme scheme,
			SubstanceColorScheme backgroundScheme) {
		int iSize = SubstanceSizeUtils.getTitlePaneIconSize();
		BufferedImage image = SubstanceCoreUtilities
				.getBlankImage(iSize, iSize);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		int start = (iSize / 4) - 2;
		int end = (3 * iSize / 4) - 1;
		int size = end - start - 3;
		Color color = SubstanceColorUtilities.getMarkColor(scheme, true);
		graphics.setColor(color);
		graphics.drawRect(start, end - size + 1, size, size);
		graphics.drawLine(start, end - size + 2, start + size, end - size + 2);
		graphics.fillRect(end - size, start + 1, size + 1, 2);
		graphics.drawLine(end, start + 1, end, start + size + 1);
		graphics.drawLine(start + size + 2, start + size + 1, end, start + size
				+ 1);

		int fgStrength = SubstanceColorUtilities.getColorBrightness(color
				.getRGB());
		int fgNegativeStrength = SubstanceColorUtilities
				.getColorBrightness(SubstanceColorUtilities
						.getNegativeColor(color.getRGB()));
		int bgStrength = SubstanceColorUtilities
				.getColorBrightness(backgroundScheme.getLightColor().getRGB());
		boolean noEcho = (fgStrength > fgNegativeStrength)
				&& (fgStrength < bgStrength);

		return new ImageIcon(SubstanceImageCreator.overlayEcho(image,
				noEcho ? 0 : SubstanceColorUtilities.getColorStrength(color),
				1, 1));
	}

	/**
	 * Returns <code>maximize</code> icon.
	 *
	 * @param scheme
	 *            Color scheme for the icon.
	 * @return <code>Maximize</code> icon.
	 */
	public static Icon getMaximizeIcon(SubstanceColorScheme scheme,
			SubstanceColorScheme backgroundScheme) {
		int iSize = SubstanceSizeUtils.getTitlePaneIconSize();
		return getMaximizeIcon(iSize, scheme, backgroundScheme);
	}

	/**
	 * Returns <code>maximize</code> icon.
	 *
	 * @param iSize
	 *            Icon dimension.
	 * @param scheme
	 *            Color scheme for the icon.
	 * @return <code>Maximize</code> icon.
	 */
	public static Icon getMaximizeIcon(int iSize, SubstanceColorScheme scheme,
			SubstanceColorScheme backgroundScheme) {
		BufferedImage image = SubstanceCoreUtilities
				.getBlankImage(iSize, iSize);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		int start = (iSize / 4) - 1;
		int end = iSize - start - 1;// (3 * iSize / 4);
		Color color = SubstanceColorUtilities.getMarkColor(scheme, true);
		graphics.setColor(color);
		graphics.drawRect(start, start, end - start, end - start);
		graphics.drawLine(start, start + 1, end, start + 1);

		int fgStrength = SubstanceColorUtilities.getColorBrightness(color
				.getRGB());
		int fgNegativeStrength = SubstanceColorUtilities
				.getColorBrightness(SubstanceColorUtilities
						.getNegativeColor(color.getRGB()));
		int bgStrength = SubstanceColorUtilities
				.getColorBrightness(backgroundScheme.getLightColor().getRGB());
		boolean noEcho = (fgStrength > fgNegativeStrength)
				&& (fgStrength < bgStrength);

		return new ImageIcon(SubstanceImageCreator.overlayEcho(image,
				noEcho ? 0 : SubstanceColorUtilities.getColorStrength(color),
				1, 1));
	}

	/**
	 * Returns <code>close</code> icon.
	 *
	 * @param scheme
	 *            Color scheme for the icon.
	 * @return <code>Close</code> icon.
	 */
	public static Icon getCloseIcon(SubstanceColorScheme scheme,
			SubstanceColorScheme backgroundScheme) {
		return SubstanceImageCreator.getCloseIcon(
				SubstanceSizeUtils.getTitlePaneIconSize(), scheme,
				backgroundScheme);
	}

	/**
	 * Returns <code>close</code> icon.
	 *
	 * @param iSize
	 *            Icon dimension.
	 * @param colorScheme
	 *            Color scheme for the icon.
	 * @return <code>Close</code> icon.
	 */
	public static Icon getCloseIcon(int iSize,
			SubstanceColorScheme colorScheme,
			SubstanceColorScheme backgroundScheme) {
		BufferedImage image = SubstanceCoreUtilities
				.getBlankImage(iSize, iSize);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		if (iSize < 15) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		int start = (iSize / 4);// - 1;
		int end = (3 * iSize / 4);

		// System.out.println(iSize + ":" + start + ":" + end);

		Stroke stroke = new BasicStroke(
				SubstanceSizeUtils.getFocusStrokeWidth(iSize),
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		graphics.setStroke(stroke);
		Color color = SubstanceColorUtilities.getMarkColor(colorScheme, true);
		graphics.setColor(color);
		graphics.drawLine(start, start, end, end);
		graphics.drawLine(start, end, end, start);

		int fgStrength = SubstanceColorUtilities.getColorBrightness(color
				.getRGB());
		int fgNegativeStrength = SubstanceColorUtilities
				.getColorBrightness(SubstanceColorUtilities
						.getNegativeColor(color.getRGB()));
		int bgStrength = SubstanceColorUtilities
				.getColorBrightness(backgroundScheme.getLightColor().getRGB());
		boolean noEcho = (fgStrength > fgNegativeStrength)
				&& (fgStrength < bgStrength);
		// System.out.println(SubstanceColorUtilities.getColorBrightness(color
		// .getRGB())
		// + ":"
		// + SubstanceColorUtilities
		// .getColorBrightness(SubstanceColorUtilities
		// .getNegativeColor(color.getRGB()))
		// + ":"
		// + SubstanceColorUtilities.getColorBrightness(backgroundScheme
		// .getLightColor().getRGB()));

		return new ImageIcon(SubstanceImageCreator.overlayEcho(image,
				noEcho ? 0 : SubstanceColorUtilities.getColorStrength(color),
				1, 1));
	}

	/**
	 * Paints rectangular gradient background.
	 *
	 * @param g
	 *            Graphic context.
	 * @param startX
	 *            Background starting X coord.
	 * @param startY
	 *            Background starting Y coord.
	 * @param width
	 *            Background width.
	 * @param height
	 *            Background height.
	 * @param colorScheme
	 *            Color scheme for the background.
	 * @param borderAlpha
	 *            Border alpha.
	 * @param isVertical
	 *            if <code>true</code>, the gradient will be vertical, if
	 *            <code>false</code>, the gradient will be horizontal.
	 */
	public static void paintRectangularBackground(Component c, Graphics g,
			int startX, int startY, int width, int height,
			SubstanceColorScheme colorScheme, float borderAlpha,
			boolean isVertical) {
		Graphics2D graphics = (Graphics2D) g.create();
		graphics.translate(startX, startY);

		if (!isVertical) {
			LinearGradientPaint paint = new LinearGradientPaint(0, 0, 0,
					height, new float[] { 0.0f, 0.4f, 0.5f, 1.0f },
					new Color[] { colorScheme.getUltraLightColor(),
							colorScheme.getLightColor(),
							colorScheme.getMidColor(),
							colorScheme.getUltraLightColor() },
					CycleMethod.REPEAT);
			graphics.setPaint(paint);
			graphics.fillRect(0, 0, width, height);
		} else {
			LinearGradientPaint paint = new LinearGradientPaint(0, 0, width, 0,
					new float[] { 0.0f, 0.4f, 0.5f, 1.0f }, new Color[] {
							colorScheme.getUltraLightColor(),
							colorScheme.getLightColor(),
							colorScheme.getMidColor(),
							colorScheme.getUltraLightColor() },
					CycleMethod.REPEAT);
			graphics.setPaint(paint);
			graphics.fillRect(0, 0, width, height);
		}

		if (borderAlpha > 0.0f) {
			Graphics2D g2d = (Graphics2D) graphics.create();
			g2d.setComposite(LafWidgetUtilities.getAlphaComposite(null,
					borderAlpha, graphics));

			paintSimpleBorderAliased(c, g2d, width, height, colorScheme);

			g2d.dispose();
		}
		graphics.dispose();
	}

	/**
	 * Paints simple border.
	 *
	 * @param g2d
	 *            Graphics context.
	 * @param width
	 *            Border width.
	 * @param height
	 *            Border height.
	 * @param borderColorScheme
	 *            Border color scheme.
	 */
	public static void paintSimpleBorder(Component c, Graphics2D g2d,
			int width, int height, SubstanceColorScheme borderColorScheme) {
		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(c);
		// int borderDelta = (int) Math.floor(SubstanceSizeUtils
		// .getBorderStrokeWidth(componentFontSize) / 2.0);
		// int borderDelta2 = (int) Math.floor(SubstanceSizeUtils
		// .getBorderStrokeWidth(componentFontSize));
		float borderThickness = (float) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize));

		g2d.setColor(SubstanceColorUtilities
				.getMidBorderColor(borderColorScheme));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);
		int joinKind = BasicStroke.JOIN_ROUND;
		int capKind = BasicStroke.CAP_BUTT;
		g2d.setStroke(new BasicStroke(borderThickness, capKind, joinKind));
		g2d.draw(new Rectangle2D.Float((borderThickness - 1.0f) / 2.0f,
				(borderThickness - 1.0f) / 2.0f, width - 1
						- (borderThickness - 1.5f), height - 1
						- (borderThickness - 1.5f)));
		// g2d.drawRect(borderDelta, borderDelta, width - 1 - borderDelta2,
		// height
		// - 1 - borderDelta2);
	}

	public static void paintSimpleBorderAliased(Component c, Graphics2D g2d,
			int width, int height, SubstanceColorScheme colorScheme) {
		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(c);
		// int borderDelta = (int) Math.floor(SubstanceSizeUtils
		// .getBorderStrokeWidth(componentFontSize) / 2.0);
		// int borderDelta2 = (int) Math.floor(SubstanceSizeUtils
		// .getBorderStrokeWidth(componentFontSize));
		float borderThickness = (float) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize));

		g2d.setColor(SubstanceColorUtilities.getMidBorderColor(colorScheme));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);
		int joinKind = BasicStroke.JOIN_MITER;
		int capKind = BasicStroke.CAP_SQUARE;
		g2d.setStroke(new BasicStroke(borderThickness, capKind, joinKind));
		g2d.draw(new Rectangle2D.Float(borderThickness / 2.0f,
				borderThickness / 2.0f, width - borderThickness, height
						- borderThickness));
	}

	/**
	 * Paints rectangular gradient background with spots and optional replicated
	 * stripe image.
	 *
	 * @param g
	 *            Graphics context.
	 * @param startX
	 *            X start coordinate.
	 * @param startY
	 *            Y start coordinate.
	 * @param width
	 *            Background width.
	 * @param height
	 *            Background height.
	 * @param colorScheme
	 *            Color scheme for the background.
	 * @param stripeImage
	 *            Stripe image to replicate.
	 * @param stripeOffset
	 *            Offset of the first stripe replication.
	 * @param borderAlpha
	 *            Border alpha.
	 * @param isVertical
	 *            Indication of horizontal / vertical orientation.
	 */
	public static void paintRectangularStripedBackground(Component c,
			Graphics g, int startX, int startY, int width, int height,
			SubstanceColorScheme colorScheme, BufferedImage stripeImage,
			int stripeOffset, float borderAlpha, boolean isVertical) {
		Graphics2D graphics = (Graphics2D) g.create(startX, startY, width,
				height);
		if (!isVertical) {
			LinearGradientPaint paint = new LinearGradientPaint(0, 0, 0,
					height, new float[] { 0.0f, 0.2f, 0.5f, 0.8f, 1.0f },
					new Color[] { colorScheme.getDarkColor(),
							colorScheme.getLightColor(),
							colorScheme.getMidColor(),
							colorScheme.getLightColor(),
							colorScheme.getDarkColor() }, CycleMethod.REPEAT);
			graphics.setPaint(paint);
			graphics.fillRect(0, 0, width, height);

			if (stripeImage != null) {
				int stripeSize = stripeImage.getHeight();
				int stripeCount = width / stripeSize;
				stripeOffset = stripeOffset % (2 * stripeSize);
				for (int stripe = -2; stripe <= stripeCount; stripe += 2) {
					int stripePos = stripe * stripeSize + stripeOffset;

					graphics.drawImage(stripeImage, stripePos, 0, null);
				}
			}
		} else {
			LinearGradientPaint paint = new LinearGradientPaint(0, 0, width, 0,
					new float[] { 0.0f, 0.2f, 0.5f, 0.8f, 1.0f }, new Color[] {
							colorScheme.getDarkColor(),
							colorScheme.getLightColor(),
							colorScheme.getMidColor(),
							colorScheme.getLightColor(),
							colorScheme.getDarkColor() }, CycleMethod.REPEAT);
			graphics.setPaint(paint);
			graphics.fillRect(0, 0, width, height);

			if (stripeImage != null) {
				int stripeSize = stripeImage.getWidth();
				int stripeCount = height / stripeSize;
				stripeOffset = stripeOffset % (2 * stripeSize);
				for (int stripe = -2; stripe <= stripeCount; stripe += 2) {
					int stripePos = stripe * stripeSize + stripeOffset;

					graphics.drawImage(stripeImage, 0, stripePos, null);
				}
			}
		}

		if (borderAlpha > 0.0f) {
			Graphics2D g2d = (Graphics2D) graphics.create();
			g2d.setComposite(LafWidgetUtilities.getAlphaComposite(null,
					borderAlpha, graphics));

			paintSimpleBorderAliased(c, g2d, width, height, colorScheme);
			g2d.dispose();
		}
		graphics.dispose();
	}

	/**
	 * Returns diagonal stripe image.
	 *
	 * @param baseSize
	 *            Stripe base in pixels.
	 * @param color
	 *            Stripe color.
	 * @return Diagonal stripe image.
	 */
	public static BufferedImage getStripe(int baseSize, Color color) {
		int width = (int) (1.8 * baseSize);
		int height = baseSize;
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(width,
				height);
		Graphics2D graphics = (Graphics2D) result.getGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Polygon polygon = new Polygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(width - 1 - baseSize, 0);
		polygon.addPoint(width - 1, height - 1);
		polygon.addPoint(baseSize, height - 1);

		graphics.setColor(color);
		graphics.fillPolygon(polygon);
		graphics.drawPolygon(polygon);

		float[] BLUR = { 0.10f, 0.10f, 0.10f, 0.10f, 0.30f, 0.10f, 0.10f,
				0.10f, 0.10f };
		ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));
		BufferedImage blurred = vBlurOp.filter(result, null);

		return blurred;
	}

	/**
	 * Returns drag bumps image.
	 *
	 * @param c
	 *            Component.
	 * @param colorScheme
	 *            Color scheme.
	 * @param width
	 *            Drag bumps width.
	 * @param height
	 *            Drag bumps height.
	 * @param maxNumberOfStripes
	 *            The maximum number of bump stripes (rows or columns).
	 * @return Drag bumps image.
	 */
	public static BufferedImage getDragImage(Component c,
			SubstanceColorScheme colorScheme, int width, int height,
			int maxNumberOfStripes) {
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(width,
				height);
		Graphics2D graphics = (Graphics2D) result.getGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		boolean isDark = colorScheme.isDark();
		Color back1 = isDark ? colorScheme.getLightColor()
				: SubstanceColorUtilities.getInterpolatedColor(
						colorScheme.getLightColor(),
						colorScheme.getDarkColor(), 0.8);
		Color back2 = isDark ? colorScheme.getExtraLightColor()
				: SubstanceColorUtilities.getInterpolatedColor(
						colorScheme.getMidColor(), colorScheme.getDarkColor(),
						0.4);
		Color fore = isDark ? colorScheme.getDarkColor() : colorScheme
				.getUltraLightColor();

		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(c);
		int bumpDotDiameter = SubstanceSizeUtils
				.getDragBumpDiameter(componentFontSize);
		int bumpCellSize = (int) (1.5 * bumpDotDiameter + 1);
		int bumpRows = Math.max(1, height / bumpCellSize - 1);
		int bumpColumns = Math.max(1, (width - 2) / bumpCellSize);
		if (maxNumberOfStripes > 0) {
			if (height > width)
				bumpColumns = Math.min(bumpColumns, maxNumberOfStripes);
			else
				bumpRows = Math.min(bumpRows, maxNumberOfStripes);
		}

		int bumpRowOffset = (height - bumpCellSize * bumpRows) / 2;
		int bumpColOffset = 1 + (width - bumpCellSize * bumpColumns) / 2;

		for (int col = 0; col < bumpColumns; col++) {
			int cx = bumpColOffset + col * bumpCellSize;
			boolean isEvenCol = (col % 2 == 0);
			int offsetY = isEvenCol ? 0 : bumpDotDiameter;
			for (int row = 0; row < bumpRows; row++) {
				int cy = offsetY + bumpRowOffset + row * bumpCellSize;
				graphics.setColor(fore);
				graphics.fillOval(cx + 1, cy + 1, bumpDotDiameter,
						bumpDotDiameter);
				// graphics.setColor(back1);
				graphics.setPaint(new GradientPaint(cx, cy, back1, cx
						+ bumpDotDiameter - 1, cy + bumpDotDiameter - 1, back2));
				graphics.fillOval(cx, cy, bumpDotDiameter, bumpDotDiameter);
			}
		}
		return result;
	}

	/**
	 * Paints the bump dots on the split pane dividers.
	 *
	 * @param g
	 *            Graphics context.
	 * @param divider
	 *            Split pane divider.
	 * @param x
	 *            X coordinate of the bump dots.
	 * @param y
	 *            Y coordinate of the bump dots.
	 * @param width
	 *            Width of the bump dots area.
	 * @param height
	 *            Height of the bump dots area.
	 * @param isHorizontal
	 *            Indicates whether the dots are horizontal.
	 * @param colorScheme
	 *            First color scheme.
	 */
	public static void paintSplitDividerBumpImage(Graphics g,
			SubstanceSplitPaneDivider divider, int x, int y, int width,
			int height, boolean isHorizontal, SubstanceColorScheme colorScheme) {
		Graphics2D graphics = (Graphics2D) g.create();
		graphics.translate(x, y);

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int componentFontSize = SubstanceSizeUtils
				.getComponentFontSize(divider);
		int bumpDotDiameter = SubstanceSizeUtils
				.getBigDragBumpDiameter(componentFontSize);
		int bumpCellSize = (int) (1.5 * bumpDotDiameter + 1);
		int bumpRows = isHorizontal ? 1 : Math
				.max(1, height / bumpCellSize - 1);
		int bumpColumns = isHorizontal ? Math
				.max(1, (width - 2) / bumpCellSize) : 1;

		int bumpRowOffset = (height - bumpCellSize * bumpRows) / 2;
		int bumpColOffset = 1 + (width - bumpCellSize * bumpColumns) / 2;

		BufferedImage singleDot = SubstanceCoreUtilities.getBlankImage(
				bumpDotDiameter, bumpDotDiameter);
		Graphics2D dotGraphics = (Graphics2D) singleDot.getGraphics();
		dotGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color markColor = SubstanceColorUtilities.getMarkColor(colorScheme,
				divider.isEnabled());
		dotGraphics.setColor(markColor);
		dotGraphics.fillOval(0, 0, bumpDotDiameter, bumpDotDiameter);

		dotGraphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.4f));
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(divider);
		borderPainter.paintBorder(dotGraphics, divider, width, height,
				new Ellipse2D.Float(0, 0, bumpDotDiameter - 1,
						bumpDotDiameter - 1), null, colorScheme);

		graphics.setComposite(LafWidgetUtilities.getAlphaComposite(divider,
				0.8f, g));
		for (int col = 0; col < bumpColumns; col++) {
			int cx = bumpColOffset + col * bumpCellSize;
			for (int row = 0; row < bumpRows; row++) {
				int cy = bumpRowOffset + row * bumpCellSize
						+ (bumpCellSize - bumpDotDiameter) / 2;
				graphics.drawImage(singleDot, cx, cy, null);
			}
		}
		graphics.dispose();
	}

	/**
	 * Returns resize grip image.
	 *
	 * @param c
	 *            Component.
	 * @param colorScheme
	 *            Color scheme.
	 * @param dimension
	 *            Resize grip width.
	 * @param isCrowded
	 *            Indicates whether the grips should be painted closely.
	 * @return Resize grip image.
	 */
	public static BufferedImage getResizeGripImage(Component c,
			SubstanceColorScheme colorScheme, int dimension, boolean isCrowded) {
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(dimension,
				dimension);
		Graphics2D graphics = (Graphics2D) result.getGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		boolean isDark = colorScheme.isDark();
		// SubstanceCoreUtilities
		// .getActiveScheme(null) : SubstanceCoreUtilities
		// .getDefaultScheme(null);
		Color back1 = isDark ? colorScheme.getLightColor()
				: SubstanceColorUtilities.getInterpolatedColor(
						colorScheme.getLightColor(),
						colorScheme.getDarkColor(), 0.8);
		Color back2 = isDark ? colorScheme.getExtraLightColor()
				: SubstanceColorUtilities.getInterpolatedColor(
						colorScheme.getMidColor(), colorScheme.getDarkColor(),
						0.4);
		Color fore = isDark ? colorScheme.getDarkColor() : colorScheme
				.getUltraLightColor();

		int bumpDotDiameter = SubstanceSizeUtils
				.getDragBumpDiameter(SubstanceSizeUtils.getComponentFontSize(c));
		int bumpCellSize = (int) (1.5 * bumpDotDiameter + 1);
		if (isCrowded)
			bumpCellSize--;
		int bumpLines = dimension / bumpCellSize;

		int bumpOffset = (dimension - bumpCellSize * bumpLines) / 2;

		for (int col = 0; col < bumpLines; col++) {
			int cx = bumpOffset + col * bumpCellSize;
			for (int row = (bumpLines - col - 1); row < bumpLines; row++) {
				int cy = bumpOffset + row * bumpCellSize;
				graphics.setColor(fore);
				graphics.fillOval(cx + 1, cy + 1, bumpDotDiameter,
						bumpDotDiameter);
				// graphics.setColor(back1);
				graphics.setPaint(new GradientPaint(cx, cy, back1, cx
						+ bumpDotDiameter - 1, cy + bumpDotDiameter - 1, back2));
				graphics.fillOval(cx, cy, bumpDotDiameter, bumpDotDiameter);
			}
		}
		return result;
	}

	/**
	 * Retrieves tree icon.
	 *
	 * @param tree
	 *            Tree.
	 * @param fillScheme
	 *            Icon fill color scheme.
	 * @param borderScheme
	 *            Icon border color scheme.
	 * @param isCollapsed
	 *            Collapsed state.
	 * @return Tree icon.
	 */
	public static BufferedImage getTreeIcon(JTree tree,
			SubstanceColorScheme fillScheme, SubstanceColorScheme borderScheme,
			boolean isCollapsed) {
		int fontSize = SubstanceSizeUtils.getComponentFontSize(tree);
		int dim = SubstanceSizeUtils.getTreeIconSize(fontSize);
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(dim + 2,
				dim);
		Graphics2D graphics = (Graphics2D) result.getGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		Graphics2D g2 = (Graphics2D) graphics.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		SimplisticFillPainter gradPainter = new SimplisticSoftBorderReverseFillPainter();
		FlatBorderPainter fbp = new FlatBorderPainter();

		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(fontSize) / 2.0);
		Shape contour = SubstanceOutlineUtilities.getBaseOutline(dim - 1,
				dim - 1, SubstanceSizeUtils.getClassicButtonCornerRadius(dim),
				null, borderDelta);

		g2.translate(0, 1);

		boolean isDark = fillScheme.isDark();

		fillScheme = new ShiftColorScheme(fillScheme,
				fillScheme.getExtraLightColor(), 0.7);

		gradPainter.paintContourBackground(g2, tree, dim - 1, dim - 1, contour,
				false, fillScheme, false);
		borderScheme = new ShiftColorScheme(borderScheme,
				isDark ? borderScheme.getUltraLightColor()
						: borderScheme.getLightColor(), 0.5);
		fbp.paintBorder(g2, tree, dim - 1, dim - 1, contour, null, borderScheme);

		g2.translate(-1, -1);

		Color signColor = isDark ? borderScheme.getUltraLightColor().brighter()
				.brighter() : borderScheme.getUltraDarkColor();
		if ((tree != null) && !tree.isEnabled())
			signColor = borderScheme.getForegroundColor();
		g2.setColor(signColor);
		int mid = dim / 2;
		int length = 5 * dim / 12;
		g2.drawLine(mid - length / 2, dim / 2, mid + length / 2, dim / 2);
		if (isCollapsed) {
			g2.drawLine(dim / 2, mid - length / 2, dim / 2, mid + length / 2);
		}
		g2.dispose();

		return result;
	}

	/**
	 * Retrieves a single crayon of the specified color and dimensions for the
	 * crayon panel in color chooser.
	 *
	 * @param mainColor
	 *            Crayon main color.
	 * @param width
	 *            Crayon width.
	 * @param height
	 *            Crayon height.
	 * @return Crayon image.
	 */
	public static BufferedImage getSingleCrayon(Color mainColor, int width,
			int height) {
		BufferedImage image = SubstanceCoreUtilities.getBlankImage(width,
				height);

		int baseTop = (int) (0.2 * height);

		Graphics2D graphics = (Graphics2D) image.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int r = mainColor.getRed();
		int g = mainColor.getGreen();
		int b = mainColor.getBlue();
		// light coefficient
		double lc = 0.8;
		int lr = (int) (r + (255 - r) * lc);
		int lg = (int) (g + (255 - g) * lc);
		int lb = (int) (b + (255 - b) * lc);
		// dark coefficient
		double dc = 0.05;
		int dr = (int) ((1.0 - dc) * r);
		int dg = (int) ((1.0 - dc) * g);
		int db = (int) ((1.0 - dc) * b);

		Color lightColor = new Color(lr, lg, lb);
		Color darkColor = new Color(dr, dg, db);

		LinearGradientPaint paint = new LinearGradientPaint(0, 0, width, 0,
				new float[] { 0.0f, 0.3f, 0.5f, 0.9f, 1.0f }, new Color[] {
						lightColor, darkColor, darkColor, lightColor,
						lightColor }, CycleMethod.REPEAT);
		graphics.setPaint(paint);
		graphics.fillRect(0, baseTop, width, height);

		int dbwr = lr;
		int dbwg = lg;
		int dbwb = lb;
		int lbwr = 128 + dr / 4;
		int lbwg = 128 + dg / 4;
		int lbwb = 128 + db / 4;

		Color lightStripeColor = new Color(lbwr, lbwg, lbwb);
		Color darkStripeColor = new Color(dbwr, dbwg, dbwb);

		int stripeTop = (int) (0.35 * height);
		int stripeHeight = (int) (0.04 * height);
		LinearGradientPaint stripePaint = new LinearGradientPaint(0, 0, width,
				0, new float[] { 0.0f, 0.3f, 0.5f, 0.9f, 1.0f }, new Color[] {
						lightStripeColor, darkStripeColor, darkStripeColor,
						lightStripeColor, lightStripeColor },
				CycleMethod.REPEAT);
		graphics.setPaint(stripePaint);
		graphics.fillRect(0, stripeTop, width, stripeHeight);

		graphics.setColor(lightStripeColor);
		graphics.drawRect(0, stripeTop, width - 1, stripeHeight);

		// create cap path
		GeneralPath capPath = new GeneralPath();
		capPath.moveTo(0.5f * width - 3, 4);
		capPath.quadTo(0.5f * width, 0, 0.5f * width + 3, 4);
		capPath.lineTo(width - 3, baseTop);
		capPath.lineTo(2, baseTop);
		capPath.lineTo(0.5f * width - 3, 4);

		graphics.setClip(capPath);

		graphics.setPaint(new GradientPaint(0, baseTop / 2, lightColor,
				(int) (0.6 * width), baseTop, mainColor));
		graphics.fillRect(0, 0, width / 2, baseTop);
		graphics.setPaint(new GradientPaint(width, baseTop / 2, lightColor,
				(int) (0.4 * width), baseTop, mainColor));
		graphics.fillRect(width / 2, 0, width / 2, baseTop);

		graphics.setStroke(new BasicStroke((float) 1.3, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));

		graphics.setClip(null);
		graphics.setColor(new Color(64 + dr / 2, 64 + dg / 2, 64 + db / 2, 200));
		graphics.drawRect(0, baseTop, width - 1, height - baseTop - 1);
		graphics.draw(capPath);

		graphics.dispose();

		return image;
	}

	/**
	 * Crayon colors.
	 */
	private final static int[] crayonColors = { 0x800000, // Cayenne
			0x808000, // Asparagus
			0x008000, // Clover
			0x008080, // Teal
			0x000080, // Midnight
			0x800080, // Plum
			0x7f7f7f, // Tin
			0x808080, // Nickel

			0x804000, // Mocha
			0x408000, // Fern
			0x008040, // Moss
			0x004080, // Ocean
			0x400080, // Eggplant
			0x800040, // Maroon
			0x666666, // Steel
			0x999999, // Aluminium

			0xff0000, // Maraschino
			0xffff00, // Lemon
			0x00ff00, // Spring
			0x00ffff, // Turquoise
			0x0000ff, // Blueberry
			0xff00ff, // Magenta
			0x4c4c4c, // Iron
			0xb3b3b3, // Magnesium

			0xff8000, // Tangerine
			0x80ff00, // Lime
			0x00ff80, // Sea Foam
			0x0080ff, // Aqua
			0x8000ff, // Grape
			0xff0080, // Strawberry
			0x333333, // Tungsten
			0xcccccc, // Silver

			0xff6666, // Salmon
			0xffff66, // Banana
			0x66ff66, // Flora
			0x66ffff, // Ice
			0x6666ff, // Orchid
			0xff66ff, // Bubblegum
			0x191919, // Lead
			0xe6e6e6, // Mercury

			0xffcc66, // Cantaloupe
			0xccff66, // Honeydew
			0x66ffcc, // Spindrift
			0x66ccff, // Sky
			0xcc66ff, // Lavender
			0xff6fcf, // Carnation
			0x000000, // Licorice
			0xffffff, // Snow
	};

	/**
	 * Retrieves crayon X offset.
	 *
	 * @param i
	 *            Crayon index.
	 * @return Crayon X offset.
	 */
	private static int crayonX(int i) {
		return (i % 8) * 22 + 4 + ((i / 8) % 2) * 11;
	}

	/**
	 * Retrieves crayon Y offset.
	 *
	 * @param i
	 *            Crayon index.
	 * @return Crayon Y offset.
	 */
	private static int crayonY(int i) {
		return (i / 8) * 20 + 23;
	}

	/**
	 * Retrieves crayons image for the crayon panel of color chooser.
	 *
	 * @return Crayons image.
	 */
	public static Image getCrayonsImage() {
		int iw = 195;
		int ih = 208;
		Image image = SubstanceCoreUtilities.getBlankImage(iw, ih);
		Graphics2D graphics = (Graphics2D) image.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(new Color(240, 240, 240));
		graphics.fillRect(0, 0, iw, ih);

		for (int i = 0; i < SubstanceImageCreator.crayonColors.length; i++) {
			Color crayonColor = new Color(
					0xff000000 | SubstanceImageCreator.crayonColors[i]);
			Image crayonImage = SubstanceImageCreator.getSingleCrayon(
					crayonColor, 22, 120);
			graphics.drawImage(crayonImage, SubstanceImageCreator.crayonX(i),
					SubstanceImageCreator.crayonY(i), null);
		}

		graphics.setColor(new Color(190, 190, 190));
		graphics.drawRoundRect(0, 1, iw - 1, ih - 2, 4, 4);

		graphics.dispose();
		return image;
	}

	/**
	 * Returns small icon representation of the specified integer value. The
	 * remainder of dividing the integer by 16 is translated to four circles
	 * arranged in 2*2 grid.
	 *
	 * @param value
	 *            Integer value to represent.
	 * @param colorScheme
	 *            Icon color scheme.
	 * @return Icon representation of the specified integer value.
	 */
	public static Icon getHexaMarker(int value, SubstanceColorScheme colorScheme) {
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(9, 9);

		value %= 16;
		Color offColor = null;
		Color onColor = null;
		if (colorScheme == null) {
			return new ImageIcon(result);
		}
		boolean isDark = colorScheme.isDark();
		offColor = isDark ? colorScheme.getMidColor() : colorScheme
				.getMidColor().darker();
		onColor = isDark ? SubstanceColorUtilities.getInterpolatedColor(
				colorScheme.getUltraLightColor(), Color.white, 0.2)
				: colorScheme.getUltraDarkColor().darker();

		boolean bit1 = ((value & 0x1) != 0);
		boolean bit2 = ((value & 0x2) != 0);
		boolean bit3 = ((value & 0x4) != 0);
		boolean bit4 = ((value & 0x8) != 0);

		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(bit1 ? onColor : offColor);
		graphics.fillOval(5, 5, 4, 4);
		graphics.setColor(bit2 ? onColor : offColor);
		graphics.fillOval(5, 0, 4, 4);
		graphics.setColor(bit3 ? onColor : offColor);
		graphics.fillOval(0, 5, 4, 4);
		graphics.setColor(bit4 ? onColor : offColor);
		graphics.fillOval(0, 0, 4, 4);

		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Returns search icon.
	 *
	 * @param dimension
	 *            Icon dimension.
	 * @param colorScheme
	 *            Icon color scheme.
	 * @param leftToRight
	 *            LTR indication of the resulting icon.
	 * @return Search icon.
	 */
	public static Icon getSearchIcon(int dimension,
			SubstanceColorScheme colorScheme, boolean leftToRight) {
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(dimension,
				dimension);

		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color color = SubstanceColorUtilities.getMarkColor(colorScheme, true);
		graphics.setColor(color);

		graphics.setStroke(new BasicStroke(1.5f));
		if (leftToRight) {
			int xc = (int) (0.6 * dimension);
			int yc = (int) (0.45 * dimension);
			int r = (int) (0.3 * dimension);

			graphics.drawOval(xc - r, yc - r, 2 * r, 2 * r);

			graphics.setStroke(new BasicStroke(3.0f));
			GeneralPath handle = new GeneralPath();
			handle.moveTo((float) (xc - r / Math.sqrt(2.0)), (float) (yc + r
					/ Math.sqrt(2.0)));
			handle.lineTo(1.8f, dimension - 2.2f);
			graphics.draw(handle);
		} else {
			int xc = (int) (0.4 * dimension);
			int yc = (int) (0.45 * dimension);
			int r = (int) (0.3 * dimension);

			graphics.drawOval(xc - r, yc - r, 2 * r, 2 * r);

			graphics.setStroke(new BasicStroke(3.0f));
			GeneralPath handle = new GeneralPath();
			handle.moveTo((float) (xc + r / Math.sqrt(2.0)), (float) (yc + r
					/ Math.sqrt(2.0)));
			handle.lineTo(dimension - 2.5f, dimension - 2.2f);
			graphics.draw(handle);
		}

		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Returns an icon that matches the specified watermark.
	 *
	 * @param watermark
	 *            Watermark instance.
	 * @return Icon that matches the specified watermark.
	 */
	public static Icon getWatermarkIcon(SubstanceWatermark watermark) {
		int iSize = SubstanceSizeUtils.getTitlePaneIconSize();
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(iSize,
				iSize);
		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		watermark
				.previewWatermark(graphics,
						SubstanceColorSchemeUtilities.METALLIC_SKIN, 0, 0,
						iSize, iSize);
		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Returns a lock icon that matches the specified scheme.
	 *
	 * @param scheme
	 *            Scheme instance.
	 * @return Lock icon that matches the specified scheme.
	 */
	public static Icon getSmallLockIcon(SubstanceColorScheme scheme, Component c) {
		int componentFontSize = SubstanceSizeUtils.getComponentFontSize(c);
		int extraPadding = SubstanceSizeUtils
				.getExtraPadding(componentFontSize);
		int width = 6 + 2 * extraPadding;
		int height = 9 + 2 * extraPadding;
		BufferedImage result = SubstanceCoreUtilities.getBlankImage(width,
				height);

		Color fore = scheme.getForegroundColor();
		Color fill = new Color(208, 208, 48);

		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		float borderStrokeWidth = SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize) / 1.2f;
		float extraInsets = borderStrokeWidth / 2.0f;
		graphics.setStroke(new BasicStroke(borderStrokeWidth,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

		float lockPadTop = height / 3.0f;
		float lockPadBottom = height - extraInsets;
		float lockPadLeft = extraInsets;
		float lockPadRight = width - extraInsets;

		// lock pad fill
		graphics.setColor(fill);
		graphics.fill(new Rectangle2D.Float(lockPadLeft, lockPadTop,
				lockPadRight - lockPadLeft, lockPadBottom - lockPadTop));
		// lock pad outline
		graphics.setColor(fore);
		graphics.draw(new Rectangle2D.Float(lockPadLeft, lockPadTop,
				lockPadRight - lockPadLeft, lockPadBottom - lockPadTop));

		// lock handle
		graphics.setColor(fore);
		float lockHandleLeft = width / 4.0f;
		float lockHandleRight = width - width / 4.0f;

		GeneralPath handle = new GeneralPath();
		handle.moveTo(lockHandleLeft, lockPadTop);
		// up to top-left
		handle.lineTo(lockHandleLeft, extraInsets);
		// right to top-right
		handle.lineTo(lockHandleRight, extraInsets);
		// down
		handle.lineTo(lockHandleRight, lockPadTop);

		graphics.draw(handle);

		// lock keyhole
		graphics.setColor(fore);
		float lockKeyholeTop = lockPadTop + 2 * borderStrokeWidth;
		float lockKeyholeBottom = lockPadBottom - 2 * borderStrokeWidth + 1;
		float lockKeyholeLeft = lockHandleLeft + 1;
		float lockKeyholeRight = lockHandleRight;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setStroke(new BasicStroke(1.0f));
		graphics.fill(new Rectangle2D.Float(lockKeyholeLeft, lockKeyholeTop,
				lockKeyholeRight - lockKeyholeLeft, lockKeyholeBottom
						- lockKeyholeTop));

		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Returns the negative of the specified image.
	 *
	 * @param bi
	 *            Image.
	 * @return The negative of the specified image.
	 */
	public static BufferedImage getNegated(BufferedImage bi) {
		return new NegatedFilter().filter(bi, null);
	}

	/**
	 * Creates a new version of the specified icon that is rendered in the
	 * colors of the specified color scheme.
	 *
	 * @param comp
	 *            Component.
	 * @param original
	 *            The original icon.
	 * @param colorScheme
	 *            Color scheme.
	 * @return Scheme-based version of the original icon.
	 */
	public static BufferedImage getColorSchemeImage(Component comp,
			Icon original, SubstanceColorScheme colorScheme,
			float originalBrightnessFactor) {
		int w = original.getIconWidth();
		int h = original.getIconHeight();
		BufferedImage origImage = SubstanceCoreUtilities.getBlankImage(w, h);
		original.paintIcon(comp, origImage.getGraphics(), 0, 0);

		return getColorSchemeImage(origImage, colorScheme,
				originalBrightnessFactor);
	}

	/**
	 * Creates a new version of the specified image that is rendered in the
	 * colors of the specified color scheme.
	 *
	 * @param original
	 *            The original image.
	 * @param colorScheme
	 *            Color scheme.
	 * @return Scheme-based version of the original icon.
	 */
	public static BufferedImage getColorSchemeImage(BufferedImage original,
			SubstanceColorScheme colorScheme, float originalBrightnessFactor) {
		return ColorSchemeFilter.getColorSchemeFilter(colorScheme,
				originalBrightnessFactor).filter(original, null);
	}
}
