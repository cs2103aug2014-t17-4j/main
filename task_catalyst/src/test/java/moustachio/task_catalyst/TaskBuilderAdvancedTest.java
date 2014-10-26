package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		assertEquals("Meet boss from 21 Jun to 22 Jun 10AM",
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
		assertFalse(task.isRange());
	}

	// Test Range Recognition - Range
	@Test
	public void tc21() {
		Task task = taskBuilder.createTask("Meet boss from 5PM to 6PM");
		assertTrue(task.isRange());
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
		Task task = taskBuilder.createTask("one thing from 1 oct 2013 5pm to 1 oct 2013 6pm");
		assertEquals("one thing from 1 Oct 2013 5PM to 6PM", task.getDescription());
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
		Task task = taskBuilder.createTask("Meet boss from 5 Oct 1pm to 2pm test");
		assertEquals("Meet boss from 5 Oct 1PM to 2PM test", task.getDescription());
	}
	
	// Handle words like "rated" after a date range
	@Test
	public void tc29() {
		Task task = taskBuilder.createTask("Get movie rated 1pm");
		assertEquals("Get movie rated today 1PM", task.getDescription());
	}
}