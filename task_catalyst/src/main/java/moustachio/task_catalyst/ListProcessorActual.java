//@author A0112584J
package moustachio.task_catalyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * ListProcessorActual is able to filter tasks by search term, hashtags and
 * dates and provide sorting functionality. It provides the main functionality
 * for the Overlapping Notification System.
 */
public class ListProcessorActual implements ListProcessor {

	private static final String KEYWORD_TO = "to";
	private static final String KEYWORD_BETWEEN = "between";

	//This method returns a list of tasks depending on the hastag user specifies (case-insensitive)
	//pre-cond: The list is not null
	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		assert list != null;
		switch (hashtag) {
		case "all" :
			return searchByHashtagAll(list);
		case "pri" :
			return searchByHashtagPriority(list);
		case "ovd" :
			return searchByHashtagOverdue(list);
		case "tdy" :
			return searchByHashtagToday(list);
		case "tmr" :
			return searchByHashtagTomorrow(list);
		case "upc" :
			return searchByHashtagUpcoming(list);
		case "smd" :
			return searchByHashtagSomeday(list);
		case "dne" :
			return searchByHashtagDone(list);
		case "olp" :
			return getOverlapping(list);
		default:
			return searchByHashTagUserDefined(list, hashtag);
		}
	}

	//This method returns a list of tasks containing the keyword user keys in (case-insensitive)
	//This method can also search by single date, multiple dates, a date range (case-insensitive)
	//Example of single date: 31 dec
	//Example of multiple dates: today, 12 nov, 30 nov
	//Examples of a date range: 12 nov to 30 nov
	//							betweeen 12 nov and 30 nov
	//pre-cond: The list is not null
	@Override
	public List<Task> searchByKeyword(List<Task> list, String keyword) {
		assert list != null;
		List<Task> searchList = new ArrayList<Task>();
		boolean strict = false;
		String interpretedString = TaskCatalystCommons.getInterpretedString(
				keyword, strict);
		List<Date> dates = TaskCatalystCommons.getAllDates(interpretedString);
		Collections.sort(dates);

		if (dates.isEmpty()) {
			for (Task task : list) {
				if (task.hasKeyword(keyword)) {
					searchList.add(task);
				}
			}
		}

		if (isRanged(interpretedString, dates)) {
			Date start = dates.get(0);
			Date end = dates.get(dates.size() - 1);
			searchList.addAll(searchByDateRange(list, start, end));
		} else {
			searchList.addAll(searchByDate(list, dates));
		}

		Collections.sort(searchList);
		return searchList;
	}

	//This method returns a list of tasks which is sorted chronologically
	//pre-cond: The list is not null
	@Override
	public List<Task> sortByDate(List<Task> list) {
		assert list != null;
		List<Task> sortList = new ArrayList<Task>(list);
		Collections.sort(sortList);
		return sortList;
	}

	//This method returns a list of tasks which a task in the list overlaps with at least one other task within the list
	//pre-cond: The list is not null
	@Override
	public List<Task> getOverlapping(List<Task> list) {
		assert list != null;
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

	//This method returns a list of tasks which at least one task in the list overlaps with the task parameter
	//pre-cond: The list is not null
	@Override
	public List<Task> getOverlapping(Task task, List<Task> list) {
		assert list != null;
		List<Task> overlapList = new ArrayList<Task>();

		for (Task task2 : list) {
			if (isOverlapping(task, task2)) {
				overlapList.add(task2);
			}
		}

		return overlapList;
	}

	private boolean hasRangeKeyword(String interpretedString) {
		if (TaskCatalystCommons.hasWordBeforeDates(interpretedString,
				KEYWORD_TO)
			|| TaskCatalystCommons.hasWordBeforeDates(interpretedString,
					KEYWORD_BETWEEN)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isRanged(String interpretedString, List<Date> dates) {
		if (hasRangeKeyword(interpretedString) && (dates.size() > 1)) {
			return true;
		} else {
			return false;
		}
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
		if (task1.isOverdue() || task2.isOverdue() || task1.isDeadline() 
				|| task2.isDeadline() || task1.isAllDay()
				|| task2.isAllDay() || task1.isDone() || task2.isDone()
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

	private List<Task> searchByDate(List<Task> list, List<Date> dates) {
		List<Task> searchList = new ArrayList<Task>();

		for (Task task : list) {
			for (Date date : dates) {
				if (task.hasDate(date) && !searchList.contains(task)) {
					searchList.add(task);
				}
			}
		}

		return searchList;
	}

	private List<Task> searchByDateRange(List<Task> list, Date start, Date end) {
		List<Task> searchList = new ArrayList<Task>();

		for (Task task : list) {
			if (task.isBetweenDates(start, end) && !searchList.contains(task)) {
				searchList.add(task);
			}
		}

		return searchList;
	}
}
