package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

public class ListProcessorStub implements
		moustachio.task_catalyst.ListProcessor {

	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		List<Task> searchList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone()) {
				searchList.add(task);
			}
		}
		return searchList;
	}

	@Override
	public List<Task> searchByKeyword(List<Task> list, String keyword) {
		List<Task> searchList = new ArrayList<Task>();
		for (Task task : list) {
			if (task.hasKeyword(keyword)) {
				searchList.add(task);
			}
		}
		return searchList;
	}

	@Override
	public List<Task> sortByDate(List<Task> list) {
		List<Task> newList = new ArrayList<Task>(list);
		Collections.sort(newList);
		return newList;
	}

	@Override
	public List<Task> getOverlapping(List<Task> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> getOverlapping(Task task, List<Task> list) {
		// TODO Auto-generated method stub
		return null;
	}

}
