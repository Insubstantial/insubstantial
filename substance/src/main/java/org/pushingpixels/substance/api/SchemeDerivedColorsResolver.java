package org.pushingpixels.substance.api;

import java.awt.Color;


/**
 * {@code SchemeDerivedColorResolver}s must be immutable. The resolvers are passed to derived color
 * schemes to ensure that derived scheme resolve derived colors in the same way as the base scheme.
 * 
 * @author Karl Schaefer
 */
public interface SchemeDerivedColorsResolver {
    /**
     * Determines if this resolver is for dark color schemes.
     * 
     * @return <code>true</code> if it should be used in dark schemes
     */
    boolean isDark();
    
    /**
     * Inverts this resolver, for use with inverted color schemes and switching from light to dark
     * schemes or vice versa.
     * <p>
     * Some resolvers may not support this option. They may choose to throw an
     * {@code UnsupportedOperationException} in that case. Instead of throwing the exception
     * developers may choose to simply return {@code this} signifying that the resolver cannot be
     * inverted. Another option would be to use assertions, allowing the developers to discover
     * mistakes during creation, but still being useful for clients:
     * 
     * <pre>
     * public void SchemeDerivedColorsResolver invert() {
     *     assert false : "this resolver cannot be inverted";
     *     
     *     return this;
     * }
     * </pre>
     * 
     * 
     * @return an inversion of this resolver
     * @throws UnsupportedOperationException
     *             if this resolver cannot be inverted
     */
    SchemeDerivedColorsResolver invert();
    
    /**
     * Resolves a derived color for a given color scheme.
     * 
     * @return the watermark stamp color for the supplied scheme.
     */
    Color getWatermarkStampColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the watermark light color for <code>this</code> scheme.
     * 
     * @return Watermark light color for <code>this</code> scheme.
     */
    Color getWatermarkLightColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the watermark dark color for <code>this</code> scheme.
     * 
     * @return Watermark dark color for <code>this</code> scheme.
     */
    Color getWatermarkDarkColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the line color for <code>this</code> scheme.
     * 
     * @return The line color for <code>this</code> scheme.
     */
    Color getLineColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the selection background color for <code>this</code> scheme.
     * 
     * @return The selection background color for <code>this</code> scheme.
     */
    Color getSelectionBackgroundColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the selection foreground color for <code>this</code> scheme.
     * 
     * @return The selection foreground color for <code>this</code> scheme.
     */
    Color getSelectionForegroundColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the background fill color for <code>this</code> scheme.
     * 
     * @return The background fill color for <code>this</code> scheme.
     */
    Color getBackgroundFillColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the text background fill color for <code>this</code> scheme.
     * 
     * @return The text background fill color for <code>this</code> scheme.
     */
    Color getTextBackgroundFillColor(SubstanceColorScheme colorScheme);

    /**
     * Returns the focus ring color for <code>this</code> scheme.
     * 
     * @return The focus ring color for <code>this</code> scheme.
     */
    Color getFocusRingColor(SubstanceColorScheme colorScheme);
}
