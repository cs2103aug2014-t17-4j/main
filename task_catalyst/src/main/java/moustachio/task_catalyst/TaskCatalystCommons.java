package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

public class TaskCatalystCommons {

	private static PrettyTimeParser prettyTimeParser = new PrettyTimeParser();
	private static final int INVALID_INTEGER = -1;

	// Command Parsing Methods

	public static int parseInt(String intString) {

		int value;

		try {
			value = Integer.parseInt(intString);
		} catch (Exception e) {
			value = INVALID_INTEGER;
		}

		return value;
	}

	public static String getFirstWord(String userCommand) {

		String oneOrMoreSpaces = "\\s+";

		String[] splitUserCommand = userCommand.split(oneOrMoreSpaces);
		String firstWord = splitUserCommand[0];

		return firstWord;
	}

	public static String removeFirstWord(String userCommand) {

		String invalidCharacters = "(\\[|\\]|\\\\|\\{|\\})";
		String blank = "";

		String firstWord = getFirstWord(userCommand);
		firstWord = firstWord.replaceAll(invalidCharacters, blank);
		String removedFirstWord = userCommand.replaceFirst(firstWord, blank);
		String removedFirstWordTrimmed = removedFirstWord.trim();

		return removedFirstWordTrimmed;
	}

	public static CommandType getCommandType(String userCommand) {

		if (userCommand == null || userCommand.trim().isEmpty()) {
			return CommandType.INVALID;
		}

		String command = getFirstWord(userCommand);
		String parameters = removeFirstWord(userCommand);
		String commandLowerCase = command.toLowerCase();
		String parametersTrimmed = parameters.trim();

		boolean noParameters = parametersTrimmed.isEmpty();

		if (Hashtag.isThisAction(commandLowerCase) && noParameters) {
			return CommandType.HASHTAG;
		} else if (Delete.isThisAction(commandLowerCase)) {
			return CommandType.DELETE;
		} else if (Done.isThisAction(commandLowerCase)) {
			return CommandType.DONE;
		} else if (Edit.isThisAction(commandLowerCase)) {
			return CommandType.EDIT;
		} else if (Redo.isThisAction(commandLowerCase) && noParameters) {
			return CommandType.REDO;
		} else if (Search.isThisAction(commandLowerCase)) {
			return CommandType.SEARCH;
		} else if (Undo.isThisAction(commandLowerCase) && noParameters) {
			return CommandType.UNDO;
		} else {
			return CommandType.ADD;
		}
	}

	// Task Parsing Methods

	// High-Level Interpreted String Parsing Methods

	public static String getInterpretedString(String userInput)
			throws UnsupportedOperationException {

		String interpretedInput = getInterpretedInput(userInput);
		String parsingInput = getParsingInput(interpretedInput);
		List<DateGroup> dateGroups = parseParsingInput(parsingInput);
		interpretedInput = replaceDateStrings(interpretedInput, parsingInput,
				dateGroups);
		interpretedInput = removePrepositionBeforeDateStrings(interpretedInput);

		return interpretedInput;
	}

	// Medium-Level Interpreted String Parsing Methods

	private static String getInterpretedInput(String userInput) {

		String fivePlusDigits = "\\d{5,}";
		String endWithNumber = "[a-zA-Z-_]+[0-9]+";
		String hashtaggedWords = "[#]+[a-zA-Z0-9]+";

		String interpretedInput = userInput;
		interpretedInput = interpretedInput.replaceAll("tmr", "tomorrow");
		interpretedInput = removeConsecutiveWhitespaces(interpretedInput);
		interpretedInput = ignoreBasedOnRegex(interpretedInput, fivePlusDigits);
		interpretedInput = ignoreBasedOnRegex(interpretedInput, endWithNumber);
		interpretedInput = ignoreBasedOnRegex(interpretedInput, hashtaggedWords);

		return interpretedInput;
	}

	private static String getParsingInput(String interpretedInput) {

		String parsingInput = interpretedInput;
		parsingInput = removeIgnoredWords(interpretedInput);
		parsingInput = removeSensitiveParsingWords(parsingInput);
		parsingInput = replaceCommasWithAnd(parsingInput);
		parsingInput = removeConsecutiveAnds(parsingInput);
		parsingInput = removeConsecutiveWhitespaces(parsingInput);

		return parsingInput;
	}

	private static List<DateGroup> parseParsingInput(String parsingInput) {
		return prettyTimeParser.parseSyntax(parsingInput);
	}

