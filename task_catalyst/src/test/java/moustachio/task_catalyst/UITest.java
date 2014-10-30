package moustachio.task_catalyst;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;
import static org.loadui.testfx.controls.TableViews.containsCell;
import static org.loadui.testfx.controls.TableViews.numberOfRowsIn;
import javafx.scene.input.KeyCode;

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

	@Test
	public void test() {
		type("Meet boss at 1pm");
		verifyThat(".text-field", hasText("Meet boss at 1pm"));
	}

	@Test
	public void test2() {
		type("Meet boss at 1pm");
		push(KeyCode.ENTER);
		verifyThat("#statusMessage",
				hasText("Task successfully added: Meet boss today 1PM"));
	}

	// This is a partial command partition
	@Test
	public void test3() {
		type("edi");
		verifyThat("#statusMessage", hasText("Do you mean \"edit\"?"));
	}

	// This is a partial command partition
	@Test
	public void test4() {
		type("edit");
		verifyThat(
				"#statusMessage",
				hasText("Edit: Hit space or enter after typing a valid task number to continue.\nSyntax: edit <task number>"));

		type(" ");
		verifyThat(
				"#statusMessage",
				hasText("Edit: Hit space or enter after typing a valid task number to continue.\nSyntax: edit <task number>"));

		type("1");
		verifyThat(
				"#statusMessage",
				hasText("Edit: Hit space or enter after typing a valid task number to continue.\nSyntax: edit <task number>"));
	}

}
