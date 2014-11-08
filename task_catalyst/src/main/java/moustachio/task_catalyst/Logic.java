package moustachio.task_catalyst;

import java.util.List;

//@author A0111890
/**
 * Logic is the main link between the front-end GUI and the back-end classes.
 * Logic provides functionality for parsing commands, providing hints, providing
 * the displaying list as well as the list of hashtags and tasks to highlight.
 */
public interface Logic {
	public void testMode();

	public Message processCommand(String userCommand);

	public Message getMessageTyping(String userCommand);

	public List<String> getHashtags();

	public List<Task> getList();

	public int getHashtagSelected();

	public List<Integer> getTasksSelected();
}