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

import java.awt.Color;

import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.painter.border.*;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.StandardFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.painter.overlay.*;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.api.watermark.SubstanceWatermark;

/**
 * <code>Twilight</code> skin. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 5.2
 */
public class TwilightSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Twilight";

	/**
	 * Overlay painter to paint a dark line along the bottom edge of the
	 * toolbars.
	 */
	private BottomLineOverlayPainter toolbarBottomLineOverlayPainter;

	/**
	 * Overlay painter to paint a light line along the top edge of the toolbars.
	 */
	private TopLineOverlayPainter toolbarTopLineOverlayPainter;

	/**
	 * Overlay painter to paint a bezel line along the top edge of the footer.
	 */
	private TopBezelOverlayPainter footerTopBezelOverlayPainter;
    public TwilightSkin(SubstanceWatermark watermark) {
        this();
        this.watermark = watermark;
    }
	/**
	 * Creates a new <code>Twilight</code> skin.
	 */
	public TwilightSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes("org/pushingpixels/substance/api/skin/twilight.colorschemes");
		SubstanceColorScheme activeScheme = schemes.get("Twilight Active");
		SubstanceColorScheme enabledScheme = schemes.get("Twilight Enabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledScheme, enabledScheme);
		defaultSchemeBundle.registerColorScheme(enabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);

		// borders
		SubstanceColorScheme borderDisabledSelectedScheme = schemes
				.get("Twilight Selected Disabled Border");
		SubstanceColorScheme borderScheme = schemes.get("Twilight Border");
		defaultSchemeBundle.registerColorScheme(borderDisabledSelectedScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.DISABLED_SELECTED);
		defaultSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.BORDER);

		// marks
		SubstanceColorScheme markActiveScheme = schemes
				.get("Twilight Mark Active");
		defaultSchemeBundle.registerColorScheme(markActiveScheme,
				ColorSchemeAssociationKind.MARK, ComponentState
						.getActiveStates());

		// separators
		SubstanceColorScheme separatorScheme = schemes
				.get("Twilight Separator");
		defaultSchemeBundle.registerColorScheme(separatorScheme,
				ColorSchemeAssociationKind.SEPARATOR);

		SubstanceColorScheme watermarkScheme = schemes
				.get("Twilight Watermark");

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				watermarkScheme, DecorationAreaType.NONE);

		SubstanceColorSchemeBundle decorationsSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledScheme, enabledScheme);
		decorationsSchemeBundle.registerColorScheme(enabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);

		// borders
		decorationsSchemeBundle.registerColorScheme(
				borderDisabledSelectedScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.DISABLED_SELECTED);
		decorationsSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.BORDER);

		// marks
		decorationsSchemeBundle.registerColorScheme(markActiveScheme,
				ColorSchemeAssociationKind.MARK, ComponentState
						.getActiveStates());

		// separators
		SubstanceColorScheme separatorDecorationsScheme = schemes
				.get("Twilight Decorations Separator");
		decorationsSchemeBundle.registerColorScheme(separatorDecorationsScheme,
				ColorSchemeAssociationKind.SEPARATOR);

		SubstanceColorScheme decorationsWatermarkScheme = schemes
				.get("Twilight Decorations Watermark");

		this.registerDecorationAreaSchemeBundle(decorationsSchemeBundle,
				decorationsWatermarkScheme, DecorationAreaType.TOOLBAR,
				DecorationAreaType.GENERAL, DecorationAreaType.FOOTER);

		SubstanceColorSchemeBundle headerSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledScheme, enabledScheme);
		headerSchemeBundle.registerColorScheme(enabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);

		// borders
		SubstanceColorScheme headerBorderScheme = schemes
				.get("Twilight Header Border");
		headerSchemeBundle.registerColorScheme(borderDisabledSelectedScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.DISABLED_SELECTED);
		headerSchemeBundle.registerColorScheme(headerBorderScheme,
				ColorSchemeAssociationKind.BORDER);
		// marks
		headerSchemeBundle.registerColorScheme(markActiveScheme,
				ColorSchemeAssociationKind.MARK, ComponentState
						.getActiveStates());

		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f,
				ComponentState.ROLLOVER_UNSELECTED,
				ComponentState.ROLLOVER_ARMED, ComponentState.ARMED);
		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f,
				ComponentState.SELECTED);
		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 1.0f,
				ComponentState.ROLLOVER_SELECTED);

		SubstanceColorScheme headerWatermarkScheme = schemes
				.get("Twilight Header Watermark");

		this.registerDecorationAreaSchemeBundle(headerSchemeBundle,
				headerWatermarkScheme, DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);

		setSelectedTabFadeStart(0.2);
		setSelectedTabFadeEnd(0.9);

		// Add overlay painters to paint drop shadows along the bottom
		// edges of toolbars and footers
		this.addOverlayPainter(BottomShadowOverlayPainter.getInstance(),
				DecorationAreaType.TOOLBAR);
		this.addOverlayPainter(BottomShadowOverlayPainter.getInstance(),
				DecorationAreaType.FOOTER);

		// add an overlay painter to paint a dark line along the bottom
		// edge of toolbars
		this.toolbarBottomLineOverlayPainter = new BottomLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						return scheme.getUltraDarkColor().darker();
					}
				});
		this.addOverlayPainter(this.toolbarBottomLineOverlayPainter,
				DecorationAreaType.TOOLBAR);

		// add an overlay painter to paint a dark line along the bottom
		// edge of toolbars
		this.toolbarTopLineOverlayPainter = new TopLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						Color fg = scheme.getForegroundColor();
						return new Color(fg.getRed(), fg.getGreen(), fg
								.getBlue(), 32);
					}
				});
		this.addOverlayPainter(this.toolbarTopLineOverlayPainter,
				DecorationAreaType.TOOLBAR);

		// add an overlay painter to paint a bezel line along the top
		// edge of footer
		this.footerTopBezelOverlayPainter = new TopBezelOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						return scheme.getUltraDarkColor().darker();
					}
				}, new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						Color fg = scheme.getForegroundColor();
						return new Color(fg.getRed(), fg.getGreen(), fg
								.getBlue(), 32);
					}
				});
		this.addOverlayPainter(this.footerTopBezelOverlayPainter,
				DecorationAreaType.FOOTER);

		this.buttonShaper = new ClassicButtonShaper();
		this.watermark = null;
		this.fillPainter = new StandardFillPainter();
		this.decorationPainter = new MatteDecorationPainter();
		this.highlightPainter = new ClassicHighlightPainter();
		this.borderPainter = new CompositeBorderPainter("Twilight",
				new ClassicBorderPainter(), new DelegateBorderPainter(
						"Twilight Inner", new ClassicBorderPainter(),
						0x40FFFFFF, 0x20FFFFFF, 0x00FFFFFF,
						new ColorSchemeTransform() {
							@Override
							public SubstanceColorScheme transform(
									SubstanceColorScheme scheme) {
								return scheme.tint(0.3);
							}
						}));
	}

	@Override
    public String getDisplayName() {
		return NAME;
	}
}
