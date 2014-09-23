package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

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
		String editedUserInput = this.description.get();
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(this.description.get());
		while (matcher.find()) {
			String word = matcher.group(1);
			List<DateGroup> currentGroupDates = new PrettyTimeParser()
					.parseSyntax(word);
			for (DateGroup group : currentGroupDates) {
				List<Date> dates = group.getDates();
				String formatString = "";
				SimpleDateFormat fmt;
				if (dates.size() == 1) {
					if (isToday(dates.get(0))) {
						formatString += "'today'";
					} else if (isTomorrow(dates.get(0))) {
						formatString += "'tomorrow'";
					} else if (isThisWeek(dates.get(0))) {
						formatString += "E";
					} else {
						formatString += "dd MMM";
					}
					formatString += " h";
					if (hasMinutes(dates.get(0))) {
						formatString += ":mm";
					}
					formatString += "a";
					fmt = new SimpleDateFormat(formatString);
					editedUserInput = editedUserInput.replaceFirst(
							word,
							word.substring(0, group.getPosition())
									+ fmt.format(dates.get(0)));
				} else if (dates.size() == 2) {
					if (isToday(dates.get(0))) {
						formatString += "'today'";
					} else if (isTomorrow(dates.get(0))) {
						formatString += "'tomorrow'";
					} else if (isThisWeek(dates.get(0))) {
						formatString += "E";
					} else {
						formatString += "dd MMM";
					}
					if (!isSameTime(dates.get(0), dates.get(1))) {
						formatString += " h";
						if (hasMinutes(dates.get(0))) {
							formatString += ":mm";
						}
						formatString += "a";
					}

					String formatString2 = "";
					if (!isSameDate(dates.get(0), dates.get(1))) {
						if (isToday(dates.get(1))) {
							formatString2 += "'today'";
						} else if (isTomorrow(dates.get(0))) {
							formatString2 += "'tomorrow'";
						} else if (isThisWeek(dates.get(0))) {
							formatString2 += "E";
						} else {
							formatString2 += "dd MMM";
						}
						formatString2 += " ";
					}
					formatString2 += "h";
					if (hasMinutes(dates.get(0))) {
						formatString2 += ":mm";
					}
					formatString2 += "a";

					fmt = new SimpleDateFormat(formatString);
					SimpleDateFormat fmt2 = new SimpleDateFormat(formatString2);
					editedUserInput = editedUserInput.replaceFirst(
							word,
							word.substring(0, group.getPosition())
									+ fmt.format(dates.get(0)) + " to "
									+ fmt2.format(dates.get(1)));
				}
			}
		}
		return editedUserInput;
	}

	private boolean isSameTime(Date date, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(date2);
		return cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR) && cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
	}

	private boolean isSameDate(Date date, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(date2);
		return cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
	}

	private boolean hasMinutes(Date date) {
		return getMinutes(date) != 0;
	}

	private int getMinutes(Date date) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		return cal1.get(Calendar.MINUTE);
	}

	private boolean isToday(Date date) {
		return daysFromToday(date) == 0;
	}

	private boolean isTomorrow(Date date) {
		return daysFromToday(date) == 1;
	}

	private boolean isThisWeek(Date date) {
		return daysFromToday(date) <= 6;
	}

	private int daysFromToday(Date date) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(new Date());
		//Can possibly explode if they are of different years, untested.
		return cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR) + (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR))*365 ;
	}
}
