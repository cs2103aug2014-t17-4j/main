package moustachio.task_catalyst;

import java.util.Date;
import java.util.List;

//@author A0111890L
/**
 * TaskAdvanced is a Task object created by the TaskBuilderAdvanced. It stores
 * information in the form of Interpreted Strings to facilitate Relative Date
 * Display. Descriptions, dates and hashtags are populated upon initialization
 * or modification of the stored description;
 */
public class TaskAdvanced implements Task {
	String description;
	Date dateStart;
	Date dateEnd;
	List<Date> allDates;
	List<String> hashtags;

	boolean isOverlapping;
	boolean isDone;
	boolean isError;

	// Initialization Methods

	public TaskAdvanced(String description) {
		setDescription(description);
	}

	private void initializeAttributes(String description) {
		this.description = description;
		this.isDone = false;
	}

	private void initializeFields() {
		String interpretedString = getDescriptionRaw();

		hashtags = TaskCatalystCommons.getAllHashtags(interpretedString);
		allDates = TaskCatalystCommons.getAllDates(interpretedString);

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
				.getDisplayStringWithoutDate(interpretedString);

		return description;
	}

	@Override
	public String getDescriptionEdit() {
		String interpretedString = getDescriptionRaw();

		boolean isAlwaysShowTime = true;

		String prettyString = TaskCatalystCommons.getRelativeString(
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
		initializeFields();
	}

	@Override
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	@Override
	public void setError(boolean isError) {
		this.isError = isError;
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
		String keywordProcessed = keyword.toLowerCase();
		keywordProcessed = TaskCatalystCommons
				.removeCurlyBraces(keywordProcessed);
		keywordProcessed = TaskCatalystCommons
				.removeSquareBrackets(keywordProcessed);
		String[] tokenizedKeywords = keywordProcessed.split("\\s+");
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
		boolean hasOr = TaskCatalystCommons.hasWordBetweenDates(
				getDescriptionRaw(), "or");
		boolean isBlocking = (!isDone() && hasOr);

		return isBlocking;
	}

	@Override
	public boolean isDeadline() {
		boolean hasBefore = TaskCatalystCommons.hasWordBeforeDates(
				getDescriptionRaw(), "before");
		boolean hasBy = TaskCatalystCommons.hasWordBeforeDates(
				getDescriptionRaw(), "by");
		boolean hasKeywords = (hasBefore || hasBy);
		boolean isDeadline = (!isDone() && hasKeywords);

		return isDeadline;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public boolean isError() {
		return isError;
	}

	@Override
	public boolean isMultiple() {
		boolean isMultiple = TaskCatalystCommons.hasWordBetweenDates(
				getDescriptionRaw(), "and");

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
		boolean isRange = TaskCatalystCommons.hasWordBetweenDates(
				getDescriptionRaw(), "to");
		return isRange;
	}

	@Override
	public int compareTo(Task o) {
		if (isError()) {
			return -1;
		}
		
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
