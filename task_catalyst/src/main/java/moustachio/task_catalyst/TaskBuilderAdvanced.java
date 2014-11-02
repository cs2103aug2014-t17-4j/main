package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskBuilderAdvanced implements TaskBuilder {

	SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy KK:mm:ss a");

	public List<Task> createTask(String userInput) {
		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}
		Task task = createOneTask(userInput);
		List<Task> tasks = splitMultipleTasks(task);
		return tasks;
	}

	public Task createOneTask(String userInput) {

		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		Task task;

		try {
			String interpretedString = TaskCatalystCommons
					.getInterpretedString(userInput);
			task = new TaskAdvanced(interpretedString);
		} catch (UnsupportedOperationException e) {
			task = null;
		}

		return task;
	}

	public List<Task> splitMultipleTasks(Task task) {
		List<Task> tasks = new ArrayList<Task>();
		if (task.isMultiple()) {
			List<Date> dates = task.getAllDates();
			String descriptionModified = task.getDescriptionRaw();
			descriptionModified = descriptionModified.replaceAll("\\{.*\\}",
					"\\{\\}");
			for (Date date : dates) {
				Task splitTask = createOneTask(descriptionModified.replaceAll(
						"\\{\\}", "{" + formatter.format(date) + "}"));
				System.out.println(splitTask);
				tasks.add(splitTask);
			}
		} else {
			tasks.add(task);
		}
		return tasks;
	}
}
