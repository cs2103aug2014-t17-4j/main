package moustachio;

import java.util.List;

public interface Logic {
    public List<Task> processCommand(String userCommand);

    public List<String> getHashtags();

    public Message getMessageTyping(String userCommand);

    public Message getMessageLast();
}
