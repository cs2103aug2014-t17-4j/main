package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TaskManagerActual implements TaskManager {

	private static final String[] DEFAULT_HASHTAGS = { "#all", "#pri", "#ovd",
			"#tdy", "#tmr", "#upc", "#smd", "#olp", "#dne" };
	private static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayMode.HASHTAG;
	private static final String DEFAULT_DISPLAY_KEYWORD = "all";
	private static final String DEFAULT_FILE_NAME = "tasks.txt";

	private Storage storage;
	private ListProcessor listProcessor;

	private DisplayMode displayMode;
	private String displayKeyword;
	private List<Integer> tasksSelected;

	private List<Task> taskList;
	private List<Task> displayList;
	private List<String> hashtagList;

	private static TaskManagerActual instance;

	public static TaskManagerActual getInstance() {
		if (instance == null) {
			instance = new TaskManagerActual();
		}
		return instance;
	}

	private TaskManagerActual() {
		instance = this;
		listProcessor = new ListProcessorActual();
		displayMode = DEFAULT_DISPLAY_MODE;
		displayKeyword = DEFAULT_DISPLAY_KEYWORD;
		storage = new StorageActual();
		taskList = storage.loadTasks(DEFAULT_FILE_NAME);
		hashtagList = new ArrayList<String>();
		tasksSelected = new ArrayList<Integer>();
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
	public String[] getDefaultHashtags() {
		return DEFAULT_HASHTAGS;
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
	public int getHashtagSelected() {
		if (displayMode == DisplayMode.HASHTAG) {
			return getHashtags().indexOf("#" + displayKeyword);
		} else {
			return getHashtags().indexOf("search " + displayKeyword);
		}
	}

	@Override
	public List<Integer> getTasksSelected() {
		return tasksSelected;
	}

	@Override
	public boolean addTask(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);
		int tasksAdded = addTasks(tasks);
		boolean isSuccess = tasksAdded == 1;
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
				tasksSelected.add(0, displayList.indexOf(task));
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
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);
		int tasksRemoved = removeTasks(tasks);
		boolean isSuccess = tasksRemoved == 1;
		return isSuccess;
	}

	@Override
	public int completeTasks(List<Task> tasks) {

		int numberCompleted = 0;

		boolean isCompleted = false;
		for (Task task : tasks) {
			if (!task.isDone()) {
				task.setDone(true);
				isCompleted = task.isDone();
				if (!isCompleted) {
					break;
				}
				numberCompleted++;
			}
		}

		boolean isSaved = false;
		if (isCompleted) {
			isSaved = saveTasks();
		} else {
			tasks = storage.loadTasks(DEFAULT_FILE_NAME);
		}

		boolean isSuccess = isCompleted && isSaved;
		if (isSuccess) {
			refreshLists();
			displayAutoswitchToTask(null);
		}

		return numberCompleted;
	}

	@Override
	public boolean completeTask(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);
		int tasksCompleted = completeTasks(tasks);
		boolean isSuccess = tasksCompleted == 1;
		return isSuccess;
	}

	@Override
	public int uncompleteTasks(List<Task> tasks) {
		int numberUncomplete = 0;

		boolean isUndone = false;
		for (Task task : tasks) {
			if (task.isDone()) {
				task.setDone(false);
				isUndone = !task.isDone();
				if (!isUndone) {
					break;
				}
				numberUncomplete++;
			}
		}

		boolean isSaved = false;
		if (isUndone) {
			isSaved = saveTasks();
		} else {
			tasks = storage.loadTasks(DEFAULT_FILE_NAME);
		}

		boolean isSuccess = isUndone && isSaved;
		if (isSuccess) {
			refreshLists();
			for (Task task : tasks) {
				displayAutoswitchToTask(task);
				tasksSelected.add(0, displayList.indexOf(task));
			}
		}

		return numberUncomplete;
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
		tasksSelected.clear();
		clearHighlights(displayList);
		highlightAllPriority(displayList);
		highlightAllOverlap(taskList);
		highlightAllOverdue(displayList);
	}

	private void clearHighlights(List<Task> tasks) {
		for (Task task : tasks) {
			task.setHighlightType(HighlightType.NORMAL);
		}
	}

	private void highlightAllPriority(List<Task> tasks) {
		for (Task task : tasks) {
			if (task.isPriority()) {
				task.setHighlightType(HighlightType.PRIORITY);
			}
		}
	}

	private void highlightAllOverdue(List<Task> tasks) {
		for (Task task : tasks) {
			if (task.isOverdue()) {
				task.setHighlightType(HighlightType.OVERDUE);
			}
		}
	}

	private void highlightAllOverlap(List<Task> tasks) {
		List<Task> overlapList = listProcessor.getOverlapping(tasks);
		for (Task task : overlapList) {
			if (task.isPriority()) {
				task.setHighlightType(HighlightType.PRIORITY_OVERLAP);
			} else {
				task.setHighlightType(HighlightType.OVERLAP);
			}
		}
	}

	private void refreshHashtagList() {

		List<String> defaultHashtags = generateDefaultHashtags();
		List<String> customHashtags = generateCustomHashtags();

		hashtagList.clear();

		if (displayMode == DisplayMode.SEARCH) {
			hashtagList.add("search " + displayKeyword);
		}

		hashtagList.addAll(defaultHashtags);
		for (String hashtag : customHashtags) {
			if (!defaultHashtags.contains(hashtag)) {
				hashtagList.add(hashtag);
			}
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
			if (!task.isDone()) {
				List<String> taskHashtags = task.getHashtags();
				allHashtagsSet.addAll(taskHashtags);
			}
		}
		List<String> customHashtags = new ArrayList<String>(allHashtagsSet);
		return customHashtags;
	}

	private void displayAutoswitchToTask(Task task) {
		boolean isTaskNull = task == null;
		boolean isTaskDisplayed = displayList.contains(task);
		boolean isListEmpty = displayList.isEmpty();
		boolean isNeedAutoswitch = (!isTaskNull && !isTaskDisplayed)
				|| isListEmpty;
		if (isNeedAutoswitch) {
			setDisplayMode(DEFAULT_DISPLAY_MODE);
			setDisplayKeyword(DEFAULT_DISPLAY_KEYWORD);
			refreshLists();
		}
	}

	private boolean saveTasks() {
		boolean success;
		success = storage.saveTasks(taskList, DEFAULT_FILE_NAME);
		return success;
	}
}
