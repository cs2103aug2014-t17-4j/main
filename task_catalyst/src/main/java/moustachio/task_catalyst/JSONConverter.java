package moustachio.task_catalyst;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * This program handles JSON conversion for Storage component.
 * 
 * This implementation uses JSONObject to encode the contents of tasks and
 * decode JSONOBject to String format which is text format.
 * 
 * @author Lin XiuQing (A0112764J)
 */

public class JSONConverter {

	private static final String IS_DONE = "isDone";
	private static final String DESCRIPTION = "description";

	private JSONObject jsonObject;

	public JSONConverter() {
		jsonObject = new JSONObject();
	}

	@SuppressWarnings("unchecked")
	public JSONObject encode(Task task) {
		jsonObject.put(IS_DONE, task.isDone());
		jsonObject.put(DESCRIPTION, task.getDescriptionRaw());

		return jsonObject;
	}

	public Task decode(JSONObject obj) {
		Task task;
		String description;
		boolean isDone;
		description = (String) obj.get(DESCRIPTION);
		isDone = (Boolean) obj.get(IS_DONE);
		task = new TaskBuilderAdvanced().createTask(description);
		if (task != null) {
			task.setDone(isDone);
		}
		return task;
	}

	public Task decodeToString(String str) throws ParseException {
		JSONObject obj = (JSONObject) JSONValue.parseWithException(str);
		return decode(obj);
	}
}
