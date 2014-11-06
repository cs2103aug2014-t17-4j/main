package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

//@author A0111890
public class TaskManagerActual implements TaskManager {
	private static final String[] DEFAULT_HASHTAGS = { "#all", "#pri", "#ovd",
			"#tdy", "#tmr", "#upc", "#smd", "#olp", "#dne" };
	private static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayMode.HASHTAG;
	private static final String DEFAULT_DISPLAY_KEYWORD = "all";
	private static final String DEFAULT_FILE_NAME = "tasks.txt";

	private static final int ERROR_INTEGER = -1;

	private Storage storage;
	private ListProcessor listProcessor;

	private DisplayMode displayMode;
	private String displayKeyword;
	private List<Integer> tasksSelected;

	private List<Task> taskList;
	private List<Task> displayList;
	private List<String> hashtagList;

	private static TaskManagerActual instance;

	// Initialization Methods

	private TaskManagerActual() {
		initializeInstance();
		initializeComponents();
		initializeLists();
		refreshLists();
	}

	public void testMode() {
		displayMode = DEFAULT_DISPLAY_MODE;
		displayKeyword = DEFAULT_DISPLAY_KEYWORD;

		storage = new StorageStub();
		hashtagList = new ArrayList<String>();

		clearLists();
		refreshLists();
	}

	public static TaskManagerActual getInstance() {
		if (instance == null) {
			instance = new TaskManagerActual();
		}
		return instance;
	}

	private void initializeInstance() {
		instance = this;
	}

	private void initializeComponents() {
		listProcessor = new ListProcessorActual();
		storage = new StorageActual();
	}

	private void initializeLists() {
		displayMode = DEFAULT_DISPLAY_MODE;
		displayKeyword = DEFAULT_DISPLAY_KEYWORD;
		taskList = loadTasks();
		hashtagList = new ArrayList<String>();
		tasksSelected = new ArrayList<Integer>();
	}

	// Getter Methods

	@Override
	public List<String> getHashtags() {
		return hashtagList;
	}

	@Override
	public String[] getDefaultHashtags() {
		return DEFAULT_HASHTAGS;
	}

	@Override
	public int getHashtagSelected() {
		boolean isHashtagMode = (displayMode == DisplayMode.HASHTAG);
		boolean isSearchMode = (displayMode == DisplayMode.SEARCH);

		int index;

		if (isHashtagMode) {
			String hashtagString = "#" + displayKeyword;
			index = getHashtags().indexOf(hashtagString);
		} else if (isSearchMode) {
			String searchString = "search " + displayKeyword;
			index = getHashtags().indexOf(searchString);
		} else {
			index = ERROR_INTEGER;
		}

		return index;
	}

	@Override
	public List<Integer> getTasksSelected() {
		return tasksSelected;
	}

	@Override
	public List<Task> getList() {
		return taskList;
	}

	@Override
	public List<Task> getDisplayList() {
		return displayList;
	}

	// Display List Methods

	@Override
	public void setDisplayModeKeyword(DisplayMode MODE, String keyword) {
		this.displayMode = MODE;
		this.displayKeyword = keyword;
		refreshLists();
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
	public Task removeDisplayTask(int taskNumber) {
		Task task = getDisplayTask(taskNumber);

		boolean isFound = (task != null);
		boolean isRemoved = removeTask(task);
		boolean isSuccess = (isRemoved && isFound);

		if (isSuccess) {
			refreshLists();
		}

		return task;
	}

	// CRUD Methods

	@Override
	public boolean addTask(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);

		int tasksAdded = addTasks(tasks);

		boolean isSuccess = (tasksAdded == 1);

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
			tasks = loadTasks();
		}

		boolean isSuccess = (isAdded && isSaved);

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
	public boolean removeTask(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);

		int tasksRemoved = removeTasks(tasks);

		boolean isSuccess = (tasksRemoved == 1);

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
			tasks = loadTasks();
		}

		boolean isSuccess = (isRemoved && isSaved);

		if (isSuccess) {
			refreshLists();
			displayAutoswitchToTask(null);
		}

		return numberRemoved;
	}

	@Override
	public boolean editTask(Task target, Task replacement) {
		boolean isRemoved = removeTask(target);
		boolean isAdded = false;

		if (isRemoved) {
			isAdded = addTask(replacement);
		}

		boolean isSuccess = (isRemoved && isAdded);

		if (isSuccess) {
			refreshLists();
		}

		return isSuccess;
	}

	@Override
	public boolean completeTask(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);

		int tasksCompleted = completeTasks(tasks);

		boolean isSuccess = (tasksCompleted == 1);

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
			tasks = loadTasks();
		}

		boolean isSuccess = (isCompleted && isSaved);

		if (isSuccess) {
			refreshLists();
			displayAutoswitchToTask(null);
		}

		return numberCompleted;
	}

	@Override
	public boolean uncompleteTask(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(task);

		int tasksUncompleted = uncompleteTasks(tasks);

		boolean isSuccess = (tasksUncompleted == 1);

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
			tasks = loadTasks();
		}

		boolean isSuccess = (isUndone && isSaved);

		if (isSuccess) {
			refreshLists();
			for (Task task : tasks) {
				displayAutoswitchToTask(task);
				tasksSelected.add(0, displayList.indexOf(task));
			}
		}

		return numberUncomplete;
	}

	// List Administration Methods

	private boolean saveTasks() {
		boolean success;

		success = storage.saveTasks(taskList, DEFAULT_FILE_NAME);

		return success;
	}

	private List<Task> loadTasks() {
		return storage.loadTasks(DEFAULT_FILE_NAME);
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

		clearOverlapping(displayList);
		setOverlapping(taskList);

		tasksSelected.clear();
	}

	private void refreshHashtagList() {
		List<String> defaultHashtags = generateDefaultHashtags();
		List<String> customHashtags = generateCustomHashtags();

		hashtagList.clear();

		boolean isSearchMode = (displayMode == DisplayMode.SEARCH);

		if (isSearchMode) {
			String searchString = "search " + displayKeyword;
			hashtagList.add(searchString);
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

	// Task Marking Methods

	private void setOverlapping(List<Task> tasks) {
		List<Task> overlapList = listProcessor.getOverlapping(tasks);

		for (Task task : overlapList) {
			task.setOverlapping(true);
		}
	}

	private void clearOverlapping(List<Task> tasks) {
		for (Task task : tasks) {
			task.setOverlapping(false);
		}
	}

	private void displayAutoswitchToTask(Task task) {
		boolean isRemoveOperation = (task == null);
		boolean isTaskDisplayed = displayList.contains(task);
		boolean isListEmpty = displayList.isEmpty();
		boolean isTaskMissing = (!isRemoveOperation && !isTaskDisplayed);
		boolean isNeedAutoswitch = (isTaskMissing || isListEmpty);

		if (isNeedAutoswitch) {
			setDisplayModeKeyword(DEFAULT_DISPLAY_MODE, DEFAULT_DISPLAY_KEYWORD);
			refreshLists();
		}
	}
}
