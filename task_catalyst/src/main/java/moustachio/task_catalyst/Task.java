package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface Task extends Comparable<Task> {

	public String getDescription();

	public void setDescription(String description);

	public LocalDateTime getDateStart();

	public void setDateStart(LocalDateTime dateStart);

	public LocalDateTime getDateEnd();

	public void setDateEnd(LocalDateTime dateEnd);

	public List<String> getHashtags();

	public boolean isDone();

	public void setDone(boolean done);

	// Other Methods

	public boolean hasHashtag(String hashtag);

	public boolean hasKeyword(String keyword);

}
