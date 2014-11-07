//@author A0112584J
package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class ListProcessorActual implements ListProcessor {

	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		switch (hashtag) {
		case "all":
			return searchByHashtagAll(list);
		case "pri":
			return searchByHashtagPriority(list);
		case "ovd":
			return searchByHashtagOverdue(list);
		case "tdy":
			return searchByHashtagToday(list);
		case "tmr":
			return searchByHashtagTomorrow(list);
		case "upc":
			return searchByHashtagUpcoming(list);
		case "smd":
			return searchByHashtagSomeday(list);
		case "dne":
			return searchByHashtagDone(list);
		case "olp":
			return getOverlapping(list);
		default:
			return searchByHashTagUserDefined(list, hashtag);
		}
	}

	@Override
	public List<Task> searchByKeyword(List<Task> list, String keyword) {
		List<Task> searchList = new ArrayList<Task>();
		List<Date> dates = TaskCatalystCommons.getInferredDate(keyword);
		for (Task task : list) {
			for (Date date : dates) {
				if (task.hasKeyword(keyword) || task.hasDate(date)) {
					searchList.add(task);
				}
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
		for (Task task : list) {
			List<Task> tempList = new ArrayList<Task>(
					getOverlapping(task, list));
			if (!tempList.isEmpty()) {
				overlapList.add(task);
				overlapList.addAll(tempList);
			}
		}
		return new ArrayList<Task>(new LinkedHashSet<Task>(overlapList));
	}

	@Override
	public List<Task> getOverlapping(Task task, List<Task> list) {
		List<Task> overlapList = new ArrayList<Task>();
		for (Task task2 : list) {
			if (isOverlapping(task, task2)) {
				overlapList.add(task2);
			}
		}
		return overlapList;
	}

	private List<Task> searchByHashtagAll(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone()) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private List<Task> searchByHashtagPriority(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone() && task.isPriority()) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private List<Task> searchByHashtagOverdue(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone() && task.isOverdue()) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private List<Task> searchByHashtagToday(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone()
					&& task.isRange()
					&& (TaskCatalystCommons.daysFromToday(task.getDateStart()) <= 0)
					&& (TaskCatalystCommons.daysFromToday(task.getDateEnd()) >= 0)) {
				filteredList.add(task);
			} else if (!task.isDone() && !task.getAllDates().isEmpty()
					&& !task.isRange()) {
				for (Date date : task.getAllDates()) {
					if (TaskCatalystCommons.isToday(date)) {
						filteredList.add(task);
					}
				}
			}
		}
		return new ArrayList<Task>(new LinkedHashSet<Task>(filteredList));
	}

	private List<Task> searchByHashtagTomorrow(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone()
					&& task.isRange()
					&& (TaskCatalystCommons.daysFromToday(task.getDateStart()) <= 1)
					&& TaskCatalystCommons.daysFromToday(task.getDateEnd()) >= 1) {
				filteredList.add(task);
			} else if (!task.isDone() && !task.getAllDates().isEmpty()
					&& !task.isRange()) {
				for (Date date : task.getAllDates()) {
					if (TaskCatalystCommons.isTomorrow(date)) {
						filteredList.add(task);
					}
				}
			}
		}
		return new ArrayList<Task>(new LinkedHashSet<Task>(filteredList));
	}

	private List<Task> searchByHashtagUpcoming(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone()
					&& task.isRange()
					&& (TaskCatalystCommons.daysFromToday(task.getDateEnd()) >= 2)) {
				filteredList.add(task);
			} else if (!task.isDone() && !task.getAllDates().isEmpty()
					&& !task.isRange()) {
				for (Date date : task.getAllDates()) {
					if (TaskCatalystCommons.daysFromToday(date) >= 2) {
						filteredList.add(task);
					}
				}
			}
		}
		return new ArrayList<Task>(new LinkedHashSet<Task>(filteredList));
	}

	private List<Task> searchByHashTagUserDefined(List<Task> list,
			String hashtag) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone() && task.hasHashtag(hashtag)) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private List<Task> searchByHashtagSomeday(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (!task.isDone() && task.getAllDates().isEmpty()) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private List<Task> searchByHashtagDone(List<Task> list) {
		List<Task> filteredList = new ArrayList<Task>();
		for (Task task : list) {
			if (task.isDone()) {
				filteredList.add(task);
			}
		}
		return filteredList;
	}

	private boolean isOverlapping(Task task1, Task task2) {
		if (task1.isDeadline() || task2.isDeadline() 
				|| task1.isAllDay() || task2.isAllDay() 
				|| task1.isDone() || task2.isDone() 
				|| task1.getAllDates().isEmpty()
				|| task2.getAllDates().isEmpty() || task1.equals(task2)) {
			return false;
		}
		if (task1.isRange() && task2.isRange()) {
			return isOverlappingTwoRanged(task1, task2);
		} else if (task1.isRange() && !task2.isRange()) {
			return isOverlappingOneRanged(task1, task2);
		} else if (!task1.isRange() && task2.isRange()) {
			return isOverlappingOneRanged(task2, task1);
		} else {
			return isOverlappingNonRanged(task1, task2);
		}
	}

	private boolean isOverlappingTwoRanged(Task task1, Task task2) {
		if (task1.getDateStart().after(task2.getDateStart())) {
			Task temp = task1;
			task1 = task2;
			task2 = temp;
		}
		if (task2.getDateStart().before(task1.getDateEnd())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isOverlappingOneRanged(Task task1, Task task2) {
		List<Date> list = new ArrayList<Date>(task2.getAllDates());
		for (Date date : list) {
			if ((date.before(task1.getDateEnd()) && date.after(task1
					.getDateStart())) || date.equals(task1.getDateStart())) {
				return true;
			}
		}
		return false;
	}

	private boolean isOverlappingNonRanged(Task task1, Task task2) {
		List<Date> list1 = new ArrayList<Date>(task1.getAllDates());
		List<Date> list2 = new ArrayList<Date>(task2.getAllDates());
		for (Date date1 : list1) {
			for (Date date2 : list2) {
				if (date1.equals(date2)) {
					return true;
				}
			}
		}
		return false;
	}
}
