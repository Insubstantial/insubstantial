package example.pushingpixels.flamingo.api.ribbon;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonFactory;

/**
 * The <code>ExampleRibbonFrame</code> class exists to show users how to use the
 * {@link RibbonFactory} with a {@link JRibbonFrame}. Not all methods provided
 * by the <code>RibbonFactory</code> are used, but enough to give a decent
 * example for new users.
 * 
 * @author Erich Schroeter
 */
@SuppressWarnings("serial")
public class ExampleRibbonFrame extends JRibbonFrame {

	/** An example contextual task group */
	private static RibbonContextualTaskGroup extra1RibbonTasks;
	/** An example contextual task group */
	private static RibbonContextualTaskGroup extra2RibbonTasks;
	/** An example contextual task group */
	private static RibbonContextualTaskGroup extra3RibbonTasks;

	/**
	 * Constructs a default <code>ExampleRibbonFrame</code> to show an example
	 * of how to use a {@link JRibbonFrame}.
	 */
	public ExampleRibbonFrame() {
		super("Example Ribbon Frame");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createApplicationRibbon(getRibbon());
		getRibbon()
				.addTaskbarComponent(
						new JCommandButton(
								getResizableIconFromResource("example/resources/cut.png")));
		getRibbon()
				.addTaskbarComponent(
						new JCommandButton(
								getResizableIconFromResource("example/resources/paste.png")));
	}

