package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

public class TaskAdvanced implements Task {

	boolean isDone;
	String description;
	HighlightType highlightType;

	public TaskAdvanced(String description) {
		highlightType = HighlightType.NORMAL;
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

	@Override
	public boolean isPriority() {
		return hasHashtag("pri");
	}

	// Date Methods

	@Override
	public List<Date> getAllDates() {
		String descriptionRaw = getDescriptionRaw();

		List<Date> allDates = TaskCatalystCommons.getAllDates(descriptionRaw);

		return allDates;
	}

	@Override
	public Date getDateStart() {
		List<Date> allDates = getAllDates();
		Date dateStart = null;
		if (!allDates.isEmpty()) {
			int firstIndex = 0;
			dateStart = allDates.get(firstIndex);
		}
		Collections.sort(allDates);
		return dateStart;
	}

	@Override
	public Date getDateEnd() {
		List<Date> allDates = getAllDates();
		Date dateEnd = null;
		if (!allDates.isEmpty()) {
			int lastIndex = allDates.size() - 1;
			dateEnd = allDates.get(lastIndex);
		}
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
		String descriptionLowerCase = this.description.toLowerCase();
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
