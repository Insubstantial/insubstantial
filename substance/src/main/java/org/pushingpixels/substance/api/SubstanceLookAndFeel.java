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
package org.pushingpixels.substance.api;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.pushingpixels.lafplugin.*;
import org.pushingpixels.lafwidget.LafWidgetRepository;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;
import org.pushingpixels.lafwidget.utils.LookUtils;
import org.pushingpixels.substance.api.SubstanceConstants.MenuGutterFillKind;
import org.pushingpixels.substance.api.SubstanceConstants.SubstanceWidgetType;
import org.pushingpixels.substance.api.combo.ComboPopupPrototypeCallback;
import org.pushingpixels.substance.api.fonts.*;
import org.pushingpixels.substance.api.inputmaps.InputMapSet;
import org.pushingpixels.substance.api.inputmaps.SubstanceInputMapUtilities;
import org.pushingpixels.substance.api.shaper.*;
import org.pushingpixels.substance.api.skin.SkinChangeListener;
import org.pushingpixels.substance.api.skin.SkinInfo;
import org.pushingpixels.substance.api.tabbed.BaseTabCloseListener;
import org.pushingpixels.substance.api.tabbed.TabCloseCallback;
import org.pushingpixels.substance.internal.contrib.jgoodies.looks.common.ShadowPopupFactory;
import org.pushingpixels.substance.internal.fonts.FontPolicies;
import org.pushingpixels.substance.internal.painter.DecorationPainterUtils;
import org.pushingpixels.substance.internal.plugin.SubstanceSkinPlugin;
import org.pushingpixels.substance.internal.ui.SubstanceRootPaneUI;
import org.pushingpixels.substance.internal.utils.*;

/**
 * <p>
 * Main class for <b>Substance </b> look and feel. <b>All</b> static methods in
 * this class should be called when Substance is the currently set look and feel
 * unless explicitly stated otherwise.
 * </p>
 * 
 * <p>
 * Since version 5.0 this class is abstract. There are three options to use
 * Substance:
 * </p>
 * 
 * <ul>
 * <li>Use one of the core skin-based look-and-feels in the
 * <code>org.pushingpixels.substance.skin</code> package.</li>
 * <li>Extend this class and pass a skin instance to the
 * {@link SubstanceLookAndFeel#SubstanceLookAndFeel(SubstanceSkin)} constructor.
 * </li>
 * <li>Call {@link SubstanceLookAndFeel#setSkin(String)} or
 * {@link SubstanceLookAndFeel#setSkin(SubstanceSkin)} static methods. These
 * methods do not require Substance to be the current look-and-feel.</li>
 * </ul>
 * 
 * @author Kirill Grouchnikov
 */
public abstract class SubstanceLookAndFeel extends BasicLookAndFeel {
	/**
	 * The name of plugin configuration XML resource name. This is used for the
	 * <a href="https://laf-plugin.dev.java.net">laf-plugin</a> support layer of
	 * third-party components.
	 */
	public static final String PLUGIN_XML = "META-INF/substance-plugin.xml";

	/**
	 * Plugin manager for component plugins.
	 */
	private static ComponentPluginManager componentPlugins;

	/**
	 * Plugin manager for skin plugins.
	 */
	private static PluginManager skinPlugins;

	/**
	 * List of all listeners on skin changes.
	 */
	protected final static Set<SkinChangeListener> skinChangeListeners = new HashSet<SkinChangeListener>();

	/**
	 * List of all listeners on changing locales.
	 */
	protected final static Set<LocaleChangeListener> localeChangeListeners = new HashSet<LocaleChangeListener>();

	/**
	 * Indicates whether option dialogs (error, question, warning, info) should
	 * use constant color schemes for icon coloring. Note that since version
	 * 4.0, the default setting is <code>true</code> (use constant color
	 * scheme). To use color scheme-consistent coloring, call
	 * {@link #setToUseConstantThemesOnDialogs(boolean)} and pass
	 * <code>false</code>.
	 * 
	 * @see #isToUseConstantThemesOnDialogs()
	 * @see #setToUseConstantThemesOnDialogs(boolean)
	 */
	private static boolean toUseConstantThemesOnDialogs = true;

	/**
	 * Change listener on keyboard focus manager - fix for defect 208.
	 */
	protected PropertyChangeListener focusOwnerChangeListener;

	/**
	 * The current keyboard focus manager - fix for defect 208.
	 */
	protected KeyboardFocusManager currentKeyboardFocusManager;

	/**
	 * Smart tree scroll animation facet. Disabled by default, use
	 * {@link AnimationConfigurationManager#allowAnimations(AnimationFacet)} to
	 * enable. </p>
	 * 
	 * <p>
	 * Smart tree scroll is relevant for scroll panes containing a tree. When
	 * enabled, it automatically scrolls the tree horizontally when the viewport
	 * shows mainly empty area (especially relevant for multi-level trees with
	 * narrow viewports).
	 * </p>
	 * 
	 * @since 4.0
	 */
	public final static AnimationFacet TREE_SMART_SCROLL_ANIMATION_KIND = new AnimationFacet(
			"substancelaf.treeSmartScrollAnimationKind", false);

	/**
	 * Client property name for requesting that watermark should be painted on
	 * the component and its descendants. This property can be set either as
	 * client property on some component or as global property on
	 * {@link UIManager}. The value should be either {@link Boolean#TRUE} or
	 * {@link Boolean#FALSE}.
	 * 
	 * <p>
	 * In order to compute whether the current watermark should be painted on a
	 * given component, its hierarchy is traversed bottom up. The first
	 * component that has this property set defines the watermark visibility. If
	 * neither component nor its ancestors define this property, the global
	 * setting on {@link UIManager} is checked. If there is no global setting,
	 * the watermark is <b>not</b> ignored (it is painted).
	 * </p>
	 * 
	 * <p>
	 * There is special default setting for trees, tables, lists and text
	 * components. These show watermark only when this property is explicitly
	 * set to {@link Boolean#TRUE} on the component itself, one of its ancestors
	 * or the {@link UIManager}.
	 * </p>
	 * 
	 * @since version 5.0
	 */
	public static final String WATERMARK_VISIBLE = "substancelaf.watermark.visible";

	/**
	 * Client property name for ignoring the default (minimum) dimension for a
	 * single button. This property can be set either on the specific button or
	 * as a global setting on {@link UIManager}. The value should be either
	 * {@link Boolean#TRUE} or {@link Boolean#FALSE}.
	 * <p>
	 * Note that {@link SubstanceButtonShaper} implementations are not required
	 * to respect this property. The current implementations of the default
	 * {@link StandardButtonShaper} and {@link ClassicButtonShaper} respect this
	 * property.
	 * </p>
	 * 
	 * <p>
	 * Example of marking a button to ignore minimum dimension settings:
	 * </p>
	 * <code>
	 * JButton button = new JButton("text");<br>
	 * button.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * <p>
	 * Example of marking all application buttons to ignore minimum dimension
	 * settings:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * 
	 * @since version 2.1
	 */
	public static final String BUTTON_NO_MIN_SIZE_PROPERTY = "substancelaf.buttonnominsize";

	/**
	 * Client property name for specifying that a single button / all
	 * application buttons should not paint the background. This property can be
	 * set on the specific button, its parent or as a global setting on
	 * {@link UIManager}. The value should be either {@link Boolean#TRUE} or
	 * {@link Boolean#FALSE}. Note that unlike the {@link #FLAT_PROPERTY}, a
	 * button marked with this property will <b>never</b> show the background
	 * (will always be painted flat).
	 * 
	 * <p>
	 * Example of marking a button to never paint background:
	 * </p>
	 * <code>
	 * JButton button = new JButton("text");<br>
	 * button.putClientProperty(SubstanceLookAndFeel.BUTTON_PAINT_NEVER_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * 
	 * <p>
	 * Example of marking all application buttons to never paint background:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.BUTTON_PAINT_NEVER_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * 
	 * @since version 2.3
	 * @see #FLAT_PROPERTY
	 */
	public static final String BUTTON_PAINT_NEVER_PROPERTY = "substancelaf.buttonpaintnever";

	/**
	 * Client property name for specifying a straight side for a single button.
	 * This property must be set on the specific button. The value can be:
	 * 
	 * <p>
	 * <ul>
	 * <li>A value in {@link SubstanceConstants.Side} enum.
	 * <li>Set of values in {@link SubstanceConstants.Side} enum.
	 * </ul>
	 * 
	 * <p>
	 * Note that the {@link SubstanceButtonShaper} implementations are not
	 * required to respect this property. The default
	 * {@link StandardButtonShaper} and {@link ClassicButtonShaper} respect this
	 * property.
	 * </p>
	 * 
	 * <p>
	 * Example of marking a button to have straight north side:
	 * </p>
	 * <code>
	 * JButton button = new JButton("text");<br>
	 * button.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY,<br>
	 * &nbsp;&nbsp;SubstanceConstants.Side.RIGHT);
	 * </code>
	 * 
	 * @since version 2.1
	 * @see #BUTTON_OPEN_SIDE_PROPERTY
	 */
	public static final String BUTTON_SIDE_PROPERTY = "substancelaf.buttonside";

	/**
	 * Client property name for specifying an open side for a single button.
	 * This property must be set on the specific button. The value can be:
	 * 
	 * <p>
	 * <ul>
	 * <li>A value in {@link SubstanceConstants.Side} enum.
	 * <li>Set of values in {@link SubstanceConstants.Side} enum.
	 * </ul>
	 * </p>
	 * <p>
	 * Example of marking a button to have open top and west sides:
	 * </p>
	 * <code>
	 * JButton button = new JButton("text");<br>
	 * Set<Side> openSides = EnumSet.of(Side.TOP, Side.WEST);<br>
	 * button.putClientProperty(SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY, <br>
	 * &nbsp;&nbsp;openSides);
	 * </code>
	 * 
	 * @since version 3.1
	 * @see #BUTTON_SIDE_PROPERTY
	 */
	public static final String BUTTON_OPEN_SIDE_PROPERTY = "substancelaf.buttonopenSide";

