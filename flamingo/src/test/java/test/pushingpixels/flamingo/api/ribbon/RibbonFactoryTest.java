package test.pushingpixels.flamingo.api.ribbon;

import java.awt.Color;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonFactory;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;

public class RibbonFactoryTest extends TestCase {

	/** The factory to be used for each test */
	private RibbonFactory ribbon;

	@Before
	public void setUp() throws Exception {
		ribbon = new RibbonFactory();
	}

	/** Tests that ribbon tasks are added successfully */
	@Test
	public void testRibbonTasks() {
		for (int i = 0; i < 5; i++) {
			addBands(4, 5);
			ribbon.addTask("Task " + i);
			JRibbon r = ribbon.getRibbon();
			assertEquals(i + 1, r.getTaskCount());

		}
	}

	/** Tests that contextual ribbon tasks are added successfully */
	@Test
	public void testRibbonContextualTasks() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				addBands(4, 5);
				ribbon.addContextualTask("Task " + j);
			}
			ribbon.addContextualTaskGroup("Task Group " + i, Color.red);
			JRibbon r = ribbon.getRibbon();
			assertEquals(0, r.getTaskCount());
			assertEquals(i + 1, r.getContextualTaskGroupCount());
		}
	}

	/**
	 * Tests that each <code>addButton</code> method in the factory successfully
	 * adds a button to the ribbon
	 */
	@Test
	public void testAddButtons() {
		assertEquals(0, ribbon.getButtonsQueue().size());
		ribbon.addButton(new JCommandButton("Test"));
		assertEquals(1, ribbon.getButtonsQueue().size());
		ribbon.addButton((ResizableIcon) null, null);
		assertEquals(2, ribbon.getButtonsQueue().size());
		ribbon.addButton("Test", null);
		assertEquals(3, ribbon.getButtonsQueue().size());
		ribbon.addButton("Test", null, null);
		assertEquals(4, ribbon.getButtonsQueue().size());
		ribbon.addButton("Test", null, null, true);
		assertEquals(5, ribbon.getButtonsQueue().size());
		ribbon.addButton("Test", null, null, CommandButtonKind.ACTION_ONLY);
		assertEquals(6, ribbon.getButtonsQueue().size());
		ribbon.addButton("Test", null, null, CommandButtonKind.ACTION_ONLY, true);
		assertEquals(7, ribbon.getButtonsQueue().size());
		ribbon.addButton("Test", null, null, CommandButtonKind.ACTION_ONLY, null, null, null, true);
		assertEquals(8, ribbon.getButtonsQueue().size());
		ribbon.addButtonTypeAction("Test", null, null, null);
		assertEquals(9, ribbon.getButtonsQueue().size());
		ribbon.addButtonTypeActionMain("test", null, null, null, null);
		assertEquals(10, ribbon.getButtonsQueue().size());
		ribbon.addButtonTypePopup("Test", null, null, null);
		assertEquals(11, ribbon.getButtonsQueue().size());
		ribbon.addButtonTypePopupMain("Test", null, null, null, null);
		assertEquals(12, ribbon.getButtonsQueue().size());
	}

	/** Tests that buttons are added to a band successfully */
	public void testCommandButtons() {
		addButtons(5);
		ribbon.addBand("Band 1");
		ribbon.addTask("Task 1");
		JRibbon r = ribbon.getRibbon();
		// JRibbonBands consist of a JBandControlPanel and JCommandButton
		// The JBandControlPanel contains the buttons in the panel
		assertEquals(5, ((JBandControlPanel) r.getTask(0).getBand(0)
				.getComponent(0)).getComponentCount());
	}

	/**
	 * Adds the amount of ribbon <code>bands</code> to the ribbon and the amount
	 * of <code>buttons</code> to each band. The <code>RibbonFactory</code> is
	 * returned in order to allow chaining with other methods for the test to
	 * quickly create a test case.
	 * 
	 * @param bands
	 *            the amount of bands to add to the ribbon
	 * @param buttons
	 *            the amount of buttons to add to each band
	 * @return the test <code>RibbonFactory</code>
	 */
	protected RibbonFactory addBands(int bands, int buttons) {
		for (int i = 0; i < bands; i++) {
			addButtons(buttons);
			ribbon.addBand("Band " + i);
		}
		return ribbon;
	}

	/**
	 * Adds the amount <code>buttons</code> of buttons to the ribbon for the
	 * test. The <code>RibbonFactory</code> is returned in order to allow
	 * chaining with other methods for the test to quickly create a test case.
	 * 
	 * @param buttons
	 *            the amount of buttons to add to the ribbon
	 * @return the test <code>RibbonFactory</code>
	 */
	protected RibbonFactory addButtons(int buttons) {
		for (int i = 0; i < buttons; i++) {
			ribbon.addButton("Button " + i, null);
		}
		return ribbon;
	}

}
