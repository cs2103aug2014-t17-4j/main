package moustachio.task_catalyst;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

//@author A0112764J
public class JSONConverter {

	private static final String MESSAGE_FAIL_TO_WRITE_FILE = "Fail to write file.";
	private static final String ERROR_TASK_PARSING = "[ERROR: %s]";
	private static final String ERROR_JSON_PARSING = "[ERROR: This task cannot be parsed. Please close the program and check tasks.txt]";
	private static final String IS_DONE = "isDone";
	private static final String DESCRIPTION = "description";
	private TaskBuilder taskBuilder;
	private JSONObject jsonObject;
	private static BlackBox blackBox = BlackBox.getInstance();

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
		tasks = handleTask(tasks, description, isDone);
		return tasks;
	}

	private List<Task> handleTask(List<Task> tasks, String description,
			boolean isDone) {
		if (tasks != null) {
			for (Task task : tasks) {
				task.setDone(isDone);
			}
		} else {
			tasks = handleNullTask(description);
		}
		return tasks;
	}

	private List<Task> handleNullTask(String description) {
		List<Task> tasks;
		tasks = new ArrayList<Task>();
		String taskErrorDescription;
		taskErrorDescription = checkDescription(description);
		TaskAdvanced task = new TaskAdvanced(taskErrorDescription);
		task.setError(true);
		tasks.add(task);
		return tasks;
	}

	private String checkDescription(String description) {
		String taskErrorDescription;
		if (description != null) {
			taskErrorDescription = String.format(ERROR_TASK_PARSING,
					description);
		} else {
			taskErrorDescription = ERROR_JSON_PARSING;
		}
		return taskErrorDescription;
	}

	public List<Task> decodeToString(String str) throws ParseException {
		JSONObject obj = (JSONObject) JSONValue.parse(str);
		if (obj == null) {
			return handleNullJSONObject(str);
		} else {
			return decode(obj);
		}
	}

	private List<Task> handleNullJSONObject(String str) {
		List<Task> tasks = new ArrayList<Task>();
		TaskAdvanced errorTask = new TaskAdvanced(ERROR_JSON_PARSING);
		List<String> strList = new ArrayList<String>();
		strList.add(str);
		try {
			writeFile(strList);
		} catch (Exception e) {
			blackBox.info(MESSAGE_FAIL_TO_WRITE_FILE);
		}
		errorTask.setError(true);
		tasks.add(errorTask);
		return tasks;
	}

	private void writeFile(List<String> strList) throws IOException {
		FileWriter errorFile = new FileWriter("Task Catalyst/" +"error.txt",
				true);
		BufferedWriter writer = new BufferedWriter(errorFile);
		for(int i=0; i<strList.size(); i++){
			System.out.println(strList.get(i));
			writer.write(strList.get(i));
			writer.flush();
			writer.close();
		}
	}
}