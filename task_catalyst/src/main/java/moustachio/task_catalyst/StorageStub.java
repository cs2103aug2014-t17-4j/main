package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String loadSetting(String name, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
