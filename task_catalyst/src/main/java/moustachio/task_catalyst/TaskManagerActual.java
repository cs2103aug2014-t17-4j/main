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

	private List<Task> taskList;
	private List<Task> displayList;
	private List<String> hashtagList;

	private List<Highlight> hashtagHighlights;
	private List<Highlight> taskHighlights;

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
		taskList = storage.loadTasks(DEFAULT_FILE_NAME);
		hashtagList = new ArrayList<String>();
		hashtagHighlights = new ArrayList<Highlight>();
		taskHighlights = new ArrayList<Highlight>();
		refreshLists();
	}

	public void testMode() {
		storage = new StorageStub();
		// listProcessor = new ListProcessorStub();
		displayMode = DEFAULT_DISPLAY_MODE;
		displayKeyword = DEFAULT_DISPLAY_KEYWORD;
		hashtagList = new ArrayList<String>();
		clearLists();
		refreshLists();
	}

	@Override
	public List<String> getHashtags() {
		return hashtagList;
	}

	@Override
	public List<Task> getDisplayList() {
		return displayList;
	}

	@Override
	public List<Task> getList() {
		return taskList;
	}

	@Override
	public boolean addTask(Task task) {
		boolean isAdded = taskList.add(task);
		boolean isSaved = false;
		if (isAdded) {
			isSaved = saveTasks();
		}
		boolean isSuccess = isAdded && isSaved;
		if (isSuccess) {
			refreshLists();
			displayAutoswitchToTask(task);
		}
		addTaskHighlight(Highlight.TYPE_TASK_LAST_ADDED, task);
		return isSuccess;
	}

	@Override
	public int addTasks(List<Task> tasks) {

		int numberAdded = 0;

		boolean isAdded = false;
		for (Task task : tasks) {
			isAdded = taskList.add(task);
			if (!isAdded) {
				break;
			}
			numberAdded++;
		}

		boolean isSaved = false;
		if (isAdded) {
			isSaved = saveTasks();
		} else {
			tasks = storage.loadTasks(DEFAULT_FILE_NAME);
		}

		boolean isSuccess = isAdded && isSaved;
		if (isSuccess) {
			refreshLists();
			for (Task task : tasks) {
				displayAutoswitchToTask(task);
				addTaskHighlight(Highlight.TYPE_TASK_LAST_ADDED, task);
			}
		}

		return numberAdded;
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
			refreshLists();
		}
		return isSuccess;
	}

	@Override
	public int removeTasks(List<Task> tasks) {

		int numberRemoved = 0;

		boolean isRemoved = false;
		for (Task task : tasks) {
			isRemoved = taskList.remove(task);
			if (!isRemoved) {
				break;
			}
			numberRemoved++;
		}

		boolean isSaved = false;
		if (isRemoved) {
			isSaved = saveTasks();
		} else {
			tasks = storage.loadTasks(DEFAULT_FILE_NAME);
		}

		boolean isSuccess = isRemoved && isSaved;
		if (isSuccess) {
			refreshLists();
			displayAutoswitchToTask(null);
		}

		return numberRemoved;
	}

	@Override
	public boolean removeTask(Task task) {
		boolean isRemoved = taskList.remove(task);
		boolean isSaved = false;
		if (isRemoved) {
			isSaved = saveTasks();
		}
		boolean isSuccess = isRemoved && isSaved;
		if (isSuccess) {
			refreshLists();
		}
		displayAutoswitchToTask(task);
		return isSuccess;
	}

	@Override
	public boolean completeTask(Task task) {
		task.setDone(true);
		boolean isSaved = saveTasks();
		boolean isDone = task.isDone();
		boolean isSuccess = isDone && isSaved;
		if (isSuccess) {
			refreshLists();
		}
		return isSuccess;
	}

	@Override
	public boolean uncompleteTask(Task task) {
		task.setDone(false);
		boolean isSaved = saveTasks();
		boolean isUndone = !task.isDone();
		boolean isSuccess = isUndone && isSaved;
		if (isSuccess) {
			refreshLists();
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
			refreshLists();
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
		refreshLists();
	}

	@Override
	public List<Highlight> getHashtagHighlight() {
		return hashtagHighlights;
	}

	@Override
	public List<Highlight> getTasksHighlight() {
		return taskHighlights;
	}

	@Override
	public boolean addHashtagHighlight(int type, String hashtag) {
		int hashtagIndex = hashtagList.indexOf(hashtag);
		boolean success;
		boolean isFound = hashtagIndex >= 0;
		if (isFound) {
			Highlight highlight = new Highlight(type, hashtagIndex);
			hashtagHighlights.add(highlight);
			success = true;
		} else {
			success = false;
		}
		return success;
	}

	@Override
	public boolean addTaskHighlight(int type, Task task) {
		int taskIndex = taskList.indexOf(task);
		boolean success;
		boolean isFound = taskIndex >= 0;
		if (isFound) {
			Highlight highlight = new Highlight(type, taskIndex);
			taskHighlights.add(highlight);
			success = true;
		} else {
			success = false;
		}
		return success;
	}

	@Override
	public void clearHashtagHighlights() {
		hashtagHighlights.clear();
	}

	@Override
	public void clearTaskHighlights() {
		taskHighlights.clear();
	}

	private void clearLists() {
		hashtagList.clear();
		taskList.clear();
	}

	private void refreshLists() {
		refreshDisplayList();
		refreshHashtagList();
	}

	private void refreshDisplayList() {
		switch (displayMode) {
		case HASHTAG:
			displayList = listProcessor.searchByHashtag(taskList,
					displayKeyword);
			break;
		case SEARCH:
			displayList = listProcessor.searchByKeyword(taskList,
					displayKeyword);
			break;
		}
		displayList = listProcessor.sortByDate(displayList);

		clearTaskHighlights();

		highlightAllPriority();

		// Add absolute overlap code here?
	}

	private void refreshHashtagList() {

		List<String> defaultHashtags = generateDefaultHashtags();
		List<String> customHashtags = generateCustomHashtags();

		hashtagList.clear();
		hashtagList.addAll(defaultHashtags);
		hashtagList.addAll(customHashtags);

		clearHashtagHighlights();
		highlightAllDefaultHashtags();

		if (displayMode == DisplayMode.HASHTAG) {
			String hashtagTerm = "#" + displayKeyword;
			boolean isValidHashtag = hashtagList.contains(hashtagTerm);
			if (!isValidHashtag) {
				hashtagList.add(hashtagTerm);
				addHashtagHighlight(Highlight.TYPE_HASHTAG_INVALID, hashtagTerm);
			} else {
				addHashtagHighlight(Highlight.TYPE_HASHTAG_SELECTED,
						hashtagTerm);
			}
		} else if (displayMode == DisplayMode.SEARCH) {
			String searchTerm = "search " + displayKeyword;
			hashtagList.add(0, searchTerm);
			addHashtagHighlight(Highlight.TYPE_SEARCH, searchTerm);
		}
	}

	private List<String> generateDefaultHashtags() {
		List<String> defaultHashtags = new ArrayList<String>();
		for (String hashtag : DEFAULT_HASHTAGS) {
			defaultHashtags.add(hashtag);
		}
		return defaultHashtags;
	}

	private List<String> generateCustomHashtags() {
		SortedSet<String> allHashtagsSet = new TreeSet<String>();
		for (Task task : taskList) {
			List<String> taskHashtags = task.getHashtags();
			allHashtagsSet.addAll(taskHashtags);
		}
		List<String> customHashtags = new ArrayList<String>(allHashtagsSet);
		return customHashtags;
	}

	private void displayAutoswitchToTask(Task task) {
		boolean isTaskDisplayed = displayList.contains(task);
		boolean isListEmpty = displayList.isEmpty();
		boolean isNeedAutoswitch = !isTaskDisplayed || isListEmpty;
		if (isNeedAutoswitch) {
			setDisplayMode(DEFAULT_DISPLAY_MODE);
			setDisplayKeyword(DEFAULT_DISPLAY_KEYWORD);
			refreshLists();
		}
	}

	private void highlightAllPriority() {
		for (Task task : displayList) {
			if (task.isPriority()) {
				addTaskHighlight(Highlight.TYPE_TASK_PRIORITY, task);
			}
		}
	}

	private void highlightAllDefaultHashtags() {
		for (String defaultHashtag : DEFAULT_HASHTAGS) {
			addHashtagHighlight(Highlight.TYPE_HASHTAG_DEFAULT, defaultHashtag);
		}
	}

	private boolean saveTasks() {
		boolean success;
		success = storage.saveTasks(taskList, DEFAULT_FILE_NAME);
		return success;
	}
}
