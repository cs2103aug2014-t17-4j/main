package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@author A0111890
public class TaskBuilderAdvanced implements TaskBuilder {

	SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy KK:mm:ss a");

	public List<Task> createTask(String userInput) {
		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		Task task = createOneTask(userInput);

		List<Task> tasks;

		boolean isTaskValid = (task != null);

		if (isTaskValid) {
			tasks = splitIntoMultipleTasks(task);
		} else {
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