	/**
	 * Client property name for specifying the corner radius for buttons.
	 * Currently, this property is respected only on toolbar buttons. This
	 * property can be set on the specific toolbar button, on the specific
	 * toolbar (will hold for all buttons in the toolbar) or as a global setting
	 * on {@link UIManager}. The value should be a positive {@link Float}.
	 * 
	 * <p>
	 * Example of specifying a (toolbar) button to have corner radius of 5
	 * pixels:
	 * </p>
	 * <code>
	 * JButton button = new JButton("text");<br>
	 * button.putClientProperty(SubstanceLookAndFeel.CORNER_RADIUS, <br>
	 * &nbsp;&nbsp;Float.valueOf(5.0f));
	 * </code>
	 * 
	 * <p>
	 * Example of specifying all buttons of a toolbar to have corner radius of 3
	 * pixels:
	 * </p>
	 * <code>
	 * JToolBar toolbar = new JToolBar("toolbar");<br>
	 * toolbar.putClientProperty(SubstanceLookAndFeel.CORNER_RADIUS, <br>
	 * &nbsp;&nbsp;Float.valueOf(3.0f));
	 * </code>
	 * 
	 * <p>
	 * Example of specifying all toolbar buttons to have corner radius of 0
	 * pixels:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.CORNER_RADIUS, Float.valueOf(0.0f));
	 * </code>
	 * 
	 * @since version 3.0
	 */
	public static final String CORNER_RADIUS = "substancelaf.cornerRadius";

	/**
	 * Property name for specifying that the component should be painted flat
	 * (no background / border) when it's inactive. This property should be
	 * specified on a specific component or its parent and must have either
	 * {@link Boolean#TRUE} or {@link Boolean#FALSE} value.
	 * 
	 * <p>
	 * Example how to mark a button to appear flat:
	 * </p>
	 * 
	 * <code>
	 * JButton button = new JButton("text");<br>
	 * button.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * 
	 * @since version 3.0
	 * @see #BUTTON_PAINT_NEVER_PROPERTY
	 */
	public static final String FLAT_PROPERTY = "substancelaf.componentFlat";

	/**
	 * VM property name for specifying the heap status trace file. The trace
	 * file will contain information on the status of heap. The property value
	 * is used as a filename for tracing the heap status. Example for specifying
	 * the trace file name:
	 * 
	 * <p>
	 * <code>
	 * -Dsubstancelaf.heapStatusTraceFile=C:/temp/myApp.heap.log
	 * </code>
	 * </p>
	 * 
	 * @since version 5.0
	 */
	public static final String HEAP_STATUS_TRACE_FILE = "substancelaf.heapStatusTraceFile";

	/**
	 * Client property name for specifying that contents of a frame, dialog,
	 * internal frame, desktop icon or tab have been modified and not saved. The
	 * property can be set on:
	 * <p>
	 * <ul>
	 * <li>{@link JRootPane} - the <b>close</b> button of the title pane of the
	 * matching frame / dialog will be animated (in case that the frame / dialog
	 * have decorated title pane). In case the root pane belongs to a
	 * {@link JInternalFrame} and that frame is iconified (to a
	 * {@link javax.swing.JInternalFrame.JDesktopIcon}), the close button of the its desktop
	 * icon is animated as well.</li>
	 * <li>{@link JComponent} in a {@link JTabbedPane}. Based on the
	 * {@link #TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION} property presence,
	 * either the entire tab or its close button area is animated. In this case,
	 * this property must be set on the tab component itself, <b>not</b> on one
	 * of its child components.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The animation cycles between red, orange and yellow color schemes. In
	 * most cases (all but tabs not marked with
	 * {@link #TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION} property), the
	 * animation will be visible only when the mouse hovers over the close
	 * button of the matching container (frame, dialog, internal frame, desktop
	 * icon, tab). The tooltip of the close button is changed as well to reflect
	 * that the container contents are marked as modified.
	 * </p>
	 * 
	 * <p>
	 * Here is a sample text editing application that illustrates the use of
	 * this property. Once the contents of the text pane are changed, the frame
	 * is marked as modified. The <b>Save</b> button marks the frame as
	 * not-modified. In the real application, the listener on this button will
	 * need to persist the changes as well.
	 * </p>
	 * 
	 * <code>
	 * public class Changer extends JFrame {<br>
	 * &nbsp;&nbsp;public Changer() {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;super("Changer");<br>
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;this.setLayout(new BorderLayout());<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;JTextPane textArea = new JTextPane();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;this.add(textArea, BorderLayout.CENTER);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;textArea.getDocument().addDocumentListener(new
	 * DocumentListener() {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;private void handleChange() {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getRootPane().putClientProperty(<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SubstanceLookAndFeel.WINDOW_MODIFIED,
	 * Boolean.TRUE);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public void
	 * changedUpdate(DocumentEvent e) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;handleChange();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public void
	 * insertUpdate(DocumentEvent e) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;handleChange();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public void
	 * removeUpdate(DocumentEvent e) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;handleChange();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;});<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;JPanel buttons = new JPanel(new
	 * FlowLayout(FlowLayout.RIGHT));<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;JButton saveButton = new JButton("Save");<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;saveButton.addActionListener(new ActionListener() {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public void
	 * actionPerformed(ActionEvent e) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;getRootPane().putClientProperty(<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SubstanceLookAndFeel.WINDOW_MODIFIED,
	 * Boolean.FALSE);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;});<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;buttons.add(saveButton);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;this.add(buttons, BorderLayout.SOUTH);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);<br>
	 * &nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;public static void main(String ... args) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;try {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;UIManager.setLookAndFeel(new
	 * SubstanceLookAndFeel());<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;catch (Exception exc) {}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;JFrame.setDefaultLookAndFeelDecorated(true);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;Changer ch = new Changer();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;ch.setPreferredSize(new Dimension(200, 200));<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;ch.setSize(ch.getPreferredSize());<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;ch.setLocationRelativeTo(null);<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;ch.setVisible(true);<br>
	 * &nbsp;&nbsp;}<br> }
	 * </code>
	 * 
	 * @since version 2.1
	 */
	public final static String WINDOW_MODIFIED = "windowModified";

    /**
     * <p>UIManager property name for specifying that whether any JRootPane with
     * Substance window decorations should be attentive to the active or selected
     * state of the respective JFrame or JInternalFrame. This property can only be
     * specified in the {@link UIManager} for this release. The value should be
     * either {@link Boolean#TRUE} or {@link Boolean#FALSE}.  The initial value
     * depends on the specific skin chosen and will be false if not specified.
     * </p>
     *
     * <p>When active this property will cause the title pane and borders of
     * JFrames and JInternal frames to toggle between {DecorationAreaType#PRIMARY_TITLE_PANE}
     * / {DecorationAreaType#PRIMARY_TITLE_PANE_INACTIVE} or {DecorationAreaType#SECONDARY_TITLE_PANE}
     * / {DecorationAreaType#SECONDARY_TITLE_PANE_INACTIVE} respectively when the
     * respective containers active or selected property changes.  If the Title pane of the
     * window decoration is change to any other DecorationAreaType, then the type will not
     * be toggled.</p>
     *
     * <code>
	 * UIManager.put(SubstanceLookAndFeel.WINDOW_AUTO_DEACTIVATE, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);<br>
     * </code>
     *
     * @since version 6.3
     */
    public final static String WINDOW_AUTO_DEACTIVATE = "windowAutoDeactivate";

    /**
     * <p>VM property name for specifying that whether any JRootPane with
     * Substance window decorations should be drawn with rounded corners in it's
     * decoration frame.  This property is specified globally in  via a system property.
     * The value will be parsed by {Boolean.valueOf}, unless it is unset or an empty
     * string, then it will default to true.  A true value only enables rounded windows,
     * they can be turned off via the UIManager or client properties.  A false value
     * disables <i>all</i> rounded corners and is used to remove artifacts from legacy
     * video cards.  This value is read when the Substance look and feel is initialized,
     * and is not consulted later.
     * </p>
     *
     * <p>When unset or set to true this property will cause the title pane and borders of
     * JFrames and JInternal frames to be rounded.
     *
     * <p>
     * <code>
     * -Dsubstancelaf.windowRoundedCorners=False
     * </code>
     * </p>
     *
     * @since version 7.1
     */
    public final static String WINDOW_ROUNDED_CORNERS_PROPERTY = "substancelaf.windowRoundedCorners";

    /**
     * <p>Client property name for specifying that whether any JRootPane with
     * Substance window decorations should be drawn with rounded corners in it's
     * decoration frame.  This property can be specified per-window or globally in
     * the UIManager or via client properties.  The value should be
     * either {@link Boolean#TRUE} or {@link Boolean#FALSE}.  This value is subject
     * to being globally set to false if the system property is set to false.  When unset
     * it defauls to true.
     * </p>
     *
     * <p>When unset or set to true this property will cause the title pane and borders of
     * JFrames and JInternal frames to be rounded.
     *
     * <code>
     * UIManager.put(SubstanceLookAndFeel.WINDOW_ROUNDED_CORNERS, <br>
     * &nbsp;&nbsp;Boolean.FALSE);<br>
     * // OR <br/>
     * someFrameOrDialog.putClientProperty(SubstanceLookAndFeel.WINDOW_ROUNDED_CORNERS, <br>
     * &nbsp;&nbsp;Boolean.FALSE);<br>
     * // for specific JInternalFrames, JFrames, or JDialogs
     *
     * </code>
     *
     * @since version 7.1
     */
    public final static String WINDOW_ROUNDED_CORNERS = "windowRoundedCorners";

	/**
	 * Client property name for adding close buttons on tabs. This property can
	 * be specified on a single tab component, on a {@link JTabbedPane} itself
	 * (will hold for all tab components that don't define this property) or on
	 * {@link UIManager}. The value should be either {@link Boolean#TRUE} or
	 * {@link Boolean#FALSE}. By default, the close buttons are not displayed.
	 * 
	 * <p>
	 * Example of setting that all tabs in the application will have close
	 * buttons:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * 
	 * <p>
	 * A more complex example:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);<br>
	 * JTabbedPane jtpMain = new JTabbedPane();<br>
	 * JTabbedPane jtpSecondary = new JTabbedPane();<br>
	 * jtpSecondary.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.FALSE);<br>
	 * JPanel panelSecondary = new JPanel();<br>
	 * jtpMain.addTab(jtpSecondary);<br>
	 * jtpMain.addTab(panelSecondary);<br>
	 * JPanel tab1 = new JPanel();<br>
	 * JPanel tab2 = new JPanel();<br>
	 * tab2.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);<br>
	 * jtpSecondary.addTab(tab1);<br>
	 * jtpSecondary.addTab(tab2);
	 * </code>
	 * 
	 * <p>
	 * In the example above, the first first-level child (<b>jtpSecondary</b>)
	 * doesn't have the close button (since it overrides the global setting).
	 * The second first-level child tab (<b>panelSecondary</b>) has close button
	 * (from the global <b>UIManager</b> setting). The first second-level tab
	 * doesn't have the close button (setting inherited from the parent
	 * <b>jtpSecondary</b> tab that overrides the global setting). The second
	 * second-level tab has the close button (since its setting overrides the
	 * parent setting).
	 * </p>
	 * 
	 * @since version 2.1
	 * @see #TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION
	 * @see #TABBED_PANE_CLOSE_CALLBACK
	 */
	public final static String TABBED_PANE_CLOSE_BUTTONS_PROPERTY = "substancelaf.tabbedpanehasclosebuttons";

