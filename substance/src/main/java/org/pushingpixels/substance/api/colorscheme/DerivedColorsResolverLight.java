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
package org.pushingpixels.substance.api.colorscheme;

import java.awt.Color;

import org.pushingpixels.substance.api.SchemeDerivedColorsResolver;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

/**
 * Resolver of derived colors for light color schemes. This class is not
 * accessible outside the package and is for internal use only.
 * 
 * @author Kirill Grouchnikov
 * @author Karl Schaefer (immutable redesign)
 */
//TODO this class should be final
public class DerivedColorsResolverLight implements SchemeDerivedColorsResolver {
    static final DerivedColorsResolverLight INSTANCE = new DerivedColorsResolverLight();
    
    @Override
    public boolean isDark() {
        return false;
    }

    @Override
    public SchemeDerivedColorsResolver invert() {
        return DerivedColorsResolverDark.INSTANCE;
    }
    
	@Override
	public Color getWatermarkStampColor(SubstanceColorScheme colorScheme) {
		return SubstanceColorUtilities.getAlphaColor(colorScheme.getMidColor(),
				50);
	}

	@Override
    public Color getWatermarkLightColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getLightColor();
	}

	@Override
    public Color getWatermarkDarkColor(SubstanceColorScheme colorScheme) {
		return SubstanceColorUtilities.getAlphaColor(
				colorScheme.getDarkColor(), 15);
	}

	@Override
    public Color getLineColor(SubstanceColorScheme colorScheme) {
		return SubstanceColorUtilities.getInterpolatedColor(colorScheme
				.getMidColor(), colorScheme.getDarkColor(), 0.7);
	}

	@Override
	public Color getSelectionForegroundColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getUltraDarkColor().darker().darker();
	}

	@Override
	public Color getSelectionBackgroundColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getExtraLightColor();
	}

	@Override
	public Color getBackgroundFillColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getExtraLightColor();
	}

	@Override
	public Color getFocusRingColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getDarkColor();
	}

	@Override
	public Color getTextBackgroundFillColor(SubstanceColorScheme colorScheme) {
		return SubstanceColorUtilities.getInterpolatedColor(colorScheme
				.getUltraLightColor(), colorScheme.getExtraLightColor(), 0.8);
	}
}
