package moustachio.task_catalyst;

import java.util.List;

public class LogicActual implements Logic {

	ActionHintSystem actionHintSystem;
	TaskManager taskManager;

	public LogicActual() {
		actionHintSystem = new ActionHintSystemActual();
		taskManager = TaskManagerActual.getInstance();
	}

	public void testMode() {
		taskManager.testMode();
		actionHintSystem.testMode();
	}

	@Override
	public Message processCommand(String userCommand) {
		return actionHintSystem.processCommand(userCommand);
	}

	@Override
	public Message getMessageTyping(String userCommand) {
		return actionHintSystem.getMessageTyping(userCommand);
	}

	@Override
	public List<String> getHashtags() {
		return taskManager.getHashtags();
	}

	@Override
	public List<Task> getList() {
		return taskManager.getDisplayList();
	}

	@Override
	public int getHashtagSelected() {
		return taskManager.getHashtagSelected();
	}

	@Override
	public int getTaskSelected() {
		return taskManager.getTaskSelected();
	}
}
