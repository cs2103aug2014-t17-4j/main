package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

public class TaskCatalystCommons {

	private static final String ERROR_NO_DESCRIPTION = "Please type in some descriptions for the task.";
	private static final String ERROR_MULTIPLE_CHUNKS = "Please keep all date information together.";
	private static final String ERROR_DEFAULT_HASHTAGS = "Please remove default hashtags from your task description.";
	private static final String ERROR_MIX_TYPES = "Please only specify one pair of date ranges per task and do not mix date types.";
	private static final int INVALID_INTEGER = -1;

	private static PrettyTimeParser prettyTimeParser = new PrettyTimeParser();
	private static Calendar cal1 = Calendar.getInstance();
	private static Calendar cal2 = Calendar.getInstance();

	// Command Parsing Methods

	public static int parsePositiveInt(String intString) {

		int value;

		try {
			value = Integer.parseInt(intString);
		} catch (Exception e) {
			value = INVALID_INTEGER;
		}

		if (value <= 0) {
			value = INVALID_INTEGER;
		}

		return value;
	}

	public static List<Integer> parsePositiveIntList(String intListString) {
		String intListStringProcessed = intListString;
		intListStringProcessed = intListStringProcessed.replaceAll(",", " ");
		String[] splitIntStrings = intListStringProcessed.split("\\s+");
		List<Integer> parsedIntegers = new ArrayList<Integer>();
		for (String intString : splitIntStrings) {
			int parsedPositiveInt = parsePositiveInt(intString);
			if (parsedPositiveInt > 0) {
				if (!parsedIntegers.contains(parsedPositiveInt)) {
					parsedIntegers.add(parsedPositiveInt);
				}
			}
		}
		return parsedIntegers;
	}

	public static String getFirstWord(String userCommand) {

		String oneOrMoreSpaces = "\\s+";

		String[] splitUserCommand = userCommand.split(oneOrMoreSpaces);
		String firstWord = splitUserCommand[0];

		return firstWord;
	}

