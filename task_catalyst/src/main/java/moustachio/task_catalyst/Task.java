package moustachio.task_catalyst;

import java.util.Date;
import java.util.List;

// @author A0111890
public interface Task extends Comparable<Task> {

	public String getDescription();

	public String getDescriptionRaw();

	public String getDescriptionEdit();

	public void setDescription(String description);

	public void setDone(boolean done);

	public void setOverlapping(boolean overlapping);

	public List<Date> getAllDates();

	public Date getDateStart();

	public Date getDateEnd();

	public Date getNextDate();

	public List<String> getHashtags();

	public boolean hasHashtag(String hashtag);

	public boolean hasKeyword(String keyword);

	public boolean isAllDay();

	public boolean isBlocking();

	public boolean isDone();

	public boolean isMultiple();

	public boolean isOverdue();

	public boolean isOverlapping();
	
	public boolean isPriority();

	public boolean isRange();

	public boolean isDeadline();

}
