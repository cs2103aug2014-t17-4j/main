package moustachio.task_catalyst;

import java.util.List;

//@author A0111890
public interface TaskManager {
	public void testMode();

	public List<String> getHashtags();

	public String[] getDefaultHashtags();

	public int getHashtagSelected();

	public List<Integer> getTasksSelected();

	public List<Task> getList();

	public List<Task> getDisplayList();

	public void setDisplayModeKeyword(DisplayMode MODE, String keyword);

	public Task getDisplayTask(int taskNumber);

	public Task removeDisplayTask(int taskNumber);

	public boolean addTask(Task task);

	public int addTasks(List<Task> tasks);

	public boolean removeTask(Task task);

	public int removeTasks(List<Task> tasks);

	public boolean editTask(Task target, Task replacement);

	public boolean completeTask(Task task);

	public int completeTasks(List<Task> tasks);

	public boolean uncompleteTask(Task task);

	public int uncompleteTasks(List<Task> tasks);
}
