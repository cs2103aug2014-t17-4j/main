package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * This program handles JSON conversion for Storage component.
 * 
 * This implementation uses JSONObject to encode the contents of tasks and
 * decode JSONOBject to String format which is text format.
 *
 */

// @author A0112764J
public class JSONConverter {

	private static final String ERROR_TASK_PARSING = "[ERROR: %s]";
	private static final String ERROR_JSON_PARSING = "[ERROR: This task cannot be parsed. Please close the program and check tasks.txt]";
	private static final String IS_DONE = "isDone";
	private static final String DESCRIPTION = "description";
	private TaskBuilder taskBuilder;

	private JSONObject jsonObject;

	public JSONConverter() {
		jsonObject = new JSONObject();
		taskBuilder = new TaskBuilderAdvanced();
	}

	@SuppressWarnings("unchecked")
	public JSONObject encode(Task task) {
		jsonObject.put(IS_DONE, task.isDone());
		jsonObject.put(DESCRIPTION, task.getDescriptionRaw());
		return jsonObject;
	}

	public List<Task> decode(JSONObject obj) {
		List<Task> tasks;
		String description;
		boolean isDone = false;
		description = (String) obj.get(DESCRIPTION);
		if (obj.containsKey(IS_DONE)) {
			isDone = (Boolean) obj.get(IS_DONE);
		}
		tasks = taskBuilder.createTask(description);
		if (tasks != null) {
			for (Task task : tasks) {
				task.setDone(isDone);
			}
		} else {
			tasks = new ArrayList<Task>();
			String taskErrorDescription;
			if (description != null) {
				taskErrorDescription = String.format(ERROR_TASK_PARSING,
						description);
			} else {
				taskErrorDescription = ERROR_JSON_PARSING;
			}
			TaskAdvanced task = new TaskAdvanced(taskErrorDescription);
			task.setError(true);
			tasks.add(task);
		}
		return tasks;
	}

	public List<Task> decodeToString(String str) throws ParseException {
		JSONObject obj = (JSONObject) JSONValue.parse(str);
		if (obj == null) {
			List<Task> tasks = new ArrayList<Task>();
			TaskAdvanced errorTask = new TaskAdvanced(ERROR_JSON_PARSING);
			errorTask.setError(true);
			tasks.add(errorTask);
			return tasks;
		} else {
			return decode(obj);
		}
	}
}