	public static String removeFirstWord(String userCommand) {
		String blank = "";

		String firstWord = getFirstWord(userCommand);
		firstWord = Pattern.quote(firstWord);
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
		} else if (Undone.isThisAction(commandLowerCase)) {
			return CommandType.UNDONE;
		} else {
			return CommandType.ADD;
		}
	}

	// Task Parsing Methods

	// High-Level Interpreted String Parsing Methods

	public static String getInterpretedString(String userInput)
			throws UnsupportedOperationException {
		String interpretedString = userInput;
		interpretedString = getInterpretedStringSingleIteration(interpretedString);
		interpretedString = removeCurlyBraces(interpretedString);
		interpretedString = getInterpretedStringSingleIteration(interpretedString);
		exceptionIfInvalidRange(interpretedString);
		exceptionIfOverlappingDates(interpretedString);
		exceptionIfContainsDefaultHashtag(interpretedString);
		exceptionIfMultipleChunks(interpretedString);
		exceptionIfDescriptionEmpty(interpretedString);
		return interpretedString;
	}

	public static String getInterpretedStringSingleIteration(
			String interpretedString) {
		String interpretedStringNextPass = getInterpretedStringSinglePass(interpretedString);
		while (!interpretedStringNextPass.equals(interpretedString)) {
			interpretedString = interpretedStringNextPass;
			interpretedStringNextPass = getInterpretedStringSinglePass(interpretedStringNextPass);
		}
		return interpretedString;
	}

	public static String getInterpretedStringSinglePass(String userInput)
			throws UnsupportedOperationException {

		String interpretedInput = getInterpretedInput(userInput);
		String parsingInput = getParsingInput(interpretedInput);
		List<DateGroup> dateGroups = parseParsingInput(parsingInput);
		interpretedInput = replaceDateStrings(interpretedInput, parsingInput,
				dateGroups);
		return interpretedInput;
	}

	// Medium-Level Interpreted String Parsing Methods

	private static String getInterpretedInput(String userInput) {

		String notHashtagged = "(?<!#)";
		String fivePlusDigits = notHashtagged + "\\d{5,}";
		String endWithNumber = notHashtagged + "(?<!#)\\b[a-zA-Z-_$]+\\d+\\b";
		String wordsContainingEst = notHashtagged + "(?<!#)\\w*est\\w*";
		String wordsContainingAted = notHashtagged + "(?<!#)\\w*ated\\w*";

		String interpretedInput = userInput;
		interpretedInput = interpretedInput.replaceAll("\\} \\{", "\\}, \\{");
		interpretedInput = interpretedInput.replaceAll(",", ", ");
		interpretedInput = interpretedInput.replaceAll("\\s+,+", ",");
		interpretedInput = ignoreBasedOnRegex(interpretedInput,
				wordsContainingEst);
		interpretedInput = ignoreBasedOnRegex(interpretedInput,
				wordsContainingAted);
		interpretedInput = ignoreBasedOnRegex(interpretedInput, fivePlusDigits);
		interpretedInput = ignoreBasedOnRegex(interpretedInput, endWithNumber);
		interpretedInput = removeConsecutiveWhitespaces(interpretedInput);
		interpretedInput = replaceYesterday(interpretedInput);

		return interpretedInput;
	}

	private static String getParsingInput(String interpretedInput) {

		String parsingInput = interpretedInput;
		parsingInput = removeWordsInBrackets(interpretedInput);
		// parsingInput = parsingInput.replaceAll(
		// "(\\d{1,2}(am|pm))(\\s|$)(?![a-zA-Z])", "$1,");
		// parsingInput = parsingInput.replaceAll(
		// "(?<!:)(\\d{2}:\\d{2}(am|pm)?)(?!:)", " $1,");
		// parsingInput = parsingInput.replaceAll(
		// "(\\d{4}(am|pm)?)(?!\\s\\d{1,2}(:|am|pm))", " $1,");
		parsingInput = removeHashtaggedWords(parsingInput);
		parsingInput = removeSensitiveParsingWords(parsingInput);
		parsingInput = removeNumberWords(parsingInput);
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

		for (DateGroup dateGroup : dateGroups) {

			boolean wholeMatch = isWholeMatch(parsingInput, dateGroup);
			boolean longMatch = isLongMatch(dateGroup);
			boolean isValidDateGroup = longMatch && wholeMatch;

			if (isValidDateGroup) {

				List<Date> dates = dateGroup.getDates();
				sortDates(dates);
				removeRepeatedDates(dates);
				truncateDateWithoutTime(dates);

				String matchingText = dateGroup.getText();

				String connector = getConnector(matchingText);

				String dateString = getDateString(dates, connector);

				interpretedInput = replaceDateString(interpretedInput,
						matchingText, dateString);

			}
		}

		interpretedInput = removePrepositionBeforeDateStrings(interpretedInput);

		return interpretedInput;
	}

	private static String getConnector(String matchingText) {

		String connector;

		boolean isContainsTo = matchingText.contains(" to ");
		boolean isContainsOr = matchingText.contains(" or ");
		boolean isContainsDash = matchingText.contains(" - ");
		boolean isDateRange = isContainsTo || isContainsDash;

		if (isDateRange) {
			connector = " to ";
		} else if (isContainsOr) {
			connector = " or ";
		} else {
			connector = " and ";
		}

		return connector;
	}

	private static String removePrepositionBeforeDateStrings(
			String interpretedInput) {

		interpretedInput = removePrepositionBeforeDateString(interpretedInput,
				"on");

		interpretedInput = removePrepositionBeforeDateString(interpretedInput,
				"at");

		return interpretedInput;
	}

	private static void exceptionIfInvalidRange(String interpretedStringNextPass)
			throws UnsupportedOperationException {
		// isRange is true when there exists a "to" between two dates,
		// with the "to" at most 2 words away from the second date.
		String rangeCondition = ".*\\}.*(\\bto\\b\\s)(\\b\\w*\\b\\s){0,2}\\{.*";
		boolean isRange = interpretedStringNextPass.matches(rangeCondition);
		boolean isMoreThanTwoDates = getAllDates(interpretedStringNextPass)
				.size() > 2;
		if (isRange && isMoreThanTwoDates) {
			throw new UnsupportedOperationException(ERROR_MIX_TYPES);
		}
	}

	private static void exceptionIfOverlappingDates(
			String interpretedStringNextPass)
			throws UnsupportedOperationException {
		List<Date> dates = getAllDates(interpretedStringNextPass);
		int j = 0;
		while (j < dates.size() - 1) {

			// This is necessary because PrettyTime may return varying
			// milliseconds.

			boolean isSameDate = TaskCatalystCommons.isSameDate(dates.get(j),
					dates.get(j + 1));
			boolean isSameTime = TaskCatalystCommons.isSameTime(dates.get(j),
					dates.get(j + 1));

			if (isSameDate && isSameTime) {
				throw new UnsupportedOperationException(
						"Please resolve overlapping dates in the task.");
			} else {
				j++;
			}
		}
	}

	private static void exceptionIfContainsDefaultHashtag(
			String interpretedStringNextPass)
			throws UnsupportedOperationException {
		TaskManager taskManager = TaskManagerActual.getInstance();
		String[] defaultHashtags = taskManager.getDefaultHashtags();
		String lowerCaseString = interpretedStringNextPass.toLowerCase();
		boolean isContainsDefaultHashtag = false;
		for (String hashtag : defaultHashtags) {
			String currentHashtag = "[" + hashtag + "]";
			boolean isHashtagFound = lowerCaseString.contains(currentHashtag);
			boolean isPriority = hashtag.equals("#pri");
			if (isHashtagFound && !isPriority) {
				isContainsDefaultHashtag = true;
				break;
			}
		}
		if (isContainsDefaultHashtag) {
			throw new UnsupportedOperationException(ERROR_DEFAULT_HASHTAGS);
		}
	}

	private static void exceptionIfMultipleChunks(
			String interpretedStringNextPass)
			throws UnsupportedOperationException {

		String prepositions = "(,|and|or|to) \\{";
		String spacesAfterBraces = "\\}(,)?(\\s)?";
		String consecutiveCurly = "\\}\\{";
		String textBetweenCurly = ".*\\}(.*?)\\{.*";

		String processingString = interpretedStringNextPass;
		processingString = processingString.replaceAll(prepositions, "\\{");
		processingString = processingString
				.replaceAll(spacesAfterBraces, "\\}");
		processingString = processingString.replaceAll(consecutiveCurly, "");
		boolean isMultipleChunk = processingString.matches(textBetweenCurly);
		if (isMultipleChunk) {
			throw new UnsupportedOperationException(ERROR_MULTIPLE_CHUNKS);
		}
	}

	private static void exceptionIfDescriptionEmpty(
			String interpretedStringNextPass)
			throws UnsupportedOperationException {

		boolean isAlwaysShowTime = false;
		String prettyString = getPrettyString(interpretedStringNextPass,
				isAlwaysShowTime);
		String withoutDates = getPrettyStringWithoutDate(prettyString);
		withoutDates = withoutDates.trim();
		if (withoutDates.isEmpty()) {
			throw new UnsupportedOperationException(ERROR_NO_DESCRIPTION);
		}
	}

	// Low-Level Interpreted String Parsing Methods

	private static void sortDates(List<Date> dates) {
		Collections.sort(dates);
	}

	private static void removeRepeatedDates(List<Date> dates) {
		int j = 0;
		while (j < dates.size() - 1) {

			// This is necessary because PrettyTime may return varying
			// milliseconds.
			boolean isSameDate = TaskCatalystCommons.isSameDate(dates.get(j),
					dates.get(j + 1));
			boolean isSameTime = TaskCatalystCommons.isSameTime(dates.get(j),
					dates.get(j + 1));

			if (isSameDate && isSameTime) {
				dates.remove(j);
			} else {
				j++;
			}
		}
	}

	public static void truncateDateWithoutTime(List<Date> dates) {
		for (Date date : dates) {
			// This is necessary because PrettyTime may return varying
			// milliseconds.

			boolean isSameTime = TaskCatalystCommons.isSameTime(new Date(),
					date);

			if (isSameTime) {
				truncateTimeToOne(date);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static Date truncateTimeToOne(Date date) {
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(1);
		return date;
	}

	@SuppressWarnings("deprecation")
	public static Date truncateTimeToZero(Date date) {
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		return date;
	}

	// Generates a single Date String for a DateGroup.
	// Example Date String: {date}, {date} and {date}
	private static String getDateString(List<Date> dates, String finalConnector) {

		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd MMM yyyy KK:mm:ss a");

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

		String matchingExpression = matchingText.replaceAll("\\.$", "");
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

	private static String removeHashtaggedWords(String parsingInput) {
		return parsingInput.replaceAll("(\\s|^)(#\\w+)(\\s|$)", " ");
	}

	private static String removePrepositionBeforeDateString(
			String interpretedInput, String preposition) {

		String wordBoundary = "(^|\\b)";
		String openingCurlyBrace = "\\{";

		return interpretedInput.replaceAll(wordBoundary + preposition + " "
				+ openingCurlyBrace, openingCurlyBrace);
	}

	private static String removeNumberWords(String parsingInput) {
		String[] numberWords = { "one", "two", "three", "four", "five", "six",
				"seven", "eight", "nine", "ten", "eleven", "twelve",
				"thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
				"eighteen", "nineteen", "twenty" };
		String newParsingInput = parsingInput;
		for (String numberWord : numberWords) {
			newParsingInput = newParsingInput.replaceAll("(\\b)(" + numberWord
					+ ")( |$)", " ");
		}
		return newParsingInput;
	}

	private static String removeSensitiveParsingWords(String parsingInput) {
		String newParsingInput = parsingInput;
		newParsingInput = parsingInput.replaceAll(
				"(\\b)(at|in|from|on)(\\b|$)", " ");
		return newParsingInput;
	}

	private static String removeWordsInBrackets(String interpretedInput) {
		return interpretedInput.replaceAll("(\\[|\\{)(.*?)(\\]|\\})", "");
	}

	private static String removeWordsInCurlyBraces(String interpretedInput) {
		return interpretedInput.replaceAll("(\\{)(.*?)(\\})", "");
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

	private static String replaceYesterday(String interpretedInput) {
		return interpretedInput.replaceAll("\\[(yesterday|Yesterday)\\]",
				"yesterday");
	}

	private static String replaceSpacesWithWildcard(String matchingExpression) {
		return matchingExpression.replaceAll(" ",
				"( |,|, )?(at|from|and)?( on)?( )");
	}

	private static String replaceCommasWithAnd(String parsingInput) {
		return parsingInput.replaceAll(",", " and ");
	}

	private static boolean isLongMatch(DateGroup dateGroup) {
		return dateGroup.getText().length() > 2;
	}

	// This checks if the match is exact
	private static boolean isWholeMatch(String parsingInput, DateGroup dateGroup) {
		String trailingSymbols = "[^A-Z^a-z^0-9]$";
		String extendedText = extendMatch(parsingInput, dateGroup);
		String matchingText = dateGroup.getText();
		matchingText = matchingText.replaceAll(trailingSymbols, "");
		matchingText = matchingText.replaceAll(",", "");
		String startWordBoundary = ".*(^|\\b)";
		String endWordBoundary = "(\\b|$).*";

		boolean wholeWord = extendedText.matches(startWordBoundary
				+ matchingText + endWordBoundary);
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
	private static String ignoreBasedOnRegex(String input, String regex) {
		String onlyOutsideBrackets = "(?=[^\\]]*(\\[|$))";
		String ignoredString = input;
		Pattern pattern = Pattern.compile(regex + onlyOutsideBrackets);
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			String matching = matcher.group();
			String replacement = "[" + matching + "]";
			ignoredString = ignoredString.replace(matching, replacement);
		}
		return ignoredString;
	}

	// High-Level Pretty String Methods

	public static String getPrettyString(String userInput,
			boolean isAlwaysShowTime) {
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
					currentDate, nextDate, isAlwaysShowTime);

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
		editedUserInput = editedUserInput.replaceAll("before \\{(on|at) ",
				"before \\{");
		editedUserInput = editedUserInput.replaceAll("after \\{(on|at) ",
				"after \\{");
		return editedUserInput;
	}

	private static SimpleDateFormat generateFormatter(Date previousDate,
			Date currentDate, Date nextDate, boolean isAlwaysShowTime) {
		String formatString = generateFormatString(previousDate, currentDate,
				nextDate, isAlwaysShowTime);

		SimpleDateFormat formatter = new SimpleDateFormat(formatString);
		return formatter;
	}

	private static String generateFormatString(Date previousDate,
			Date currentDate, Date nextDate, boolean isAlwaysShowTime) {
		String formatString = "";
		if (!isSameDate(previousDate, currentDate)) {
			if (isYesterday(currentDate)) {
				formatString = "'yesterday'";
			} else if (isToday(currentDate)) {
				formatString = "'today'";
			} else if (isTomorrow(currentDate)) {
				formatString = "'tomorrow'";
			} else if (isThisWeek(currentDate)) {
				boolean isFirstDate = previousDate == null;
				if (isFirstDate) {
					formatString = "'on' ";
				}
				formatString += "E";
			} else {
				formatString = "'on' d MMM";
			}

			if (!isThisYear(currentDate)) {
				formatString = formatString + " yyyy";
			}
		}
		if (isAlwaysShowTime || !isSameTime(currentDate, nextDate)
				|| formatString.isEmpty()) {
			if (hasHours(currentDate) || hasMinutes(currentDate)
					|| getSeconds(currentDate) != 1) {
				if (!formatString.isEmpty()) {
					formatString = formatString + " ";
				}
				formatString = formatString + "h";
				if (hasMinutes(currentDate)) {
					formatString = formatString + ":mm";
				}
				formatString = formatString + "a";
			}
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

	// Friendly String Methods

	public static String getFriendlyString(String userCommand)
			throws UnsupportedOperationException {
		boolean isAlwaysShowTime = false;
		String interpretedString = TaskCatalystCommons
				.getInterpretedString(userCommand);
		String prettyString = TaskCatalystCommons.getPrettyString(
				interpretedString, isAlwaysShowTime);
		String friendlyString = TaskCatalystCommons
				.removeSquareBrackets(prettyString);
		friendlyString = TaskCatalystCommons.removeCurlyBraces(friendlyString);
		return friendlyString;
	}

	public static String getPrettyStringWithoutDate(String prettyString) {
		String prepositions = " (from|before|after|either|by) \\{";
		String emptyPrepositions = "\\}(\\s)?(,|to|and|or) \\{";
		String spaceBeforeCommas = " ,";
		String spaceBeforeFullstops = " \\.";
		String friendlyString = prettyString;

		friendlyString = friendlyString.replaceAll(prepositions, "\\{");
		friendlyString = friendlyString.replaceAll(emptyPrepositions, "\\}\\{");
		friendlyString = removeWordsInCurlyBraces(friendlyString);
		friendlyString = removeSquareBrackets(friendlyString);
		friendlyString = friendlyString.replaceAll(spaceBeforeCommas, ",");
		friendlyString = friendlyString.replaceAll(spaceBeforeFullstops, "\\.");
		friendlyString = friendlyString.trim();
		friendlyString = removeConsecutiveWhitespaces(friendlyString);
		return friendlyString;
	}

	public static List<String> extractHashtags(String interpretedString) {
		List<String> hashtagList = new ArrayList<String>();

		String[] descriptionTokenized = interpretedString.split(" ");

		for (String token : descriptionTokenized) {
			if (token.startsWith("#")) {
				String tokenProcessed = token.toLowerCase();
				tokenProcessed = TaskCatalystCommons
						.removeSquareBrackets(tokenProcessed);

				String repeatedStartingHashtags = "(\\s|^)#+";
				String endingPunctuations = "(,|\\.|\\?|!|:|;)+(\\s|$)";

				tokenProcessed = tokenProcessed.replaceAll(
						repeatedStartingHashtags, "#");

				tokenProcessed = tokenProcessed.replaceAll(endingPunctuations,
						"");

				if (tokenProcessed.length() > 1) {
					hashtagList.add(tokenProcessed);
				}
			}
		}

		return hashtagList;
	}

	// Date Time Libraries

	public static List<Date> getInferredDates(String keyword) {
		List<Date> dates = new ArrayList<Date>();
		List<DateGroup> dateGroups = prettyTimeParser.parseSyntax(keyword);

		for (DateGroup dateGroup : dateGroups) {
			dates.addAll(dateGroup.getDates());
		}

		return dates;
	}

	public static boolean isBetweenDates(Date start, Date end, Date check) {
		boolean isSameStartDate = TaskCatalystCommons.isSameDate(start, check);
		boolean isSameStartEnd = TaskCatalystCommons.isSameDate(end, check);
		boolean isAfterStartDate = check.after(start);
		boolean isBeforeEndDate = check.before(end);
		boolean isBetweenStartAndEnd = (isAfterStartDate && isBeforeEndDate);

		boolean isBetweenDates = (isSameStartDate || isSameStartEnd || isBetweenStartAndEnd);
		return isBetweenDates;
	}

	public static boolean isSameTime(Date date, Date date2) {
		if (date == null || date2 == null) {
			return false;
		}
		cal1.setTime(date);
		cal2.setTime(date2);
		int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
		int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
		int minute1 = cal1.get(Calendar.MINUTE);
		int minute2 = cal2.get(Calendar.MINUTE);
		boolean isSameHour = hour1 == hour2;
		boolean isSameMinute = minute1 == minute2;
		return isSameHour && isSameMinute;
	}

	public static boolean isSameDate(Date date, Date date2) {
		if (date == null || date2 == null) {
			return false;
		}
		cal1.setTime(date);
		cal2.setTime(date2);
		int dateOfMonth1 = cal1.get(Calendar.DATE);
		int dateOfMonth2 = cal2.get(Calendar.DATE);
		int month1 = cal1.get(Calendar.MONTH);
		int month2 = cal2.get(Calendar.MONTH);
		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		boolean isSameDateOfMonth = dateOfMonth1 == dateOfMonth2;
		boolean isSameMonth = month1 == month2;
		boolean isSameYear = year1 == year2;
		boolean isSameDate = isSameDateOfMonth && isSameMonth && isSameYear;
		return isSameDate;
	}

	public static boolean hasHours(Date date) {
		return getHours(date) != 0;
	}

	public static boolean hasMinutes(Date date) {
		return getMinutes(date) != 0;
	}

	public static boolean hasSeconds(Date date) {
		return getSeconds(date) != 0;
	}

	public static int getHours(Date date) {
		cal1.setTime(date);
		return cal1.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinutes(Date date) {
		cal1.setTime(date);
		return cal1.get(Calendar.MINUTE);
	}

	public static int getSeconds(Date date) {
		cal1.setTime(date);
		return cal1.get(Calendar.SECOND);
	}

	public static int getMilliseconds(Date date) {
		cal1.setTime(date);
		return cal1.get(Calendar.MILLISECOND);
	}

	public static boolean isYesterday(Date date) {
		return daysFromToday(date) == -1;
	}

	public static boolean isToday(Date date) {
		return daysFromToday(date) == 0;
	}

	public static boolean isTomorrow(Date date) {
		return daysFromToday(date) == 1;
	}

	public static boolean isThisWeek(Date date) {
		return daysFromToday(date) <= 7 && daysFromToday(date) > 0;
	}

	public static int daysFromToday(Date date) {
		cal1.setTime(date);
		cal2.setTimeInMillis(System.currentTimeMillis());
		int dayOfYear1 = cal1.get(Calendar.DAY_OF_YEAR);
		int dayOfYear2 = cal2.get(Calendar.DAY_OF_YEAR);
		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		int dayDifference = dayOfYear1 - dayOfYear2;
		int yearDifference = (year1 - year2) * 365;
		return dayDifference + yearDifference;
	}

	public static boolean isThisYear(Date date) {
		cal1.setTime(date);
		cal2.setTimeInMillis(System.currentTimeMillis());
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	public static List<Date> getAllDates(String interpretedString) {
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(interpretedString);
		List<Date> allDates = new ArrayList<Date>();
		while (matcher.find()) {
			String matching = matcher.group();
			List<DateGroup> dateGroups = prettyTimeParser.parseSyntax(matching);
			for (DateGroup dateGroup : dateGroups) {
				List<Date> dates = dateGroup.getDates();
				allDates.addAll(dates);
			}
		}
		sortDates(allDates);
		return allDates;
	}
}
