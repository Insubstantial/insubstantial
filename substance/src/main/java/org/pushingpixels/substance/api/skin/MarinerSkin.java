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
package org.pushingpixels.substance.api.skin;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.FractionBasedBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.painter.overlay.*;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.api.watermark.SubstanceCrosshatchWatermark;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;
/**
 * <code>Mariner</code> skin. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 6.1
 */
public class MarinerSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Mariner";

	/**
	 * Overlay painter to paint a dark line along the bottom edge of the
	 * menubar.
	 */
	private BottomLineOverlayPainter menuOverlayPainter;

	/**
	 * Overlay painter to paint a light line along the top edge of the toolbars.
	 */
	private TopLineOverlayPainter toolbarOverlayPainter;

	/**
	 * Overlay painter to paint a dark line along the bottom edge of the
	 * toolbars.
	 */
	private BottomLineOverlayPainter toolbarBottomLineOverlayPainter;

	/**
	 * Overlay painter to paint a bezel line along the top edge of the footer.
	 */
	private TopBezelOverlayPainter footerTopBezelOverlayPainter;
    public MarinerSkin(SubstanceWatermark watermark) {
        this();
        this.watermark = watermark;
    }
	/**
	 * Creates a new <code>Mariner</code> skin.
	 */
	public MarinerSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes("org/pushingpixels/substance/api/skin/mariner.colorschemes");

		SubstanceColorScheme activeScheme = schemes.get("Mariner Active");
		SubstanceColorScheme enabledScheme = schemes.get("Mariner Enabled");
		SubstanceColorScheme disabledScheme = schemes.get("Mariner Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledScheme, disabledScheme);

		defaultSchemeBundle.registerColorScheme(activeScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);
		defaultSchemeBundle.registerColorScheme(disabledScheme, 0.8f,
				ComponentState.DISABLED_UNSELECTED);

		// borders
		SubstanceColorScheme activeBorderScheme = schemes
				.get("Mariner Active Border");
		SubstanceColorScheme enabledBorderScheme = schemes
				.get("Mariner Enabled Border");
		defaultSchemeBundle.registerColorScheme(activeBorderScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(activeBorderScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.DISABLED_SELECTED);
		defaultSchemeBundle.registerColorScheme(enabledBorderScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState.ENABLED);

		// marks
		SubstanceColorScheme activeMarkScheme = schemes
				.get("Mariner Active Mark");
		SubstanceColorScheme enabledMarkScheme = schemes
				.get("Mariner Enabled Mark");
		defaultSchemeBundle.registerColorScheme(activeMarkScheme,
				ColorSchemeAssociationKind.MARK, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(enabledMarkScheme,
				ColorSchemeAssociationKind.MARK, ComponentState.ENABLED);

		ComponentState uneditable = new ComponentState("uneditable",
				new ComponentStateFacet[] { ComponentStateFacet.ENABLE },
				new ComponentStateFacet[] { ComponentStateFacet.EDITABLE });
		SubstanceColorScheme uneditableControls = schemes
				.get("Mariner Uneditable");
		defaultSchemeBundle.registerColorScheme(uneditableControls,
				ColorSchemeAssociationKind.FILL, uneditable);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		// header color scheme bundle
		SubstanceColorScheme headerColorScheme = schemes.get("Mariner Header");
		SubstanceColorScheme headerBorderColorScheme = schemes
				.get("Mariner Header Border");
		SubstanceColorSchemeBundle headerSchemeBundle = new SubstanceColorSchemeBundle(
				headerColorScheme, headerColorScheme, headerColorScheme);
		headerSchemeBundle.registerColorScheme(headerColorScheme, 0.5f,
				ComponentState.DISABLED_SELECTED,
				ComponentState.DISABLED_UNSELECTED);
		headerSchemeBundle.registerColorScheme(headerColorScheme,
				ComponentState.ROLLOVER_UNSELECTED);
		headerSchemeBundle.registerColorScheme(headerColorScheme,
				ColorSchemeAssociationKind.MARK);
		headerSchemeBundle.registerColorScheme(headerBorderColorScheme,
				ColorSchemeAssociationKind.BORDER);
		this.registerDecorationAreaSchemeBundle(headerSchemeBundle,
				headerColorScheme, DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);

		// footer color scheme bundle
		SubstanceColorScheme enabledFooterScheme = schemes
				.get("Mariner Footer Enabled");
		SubstanceColorScheme disabledFooterScheme = schemes
				.get("Mariner Footer Disabled");

		SubstanceColorSchemeBundle footerSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledFooterScheme, disabledFooterScheme);

		footerSchemeBundle.registerColorScheme(activeScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);
		footerSchemeBundle.registerColorScheme(disabledFooterScheme, 0.8f,
				ComponentState.DISABLED_UNSELECTED);

		// borders
		SubstanceColorScheme footerEnabledBorderScheme = schemes
				.get("Mariner Footer Enabled Border");
		footerSchemeBundle.registerColorScheme(activeBorderScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState
						.getActiveStates());
		footerSchemeBundle.registerColorScheme(activeBorderScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.DISABLED_SELECTED);
		footerSchemeBundle.registerColorScheme(footerEnabledBorderScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState.ENABLED);

		// marks
		SubstanceColorScheme footerEnabledMarkScheme = schemes
				.get("Mariner Footer Enabled Mark");
		footerSchemeBundle.registerColorScheme(activeMarkScheme,
				ColorSchemeAssociationKind.MARK, ComponentState
						.getActiveStates());
		footerSchemeBundle.registerColorScheme(footerEnabledMarkScheme,
				ColorSchemeAssociationKind.MARK, ComponentState.ENABLED);

		// separators
		SubstanceColorScheme footerSeparatorScheme = schemes
				.get("Mariner Footer Separator");
		footerSchemeBundle.registerColorScheme(footerSeparatorScheme,
				ColorSchemeAssociationKind.SEPARATOR);

		SubstanceColorScheme footerWatermarkColorScheme = schemes
				.get("Mariner Footer Watermark");
		this.registerDecorationAreaSchemeBundle(footerSchemeBundle,
				footerWatermarkColorScheme, DecorationAreaType.FOOTER,
				DecorationAreaType.TOOLBAR, DecorationAreaType.GENERAL);

		this.setSelectedTabFadeStart(0.15);
		this.setSelectedTabFadeEnd(0.25);

		// add an overlay painter to paint a bezel line along the top
		// edge of footer
		this.footerTopBezelOverlayPainter = new TopBezelOverlayPainter(
				ColorSchemeSingleColorQuery.ULTRADARK,
				ColorSchemeSingleColorQuery.LIGHT);
		this.addOverlayPainter(this.footerTopBezelOverlayPainter,
				DecorationAreaType.FOOTER);

		// add two overlay painters to create a bezel line between
		// menu bar and toolbars
		this.menuOverlayPainter = new BottomLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						return scheme.getUltraDarkColor().darker();
					}
				});
		this.toolbarOverlayPainter = new TopLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						Color fg = scheme.getForegroundColor();
						return new Color(fg.getRed(), fg.getGreen(), fg
								.getBlue(), 32);
					}
				});
		this.addOverlayPainter(this.menuOverlayPainter,
				DecorationAreaType.HEADER);
		this.addOverlayPainter(this.toolbarOverlayPainter,
				DecorationAreaType.TOOLBAR);

		// add overlay painter to paint drop shadows along the bottom
		// edges of toolbars
		this.addOverlayPainter(BottomShadowOverlayPainter.getInstance(),
				DecorationAreaType.TOOLBAR);

		// add overlay painter to paint a dark line along the bottom
		// edge of toolbars
		this.toolbarBottomLineOverlayPainter = new BottomLineOverlayPainter(
				ColorSchemeSingleColorQuery.ULTRADARK);
		this.addOverlayPainter(this.toolbarBottomLineOverlayPainter,
				DecorationAreaType.TOOLBAR);

		this.buttonShaper = new ClassicButtonShaper();
		this.watermark = new TextureWatermark();
		this.fillPainter = new FractionBasedFillPainter("Mariner", new float[] {
				0.0f, 0.5f, 1.0f }, new ColorSchemeSingleColorQuery[] {
				ColorSchemeSingleColorQuery.EXTRALIGHT,
				ColorSchemeSingleColorQuery.LIGHT,
				ColorSchemeSingleColorQuery.MID });

		this.decorationPainter = new MatteDecorationPainter();
		this.highlightPainter = new ClassicHighlightPainter();

		this.borderPainter = new FractionBasedBorderPainter("Mariner",
				new float[] { 0.0f, 0.5f, 1.0f },
				new ColorSchemeSingleColorQuery[] {
						ColorSchemeSingleColorQuery.ULTRADARK,
						ColorSchemeSingleColorQuery.DARK,
						ColorSchemeSingleColorQuery.MID });
		this.highlightBorderPainter = new ClassicBorderPainter();

		this.watermarkScheme = schemes.get("Mariner Watermark");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.skin.SubstanceSkin#getDisplayName()
	 */
	@Override
    public String getDisplayName() {
		return NAME;
	}

	private static class TextureWatermark implements SubstanceWatermark {
		/**
		 * Watermark image (screen-sized).
		 */
		private static Image watermarkImage = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.pushingpixels.substance.watermark.SubstanceWatermark#
		 * drawWatermarkImage(java .awt.Graphics, int, int, int, int)
		 */
		@Override
        public void drawWatermarkImage(Graphics graphics, Component c, int x,
				int y, int width, int height) {
			if (!c.isShowing())
				return;
			int dx = c.getLocationOnScreen().x;
			int dy = c.getLocationOnScreen().y;
			graphics.drawImage(TextureWatermark.watermarkImage, x, y,
					x + width, y + height, x + dx, y + dy, x + dx + width, y
							+ dy + height, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.pushingpixels.substance.watermark.SubstanceWatermark#
		 * updateWatermarkImage (org.pushingpixels.substance.skin.SubstanceSkin)
		 */
		@Override
        public boolean updateWatermarkImage(SubstanceSkin skin) {
			// fix by Chris for bug 67 - support for multiple screens
			Rectangle virtualBounds = new Rectangle();
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice[] gds = ge.getScreenDevices();
			for (GraphicsDevice gd : gds) {
				GraphicsConfiguration gc = gd.getDefaultConfiguration();
				virtualBounds = virtualBounds.union(gc.getBounds());
			}

			int screenWidth = virtualBounds.width;
			int screenHeight = virtualBounds.height;
			TextureWatermark.watermarkImage = SubstanceCoreUtilities
					.getBlankImage(screenWidth, screenHeight);

			Graphics2D graphics = (Graphics2D) TextureWatermark.watermarkImage
					.getGraphics().create();
			boolean status = this.drawWatermarkImage(skin, graphics, 0, 0,
					screenWidth, screenHeight, false);
			graphics.dispose();
			return status;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.pushingpixels.substance.api.watermark.SubstanceWatermark#
		 * previewWatermark (java.awt.Graphics,
		 * org.pushingpixels.substance.api.SubstanceSkin, int, int, int, int)
		 */
		@Override
		public void previewWatermark(Graphics g, SubstanceSkin skin, int x,
				int y, int width, int height) {
			this.drawWatermarkImage(skin, (Graphics2D) g, x, y, width, height,
					true);
		}

		/**
		 * Draws the specified portion of the watermark image.
		 * 
		 * @param skin
		 *            Skin to use for painting the watermark.
		 * @param graphics
		 *            Graphic context.
		 * @param x
		 *            the <i>x</i> coordinate of the watermark to be drawn.
		 * @param y
		 *            The <i>y</i> coordinate of the watermark to be drawn.
		 * @param width
		 *            The width of the watermark to be drawn.
		 * @param height
		 *            The height of the watermark to be drawn.
		 * @param isPreview
		 *            Indication whether the result is a preview image.
		 * @return Indication whether the draw succeeded.
		 */
		private boolean drawWatermarkImage(SubstanceSkin skin,
				Graphics2D graphics, int x, int y, int width, int height,
				boolean isPreview) {
			Color stampColorDark;
			Color stampColorAll;
			//Color stampColorLight = null;
			SubstanceColorScheme scheme = skin.getWatermarkColorScheme();
			if (isPreview) {
				stampColorDark = scheme.isDark() ? Color.white : Color.black;
				stampColorAll = Color.lightGray;
				//stampColorLight = scheme.isDark() ? Color.black : Color.white;
			} else {
				stampColorDark = scheme.getWatermarkDarkColor();
				stampColorAll = scheme.getWatermarkStampColor();
				//stampColorLight = scheme.getWatermarkLightColor();
			}

			graphics.setColor(stampColorAll);
			graphics.fillRect(0, 0, width, height);

			BufferedImage tile = SubstanceCoreUtilities.getBlankImage(8, 4);
			int rgbDark = stampColorDark.getRGB();
			tile.setRGB(0, 0, rgbDark);
			tile.setRGB(0, 1, rgbDark);
			tile.setRGB(0, 2, rgbDark);
			tile.setRGB(0, 3, rgbDark);
			tile.setRGB(1, 2, rgbDark);
			tile.setRGB(2, 1, rgbDark);
			tile.setRGB(3, 0, rgbDark);
			tile.setRGB(4, 0, rgbDark);
			tile.setRGB(4, 1, rgbDark);
			tile.setRGB(4, 2, rgbDark);
			tile.setRGB(4, 3, rgbDark);
			tile.setRGB(5, 0, rgbDark);
			tile.setRGB(6, 1, rgbDark);
			tile.setRGB(7, 2, rgbDark);

			Graphics2D g2d = (Graphics2D) graphics.create();
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.05f));
			for (int row = y; row < (y + height); row += 4) {
				for (int col = x; col < (x + width); col += 8) {
					g2d.drawImage(tile, col, row, null);
				}
			}
			g2d.dispose();
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.pushingpixels.substance.api.trait.SubstanceTrait#getDisplayName()
		 */
		@Override
        public String getDisplayName() {
			return SubstanceCrosshatchWatermark.getName();
		}

		/**
		 * Returns the name of all watermarks of <code>this</code> class.
		 * 
		 * @return The name of all watermarks of <code>this</code> class.
		 */
		public static String getName() {
			return "Crosshatch";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.pushingpixels.substance.watermark.SubstanceWatermark#dispose()
		 */
		@Override
        public void dispose() {
			watermarkImage = null;
		}
	}
}