	/**
	 * Client property name for specifying that only the close button of a
	 * marked-as-modified tab component should pulsate. This property can be
	 * specified on a single tab component, on a {@link JTabbedPane} itself
	 * (will hold for all tab components that don't define this property) or on
	 * {@link UIManager}. The value should be either {@link Boolean#TRUE} or
	 * {@link Boolean#FALSE}. By default, the animation on modified tabs is on
	 * the entire tab rectangle. Note that this setting is only relevant for
	 * tabs marked with {@link #WINDOW_MODIFIED} property.
	 * 
	 * <p>
	 * Example of setting that all tabs in the application will have modified
	 * animation on close button:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);
	 * </code>
	 * 
	 * <p>
	 * A more complex example:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);<br>
	 * JTabbedPane jtpMain = new JTabbedPane();<br>
	 * JTabbedPane jtpSecondary = new JTabbedPane();<br>
	 * jtpSecondary.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION, <br>
	 * &nbsp;&nbsp;Boolean.FALSE);<br>
	 * JPanel panelSecondary = new JPanel();<br>
	 * jtpMain.addTab(jtpSecondary);<br>
	 * jtpMain.addTab(panelSecondary);<br>
	 * JPanel tab1 = new JPanel();<br>
	 * JPanel tab2 = new JPanel();<br>
	 * tab2.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION, <br>
	 * &nbsp;&nbsp;Boolean.TRUE);<br>
	 * jtpSecondary.addTab(tab1);<br>
	 * jtpSecondary.addTab(tab2);
	 * </code>
	 * 
	 * <p>
	 * In the example above, the first first-level child (<b>jtpSecondary</b>)
	 * has the animation on the entire tab (since it overrides the global
	 * setting). The second first-level child tab (<b>panelSecondary</b>) has
	 * animation on the close button (from the global <b>UIManager</b> setting).
	 * The first second-level tab has the animation on the entire tab (setting
	 * inherited from the parent <b>jtpSecondary</b> tab that overrides the
	 * global setting). The second second-level tab has animation on the close
	 * button (since its setting overrides the parent setting).
	 * </p>
	 * 
	 * @since version 2.2
	 * @see #TABBED_PANE_CLOSE_BUTTONS_PROPERTY
	 * @see #TABBED_PANE_CLOSE_CALLBACK
	 */
	public final static String TABBED_PANE_CLOSE_BUTTONS_MODIFIED_ANIMATION = "substancelaf.tabbedpaneclosebuttonsmodifiedanimation";

	/**
	 * Client property name for specifying the callback for deciding on the tab
	 * close type. This property can be specified on a single tab component, on
	 * a {@link JTabbedPane} itself (will hold for all tab components that don't
	 * define this property) or on {@link UIManager}. The value should be an
	 * instance of {@link TabCloseCallback}. Note that this setting is only
	 * relevant for tabs marked with {@link #TABBED_PANE_CLOSE_BUTTONS_PROPERTY}
	 * property.
	 * 
	 * <p>
	 * Example of custom tab close callback set on a tabbed pane:
	 * </p>
	 * <code>
	 * TabCloseCallback closeCallback = new TabCloseCallback() {<br>
	 * &nbsp;&nbsp;public TabCloseKind onAreaClick(JTabbedPane tabbedPane,<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int tabIndex, MouseEvent mouseEvent) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;if (mouseEvent.getButton() != MouseEvent.BUTTON3)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return TabCloseKind.NONE;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;if (mouseEvent.isShiftDown()) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return TabCloseKind.ALL;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;return TabCloseKind.THIS;<br>
	 * &nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;public TabCloseKind onCloseButtonClick(JTabbedPane tabbedPane,<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int tabIndex, MouseEvent mouseEvent) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;if (mouseEvent.isAltDown()) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return TabCloseKind.ALL_BUT_THIS;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;if (mouseEvent.isShiftDown()) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return TabCloseKind.ALL;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;return TabCloseKind.THIS;<br>
	 * &nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;public String getAreaTooltip(JTabbedPane tabbedPane, int tabIndex) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;return null;<br>
	 * &nbsp;&nbsp;}<br>
	 * <br>
	 * &nbsp;&nbsp;public String getCloseButtonTooltip(JTabbedPane tabbedPane,<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int tabIndex) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;StringBuffer result = new StringBuffer();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;result.append("&lt;html&gt;&lt;body&gt;");<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;result.append("Mouse click closes &lt;b&gt;"<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+ tabbedPane.getTitleAt(tabIndex) + "&lt;/b&gt; tab");<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;result.append("&lt;br&gt;&lt;b&gt;Alt&lt;/b&gt;-Mouse click closes all tabs but &lt;b&gt;"<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+ tabbedPane.getTitleAt(tabIndex) + "&lt;/b&gt; tab");<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;result.append("&lt;br&gt;&lt;b&gt;Shift&lt;/b&gt;-Mouse click closes all tabs");<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;result.append("&lt;/body&gt;&lt;/html&gt;");<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;return result.toString();<br>
	 * &nbsp;&nbsp;}<br>
	 * };<br>
	 * 
	 * JTabbedPane jtp = new JTabbedPane();<br>
	 * jtp.putClientProperty(<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;SubstanceLookAndFeel.TABBED_PANE_CLOSE_CALLBACK, <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;closeCallback);
	 * </code>
	 * 
	 * @since version 2.3
	 */
	public final static String TABBED_PANE_CLOSE_CALLBACK = "substancelaf.tabbedpanecloseCallback";

	/**
	 * Client property name for specifying the content pane border kind. This
	 * property can be specified either on a single {@link JTabbedPane} or on
	 * {@link UIManager}. The value should be one of
	 * {@link SubstanceConstants.TabContentPaneBorderKind} enum. By default, the
	 * border kind is
	 * {@link SubstanceConstants.TabContentPaneBorderKind#DOUBLE_FULL}.
	 * 
	 * <p>
	 * Example of setting that all tabbed panes in the application have single
	 * full border (default setting prior to version 4.1):
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, <br>
	 * &nbsp;&nbsp;TabContentPaneBorderKind.SINGLE_FULL);
	 * </code>
	 * 
	 * <p>
	 * Example of specifying that the specific tabbed pane has single full
	 * border (default setting prior to version 4.1):
	 * </p>
	 * <code>
	 * JTabbedPane jtpMain = new JTabbedPane();<br>
	 * jtpMain.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, <br>
	 * &nbsp;&nbsp;TabContentPaneBorderKind.SINGLE_FULL);
	 * </code>
	 * 
	 * @since version 4.1
	 */
	public final static String TABBED_PANE_CONTENT_BORDER_KIND = "substancelaf.tabbedPaneContentBorderKind";

	/**
	 * Client property name for specifying combo popup flyout orientation. This
	 * property can be set on either a specific {@link JComboBox} or globally on
	 * {@link UIManager}. The value should be one of the {@link Integer}s below:
	 * 
	 * <p>
	 * <ul>
	 * <li>The default {@link SwingConstants#SOUTH} - the popup is displayed
	 * directly below the combo aligned to the left.
	 * <li>{@link SwingConstants#NORTH} - the popup is displayed directly above
	 * the combo aligned to the left.
	 * <li>{@link SwingConstants#EAST} - the popup is displayed to the left of
	 * the combo aligned to the top.
	 * <li>{@link SwingConstants#WEST} - the popup is displayed to the right of
	 * the combo aligned to the top.
	 * <li>{@link SwingConstants#CENTER} - the popup is displayed centered
	 * vertically over the combo aligned to the left.
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * Note that the combo arrow changes in accordance with the combo popup
	 * flyout orientation. Example of setting a combobox with a custom flyout
	 * orientation:
	 * </p>
	 * <code>
	 * JComboBox cb = new JComboBox(<br>
	 * &nbsp;&nbsp;new Object[] { "Ester", "Jordi", "Jordina", "Jorge", "Sergi" });<br>
	 * cb.putClientProperty(SubstanceLookAndFeel.COMBO_BOX_POPUP_FLYOUT_ORIENTATION, <br>
	 * &nbsp;&nbsp;SwingConstants.CENTER);
	 * </code>
	 * 
	 * @since version 2.3
	 * @see #COMBO_POPUP_PROTOTYPE
	 */
	public final static String COMBO_BOX_POPUP_FLYOUT_ORIENTATION = "substancelaf.comboboxpopupFlyoutOrientation";

	/**
	 * Client property name for specifying scroll pane button policy. This
	 * property can be set on either a specific {@link JScrollPane} or globally
	 * on {@link UIManager}. The value should be one of the
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind} enum. Example of
	 * setting a scroll pane with a custom button policy:
	 * 
	 * <p>
	 * <code>
	 * JScrollPane jsp = new JScrollPane(new JPanel());<br>
	 * jsp.putClientProperty(SubstanceLookAndFeel.SCROLL_PANE_BUTTONS_POLICY,<br>
	 * &nbsp;&nbsp;ScrollPaneButtonPolicyKind.MULTIPLE);
	 * </code>
	 * 
	 * @since version 3.1
	 */
	public final static String SCROLL_PANE_BUTTONS_POLICY = "substancelaf.scrollPaneButtonsPolicy";

	/**
	 * Property name for specifying that extra UI elements (such as menu items
	 * in system menu or lock borders) should be shown. This property can be set
	 * as a global setting on {@link UIManager} or as a client property on a
	 * specific component. The value should be either {@link Boolean#TRUE} or
	 * {@link Boolean#FALSE}.
	 * 
	 * <p>
	 * Example of setting this property on {@link UIManager}:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
	 * SwingUtilities.updateComponentTree(myFrame);
	 * </code>
	 * 
	 * @since version 5.0
	 */
	public final static String SHOW_EXTRA_WIDGETS = "substancelaf.addWidgets";

