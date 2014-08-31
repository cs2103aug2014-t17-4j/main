import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task {

	private int taskId;
	private String taskDescription;
	private LocalDateTime taskStartDate;
	private LocalDateTime taskEndDate;
	private ArrayList<String> taskHashTags;

	// Constructors

	public Task(int taskId, String taskDescription) {
		this.taskId = taskId;
		this.taskDescription = taskDescription;
	}

	public Task(int taskId, String taskDescription, LocalDateTime taskStartDate) {
		this(taskId, taskDescription);
		this.taskStartDate = taskStartDate;
	}

	public Task(int taskId, String taskDescription,
			LocalDateTime taskStartDate, LocalDateTime taskEndDate) {
		this(taskId, taskDescription, taskStartDate);
		this.taskEndDate = taskEndDate;
	}

	// Mutators

	public int getTaskId() {
		return this.taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getTaskDescription() {
		return this.taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public LocalDateTime getTaskStartDate() {
		return this.taskStartDate;
	}

	public void setTaskStartDate(LocalDateTime taskStartDate) {
		this.taskStartDate = taskStartDate;
	}

	public LocalDateTime getTaskEndDate() {
		return this.taskEndDate;
	}

	public void setTaskEndDate(LocalDateTime taskEndDate) {
		this.taskEndDate = taskEndDate;
	}

	public ArrayList<String> getTaskHashTags() {
		return taskHashTags;
	}

	public void setTaskHashTags(ArrayList<String> taskHashTags) {
		this.taskHashTags = taskHashTags;
	}

	// Other Methods

	public void addTaskHashTag(String taskHashTag) {
		this.taskHashTags.add(taskHashTag);
	}

	public boolean hasHashTag(String taskHashTag) {
		return this.taskHashTags.contains(taskHashTag);
	}
}
