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
		// interpretedInput = interpretedInput.replaceAll(",", " and "); //
		// Change
		// , to
		// and.

		interpretedInput = interpretedInput.replaceAll("\\s+", " "); // Fix
																		// consecutive
																		// whitespaces.

		interpretedInput = interpretedInput.replaceAll("tmr", "tomorrow");

		interpretedInput = ignoreBasedOnRegex(interpretedInput, "\\d{5,}"); // Fix
																			// handphone
																			// number
																			// problem
		/*
		 * interpretedInput = ignoreBasedOnRegex(interpretedInput,
		 * "[a-zA-Z-_]+[0-9]+"); // Ignore anything ending with numbers. //
		 * Fixes LT5 problem.
		 * 
		 * interpretedInput = ignoreBasedOnRegex(interpretedInput,
		 * "[#][a-zA-Z0-9]+"); // Ignore anything with hashtag
		 * 
		 * /* interpretedInput = ignoreBasedOnRegex(interpretedInput,
		 * "[a-zA-Z]*tomorrow[a-zA-Z]+|[a-zA-Z]+tomorrow[a-zA-Z]*"); // Ignore
		 * // anything // containing // tomorrow
		 */

		String parsingInput = interpretedInput.replaceAll("\\[(.*?)\\]", ""); // Remove
																				// all
																				// items
																				// in
																				// brackets.
		// parsingInput = parsingInput.replaceAll(" to ", " *e*s*c*a*p*e* ");
		parsingInput = parsingInput.replaceAll(" at | at$| in | in$| from |from$", " ");
		parsingInput = parsingInput.replaceAll(", "," and ");
		// parsingInput = parsingInput.replaceAll("#(\\S+)", "");
		parsingInput = parsingInput.replaceAll("( and)+", " and ");
		parsingInput = parsingInput.replaceAll("( on)+", " ");
		parsingInput = parsingInput.replaceAll("\\s+", " ");

		List<DateGroup> dateGroups = new PrettyTimeParser()
				.parseSyntax(parsingInput);

		for (DateGroup dateGroup : dateGroups) {

			// This fixes the partial matching problem, for almost everything! OMG!
			int position = dateGroup.getPosition();
			int length = dateGroup.getText().length();
			int startIndex = Math.max(0,position-5);
			int endIndex = Math.min(position+length+5,parsingInput.length());
			boolean wholeWord = parsingInput.substring(startIndex,endIndex).matches(".*(^|\\b)"
					+ Pattern.quote(dateGroup.getText()) + "(\\b|$).*");

			// This is a quickfix for bad matches. (most of them are below 4
			// characters. This fixes 5 apples problem.
			boolean longMatch = dateGroup.getText().length() > 2;
			
			if (longMatch && wholeWord) {
				List<Date> dates = dateGroup.getDates();
				Collections.sort(dates);
				int dateCount = dates.size();
				String dateString = "";
				String finalConnector;
				String intermediateConnector;
				if (dateGroup.getText().contains(" to ")) {
					finalConnector = " to ";
					intermediateConnector = ", ";
				} else {
					finalConnector = " and ";
					intermediateConnector = ", ";
				}
				for (int i = 0; i < dateCount; i++) {
					dateString += "{" + formatter.format(dates.get(i)) + "}";
					if (i == dateCount - 2) {
						dateString += finalConnector;
					} else if (i != dateCount - 1) {
						dateString += intermediateConnector;
					}
				}
				//interpretedInput = interpretedInput.replaceAll(dateGroup.getText() + "(?=[^\\]]*(\\[|$))", dateString);
				String onlyOutsideBrackets = "(?=[^\\]]*(\\[|$))";
				//String matchingText = Pattern.quote(dateGroup.getText());

				String matchingText = dateGroup.getText().replaceAll(" and ", " ").replaceAll("\\s+", " ").replaceAll(" ", "( | at | from |, | and |, and |, and on | and on )?");
				System.out.println(interpretedInput);
				System.out.println(parsingInput);
				System.out.println(matchingText);
				interpretedInput = interpretedInput.replaceAll("(^|\\b)"+matchingText+"(\\b|$)"+onlyOutsideBrackets, dateString);
			}
		}
		interpretedInput = interpretedInput.replaceAll("at \\{", "\\{");
		interpretedInput = interpretedInput.replaceAll("on \\{", "\\{");
		return interpretedInput;
	}

	public static String ignoreBasedOnRegex(String input, String regex) {
		String ignoredString = input;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		DateGroup previousGroup = null;
		while (matcher.find()) {
			if (previousGroup == null) {
				String matching = matcher.group();
				ignoredString = ignoredString.replace(matching, "[" + matching
						+ "]");
			}
		}
		return ignoredString;
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
			if (!isSameDate(previousDate, currentDate)) {
				// Can add some more, like yesterday, last Tuesday, etc.
				if (isToday(currentDate)) {
					formatString = "'today'";
				} else if (isTomorrow(currentDate)) {
					formatString = "'tomorrow'";
				} else if (isThisWeek(currentDate)) {
					formatString = "'on' E";
				} else {
					formatString = "'on' d MMM";
				}

				if (!isThisYear(currentDate)) {
					formatString = formatString + " yyyy";
				}
			}
			if (!isSameTime(currentDate, nextDate)) {
				if (!formatString.isEmpty()) {
					formatString = formatString + " ";
				}
				formatString = formatString + "h";
				if (hasMinutes(currentDate)) {
					formatString = formatString + ":mm";
				}
				formatString = formatString + "a";
			}

			SimpleDateFormat formatter = new SimpleDateFormat(formatString);
			editedUserInput = editedUserInput.replace(dateGroups.get(i)
					.getText(), formatter.format(currentDate));
			previousDate = currentDate;
		}
		editedUserInput = editedUserInput.replaceAll("by \\{on ", "by \\{");
		editedUserInput = editedUserInput.replaceAll("by \\{at ", "by \\{");
		editedUserInput = editedUserInput.replaceAll("to \\{on ", "to \\{");
		editedUserInput = editedUserInput.replaceAll("to \\{at ", "to \\{");
		editedUserInput = editedUserInput.replaceAll("from \\{on ", "from \\{");
		editedUserInput = editedUserInput.replaceAll("from \\{at ", "from \\{");
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

	private static boolean isThisYear(Date date) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(new Date());
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
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
