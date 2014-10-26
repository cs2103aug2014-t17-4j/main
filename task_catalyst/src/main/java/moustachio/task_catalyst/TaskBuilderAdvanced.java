package moustachio.task_catalyst;

public class TaskBuilderAdvanced implements TaskBuilder {

	public Task createTask(String userInput) {

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
}
