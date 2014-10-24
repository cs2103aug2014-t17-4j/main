package moustachio.task_catalyst;

import java.util.List;

public interface TaskManager {

	public List<String> getHashtags();

	public List<Task> getDisplayList();

	public List<Task> getList();

	public boolean completeTask(Task task);

	public boolean uncompleteTask(Task task);

	public boolean addTask(Task task);

	public boolean editTask(Task target, Task replacement);

	public boolean removeTask(Task task);

	public Task removeDisplayTask(int taskNumber);

	public Task getDisplayTask(int taskNumber);

	public void setDisplayMode(DisplayMode TYPE);

	public void setDisplayKeyword(String keyword);

	public List<Highlight> getHashtagHighlight();

	public List<Highlight> getTasksHighlight();

	public boolean addHashtagHighlight(int type, String hashtag);

	public void clearHashtagHighlights();

	public boolean addTaskHighlight(int type, Task task);

	public void clearTaskHighlights();

	public void testMode();
}
