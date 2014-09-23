package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskBasic implements Task {

	protected StringProperty description;
	private boolean done;

	private LocalDateTime dateStart;
	private LocalDateTime dateEnd;

	// Constructors

	public TaskBasic(String description) {
		this.description = new SimpleStringProperty();
		this.description.set(description);
	}

	public TaskBasic(String description, LocalDateTime dateStart) {
		this(description);
		this.dateStart = dateStart;
	}

	public TaskBasic(String description, LocalDateTime dateStart,
			LocalDateTime dateEnd) {
		this(description, dateStart);
		this.dateEnd = dateEnd;
	}

	// Mutators

	public String getDescription() {
		return this.description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public StringProperty getDescriptionProperty() {
		return this.description;
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
		String[] descriptionTokenized = this.description.get().split(" ");
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
		String descriptionLowerCase = this.description.get().toLowerCase();
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
