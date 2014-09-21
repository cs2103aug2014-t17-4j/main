package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogicActualTest {

	Logic logic;

	@Before
	public void setUp() throws Exception {
		logic = new LogicActual();
	}

	@After
	public void tearDown() throws Exception {
	}

	// Basic Add
	@Test
	public void addTc1() {
		Message message = logic.processCommand("Meet boss at MR5");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Task successfully added: Meet boss at MR5",
				message.getMessage());
		assertEquals("Meet boss at MR5", logic.getList().get(0)
				.getDescription());
	}

	// Basic Add with Start Date
	@Test
	public void addTc2() {
		String userCommand = "Meet boss at MR5 at [21 Sep 5pm]";
		Message message = logic.processCommand(userCommand);
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Task successfully added: " + userCommand,
				message.getMessage());

		Task task = logic.getList().get(0);
		assertEquals(userCommand, task.getDescription());
		assertEquals("2014-09-21T17:00", task.getDateStart().toString());
	}

	// Basic Add with End Date
	@Test
	public void addTc3() {
		String userCommand = "Meet boss at MR5 at [21 Sep 5pm to 6pm]";
		Message message = logic.processCommand(userCommand);
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Task successfully added: " + userCommand,
				message.getMessage());

		Task task = logic.getList().get(0);
		assertEquals(userCommand, task.getDescription());
		assertEquals("2014-09-21T17:00", task.getDateStart().toString());
		assertEquals("2014-09-21T18:00", task.getDateEnd().toString());
	}

	// Two or more basic adds
	@Test
	public void addTc4() {
		String userCommand2 = "Meet boss at MR5 at [21 Sep 5pm to 6pm]";
		String userCommand1 = "[25 Sep 6pm to 26 Sep 8pm] clients conference.";

		Message message1 = logic.processCommand(userCommand1);
		Message message2 = logic.processCommand(userCommand2);

		assertEquals(Message.TYPE_SUCCESS, message1.getType());
		assertEquals(Message.TYPE_SUCCESS, message2.getType());

		Task task1 = logic.getList().get(0);
		assertEquals(userCommand1, task1.getDescription());
		Task task2 = logic.getList().get(1);
		assertEquals(userCommand2, task2.getDescription());
	}

	// Empty string
	@Test
	public void addTc5() {
		Message message = logic.processCommand("");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("Invalid Action Encountered", message.getMessage());
	}

	// Whitespace string
	@Test
	public void addTc6() {
		Message message = logic.processCommand(" ");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("Invalid Action Encountered", message.getMessage());
	}

	// Null
	@Test
	public void addTc7() {
		Message message = logic.processCommand(null);
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("Invalid Action Encountered", message.getMessage());
	}

	// Delete invalid index.
	@Test
	public void deleteTc1() {
		Message message = logic.processCommand("rm -1");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was an error deleting the task.",
				message.getMessage());
	}

	// Delete invalid index.
	@Test
	public void deleteTc2() {
		logic.processCommand("Hello kitty!");
		Message message = logic.processCommand("rm 2");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was an error deleting the task.",
				message.getMessage());
	}

	// Delete without specification.
	@Test
	public void deleteTc3() {
		Message message = logic.processCommand("rm");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was an error deleting the task.",
				message.getMessage());
	}

	// Delete one item.
	@Test
	public void deleteTc4() {
		logic.processCommand("Hello kitty!");
		Message message = logic.processCommand("rm 1");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Task successfully deleted: Hello kitty!",
				message.getMessage());
	}

	// Edit non-existing item
	@Test
	public void editTc1() {
		Message message = logic.processCommand("edit 1 after!");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was an error editing the task.",
				message.getMessage());
	}

	// Edit without parameters
	@Test
	public void editTc2() {
		Message message = logic.processCommand("edit");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was an error editing the task.",
				message.getMessage());
	}

	// Edit existing task without parameters
	@Test
	public void editTc3() {
		logic.processCommand("hello kitty!");
		Message message = logic.processCommand("edit");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was an error editing the task.",
				message.getMessage());
	}

	// Edit existing task with parameters
	@Test
	public void editTc4() {
		logic.processCommand("before1!");
		logic.processCommand("before2!");
		Message message = logic.processCommand("edit 2 after!");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Task successfully edited: after!", message.getMessage());
	}

	// Get default hashtags
	@Test
	public void getDefaultHashtagsTc1() {
		List<String> defaultHashtags = logic.getDefaultHashtags();
		assertEquals("#all", defaultHashtags.get(0));
		assertEquals("#pri", defaultHashtags.get(1));
		assertEquals("#tdy", defaultHashtags.get(2));
		assertEquals("#tmr", defaultHashtags.get(3));
		assertEquals("#upc", defaultHashtags.get(4));
		assertEquals("#smd", defaultHashtags.get(5));
		assertEquals("#dne", defaultHashtags.get(6));
	}

	// Get hashtags on empty list.
	@Test
	public void getHashtagsTc1() {
		List<String> userHashtags = logic.getHashtags();
		assertEquals(0, userHashtags.size());
	}

	// Get hashtags on a list with no hashtags.
	@Test
	public void getHashtagsTc2() {
		logic.processCommand("hello");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(0, userHashtags.size());
	}

	// Single hashtagged item in a list.
	@Test
	public void getHashtagsTc3() {
		logic.processCommand("Need to meet #boss for #client meeting.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(2, userHashtags.size());
	}

	// Two hashtagged item, no overlaps.
	@Test
	public void getHashtagsTc4() {
		logic.processCommand("Need to meet #boss for #client meeting.");
		logic.processCommand("Go do some #fishing for #charity.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(4, userHashtags.size());
	}

	// Two hashtagged item, with overlaps.
	@Test
	public void getHashtagsTc5() {
		logic.processCommand("Need to meet #boss for #client meeting.");
		logic.processCommand("Go do some #fishing for #client.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(3, userHashtags.size());
	}

	// Numbered hashtags , with overlaps.
	@Test
	public void getHashtagsTc6() {
		logic.processCommand("Need to meet #boss1 for #boss2 meeting.");
		logic.processCommand("Go do some #boss2 for #boss3.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(3, userHashtags.size());
	}
}
