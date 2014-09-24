package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

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
		Task task = taskBuilder.createTask("Meet boss [21 Jun 10:05am]");
		assertEquals("Meet boss [21 Jun 10:05AM]", task.getDescription());
	}

	// Test for basic date range recognition.
	@Test
	public void tc2() {
		Task task = taskBuilder
				.createTask("Meet boss [21 jun 10am to 22nd jun]");
		assertEquals("Meet boss [21 Jun to 22 Jun 10AM]", task.getDescription());
	}

	// Test for basic time range recognition.
	@Test
	public void tc3() {
		Task task = taskBuilder.createTask("Meet boss [21 jun 10am to 11am]");
		assertEquals("Meet boss [21 Jun 10AM to 11AM]", task.getDescription());
	}

	// Test for relative display.
	@Test
	public void tc4() {
		Task task = taskBuilder.createTask("Meet boss [tomorrow 10am]");
		assertEquals("Meet boss [tomorrow 10AM]", task.getDescription());
	}

	// Test for relative display with day range.
	@Test
	public void tc5() {
		Task task = taskBuilder
				.createTask("Meet boss [today 10am to today 12pm]");
		assertEquals("Meet boss [today 10AM to 12PM]", task.getDescription());
	}

	// Test for relative display with day range.
	@Test
	public void tc6() {
		Task task = taskBuilder
				.createTask("Meet boss [today 10am to 25 sep 12pm]");
		assertEquals("Meet boss [today 10AM to 25 Sep 12PM]",
				task.getDescription());
	}

	@Test
	public void tc7() {
		String interpreted = TaskBuilderAdvanced.interpretedString("Get boss [5 amperes] today 10am to later 6pm");
		String pretty = TaskBuilderAdvanced.prettyString(interpreted);
		System.out.println(interpreted);
		System.out.println(pretty);
	}
}