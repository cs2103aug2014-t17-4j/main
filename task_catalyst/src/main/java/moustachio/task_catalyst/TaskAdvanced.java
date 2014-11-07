package moustachio.task_catalyst;

import java.util.Date;
import java.util.List;

//@author A0111890
public class TaskAdvanced implements Task {
	String description;
	Date dateStart;
	Date dateEnd;
	List<Date> allDates;

	boolean isOverlapping;
	boolean isDone;

	// Initialization Methods

	public TaskAdvanced(String description) {
		setDescription(description);
	}

	private void initializeAttributes(String description) {
		this.description = description;
		this.isDone = false;
	}

	private void initializeDates() {
		String descriptionRaw = getDescriptionRaw();

		allDates = TaskCatalystCommons.getAllDates(descriptionRaw);

		boolean isFloatingTask = allDates.isEmpty();

		if (!isFloatingTask) {
			int firstIndex = 0;
			int lastIndex = allDates.size() - 1;

			dateStart = allDates.get(firstIndex);
			dateEnd = allDates.get(lastIndex);
		}
	}

	// Public Mutators

	@Override
	public String getDescription() {
		String interpretedString = getDescriptionRaw();

		String description = TaskCatalystCommons
				.getPrettyStringWithoutDate(interpretedString);

		return description;
	}

	@Override
	public String getDescriptionEdit() {
		String interpretedString = getDescriptionRaw();

		boolean isAlwaysShowTime = true;

		String prettyString = TaskCatalystCommons.getPrettyString(
				interpretedString, isAlwaysShowTime);

		String descriptionEdit = TaskCatalystCommons
				.removeCurlyBraces(prettyString);

		return descriptionEdit;
	}

	@Override
	public String getDescriptionRaw() {
		String descriptionRaw = this.description;

		return descriptionRaw;
	}

	@Override
	public void setDescription(String description) {
		initializeAttributes(description);
		initializeDates();
	}

	@Override
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	@Override
	public void setOverlapping(boolean overlapping) {
		this.isOverlapping = overlapping;
	}

	// Date Methods

	@Override
	public List<Date> getAllDates() {
		return this.allDates;
	}

	@Override
	public Date getDateStart() {
		return this.dateStart;
	}

	@Override
	public Date getDateEnd() {
		return this.dateEnd;
	}

	@Override
	public Date getNextDate() {
		if (allDates.isEmpty()) {
			return null;
		}

		if (isRange()) {
			return getDateStart();
		}

		Date now = new Date();
		Date nextDate = null;

		for (Date date : allDates) {
			boolean isDateAfterNow = date.after(now);
			if (isDateAfterNow) {
				nextDate = date;
				break;
			}
		}

		return nextDate;
	}

	@Override
	public boolean hasDate(Date date) {
		boolean hasDate = false;

		boolean isFloatingTask = (allDates == null);

		if (isFloatingTask) {
			hasDate = false;
		} else if (isRange()) {
			hasDate = TaskCatalystCommons.isBetweenDates(getDateStart(),
					getDateEnd(), date);
		} else {
			for (Date eachDate : allDates) {
				hasDate = TaskCatalystCommons.isSameDate(eachDate, date);

				if (hasDate) {
					break;
				}
			}
		}

		return hasDate;
	}

	@Override
	public boolean isBetweenDates(Date start, Date end) {
		boolean isBetweenDates = false;

		boolean isFloatingTask = (allDates == null);

		if (isFloatingTask) {
			isBetweenDates = false;
		} else if (isRange()) {
			boolean startIsBetween = TaskCatalystCommons.isBetweenDates(start,
					end, getDateStart());
			boolean endIsBetween = TaskCatalystCommons.isBetweenDates(start,
					end, getDateEnd());

			isBetweenDates = (startIsBetween || endIsBetween);
		} else {
			for (Date eachDate : allDates) {
				isBetweenDates = TaskCatalystCommons.isBetweenDates(start, end,
						eachDate);

				if (isBetweenDates) {
					break;
				}
			}
		}

		return isBetweenDates;
	}

	// Hashtag / Search Methods

	@Override
	public List<String> getHashtags() {
		String interpretedString = getDescriptionRaw();

		List<String> hashtags = TaskCatalystCommons
				.extractHashtags(interpretedString);

		return hashtags;
	}

