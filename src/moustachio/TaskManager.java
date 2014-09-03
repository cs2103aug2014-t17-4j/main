package moustachio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskManager {

	public static void main(String[] args) {
	}

	private Path path;
	private ArrayList<Task> tasks;
	private ArrayList<Task> tasksDone;

	// Constructors

	public TaskManager(String pathString) {
		path = Paths.get(pathString);
		tasks = new ArrayList<Task>();
		tasksDone = new ArrayList<Task>();
	}

	// Mutators

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	// File I/O

	public boolean writeTasksToFile() {
		try {
			OutputStream outputStream = Files.newOutputStream(this.path);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					outputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					bufferedOutputStream);
			objectOutputStream.writeObject(tasks);
			objectOutputStream.writeObject(tasksDone);
			objectOutputStream.close();
			bufferedOutputStream.close();
			outputStream.close();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
		}
	}

	@SuppressWarnings("unchecked")
	public boolean readTasksFromFile() {
		try {
			InputStream inputStream = Files.newInputStream(this.path);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					inputStream);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					bufferedInputStream);
			Object tasksObject = objectInputStream.readObject();
			tasks = (ArrayList<Task>) tasksObject;
			Object doneObject = objectInputStream.readObject();
			tasksDone = (ArrayList<Task>) doneObject;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// Other Methods
	
	public void addTask(Task task) {
		this.tasks.add(task);
	}

	public Task getTask(int id) {
		Iterator<Task> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			Task task = iterator.next();
			if (task.getId()==id) {
				return task;
			}
		}
		return null;
	}
	
	public Task deleteTask(int id) {
		Iterator<Task> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			Task task = iterator.next();
			if (task.getId()==id) {
				iterator.remove();
				return task;
			}
		}
		return null;
	}
}