	/**
	 * Property name for specifying menu gutter fill kind. Menu gutter is the
	 * part of the menu where checkmarks and icons are painted. The value should
	 * be one of {@link MenuGutterFillKind} enum. This property can be set
	 * globally on the {@link UIManager}. The default value is
	 * {@link MenuGutterFillKind#HARD}.
	 * 
	 * <p>
	 * Example of setting soft fill kind:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.MENU_GUTTER_FILL_KIND, MenuGutterFillKind.SOFT);
	 * </code>
	 * 
	 * @since version 3.2
	 */
	public final static String MENU_GUTTER_FILL_KIND = "substancelaf.menuGutterFillKind";

	/**
	 * Client property name for specifying the kind of focus indication on
	 * buttons, check boxes and radio buttons. The value should be one of
	 * {@link SubstanceConstants.FocusKind} enum. This property can be set
	 * either on the specific component or as global property on
	 * {@link UIManager}.
	 * 
	 * <p>
	 * In order to compute the kind of focus indication for some component, the
	 * component's hierarchy is traversed bottom up. The first component that
	 * has this property set, defines the focus indication kind. If neither
	 * component nor its ancestors define this property, the global setting on
	 * {@link UIManager} is checked. If there is no global setting, the default
	 * {@link SubstanceConstants.FocusKind#ALL_INNER} is used. Here is an
	 * example to illustrate the above:
	 * </p>
	 * 
	 * <code>
	 * &nbsp;&nbsp;JPanel topPanel = new JPanel();<br>
	 * &nbsp;&nbsp;topPanel.putClientProperty(SubstanceLookAndFeel.FOCUS_KIND, FocusKind.UNDERLINE);<br>
	 * &nbsp;&nbsp;JPanel panel1 = new JPanel();<br>
	 * &nbsp;&nbsp;JButton b1 = new JButton("button1");<br>
	 * &nbsp;&nbsp;b1.putClientProperty(SubstanceLookAndFeel.FOCUS_KIND, FocusKind.TEXT);<br>
	 * &nbsp;&nbsp;JButton b2 = new JButton("button2");<br>
	 * &nbsp;&nbsp;JButton b3 = new JButton("button3");<br>
	 * &nbsp;&nbsp;b3.putClientProperty(SubstanceLookAndFeel.FOCUS_KIND, FocusKind.ALL_INNER);<br>
	 * &nbsp;&nbsp;panel1.add(b1);<br>
	 * &nbsp;&nbsp;panel1.add(b2);<br>
	 * &nbsp;&nbsp;topPanel.add(panel1);<br>
	 * &nbsp;&nbsp;topPanel.add(b3);<br>
	 * </code>
	 * 
	 * <p>
	 * In the code above:
	 * </p>
	 * <ul>
	 * <li>Button <b>b1</b> will have {@link SubstanceConstants.FocusKind#NONE}
	 * focus kind which is set directly on the button.
	 * <li>Button <b>b2</b> will have
	 * {@link SubstanceConstants.FocusKind#UNDERLINE} focus kind which is
	 * inherited from its <b>topPanel</b> parent.
	 * <li>Button <b>b3</b> will have
	 * {@link SubstanceConstants.FocusKind#ALL_INNER} focus kind which is set
	 * directly on the button.
	 * </ul>
	 * 
	 * @since 2.2
	 * @see SubstanceConstants.FocusKind
	 */
	public final static String FOCUS_KIND = "substancelaf.focusKind";

	/**
	 * Property name for specifying the combobox popup prototype display value
	 * which is used to compute the width of the popup at runtime. The property
	 * value should be one of:
	 * 
	 * <p>
	 * <ul>
	 * <li>{@link ComboPopupPrototypeCallback} - will provide
	 * application-specific logic at runtime.
	 * <li>{@link Object} - will point to the prototype entry itself.
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * This property can be set either on a specific {@link JComboBox} or
	 * globally on {@link UIManager}.
	 * </p>
	 * 
	 * <p>
	 * Here is an example of combo popup prototype set to a model element:
	 * </p>
	 * <code>
	 * 	JComboBox comboProto1 = new JComboBox(new Object[] { "aa", "aaaaa",<br>
	 * 	&nbsp;&nbsp;"aaaaaaaaaa", "this one is the one", "aaaaaaaaaaaaaaaaaaaaa" });<br>
	 * 	comboProto1.setPrototypeDisplayValue("aaaaa");<br>
	 * 	comboProto1.putClientProperty(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE,<br>
	 * 	&nbsp;&nbsp;"this one is the one");
	 * </code>
	 * 
	 * <p>
	 * Here is an example of combo popup prototype set to a dynamic callback.
	 * This callback always returns the last model element:
	 * </p>
	 * <code>
	 *  JComboBox comboProto3 = new JComboBox(new Object[] { "aa", "aaaaa",<br>
	 * 	&nbsp;&nbsp;"this is not", "this one is not it",<br>
	 * 	&nbsp;&nbsp;"this one is it that is for the popup" });<br>
	 * 	comboProto3.setPrototypeDisplayValue("aaaaa");<br>
	 * 	comboProto3.putClientProperty(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE,<br>
	 * 	&nbsp;&nbsp;new ComboPopupPrototypeCallback() {<br>
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;public Object getPopupPrototypeDisplayValue(JComboBox jc) {<br>
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return jc.getModel().getElementAt(<br>
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;jc.getModel().getSize() - 1);<br>
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * 	&nbsp;&nbsp;});
	 *  </code>
	 * 
	 * @since version 3.0
	 * @see #COMBO_BOX_POPUP_FLYOUT_ORIENTATION
	 */
	public final static String COMBO_POPUP_PROTOTYPE = "substancelaf.comboPopupPrototype";

	/**
	 * VM property name for specifying the trace file. The trace file will
	 * contain output of the memory analyser which can be used to pinpoint the
	 * memory leaks. The property value is used as a filename for tracing the
	 * memory allocations. Example for specifying the trace file name:
	 * 
	 * <p>
	 * <code>
	 * -Dsubstancelaf.traceFile=C:/temp/myApp.substance.log
	 * </code>
	 * </p>
	 * 
	 * @since version 2.0
	 */
	public final static String TRACE_FILE = "substancelaf.traceFile";

	/**
	 * Client property name for specifying the number of echo characters for
	 * each password character. The value should be an instance of
	 * {@link Integer}, otherwise will be ignored. This property can be set
	 * either on a specific {@link JPasswordField} or globally on
	 * {@link UIManager}.
	 * 
	 * <p>
	 * Example of having all password fields echo 3 characters per each typed
	 * user character:
	 * </p>
	 * <code>
	 * UIManager.put(SubstanceLookAndFeel.PASSWORD_ECHO_PER_CHAR, <br>
	 * &nbsp;&nbsp;new Integer(3));
	 * </code>
	 * 
	 * <p>
	 * Example of having a specific password field echo 2 characters per each
	 * typed user character:
	 * </p>
	 * <code>
	 * JPasswordField jpf = new JPasswordField();<br>
	 * jpf.putClientProperty(SubstanceLookAndFeel.PASSWORD_ECHO_PER_CHAR, <br>
	 * &nbsp;&nbsp;new Integer(2));
	 * </code>
	 * 
	 * @since version 2.2
	 */
	public final static String PASSWORD_ECHO_PER_CHAR = "substancelaf.passwordEchoPerChar";

	/**
	 * <p>
	 * Client property name for specifying that icons on controls such as
	 * buttons, toggle buttons, labels, tabs and menu items should match the
	 * color of the current color scheme when they are in default state. The
	 * control is in default state when it's not pressed, not selected, not
	 * armed and not rolled over. The value should be an instance of
	 * {@link Boolean}. By default, all controls show regular (full-color
	 * original) icons. The value can be set globally on {@link UIManager}.
	 * </p>
	 * 
	 * @since version 3.3
	 */
	public final static String USE_THEMED_DEFAULT_ICONS = "substancelaf.useThemedDefaultIcons";

	/**
	 * <p>
	 * Client property name for specifying the colorization amount applied to
	 * the background and foreground of the current color scheme and the
	 * specific control. By default, when the application does not use any
	 * custom colors, all the controls are painted with the colors of the
	 * current color scheme / skin. The colors coming from the look-and-feel
	 * implement the marker {@link UIResource} interface which allows the UI
	 * delegates to differentiate between application-specific colors which are
	 * not changed, and the LAF-provide colors that are changed on LAF switch.
	 * </p>
	 * 
	 * <p>
	 * This new client property installs the "smart colorization" mode which
	 * uses the colors of the current color scheme and the custom background /
	 * foreground colors (when installed by application) to colorize the
	 * relevant portions of the control. For example, on checkbox the custom
	 * background color will be used to colorize the check box itself, while the
	 * custom foreground color will be applied to the check box text and the
	 * check mark.
	 * </p>
	 * 
	 * <p>
	 * The value of this property specifies the actual colorization amount.
	 * Value of 0.0 results in Substance completely <strong>ignoring</strong>
	 * the custom application background and foreground colors set on the
	 * components - no colorization. Values closer to 1.0 result in almost full
	 * usage of the custom application background and foreground colors set on
	 * the components. Note that in order to maintain the gradients (fill,
	 * border, etc), even value of 1.0 does not result in full custom color
	 * being applied to the relevant visuals of the control.
	 * </p>
	 * 
	 * <p>
	 * This property can be specified globally on {@link UIManager}, applying on
	 * all controls, or on the specific component / container. In the later
	 * case, the value will be applied to the component / container itself and
	 * all its children that do not specify a custom value for this property.
	 * </p>
	 * 
	 * <p>
	 * The default colorization amount (when this property is not set at all) is
	 * 0.5. This means that applications that install custom background /
	 * foreground colors on their UI controls will see them colorized with 50%
	 * "strength", even without setting this property.
	 * </p>
	 * 
	 * <p>
	 * The value should be an instance of {@link Double} in 0.0-1.0 range.
	 * </p>
	 * 
	 * <p>
	 * Example of marking a button to have a custom background color and
	 * colorizing it with 40%:
	 * </p>
	 * <code>
	 * JButton jb = new JButton("sample", myIcon);<br>
	 * jb.setBackground(Color.red);<br>
	 * jb.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, <br>
	 * &nbsp;&nbsp;new Double(0.4));
	 * </code>
	 * 
	 * <p>
	 * Note that components in decoration areas registered on the current skin
	 * will ignore the colorization on custom background color. The background
	 * of such components is always painted by the skin's decoration painter to
	 * ensure consistent background painting of the relevant decoration area.
	 * </p>
	 * 
	 * @since version 4.2
	 * @see Component#setBackground(Color)
	 * @see Component#setForeground(Color)
	 */
	public final static String COLORIZATION_FACTOR = "substancelaf.colorizationFactor";

