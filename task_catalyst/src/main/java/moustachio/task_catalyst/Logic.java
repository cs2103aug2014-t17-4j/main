package moustachio.task_catalyst;

import java.util.List;

//@author A0111890
public interface Logic {
	public void testMode();

	public Message processCommand(String userCommand);

	public Message getMessageTyping(String userCommand);

	public List<String> getHashtags();

	public List<Task> getList();

	public int getHashtagSelected();

	public List<Integer> getTasksSelected();
}