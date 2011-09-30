package test.pushingpixels.flamingo.api.ribbon;

import junit.framework.TestCase;

import org.junit.Test;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonFactory;

public class JRibbonTest extends TestCase {

	/* The ribbon being tested */
	private JRibbon ribbon;
	
	@Override
	protected void setUp() throws Exception {
		ribbon = new JRibbon();
	}
	
	@Test
	public void testIconChanges() {
		ResizableIcon icon = RibbonFactory.getResizableIconFromResource("test/resources/erichschroeter.png");
		
		// test the mutator
		ribbon.setApplicationIcon(icon);
		assertEquals(icon, ribbon.getApplicationIcon());
		
		// test the constructor
		ribbon = new JRibbon(icon);
		assertEquals(icon, ribbon.getApplicationIcon());
	}

}
