package moustachio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskBuilder {

	private static final String[] dateFormats = {
		"d[' '][MMMM][MMM][MM][' ']yyyy", // Day Month Year
		"[MMMM][MMM][MM][' ']d[' ']yyyy", // Month Day Year
		"yyyy[' ']d[' '][MMMM][MMM][MM]", // Year Day Month
		"yyyy[' '][MMMM][MMM][MM][' ']d", // Year Month Day
	};

	private static final String[] timeFormats = {
		"k[:]m", 		// 24H:Min
		"h[:][m][' ']a" // 12H:Min AM/PM
	};

	public static void main(String[] args) {
		String userInput = "Meeting with boss on latest promotion at MR5 on 30 Aug at 15:30";
		Task task = createTask(userInput);
		System.out.println(task.getDateStart().toString());
	}

	public static Task createTask(String userInput) {
		LocalDate localDate = findDate(userInput);
		LocalTime localTime = findTime(userInput);
		int descriptionCutIndex = userInput.length();
		if (localDate == null) {
			descriptionCutIndex = userInput.lastIndexOf("on");
			localDate = LocalDate.now();
		}
		if (localTime == null) {
			descriptionCutIndex = Math.min(userInput.lastIndexOf("at"),descriptionCutIndex);
			localTime = findTime("12AM");
		}
		String description = userInput.substring(0, descriptionCutIndex);
		LocalDateTime dateStart = LocalDateTime.of(localDate, localTime);
		return new Task(1, description, dateStart);
	}

	public static LocalDate findDate(String userInput) {
		String[] splitUserInput = userInput.split("\\s+");
		LocalDate localDate = null;
		for (int pieces = 3; pieces > 0; pieces--) {
			for (int i = 0; i < splitUserInput.length - (pieces - 1); i++) {
				String dateString = "";
				for (int j = 0; j < pieces; j++) {
					dateString += splitUserInput[i + j];
				}
				// System.out.println(dateString);
				localDate = parseLocalDate(dateString);
				if (localDate != null) {
					if (localDate.isBefore(LocalDate.now())) {
						localDate = parseLocalDate(dateString
								+ (LocalDate.now().getYear() + 1));
					}
					return localDate;
				}
				localDate = parseLocalDate(dateString + LocalDate.now().getYear());
				if (localDate != null) {
					if (localDate.isBefore(LocalDate.now())) {
						localDate = parseLocalDate(dateString
								+ (LocalDate.now().getYear() + 1));
					}
					return localDate;
				}
			}
		}
		return null;
	}

	public static LocalTime findTime(String userInput) {
		String[] splitUserInput = userInput.split("\\s+");
		LocalTime localTime = null;
		for (int pieces = 3; pieces > 0; pieces--) {
			for (int i = 0; i < splitUserInput.length - (pieces - 1); i++) {
				String timeString = "";
				for (int j = 0; j < pieces; j++) {
					timeString += splitUserInput[i + j];
				}
				// System.out.println(timeString);
				localTime = parseLocalTime(timeString);
				if (localTime != null) {
					return localTime;
				}
			}
		}
		return null;
	}

	public static LocalDate parseLocalDate(String dateString) {
		for (int i = 0; i < dateFormats.length; i++) {
			try {
				LocalDate localDate = LocalDate.parse(dateString,
						DateTimeFormatter.ofPattern(dateFormats[i]));
				//System.out.println("Date Parsed: " + localDate);
				return localDate;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return null;
	}

	public static LocalTime parseLocalTime(String timeString) {
		for (int i = 0; i < timeFormats.length; i++) {
			try {
				LocalTime localTime = LocalTime.parse(timeString,
						DateTimeFormatter.ofPattern(timeFormats[i]));
				//System.out.println("Time Parsed: " + localTime);
				return localTime;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return null;
	}
}
