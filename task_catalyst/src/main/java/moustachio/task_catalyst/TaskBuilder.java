package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class TaskBuilder {

	public static void main(String[] args) {
		Task task = createTask("meet boss tomorrow to friday");
		System.out.println(task.getDescription());
		System.out.println(task.getDateStart());
		System.out.println(task.getDateEnd());
	}

	public static Task createTask(String userInput) {

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(userInput);
		Task task = new Task(userInput);
		if (!groups.isEmpty()) {
			List<Date> dateList = groups.get(0).getDates();
			if (!dateList.isEmpty()) {
				Date dateStart = dateList.get(0);
				LocalDateTime ldtDateStart = LocalDateTime.ofInstant(
						dateStart.toInstant(), ZoneId.systemDefault());
				Date dateEnd = null;
				LocalDateTime ldtDateEnd = null;
				if (dateList.size() == 2) {
					dateEnd = dateList.get(dateList.size() - 1);
					ldtDateEnd = LocalDateTime.ofInstant(dateEnd.toInstant(),
							ZoneId.systemDefault());
				}
				task = new Task(userInput, ldtDateStart, ldtDateEnd);
			}
		}
		return task;
	}
}