	/**
	 * Internal client property name for storing application-specific font
	 * policy.
	 * 
	 * @since version 3.3
	 * @see #setFontPolicy(FontPolicy)
	 * @see #getFontPolicy()
	 */
	protected final static String SUBSTANCE_FONT_POLICY_KEY = "substancelaf.fontPolicyKey";

	/**
	 * Internal client property name for storing application-specific input map
	 * set.
	 * 
	 * @since version 6.1
	 * @see #setInputMapSet(InputMapSet)
	 * @see #getInputMapSet()
	 */
	protected final static String SUBSTANCE_INPUT_MAP_SET_KEY = "substancelaf.inputMapSetKey";

	/**
	 * Property name for specifying outline shaper. This property is used a
	 * client property that can be set on a specific control.
	 * 
	 * <p>
	 * The value must be a {@link SubstanceButtonShaper} object.
	 * </p>
	 * 
	 * <p>
	 * Example of using a {@link SubstanceButtonShaper} object as client
	 * property value:
	 * </p>
	 * <code>
	 * JButton b = new JButton("text");<br>
	 * b.putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, <br>
	 * &nbsp;&nbsp;new ClassicButtonShaper());
	 * </code>
	 * 
	 * @since version 2.1
	 */
	public static final String BUTTON_SHAPER_PROPERTY = "substancelaf.buttonShaper";

	/**
	 * Property name for specifying a skin to be used on the specific root pane.
	 * This property can only be installed on a {@link JRootPane} and will
	 * affect all the controls in that root pane. The value must be an instance
	 * of {@link SubstanceSkin}. After setting this property, call
	 * {@link SwingUtilities#updateComponentTreeUI(Component)} on the matching
	 * window.
	 * 
	 * @since version 5.0
	 * @see #getCurrentSkin(Component)
	 */
	public static final String SKIN_PROPERTY = "substancelaf.skin";

	/**
	 * Resource bundle for <b>Substance</b> labels.
	 */
	private static ResourceBundle LABEL_BUNDLE = null;

	/**
	 * Class loader for {@link #LABEL_BUNDLE}.
	 */
	private static ClassLoader labelBundleClassLoader;

	/**
	 * The skin of this look-and-feel instance.
	 */
	protected SubstanceSkin skin;

	/**
	 * The name of this look-and-feel instance.
	 */
	protected String name;

	/**
	 * Creates a new skin-based Substance look-and-feel. This is the only way to
	 * create an instance of {@link SubstanceLookAndFeel} class.
	 * 
	 * @param skin
	 *            Skin.
	 */
	protected SubstanceLookAndFeel(SubstanceSkin skin) {
		this.skin = skin;
		this.name = "Substance " + skin.getDisplayName();

		initPluginsIfNecessary();
	}

	/**
	 * Initializes the plugins if necessary.
	 */
	protected static void initPluginsIfNecessary() {
		if (SubstanceLookAndFeel.skinPlugins != null)
			return;
		SubstanceLookAndFeel.skinPlugins = new PluginManager(
				SubstanceLookAndFeel.PLUGIN_XML, LafPlugin.TAG_MAIN,
				SubstanceSkinPlugin.TAG_SKIN_PLUGIN_CLASS);
		SubstanceLookAndFeel.componentPlugins = new ComponentPluginManager(
				SubstanceLookAndFeel.PLUGIN_XML);
	}

	/**
	 * Retrieves the current label bundle.
	 * 
	 * @return The current label bundle.
	 * @see #resetLabelBundle()
	 */
	public static synchronized ResourceBundle getLabelBundle() {
		if (SubstanceLookAndFeel.LABEL_BUNDLE == null) {
			// fix for RFE 157 (allowing custom class loader for
			// resource bundles which can remove server calls
			// in applets)
			if (SubstanceLookAndFeel.labelBundleClassLoader == null) {
				SubstanceLookAndFeel.LABEL_BUNDLE = ResourceBundle
						.getBundle(
								"org.pushingpixels.substance.internal.resources.Labels",
								Locale.getDefault());
			} else {
				SubstanceLookAndFeel.LABEL_BUNDLE = ResourceBundle
						.getBundle(
								"org.pushingpixels.substance.internal.resources.Labels",
								Locale.getDefault(),
								SubstanceLookAndFeel.labelBundleClassLoader);
			}
			for (LocaleChangeListener lcl : SubstanceLookAndFeel.localeChangeListeners)
				lcl.localeChanged();
		}
		return SubstanceLookAndFeel.LABEL_BUNDLE;
	}

	/**
	 * Retrieves the label bundle for the specified locale.
	 * 
	 * @param locale
	 *            Locale.
	 * @return The label bundle for the specified locale.
	 */
	public static synchronized ResourceBundle getLabelBundle(Locale locale) {
		// fix for RFE 157 (allowing custom class loader for
		// resource bundles which can remove server calls
		// in applets)
		if (SubstanceLookAndFeel.labelBundleClassLoader == null) {
			return ResourceBundle.getBundle(
					"org.pushingpixels.substance.internal.resources.Labels",
					locale);
		} else {
			return ResourceBundle.getBundle(
					"org.pushingpixels.substance.internal.resources.Labels",
					locale, SubstanceLookAndFeel.labelBundleClassLoader);
		}
	}

	/**
	 * Resets the current label bundle. Useful when the application changes
	 * Locale at runtime.
	 * 
	 * @see #getLabelBundle()
	 */
	public static synchronized void resetLabelBundle() {
		SubstanceLookAndFeel.LABEL_BUNDLE = null;
		LafWidgetRepository.resetLabelBundle();
	}

	/**
	 * Returns the current global skin. If the current look-and-feel is not
	 * Substance, this method returns <code>null</code>. This method is for
	 * internal use only. Applications should use the
	 * {@link #getCurrentSkin(Component)}.
	 * 
	 * @return Current global skin.
	 * @see #getCurrentSkin(Component)
	 */
	public static SubstanceSkin getCurrentSkin() {
		LookAndFeel current = UIManager.getLookAndFeel();
		if (current instanceof SubstanceLookAndFeel) {
			return currentSkin;
		}
		return null;
	}

	/**
	 * Returns the current skin for the specified component. If the current
	 * look-and-feel is not Substance, this method returns <code>null</code>.
	 * 
	 * @param c
	 *            Component. May be <code>null</code> - in this case the global
	 *            current Substance skin will be returned.
	 * @return Current skin for the specified component.
	 * @see #SKIN_PROPERTY
	 * @see #getCurrentSkin()
	 */
	public static SubstanceSkin getCurrentSkin(Component c) {
		return SubstanceCoreUtilities.getSkin(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Substance Look and Feel by Kirill Grouchnikov";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#getID()
	 */
	@Override
	public String getID() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#isNativeLookAndFeel()
	 */
	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#isSupportedLookAndFeel()
	 */
	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicLookAndFeel#initClassDefaults(javax.swing
	 * .UIDefaults)
	 */
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);

		String UI_CLASSNAME_PREFIX = "org.pushingpixels.substance.internal.ui.Substance";
		Object[] uiDefaults = {

		"ButtonUI", UI_CLASSNAME_PREFIX + "ButtonUI",

		"CheckBoxUI", UI_CLASSNAME_PREFIX + "CheckBoxUI",

		"ComboBoxUI", UI_CLASSNAME_PREFIX + "ComboBoxUI",

		"CheckBoxMenuItemUI", UI_CLASSNAME_PREFIX + "CheckBoxMenuItemUI",

		"DesktopIconUI", UI_CLASSNAME_PREFIX + "DesktopIconUI",

		"DesktopPaneUI", UI_CLASSNAME_PREFIX + "DesktopPaneUI",

		"EditorPaneUI",
				UI_CLASSNAME_PREFIX + "EditorPaneUI",

				"FileChooserUI",
				UI_CLASSNAME_PREFIX + "FileChooserUI",

				// "FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI",

				"FormattedTextFieldUI",
				UI_CLASSNAME_PREFIX + "FormattedTextFieldUI",

				"InternalFrameUI", UI_CLASSNAME_PREFIX + "InternalFrameUI",

				"LabelUI", UI_CLASSNAME_PREFIX + "LabelUI",

				"ListUI", UI_CLASSNAME_PREFIX + "ListUI",

				"MenuUI", UI_CLASSNAME_PREFIX + "MenuUI",

				"MenuBarUI", UI_CLASSNAME_PREFIX + "MenuBarUI",

				"MenuItemUI", UI_CLASSNAME_PREFIX + "MenuItemUI",

				"OptionPaneUI", UI_CLASSNAME_PREFIX + "OptionPaneUI",

				"PanelUI", UI_CLASSNAME_PREFIX + "PanelUI",

				"PasswordFieldUI", UI_CLASSNAME_PREFIX + "PasswordFieldUI",

				"PopupMenuUI", UI_CLASSNAME_PREFIX + "PopupMenuUI",

				"PopupMenuSeparatorUI",
				UI_CLASSNAME_PREFIX + "PopupMenuSeparatorUI",

				"ProgressBarUI", UI_CLASSNAME_PREFIX + "ProgressBarUI",

				"RadioButtonUI", UI_CLASSNAME_PREFIX + "RadioButtonUI",

				"RadioButtonMenuItemUI",
				UI_CLASSNAME_PREFIX + "RadioButtonMenuItemUI",

				"RootPaneUI", UI_CLASSNAME_PREFIX + "RootPaneUI",

				"ScrollBarUI", UI_CLASSNAME_PREFIX + "ScrollBarUI",

				"ScrollPaneUI", UI_CLASSNAME_PREFIX + "ScrollPaneUI",

				"SeparatorUI", UI_CLASSNAME_PREFIX + "SeparatorUI",

				"SliderUI", UI_CLASSNAME_PREFIX + "SliderUI",

				"SpinnerUI", UI_CLASSNAME_PREFIX + "SpinnerUI",

				"SplitPaneUI", UI_CLASSNAME_PREFIX + "SplitPaneUI",

				"TabbedPaneUI", UI_CLASSNAME_PREFIX + "TabbedPaneUI",

				"TableUI", UI_CLASSNAME_PREFIX + "TableUI",

				"TableHeaderUI", UI_CLASSNAME_PREFIX + "TableHeaderUI",

				"TextAreaUI", UI_CLASSNAME_PREFIX + "TextAreaUI",

				"TextFieldUI", UI_CLASSNAME_PREFIX + "TextFieldUI",

				"TextPaneUI", UI_CLASSNAME_PREFIX + "TextPaneUI",

				"ToggleButtonUI", UI_CLASSNAME_PREFIX + "ToggleButtonUI",

				"ToolBarUI", UI_CLASSNAME_PREFIX + "ToolBarUI",

				"ToolBarSeparatorUI",
				UI_CLASSNAME_PREFIX + "ToolBarSeparatorUI",

				"ToolTipUI", UI_CLASSNAME_PREFIX + "ToolTipUI",

				"TreeUI", UI_CLASSNAME_PREFIX + "TreeUI",

				"ViewportUI", UI_CLASSNAME_PREFIX + "ViewportUI",

		};
		table.putDefaults(uiDefaults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicLookAndFeel#initComponentDefaults(javax.swing
	 * .UIDefaults)
	 */
	@Override
	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults(table);

