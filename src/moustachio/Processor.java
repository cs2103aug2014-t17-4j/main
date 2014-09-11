package moustachio;

import java.util.ArrayList;

public interface Processor {
    public ArrayList<Task> searchByHashtag(ArrayList<Task> list, String hashtag);

    public ArrayList<Task> searchByKeyword(ArrayList<Task> list, String keyword);

    public ArrayList<Task> sortByDate(ArrayList<Task> list);
}
