package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

public class TaskBuilderAdvanced implements TaskBuilder {

	public List<Task> createTask(String userInput) {

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
		List<Task> tasks = null;
		if (task != null) {
			tasks = new ArrayList<Task>();
			tasks.add(task);
		}
		return tasks;
	}
}