	/**
	 * Uses a {@link RibbonFactory} to build a {@link JRibbon}.
	 * 
	 * @return the application ribbon
	 */
	private JRibbon createApplicationRibbon(JRibbon ribbon) {
		RibbonFactory factory = new RibbonFactory(ribbon)
				.withHelp(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("Help");
					}
				});

		// build application menu
		factory.newSubMenuGroup();
		factory.addSubMenuItem("Github, Inc (Flamingo snapshot)",
				getResizableIconFromResource("example/resources/github.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openInBrowser("https://github.com/kirillcool/flamingo");
					}
				});
		factory.addSubMenuItem("Kirill Grouchnikov Website",
				getResizableIconFromResource("example/resources/blank.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openInBrowser("http://www.pushing-pixels.org/");
					}
				});
		factory.addSubMenuItem("Insubstantial (Flamingo fork)",
				getResizableIconFromResource("example/resources/github.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openInBrowser("https://github.com/Insubstantial/insubstantial");
					}
				});
		factory.addSubMenuItem(
				"Erich Schroeter Website",
				getResizableIconFromResource("example/resources/erichschroeter.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openInBrowser("http://www.erichschroeter.com/");
					}
				});
		factory.addMenuItem(
				"Websites",
				getResizableIconFromResource("example/resources/websites-menu-item.png"));
		factory.addSpacerMenuItem();
		factory.addSpacerMenuItem();
		factory.addSpacerMenuItem();
		factory.addSpacerMenuItem();
		factory.addFooterMenuItem("Exit",
				getResizableIconFromResource("example/resources/exit.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
		factory.withMenu(getResizableIconFromResource("example/resources/erichschroeter.png"));

		// create ribbon task 1
		factory.addButton("Extra 1",
				getResizableIconFromResource("example/resources/task.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle task visibility
						if (getRibbon().isVisible(extra1RibbonTasks)) {
							getRibbon().setVisible(extra1RibbonTasks, false);
						} else {
							getRibbon().setVisible(extra1RibbonTasks, true);
						}
					}
				});
		factory.addButton("Extra 2",
				getResizableIconFromResource("example/resources/task.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle task visibility
						if (getRibbon().isVisible(extra2RibbonTasks)) {
							getRibbon().setVisible(extra2RibbonTasks, false);
						} else {
							getRibbon().setVisible(extra2RibbonTasks, true);
						}
					}
				});
		factory.addButton("Extra 3",
				getResizableIconFromResource("example/resources/task.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle task visibility
						if (getRibbon().isVisible(extra3RibbonTasks)) {
							getRibbon().setVisible(extra3RibbonTasks, false);
						} else {
							getRibbon().setVisible(extra3RibbonTasks, true);
						}
					}
				});
		factory.addBand("Contextual Ribbon Tasks");
		factory.addButton("Copy",
				getResizableIconFromResource("example/resources/copy.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle copy
					}
				}).hasPriority(RibbonElementPriority.LOW);
		factory.addButton("Paste",
				getResizableIconFromResource("example/resources/paste.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle paste
					}
				}).hasPriority(RibbonElementPriority.LOW);
		factory.addButton("Cut",
				getResizableIconFromResource("example/resources/cut.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle cut
					}
				}).hasPriority(RibbonElementPriority.LOW);
		factory.addButton("Copy",
				getResizableIconFromResource("example/resources/copy.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle copy
					}
				}).hasPriority(RibbonElementPriority.MEDIUM);
		factory.addButton("Paste",
				getResizableIconFromResource("example/resources/paste.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle paste
					}
				}).hasPriority(RibbonElementPriority.MEDIUM);
		factory.addButton("Cut",
				getResizableIconFromResource("example/resources/cut.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle cut
					}
				}).hasPriority(RibbonElementPriority.MEDIUM);
		factory.addButton("Copy",
				getResizableIconFromResource("example/resources/copy.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle copy
					}
				});
		factory.addButton("Paste",
				getResizableIconFromResource("example/resources/paste.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle paste
					}
				});
		factory.addButton("Cut",
				getResizableIconFromResource("example/resources/cut.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle cut
					}
				});
		factory.addBand("Basic Commands");
		factory.addTask("Text");

		// create ribbon task 2
		factory.addButton("Smite",
				getResizableIconFromResource("example/resources/smite.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle smite
					}
				});
		factory.addButton("Forgive",
				getResizableIconFromResource("example/resources/forgive.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle forgive
					}
				});
		factory.addButton(
				"Create Life",
				getResizableIconFromResource("example/resources/create-life.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle create life
					}
				});
		factory.addButton(
				"Destroy Life",
				getResizableIconFromResource("example/resources/destroy-life.png"),
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO handle destroy life
					}
				});
		factory.addBand("Basic Commands");
		factory.addTask("God");

		factory.addButton("Blah",
				getResizableIconFromResource("example/resources/task.png"),
				null).addBand("Blah",
				getResizableIconFromResource("example/resources/task.png"));
		factory.addContextualTask("Extra 1");
		extra1RibbonTasks = factory
				.getContextualTaskGroup("Extra 1", Color.red);
		factory.addContextualTaskGroup(extra1RibbonTasks);

		factory.clearContextualTaskGroupsQueue();
		factory.addButton("First",
				getResizableIconFromResource("example/resources/task.png"),
				null).addBand("Blah",
				getResizableIconFromResource("example/resources/task.png"));
		factory.addContextualTask("Extra 1");
		factory.addButton("Second",
				getResizableIconFromResource("example/resources/task.png"),
				null).addBand("Blah",
				getResizableIconFromResource("example/resources/task.png"));
		factory.addContextualTask("Extra 2");
		extra2RibbonTasks = factory.getContextualTaskGroup("Extra 2",
				Color.green);
		factory.addContextualTaskGroup(extra2RibbonTasks);

		factory.clearContextualTaskGroupsQueue();
		factory.addButton("First",
				getResizableIconFromResource("example/resources/task.png"),
				null).addBand("Blah",
				getResizableIconFromResource("example/resources/task.png"));
		factory.addContextualTask("Extra 1");
		factory.addButton("Second",
				getResizableIconFromResource("example/resources/task.png"),
				null).addBand("Blah",
				getResizableIconFromResource("example/resources/task.png"));
		factory.addContextualTask("Extra 2");
		factory.addButton("Third",
				getResizableIconFromResource("example/resources/task.png"),
				null).addBand("Blah",
				getResizableIconFromResource("example/resources/task.png"));
		factory.addContextualTask("Extra 3");
		extra3RibbonTasks = factory.getContextualTaskGroup("Extra 3",
				Color.blue);
		factory.addContextualTaskGroup(extra3RibbonTasks);

		return factory.getRibbon();
	}

	/**
	 * Handles opening the <code>url</code> in the default web browser.
	 * 
	 * @param url
	 *            a valid website address
	 * @return <code>false</code> if an IOException occurs when creating a
	 *         {@link URI} instance with <code>url</code>, else
	 *         <code>true</code>
	 */
	public static boolean openInBrowser(String url) {
		try {
			Desktop.getDesktop().browse(URI.create(url));
		} catch (IOException e1) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				ExampleRibbonFrame frame = new ExampleRibbonFrame();
				frame.setPreferredSize(new Dimension(500, 200));
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	/**
	 * A wrapper helper function to the
	 * <code>{@link #getResizableIconFromResource(URL, Dimension)}</code>
	 * function.
	 * <p>
	 * This function simply calls
	 * <code>getResizableIconFromResource(resource, new
	 * Dimension(48,48))</code> .
	 * </p>
	 * 
	 * @param resource
	 *            the resource to retrieve
	 * @return a <code>ResizableIcon</code> object with the resource
	 */
	public static ResizableIcon getResizableIconFromResource(URL resource) {
		return getResizableIconFromResource(resource, new Dimension(48, 48));
	}

	/**
	 * A wrapper helper function to the
	 * <code>{@link #getResizableIconFromResource(URL, Dimension)}</code>
	 * function.
	 * <p>
	 * This function simply calls
	 * <code>getResizableIconFromResource(Helpers.class.getClassLoader()
				.getResource(resource), new Dimension(48,48))</code>.
	 * <p>
	 * The <code>resource</code> string should be the full package path of where
	 * the resource is located omitting a preceding / (e.g.
	 * "example/resources/blah.png" and <em>NOT</em>
	 * "/example/resources/blah.png").
	 * 
	 * @param resource
	 *            the resource to retrieve
	 * @return a <code>ResizableIcon</code> object with the resource
	 */
	public static ResizableIcon getResizableIconFromResource(String resource) {
		return getResizableIconFromResource(ExampleRibbonFrame.class
				.getClassLoader().getResource(resource), new Dimension(48, 48));
	}

	/**
	 * A helper function that returns a <code>ResizableIcon</code> after
	 * retrieving the resource given.
	 * 
	 * @param resource
	 *            the resource to retrieve
	 * @param size
	 *            the size of the returned icon
	 * @return a <code>ResizableIcon</code> object with the resource
	 */
	public static ResizableIcon getResizableIconFromResource(URL resource,
			Dimension size) {
		return ImageWrapperResizableIcon.getIcon(resource, size);
	}

}
