package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

public class TaskBuilderAdvanced implements TaskBuilder {

	public Task createTask(String userInput) {

		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}

		List<Date> allDates = new ArrayList<Date>();
		String editedUserInput = userInput;
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(userInput);
		while (matcher.find()) {
			String word = matcher.group(1);
			List<DateGroup> currentGroupDates = new PrettyTimeParser()
					.parseSyntax(word);
			for (DateGroup group : currentGroupDates) {
				List<Date> dates = group.getDates();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"dd MMM yyyy KK:mm a");
				if (dates.size() == 1) {
					editedUserInput = editedUserInput.replaceFirst(
							word,
							word.substring(0, group.getPosition())
									+ formatter.format(dates.get(0)));
				} else if (dates.size() == 2) {
					editedUserInput = editedUserInput.replaceFirst(
							word,
							word.substring(0, group.getPosition())
									+ formatter.format(dates.get(0)) + " to "
									+ formatter.format(dates.get(1)));
				}
				allDates.addAll(dates);
			}
		}

		List<LocalDateTime> ldts = toLdtList(allDates);
		Task task;
		if (ldts.isEmpty()) {
			task = new TaskAdvanced(editedUserInput);
		} else if (ldts.size() == 1) {
			task = new TaskAdvanced(editedUserInput, ldts.get(0));
		} else {
			task = new TaskAdvanced(editedUserInput, ldts.get(0), ldts.get(ldts
					.size() - 1));
		}
		return task;
	}

	private static List<LocalDateTime> toLdtList(List<Date> dates) {
		List<LocalDateTime> ldts = new ArrayList<LocalDateTime>();
		for (Date date : dates) {
			ldts.add(LocalDateTime.ofInstant(date.toInstant(),
					ZoneId.systemDefault()));
		}
		return ldts;
	}

}
