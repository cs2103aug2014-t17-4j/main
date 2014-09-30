package moustachio.task_catalyst;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ListProcessorActual implements ListProcessor{

	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		List<Task> searchList = new ArrayList<Task>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Calendar date = Calendar.getInstance();
		Date today = date.getTime();
		String todayDate = dateFormat.format(today);
		date.setTime(today);
		date.add(Calendar.DATE, 1);
		Date tomorrow = date.getTime();
		String tomorrowDate = dateFormat.format(tomorrow);
		switch(hashtag) {
			case "#all": 
				return list;
			case "#tdy": 
				for(Task task:list) {
					if(task.getDescriptionRaw().contains(todayDate)) {
						searchList.add(task);
					}
				}
				return searchList;
			case "#tmr": 
				for(Task task:list) {
					if(task.getDescriptionRaw().contains(tomorrowDate)) {
						searchList.add(task);
					}
				}
				return searchList;
			case "#upc": 
				for(Task task:list) {
					if(task.getDescriptionRaw().contains(todayDate) || task.getDescriptionRaw().contains(tomorrowDate)) {
						continue;
					}
					else {
						searchList.add(task);
					}
				}
				return searchList;
			default:
				return searchList;
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

}
