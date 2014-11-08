package moustachio.task_catalyst;

import java.util.List;

//@author A0111890L
/**
 * TaskBuilder is used for creating a list of tasks based on user input.
 */
public interface TaskBuilder {
	public List<Task> createTask(String userInput);
}
