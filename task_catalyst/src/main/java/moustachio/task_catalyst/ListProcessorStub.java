package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

public class ListProcessorStub implements moustachio.task_catalyst.ListProcessor {

	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		return list;
	}

	@Override
	public List<Task> searchByKeyword(List<Task> list, String keyword) {
		List<Task> searchList = new ArrayList<Task>();
		for (Task task:list) {
			if (task.hasKeyword(keyword)) {
				searchList.add(task);
			}
		}
		return searchList;
	}

	@Override
	public List<Task> sortByDate(List<Task> list) {
		return list;
	}

}