	private static String replaceDateStrings(String interpretedInput,
			String parsingInput, List<DateGroup> dateGroups)
			throws UnsupportedOperationException {

		int dateGroupCount = dateGroups.size();

		for (DateGroup dateGroup : dateGroups) {

			boolean wholeMatch = isWholeMatch(parsingInput, dateGroup);
			boolean longMatch = isLongMatch(dateGroup);
			boolean isValidDateGroup = longMatch && wholeMatch;

			if (isValidDateGroup) {

				List<Date> dates = dateGroup.getDates();
				sortDates(dates);
				removeRepeatedDates(dates);

				int dateCount = dates.size();
				String matchingText = dateGroup.getText();

				String connector = getConnector(dateGroupCount, dateCount,
						matchingText);

				String dateString = getDateString(dates, connector);

				interpretedInput = replaceDateString(interpretedInput,
						matchingText, dateString);
			}
		}

		return interpretedInput;
	}

	private static String getConnector(int dateGroupCount, int dateCount,
			String matchingText) throws UnsupportedOperationException {

		String finalConnector;

		boolean isDateRange = matchingText.contains(" to ");
		boolean multipleDateGroups = dateGroupCount > 1;
		boolean moreThanTwoDates = dateCount > 2;
		boolean isInvalidDateRange = multipleDateGroups || moreThanTwoDates;

		if (isDateRange && isInvalidDateRange) {
			throw new UnsupportedOperationException();
		} else if (isDateRange) {
			finalConnector = " to ";
		} else {
			finalConnector = " and ";
		}

		return finalConnector;
	}

	private static String removePrepositionBeforeDateStrings(
			String interpretedInput) {
		interpretedInput = removePrepositionBeforeDateString(interpretedInput,
				"on");
		interpretedInput = removePrepositionBeforeDateString(interpretedInput,
				"at");
		return interpretedInput;
	}

	// Low-Level Interpreted String Parsing Methods

	private static void sortDates(List<Date> dates) {
		Collections.sort(dates);
	}

	private static void removeRepeatedDates(List<Date> dates) {
		int j = 0;
		while (j < dates.size() - 1) {
			if (dates.get(j).equals(dates.get(j + 1))) {
				dates.remove(j);
			} else {
				j++;
			}
		}
	}

	// Generates a single Date String for a DateGroup.
	// Example Date String: {date}, {date} and {date}
	private static String getDateString(List<Date> dates, String finalConnector) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy KK:mm a");
		int dateCount = dates.size();

		String intermediateConnector = ", ";
		String dateString = "";

