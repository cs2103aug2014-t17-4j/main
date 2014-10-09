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
		assertEquals("Meet boss today 10AM at [MR-23]", task.getDescriptionEdit());
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
		Task task = taskBuilder.createTask("Meet boss at sat 1pm");
		assertEquals("Meet boss on Sat 1PM", task.getDescriptionEdit());
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
		assertEquals("At at at at at at today 5PM",
				task.getDescriptionEdit());
	}
	
	// Able to handle "at" and "from" between date/time specifications
	@Test
	public void tc19() {
		Task task = taskBuilder.createTask("Today at 5pm and tomorrow from 6pm to 7pm");
		assertEquals("today 5PM, tomorrow 6PM to 7PM",
				task.getDescriptionEdit());
	}
	
	@Test
	public void tc20() {
	    System.out.println("presentation today".replaceAll("(^|\\b)today(\\b|$)(?=[^\\]]*(\\[|$))","{09 Oct 2014 03:38 PM}"));
	}
}