package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskBuilderAdvancedTest {

	TaskBuilder taskBuilder = new TaskBuilderAdvanced();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// Test for basic date recognition.
	@Test
	public void tc1() {
		Task task = taskBuilder.createTask("Meet boss 21 Jun 10:05am");
		assertEquals("Meet boss on 21 Jun 10:05AM", task.getDescriptionEdit());
	}

	// Test for basic date range recognition.
	@Test
	public void tc2() {
		Task task = taskBuilder
				.createTask("Meet boss from 21 jun 10am to 22nd jun");
		assertEquals("Meet boss from 21 Jun 10AM to 22 Jun 10AM",
				task.getDescriptionEdit());
	}

	// Test for basic time range recognition.
	@Test
	public void tc3() {
		Task task = taskBuilder
				.createTask("Meet boss from 21 jun 10am to 11am");
		assertEquals("Meet boss from 21 Jun 10AM to 11AM",
				task.getDescriptionEdit());
	}

	// Test for relative display.
	@Test
	public void tc4() {
		Task task = taskBuilder.createTask("Meet boss tomorrow 10am");
		assertEquals("Meet boss tomorrow 10AM", task.getDescriptionEdit());
	}

	// Test for relative display with day range.
	@Test
	public void tc5() {
		Task task = taskBuilder
				.createTask("Meet boss today 10am to today 12pm");
		assertEquals("Meet boss today 10AM to 12PM", task.getDescriptionEdit());
	}

	// Test for relative display with day range.
	@Test
	public void tc6() {
		Task task = taskBuilder
				.createTask("Meet boss 21 Jun 10am to 25 sep 2015 12pm");
		assertEquals("Meet boss on 21 Jun 10AM to 25 Sep 2015 12PM",
				task.getDescriptionEdit());
	}

	// Ignore text+number
	@Test
	public void tc7() {
		Task task = taskBuilder.createTask("Meet boss today 10am at LT5");
		assertEquals("Meet boss today 10AM at [LT5]", task.getDescriptionEdit());
	}

	// Ignore text/symbol+number
	@Test
	public void tc8() {
		Task task = taskBuilder.createTask("Meet boss today 10am at MR-23");
		assertEquals("Meet boss today 10AM at [MR-23]",
				task.getDescriptionEdit());
	}

	// Ignore numbers of 5 or more digits
	@Test
	public void tc9() {
		Task task = taskBuilder.createTask("Call John later 5pm at 90044174");
		assertEquals("Call John later today 5PM at [90044174]",
				task.getDescriptionEdit());
	}

	// Swap inverted range
	@Test
	public void tc10() {
		Task task = taskBuilder.createTask("Meeting today 10pm to 1pm");
		assertEquals("Meeting today 1PM to 10PM", task.getDescriptionEdit());
	}

	// Swap inverted range different date
	@Test
	public void tc11() {
		Task task = taskBuilder
				.createTask("Meeting tomorrow 10pm to today 1pm");
		assertEquals("Meeting today 1PM to tomorrow 10PM",
				task.getDescriptionEdit());
	}

	// Swap inverted multiple
	@Test
	public void tc12() {
		Task task = taskBuilder
				.createTask("Visitors arriving 10pm and 1pm and 2pm");
		assertEquals("Visitors arriving today 1PM, 2PM and 10PM",
				task.getDescriptionEdit());
	}

	// Swap inverted multiple different date
	@Test
	public void tc13() {
		Task task = taskBuilder
				.createTask("Visitors arriving today 10pm and tomorrow 1pm and tomorrow 2pm");
		assertEquals("Visitors arriving today 10PM, tomorrow 1PM and 2PM",
				task.getDescriptionEdit());
	}

	// Grammar correction at-to-on (my fail on a Friday)
	@Test
	public void tc14() {
		Task task = taskBuilder.createTask("Meet boss at 21 oct 2003 1pm");
		assertEquals("Meet boss on 21 Oct 2003 1PM", task.getDescriptionEdit());
	}

	// Ignore partial matches.
	@Test
	public void tc15() {
		Task task = taskBuilder
				.createTask("Meet boss now 5pm knowing that I'm screwed");
		assertEquals("Meet boss today 5PM knowing that I'm screwed",
				task.getDescriptionEdit());
	}

	// Ignore partial matches.
	@Test
	public void tc16() {
		Task task = taskBuilder.createTask("LT5 [sep] programme on 5 sep 10am");
		assertEquals("[LT5] [sep] programme on 5 Sep 10AM",
				task.getDescriptionEdit());
	}

	// Ignore partial matches.
	@Test
	public void tc17() {
		Task task = taskBuilder.createTask("Meet boss today 5pm to 6pm at LT5");
		assertEquals("Meet boss today 5PM to 6PM at [LT5]",
				task.getDescriptionEdit());
	}

	// Automatically match "wholeWord" at the correct range.
	@Test
	public void tc18() {
		Task task = taskBuilder.createTask("At at at at at at at today 5pm");
		assertEquals("At today 5PM", task.getDescriptionEdit());
	}

	// Able to handle "at" and "from" between date/time specifications
	@Test
	public void tc19() {
		try {
			taskBuilder.createTask("Today at 5pm and tomorrow from 6pm to 7pm");
		} catch (UnsupportedOperationException e) {
		}
	}

	// Test Range Recognition - No Range
	@Test
	public void tc20() {
		Task task = taskBuilder.createTask("Meet boss at 5PM");
		assertEquals(false, task.isRange());
	}

	// Test Range Recognition - Range
	@Test
	public void tc21() {
		Task task = taskBuilder.createTask("Meet boss from 5PM to 6PM");
		assertEquals(true, task.isRange());
	}

	// Repeated Date Correction
	@Test
	public void tc22() {
		Task task = taskBuilder.createTask("Meet boss from 5PM to 5PM");
		assertEquals("Meet boss from today 5PM", task.getDescription());
	}

	// Displaying dates when previous day == current day, and next time ==
	// current time.
	@Test
	public void tc23() {
		Task task = taskBuilder
				.createTask("Meet boss today 1pm, 5pm and tomorrow");
		assertEquals("Meet boss today 1PM, 5PM and tomorrow 5PM",
				task.getDescription());
	}

	// Repeated Date alternate.
	@Test
	public void tc24() {
		Task task = taskBuilder.createTask("Meet boss 5PM and today.");
		assertEquals("Meet boss today 5PM.", task.getDescription());
	}

	// Ignore parsing of number words
	@Test
	public void tc25() {
		Task task = taskBuilder.createTask("Twenty four");
		assertEquals("Twenty four", task.getDescription());
	}

	// Ignore parsing of number words
	@Test
	public void tc26() {
		Task task = taskBuilder
				.createTask("one thing from 1 oct 2013 5pm to 1 oct 2013 6pm");
		assertEquals("one thing from 1 Oct 2013 5PM to 6PM",
				task.getDescription());
	}

	// Handle words like "a*", "h*", after a date range
	@Test
	public void tc27() {
		Task task = taskBuilder.createTask("Meet boss from 5 Oct 1pm to 2pm a");
		assertEquals("Meet boss from 5 Oct 1PM to 2PM a", task.getDescription());
	}

	// Handle words like "test" after a date range
	@Test
	public void tc28() {
		Task task = taskBuilder
				.createTask("Meet boss from 5 Oct 1pm to 2pm test");
		assertEquals("Meet boss from 5 Oct 1PM to 2PM test",
				task.getDescription());
	}

	// Handle words containing "ated" after a date range
	@Test
	public void tc29() {
		Task task = taskBuilder.createTask("Get movie rated 1pm");
		assertEquals("Get movie rated today 1PM", task.getDescription());
	}

	// Display Comma Separated Items Properly (artifact problem from multi-pass
	// parsing)
	@Test
	public void tc30() {
		Task task = taskBuilder.createTask("Meet boss 5pm 6pm 7pm.");
		assertEquals("Meet boss today 5PM, 6PM and 7PM.", task.getDescription());
	}

	// Catch mixed date types in this situation.
	@Test
	public void tc31() {
		Task task = taskBuilder.createTask("Meet boss 5pm 6pm to 7pm.");
		assertEquals(null, task);
	}

	// Catch mixed date types, in different many characters away.
	@Test
	public void tc32() {
		Task task = taskBuilder
				.createTask("Meet boss 5pm and 6pm and then meet secretary from 5pm to 7pm.");
		assertEquals(null, task);
	}

	// Detect range "to" keyword with other intermediate words.
	@Test
	public void tc33() {
		Task task = taskBuilder
				.createTask("Meet boss at 5pm for fun to around 6pm.");
		assertEquals(true, task.isRange());
	}

	// Catch invalid date range in the case of tc33.
	@Test
	public void tc34() {
		Task task = taskBuilder
				.createTask("Meet boss at 5pm for fun to around 6pm and then at 7pm.");
		assertEquals(null, task);
	}

	// Do not consider a task ranged if the "to" is more than 2 words away from
	// the time.
	// For example, 5PM to do project and 6PM.
	@Test
	public void tc35() {
		Task task = taskBuilder
				.createTask("Meet boss at 5pm to do project and 6pm for fun and then 7pm.");
		assertEquals(false, task.isRange());
		assertEquals(
				"Meet boss today 5PM to do project and 6PM for fun and then 7PM.",
				task.getDescription());
	}

	// Consider a task ranged if the "to" is less than 2 words away from the
	// time.
	@Test
	public void tc36() {
		Task task = taskBuilder
				.createTask("Meet boss at 5pm to do project to around 6pm.");
		assertEquals(true, task.isRange());
		assertEquals("Meet boss today 5PM to do project to around 6PM.",
				task.getDescription());
	}

	// Fixed a problem when a partial keyword is found between two valid dates.
	@Test
	public void tc37() {
		Task task = taskBuilder.createTask("5pm mor 6pm mor 7pm.");
		assertEquals("today 5PM mor 6PM mor 7PM.", task.getDescription());
	}

	// Alternate case to tc37.
	@Test
	public void tc38() {
		Task task = taskBuilder.createTask("5pm mor even 6pm");
		assertEquals("today 5PM mor even 6PM", task.getDescription());
	}

	// Catch mixed date types in repeated dates.
	@Test
	public void tc39() {
		Task task = taskBuilder.createTask("5pm 5pm 5pm to 6pm and 7pm");
		assertEquals(null, task);
	}

	// Fix "before on" preposition.
	@Test
	public void tc40() {
		Task task = taskBuilder
				.createTask("Complete the task before 5pm 13 Oct.");
		assertEquals("Complete the task before 13 Oct 5PM.",
				task.getDescription());
	}

	// Friday should show up without dates.
	@Test
	public void tc41() {
		Task task = taskBuilder.createTask("today");
		assertEquals("today", task.getDescription());
	}

	// Commas after dates should be parsable.
	@Test
	public void tc42() {
		Task task = taskBuilder.createTask("Something mon and tue,");
		assertEquals("Something on Mon and Tue,", task.getDescription());
	}

	// Fullstops after dates should be parsable.
	@Test
	public void tc43() {
		Task task = taskBuilder
				.createTask("Something mon and tue. then something else.");
		assertEquals("Something on Mon and Tue. then something else.",
				task.getDescription());
	}

	// Overlapping dates in the same task.
	@Test
	public void tc44() {
		Task task = taskBuilder.createTask("Something 1pm 2pm 3pm then 1pm");
		assertEquals(null, task);
	}
}