package moustachio;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task implements Serializable {

	private int id;
	private String description;
	private LocalDateTime dateStart;
	private LocalDateTime dateEnd;
	private ArrayList<String> hashTags;

	// Constructors

	public Task(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public Task(int id, String description, LocalDateTime dateStart) {
		this(id, description);
		this.dateStart = dateStart;
	}

	public Task(int id, String description,
			LocalDateTime dateStart, LocalDateTime dateEnd) {
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

	public ArrayList<String> getHashTags() {
		return hashTags;
	}

	public void setHashTags(ArrayList<String> hashTags) {
		this.hashTags = hashTags;
	}

	// Other Methods

	public void addTaskHashTag(String taskHashTag) {
		this.hashTags.add(taskHashTag);
	}

	public boolean hasHashTag(String taskHashTag) {
		return this.hashTags.contains(taskHashTag);
	}
}
