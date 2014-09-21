package moustachio;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class LogicActual implements Logic {

	private Storage storage;
	private Processor processor;
	private Message messageLast;
	private List<Task> tasks;
	
	public LogicActual() {
		storage = new StorageStub();
		processor = new ProcessorStub();
		tasks = storage.loadTasks("tasks.txt");
	}

	@Override
	public ArrayList<Task> processCommand(String userCommand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getHashtags() {
		SortedSet<String> allHashTags = new TreeSet<String>();
		for (Task task:tasks) {
			allHashTags.addAll(task.getHashTags());
		}
		List<String> allHashTagsList = new ArrayList<String>(allHashTags);
		return allHashTagsList;
	}

	@Override
	public Message getMessageTyping(String userCommand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message getMessageLast() {
		// TODO Auto-generated method stub
		return null;
	}

}
