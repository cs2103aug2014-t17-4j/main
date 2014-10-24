package moustachio.task_catalyst;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class TaskAdvanced implements Task {

	boolean isDone;
	String description;

	public TaskAdvanced(String description) {
		this.description = description;
		isDone = false;
	}

	// Description Mutators

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		String interpretedString = this.description;

		String prettyString = TaskCatalystCommons
				.getPrettyString(interpretedString);

		String noCurlyBracesString = TaskCatalystCommons
				.removeCurlyBraces(prettyString);

		String friendlyString = TaskCatalystCommons
				.removeSquareBrackets(noCurlyBracesString);

		return friendlyString;
	}

	@Override
	public String getDescriptionEdit() {
		String interpretedString = this.description;

		String prettyString = TaskCatalystCommons
				.getPrettyString(interpretedString);

		String noCurlyBracesString = TaskCatalystCommons
				.removeCurlyBraces(prettyString);

		return noCurlyBracesString;
	}

	@Override
	public String getDescriptionRaw() {
		return this.description;
	}

	// Administrative Mutators

	@Override
	public boolean isRange() {
		return description.contains(" to {");
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	// Date Methods

	@Override
	public List<Date> getAllDates() {
		String descriptionRaw = getDescriptionRaw();

		List<Date> allDates = TaskCatalystCommons.getAllDates(descriptionRaw);

		return allDates;
	}

	@Override
	public LocalDateTime getDateStart() {
		List<Date> allDates = getAllDates();
		LocalDateTime localDateTime = null;
		if (!allDates.isEmpty()) {
			Instant firstDateInstant = allDates.get(0).toInstant();
			localDateTime = LocalDateTime.ofInstant(firstDateInstant,
					ZoneId.systemDefault());
		}
		return localDateTime;
	}

	@Override
	public LocalDateTime getDateEnd() {
		List<Date> allDates = getAllDates();
		LocalDateTime localDateTime = null;
		if (!allDates.isEmpty()) {
			Instant lastDateInstant = allDates.get(allDates.size() - 1)
					.toInstant();
			localDateTime = LocalDateTime.ofInstant(lastDateInstant,
					ZoneId.systemDefault());
		}
		return localDateTime;
	}

	// Comparison Methods

	@Override
	public List<String> getHashtags() {
		List<String> hashtagList = new ArrayList<String>();
		String[] descriptionTokenized = this.description.split(" ");
		for (String token : descriptionTokenized) {
			if (token.startsWith("#") || token.startsWith("[#")) {
				String tokenLowerAlphabets = "#"
						+ token.toLowerCase().replaceAll("[^A-Za-z0-9]+", "");
				hashtagList.add(tokenLowerAlphabets);
			}
		}
		return hashtagList;
	}

	@Override
	public boolean hasHashtag(String hashtag) {
		String hashtagLowerCase = hashtag.toLowerCase();
		String descriptionLowerCase = this.description.toLowerCase();
		boolean hasHashtag = descriptionLowerCase.matches(".*(^| )(#)"
				+ Pattern.quote(hashtagLowerCase) + "(\\b|$).*");
		return hasHashtag;
	}

	@Override
	public boolean hasKeyword(String keyword) {
		String descriptionLowerCase = this.description.toLowerCase();
		String keywordLowerCase = keyword.toLowerCase();
		return descriptionLowerCase.contains(keywordLowerCase);
	}

	@Override
	public int compareTo(Task o) {
		LocalDateTime thisDateTime = this.getDateStart();
		LocalDateTime otherDateTime = o.getDateStart();
		if (thisDateTime == null && otherDateTime == null) {
			return 0;
		} else if (thisDateTime == null) {
			return -1;
		} else if (otherDateTime == null) {
			return 1;
		} else {
			return thisDateTime.compareTo(otherDateTime);
		}
	}
}
