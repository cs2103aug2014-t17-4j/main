package moustachio.task_catalyst;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class JSONConverter {

    private JSONObject object;

    public JSONConverter() {
	object = new JSONObject();
    }

    @SuppressWarnings("unchecked")
    public JSONObject encode(Task task) {
	object.put("isDone", task.isDone());
	object.put("description", task.getDescriptionRaw());

	return object;
    }

    public Task decode(JSONObject obj) {
	Task task = new TaskAdvanced("");
	if (obj.get("description") != null) {
	    task.setDescription((String) obj.get("description"));
	}
	if (obj.get("isDone") != null) {
	    task.setDone(((Boolean) obj.get("isDone")));
	}
	return task;
    }

    public Task decodeToString(String str) throws ParseException {
	JSONObject obj = (JSONObject) JSONValue.parseWithException(str);
	return decode(obj);
    }
}
