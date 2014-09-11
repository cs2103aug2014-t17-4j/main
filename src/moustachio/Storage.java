package moustachio;

import java.util.ArrayList;

public interface Storage {
    public boolean saveTasks(ArrayList<Task> list, String fileName);
    public ArrayList<Task> loadTasks(String fileName);
    //public boolean saveSetting(String name, String fileName,String value);
    //public String loadSetting(String name, String fileName);
}
