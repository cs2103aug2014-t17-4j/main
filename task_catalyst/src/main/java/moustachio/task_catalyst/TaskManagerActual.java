package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TaskManagerActual implements TaskManager {

	private static final String[] DEFAULT_HASHTAGS = { "#all", "#pri", "#tdy",
			"#tmr", "#upc", "#smd", "#dne" };
	private static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayMode.HASHTAG;
	private static final String DEFAULT_DISPLAY_KEYWORD = "all";
	private static final String DEFAULT_FILE_NAME = "tasks.txt";

	private Storage storage;
	private ListProcessor listProcessor;

	private DisplayMode displayMode;
	private String displayKeyword;

	private List<Task> tasks;
	private List<Task> displayList;

	private static TaskManagerActual instance;

	public static TaskManagerActual getInstance() {
		if (instance == null) {
			instance = new TaskManagerActual();
		}
		return instance;
	}

	private TaskManagerActual() {
		storage = new StorageActual();
		listProcessor = new ListProcessorActual();
		displayMode = DEFAULT_DISPLAY_MODE;
		displayKeyword = DEFAULT_DISPLAY_KEYWORD;
		tasks = storage.loadTasks(DEFAULT_FILE_NAME);
		refreshDisplayList();
	}
	
	public void testMode() {
		storage = new StorageStub();
		//listProcessor = new ListProcessorStub();
		displayMode = DEFAULT_DISPLAY_MODE;
		displayKeyword = DEFAULT_DISPLAY_KEYWORD;
		tasks.clear();
		refreshDisplayList();
	}

	@Override
	public List<String> getDefaultHashtags() {
		List<String> defaultHashtagsList = new ArrayList<String>();
		for (String hashtag : DEFAULT_HASHTAGS) {
			defaultHashtagsList.add(hashtag);
		}
		return defaultHashtagsList;
	}

	@Override
	public List<String> getHashtags() {
		SortedSet<String> allHashtagsSet = new TreeSet<String>();
		for (Task task : tasks) {
			List<String> taskHashtags = task.getHashtags();
			allHashtagsSet.addAll(taskHashtags);
		}
		List<String> allHashtagsList = new ArrayList<String>(allHashtagsSet);
		return allHashtagsList;
	}

	@Override
	public List<Task> getDisplayList() {
		return displayList;
	}

	@Override
	public List<Task> getList() {
		return tasks;
	}

	@Override
	public boolean addTask(Task task) {
		boolean isAdded = tasks.add(task);
		boolean isSaved = false;
		if (isAdded) {
			isSaved = save();
		}
		boolean isSuccess = isAdded && isSaved;
		if (isSuccess) {
			refreshDisplayList();
		}
		return isSuccess;
	}

	@Override
	public boolean editTask(Task target, Task replacement) {
		boolean isRemoved = removeTask(target);
		boolean isAdded = false;
		if (isRemoved) {
			isAdded = addTask(replacement);
		}
		boolean isSuccess = isRemoved && isAdded;
		if (isSuccess) {
			refreshDisplayList();
		}
		return isSuccess;
	}

	@Override
	public boolean removeTask(Task task) {
		boolean isRemoved = tasks.remove(task);
		boolean isSaved = false;
		if (isRemoved) {
			isSaved = save();
		}
		boolean isSuccess = isRemoved && isSaved;
		if (isSuccess) {
			refreshDisplayList();
		}
		return isSuccess;
	}

	@Override
	public boolean completeTask(Task task) {
		task.setDone(true);
		boolean isSaved = save();
		boolean isDone = task.isDone();
		boolean isSuccess = isDone && isSaved;
		if (isSuccess) {
			refreshDisplayList();
		}
		return isSuccess;
	}

	@Override
	public boolean uncompleteTask(Task task) {
		task.setDone(false);
		boolean isSaved = save();
		boolean isUndone = !task.isDone();
		boolean isSuccess = isUndone && isSaved;
		if (isSuccess) {
			refreshDisplayList();
		}
		return isSuccess;
	}

	@Override
	public Task removeDisplayTask(int taskNumber) {
		Task task = null;
		boolean isFound = false;
		boolean isRemoved = false;
		task = getDisplayTask(taskNumber);
		isFound = task != null;
		isRemoved = removeTask(task);
		boolean isSuccess = isRemoved && isFound;
		if (isSuccess) {
			refreshDisplayList();
		}
		return task;
	}

	@Override
	public Task getDisplayTask(int taskNumber) {
		Task task;
		try {
			task = displayList.get(taskNumber - 1);
		} catch (Exception e) {
			task = null;
		}
		return task;
	}

	@Override
	public void setDisplayMode(DisplayMode TYPE) {
		displayMode = TYPE;
	}

	@Override
	public void setDisplayKeyword(String keyword) {
		displayKeyword = keyword;
		refreshDisplayList();
	}

	private void refreshDisplayList() {
		switch (displayMode) {
		case HASHTAG:
			displayList = listProcessor.searchByHashtag(tasks, displayKeyword);
			break;
		case SEARCH:
			displayList = listProcessor.searchByKeyword(tasks, displayKeyword);
			break;
		}
		displayList = listProcessor.sortByDate(displayList);
	}

	private boolean save() {
		boolean success;
		success = storage.saveTasks(tasks, DEFAULT_FILE_NAME);
		return success;
	}
}
