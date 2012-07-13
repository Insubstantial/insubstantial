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
 * Resolver of derived colors for dark color schemes. This class is not
 * accessible outside the package and is for internal use only.
 * 
 * @author Kirill Grouchnikov
 * @author Karl Schaefer (immutable redesign)
 */
//TODO this class should be final
class DerivedColorsResolverDark implements SchemeDerivedColorsResolver {
    static final DerivedColorsResolverDark INSTANCE = new DerivedColorsResolverDark();
    
    @Override
    public boolean isDark() {
        return true;
    }

    @Override
    public SchemeDerivedColorsResolver invert() {
        return DerivedColorsResolverLight.INSTANCE;
    }

	@Override
	public Color getWatermarkStampColor(SubstanceColorScheme colorScheme) {
		return SubstanceColorUtilities.getAlphaColor(colorScheme
				.getUltraLightColor(), 30);
	}

	@Override
	public Color getWatermarkDarkColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getLightColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.api.SchemeDerivedColors#getWatermarkLightColor()
	 */
	@Override
    public Color getWatermarkLightColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getUltraLightColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.api.SchemeDerivedColors#getLineColor()
	 */
	@Override
	public Color getLineColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getMidColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pushingpixels.substance.api.SchemeDerivedColors#getSelectionForegroundColor()
	 */
	@Override
	public Color getSelectionForegroundColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getUltraDarkColor().darker();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pushingpixels.substance.api.SchemeDerivedColors#getSelectionBackgroundColor()
	 */
	@Override
	public Color getSelectionBackgroundColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getUltraLightColor().brighter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.api.SchemeDerivedColors#getBackgroundFillColor()
	 */
	@Override
	public Color getBackgroundFillColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getDarkColor().brighter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.api.SchemeDerivedColors#getFocusRingColor()
	 */
	@Override
	public Color getFocusRingColor(SubstanceColorScheme colorScheme) {
		return colorScheme.getUltraDarkColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pushingpixels.substance.api.SchemeDerivedColors#getTextBackgroundFillColor()
	 */
	@Override
	public Color getTextBackgroundFillColor(SubstanceColorScheme colorScheme) {
		return SubstanceColorUtilities.getInterpolatedColor(colorScheme
				.getMidColor(), colorScheme.getLightColor(), 0.4);
	}
}
