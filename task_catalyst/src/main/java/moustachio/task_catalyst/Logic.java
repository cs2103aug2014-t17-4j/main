package moustachio.task_catalyst;

import java.util.List;

public interface Logic {
	public Message processCommand(String userCommand);

	public List<String> getHashtags();

	public Message getMessageTyping(String userCommand);

	public List<Task> getList();

	public List<Highlight> getHashtagHighlight();

	public List<Highlight> getTasksHighlight();

	public void testMode();
}