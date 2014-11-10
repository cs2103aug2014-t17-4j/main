package moustachio.task_catalyst;

import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;

import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

//@author A0111921W - reused from https://github.com/TestFX/TestFX/issues/57
public class UITest {
	private static GuiTest controller;
	private static TaskCatalyst tc;

	@BeforeClass
	public static void setUpClass() {
		FXTestUtils.launchApp(TaskCatalyst.class);

		// here is that closure I talked about above, you instantiate the
		// getRootNode abstract method
		// which requires you to return a 'parent' object, luckily for us,
		// getRoot() gives a parent!
		// getRoot() is available from ALL Node objects, which makes it easy.
		controller = new GuiTest() {
			@Override
			protected Parent getRootNode() {
				return tc.getStage().getScene().getRoot();
			}
		};

	}

	private void deleteAllTask() {
		controller.type("del all");
		controller.push(KeyCode.ENTER);
	}

	// test adding of floating task
	@Test
	public void test1() {
		controller.type("Floating task");
		controller.push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: Floating task"));
	}

	// test adding of single time task
	@Test
	public void test2() {
		controller.type("task 5pm");
		controller.push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: task today 5PM"));
	}

	// test adding of task with time range
	@Test
	public void test3() {
		controller.type("task from 2pm to 3pm");
		controller.push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: task from today 2PM to 3PM"));
	}

	// test adding of task with reserved time slots
	@Test
	public void test4() {
		controller.type("task at 2pm or 3pm or 4pm");
		controller.push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: task today 2PM, 3PM or 4PM"));
	}

	// delete all task added previously
	@Test
	public void test5() {
		deleteAllTask();
		// check status message when deleting empty task list
		deleteAllTask();
		verifyThat("#statusMessage",
				hasText("There was/were no matching task(s) to delete."));
	}
	
}
