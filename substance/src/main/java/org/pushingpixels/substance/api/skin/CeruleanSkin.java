/*
 * Copyright (c) 2005-2012 Substance Kirill Grouchnikov and contributing authors. All Rights Reserved.
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

import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.colorscheme.BaseColorScheme;
import org.pushingpixels.substance.api.colorscheme.SteelBlueColorScheme;
import org.pushingpixels.substance.api.painter.border.GlassBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.ArcDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.GlassHighlightPainter;
import org.pushingpixels.substance.api.painter.overlay.BottomLineOverlayPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.internal.colorscheme.BlendBiColorScheme;

import java.awt.Color;

/**
 * <code>Cerulean</code> skin.
 */
public class CeruleanSkin extends SubstanceSkin {
    /**
     * Display name for <code>this</code> skin.
     */
    public static final String NAME = "Cerulean";


    /**
     * Overlay painter to paint separator lines on some decoration areas.
     */
    private BottomLineOverlayPainter bottomLineOverlayPainter;

    /**
     * Creates a new <code>Nebulous</code> skin.
     */
    public CeruleanSkin() {
        super();

        ColorSchemes schemes = SubstanceSkin
                .getColorSchemes("org/pushingpixels/substance/api/skin/nebula.colorschemes");

        SubstanceColorScheme activeScheme = schemes.get("Nebula Active");
        SubstanceColorScheme enabledScheme = schemes.get("Nebula Enabled").saturate(-0.9);
        SubstanceColorScheme rolloverUnselectedScheme = schemes
                .get("Nebula Rollover Unselected");
        final SubstanceColorScheme pressedScheme = schemes.get("Nebula Pressed");
        SubstanceColorScheme rolloverSelectedScheme = schemes
                .get("Nebula Rollover Selected");
        SubstanceColorScheme disabledScheme = schemes.get("Nebula Disabled").saturate(-0.9);

        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
                activeScheme, enabledScheme, disabledScheme);

        CopyMutableColorScheme steelBlue= new CopyMutableColorScheme("Cerulean Hover", new SteelBlueColorScheme().tint(0.4));
        steelBlue.setForegroundColor(enabledScheme.getForegroundColor());

        double saturate = 0.1;
        double tint = 0.4;
        double shade = tint/4;
        CopyMutableColorScheme pressed = new CopyMutableColorScheme("Cerulean Pressed", steelBlue.saturate(saturate).shade(shade));
        //pressed.setForegroundColor(pressedScheme.getForegroundColor());
        defaultSchemeBundle.registerColorScheme(pressed,
                ComponentState.PRESSED_SELECTED, ComponentState.PRESSED_UNSELECTED);
        defaultSchemeBundle.registerColorScheme(new BlendBiColorScheme(
                        steelBlue, disabledScheme, 0.25),
                ComponentState.DISABLED_SELECTED);
        defaultSchemeBundle.registerColorScheme(
                steelBlue.tint(tint).saturate(saturate),
                ComponentState.SELECTED);
        defaultSchemeBundle.registerColorScheme(
                steelBlue.shade(shade / 2).saturate(saturate/2),
                ComponentState.ROLLOVER_SELECTED);
        defaultSchemeBundle.registerColorScheme(
                steelBlue.tint(tint / 2).saturate(saturate/2),
                ComponentState.ROLLOVER_UNSELECTED);
        defaultSchemeBundle.registerColorScheme(steelBlue.shade(0.5),
                ColorSchemeAssociationKind.MARK, ComponentState.getActiveStates());
        defaultSchemeBundle.registerColorScheme(steelBlue,
                ColorSchemeAssociationKind.BORDER, ComponentState.getActiveStates());

        // for progress bars
        ComponentState determinateState = new ComponentState(
                "determinate enabled", new ComponentStateFacet[] {
                        ComponentStateFacet.ENABLE,
                        ComponentStateFacet.DETERMINATE,
                        ComponentStateFacet.SELECTION }, null);
        ComponentState determinateDisabledState = new ComponentState(
                "determinate disabled", new ComponentStateFacet[] {
                        ComponentStateFacet.DETERMINATE,
                        ComponentStateFacet.SELECTION },
                new ComponentStateFacet[] { ComponentStateFacet.ENABLE });
        ComponentState indeterminateState = new ComponentState(
                "indeterminate enabled",
                new ComponentStateFacet[] { ComponentStateFacet.ENABLE,
                        ComponentStateFacet.SELECTION },
                new ComponentStateFacet[] { ComponentStateFacet.DETERMINATE });
        ComponentState indeterminateDisabledState = new ComponentState(
                "indeterminate disabled", null, new ComponentStateFacet[] {
                        ComponentStateFacet.DETERMINATE,
                        ComponentStateFacet.ENABLE, ComponentStateFacet.SELECTION });
        defaultSchemeBundle.registerColorScheme(rolloverSelectedScheme,
                determinateState, indeterminateState);
        defaultSchemeBundle.registerColorScheme(rolloverSelectedScheme,
                ColorSchemeAssociationKind.BORDER,
                determinateState, indeterminateState);
        defaultSchemeBundle.registerColorScheme(disabledScheme,
                determinateDisabledState, indeterminateDisabledState);
        defaultSchemeBundle.registerColorScheme(disabledScheme,
                ColorSchemeAssociationKind.BORDER,
                determinateDisabledState, indeterminateDisabledState);

