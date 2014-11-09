package moustachio.task_catalyst;

import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

//@author A0111921W
public class UITest extends GuiTest {
	public Parent getRootNode() {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource(
					"/fxml/userInterface.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}

	private void deleteAllTask(){
		type("del all");
		push(KeyCode.ENTER);
	}
	
	//check status message text
	@Test
	public void test1() {
		verifyThat("#statusMessage",
				hasText("Type something to begin adding a task.\nOther Commands: delete, edit, done, restore, redo, undo, #, find. Press CTRL+H for more details."));
	}
	
	//delete all task added previously
	@Test
    public void test2() {
		deleteAllTask();
		//check status message when deleting empty task list
		deleteAllTask();
        verifyThat("#statusMessage",
				hasText("There was/were no matching task(s) to delete."));
    }
	
	//test adding of floating task
	@Test
	public void test3() {
		type("Floating task");
		push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: Floating task"));
	}

	//test adding of single time task
	@Test
	public void test4() {
		type("task 5pm");
		push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: task today 5PM"));
	}

	//test adding of task with time range
	@Test
	public void test5() {
		type("task from 2pm to 3pm");
		push(KeyCode.ENTER);
		verifyThat("#statusMessage", hasText("Task successfully added: task from today 2PM to 3PM"));
	}

	//test adding of task with reserved time slots
	@Test
	public void test6() {
		type("task at 2pm or 3pm or 4pm");
		push(KeyCode.ENTER);
		verifyThat("#statusMessage", hasText("Task successfully added: task today 2PM, 3PM or 4PM"));
	}
}