	@Override
	public boolean hasHashtag(String hashtag) {
		String hashtagLowerCase = "#" + hashtag.toLowerCase();

		List<String> hashtags = getHashtags();

		boolean hasHashtag = hashtags.contains(hashtagLowerCase);

		return hasHashtag;
	}

	@Override
	public boolean hasKeyword(String keyword) {
		String descriptionLowerCase = getDescription().toLowerCase();
		String keywordLowerCase = keyword.toLowerCase();
		String[] tokenizedKeywords = keywordLowerCase.split(" ");
		boolean isKeywordFound = false;

		for (String token : tokenizedKeywords) {
			isKeywordFound = descriptionLowerCase.contains(token);
			if (isKeywordFound) {
				break;
			}
		}

		return isKeywordFound;
	}

	// Marking Methods

	@Override
	public boolean isAllDay() {
		if (getDateStart() == null || isBlocking() || isRange()) {
			return false;
		}

		boolean isOneSecond = (TaskCatalystCommons.getSeconds(getDateStart()) == 1);
		boolean isZeroMinutes = (TaskCatalystCommons.getMinutes(getDateStart()) == 0);
		boolean isZeroHours = (TaskCatalystCommons.getHours(getDateStart()) == 0);
		boolean isAllDay = (isOneSecond && isZeroMinutes && isZeroHours);

		return isAllDay;
	}

	@Override
	public boolean isBlocking() {
		boolean hasOr = hasWordBetweenDates("or");
		boolean isBlocking = (!isDone() && hasOr);

		return isBlocking;
	}

	@Override
	public boolean isDeadline() {
		boolean hasBefore = hasWordBeforeDates("before");
		boolean hasBy = hasWordBeforeDates("by");
		boolean hasKeywords = (hasBefore || hasBy);
		boolean isDeadline = (!isDone() && hasKeywords);

		return isDeadline;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public boolean isMultiple() {
		boolean isMultiple = hasWordBetweenDates("and");

		return isMultiple;
	}

	@Override
	public boolean isOverdue() {
		boolean isFloatingTask = (getDateEnd() == null);

		if (isFloatingTask) {
			return false;
		}

		boolean isToday = TaskCatalystCommons.isToday(getDateStart());

		if (isAllDay() && isToday) {
			return false;
		}

		Date now = new Date();

		boolean isNowAfterEnd = now.after(getDateEnd());
		boolean isOverdue = (!isDone() && isNowAfterEnd);

		return isOverdue;
	}

	@Override
	public boolean isOverlapping() {
		return isOverlapping;
	}

	@Override
	public boolean isPriority() {
		boolean isPriority = hasHashtag("pri");
		return isPriority;
	}

	@Override
	public boolean isRange() {
		boolean isRange = hasWordBetweenDates("to");
		return isRange;
	}

	private boolean hasWordBetweenDates(String word) {
		String openingBrace = ".*\\}.*(\\b";
		String closingBrace = "\\b\\s)(\\b\\w+\\b\\s){0,2}\\{.*";
		String matchingCriteria = openingBrace + word + closingBrace;

		boolean hasWordBetweenDates = description.matches(matchingCriteria);

		return hasWordBetweenDates;
	}

	private boolean hasWordBeforeDates(String word) {
		String openingBrace = ".*(\\b(";
		String closingBrace = ")\\b\\s)(\\b\\w+\\b\\s){0,2}\\{.*";
		String matchingCriteria = openingBrace + word + closingBrace;

		boolean hasWordBeforeDates = description.matches(matchingCriteria);

		return hasWordBeforeDates;
	}

	@Override
	public int compareTo(Task o) {
		Date thisDateTime = this.getDateStart();
		Date otherDateTime = o.getDateStart();

		boolean isThisDateNull = thisDateTime == null;
		boolean isOtherDateNull = otherDateTime == null;

		if (isThisDateNull && !isOtherDateNull) {
			return 1;
		}

		if (isOtherDateNull && !isThisDateNull) {
			return -1;
		}

		if (isThisDateNull && isOtherDateNull) {
			return 0;
		}

		boolean isSameDate = TaskCatalystCommons.isSameDate(thisDateTime,
				otherDateTime);
		boolean isThisAllDay = isAllDay();
		boolean isOtherAllDay = o.isAllDay();

		if (isSameDate && isThisAllDay && !isOtherAllDay) {
			return -1;
		}

		return thisDateTime.compareTo(otherDateTime);
	}
}
