package moustachio.task_catalyst;

import moustachio.task_catalyst.TaskAdvanced;
import java.time.LocalDateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class JSONConverter {
	
	private JSONObject object;
	
	public JSONConverter(){
		object = new JSONObject();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject encode (Task task){
		String dateStart = null;
		String dateEnd= null;
		if(task.getDateStart()==null);
		else{
			dateStart = task.getDateStart().toString();
		}
		if(task.getDateEnd()==null);
		else{
			dateEnd = task.getDateEnd().toString();
		}
		object.put("dateStart", dateStart);
		object.put("dateEnd", dateEnd);
		object.put("description", task.getDescriptionRaw());
		
		return object;	
	}
	
	public Task decode (JSONObject obj){
		Task task = new TaskAdvanced("");
		if (obj.get("description") != null) {
			task.setDescription((String) obj.get("description"));
		}
		if(obj.get("startDateTime") == null){
			task.setDateStart(null);
		}
		else{
			task.setDateStart(LocalDateTime.parse((String)obj.get("startDate")));
		}
		if(obj.get("endDateTime")==null){
			task.setDateEnd(null);
		}
		else{
			task.setDateEnd(LocalDateTime.parse((String)obj.get("endDate")));
		}
		return task;
	}

	public Task decodeToString(String str) throws ParseException{
		JSONObject obj = (JSONObject) JSONValue.parseWithException(str);
		return decode(obj);
	}
}
