package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class TaskBuilderBasic implements TaskBuilder {

	public Task createTask(String userInput) {

		if (userInput == null || userInput.trim().isEmpty()) {
			return null;
		}
		
		List<Date> allDates = new ArrayList<Date>();

		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(userInput);
		while (matcher.find()) {
			String word = matcher.group(1);
			List<Date> currentGroupDates = new PrettyTimeParser().parse(word);
			allDates.addAll(currentGroupDates);
		}

		List<LocalDateTime> ldts = toLdtList(allDates);
		Task task;
		if (ldts.isEmpty()) {
			task = new TaskBasic(userInput);
		} else if (ldts.size() == 1) {
			task = new TaskBasic(userInput, ldts.get(0));
		} else {
			task = new TaskBasic(userInput, ldts.get(0), ldts.get(ldts.size() - 1));
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
