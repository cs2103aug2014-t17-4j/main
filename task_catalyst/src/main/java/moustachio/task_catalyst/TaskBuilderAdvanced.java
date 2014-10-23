package moustachio.task_catalyst;

public class TaskBuilderAdvanced implements TaskBuilder {

	// private static BlackBox blackBox = BlackBox.getInstance();

	public Task createTask(String userInput) {

		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		String interpretedString = TaskCatalystCommons
				.interpretedString(userInput);
		// blackBox.info("User Input: " + userInput);
		// blackBox.info("Interpreted String: " + interpretedString);
		return new TaskAdvanced(interpretedString);
	}
}
