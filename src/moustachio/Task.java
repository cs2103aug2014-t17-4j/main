package moustachio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task implements Comparable<Task> {

	private int id;
	private String description;
	private boolean done;

	private LocalDateTime dateStart;
	private LocalDateTime dateEnd;

	// Constructors

	public Task(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public Task(int id, String description, LocalDateTime dateStart) {
		this(id, description);
		this.dateStart = dateStart;
	}

	public Task(int id, String description, LocalDateTime dateStart,
			LocalDateTime dateEnd) {
		this(id, description, dateStart);
		this.dateEnd = dateEnd;
	}

	// Mutators

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
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

	public List<String> getHashTags() {
		List<String> hashtagList = new ArrayList<String>();
		String[] descriptionTokenized = description.split(" ");
		for (String token : descriptionTokenized) {
			if (token.startsWith("#")) {
				hashtagList.add(token);
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
		return hasKeyword("#" + hashtagLowerCase);
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
