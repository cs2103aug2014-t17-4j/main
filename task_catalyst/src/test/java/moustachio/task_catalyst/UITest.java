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
import static org.loadui.testfx.controls.TableViews.containsCell;
import javafx.scene.input.KeyCode;
public class UITest extends GuiTest {
	public Parent getRootNode() {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("userInterface.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}

	@Test
	public void test() {
		type("Meet boss at 1pm");
		push(KeyCode.ENTER);
		verifyThat("#taskTable", containsCell("Meet boss at 1pm"));
	}

}
