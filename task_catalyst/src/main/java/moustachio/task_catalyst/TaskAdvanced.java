package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
		return TaskCatalystCommons.removeSquareBrackets(TaskCatalystCommons
				.removeCurlyBraces(TaskCatalystCommons
						.prettyString(this.description.get())));
	}

	public String getDescriptionRaw() {
		return this.description.get();
	}

	public String getDescriptionEdit() {
		return TaskCatalystCommons.removeCurlyBraces(TaskCatalystCommons
				.prettyString(this.description.get()));
	}

	public LocalDateTime getDateStart() {
		List<Date> allDates = TaskCatalystCommons.getAllDates(this.description
				.get());
		LocalDateTime localDateTime = null;
		if (!allDates.isEmpty()) {
			localDateTime = LocalDateTime.ofInstant(
					allDates.get(0).toInstant(), ZoneId.systemDefault());
		}
		return localDateTime;
	}

	public LocalDateTime getDateEnd() {
		List<Date> allDates = TaskCatalystCommons.getAllDates(this.description
				.get());
		LocalDateTime localDateTime = null;
		if (!allDates.isEmpty()) {
			localDateTime = LocalDateTime.ofInstant(
					allDates.get(allDates.size() - 1).toInstant(),
					ZoneId.systemDefault());
		}
		return localDateTime;
	}

	public List<String> getHashtags() {
		List<String> hashtagList = new ArrayList<String>();
		String[] descriptionTokenized = this.description.get().split(" ");
		for (String token : descriptionTokenized) {
			if (token.startsWith("#") || token.startsWith("[#")) {
				String tokenLowerAlphabets = "#"
						+ token.toLowerCase().replaceAll("[^A-Za-z0-9]+", "");
				hashtagList.add(tokenLowerAlphabets);
			}
		}
		return hashtagList;
	}
}
