package moustachio;

import java.util.List;

public interface ListProcessor {
    public List<Task> searchByHashtag(List<Task> list, String hashtag);

    public List<Task> searchByKeyword(List<Task> list, String keyword);

    public List<Task> sortByDate(List<Task> list);
}
