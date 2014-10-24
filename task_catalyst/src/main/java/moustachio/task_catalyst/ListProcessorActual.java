package moustachio.task_catalyst;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListProcessorActual implements ListProcessor {
	
	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		List<Task> filteredList = new ArrayList<Task>();
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		switch(hashtag) {
			case "all": 
				for(Task task:list) {
					if(!task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
			case "tdy": 
				for(Task task:list) {
					if(task.getDateEnd() != null && task.getDateEnd().toLocalDate().isEqual(today) && !task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
			case "tmr": 
				for(Task task:list) {
					if(task.getDateEnd() != null && task.getDateEnd().toLocalDate().isEqual(tomorrow) && !task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
			case "upc": 
				for(Task task:list) {
					if(task.getDateEnd() != null && task.getDateEnd().toLocalDate().isAfter(tomorrow) && !task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
			case "smd":
				for(Task task:list) {
					if(task.getDateEnd() == null && !task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
			case "dne":
				for(Task task:list) {
					if(task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
			default:
				for(Task task:list) {
					if(task.hasHashtag(hashtag) && !task.isDone()) {
						filteredList.add(task);
					}
				}
				return filteredList;
		}
	}

	@Override
	public List<Task> searchByKeyword(List<Task> list, String keyword) {
		List<Task> searchList = new ArrayList<Task>();
		for(Task task:list) {
			if(task.hasKeyword(keyword)) {
				searchList.add(task);
			}
		}
		return searchList;
	}

	@Override
	public List<Task> sortByDate(List<Task> list) {
		List<Task> sortList = new ArrayList<Task>(list);
		Collections.sort(sortList);
		return sortList;
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
