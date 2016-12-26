package test.pushingpixels.flamingo.api.ribbon;

import java.awt.Color;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
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

	@After
	public void tearDown() throws Exception {
		ribbon = null;
	}

	/** Tests that ribbon tasks are added successfully */
	public void testRibbonTasks() {
		for (int i = 0; i < 5; i++) {
			addBands(4, 5);
			ribbon.addTask("Task " + i);
			JRibbon r = ribbon.getRibbon();
			assertEquals(i + 1, r.getTaskCount());
		}
	}

	/** Tests that contextual ribbon tasks are added successfully */
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
			ribbon.addButton("Button " + i);
		}
		return ribbon;
	}

}
