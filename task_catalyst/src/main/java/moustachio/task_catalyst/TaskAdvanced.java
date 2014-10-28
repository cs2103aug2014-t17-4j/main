package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskAdvanced implements Task {

	boolean isDone;
	String description;
	HighlightType highlightType;

	Date dateStart;
	Date dateEnd;
	List<Date> allDates;

	public TaskAdvanced(String description) {
		highlightType = HighlightType.NORMAL;
		this.description = description;
		isDone = false;
		initializeDates();
	}

	// Description Mutators

	private void initializeDates() {
		String descriptionRaw = getDescriptionRaw();
		allDates = TaskCatalystCommons.getAllDates(descriptionRaw);
		if (!allDates.isEmpty()) {
			int firstIndex = 0;
			int lastIndex = allDates.size() - 1;
			dateStart = allDates.get(firstIndex);
			dateEnd = allDates.get(lastIndex);
		}
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		initializeDates();
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
		return description
				.matches(".*\\}.*(\\bto\\b\\s)(\\b\\w+\\b\\s){0,2}\\{.*");
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	@Override
	public boolean isPriority() {
		return hasHashtag("pri");
	}

	// Date Methods

	@Override
	public List<Date> getAllDates() {
		return allDates;
	}

	@Override
	public Date getDateStart() {
		return dateStart;
	}

	@Override
	public Date getDateEnd() {
		return dateEnd;
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
		String hashtagLowerCase = "#" + hashtag.toLowerCase();
		return getHashtags().contains(hashtagLowerCase);
	}

	@Override
	public boolean hasKeyword(String keyword) {
		String descriptionLowerCase = getDescription().toLowerCase();
		String keywordLowerCase = keyword.toLowerCase();
		return descriptionLowerCase.contains(keywordLowerCase);
	}

	@Override
	public int compareTo(Task o) {
		Date thisDateTime = this.getDateStart();
		Date otherDateTime = o.getDateStart();
		if (this.isPriority() && !o.isPriority()) {
			return -1;
		} else if (!this.isPriority() && o.isPriority()) {
			return 1;
		}
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

	@Override
	public HighlightType getHighlightType() {
		return highlightType;
	}

	@Override
	public void setHighlightType(HighlightType highlightType) {
		this.highlightType = highlightType;
	}
}
