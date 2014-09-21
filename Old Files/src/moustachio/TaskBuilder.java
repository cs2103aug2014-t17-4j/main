package moustachio;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class TaskBuilder {

	public static void main(String[] args) {
		Task task = createTask("meet boss at 5pm");
		System.out.println(task.getDescription());
		System.out.println(task.getDateStart());
		System.out.println(task.getDateEnd());
	}
	
	public static Task createTask(String userInput) {

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(userInput);
		/*for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
			int line = group.getLine();
			int column = group.getPosition();
			String matchingValue = group.getText();
			String syntaxTree = group.getSyntaxTree().toStringTree();
			Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
			boolean isRecurreing = group.isRecurring();
			Date recursUntil = group.getRecursUntil();
		}*/
		Task task = null;
		if (!groups.isEmpty()) {
			Date dateStart = groups.get(0).getDates().get(0);
			LocalDateTime ldtDateStart = LocalDateTime.ofInstant(dateStart.toInstant(), ZoneId.systemDefault());
			Date dateEnd = null;
			LocalDateTime ldtDateEnd = null;
			if (groups.size()>1) {
				dateEnd = groups.get(groups.size()-1).getDates().get(0);
				ldtDateEnd = LocalDateTime.ofInstant(dateEnd.toInstant(), ZoneId.systemDefault());
			}
			task = new Task(userInput, ldtDateStart, ldtDateEnd);
		}
		return task;
	}
}
