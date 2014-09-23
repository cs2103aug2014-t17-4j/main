package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.StringProperty;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

public class TaskAbsoluteSystem extends TaskBasic {
	public TaskAbsoluteSystem(String description) {
		super(description);
	}

	public TaskAbsoluteSystem(String description, LocalDateTime dateStart) {
		super(description, dateStart);
	}

	public TaskAbsoluteSystem(String description, LocalDateTime dateStart,
			LocalDateTime dateEnd) {
		super(description, dateStart, dateEnd);
	}

	// Mutators

	public String getDescription() {
		PrettyTime p = new PrettyTime();
		List<Date> allDates = new ArrayList<Date>();
		String editedUserInput = this.description.get();
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(this.description.get());
		while (matcher.find()) {
			String word = matcher.group(1);
			List<DateGroup> currentGroupDates = new PrettyTimeParser()
					.parseSyntax(word);
			for (DateGroup group : currentGroupDates) {
				// editedUserInput = editedUserInput.replaceFirst(word,
				// word.substring(0,group.getPosition())+p.format(group.getDates().get(0)));
				allDates.addAll(group.getDates());
			}
		}
		return editedUserInput;
	}
}
