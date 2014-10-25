package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class ListProcessorActual implements ListProcessor {
	
	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		List<Task> filteredList = new ArrayList<Task>();
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
					if(!task.getAllDates().isEmpty() && task.isRange() 
						&& (TaskCatalystCommons.daysFromToday(task.getDateEnd()) == 0)
						&& !task.isDone()) {
						filteredList.add(task);
					}
					else if(!task.getAllDates().isEmpty() && !task.isRange() && !task.isDone()) {
						for(Date date:task.getAllDates()) {
							if(TaskCatalystCommons.isToday(date)) {
								filteredList.add(task);
							}
						}
					}
				}
				return filteredList;
			case "tmr": 
				for(Task task:list) {
					if(!task.getAllDates().isEmpty() && task.isRange() 
						&& (TaskCatalystCommons.daysFromToday(task.getDateEnd()) == 1)
						&& !task.isDone()) {
						filteredList.add(task);
					}
					else if(!task.getAllDates().isEmpty() && !task.isRange() && !task.isDone()) {
						for(Date date:task.getAllDates()) {
							if(TaskCatalystCommons.isTomorrow(date)) {
								filteredList.add(task);
							}
						}
					}
				}
				return filteredList;
			case "upc": 
				for(Task task:list) {
					if(!task.getAllDates().isEmpty() && task.isRange() 
						&& (TaskCatalystCommons.daysFromToday(task.getDateEnd()) >= 2)
						&& !task.isDone()) {
						filteredList.add(task);
					}
					else if(!task.getAllDates().isEmpty() && !task.isRange() && !task.isDone()) {
						for(Date date:task.getAllDates()) {
							if(TaskCatalystCommons.daysFromToday(date) >= 2) {
								filteredList.add(task);
							}
						}
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
		List<Task> overlapList = new ArrayList<Task>();
		for(Task task:list) {
			List<Task> tempList = new ArrayList<Task>(getOverlapping(task,list));
			if(!tempList.isEmpty()) {
				overlapList.add(task);
				overlapList.addAll(tempList);
			}
		}
		return new ArrayList<Task>(new LinkedHashSet<Task>(overlapList));
	}

	@Override
	public List<Task> getOverlapping(Task task, List<Task> list) {
		List<Task> overlapList = new ArrayList<Task>();
		for(Task task2:list) {
			if(isOverlap(task, task2)) {
				overlapList.add(task2);
			}
		}
		return overlapList;
	}
	
	private boolean isOverlap(Task task1, Task task2) {
		List<Date> list1 = new ArrayList<Date>(task1.getAllDates());
		List<Date> list2 = new ArrayList<Date>(task2.getAllDates());
		if(list1.isEmpty()|| list2.isEmpty() || task1.equals(task2) || task1.isDone() || task2.isDone()) {
			return false;
		}
		if(task1.isRange() && task2.isRange()) {
			if(task1.getDateEnd().before(task2.getDateStart()) || task2.getDateEnd().before(task1.getDateStart())) {
				return false;
			}
			else {
				return true;
			}
		}
		else if(task1.isRange() && !task2.isRange()) {
			for(Date date:list2) {
				if((date.before(task1.getDateEnd()) && date.after(task1.getDateStart())) 
					|| date.equals(task1.getDateStart()) 
					|| date.equals(task1.getDateEnd())) {
					return true;
				}
			}
			return false;
		}
		else if(!task1.isRange() && task2.isRange()) {
			for(Date date:list1) {
				if((date.before(task2.getDateEnd()) && date.after(task2.getDateStart()))
					|| date.equals(task2.getDateStart()) 
					|| date.equals(task2.getDateEnd())){
					return true;
				}
			}
			return false;
		}
		else {
			for(Date date1:list1) {
				for(Date date2:list2) {
					if(TaskCatalystCommons.isSameDate(date1, date2) && TaskCatalystCommons.isSameTime(date1, date2)) {
						return true;
					}
				}
			}
			return false;
		}
	}

}
