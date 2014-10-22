package moustachio.task_catalyst;

import java.util.List;

public interface Logic {
    public Message processCommand(String userCommand);

    public List<String> getHashtags();
    
    public List<String> getDefaultHashtags();

    public Message getMessageTyping(String userCommand);
    
    public List<Task> getList();
    
    public void testMode();
}