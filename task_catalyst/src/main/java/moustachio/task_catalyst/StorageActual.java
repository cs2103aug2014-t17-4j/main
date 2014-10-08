package moustachio.task_catalyst;

import java.io.IOException;
import java.util.List;

public class StorageActual implements Storage {
	
	@Override
	public boolean saveTasks(List<Task> list, String fileName) {
		return saveTasksToFile(list, fileName);
	}

	@Override
	public List<Task> loadTasks(String fileName) {
		return FileHandler.readTask(fileName);
	}

	@Override
	public boolean saveSetting(String name, String fileName, String value) {
		return FileHandler.writeSetting(name, fileName, value) ;
	}

	@Override
	public String loadSetting(String name, String fileName) {
		return FileHandler.readSetting(name, fileName);
	}
	
	private boolean saveTasksToFile(List<Task> list, String fileName) {
		try{
			FileHandler.clear(fileName);
			for(int i=0; i<list.size(); i++){
				 FileHandler.writeTask(list.get(i), fileName);
			}
			return true;
		}catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}