		initFontDefaults(table);
		this.skin.addCustomEntriesToTable(table);
	}

	/**
	 * Sets the {@link FontPolicy} to be used with Substance family. If the
	 * specified policy is <code>null</code>, the default will be reset. This
	 * method does not require Substance to be the current look-and-feel, and
	 * will cause Substance to be set as the current application look-and-feel.
	 * 
	 * @param fontPolicy
	 *            The {@link FontPolicy} to be used with Substance family, or
	 *            <code>null</code> to reset to the default
	 * 
	 * @see #getFontPolicy()
	 * @see SubstanceLookAndFeel#SUBSTANCE_FONT_POLICY_KEY
	 */
	public static void setFontPolicy(FontPolicy fontPolicy) {
		UIManager.put(SUBSTANCE_FONT_POLICY_KEY, fontPolicy);
		SubstanceSizeUtils.setControlFontSize(-1);
		SubstanceSizeUtils.resetPointsToPixelsRatio(fontPolicy);
		SubstanceLookAndFeel.setSkin(SubstanceLookAndFeel.getCurrentSkin());
	}

	/**
	 * Looks up and retrieves the {@link FontPolicy} used by the Substance
	 * family. If a {@link FontPolicy} has been set, it'll be returned.
	 * Otherwise, this method checks if a {@link FontPolicy} or {@link FontSet}
	 * is defined in the system properties or UIDefaults. If so, it is returned.
	 * If no {@link FontPolicy} has been set for this look, in the system
	 * properties or {@link UIDefaults}, the default Substance font policy will
	 * be returned.
	 * 
	 * @return the {@link FontPolicy} set for this Look&amp;feel - if any, the
	 *         {@link FontPolicy} specified in the system properties or
	 *         {@link UIDefaults} - if any, or the default Substance font
	 *         policy.
	 * 
	 * @see #setFontPolicy(org.pushingpixels.substance.api.fonts.FontPolicy)
	 * @see FontPolicies
	 * @see FontPolicies#customSettingsPolicy(FontPolicy)
	 */
	public static FontPolicy getFontPolicy() {
		FontPolicy policy = (FontPolicy) UIManager
				.get(SUBSTANCE_FONT_POLICY_KEY);
		if (policy != null)
			return policy;

		// return default policy
		return SubstanceFontUtilities.getDefaultFontPolicy();
	}

	/**
	 * Sets the {@link InputMapSet} to be used with Substance family. If the
	 * specified set is <code>null</code>, the default will be reset. This
	 * method does not require Substance to be the current look-and-feel, and
	 * will cause Substance to be set as the current application look-and-feel.
	 * 
	 * @param inputMapSet
	 *            The {@link InputMapSet} to be used with Substance family, or
	 *            <code>null</code> to reset to the default
	 * 
	 * @see #getInputMapSet()
	 * @see SubstanceLookAndFeel#SUBSTANCE_INPUT_MAP_SET_KEY
	 */
	public static void setInputMapSet(InputMapSet inputMapSet) {
		UIManager.put(SUBSTANCE_INPUT_MAP_SET_KEY, inputMapSet);
		SubstanceLookAndFeel.setSkin(SubstanceLookAndFeel.getCurrentSkin());
	}

	/**
	 * Looks up and retrieves the {@link InputMapSet} used by the Substance
	 * family. If a {@link InputMapSet} has been set, it'll be returned. If no
	 * {@link InputMapSet} has been set for this look, the default Substance
	 * input map set will be returned.
	 * 
	 * @return the {@link InputMapSet} set for this Look&amp;feel - if any, or
	 *         the default Substance input map set.
	 * 
	 * @see #setInputMapSet(InputMapSet)
	 */
	public static InputMapSet getInputMapSet() {
		InputMapSet inputMapSet = (InputMapSet) UIManager
				.get(SUBSTANCE_INPUT_MAP_SET_KEY);
		if (inputMapSet != null)
			return inputMapSet;

		// return system input map set
		return SubstanceInputMapUtilities.getSystemInputMapSet();
	}

	/**
	 * Looks up the correct control font and sets it for all controls.
	 * 
	 * @param table
	 *            The UI defaults table.
	 */
	private void initFontDefaults(UIDefaults table) {
		FontSet substanceFontSet = getFontPolicy()
				.getFontSet("Substance", null);
		initFontDefaults(table, substanceFontSet);
	}

	/**
	 * Sets Fonts in the given FontSet as defaults for all known component types
	 * in the given UIDefaults table.
	 * 
	 * @param table
	 *            the UIDefaults table used to set fonts
	 * @param fontSet
	 *            describes the set of Fonts to be installed
	 */
	private static void initFontDefaults(UIDefaults table, FontSet fontSet) {
		Font controlFont = fontSet.getControlFont();
		Font menuFont = fontSet.getMenuFont();
		Font messageFont = fontSet.getMessageFont();
		Font toolTipFont = fontSet.getSmallFont();
		Font titleFont = fontSet.getTitleFont();
		Font windowFont = fontSet.getWindowTitleFont();

		// System.out.println("Control: " + fontSet.getControlFont());
		// System.out.println("Menu: " + fontSet.getMenuFont());
		// System.out.println("Message: " + fontSet.getMessageFont());
		// System.out.println("Small: " + fontSet.getSmallFont());
		// System.out.println("Title: " + fontSet.getTitleFont());
		// System.out.println("Window title: " + fontSet.getWindowTitleFont());

		Object[] defaults = {

		"Button.font", controlFont,

		"CheckBox.font", controlFont,

		"ColorChooser.font", controlFont,

		"ComboBox.font", controlFont,

		"EditorPane.font", controlFont,

		"FormattedTextField.font", controlFont,

		"Label.font", controlFont,

		"List.font", controlFont,

		"Panel.font", controlFont,

		"PasswordField.font", controlFont,

		"ProgressBar.font", controlFont,

		"RadioButton.font", controlFont,

		"ScrollPane.font", controlFont,

		"Spinner.font", controlFont,

		"TabbedPane.font", controlFont,

		"Table.font", controlFont,

		"TableHeader.font", controlFont,

		"TextArea.font", controlFont,

		"TextField.font", controlFont,

		"TextPane.font", controlFont,

		"ToolBar.font", controlFont,

		"ToggleButton.font", controlFont,

		"Tree.font", controlFont,

		"Viewport.font", controlFont,

		"InternalFrame.titleFont", windowFont,

		"DesktopIcon.titleFont", windowFont,

		"OptionPane.font", messageFont,

		"OptionPane.messageFont", messageFont,

		"OptionPane.buttonFont", messageFont,

		"TitledBorder.font", titleFont,

		"ToolTip.font", toolTipFont,

		"CheckBoxMenuItem.font", menuFont,

		"CheckBoxMenuItem.acceleratorFont", menuFont,

		"Menu.font", menuFont,

		"Menu.acceleratorFont", menuFont,

		"MenuBar.font", menuFont,

		"MenuItem.font", menuFont,

		"MenuItem.acceleratorFont", menuFont,

		"PopupMenu.font", menuFont,

		"RadioButtonMenuItem.font", menuFont,

		"RadioButtonMenuItem.acceleratorFont", menuFont,
		// ?
		};
		table.putDefaults(defaults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicLookAndFeel#getDefaults()
	 */
	@Override
	public UIDefaults getDefaults() {
		UIDefaults table = super.getDefaults();

		SubstanceLookAndFeel.componentPlugins.processAllDefaultsEntries(table,
				this.skin);
		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicLookAndFeel#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		ShadowPopupFactory.install();

		setSkin(this.skin, false);

		// tracer for memory analysis
		String paramTraceFile = SubstanceCoreUtilities
				.getVmParameter(SubstanceLookAndFeel.TRACE_FILE);
		if (paramTraceFile != null) {
			MemoryAnalyzer.commence(1000, paramTraceFile);
			for (Object plugin : SubstanceLookAndFeel.componentPlugins
					.getAvailablePlugins(true))
				MemoryAnalyzer.enqueueUsage("Has plugin '"
						+ plugin.getClass().getName() + "'");
		}

		// to show heap status panel in title pane?
		String heapStatusPanelParam = SubstanceCoreUtilities
				.getVmParameter(SubstanceLookAndFeel.HEAP_STATUS_TRACE_FILE);
		SubstanceTitlePane.setHeapStatusLogfileName(heapStatusPanelParam);

		// initialize component plugins
		SubstanceLookAndFeel.componentPlugins.initializeAll();

		// initialize widget support
		LafWidgetRepository.getRepository().setLafSupport(
				new SubstanceWidgetSupport());

		// fix for defect 208 - tracking changes to focus owner
		// and repainting the default button
		this.focusOwnerChangeListener = new PropertyChangeListener() {
			@Override
            public void propertyChange(PropertyChangeEvent evt) {
				if ("focusOwner".equals(evt.getPropertyName())) {
					Component newFocusOwner = (Component) evt.getNewValue();
					if (newFocusOwner != null) {
						JRootPane rootPane = SwingUtilities
								.getRootPane(newFocusOwner);
						if (rootPane == null)
							return;
						JButton defaultButton = rootPane.getDefaultButton();
						if (defaultButton == null)
							return;
						defaultButton.repaint();
					}
				}
				if ("managingFocus".equals(evt.getPropertyName())) {
					if (Boolean.FALSE.equals(evt.getNewValue())) {
						// new keyboard focus manager has been installed
						currentKeyboardFocusManager
								.removePropertyChangeListener(focusOwnerChangeListener);
						currentKeyboardFocusManager = KeyboardFocusManager
								.getCurrentKeyboardFocusManager();
						currentKeyboardFocusManager
								.addPropertyChangeListener(focusOwnerChangeListener);
					}
				}
			}
		};
		this.currentKeyboardFocusManager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		this.currentKeyboardFocusManager
				.addPropertyChangeListener(this.focusOwnerChangeListener);
		if (!LookUtils.IS_OS_WINDOWS
				|| System.getProperty("java.version").compareTo("1.6.0_10") < 0) {
			UIManager.put(WINDOW_ROUNDED_CORNERS, Boolean.FALSE);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicLookAndFeel#uninitialize()
	 */
	@Override
	public void uninitialize() {
		super.uninitialize();

		SubstanceLookAndFeel.currentSkin = null;

		ShadowPopupFactory.uninstall();

		SubstanceCoreUtilities.stopThreads();

		// fix for defect 109 - memory leak on watermarks
		if (this.skin.getWatermark() != null)
			this.skin.getWatermark().dispose();

		// uninitialize component plugins
		SubstanceLookAndFeel.componentPlugins.uninitializeAll();

		// reset widget support
		LafWidgetRepository.getRepository().unsetLafSupport();

		// clear caches
		LazyResettableHashMap.reset();

		this.currentKeyboardFocusManager
				.removePropertyChangeListener(this.focusOwnerChangeListener);
		this.focusOwnerChangeListener = null;
		this.currentKeyboardFocusManager = null;
	}

	/**
	 * Registers a new listener on skin change.
	 * 
	 * @param skinChangeListener
	 *            New listener on skin change.
	 * @see #setSkin(String)
	 * @see #setSkin(SubstanceSkin)
	 * @see #unregisterSkinChangeListener(SkinChangeListener)
	 */
	public static void registerSkinChangeListener(
			SkinChangeListener skinChangeListener) {
		SubstanceLookAndFeel.skinChangeListeners.add(skinChangeListener);
	}

	/**
	 * Unregisters a listener on skin change.
	 * 
	 * @param skinChangeListener
	 *            The listener to unregister.
	 * @see #setSkin(String)
	 * @see #setSkin(SubstanceSkin)
	 * @see #registerSkinChangeListener(SkinChangeListener)
	 */
	public static void unregisterSkinChangeListener(
			SkinChangeListener skinChangeListener) {
		SubstanceLookAndFeel.skinChangeListeners.remove(skinChangeListener);
	}

	/**
	 * Registers the specified listener on tab-close events on <b>all</b> tabbed
	 * panes.
	 * 
	 * @param tabCloseListener
	 *            Listener to register.
	 * @see #registerTabCloseChangeListener(JTabbedPane, BaseTabCloseListener)
	 * @see #unregisterTabCloseChangeListener(BaseTabCloseListener)
	 * @see #unregisterTabCloseChangeListener(JTabbedPane, BaseTabCloseListener)
	 */
	public static void registerTabCloseChangeListener(
			BaseTabCloseListener tabCloseListener) {
		TabCloseListenerManager.getInstance()
				.registerListener(tabCloseListener);
	}

	/**
	 * Registers the specified listener on tab-close events on <b>the
	 * specified</b> tabbed pane.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane. If <code>null</code>, the tab close listener is
	 *            registered globally (for all tabbed panes).
	 * @param tabCloseListener
	 *            Listener to register.
	 * @see #registerTabCloseChangeListener(BaseTabCloseListener)
	 * @see #unregisterTabCloseChangeListener(BaseTabCloseListener)
	 * @see #unregisterTabCloseChangeListener(JTabbedPane, BaseTabCloseListener)
	 */
	public static void registerTabCloseChangeListener(JTabbedPane tabbedPane,
			BaseTabCloseListener tabCloseListener) {
		TabCloseListenerManager.getInstance().registerListener(tabbedPane,
				tabCloseListener);
	}

	/**
	 * Unregisters the specified listener on tab-close events on <b>all</b>
	 * tabbed panes.
	 * 
	 * @param tabCloseListener
	 *            Listener to unregister.
	 * @see #registerTabCloseChangeListener(BaseTabCloseListener)
	 * @see #registerTabCloseChangeListener(JTabbedPane, BaseTabCloseListener)
	 * @see #unregisterTabCloseChangeListener(JTabbedPane, BaseTabCloseListener)
	 */
	public static void unregisterTabCloseChangeListener(
			BaseTabCloseListener tabCloseListener) {
		TabCloseListenerManager.getInstance().unregisterListener(
				tabCloseListener);
	}

	/**
	 * Unregisters the specified listener on tab-close events on <b>the
	 * specified</b> tabbed pane.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane. If <code>null</code>, the tab close listener is
	 *            unregistered globally (for all tabbed panes).
	 * @param tabCloseListener
	 *            Listener to unregister.
	 * @see #registerTabCloseChangeListener(BaseTabCloseListener)
	 * @see #registerTabCloseChangeListener(JTabbedPane, BaseTabCloseListener)
	 * @see #unregisterTabCloseChangeListener(BaseTabCloseListener)
	 */
	public static void unregisterTabCloseChangeListener(JTabbedPane tabbedPane,
			BaseTabCloseListener tabCloseListener) {
		TabCloseListenerManager.getInstance().unregisterListener(tabbedPane,
				tabCloseListener);
	}

	/**
	 * Returns the set of all listeners registered on tab-close events on
	 * <b>all</b> tabbed panes.
	 * 
	 * @return Set of all listeners registered on tab-close events on <b>all</b>
	 *         tabbed panes.
	 */
	public static Set<BaseTabCloseListener> getAllTabCloseListeners() {
		return TabCloseListenerManager.getInstance().getListeners();
	}

	/**
	 * Returns all listeners registered on tab closing of the specified tabbed
	 * pane.
	 * 
	 * @param tabbedPane
	 *            A tabbed pane. If <code>null</code>, all globally registered
	 *            tab close listeners are returned.
	 * @return All listeners registered on tab closing of the specified tabbed
	 *         pane.
	 */
	public static Set<BaseTabCloseListener> getAllTabCloseListeners(
			JTabbedPane tabbedPane) {
		return TabCloseListenerManager.getInstance().getListeners(tabbedPane);
	}

	/**
	 * Registers a new listener on locale change.
	 * 
	 * @param localeListener
	 *            New listener on locale change.
	 */
	public static void registerLocaleChangeListener(
			LocaleChangeListener localeListener) {
		SubstanceLookAndFeel.localeChangeListeners.add(localeListener);
	}

	/**
	 * Unregisters a listener on locale change.
	 * 
	 * @param localeListener
	 *            The listener to unregister.
	 */
	public static void unregisterLocaleChangeListener(
			LocaleChangeListener localeListener) {
		SubstanceLookAndFeel.localeChangeListeners.remove(localeListener);
	}

	/**
	 * Returns all listeners registered on locale change.
	 * 
	 * @return All listeners registered on locale change.
	 */
	public static Set<LocaleChangeListener> getLocaleListeners() {
		return Collections
				.unmodifiableSet(SubstanceLookAndFeel.localeChangeListeners);
	}

	/**
	 * Sets the visibility of the specified widget kind(s). If the first
	 * <code>rootPane</code> parameter is <code>null</code>, this call applies
	 * to all root panes. This method should not be called from inside the
	 * initialization sequence of your window. If the specific widget needs to
	 * be visible when the window is shown, wrap the call with
	 * {@link SwingUtilities#invokeLater(Runnable)}.
	 * 
	 * @param rootPane
	 *            Root pane. May be <code>null</code>.
	 * @param visible
	 *            Visibility indication.
	 * @param substanceWidgets
	 *            Widget types.
	 * @since version 5.0
	 */
	public static void setWidgetVisible(JRootPane rootPane, boolean visible,
			SubstanceWidgetType... substanceWidgets) {
		SubstanceWidgetManager.getInstance().register(rootPane, visible,
				substanceWidgets);
		if (rootPane != null) {
			SwingUtilities.updateComponentTreeUI(rootPane);
		} else {
			for (Window window : Window.getWindows()) {
				JRootPane root = SwingUtilities.getRootPane(window);
				SwingUtilities.updateComponentTreeUI(root);
			}
		}
	}

	/**
	 * Checks whether the <code>JOptionPane</code>s created with predefined
	 * message types should use constant color schemes for the icons.
	 * 
	 * @return <code>true</code> if the <code>JOptionPane</code>s created with
	 *         predefined message types should use constant color schemes for
	 *         the icons, <code>false</code> otherwise.
	 * @see #setToUseConstantThemesOnDialogs(boolean)
	 */
	public static boolean isToUseConstantThemesOnDialogs() {
		return SubstanceLookAndFeel.toUseConstantThemesOnDialogs;
	}

	/**
	 * Sets the new setting for the icons of the <code>JOptionPane</code>s
	 * created with predefined message types.
	 * 
	 * @param toUseConstantThemesOnDialogs
	 *            if <code>true</code>, the <code>JOptionPane</code>s created
	 *            with predefined message types should use constant color
	 *            schemes for the icons.
	 * @see #isToUseConstantThemesOnDialogs()
	 */
	public static void setToUseConstantThemesOnDialogs(
			boolean toUseConstantThemesOnDialogs) {
		SubstanceLookAndFeel.toUseConstantThemesOnDialogs = toUseConstantThemesOnDialogs;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
				for (Window window : Window.getWindows()) {
					SwingUtilities.updateComponentTreeUI(window);
				}
			}
		});
	}

	/**
	 * The current Substance skin.
	 */
	private static SubstanceSkin currentSkin = null;

	/**
	 * Sets the specified skin. If the current look-and-feel is not Substance,
	 * this method will create a new Substance look-and-feel based on the
	 * specified skin and set it on {@link UIManager}. This method does not
	 * require Substance to be the current look-and-feel.
	 * 
	 * @param newSkin
	 *            Skin to set.
	 * @param toUpdateWindows
	 *            if <code>true</code>, the
	 *            {@link SwingUtilities#updateComponentTreeUI(Component)} is
	 *            called on all windows returned by {@link Window#getWindows()}
	 *            API.
	 * @return <code>true</code> if the specified skin has been set
	 *         successfully, <code>false</code> otherwise.
	 * @see #setSkin(SubstanceSkin)
	 */
	private static boolean setSkin(final SubstanceSkin newSkin,
			final boolean toUpdateWindows) {
		if (!SwingUtilities.isEventDispatchThread()) {
            final boolean[] returnValue = new boolean[1];
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        returnValue[0] = setSkin(newSkin, toUpdateWindows);
                    }
                });
            } catch (InvocationTargetException ite) {
                if (ite.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ite.getCause();
                } else if (ite.getCause() instanceof Error) {
                    throw (Error) ite.getCause();
                }
                
            } catch (InterruptedException ignore) {}
            return returnValue[0];
		}

		if (!newSkin.isValid())
			return false;

		boolean isSubstance = (UIManager.getLookAndFeel() instanceof SubstanceLookAndFeel);
		if (!isSubstance) {
			class SkinDerivedLookAndFeel extends SubstanceLookAndFeel {
				public SkinDerivedLookAndFeel(SubstanceSkin newSkin) {
					super(newSkin);
				}
			}

			LookAndFeel derived = new SkinDerivedLookAndFeel(newSkin);
			try {
				UIManager.setLookAndFeel(derived);
			} catch (UnsupportedLookAndFeelException ulafe) {
				return false;
			}
			if (!(UIManager.getLookAndFeel() instanceof SubstanceLookAndFeel)) {
				return false;
			}
			for (Window window : Window.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
			return true;
		}

		try {
			// Required skin settings must be non-null
			if (!newSkin.isValid()) {
				return false;
			}

			// fix for defect 109 - memory leak on watermark switch
			if ((currentSkin != null) && (currentSkin.getWatermark() != null)) {
				currentSkin.getWatermark().dispose();
			}
			if (newSkin.getWatermark() != null) {
				if (!newSkin.getWatermark().updateWatermarkImage(newSkin)) {
					return false;
				}
			}

			UIDefaults lafDefaults = UIManager.getLookAndFeelDefaults();
			// The table will be null when the skin is set using a custom
			// LAF
			if (lafDefaults != null) {
				initFontDefaults(lafDefaults, SubstanceLookAndFeel
						.getFontPolicy().getFontSet("Substance", null));
				newSkin.addCustomEntriesToTable(lafDefaults);
				SubstanceLookAndFeel.componentPlugins
						.processAllDefaultsEntries(lafDefaults, newSkin);
			}

			// file chooser strings go to the main UIManager table
            for (ResourceBundle bundle : new ResourceBundle[] {
                    ResourceBundle.getBundle("com.sun.swing.internal.plaf.metal.resources.metal"),
                    SubstanceLookAndFeel.getLabelBundle()
            }) {
                Enumeration<String> keyEn = bundle.getKeys();
                while (keyEn.hasMoreElements()) {
                    String key = keyEn.nextElement();
                    if (key.contains("FileChooser")) {
                        String value = bundle.getString(key);
                        UIManager.put(key, value);
                    }
                }
            }

			if (isSubstance)
				LazyResettableHashMap.reset();

			currentSkin = newSkin;

			if (toUpdateWindows) {
				for (Window window : Window.getWindows()) {
					SwingUtilities.updateComponentTreeUI(window);
				}
			}

			// SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
			for (SkinChangeListener skinChangeListener : SubstanceLookAndFeel.skinChangeListeners)
				skinChangeListener.skinChanged();
			// }
			// });
			return true;
		} catch (NoClassDefFoundError ncdfe) {
			// this may happen when a skin references some class
			// that can't be found in the classpath.
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Sets the specified skin. If the current look-and-feel is not Substance,
	 * this method will create a new Substance look-and-feel based on the
	 * specified skin and set it on {@link UIManager}. This method does not
	 * require Substance to be the current look-and-feel. Calling this method
	 * will call {@link SwingUtilities#updateComponentTreeUI(Component)} on all
	 * open top-level windows.
	 * 
	 * @param newSkin
	 *            Skin to set.
	 * @return <code>true</code> if the specified skin has been set
	 *         successfully, <code>false</code> otherwise.
	 * @throws IllegalStateException
	 *             When called outside the Event Dispatch Thread.
	 * @see #registerSkinChangeListener(SkinChangeListener)
	 * @see #unregisterSkinChangeListener(SkinChangeListener)
	 * @see SubstanceSkin#isValid()
	 */
	public static boolean setSkin(SubstanceSkin newSkin) {
		return setSkin(newSkin, true);
	}

	/**
	 * Sets the specified skin. If the current look-and-feel is not Substance,
	 * this method will create a new Substance look-and-feel based on the
	 * specified skin and set it on {@link UIManager}. This method does not
	 * require Substance to be the current look-and-feel. Calling this method
	 * will call {@link SwingUtilities#updateComponentTreeUI(Component)} on all
	 * open top-level windows.
	 * 
	 * @param skinClassName
	 *            Skin to set.
	 * @return <code>true</code> if the specified skin has been set
	 *         successfully, <code>false</code> otherwise.
	 * @throws IllegalStateException
	 *             When called outside the Event Dispatch Thread.
	 * @since version 3.1
	 * @see #setSkin(SubstanceSkin)
	 * @see #registerSkinChangeListener(SkinChangeListener)
	 * @see #unregisterSkinChangeListener(SkinChangeListener)
	 * @see SubstanceSkin#isValid()
	 */
	public static boolean setSkin(String skinClassName) {
		try {
			Class<?> skinClass = Class.forName(skinClassName);
			if (skinClass == null) {
				return false;
			}
			Object obj = skinClass.newInstance();
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof SubstanceSkin)) {
				return false;
			}
			return SubstanceLookAndFeel.setSkin((SubstanceSkin) obj);
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns all available skins.
	 * 
	 * @return All available skins. Key - skin display name, value - skin
	 *         information.
	 */
	public static Map<String, SkinInfo> getAllSkins() {
		initPluginsIfNecessary();
		Map<String, SkinInfo> result = new TreeMap<String, SkinInfo>();
		for (Object skinPlugin : SubstanceLookAndFeel.skinPlugins
				.getAvailablePlugins(true)) {
			for (SkinInfo skinInfo : ((SubstanceSkinPlugin) skinPlugin)
					.getSkins()) {
				result.put(skinInfo.getDisplayName(), skinInfo);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#getSupportsWindowDecorations()
	 */
	@Override
	public boolean getSupportsWindowDecorations() {
		return true;
	}

	/**
	 * Sets the class loader for {@link #LABEL_BUNDLE}.
	 * 
	 * @param labelBundleClassLoader
	 *            Class loader for {@link #LABEL_BUNDLE}.
	 * @since version 3.1
	 */
	public static void setLabelBundleClassLoader(
			ClassLoader labelBundleClassLoader) {
		SubstanceLookAndFeel.labelBundleClassLoader = labelBundleClassLoader;
		LafWidgetRepository.setLabelBundleClassLoader(labelBundleClassLoader);
	}

	/**
	 * Returns the title pane of the specified top-level window.
	 * 
	 * @param window
	 *            Top-level window.
	 * @return If the parameter is either {@link JFrame} or {@link JDialog} and
	 *         has custom decorations, the result is the title pane,
	 *         <code>null</code> otherwise.
	 * @since version 3.1
	 */
	public static JComponent getTitlePaneComponent(Window window) {
		JRootPane rootPane = null;
		if (window instanceof JFrame) {
			JFrame f = (JFrame) window;
			rootPane = f.getRootPane();
		}
		if (window instanceof JDialog) {
			JDialog d = (JDialog) window;
			rootPane = d.getRootPane();
		}
		if (rootPane != null) {
			SubstanceRootPaneUI ui = (SubstanceRootPaneUI) rootPane.getUI();
			return ui.getTitlePane();
		}
		return null;
	}

	/**
	 * Sets the decoration type of the specified component and all its children.
	 * 
	 * @param comp
	 *            Component.
	 * @param type
	 *            Decoration type of the component and all its children.
	 */
	public static void setDecorationType(JComponent comp,
			DecorationAreaType type) {
		DecorationPainterUtils.setDecorationType(comp, type);
	}

	/**
	 * Returns the decoration area type of the specified component. The
	 * component and its ancestor hierarchy are scanned for the registered
	 * decoration area type. If
	 * {@link #setDecorationType(JComponent, DecorationAreaType)} has been
	 * called on the specified component, the matching decoration type is
	 * returned. Otherwise, the component hierarchy is scanned to find the
	 * closest ancestor that was passed to
	 * {@link #setDecorationType(JComponent, DecorationAreaType)} - and its
	 * decoration type is returned. If neither the component, nor any one of its
	 * parent components has been passed to the setter method,
	 * {@link DecorationAreaType#NONE} is returned.
	 * 
	 * @param comp
	 *            Component.
	 * @return Decoration area type of the component.
	 */
	public static DecorationAreaType getDecorationType(Component comp) {
		return DecorationPainterUtils.getDecorationType(comp);
	}

	/**
	 * Returns the immediate decoration area type of the specified component.
	 * The component is checked for the registered decoration area type. If
	 * {@link #setDecorationType(javax.swing.JComponent, DecorationAreaType)} was
	 * not called on this component, this method returns <code>null</code>.
	 * 
	 * @param comp
	 *            Component.
	 * @return Immediate decoration area type of the component.
	 */
	public static DecorationAreaType getImmediateDecorationType(Component comp) {
		return DecorationPainterUtils.getImmediateDecorationType(comp);
	}

	/**
	 * Checks whether Substance is the current look-and-feel. This method is for
	 * internal use only.
	 * 
	 * @return <code>true</code> if Substance is the current look-and-feel,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isCurrentLookAndFeel() {
		return ((UIManager.getLookAndFeel() instanceof SubstanceLookAndFeel) && (currentSkin != null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.LookAndFeel#getDisabledIcon(javax.swing.JComponent,
	 * javax.swing.Icon)
	 */
	@Override
	public Icon getDisabledIcon(JComponent component, Icon icon) {
		if (icon == null)
			return null;
		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(component, ComponentState.DISABLED_UNSELECTED);
		BufferedImage result = SubstanceImageCreator.getColorSchemeImage(
				component, icon, colorScheme, 0.5f);
		float alpha = SubstanceColorSchemeUtilities.getAlpha(component,
				ComponentState.DISABLED_UNSELECTED);
		if (alpha < 1.0f) {
			BufferedImage intermediate = SubstanceCoreUtilities.getBlankImage(
					result.getWidth(), result.getHeight());
			Graphics2D g2d = intermediate.createGraphics();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(result, 0, 0, null);
			g2d.dispose();
			result = intermediate;
		}

		return new IconUIResource(new ImageIcon(result));
	}
}
