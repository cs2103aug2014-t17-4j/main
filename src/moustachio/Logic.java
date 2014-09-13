package moustachio;

import java.util.ArrayList;

public interface Logic {
    public ArrayList<Task> processCommand(String userCommand);

    public ArrayList<String> getHashtags();

    public String getHint(String userCommand);

    public String getMessage();
}
