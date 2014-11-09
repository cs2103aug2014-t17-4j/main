package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//@author A0111890L
public class TaskBuilderAdvancedTest {

	TaskBuilderAdvanced taskBuilder = new TaskBuilderAdvanced();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		BlackBox.getInstance().close();
	}

	// Test for basic date recognition.
	@Test
	public void tc1() {
		Task task = taskBuilder.createOneTask("Meet boss 21 Jun 10:05am");
		assertEquals("Meet boss on 21 Jun 10:05AM", task.getDescriptionEdit());
	}

	// Test for basic date range recognition.
	@Test
	public void tc2() {
		Task task = taskBuilder
				.createOneTask("Meet boss from 21 jun 10am to 22nd jun");
		assertEquals("Meet boss from 21 Jun 10AM to 22 Jun 10AM",
				task.getDescriptionEdit());
	}

	// Test for basic time range recognition.
	@Test
	public void tc3() {
		Task task = taskBuilder
				.createOneTask("Meet boss from 21 jun 10am to 11am");
		assertEquals("Meet boss from 21 Jun 10AM to 11AM",
				task.getDescriptionEdit());
	}

	// Test for relative display.
	@Test
	public void tc4() {
		Task task = taskBuilder.createOneTask("Meet boss tomorrow 10am");
		assertEquals("Meet boss tomorrow 10AM", task.getDescriptionEdit());
	}

	// Test for relative display with day range.
	@Test
	public void tc5() {
		Task task = taskBuilder
				.createOneTask("Meet boss today 10am to today 12pm");
		assertEquals("Meet boss today 10AM to 12PM", task.getDescriptionEdit());
	}

	// Test for relative display with day range.
	@Test
	public void tc6() {
		Task task = taskBuilder
				.createOneTask("Meet boss 21 Jun 10am to 25 sep 2015 12pm");
		assertEquals("Meet boss on 21 Jun 10AM to 25 Sep 2015 12PM",
				task.getDescriptionEdit());
	}

	// Ignore text+number
	@Test
	public void tc7() {
		Task task = taskBuilder.createOneTask("Meet boss today 10am at LT5");
		assertEquals("Meet boss today 10AM at [LT5]", task.getDescriptionEdit());
	}

	// Ignore text/symbol+number
	@Test
	public void tc8() {
		Task task = taskBuilder.createOneTask("Meet boss today 10am at MR-23");
		assertEquals("Meet boss today 10AM at [MR-23]",
				task.getDescriptionEdit());
	}

	// Ignore numbers of 5 or more digits
	@Test
	public void tc9() {
		Task task = taskBuilder
				.createOneTask("Call John later 5pm at 90044174");
		assertEquals("Call John later today 5PM at [90044174]",
				task.getDescriptionEdit());
	}

	// Swap inverted range
	@Test
	public void tc10() {
		Task task = taskBuilder.createOneTask("Meeting today 10pm to 1pm");
		assertEquals("Meeting today 1PM to 10PM", task.getDescriptionEdit());
	}

	// Swap inverted range different date
	@Test
	public void tc11() {
		Task task = taskBuilder
				.createOneTask("Meeting tomorrow 10pm to today 1pm");
		assertEquals("Meeting today 1PM to tomorrow 10PM",
				task.getDescriptionEdit());
	}

	// Swap inverted multiple
	@Test
	public void tc12() {
		Task task = taskBuilder
				.createOneTask("Visitors arriving 10pm and 1pm and 2pm");
		assertEquals("Visitors arriving today 1PM, 2PM and 10PM",
				task.getDescriptionEdit());
	}

	// Swap inverted multiple different date
	@Test
	public void tc13() {
		Task task = taskBuilder
				.createOneTask("Visitors arriving today 10pm and tomorrow 1pm and tomorrow 2pm");
		assertEquals("Visitors arriving today 10PM, tomorrow 1PM and 2PM",
				task.getDescriptionEdit());
	}

	// Grammar correction at-to-on (my fail on a Friday)
	@Test
	public void tc14() {
		Task task = taskBuilder.createOneTask("Meet boss at 21 oct 2003 1pm");
		assertEquals("Meet boss on 21 Oct 2003 1PM", task.getDescriptionEdit());
	}

	// Ignore partial matches.
	@Test
	public void tc15() {
		Task task = taskBuilder
				.createOneTask("Meet boss now 5pm knowing that I'm screwed");
		assertEquals("Meet boss today 5PM knowing that I'm screwed",
				task.getDescriptionEdit());
	}

	// Ignore partial matches.
	@Test
	public void tc16() {
		Task task = taskBuilder
				.createOneTask("LT5 [sep] programme on 5 sep 10am");
		assertEquals("[LT5] [sep] programme on 5 Sep 10AM",
				task.getDescriptionEdit());
	}

	// Ignore partial matches.
	@Test
	public void tc17() {
		Task task = taskBuilder
				.createOneTask("Meet boss today 5pm to 6pm at LT5");
		assertEquals("Meet boss today 5PM to 6PM at [LT5]",
				task.getDescriptionEdit());
	}

	// Automatically remove prepositions.
	@Test
	public void tc18() {
		Task task = taskBuilder.createOneTask("At at at at at at at today 5pm");
		assertEquals(null, task);
	}

	// Able to prevent mixed date time formats.
	@Test
	public void tc19() {
		Task task = taskBuilder
					.createOneTask("Today at 5pm and tomorrow from 6pm to 7pm");
		assertEquals(null, task);
	}

	// Test Range Recognition - No Range
	@Test
	public void tc20() {
		Task task = taskBuilder.createOneTask("Meet boss at 5PM");
		assertEquals(false, task.isRange());
	}

	// Test Range Recognition - Range
	@Test
	public void tc21() {
		Task task = taskBuilder.createOneTask("Meet boss from 5PM to 6PM");
		assertEquals(true, task.isRange());
	}

	// Repeated Date Correction
	@Test
	public void tc22() {
		Task task = taskBuilder.createOneTask("Meet boss from 5PM to 5PM");
		assertEquals("Meet boss from today 5PM", task.getDescriptionEdit());
	}

	// Displaying dates when previous day == current day, and next time ==
	// current time.
	@Test
	public void tc23() {
		Task task = taskBuilder
				.createOneTask("Meet boss today 1pm, 5pm and tomorrow");
		assertEquals("Meet boss today 1PM, 5PM and tomorrow 5PM", task.getDescriptionEdit());
	}

	// Repeated Date alternate.
	@Test
	public void tc24() {
		Task task = taskBuilder.createOneTask("Meet boss 5PM and today.");
		assertEquals("Meet boss today 5PM.", task.getDescriptionEdit());
	}

	// Ignore parsing of number words
	@Test
	public void tc25() {
		Task task = taskBuilder.createOneTask("Twenty four");
		assertEquals("Twenty four", task.getDescriptionEdit());
	}

	// Ignore parsing of number words
	@Test
	public void tc26() {
		Task task = taskBuilder
				.createOneTask("one thing from 1 oct 2013 5pm to 1 oct 2013 6pm");
		assertEquals("one thing from 1 Oct 2013 5PM to 6PM", task.getDescriptionEdit());
	}

	// Handle words like "a*", "h*", after a date range
	@Test
	public void tc27() {
		Task task = taskBuilder
				.createOneTask("Meet boss from 5 Oct 1pm to 2pm a");
		assertEquals("Meet boss from 5 Oct 1PM to 2PM a", task.getDescriptionEdit());
	}

	// Handle words like "test" after a date range
	@Test
	public void tc28() {
		Task task = taskBuilder
				.createOneTask("Meet boss from 5 Oct 1pm to 2pm test");
		assertEquals("Meet boss from 5 Oct 1PM to 2PM [test]", task.getDescriptionEdit());
	}

	// Handle words containing "ated" after a date range
	@Test
	public void tc29() {
		Task task = taskBuilder.createOneTask("Get movie rated 1pm");
		assertEquals("Get movie [rated] today 1PM", task.getDescriptionEdit());
	}

	// Display Comma Separated Items Properly (artifact problem from multi-pass
	// parsing)
	@Test
	public void tc30() {
		Task task = taskBuilder.createOneTask("Meet boss 5pm 6pm 7pm.");
		assertEquals("Meet boss today 5PM, 6PM and 7PM.", task.getDescriptionEdit());
	}

	// Catch mixed date types in this situation.
	@Test
	public void tc31() {
		Task task = taskBuilder.createOneTask("Meet boss 5pm 6pm to 7pm.");
		assertEquals(null, task);
	}

	// Catch mixed date types, in different many characters away.
	@Test
	public void tc32() {
		Task task = taskBuilder
				.createOneTask("Meet boss 5pm and 6pm and then meet secretary from 5pm to 7pm.");
		assertEquals(null, task);
	}

	// Catch invalid date range in the case of tc33.
	@Test
	public void tc33() {
		Task task = taskBuilder
				.createOneTask("Meet boss at 5pm for fun to around 6pm and then at 7pm.");
		assertEquals(null, task);
	}

		// Fixed a problem when a partial keyword is found between two valid dates.
	@Test
	public void tc34() {
		Task task = taskBuilder.createOneTask("5pm mor 6pm mor 7pm.");
		assertEquals(null, task);
	}

	// Alternate case to tc34.
	@Test
	public void tc35() {
		Task task = taskBuilder.createOneTask("5pm mor even 6pm");
		assertEquals(null, task);
	}

	// Catch mixed date types in repeated dates.
	@Test
	public void tc36() {
		Task task = taskBuilder.createOneTask("5pm 5pm 5pm to 6pm and 7pm");
		assertEquals(null, task);
	}

	// Fix "before on" preposition.
	@Test
	public void tc37() {
		Task task = taskBuilder
				.createOneTask("Complete the task before 5pm 13 Oct.");
		assertEquals("Complete the task before 13 Oct 5PM and today 5PM.", task.getDescriptionEdit());
	}

	// Friday should show up without dates.
	@Test
	public void tc38() {
		Task task = taskBuilder.createOneTask("today");
		assertEquals(null, task);
	}

	// Commas after dates should be parsable.
	@Test
	public void tc39() {
		Task task = taskBuilder.createOneTask("Something today and tomorrow,");
		assertEquals("Something today and tomorrow, ", task.getDescriptionEdit());
	}

	// Fullstops after dates should be parsable.
	@Test
	public void tc40() {
		Task task = taskBuilder
				.createOneTask("Something today and tomorrow. then something else.");
		assertEquals("Something today and tomorrow. then something else.", task.getDescriptionEdit());
	}

	// Overlapping dates in the same task.
	@Test
	public void tc41() {
		Task task = taskBuilder.createOneTask("Something 1pm 2pm 3pm then 1pm");
		assertEquals(null, task);
	}

	// Overlapping dates in the same task.
	@Test
	public void tc42() {
		Task task = taskBuilder.createOneTask("Something today");
		assertEquals(true, task.isAllDay());
	}

	// Be able to take time without commas correctly
	@Test
	public void tc43() {
		Task task = taskBuilder.createOneTask("Something tomorrow 1pm 2pm 3pm");
		assertEquals("Something tomorrow 1PM, 2PM and 3PM",
				task.getDescriptionEdit());
	}

	// Be able to take time without commas correctly
	@Test
	public void tc44() {
		Task task = taskBuilder
				.createOneTask("Something tomorrow 13:00 14:00 15:00");
		assertEquals("Something tomorrow 1PM, 2PM and 3PM",
				task.getDescriptionEdit());
	}

	// Test for deadline
	@Test
	public void tc45() {
		Task task = taskBuilder.createOneTask("Something by today");
		assertEquals(true, task.isDeadline());
		task = taskBuilder.createOneTask("Something today");
		assertEquals(false, task.isDeadline());
	}

	// Test for deadline
	@Test
	public void tc46() {
		Task task = taskBuilder.createOneTask("Something 21/6");
		assertEquals("Sat Jun 21 00:00:01 SGT 2014", task.getDateStart()
				.toString());
	}
}