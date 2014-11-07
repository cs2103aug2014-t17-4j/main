package moustachio.task_catalyst;

import java.util.List;

//@author A0111890
public interface Storage {
	
    public boolean saveTasks(List<Task> list, String fileName);
    
    public List<Task> loadTasks(String fileName);
    
    public boolean saveSetting(String name, String fileName,String value);
    
    public String loadSetting(String name, String fileName);
}