		for (int i = 0; i < dateCount; i++) {
			Date date = dates.get(i);

			dateString += "{" + formatter.format(date) + "}";

			boolean isSecondLastDate = i == dateCount - 2;
			boolean isLastDate = i == dateCount - 1;

			if (isSecondLastDate) {
				dateString += finalConnector;
			} else if (!isLastDate) {
				dateString += intermediateConnector;
			}
		}
		return dateString;
	}

	// Replace corresponding matching text with date string.
	private static String replaceDateString(String interpretedInput,
			String matchingText, String dateString) {

		String wordBoundaryStart = "(^|\\b)";
		String wordBoundaryEnd = "(\\b|$)";
		String onlyOutsideBrackets = "(?=[^\\]]*(\\[|$))";

		String matchingExpression = matchingText;
		matchingExpression = removeAllAnds(matchingExpression);
		matchingExpression = removeConsecutiveWhitespaces(matchingExpression);
		matchingExpression = replaceSpacesWithWildcard(matchingExpression);
		matchingExpression = wordBoundaryStart + matchingExpression;
		matchingExpression = matchingExpression + wordBoundaryEnd;
		matchingExpression = matchingExpression + onlyOutsideBrackets;

		interpretedInput = interpretedInput.replaceAll(matchingExpression,
				dateString);

		return interpretedInput;
	}

	private static String removePrepositionBeforeDateString(
			String interpretedInput, String preposition) {

		String wordBoundary = "(^|\\b)";
		String openingCurlyBrace = "\\{";

		return interpretedInput.replaceAll(wordBoundary + preposition + " "
				+ openingCurlyBrace, openingCurlyBrace);
	}

	private static String removeSensitiveParsingWords(String parsingInput) {
		return parsingInput.replaceAll("(\\b)(at|in|from|on)( |$)", " ");
	}

	private static String removeIgnoredWords(String interpretedInput) {
		return interpretedInput.replaceAll("\\[(.*?)\\]", "");
	}

	private static String removeConsecutiveWhitespaces(String interpretedInput) {
		return interpretedInput.replaceAll("\\s+", " ");
	}

	private static String removeAllAnds(String matchingExpression) {
		return matchingExpression.replaceAll(" and ", " ");
	}

	private static String removeConsecutiveAnds(String parsingInput) {
		return parsingInput.replaceAll("(\\b)(and)+", " and ");
	}

	private static String replaceSpacesWithWildcard(String matchingExpression) {
		return matchingExpression.replaceAll(" ",
				"( |,|, )?(at|from|and)?( on)?( )");
	}

	private static String replaceCommasWithAnd(String parsingInput) {
		return parsingInput.replaceAll(", ", " and ");
	}

	private static boolean isLongMatch(DateGroup dateGroup) {
		return dateGroup.getText().length() > 2;
	}

	// This checks if the match is exact
	private static boolean isWholeMatch(String parsingInput, DateGroup dateGroup) {
		String extendedText = extendMatch(parsingInput, dateGroup);
		String originalText = Pattern.quote(dateGroup.getText());
		String startWordBoundary = ".*(^|\\b)";
		String endWordBoundary = "(\\b|$).*";

		boolean wholeWord = extendedText.matches(startWordBoundary
				+ originalText + endWordBoundary);
		return wholeWord;
	}

	// This is used to extend a match of dateGroup by 1 on both sides.
	private static String extendMatch(String parsingInput, DateGroup dateGroup) {
		int position = dateGroup.getPosition();
		int length = dateGroup.getText().length();
		int startIndex = Math.max(0, position - 1);
		int endIndex = Math.min(position + length + 1, parsingInput.length());
		String extendedText = parsingInput.substring(startIndex, endIndex);
		return extendedText;
	}

	// Add ignore brackets ([]) around anything in input matching the regex.
	public static String ignoreBasedOnRegex(String input, String regex) {
		String ignoredString = input;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			String matching = matcher.group();
			String replacement = "[" + matching + "]";
			ignoredString = ignoredString.replace(matching, replacement);
		}
		return ignoredString;
	}

	// High-Level Pretty String Methods

	public static String getPrettyString(String userInput) {
		String editedUserInput = userInput;

		List<DateGroup> dateGroups = getPrettyStringDateGroups(userInput);

		Date previousDate = null;
		for (int i = 0; i < dateGroups.size(); i++) {

			Date currentDate = dateGroups.get(i).getDates().get(0);
			Date nextDate;

			try {
				nextDate = dateGroups.get(i + 1).getDates().get(0);
			} catch (Exception e) {
				nextDate = null;
			}

			SimpleDateFormat formatter = generateFormatter(previousDate,
					currentDate, nextDate);

			editedUserInput = editedUserInput.replace(dateGroups.get(i)
					.getText(), formatter.format(currentDate));

			previousDate = currentDate;
		}
		editedUserInput = replacePrettyStringPrepositions(editedUserInput);
		return editedUserInput;
	}

	// Low-Level Pretty String Methods

	private static String replacePrettyStringPrepositions(String editedUserInput) {
		editedUserInput = editedUserInput
				.replaceAll("by \\{(on|at) ", "by \\{");
		editedUserInput = editedUserInput
				.replaceAll("to \\{(on|at) ", "to \\{");
		editedUserInput = editedUserInput.replaceAll("from \\{(on|at) ",
				"from \\{");
		return editedUserInput;
	}

	private static SimpleDateFormat generateFormatter(Date previousDate,
			Date currentDate, Date nextDate) {
		String formatString = generateFormatString(previousDate, currentDate,
				nextDate);

		SimpleDateFormat formatter = new SimpleDateFormat(formatString);
		return formatter;
	}

	private static String generateFormatString(Date previousDate,
			Date currentDate, Date nextDate) {
		String formatString = "";
		if (!isSameDate(previousDate, currentDate)) {
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
		return formatString;
	}

	private static List<DateGroup> getPrettyStringDateGroups(String userInput) {
		String textInCurlyBraces = "\\{(.*?)\\}";
		Pattern pattern = Pattern.compile(textInCurlyBraces);
		Matcher matcher = pattern.matcher(userInput);
		DateGroup previousGroup = null;
		List<DateGroup> dateGroups = new ArrayList<DateGroup>();
		while (matcher.find()) {
			if (previousGroup == null) {
				String matching = matcher.group();
				dateGroups.addAll(new PrettyTimeParser().parseSyntax(matching));
			}
		}
		return dateGroups;
	}

	public static String removeSquareBrackets(String userInput) {
		return userInput.replaceAll("\\[|\\]", "");
	}

	public static String removeCurlyBraces(String userInput) {
		return userInput.replaceAll("\\{|\\}", "");
	}

	// Date Time Libraries

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

	public static int daysFromToday(Date date) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(new Date());
		return cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR)
				+ (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 365;
	}

	public static int daysFromToday(LocalDateTime date) {
		ZonedDateTime zdt = date.atZone(ZoneId.systemDefault());
		Date output = Date.from(zdt.toInstant());
		return daysFromToday(output);
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
		sortDates(allDates);
		return allDates;
	}
}
