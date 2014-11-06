package moustachio.task_catalyst;

import java.util.List;

//@author A0111890
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

	// ActionHintSystem Commands

	@Override
	public Message processCommand(String userCommand) {
		return actionHintSystem.processCommand(userCommand);
	}

	@Override
	public Message getMessageTyping(String userCommand) {
		return actionHintSystem.getMessageTyping(userCommand);
	}

	// TaskManager Commands

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
	public List<Integer> getTasksSelected() {
		return taskManager.getTasksSelected();
	}
}
