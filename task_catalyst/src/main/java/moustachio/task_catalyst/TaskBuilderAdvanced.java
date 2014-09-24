package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

public class TaskBuilderAdvanced implements TaskBuilder {

	public Task createTask(String userInput) {

		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		return new TaskAdvanced(
				TaskBuilderAdvanced.interpretedString(userInput));
	}

	public static String interpretedString(String userInput) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy KK:mm a");
		String interpretedInput = userInput;
		interpretedInput = interpretedInput.replaceAll(",", " and "); // Change
																		// , to
																		// and.
		interpretedInput = interpretedInput.replaceAll("\\s+", " "); // Fix
																		// consecutive
																		// whitespaces.
		String parsingInput = interpretedInput.replaceAll("\\[(.*?)\\]", ""); // Remove
																				// all
																				// items
																				// in
																				// brackets.
		parsingInput = parsingInput.replaceAll(" to |on", " *e*s*c*a*p*e* ")
				.replaceAll("\\s+", " ");
		parsingInput = parsingInput.replaceAll("\\d{5,}", "");

		List<DateGroup> dateGroups = new PrettyTimeParser()
				.parseSyntax(parsingInput);
		for (DateGroup dateGroup : dateGroups) {
			List<Date> dates = dateGroup.getDates();
			int dateCount = dates.size();
			String dateString = "";
			String finalConnector;
			if (dateGroup.getText().contains(" to ")) {
				finalConnector = " to ";
			} else {
				finalConnector = " and ";
			}
			for (int i = 0; i < dateCount; i++) {
				dateString += "{" + formatter.format(dates.get(i)) + "}";
				if (i == dateCount - 2) {
					dateString += finalConnector;
				} else if (i != dateCount - 1) {
					dateString += ", ";
				}
			}
			// This is a quickfix for bad matches. (most of them are 1 or 2
			// characters long)
			if (dateGroup.getText().length() > 2) {
				interpretedInput = interpretedInput.replaceAll(
						dateGroup.getText(), dateString);
			} else {
				interpretedInput = interpretedInput.replaceAll(
						dateGroup.getText() + "(?=[^\\}]*(\\{|$))", "["
								+ dateGroup.getText() + "]");
			}
		}
		return interpretedInput;
	}

	public static String prettyString(String userInput) {
		String editedUserInput = userInput;
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(userInput);
		DateGroup previousGroup = null;
		List<DateGroup> dateGroups = new ArrayList<DateGroup>();
		while (matcher.find()) {
			if (previousGroup == null) {
				String matching = matcher.group();
				dateGroups.addAll(new PrettyTimeParser().parseSyntax(matching));
			}
		}
		Date previousDate = null;
		for (int i = 0; i < dateGroups.size(); i++) {
			Date currentDate = dateGroups.get(i).getDates().get(0);
			Date nextDate;
			try {
				nextDate = dateGroups.get(i + 1).getDates().get(0);
			} catch (Exception e) {
				nextDate = null;
			}
			String formatString = "";
			if (!isSameTime(currentDate, nextDate)) {
				formatString = formatString + "h";
				if (hasMinutes(currentDate)) {
					formatString = formatString + ":mm";
				}
				formatString = formatString + "a";
			}
			if (!isSameDate(previousDate, currentDate)) {
				if (!formatString.isEmpty()) {
					formatString = " " + formatString;
				}
				// Can add some more, like yesterday, last Tuesday, etc.
				if (isToday(currentDate)) {
					formatString = "'today'" + formatString;
				} else if (isTomorrow(currentDate)) {
					formatString = "'tomorrow'" + formatString;
				} else if (isThisWeek(currentDate)) {
					formatString = "E" + formatString;
				} else {
					formatString = "d MMM" + formatString;
				}
			}
			SimpleDateFormat formatter = new SimpleDateFormat(formatString);
			editedUserInput = editedUserInput.replace(dateGroups.get(i)
					.getText(), formatter.format(currentDate));
			previousDate = currentDate;
		}
		return editedUserInput;
	}

	public static String removeSquareBrackets(String userInput) {
		return userInput.replaceAll("\\[|\\]", "");
	}

	public static String removeCurlyBraces(String userInput) {
		return userInput.replaceAll("\\{|\\}", "");
	}

	private static boolean isSameTime(Date date, Date date2) {
		if (date == null || date2 == null) {
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(date2);
		return cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
				&& cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
	}

	private static boolean isSameDate(Date date, Date date2) {
		if (date == null || date2 == null) {
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(date2);
		return cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
	}

	private static boolean hasMinutes(Date date) {
		return getMinutes(date) != 0;
	}

	private static int getMinutes(Date date) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		return cal1.get(Calendar.MINUTE);
	}

	private static boolean isToday(Date date) {
		return daysFromToday(date) == 0;
	}

	private static boolean isTomorrow(Date date) {
		return daysFromToday(date) == 1;
	}

	private static boolean isThisWeek(Date date) {
		return daysFromToday(date) <= 6 && daysFromToday(date) > 0;
	}

	private static int daysFromToday(Date date) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(new Date());
		// Can possibly explode if they are of different years, untested.
		return cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR)
				+ (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 365;
	}

	public static List<Date> getAllDates(String interpretedString) {
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(interpretedString);
		DateGroup previousGroup = null;
		List<DateGroup> dateGroups = new ArrayList<DateGroup>();
		while (matcher.find()) {
			if (previousGroup == null) {
				String matching = matcher.group();
				dateGroups.addAll(new PrettyTimeParser().parseSyntax(matching));
			}
		}
		List<Date> allDates = new ArrayList<Date>();
		for (int i = 0; i < dateGroups.size(); i++) {
			Date currentDate = dateGroups.get(i).getDates().get(0);
			allDates.add(currentDate);
		}
		Collections.sort(allDates);
		return allDates;
	}
}