        // for uneditable fields
        ComponentState editable = new ComponentState("editable",
                new ComponentStateFacet[] {ComponentStateFacet.ENABLE, ComponentStateFacet.EDITABLE},
                null);
        ComponentState uneditable = new ComponentState("uneditable",
                editable, new ComponentStateFacet[] {ComponentStateFacet.ENABLE},
                new ComponentStateFacet[] {ComponentStateFacet.EDITABLE});
        defaultSchemeBundle.registerColorScheme(
                defaultSchemeBundle.getColorScheme(editable),
                ColorSchemeAssociationKind.FILL, uneditable
        );

        // for text highlight
        ColorSchemes kitchenSinkSchemes = SubstanceSkin
                .getColorSchemes("org/pushingpixels/substance/api/skin/kitchen-sink.colorschemes");
        SubstanceColorScheme highlightColorScheme = kitchenSinkSchemes
                .get("Moderate Highlight");
//        SubstanceColorScheme highlightColorScheme = activeScheme.hueShift(-0.5).saturate(0.5).tint(0.5);
        defaultSchemeBundle.registerHighlightColorScheme(highlightColorScheme);

        registerDecorationAreaSchemeBundle(defaultSchemeBundle,
                DecorationAreaType.NONE);

        CopyMutableColorScheme chrome = new CopyMutableColorScheme("Cerulean Chrome", pressedScheme);
        chrome.setUltraDarkColor(chrome.getExtraLightColor());
        registerDecorationAreaSchemeBundle(new SubstanceColorSchemeBundle(pressedScheme, pressedScheme, disabledScheme), chrome,
                DecorationAreaType.PRIMARY_TITLE_PANE,
                DecorationAreaType.SECONDARY_TITLE_PANE);


        this.registerAsDecorationArea(enabledScheme,
                DecorationAreaType.PRIMARY_TITLE_PANE_INACTIVE,
                DecorationAreaType.SECONDARY_TITLE_PANE_INACTIVE);


        registerAsDecorationArea(activeScheme.saturate(-0.75),
                DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
                DecorationAreaType.GENERAL);



        this.buttonShaper = new ClassicButtonShaper();
        this.fillPainter = new ClassicFillPainter();

        this.decorationPainter = new ArcDecorationPainter();

        this.highlightPainter = new GlassHighlightPainter();
        this.borderPainter = new GlassBorderPainter();
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
}

class CopyMutableColorScheme extends BaseColorScheme {

    Color foregroundColor;
    Color ultraLightColor;
    Color extraLightColor;
    Color lightColor;
    Color midColor;
    Color darkColor;
    Color ultraDarkColor;

    public CopyMutableColorScheme(String name, SubstanceColorScheme copy) {
        super(name, copy.isDark());
        foregroundColor = copy.getForegroundColor();
        ultraLightColor = copy.getUltraLightColor();
        extraLightColor = copy.getExtraLightColor();
        lightColor = copy.getLightColor();
        midColor = copy.getMidColor();
        darkColor = copy.getDarkColor();
        ultraDarkColor = copy.getUltraDarkColor();
    }

    public void setDark(boolean isDark) {
        this.isDark = isDark;
    }

    @Override
    public Color getDarkColor() {
        return darkColor;
    }

    public void setDarkColor(Color darkColor) {
        this.darkColor = darkColor;
    }

    @Override
    public Color getExtraLightColor() {
        return extraLightColor;
    }

    public void setExtraLightColor(Color extraLightColor) {
        this.extraLightColor = extraLightColor;
    }

    @Override
    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    @Override
    public Color getLightColor() {
        return lightColor;
    }

    public void setLightColor(Color lightColor) {
        this.lightColor = lightColor;
    }

    @Override
    public Color getMidColor() {
        return midColor;
    }

    public void setMidColor(Color midColor) {
        this.midColor = midColor;
    }

    @Override
    public Color getUltraDarkColor() {
        return ultraDarkColor;
    }

    public void setUltraDarkColor(Color ultraDarkColor) {
        this.ultraDarkColor = ultraDarkColor;
    }

    @Override
    public Color getUltraLightColor() {
        return ultraLightColor;
    }

    public void setUltraLightColor(Color ultraLightColor) {
        this.ultraLightColor = ultraLightColor;
    }
}
