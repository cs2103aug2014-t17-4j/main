package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogicActualTest {

	private static final int NUM_OF_DEFAULT_HASHTAGS = 9;
	Logic logic;
	TaskManager taskManager;

	@Before
	public void setUp() throws Exception {
		logic = new LogicActual();
		logic.testMode();
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
		String userCommand = "Meet boss at MR5 at 21 Sep 5pm";
		Message message = logic.processCommand(userCommand);
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		// assertEquals("Task successfully added: " + userCommand,
		// message.getMessage());

		Task task = logic.getList().get(0);
		// assertEquals(userCommand, task.getDescription());
		assertEquals("Sun Sep 21 17:00:00 SGT 2014", task.getDateStart()
				.toString());
	}

	// Basic Add with End Date
	@Test
	public void addTc3() {
		String userCommand = "Meet boss at MR5 at 21 Sep 5pm to 6pm";
		Message message = logic.processCommand(userCommand);
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		// assertEquals("Task successfully added: " + userCommand,
		// message.getMessage());

		Task task = logic.getList().get(0);
		// assertEquals(userCommand, task.getDescription());
		assertEquals("Sun Sep 21 17:00:00 SGT 2014", task.getDateStart()
				.toString());
		assertEquals("Sun Sep 21 18:00:00 SGT 2014", task.getDateEnd()
				.toString());
	}

	// Two or more basic adds
	@Test
	public void addTc4() {
		String userCommand1 = "[25 Sep 6pm to 26 Sep 8pm] clients conference.";
		String userCommand2 = "Meet boss at MR5 from [21 Sep 5pm to 6pm]";

		Message message1 = logic.processCommand(userCommand1);
		Message message2 = logic.processCommand(userCommand2);

		assertEquals(Message.TYPE_SUCCESS, message1.getType());
		assertEquals(Message.TYPE_SUCCESS, message2.getType());

		Task task1 = logic.getList().get(0);
		assertEquals("25 Sep 6pm to 26 Sep 8pm clients conference.",
				task1.getDescription());
		Task task2 = logic.getList().get(1);
		assertEquals("Meet boss at MR5 from 21 Sep 5pm to 6pm",
				task2.getDescription());
	}

	// Empty string
	/* This is a boundary case for the ‘empty string’ partition */
	@Test
	public void addTc5() {
		Message message = logic.processCommand("");
		assertEquals(Message.TYPE_ERROR, message.getType());
		// assertEquals("Invalid Action Encountered", message.getMessage());
	}

	// Whitespace string
	/* This is a boundary case for the ‘empty string’ partition */
	@Test
	public void addTc6() {
		Message message = logic.processCommand(" ");
		assertEquals(Message.TYPE_ERROR, message.getType());
		// assertEquals("Invalid Action Encountered", message.getMessage());
	}

	// Null
	@Test
	public void addTc7() {
		Message message = logic.processCommand(null);
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals(
				"Type something to begin adding a task."
						+ "\nOther Commands: delete, edit, done, redo, undo, #, find. Press CTRL+H for more details.",
				message.getMessage());
	}

	// Test empty string
	/* This is a boundary case for the ‘empty string’ partition */
	@Test
	public void getMessageTypingTc1() {
		Message message = logic.getMessageTyping("");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Type something to begin adding a task."
						+ "\nOther Commands: delete, edit, done, redo, undo, #, find. Press CTRL+H for more details.",
				message.getMessage());
	}

	// Test whitespace string
	/* This is a boundary case for the ‘empty string’ partition */
	@Test
	public void getMessageTypingTc2() {
		Message message = logic.getMessageTyping(" ");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Type something to begin adding a task."
						+ "\nOther Commands: delete, edit, done, redo, undo, #, find. Press CTRL+H for more details.",
				message.getMessage());
	}

	// Test null
	@Test
	public void getMessageTypingTc3() {
		Message message = logic.getMessageTyping(null);
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Type something to begin adding a task."
						+ "\nOther Commands: delete, edit, done, redo, undo, #, find. Press CTRL+H for more details.",
				message.getMessage());
	}

	// Test command partial match
	@Test
	public void getMessageTypingTc4() {
		Message message = logic.getMessageTyping("e");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals("Do you mean \"edit\"?", message.getMessage());
	}

	// Test multiple command partial match
	/* This is a boundary case for the ‘no command’ partition */
	@Test
	public void getMessageTypingTc5() {
		Message message = logic.getMessageTyping("d");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals("Do you mean \"delete\", \"del\", \"done\"?",
				message.getMessage());
	}

	// Test edit command match with no parameters
	/* This is a boundary case for the ‘no parameter’ partition */
	@Test
	public void getMessageTypingTc6() {
		Message message = logic.getMessageTyping("edit");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Edit: Hit space or enter after typing a valid task number to continue.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Test edit command match with space
	/* This is a boundary case for the ‘no parameter’ partition */
	@Test
	public void getMessageTypingTc7() {
		Message message = logic.getMessageTyping("edit ");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Edit: Hit space or enter after typing a valid task number to continue.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Test edit command match with parameter
	/* This is a boundary case for the ‘has parameter’ partition */
	@Test
	public void getMessageTypingTc8() {
		logic.processCommand("item 1");
		Message message = logic.getMessageTyping("edit 2");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Edit: Hit space or enter after typing a valid task number to continue.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Test command match with non-matching parameter and space
	/* This is a boundary case for the ‘has parameter’ partition */
	@Test
	public void getMessageTypingTc9() {
		logic.processCommand("item 1");
		Message message = logic.getMessageTyping("edit 2 ");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Invalid task number specified.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Test command match with matching parameter and space
	/* This is a boundary case for the ‘has parameter’ partition */
	@Test
	public void getMessageTypingTc10() {
		logic.processCommand("item 1");
		Message message = logic.getMessageTyping("edit 1 ");
		assertEquals(Message.TYPE_AUTOCOMPLETE, message.getType());
		assertEquals("edit 1 item 1", message.getMessage());
	}

	// Test command match with matching parameter and double-space
	/* This is a non-boundary case for the ‘has parameter’ partition */
	@Test
	public void getMessageTypingTc11() {
		logic.processCommand("item 1");
		Message message = logic.getMessageTyping("edit 1  ");
		// assertEquals(Message.TYPE_AUTOCOMPLETE, message.getType());
		assertEquals("edit 1 item 1", message.getMessage());
	}

	// Test command match with matching parameter and double-space
	/* This is a boundary case for the ‘has parameter has content’ partition */
	@Test
	public void getMessageTypingTc12() {
		logic.processCommand("item 1");
		Message message = logic.getMessageTyping("edit 1 item 1");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"item 1\nEdit: Hit enter after making your changes.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Test command match with matching parameter and double-space
	/* This is a boundary case for the ‘has parameter has content’ partition */
	@Test
	public void getMessageTypingTc13() {
		logic.processCommand("item 1");
		Message message = logic.getMessageTyping("edit 2 item 1");
		assertEquals(Message.TYPE_HINT, message.getType());
		assertEquals(
				"Invalid task number specified.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Delete invalid index.
	@Test
	public void deleteTc1() {
		Message message = logic.processCommand("rm -1");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was/were no matching task(s) to delete.",
				message.getMessage());
	}

	// Delete invalid index.
	@Test
	public void deleteTc2() {
		logic.processCommand("Hello kitty!");
		Message message = logic.processCommand("rm 2");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was/were no matching task(s) to delete.",
				message.getMessage());
	}

	// Delete without parameters.
	@Test
	public void deleteTc3() {
		Message message = logic.processCommand("rm");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was/were no matching task(s) to delete.",
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

	// Delete all items.
	@Test
	public void deleteTc5() {
		logic.processCommand("Task 1!");
		logic.processCommand("Task 2!");
		Message message = logic.processCommand("rm all");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Successfully deleted 2 tasks.", message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Delete all items with space .
	@Test
	public void deleteTc6() {
		logic.processCommand("Task 1!");
		logic.processCommand("Task 2!");
		Message message = logic.processCommand("rm all    ");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Successfully deleted 2 tasks.", message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Delete all items space separated.
	@Test
	public void deleteTc7() {
		logic.processCommand("Task 1!");
		logic.processCommand("Task 2!");
		Message message = logic.processCommand("rm 1 2");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Successfully deleted 2 tasks.", message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Delete all items comma and space separated.
	@Test
	public void deleteTc8() {
		logic.processCommand("Task 1!");
		logic.processCommand("Task 2!");
		Message message = logic.processCommand("rm 1, 2");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Successfully deleted 2 tasks.", message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Delete all items comma and space separated.
	@Test
	public void deleteTc9() {
		logic.processCommand("Task 1!");
		logic.processCommand("Task 2!");
		Message message = logic.processCommand("rm 1,2");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Successfully deleted 2 tasks.", message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Complete invalid index.
	@Test
	public void doneTc1() {
		Message message = logic.processCommand("done -1");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was/were no matching task(s) to complete.",
				message.getMessage());
	}

	// Complete invalid index.
	@Test
	public void doneTc2() {
		logic.processCommand("Hello kitty!");
		Message message = logic.processCommand("done 2");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was/were no matching task(s) to complete.",
				message.getMessage());
	}

	// Complete without parameters.
	@Test
	public void doneTc3() {
		Message message = logic.processCommand("done");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals("There was/were no matching task(s) to complete.",
				message.getMessage());
	}

	// Complete one item.
	@Test
	public void doneTc4() {
		logic.processCommand("Hello kitty!");
		Message message = logic.processCommand("done 1");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Task successfully completed: Hello kitty!",
				message.getMessage());
	}

	// Edit non-existing item
	@Test
	public void editTc1() {
		Message message = logic.processCommand("edit 1 after!");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals(
				"Invalid task number specified.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Edit without parameters
	@Test
	public void editTc2() {
		Message message = logic.processCommand("edit");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals(
				"Invalid task number specified.\nSyntax: edit <task number>",
				message.getMessage());
	}

	// Edit existing task without parameters
	@Test
	public void editTc3() {
		logic.processCommand("hello kitty!");
		Message message = logic.processCommand("edit");
		assertEquals(Message.TYPE_ERROR, message.getType());
		assertEquals(
				"Invalid task number specified.\nSyntax: edit <task number>",
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
		List<String> defaultHashtags = logic.getHashtags();
		assertEquals("#all", defaultHashtags.get(0));
		assertEquals("#pri", defaultHashtags.get(1));
		assertEquals("#ovd", defaultHashtags.get(2));
		assertEquals("#tdy", defaultHashtags.get(3));
		assertEquals("#tmr", defaultHashtags.get(4));
		assertEquals("#upc", defaultHashtags.get(5));
		assertEquals("#smd", defaultHashtags.get(6));
		assertEquals("#olp", defaultHashtags.get(7));
		assertEquals("#dne", defaultHashtags.get(8));
	}

	// Get hashtags on empty list.
	@Test
	public void getHashtagsTc1() {
		List<String> userHashtags = logic.getHashtags();
		assertEquals(NUM_OF_DEFAULT_HASHTAGS + 0, userHashtags.size());
	}

	// Get hashtags on a list with no hashtags.
	@Test
	public void getHashtagsTc2() {
		logic.processCommand("hello");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(NUM_OF_DEFAULT_HASHTAGS + 0, userHashtags.size());
	}

	// Single hashtagged item in a list.
	@Test
	public void getHashtagsTc3() {
		logic.processCommand("Need to meet #boss for #client meeting.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(NUM_OF_DEFAULT_HASHTAGS + 2, userHashtags.size());
	}

	// Two hashtagged item, no overlaps.
	@Test
	public void getHashtagsTc4() {
		logic.processCommand("Need to meet #boss for #client meeting.");
		logic.processCommand("Go do some #fishing for #charity.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(NUM_OF_DEFAULT_HASHTAGS + 4, userHashtags.size());
	}

	// Two hashtagged item, with overlaps.
	@Test
	public void getHashtagsTc5() {
		logic.processCommand("Need to meet #boss for #client meeting.");
		logic.processCommand("Go do some #fishing for #client.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(NUM_OF_DEFAULT_HASHTAGS + 3, userHashtags.size());
	}

	// Numbered hashtags , with overlaps.
	@Test
	public void getHashtagsTc6() {
		logic.processCommand("Need to meet #boss1 for #boss2 meeting.");
		logic.processCommand("Go do some #boss2 for #boss3.");
		List<String> userHashtags = logic.getHashtags();
		assertEquals(NUM_OF_DEFAULT_HASHTAGS + 3, userHashtags.size());
	}

	// Non-existent hashtags.
	@Test
	public void hasHashtagTc1() {
		logic.processCommand("Not hashtagged");
		assertEquals(false, logic.getList().get(0).hasHashtag("hashtag"));
	}

	// Hashtag in front.
	@Test
	public void hasHashtagTc2() {
		logic.processCommand("#hashtag in front");
		assertEquals(true, logic.getList().get(0).hasHashtag("hashtag"));
	}

	// Hashtag behind.
	@Test
	public void hasHashtagTc3() {
		logic.processCommand("behind #hashtag");
		assertEquals(true, logic.getList().get(0).hasHashtag("hashtag"));
	}

	// Hashtagged, but search for non-existent.
	@Test
	public void hasHashtagTc4() {
		logic.processCommand("#hashtag in front");
		assertEquals(false, logic.getList().get(0).hasHashtag("hashtag2"));
	}

	// Ignore punctuations at the end.
	@Test
	public void hasHashtagTc5() {
		logic.processCommand("Punctuations ending #hashtag.. ");
		assertEquals(true, logic.getList().get(0).hasHashtag("hashtag"));
	}

	// Accept punctuations between.
	@Test
	public void hasHashtagTc6() {
		logic.processCommand("Punctuations ending #hash.tag.. ");
		assertEquals(true, logic.getList().get(0).hasHashtag("hash.tag"));
	}

	// Priority Recognition - Not Priority.
	@Test
	public void priorityTc1() {
		logic.processCommand("task 1");

		List<Task> tasks = logic.getList();

		assertEquals(false, tasks.get(0).isPriority());
	}

	// Priority Recognition - Priority.
	@Test
	public void priorityTc2() {
		logic.processCommand("task 1 #pri");

		List<Task> tasks = logic.getList();

		assertEquals(true, tasks.get(0).isPriority());
	}

	// Search for non-existent item
	@Test
	public void searchTc1() {
		Message message = logic.processCommand("search hello");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Displaying search: hello.", message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Search for one matching item
	@Test
	public void searchTc2() {
		logic.processCommand("apple banana");
		logic.processCommand("apple papaya");
		Message message = logic.processCommand("search banana");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Displaying search: banana.", message.getMessage());
		assertEquals(1, logic.getList().size());
	}

	// Search for two or more matching item
	@Test
	public void searchTc3() {
		logic.processCommand("apple banana");
		logic.processCommand("apple papaya");
		Message message = logic.processCommand("search apple");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Displaying search: apple.", message.getMessage());
		assertEquals(2, logic.getList().size());
	}

	// Undo nothing.
	@Test
	public void undoTc1() {
		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("There is nothing to undo.", message.getMessage());
	}

	// Undo add, and then redo.
	@Test
	public void undoTc2() {
		logic.processCommand("this is the first item");
		assertEquals(1, logic.getList().size());

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Undo: Task successfully removed: this is the first item",
				message.getMessage());
		assertEquals(0, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Redo: Task successfully added: this is the first item",
				message.getMessage());
		assertEquals(1, logic.getList().size());
	}

	// Undo multiple add, and then redo multiple add.
	@Test
	public void undoTc3() {
		logic.processCommand("this is the first item");
		logic.processCommand("this is the second item");
		assertEquals(2, logic.getList().size());

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Undo: Task successfully removed: this is the second item",
				message.getMessage());
		assertEquals(1, logic.getList().size());

		message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Undo: Task successfully removed: this is the first item",
				message.getMessage());
		assertEquals(0, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Redo: Task successfully added: this is the first item",
				message.getMessage());
		assertEquals(1, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Redo: Task successfully added: this is the second item",
				message.getMessage());
		assertEquals(2, logic.getList().size());
	}

	// Undo multiple deletes, and then redo multiple deletes.
	@Test
	public void undoTc4() {
		logic.processCommand("this is the first item");
		logic.processCommand("this is the second item");
		logic.processCommand("delete 1");
		logic.processCommand("delete 1");
		assertEquals(0, logic.getList().size());

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Undo: Task successfully restored: this is the second item",
				message.getMessage());
		assertEquals(1, logic.getList().size());

		message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Undo: Task successfully restored: this is the first item",
				message.getMessage());
		assertEquals(2, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Redo: Task successfully deleted: this is the first item",
				message.getMessage());
		assertEquals(1, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Redo: Task successfully deleted: this is the second item",
				message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Undo multiple completes, then redo multiple completes.
	@Test
	public void undoTc5() {
		logic.processCommand("this is the first item");
		logic.processCommand("this is the second item");
		logic.processCommand("done 1");
		logic.processCommand("done 1");
		assertEquals(0, logic.getList().size());

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Undo: Task successfully restored: this is the second item",
				message.getMessage());
		assertEquals(1, logic.getList().size());

		message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Undo: Task successfully restored: this is the first item",
				message.getMessage());
		assertEquals(2, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Redo: Task successfully completed: this is the first item",
				message.getMessage());
		assertEquals(1, logic.getList().size());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals(
				"Redo: Task successfully completed: this is the second item",
				message.getMessage());
		assertEquals(0, logic.getList().size());
	}

	// Undo single edit.
	@Test
	public void undoTc6() {
		logic.processCommand("before");
		logic.processCommand("edit 1 after");

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Undo: Task successfully restored: before",
				message.getMessage());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Redo: Task successfully edited: after",
				message.getMessage());
	}

	// Ensure redos are cleared after doing something.
	@Test
	public void undoTc7() {
		logic.processCommand("before");
		logic.processCommand("edit 1 after");

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Undo: Task successfully restored: before",
				message.getMessage());

		logic.processCommand("something else");

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("There is nothing to redo.", message.getMessage());
	}

	// Test that invalid commands are not added to undo list.
	@Test
	public void undoTc8() {
		logic.processCommand("before");
		logic.processCommand("edit 1 after");
		logic.processCommand("edit 1");
		logic.processCommand("delete");

		Message message = logic.processCommand("undo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Undo: Task successfully restored: before",
				message.getMessage());

		message = logic.processCommand("redo");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Redo: Task successfully edited: after",
				message.getMessage());
	}

	// Undo for delete multiple / all.
	@Test
	public void undoTc9() {
		logic.processCommand("Task 1!");
		logic.processCommand("Task 2!");
		logic.processCommand("Task 3!");
		Message message = logic.processCommand("rm 1, 3");
		assertEquals(Message.TYPE_SUCCESS, message.getType());
		assertEquals("Successfully deleted 2 tasks.", message.getMessage());
		List<Task> tasks = logic.getList();
		assertEquals(1, tasks.size());
		assertEquals("Task 2!", tasks.get(0).getDescription());
		logic.processCommand("undo");
		tasks = logic.getList();
		assertEquals(3, tasks.size());
		assertEquals("Task 2!", tasks.get(0).getDescription());
		assertEquals("Task 1!", tasks.get(1).getDescription());
		assertEquals("Task 3!", tasks.get(2).getDescription());
	}

	// Test that display is automatically switched to added task if not in
	// display.
	@Test
	public void autoswitchTc1() {
		logic.processCommand("Hello boss1");
		logic.processCommand("Hello boss2");
		logic.processCommand("search boss1");
		assertEquals(1, logic.getList().size());
		logic.processCommand("Hello boss3");
		assertEquals(3, logic.getList().size());
	}

	// Test autoswitching functionality for edit as well.
	@Test
	public void autoswitchTc2() {
		logic.processCommand("Hello boss1");
		logic.processCommand("Hello boss2");
		logic.processCommand("search boss1");
		assertEquals(1, logic.getList().size());
		logic.processCommand("edit 1 boss3");
		assertEquals(2, logic.getList().size());
	}

	// Ensure that switching occurs only when list is empty during remove.
	@Test
	public void autoswitchTc3() {
		logic.processCommand("1");
		logic.processCommand("2");
		logic.processCommand("2");
		assertEquals(3, logic.getList().size());
		logic.processCommand("search 2");
		assertEquals(2, logic.getList().size());
		logic.processCommand("rm 1");
		assertEquals(1, logic.getList().size());
		logic.processCommand("rm 1");
		assertEquals(1, logic.getList().size());
	}

	// Test if #all is highlighted by default.
	@Test
	public void highlightHashtagTc1() {
		assertEquals(0, logic.getHashtagSelected());
	}

	// Test if #tdy is highlighted when #tdy is called.
	@Test
	public void highlightHashtagTc2() {
		logic.processCommand("#tdy");
		assertEquals(3, logic.getHashtagSelected());
	}

	// Test if search is added and highlighted.
	@Test
	public void highlightHashtagTc3() {
		logic.processCommand("#tdy");
		logic.processCommand("search fish");
		assertEquals("search fish", logic.getHashtags().get(0));
		assertEquals(0, logic.getHashtagSelected());
	}
}
