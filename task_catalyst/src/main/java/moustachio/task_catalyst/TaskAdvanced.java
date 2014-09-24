package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TaskAdvanced extends TaskBasic {
	public TaskAdvanced(String description) {
		super(description);
	}

	public TaskAdvanced(String description, LocalDateTime dateStart) {
		super(description, dateStart);
	}

	public TaskAdvanced(String description, LocalDateTime dateStart,
			LocalDateTime dateEnd) {
		super(description, dateStart, dateEnd);
	}

	// Mutators

	public String getDescription() {
		return TaskBuilderAdvanced.removeSquareBrackets(TaskBuilderAdvanced.removeCurlyBraces(TaskBuilderAdvanced.prettyString(this.description.get())));
	}
	
	public String getDescriptionEdit() {
		return TaskBuilderAdvanced.removeCurlyBraces(TaskBuilderAdvanced.prettyString(this.description.get()));
	}

	public LocalDateTime getDateStart() {
		List<Date> allDates = TaskBuilderAdvanced.getAllDates(this.description.get());
		LocalDateTime localDateTime = null;
		if (!allDates.isEmpty()) {
			localDateTime = LocalDateTime.ofInstant(allDates.get(0).toInstant(), ZoneId.systemDefault());
		}
		return localDateTime;
	}
	
	public LocalDateTime getDateEnd() {
		List<Date> allDates = TaskBuilderAdvanced.getAllDates(this.description.get());
		LocalDateTime localDateTime = null;
		if (!allDates.isEmpty()) {
			localDateTime = LocalDateTime.ofInstant(allDates.get(allDates.size()-1).toInstant(), ZoneId.systemDefault());
		}
		return localDateTime;
	}
}
