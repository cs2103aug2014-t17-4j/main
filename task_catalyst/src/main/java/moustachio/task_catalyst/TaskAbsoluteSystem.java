package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

public class TaskAbsoluteSystem implements Task {

	private String description;
	private boolean done;

	private LocalDateTime dateStart;
	private LocalDateTime dateEnd;

	// Constructors

	public TaskAbsoluteSystem(String description) {
		this.description = description;
	}

	public TaskAbsoluteSystem(String description, LocalDateTime dateStart) {
		this(description);
		this.dateStart = dateStart;
	}

	public TaskAbsoluteSystem(String description, LocalDateTime dateStart,
			LocalDateTime dateEnd) {
		this(description, dateStart);
		this.dateEnd = dateEnd;
	}

	// Mutators

	public String getDescription() {
		PrettyTime p = new PrettyTime();
		List<Date> allDates = new ArrayList<Date>();
		String editedUserInput = description;
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(description);
		while (matcher.find()) {
			String word = matcher.group(1);
			List<DateGroup> currentGroupDates = new PrettyTimeParser()
					.parseSyntax(word);
			for (DateGroup group : currentGroupDates) {
				//editedUserInput = editedUserInput.replaceFirst(word, word.substring(0,group.getPosition())+p.format(group.getDates().get(0)));
				allDates.addAll(group.getDates());
			}
		}
		return editedUserInput;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDateStart() {
		return this.dateStart;
	}

	public void setDateStart(LocalDateTime dateStart) {
		this.dateStart = dateStart;
	}

	public LocalDateTime getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(LocalDateTime dateEnd) {
		this.dateEnd = dateEnd;
	}

	public List<String> getHashtags() {
		List<String> hashtagList = new ArrayList<String>();
		String[] descriptionTokenized = description.split(" ");
		for (String token : descriptionTokenized) {
			if (token.startsWith("#")) {
				String tokenLowerAlphabets = token.toLowerCase().replaceAll(
						"[^A-Za-z0-9]+", "");
				hashtagList.add(tokenLowerAlphabets);
			}
		}
		return hashtagList;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	// Other Methods

	public boolean hasHashtag(String hashtag) {
		String hashtagLowerCase = hashtag.toLowerCase();
		return hasKeyword(hashtagLowerCase + " ")
				|| hasKeyword(hashtagLowerCase + "\n");
	}

	public boolean hasKeyword(String keyword) {
		String descriptionLowerCase = description.toLowerCase();
		String keywordLowerCase = keyword.toLowerCase();
		return descriptionLowerCase.contains(keywordLowerCase);
	}

	// Interfaces

	@Override
	public int compareTo(Task o) {
		LocalDateTime thisDateTime = this.getDateStart();
		LocalDateTime otherDateTime = o.getDateStart();
		return thisDateTime.compareTo(otherDateTime);
	}
}
