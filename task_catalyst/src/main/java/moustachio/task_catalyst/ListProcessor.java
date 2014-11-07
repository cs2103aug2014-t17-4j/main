package moustachio.task_catalyst;

import java.util.Date;
import java.util.List;

//@author A0111890
public interface ListProcessor {
	public List<Task> searchByHashtag(List<Task> list, String hashtag);

	public List<Task> searchByKeyword(List<Task> list, String keyword);

	public List<Task> searchByDate(List<Task> list, Date date);

	public List<Task> searchByDateRange(List<Task> list, Date start, Date end);

	public List<Task> sortByDate(List<Task> list);

	public List<Task> getOverlapping(List<Task> list);

	public List<Task> getOverlapping(Task task, List<Task> list);
}
