package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

//@author A0111890L
/**
 * This is the stub Storage used for unit testing of LogicActual.
 */
public class StorageStub implements Storage {

	List<Task> list;

	public StorageStub() {
		this.list = new ArrayList<Task>();
	}

	@Override
	public boolean saveTasks(List<Task> list, String fileName) {
		this.list = list;
		return true;
	}

	@Override
	public List<Task> loadTasks(String fileName) {
		return this.list;
	}

	@Override
	public boolean saveSetting(String name, String fileName, String value) {
		return false;
	}

	@Override
	public String loadSetting(String name, String fileName) {
		return null;
	}

}
