package utest.ribbon;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.fest.assertions.Assertions;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;

public class RibbonBandTestCase extends FestSwingJUnitTestCase {
	private JRibbonFrame frame;

	private JRibbonBand band11;
	private JRibbonBand band12;
	private JRibbonBand band21;
	private JRibbonBand band22;

	private RibbonTask task1;
	private RibbonTask task2;

	private RibbonTask taskCont1_1;
	private RibbonTask taskCont1_2;
	private RibbonTask taskCont1_3;

	private RibbonContextualTaskGroup taskGroup1;
	private RibbonContextualTaskGroup taskGroup2;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(new GuiTask() {
			private JRibbonBand createBand(int buttonCount, String title,
					String titlePrefix) {
				JRibbonBand result = new JRibbonBand(title,
						new EmptyResizableIcon(32));
				for (int i = 0; i < buttonCount; i++) {
					result.addCommandButton(new JCommandButton(titlePrefix
							+ (i + 1), new EmptyResizableIcon(32)),
							RibbonElementPriority.TOP);
				}
				result.setResizePolicies((List) Arrays
						.asList(new IconRibbonBandResizePolicy(result
								.getControlPanel())));
				return result;
			}

			@Override
			protected void executeInEDT() throws Throwable {
				frame = new JRibbonFrame();
				JRibbon ribbon = frame.getRibbon();

				band11 = createBand(5, "Band 11", "Test 11");
				band12 = createBand(6, "Band 12", "Test 12");

				task1 = new RibbonTask("Task1", band11, band12);
				ribbon.addTask(task1);

				band21 = createBand(5, "Band 21", "Test 21");
				band22 = createBand(6, "Band 22", "Test 22");

				task2 = new RibbonTask("Task2", band21, band22);
				ribbon.addTask(task2);

				taskCont1_1 = new RibbonTask("Context task 1 1", createBand(6,
						"Context 11", "Test 11"));
				taskCont1_2 = new RibbonTask("Context task 1 2", createBand(6,
						"Context 12", "Test 12"));
				taskCont1_3 = new RibbonTask("Context task 1 3", createBand(6,
						"Context 13", "Test 13"));
				taskGroup1 = new RibbonContextualTaskGroup("Context 1",
						Color.red, taskCont1_1, taskCont1_2, taskCont1_3);
				ribbon.addContextualTaskGroup(taskGroup1);

				RibbonTask taskCont2_1 = new RibbonTask("Context task 2 1",
						createBand(6, "Context 21", "Test 21"));
				RibbonTask taskCont2_2 = new RibbonTask("Context task 2 2",
						createBand(6, "Context 22", "Test 22"));
				taskGroup2 = new RibbonContextualTaskGroup("Context 2",
						Color.blue, taskCont2_1, taskCont2_2);
				ribbon.addContextualTaskGroup(taskGroup2);

				frame.setSize(600, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	@Test
	public void testBands() {
		Assertions.assertThat(task1.getBandCount()).isEqualTo(2);
		Assertions.assertThat(
				frame.getRibbon().getSelectedTask().getBandCount())
				.isEqualTo(2);
		Assertions.assertThat(
				frame.getRibbon().getSelectedTask().getBands().size())
				.isEqualTo(2);
	}

	@Test
	public void testSelectedTask() {
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task1);

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				frame.getRibbon().setSelectedTask(task1);
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task1);

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				frame.getRibbon().setSelectedTask(task1);
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task1);

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				frame.getRibbon().setSelectedTask(task2);
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task2);

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				frame.getRibbon().setSelectedTask(task2);
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task2);

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				frame.getRibbon().setSelectedTask(task1);
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task1);

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				frame.getRibbon().setSelectedTask(task2);
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(frame.getRibbon().getSelectedTask()).isEqualTo(
				task2);
	}

	@Test
	public void testSetTaskTitle() {
		Assertions.assertThat(task1.getTitle()).isEqualTo("Task1");
		Assertions.assertThat(task2.getTitle()).isEqualTo("Task2");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				task1.setTitle("NewTask1");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask1");
		Assertions.assertThat(task2.getTitle()).isEqualTo("Task2");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				task1.setTitle("NewTask11");
			}
		});
		robot().waitForIdle();

		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask11");
		Assertions.assertThat(task2.getTitle()).isEqualTo("Task2");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				task2.setTitle("NewTask2");
			}
		});
		robot().waitForIdle();

		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask11");
		Assertions.assertThat(task2.getTitle()).isEqualTo("NewTask2");

		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask11");
		Assertions.assertThat(task2.getTitle()).isEqualTo("NewTask2");
		Assertions.assertThat(taskCont1_1.getTitle()).isEqualTo(
				"Context task 1 1");
		Assertions.assertThat(taskCont1_2.getTitle()).isEqualTo(
				"Context task 1 2");
		Assertions.assertThat(taskCont1_3.getTitle()).isEqualTo(
				"Context task 1 3");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				taskCont1_1.setTitle("New Context task 1 1");
			}
		});
		robot().waitForIdle();

		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask11");
		Assertions.assertThat(task2.getTitle()).isEqualTo("NewTask2");
		Assertions.assertThat(taskCont1_1.getTitle()).isEqualTo(
				"New Context task 1 1");
		Assertions.assertThat(taskCont1_2.getTitle()).isEqualTo(
				"Context task 1 2");
		Assertions.assertThat(taskCont1_3.getTitle()).isEqualTo(
				"Context task 1 3");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				taskCont1_2.setTitle("New Context task 1 2");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask11");
		Assertions.assertThat(task2.getTitle()).isEqualTo("NewTask2");
		Assertions.assertThat(taskCont1_1.getTitle()).isEqualTo(
				"New Context task 1 1");
		Assertions.assertThat(taskCont1_2.getTitle()).isEqualTo(
				"New Context task 1 2");
		Assertions.assertThat(taskCont1_3.getTitle()).isEqualTo(
				"Context task 1 3");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				taskCont1_3.setTitle("New Context task 1 3");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(task1.getTitle()).isEqualTo("NewTask11");
		Assertions.assertThat(task2.getTitle()).isEqualTo("NewTask2");
		Assertions.assertThat(taskCont1_1.getTitle()).isEqualTo(
				"New Context task 1 1");
		Assertions.assertThat(taskCont1_2.getTitle()).isEqualTo(
				"New Context task 1 2");
		Assertions.assertThat(taskCont1_3.getTitle()).isEqualTo(
				"New Context task 1 3");
	}

	@Test
	public void testSetBandTitle() {
		Assertions.assertThat(band11.getTitle()).isEqualTo("Band 11");
		Assertions.assertThat(band12.getTitle()).isEqualTo("Band 12");
		Assertions.assertThat(band21.getTitle()).isEqualTo("Band 21");
		Assertions.assertThat(band22.getTitle()).isEqualTo("Band 22");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				band11.setTitle("New Band 1 1");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(band11.getTitle()).isEqualTo("New Band 1 1");
		Assertions.assertThat(band12.getTitle()).isEqualTo("Band 12");
		Assertions.assertThat(band21.getTitle()).isEqualTo("Band 21");
		Assertions.assertThat(band22.getTitle()).isEqualTo("Band 22");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				band12.setTitle("New Band 1 2");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(band11.getTitle()).isEqualTo("New Band 1 1");
		Assertions.assertThat(band12.getTitle()).isEqualTo("New Band 1 2");
		Assertions.assertThat(band21.getTitle()).isEqualTo("Band 21");
		Assertions.assertThat(band22.getTitle()).isEqualTo("Band 22");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				band21.setTitle("New Band 2 1");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(band11.getTitle()).isEqualTo("New Band 1 1");
		Assertions.assertThat(band12.getTitle()).isEqualTo("New Band 1 2");
		Assertions.assertThat(band21.getTitle()).isEqualTo("New Band 2 1");
		Assertions.assertThat(band22.getTitle()).isEqualTo("Band 22");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				band22.setTitle("New Band 2 2");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(band11.getTitle()).isEqualTo("New Band 1 1");
		Assertions.assertThat(band12.getTitle()).isEqualTo("New Band 1 2");
		Assertions.assertThat(band21.getTitle()).isEqualTo("New Band 2 1");
		Assertions.assertThat(band22.getTitle()).isEqualTo("New Band 2 2");
	}

	@Test
	public void testSetContextualTaskGroupTitle() {
		Assertions.assertThat(taskGroup1.getTitle()).isEqualTo("Context 1");
		Assertions.assertThat(taskGroup2.getTitle()).isEqualTo("Context 2");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				taskGroup1.setTitle("New Context 1");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(taskGroup1.getTitle()).isEqualTo("New Context 1");
		Assertions.assertThat(taskGroup2.getTitle()).isEqualTo("Context 2");

		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				taskGroup2.setTitle("New Context 2");
			}
		});
		robot().waitForIdle();
		Assertions.assertThat(taskGroup1.getTitle()).isEqualTo("New Context 1");
		Assertions.assertThat(taskGroup2.getTitle()).isEqualTo("New Context 2");
	}
}
