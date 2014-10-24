package moustachio.task_catalyst;

import java.io.IOException;
import java.util.List;

/**
 * This program  is implements Storage for Task Catalyst system. 
 * 
 * This program makes use of FileHandler to handle saving and loading tasks or setting. 
 * 
 * @author Lin XiuQing (A0112764J)
 */

public class StorageActual implements Storage {

	private static BlackBox blackbox = BlackBox.getInstance();
	
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
			blackbox.info("IO fault has been enountered.");
		}
		return false;
	}
}