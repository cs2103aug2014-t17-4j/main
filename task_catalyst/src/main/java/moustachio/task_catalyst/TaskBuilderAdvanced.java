package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@author A0111890L
/**
 * TaskBuilderAdvanced implements the TaskBuilder interface and is able to build
 * tasks based on user input. It is also able to split tasks if a task contains
 * multiple dates.
 */
public class TaskBuilderAdvanced implements TaskBuilder {

	BlackBox blackBox;
	SimpleDateFormat formatter;

	public TaskBuilderAdvanced() {
		formatter = new SimpleDateFormat("dd MMM yyyy KK:mm:ss a");
		blackBox = BlackBox.getInstance();
	}

	public List<Task> createTask(String userInput) {
		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		blackBox.info("User Entered: " + userInput);

		Task task = createOneTask(userInput);

		List<Task> tasks;

		boolean isTaskValid = (task != null);

		if (isTaskValid) {
			blackBox.info("Task built with description: "
					+ task.getDescriptionRaw());

			tasks = splitIntoMultipleTasks(task);

			blackBox.info("Task split into " + tasks.size() + " tasks.");
		} else {
			blackBox.info("Task not built successfully.");

			tasks = null;
		}

		return tasks;
	}

	public Task createOneTask(String userInput) {
		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		Task task;

		try {
			boolean strict = true;
			String interpretedString = TaskCatalystCommons
					.getInterpretedString(userInput, strict);

			task = new TaskAdvanced(interpretedString);
		} catch (UnsupportedOperationException e) {
			task = null;
		}

		return task;
	}

	public List<Task> splitIntoMultipleTasks(Task task) {
		String wordsInCurlyBraces = "\\{.*\\}";
		String emptyCurlyBraces = "\\{\\}";

		List<Task> tasks = new ArrayList<Task>();

		if (task.isMultiple()) {
			List<Date> dates = task.getAllDates();

			String descriptionModified = task.getDescriptionRaw();
			descriptionModified = descriptionModified.replaceAll(
					wordsInCurlyBraces, emptyCurlyBraces);

			for (Date date : dates) {
				String formattedDate = formatter.format(date);
				formattedDate = "{" + formattedDate + "}";

				Task splitTask = createOneTask(descriptionModified.replaceAll(
						emptyCurlyBraces, formattedDate));

				tasks.add(splitTask);
			}
		} else {
			tasks.add(task);
		}
		return tasks;
	}
}
