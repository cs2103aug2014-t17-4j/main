package moustachio.task_catalyst;

public class TaskBuilderAdvanced implements TaskBuilder {

	public Task createTask(String userInput) {

		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		String interpretedString = TaskCatalystCommons
				.interpretedString(userInput);

		return new TaskAdvanced(interpretedString);
	}
}
