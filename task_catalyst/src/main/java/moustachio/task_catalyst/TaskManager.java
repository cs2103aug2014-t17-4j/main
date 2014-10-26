package moustachio.task_catalyst;

import java.util.List;

public interface TaskManager {

	public List<String> getHashtags();

	public List<Task> getDisplayList();

	public List<Task> getList();

	public boolean completeTask(Task task);

	public boolean uncompleteTask(Task task);

	public boolean addTask(Task task);

	public int addTasks(List<Task> tasks);

	public boolean removeTask(Task task);

	public int removeTasks(List<Task> tasks);

	public boolean editTask(Task target, Task replacement);

	public Task removeDisplayTask(int taskNumber);

	public Task getDisplayTask(int taskNumber);

	public void setDisplayMode(DisplayMode TYPE);

	public void setDisplayKeyword(String keyword);

	public int getHashtagSelected();
	
	public int getTaskSelected();

	public void testMode();
}
