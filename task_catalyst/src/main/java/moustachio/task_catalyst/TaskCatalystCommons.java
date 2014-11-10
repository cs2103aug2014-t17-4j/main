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

//@author A0111890L
/**
 * This class contains parsing and interpretation methods shared across multiple
 * classes in the system. It provides methods for parsing commands as well as
 * NLP conversion of user input into various formats (Interpreted String,
 * Relative String, Display String, etc.).
 */
public class TaskCatalystCommons {

	private static final String ERROR_PRETTY_TIME_CRASH = "Please check your date formats.";
	private static final String ERROR_OVERLAPPING_INTERNALLY = "Please resolve overlapping dates in the task.";
	private static final String ERROR_NO_DESCRIPTION = "Please type in some descriptions for the task.";
	private static final String ERROR_MULTIPLE_CHUNKS = "Please keep all date information together.";
	private static final String ERROR_DEFAULT_HASHTAGS = "Please remove default hashtags from your task description.";
	private static final String ERROR_MIX_TYPES = "Please only specify one pair of date ranges per task and do not mix date types.";
	private static final int INVALID_INTEGER = -1;

	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"dd MMM yyyy KK:mm:ss a");
	private static PrettyTimeParser prettyTimeParser = new PrettyTimeParser();
	private static Calendar cal1 = Calendar.getInstance();
	private static Calendar cal2 = Calendar.getInstance();
	private static BlackBox blackBox = BlackBox.getInstance();

	// Command Parsing Methods

	/**
	 * Parses and interprets the user's input and outputs the command type.
	 * 
	 * @param userCommand
	 *            The entire string entered by the user.
	 * @return A CommandType enumeration type.
	 */
	public static CommandType getCommandType(String userCommand) {
		blackBox.info("User Typed: " + userCommand);

		if (userCommand == null
				|| removeSurroundingSpaces(userCommand).isEmpty()) {
			blackBox.info("Command is invalid.");
			return CommandType.INVALID;
		}

		String command = getFirstWord(userCommand);
		String parameters = removeFirstWord(userCommand);
		String commandLowerCase = command.toLowerCase();
		String parametersTrimmed = removeSurroundingSpaces(parameters);

		boolean noParameters = parametersTrimmed.isEmpty();

		CommandType commandType;

		if (Hashtag.isThisAction(commandLowerCase) && noParameters) {
			commandType = CommandType.HASHTAG;
		} else if (Delete.isThisAction(commandLowerCase)) {
			commandType = CommandType.DELETE;
		} else if (Done.isThisAction(commandLowerCase)) {
			commandType = CommandType.DONE;
		} else if (Edit.isThisAction(commandLowerCase)) {
			commandType = CommandType.EDIT;
		} else if (Redo.isThisAction(commandLowerCase) && noParameters) {
			commandType = CommandType.REDO;
		} else if (Search.isThisAction(commandLowerCase)) {
			commandType = CommandType.SEARCH;
		} else if (Undo.isThisAction(commandLowerCase) && noParameters) {
			commandType = CommandType.UNDO;
		} else if (Undone.isThisAction(commandLowerCase)) {
			commandType = CommandType.UNDONE;
		} else {
			commandType = CommandType.ADD;
		}

		blackBox.info("Command interpreted: " + commandType);

		return commandType;
	}

	public static String getFirstWord(String userCommand) {
		assert userCommand != null;

		String oneOrMoreSpaces = "\\s+";

		String[] splitUserCommand = userCommand.split(oneOrMoreSpaces);
		String firstWord = splitUserCommand[0];

		return firstWord;
	}

	public static String removeFirstWord(String userCommand) {
		assert userCommand != null;

		String blank = "";

		String firstWord = getFirstWord(userCommand);
		firstWord = Pattern.quote(firstWord);

		String removedFirstWord = userCommand.replaceFirst(firstWord, blank);
		removedFirstWord = removeSurroundingSpaces(removedFirstWord);

		return removedFirstWord;
	}

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
		assert intListString != null;

		String intListStringProcessed = intListString.replaceAll(",", " ");
		String[] splitIntStrings = intListStringProcessed.split("\\s+");

		List<Integer> parsedIntegers = new ArrayList<Integer>();

		for (String intString : splitIntStrings) {
			int parsedPositiveInt = parsePositiveInt(intString);

			boolean isPositive = (parsedPositiveInt > 0);
			boolean isDuplicate = parsedIntegers.contains(parsedPositiveInt);

			if (isPositive && !isDuplicate) {
				parsedIntegers.add(parsedPositiveInt);
			}
		}

		return parsedIntegers;
	}

	// High-Level Interpreted String Parsing Methods

	/**
	 * Parses the user's input into an Interpreted String which are specially
	 * formatted strings with all dates replaced with absolute values enclosed
	 * in curly braces, and all ignored text in square brackets. Interpreted
	 * Strings are stored directly in the tasks, and then recomputed later into
	 * Relative Strings, Display Strings, and Display Strings without Date.
	 * 
	 * @param userInput
	 *            The user's input as typed.
	 * @param strict
	 *            Whether the parsing should raise an exception when invalid
	 *            scenarios are encountered.
	 * @return Interpreted String
	 * @throws UnsupportedOperationException
	 *             When strict mode is enabled and an invalid situation occurs.
	 */
	public static String getInterpretedString(String userInput, boolean strict)
			throws UnsupportedOperationException {
		assert userInput != null;

		blackBox.fine("User Input: " + userInput);

		String interpretedString = userInput;
		interpretedString = getInterpretedStringSingleIteration(interpretedString);
		interpretedString = removeCurlyBraces(interpretedString);
		interpretedString = getInterpretedStringSingleIteration(interpretedString);

		if (strict) {
			exceptionIfInvalidRange(interpretedString);
			exceptionIfOverlappingDates(interpretedString);
			exceptionIfContainsDefaultHashtag(interpretedString);
			exceptionIfMultipleChunks(interpretedString);
			exceptionIfDescriptionEmpty(interpretedString);
		}

		blackBox.fine("InterpretedString: " + interpretedString);
		return interpretedString;
	}

	private static String getInterpretedStringSingleIteration(
			String interpretedString) {
		assert interpretedString != null;

		String interpretedStringNextPass = getInterpretedStringSinglePass(interpretedString);

		boolean isNoChanges;

		do {
			interpretedString = interpretedStringNextPass;
			interpretedStringNextPass = getInterpretedStringSinglePass(interpretedStringNextPass);
			isNoChanges = interpretedStringNextPass.equals(interpretedString);
		} while (!isNoChanges);

		return interpretedString;
	}

	private static String getInterpretedStringSinglePass(String userInput)
			throws UnsupportedOperationException {
		assert userInput != null;

		String interpretedInput = getInterpretedInput(userInput);
		String parsingInput = getParsingInput(interpretedInput);
		List<DateGroup> dateGroups = parseParsingInput(parsingInput);
		interpretedInput = replaceDateStrings(interpretedInput, parsingInput,
				dateGroups);

		return interpretedInput;
	}

	private static String getInterpretedInput(String userInput) {
		assert userInput != null;

		String interpretedInput = userInput;
		interpretedInput = removeRepeatedCommas(interpretedInput);
		interpretedInput = addCommaToBraces(interpretedInput);
		interpretedInput = addSpaceAfterCommas(interpretedInput);
		interpretedInput = replaceTomorrowShort(interpretedInput);
		interpretedInput = replaceTodayShort(interpretedInput);
		interpretedInput = ignoreWordsContainingEst(interpretedInput);
		interpretedInput = ignoreWordsContainingAted(interpretedInput);
		interpretedInput = ignoreWordsContainingFivePlusDigits(interpretedInput);
		interpretedInput = ignoreWordsEndingWithNumbers(interpretedInput);
		interpretedInput = removeConsecutiveWhitespaces(interpretedInput);

		return interpretedInput;
	}

	private static String getParsingInput(String interpretedInput) {
		assert interpretedInput != null;

		String parsingInput = interpretedInput;
		parsingInput = removeWordsInBrackets(interpretedInput);
		parsingInput = addCommaAfterTimeAmPm(parsingInput);
		parsingInput = addCommaAfterTimeColon(parsingInput);
		parsingInput = removeHashtaggedWords(parsingInput);
		parsingInput = removeSensitiveParsingWords(parsingInput);
		parsingInput = removeNumberWords(parsingInput);
		parsingInput = replaceCommasWithAnd(parsingInput);
		parsingInput = removeConsecutiveAnds(parsingInput);
		parsingInput = removeConsecutiveWhitespaces(parsingInput);

		return parsingInput;
	}

	// Medium-Level Interpreted String Parsing Methods

	private static List<DateGroup> parseParsingInput(String parsingInput) {
		assert parsingInput != null;

		List<DateGroup> dateGroups;
		try {
			dateGroups = prettyTimeParser.parseSyntax(parsingInput);
		} catch (Exception e) {
			throw new UnsupportedOperationException(ERROR_PRETTY_TIME_CRASH);
		}

		return dateGroups;
	}

	private static String replaceDateStrings(String interpretedInput,
			String parsingInput, List<DateGroup> dateGroups)
			throws UnsupportedOperationException {
		assert interpretedInput != null;
		assert parsingInput != null;
		assert dateGroups != null;

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
		assert matchingText != null;

		String connector;

		matchingText = matchingText.toLowerCase();
		boolean isContainsTo = matchingText.contains(" to ");
		boolean isContainsOr = matchingText.contains(" or ");
		boolean isContainsDash = matchingText.contains(" - ");
		boolean isDateRange = (isContainsTo || isContainsDash);

		if (isDateRange) {
			connector = " to ";
		} else if (isContainsOr) {
			connector = " or ";
		} else {
			connector = " and ";
		}

		return connector;
	}

	private static void exceptionIfInvalidRange(String interpretedString)
			throws UnsupportedOperationException {
		assert interpretedString != null;

		// isRange is true when there exists a "to" between two dates,
		// with the "to" at most 2 words away from the second date.
		String rangeCondition = ".*\\}.*(\\bto\\b\\s)(\\b\\w*\\b\\s){0,2}\\{.*";

		List<Date> dates = getAllDates(interpretedString);

		boolean isRange = interpretedString.matches(rangeCondition);
		boolean isMoreThanTwoDates = (dates.size() > 2);
		boolean isMixedTypes = isRange && isMoreThanTwoDates;

		if (isMixedTypes) {
			blackBox.fine("Invalid Range: " + interpretedString);
			throw new UnsupportedOperationException(ERROR_MIX_TYPES);
		}
	}

	private static void exceptionIfOverlappingDates(String interpretedString)
			throws UnsupportedOperationException {
		assert interpretedString != null;

		List<Date> dates = getAllDates(interpretedString);

		int j = 0;
		int secondLastElement = dates.size() - 1;

		while (j < secondLastElement) {
			// This is necessary because PrettyTime may return varying
			// milliseconds.
			boolean isSameDate = TaskCatalystCommons.isSameDate(dates.get(j),
					dates.get(j + 1));
			boolean isSameTime = TaskCatalystCommons.isSameTime(dates.get(j),
					dates.get(j + 1));
			boolean isOverlapping = isSameDate && isSameTime;

			if (isOverlapping) {
				blackBox.fine("Overlapping Internally: " + interpretedString);
				throw new UnsupportedOperationException(
						ERROR_OVERLAPPING_INTERNALLY);
			} else {
				j++;
			}
		}
	}

	private static void exceptionIfContainsDefaultHashtag(
			String interpretedString) throws UnsupportedOperationException {
		assert interpretedString != null;

		TaskManager taskManager = TaskManagerActual.getInstance();

		String[] defaultHashtags = taskManager.getDefaultHashtags();
		String lowerCaseString = interpretedString.toLowerCase();

		boolean isContainsDefaultHashtag = false;

		for (String hashtag : defaultHashtags) {
			String startBoundary = ".*(\\s|^)";
			String endBoundary = "(\\s|$).*";
			String currentHashtag = startBoundary + hashtag + endBoundary;

			boolean isHashtagFound = lowerCaseString.matches(currentHashtag);
			boolean isPriority = hashtag.equals("#pri");

			if (isHashtagFound && !isPriority) {
				isContainsDefaultHashtag = true;
				break;
			}
		}

		if (isContainsDefaultHashtag) {
			blackBox.fine("Default Hashtags Used: " + interpretedString);
			throw new UnsupportedOperationException(ERROR_DEFAULT_HASHTAGS);
		}
	}

	private static void exceptionIfMultipleChunks(String interpretedString)
			throws UnsupportedOperationException {
		assert interpretedString != null;

		String prepositions = "(?i)(,|and|or|to) \\{";
		String spacesAfterBraces = "\\}(,)?(\\s)?";
		String consecutiveCurly = "\\}\\{";
		String textBetweenCurly = ".*\\}(.*?)\\{.*";

		String processingString = interpretedString;
		processingString = processingString.replaceAll(prepositions, "\\{");
		processingString = processingString
				.replaceAll(spacesAfterBraces, "\\}");
		processingString = processingString.replaceAll(consecutiveCurly, "");

		boolean isMultipleChunk = processingString.matches(textBetweenCurly);

		if (isMultipleChunk) {
			blackBox.fine("Multiple Chunks Encountered: " + interpretedString);
			throw new UnsupportedOperationException(ERROR_MULTIPLE_CHUNKS);
		}
	}

	private static void exceptionIfDescriptionEmpty(String interpretedString)
			throws UnsupportedOperationException {
		assert interpretedString != null;

		boolean isAlwaysShowTime = false;

		String relativeString = getRelativeString(interpretedString,
				isAlwaysShowTime);
		String withoutDates = getDisplayStringWithoutDate(relativeString);
		withoutDates = removeSurroundingSpaces(withoutDates);

		boolean isOnlyContainsDate = withoutDates.isEmpty();

		if (isOnlyContainsDate) {
			blackBox.fine("Contains Only Dates: " + interpretedString);
			throw new UnsupportedOperationException(ERROR_NO_DESCRIPTION);
		}
	}

	private static void truncateDateWithoutTime(List<Date> dates) {
		assert dates != null;

		for (Date date : dates) {
			Date now = new Date();

			boolean isSameTime = TaskCatalystCommons.isSameTime(now, date);

			if (isSameTime) {
				truncateTime(date);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static Date truncateTime(Date date) {
		assert date != null;

		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(1);

		return date;
	}

	// Low-Level Interpreted String Parsing Methods

	private static void sortDates(List<Date> dates) {
		assert dates != null;

		Collections.sort(dates);
	}

	// Generates a single Date String for a DateGroup.
	// Example Date String: {date}, {date} and {date}
	private static String getDateString(List<Date> dates, String finalConnector) {
		assert dates != null;
		assert finalConnector != null;

		int dateCount = dates.size();

		String intermediateConnector = ", ";
		String dateString = "";

		for (int i = 0; i < dateCount; i++) {
			Date date = dates.get(i);

			dateString += "{" + formatter.format(date) + "}";

			boolean isSecondLastDate = (i == dateCount - 2);
			boolean isLastDate = (i == dateCount - 1);

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
		assert interpretedInput != null;
		assert matchingText != null;
		assert dateString != null;

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

	private static boolean isLongMatch(DateGroup dateGroup) {
		assert dateGroup != null;

		return (dateGroup.getText().length() > 2);
	}

	// This checks if the match is exact
	private static boolean isWholeMatch(String parsingInput, DateGroup dateGroup) {
		assert parsingInput != null;
		assert dateGroup != null;

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
		assert parsingInput != null;
		assert dateGroup != null;

		int position = dateGroup.getPosition();
		int length = dateGroup.getText().length();
		int startIndex = Math.max(0, position - 1);
		int endIndex = Math.min(position + length + 1, parsingInput.length());

		String extendedText = parsingInput.substring(startIndex, endIndex);

		return extendedText;
	}

	// High-Level Pretty String Parsing Methods

	/**
	 * Replaces all date blocks (enclosed in curly braces) of an Interpreted
	 * String with strings representing relative date/time, such as "today",
	 * "tomorrow", "Monday", and so on.
	 * 
	 * @param interpretedString
	 *            A String formatted as an Interpreted String.
	 * @param isAlwaysShowTime
	 *            Always print the time, even if adjacent dates are the same
	 *            time.
	 * @return Relative String
	 */
	public static String getRelativeString(String interpretedString,
			boolean isAlwaysShowTime) {
		assert interpretedString != null;

		String relativeString = interpretedString;

		List<DateGroup> dateGroups = getRelativeStringDateGroups(interpretedString);

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

			relativeString = relativeString.replace(
					dateGroups.get(i).getText(), formatter.format(currentDate));

			previousDate = currentDate;
		}

		relativeString = replaceRelativeStringPrepositions(relativeString);

		return relativeString;
	}

	/**
	 * Automatically parses a user's input into an Interpreted String, then a
	 * Relative String, and finally into a Display String. A Display String is a
	 * Relative String with no square or curly brackets printed. This is useful
	 * for Live Task Preview.
	 * 
	 * @param userInput
	 *            The entire user's input exactly as he types it.
	 * @return Display String
	 * @throws UnsupportedOperationException
	 *             This is thrown when the user enters an invalid combination of
	 *             date and time.
	 */
	public static String getDisplayString(String userInput)
			throws UnsupportedOperationException {
		assert userInput != null;

		boolean isAlwaysShowTime = false;
		boolean strict = true;

		String interpretedString = getInterpretedString(userInput, strict);
		String relativeString = getRelativeString(interpretedString,
				isAlwaysShowTime);
		String displayString = removeSquareBrackets(relativeString);
		displayString = removeCurlyBraces(displayString);

		return displayString;
	}

	public static String getDisplayStringWithoutDate(String relativeString) {
		assert relativeString != null;

		String friendlyStringWithoutDate = relativeString;

		friendlyStringWithoutDate = removeAllPrepositions(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeEmptyPrepositions(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeWordsInCurlyBraces(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeSquareBrackets(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeSpacesBeforeCommas(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeSpacesBeforeFullstops(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeSurroundingSpaces(friendlyStringWithoutDate);
		friendlyStringWithoutDate = removeConsecutiveWhitespaces(friendlyStringWithoutDate);

		return friendlyStringWithoutDate;
	}

	// Low-Level Pretty String Parsing Methods

	private static String replaceRelativeStringPrepositions(String userInput) {
		assert userInput != null;

		String editedUserInput = userInput;

		editedUserInput = removeRepeatedPrepositions(editedUserInput, "by",
				"(on|at)");
		editedUserInput = removeRepeatedPrepositions(editedUserInput, "to",
				"(on|at)");
		editedUserInput = removeRepeatedPrepositions(editedUserInput, "from",
				"(on|at)");
		editedUserInput = removeRepeatedPrepositions(editedUserInput, "before",
				"(on|at)");
		editedUserInput = removeRepeatedPrepositions(editedUserInput, "after",
				"(on|at)");
		editedUserInput = removeRepeatedPrepositions(editedUserInput,
				"between", "(on|at)");

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

		boolean isShowDate = !isSameDate(previousDate, currentDate);
		boolean isFirstDate = (previousDate == null);

		if (isShowDate) {
			if (isYesterday(currentDate)) {
				formatString = "'yesterday'";
			} else if (isToday(currentDate)) {
				formatString = "'today'";
			} else if (isTomorrow(currentDate)) {
				formatString = "'tomorrow'";
			} else if (isThisWeek(currentDate) && isFirstDate) {
				formatString = "'on' E";
			} else if (isThisWeek(currentDate)) {
				formatString = "E";
			} else if (isFirstDate) {
				formatString = "'on' d MMM";
			} else {
				formatString = "d MMM";
			}
			if (!isThisYear(currentDate)) {
				formatString = formatString + " yyyy";
			}
		}

		boolean isSameTimeAsNext = !isSameTime(currentDate, nextDate);
		boolean isDateEmpty = formatString.isEmpty();
		boolean isAllDay = !hasHours(currentDate) && !hasMinutes(currentDate)
				&& getSeconds(currentDate) == 1;
		boolean isShowTime = (isAlwaysShowTime || isSameTimeAsNext || isDateEmpty)
				&& !isAllDay;

		if (isShowTime) {
			if (!isDateEmpty) {
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

	private static List<DateGroup> getRelativeStringDateGroups(String userInput) {
		assert userInput != null;

		String textInCurlyBraces = "\\{(.*?)\\}";

		Pattern pattern = Pattern.compile(textInCurlyBraces);
		Matcher matcher = pattern.matcher(userInput);

		List<DateGroup> dateGroups = new ArrayList<DateGroup>();

		while (matcher.find()) {
			String matching = matcher.group();

			List<DateGroup> parsedDateGroups = prettyTimeParser
					.parseSyntax(matching);

			dateGroups.addAll(parsedDateGroups);
		}

		return dateGroups;
	}

	// Interpreted String Operation Methods

	/**
	 * Parses an Interpreted String and returns all dates in an array.
	 * 
	 * @param interpretedString
	 *            A String formatted as an Interpreted String.
	 * @return A List of Dates
	 */
	public static List<Date> getAllDates(String interpretedString) {
		assert interpretedString != null;

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

	/**
	 * Parses an Interpreted String and returns all hashtags.
	 * 
	 * @param interpretedString
	 *            A String formatted as an Interpreted String.
	 * @return A List of String Hashtags
	 */
	public static List<String> getAllHashtags(String interpretedString) {
		assert interpretedString != null;

		List<String> hashtagList = new ArrayList<String>();

		String[] descriptionTokenized = interpretedString.split(" ");

		for (String token : descriptionTokenized) {
			if (token.startsWith("#")) {
				String tokenProcessed = token.toLowerCase();
				tokenProcessed = removeSquareBrackets(tokenProcessed);
				tokenProcessed = removeRepeatedHashtags(tokenProcessed);
				tokenProcessed = removeEndingPunctuations(tokenProcessed);

				boolean isValidHashtag = (tokenProcessed.length() > 1);

				if (isValidHashtag) {
					hashtagList.add(tokenProcessed);
				}
			}
		}

		return hashtagList;
	}

	/**
	 * Checks for words between dates to determine user intention (to, or, and,
	 * etc.)
	 * 
	 * @param interpretedString
	 *            A String formatted as an Interpreted String.
	 * @param word
	 *            The word to be checked.
	 * @return Returns true if it is found, false otherwise.
	 */
	public static boolean hasWordBetweenDates(String interpretedString,
			String word) {
		assert interpretedString != null;
		assert word != null;

		String openingBrace = ".*\\}.*(\\b";
		String closingBrace = "\\b\\s)(\\b\\w+\\b\\s){0,2}\\{.*";
		String matchingCriteria = openingBrace + word + closingBrace;

		boolean hasWordBetweenDates = interpretedString
				.matches(matchingCriteria);

		return hasWordBetweenDates;
	}

	/**
	 * Checks for words before dates to determine user intention (from, between,
	 * etc.)
	 * 
	 * @param interpretedString
	 *            A String formatted as an Interpreted String.
	 * @param word
	 *            The word to be checked.
	 * @return Returns true if it is found, false otherwise.
	 */
	public static boolean hasWordBeforeDates(String interpretedString,
			String word) {
		assert interpretedString != null;
		assert word != null;

		String openingBrace = ".*(\\b(";
		String closingBrace = ")\\b\\s)(\\b\\w+\\b\\s){0,2}\\{.*";
		String matchingCriteria = openingBrace + word + closingBrace;

		boolean hasWordBeforeDates = interpretedString
				.matches(matchingCriteria);

		return hasWordBeforeDates;
	}

	// Date Time Libraries

	public static boolean isBetweenDates(Date start, Date end, Date check) {
		assert start != null;
		assert end != null;
		assert check != null;

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
		assert date != null;

		return getHours(date) != 0;
	}

	public static boolean hasMinutes(Date date) {
		assert date != null;

		return getMinutes(date) != 0;
	}

	public static boolean hasSeconds(Date date) {
		assert date != null;

		return getSeconds(date) != 0;
	}

	public static int getHours(Date date) {
		assert date != null;

		cal1.setTime(date);

		return cal1.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinutes(Date date) {
		assert date != null;

		cal1.setTime(date);

		return cal1.get(Calendar.MINUTE);
	}

	public static int getSeconds(Date date) {
		assert date != null;

		cal1.setTime(date);

		return cal1.get(Calendar.SECOND);
	}

	public static int getMilliseconds(Date date) {
		assert date != null;

		cal1.setTime(date);

		return cal1.get(Calendar.MILLISECOND);
	}

	public static boolean isYesterday(Date date) {
		assert date != null;

		return daysFromToday(date) == -1;
	}

	public static boolean isToday(Date date) {
		assert date != null;

		return daysFromToday(date) == 0;
	}

	public static boolean isTomorrow(Date date) {
		assert date != null;

		return daysFromToday(date) == 1;
	}

	public static boolean isThisWeek(Date date) {
		assert date != null;

		return daysFromToday(date) <= 7 && daysFromToday(date) > 0;
	}

	public static boolean isThisYear(Date date) {
		assert date != null;

		cal1.setTime(date);
		cal2.setTimeInMillis(System.currentTimeMillis());

		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	public static int daysFromToday(Date date) {
		assert date != null;

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

	// Very Low Level Methods

	private static String addSpaceAfterCommas(String interpretedInput) {
		assert interpretedInput != null;

		String commas = ",";
		String commasWithSpace = ", ";

		interpretedInput = interpretedInput.replaceAll(commas, commasWithSpace);

		return interpretedInput;
	}

	private static String addCommaToBraces(String interpretedInput) {
		assert interpretedInput != null;

		String curlyBraces = "\\} \\{";
		String curlyBracesWithComma = "\\}, \\{";

		interpretedInput = interpretedInput.replaceAll(curlyBraces,
				curlyBracesWithComma);

		return interpretedInput;
	}

	private static String addCommaAfterTimeColon(String parsingInput) {
		assert parsingInput != null;

		String timeWithAmPm = "(?<!:)(\\d{2}:\\d{2}(?i)(am|pm)?)(?!:)(?![a-zA-Z])";

		return parsingInput.replaceAll(timeWithAmPm, " $1,");
	}

	private static String addCommaAfterTimeAmPm(String parsingInput) {
		assert parsingInput != null;

		String timeWithColon = "(\\d{1,2}(?i)(am|pm))(\\s|$)(?![a-zA-Z])";

		return parsingInput.replaceAll(timeWithColon, "$1,");
	}

	private static String replaceCommasWithAnd(String parsingInput) {
		assert parsingInput != null;

		return parsingInput.replaceAll(",", " and ");
	}

	private static String replaceTodayShort(String interpretedInput) {
		assert interpretedInput != null;

		String todayShort = "(\\s|^)(?i)(tdy)(\\s|$)";
		String today = " today ";

		interpretedInput = interpretedInput.replaceAll(todayShort, today);

		return interpretedInput;
	}

	private static String replaceTomorrowShort(String interpretedInput) {
		assert interpretedInput != null;

		String tomorrowShort = "(\\s|^)(?i)(tmr|tml)(\\s|$)";
		String tomorrow = " tomorrow ";

		interpretedInput = interpretedInput.replaceAll(tomorrowShort, tomorrow);

		return interpretedInput;
	}

	private static String replaceSpacesWithWildcard(String matchingExpression) {
		assert matchingExpression != null;

		String spaces = "\\s+";
		String wildCard = "( |,|, )?(?i)(at|from|and)?( on)?( )";

		return matchingExpression.replaceAll(spaces, wildCard);
	}

	private static String removeAllAnds(String matchingExpression) {
		assert matchingExpression != null;

		return matchingExpression.replaceAll(" and ", " ");
	}

	private static String removeAllPrepositions(String displayString) {
		assert displayString != null;

		String prepositions = " (?i)(from|before|after|either|by|between) \\{";
		displayString = displayString.replaceAll(prepositions, "\\{");

		return displayString;
	}

	private static String removeConsecutiveAnds(String parsingInput) {
		assert parsingInput != null;

		return parsingInput.replaceAll("(\\b)(?i)(and)+", " and ");
	}

	private static String removeConsecutiveWhitespaces(String interpretedInput) {
		assert interpretedInput != null;

		return interpretedInput.replaceAll("\\s+", " ");
	}

	public static String removeCurlyBraces(String userInput) {
		assert userInput != null;

		return userInput.replaceAll("\\{|\\}", "");
	}

	private static String removeEmptyPrepositions(String displayString) {
		assert displayString != null;

		String emptyPrepositions = "\\}(\\s)?(?i)(,|to|and|or) \\{";
		displayString = displayString.replaceAll(emptyPrepositions, "\\}\\{");

		return displayString;
	}

	private static String removeEndingPunctuations(String tokenProcessed) {
		assert tokenProcessed != null;

		String endingPunctuations = "(,|\\.|\\?|!|:|;)+(\\s|$)";
		tokenProcessed = tokenProcessed.replaceAll(endingPunctuations, "");

		return tokenProcessed;
	}

	private static String removeHashtaggedWords(String parsingInput) {
		assert parsingInput != null;

		String hashtaggedWords = "(\\s|^)(#\\w+)(\\s|$)";

		return parsingInput.replaceAll(hashtaggedWords, " ");
	}

	private static String removeNumberWords(String parsingInput) {
		assert parsingInput != null;

		String[] numberWords = { "one", "two", "three", "four", "five", "six",
				"seven", "eight", "nine", "ten", "eleven", "twelve",
				"thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
				"eighteen", "nineteen", "twenty" };

		String newParsingInput = parsingInput;

		for (String numberWord : numberWords) {
			newParsingInput = newParsingInput.replaceAll("(\\b)(?i)("
					+ numberWord + ")( |$)", " ");
		}

		return newParsingInput;
	}

	private static String removePrepositionBeforeDateStrings(
			String interpretedInput) {
		assert interpretedInput != null;

		interpretedInput = removePrepositionBeforeDateString(interpretedInput,
				"on");
		interpretedInput = removePrepositionBeforeDateString(interpretedInput,
				"at");

		return interpretedInput;
	}

	private static String removePrepositionBeforeDateString(
			String interpretedInput, String preposition) {
		assert interpretedInput != null;
		assert preposition != null;

		String wordBoundary = "(^|\\b)";
		String openingCurlyBrace = "\\{";
		String caseInsensitive = "(?i)";

		return interpretedInput.replaceAll(wordBoundary + caseInsensitive
				+ preposition + " " + openingCurlyBrace, openingCurlyBrace);
	}

	private static String removeRepeatedCommas(String interpretedInput) {
		assert interpretedInput != null;

		String commas = ",";
		String repeatedSpacesCommas = "\\s+,+";

		interpretedInput = interpretedInput.replaceAll(repeatedSpacesCommas,
				commas);

		return interpretedInput;
	}

	private static void removeRepeatedDates(List<Date> dates) {
		assert dates != null;

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

	private static String removeRepeatedHashtags(String tokenProcessed) {
		assert tokenProcessed != null;

		String repeatedStartingHashtags = "(\\s|^)#+";
		tokenProcessed = tokenProcessed.replaceAll(repeatedStartingHashtags,
				"#");

		return tokenProcessed;
	}

	private static String removeRepeatedPrepositions(String userInput,
			String kept, String removed) {
		assert userInput != null;
		assert kept != null;
		assert removed != null;

		String caseInsensitive = "(?i)";
		String rightPattern = kept + " \\{";
		String wrongPattern = rightPattern + caseInsensitive + removed + " ";

		return userInput.replaceAll(wrongPattern, rightPattern);
	}

	private static String removeSpacesBeforeCommas(String displayString) {
		assert displayString != null;

		String spaceBeforeCommas = "\\s+,+";
		displayString = displayString.replaceAll(spaceBeforeCommas, ",");

		return displayString;
	}

	private static String removeSpacesBeforeFullstops(String displayString) {
		assert displayString != null;

		String spaceBeforeFullstops = "\\s+\\.";
		displayString = displayString.replaceAll(spaceBeforeFullstops, "\\.");

		return displayString;
	}

	private static String removeSensitiveParsingWords(String parsingInput) {
		assert parsingInput != null;

		String sensitiveWords = "(\\b)(?i)(at|in|from|on)(\\b|$)";

		return parsingInput.replaceAll(sensitiveWords, " ");
	}

	private static String removeSurroundingSpaces(String friendlyString) {
		assert friendlyString != null;

		return friendlyString.trim();
	}

	public static String removeSquareBrackets(String userInput) {
		assert userInput != null;

		return userInput.replaceAll("\\[|\\]", "");
	}

	private static String removeWordsInBrackets(String interpretedInput) {
		assert interpretedInput != null;

		return interpretedInput.replaceAll("(\\[|\\{)(.*?)(\\]|\\})", "");
	}

	private static String removeWordsInCurlyBraces(String interpretedInput) {
		assert interpretedInput != null;

		return interpretedInput.replaceAll("(\\{)(.*?)(\\})", "");
	}

	private static String ignoreBasedOnRegex(String input, String regex) {
		assert input != null;
		assert regex != null;

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

	private static String ignoreWordsContainingAted(String interpretedInput) {
		assert interpretedInput != null;

		String notHashtagged = "(?<!#)";
		String wordsContainingAted = notHashtagged + "\\w*ated\\w*";

		interpretedInput = ignoreBasedOnRegex(interpretedInput,
				wordsContainingAted);

		return interpretedInput;
	}

	private static String ignoreWordsContainingEst(String interpretedInput) {
		assert interpretedInput != null;

		String notHashtagged = "(?<!#)";
		String wordsContainingEst = notHashtagged
				+ "(?i)\\w*(?<!y)est(?!erday)\\w*";

		interpretedInput = ignoreBasedOnRegex(interpretedInput,
				wordsContainingEst);

		return interpretedInput;
	}

	private static String ignoreWordsContainingFivePlusDigits(
			String interpretedInput) {
		assert interpretedInput != null;

		String notHashtagged = "(?<!#)";
		String fivePlusDigits = notHashtagged + "\\d{5,}";

		interpretedInput = ignoreBasedOnRegex(interpretedInput, fivePlusDigits);

		return interpretedInput;
	}

	private static String ignoreWordsEndingWithNumbers(String interpretedInput) {
		assert interpretedInput != null;

		String notHashtagged = "(?<!#)";
		String endWithNumber = notHashtagged + "\\b[a-zA-Z-_$]+\\d+\\b";

		interpretedInput = ignoreBasedOnRegex(interpretedInput, endWithNumber);

		return interpretedInput;
	}
}
