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
package org.pushingpixels.substance.internal.ui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceInternalFrameTitlePane;

/**
 * UI for internal frames in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceInternalFrameUI extends BasicInternalFrameUI {
	/**
	 * Title pane
	 */
	private SubstanceInternalFrameTitlePane titlePane;

	/**
	 * Property listener on the associated internal frame.
	 */
	protected PropertyChangeListener substancePropertyListener;

	/**
	 * Simple constructor.
	 * 
	 * @param b
	 *            Associated internal frame.
	 */
	public SubstanceInternalFrameUI(JInternalFrame b) {
		super(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceInternalFrameUI((JInternalFrame) comp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicInternalFrameUI#createNorthPane(javax.swing
	 * .JInternalFrame)
	 */
	@Override
	protected JComponent createNorthPane(JInternalFrame w) {
		this.titlePane = new SubstanceInternalFrameTitlePane(w);

		// f.putClientProperty(INTERNAL_FRAME_PINNED, Boolean.TRUE);

		return this.titlePane;
	}

    @Override
    protected void installComponents() {
        if (SubstanceCoreUtilities.isRoundedCorners(frame)) {
            frame.setOpaque(false);
        }
        super.installComponents();
    }

    /*
      * (non-Javadoc)
      *
      * @see javax.swing.plaf.basic.BasicInternalFrameUI#uninstallComponents()
      */
	@Override
	protected void uninstallComponents() {
		this.titlePane.uninstall();
		super.uninstallComponents();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.plaf.basic.BasicInternalFrameUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		this.substancePropertyListener = new PropertyChangeListener() {
			@Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
				if (JInternalFrame.IS_CLOSED_PROPERTY.equals(propertyName)) {
					titlePane.uninstall();
					JDesktopIcon jdi = frame.getDesktopIcon();
					SubstanceDesktopIconUI ui = (SubstanceDesktopIconUI) jdi
							.getUI();
					ui.uninstallIfNecessary(jdi);
				} else if ("background".equals(propertyName)) {
					Color newBackgr = (Color) evt.getNewValue();
					if (!(newBackgr instanceof UIResource)) {
						getTitlePane().setBackground(newBackgr);
						frame.getDesktopIcon().setBackground(newBackgr);
					}
				} else if ("ancestor".equals(propertyName)) {
					// fix for issue 344 - reopening an internal frame
					// that has been closed.
					JDesktopIcon jdi = frame.getDesktopIcon();
					SubstanceDesktopIconUI ui = (SubstanceDesktopIconUI) jdi
							.getUI();
					ui.installIfNecessary(jdi);
				} else if (JInternalFrame.IS_SELECTED_PROPERTY.equals(propertyName)) {
                    titlePane.setActive((Boolean)evt.getNewValue());
                } else if (SubstanceLookAndFeel.WINDOW_ROUNDED_CORNERS.equals(propertyName)
                        || JInternalFrame.IS_MAXIMUM_PROPERTY.equals(propertyName))
                {
                    if (SubstanceCoreUtilities.isRoundedCorners(frame)) {
                        frame.setOpaque(false);
                    }
                }
			}
		};
		this.frame.addPropertyChangeListener(this.substancePropertyListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicInternalFrameUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		this.frame.removePropertyChangeListener(this.substancePropertyListener);
		this.substancePropertyListener = null;
		super.uninstallListeners();
	}

	// private class BorderListener1 extends BorderListener implements
	// SwingConstants {
	//
	// Rectangle getIconBounds() {
	// int xOffset = 5;
	// Rectangle rect = null;
	//
	// Icon icon = SubstanceInternalFrameUI.this.frame.getFrameIcon();
	// if (icon != null) {
	// int iconY = ((SubstanceInternalFrameUI.this.titlePane.getHeight() / 2) -
	// (icon
	// .getIconHeight() / 2));
	// rect = new Rectangle(xOffset, iconY, icon.getIconWidth(), icon
	// .getIconHeight());
	// }
	// return rect;
	// }
	//
	// @Override
	// public void mouseClicked(MouseEvent e) {
	// if ((e.getClickCount() == 2) && (e.getSource() ==
	// SubstanceInternalFrameUI.this.getNorthPane())
	// && SubstanceInternalFrameUI.this.frame.isClosable() &&
	// !SubstanceInternalFrameUI.this.frame.isIcon()) {
	// Rectangle rect = this.getIconBounds();
	// if ((rect != null) && rect.contains(e.getX(), e.getY())) {
	// SubstanceInternalFrameUI.this.frame.doDefaultCloseAction();
	// } else {
	// super.mouseClicked(e);
	// }
	// } else {
	// super.mouseClicked(e);
	// }
	// }
	// } // / End BorderListener Class
	//
	// /**
	// * Returns the <code>MouseInputAdapter<code> that will be installed
	// * on the TitlePane.
	// *
	// * @param w the <code>JInternalFrame</code>
	// * @return the <code>MouseInputAdapter</code> that will be installed
	// * on the TitlePane.
	// * @since 1.6
	// */
	// @Override
	// protected MouseInputAdapter createBorderListener(JInternalFrame w) {
	// return new BorderListener1();
	// }
	//
	/**
	 * Returns the title pane of the associated internal frame. This method is
	 * <b>for internal use only</b>.
	 * 
	 * @return Title pane of the associated internal frame.
	 */
	public SubstanceInternalFrameTitlePane getTitlePane() {
		return titlePane;
	}

	void setWindowModified(boolean isWindowModified) {
		titlePane.getCloseButton().putClientProperty(
				SubstanceLookAndFeel.WINDOW_MODIFIED,
                isWindowModified);

		SubstanceDesktopIconUI desktopIconUi = (SubstanceDesktopIconUI) this.frame
				.getDesktopIcon().getUI();
		desktopIconUi.setWindowModified(isWindowModified);
	}
}
