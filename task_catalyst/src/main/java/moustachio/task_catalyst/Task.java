package moustachio.task_catalyst;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javafx.beans.property.StringProperty;

public interface Task extends Comparable<Task> {

	public String getDescription();

	public String getDescriptionRaw();

	public String getDescriptionEdit();

	public void setDescription(String description);

	public StringProperty getDescriptionProperty();

	public List<Date> getAllDates();

	public LocalDateTime getDateStart();

	public LocalDateTime getDateEnd();

	public List<String> getHashtags();

	public boolean isRange();

	public void setRange(boolean done);

	public boolean isDone();

	public void setDone(boolean done);

	// Other Methods

	public boolean hasHashtag(String hashtag);

	public boolean hasKeyword(String keyword);

}
