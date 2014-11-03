package moustachio.task_catalyst;

import java.util.Date;
import java.util.List;

public interface Task extends Comparable<Task> {

	public String getDescription();

	public String getDescriptionRaw();

	public String getDescriptionEdit();

	public void setDescription(String description);

	public List<Date> getAllDates();

	public Date getDateStart();

	public Date getDateEnd();

	public Date getNextDate();

	public List<String> getHashtags();

	public boolean isRange();

	public boolean isBlocking();

	public boolean isMultiple();

	public boolean isDone();

	public boolean isPriority();

	public boolean isOverdue();

	public boolean isOverlapping();
	
	public boolean isAllDay();

	public void setDone(boolean done);

	public void setOverlapping(boolean overlapping);

	public boolean hasHashtag(String hashtag);

	public boolean hasKeyword(String keyword);

}
