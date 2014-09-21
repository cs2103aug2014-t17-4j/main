package moustachio.task_catalyst;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// Null input, should return null.
	@Test
	public void tc1() {
		Task task = TaskBuilder.createTask(null);
		assertEquals(null, task);
	}

	// Empty input, should return null.
	@Test
	public void tc2() {
		Task task = TaskBuilder.createTask("");
		assertEquals(null, task);
	}

	// Whitespace input, should return null.
	@Test
	public void tc3() {
		Task task = TaskBuilder.createTask(" ");
		assertEquals(null, task);
	}

	// Basic add without any date or numbers.
	@Test
	public void tc4() {
		String description = "Meet boss";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals(null, task.getDateStart());
		assertEquals(null, task.getDateEnd());
	}

	// Basic add with one date.
	@Test
	public void tc5() {
		String description = "Meet boss on [21 Sep 6pm]";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals("2014-09-21T18:00", task.getDateStart().toString());
		assertEquals(null, task.getDateEnd());
	}

	// Basic add with explicit date range.
	@Test
	public void tc6() {
		String description = "Meet boss [from 22 Sep 6pm to 23 Sep 8pm]";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals("2014-09-22T18:00", task.getDateStart().toString());
		assertEquals("2014-09-23T20:00", task.getDateEnd().toString());
	}

	// Basic add with explicit date but implicit time.
	@Test
	public void tc7() {
		String description = "Meet boss [from 22 Sep 6pm to 8pm]";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals("2014-09-22T18:00", task.getDateStart().toString());
		assertEquals("2014-09-22T20:00", task.getDateEnd().toString());
	}

	// Basic add with explicit date but implicit time, numbered item behind.
	@Test
	public void tc8() {
		String description = "Meet boss [from 22 Sep 6pm to 8pm] at MR5";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals("2014-09-22T18:00", task.getDateStart().toString());
		assertEquals("2014-09-22T20:00", task.getDateEnd().toString());
	}
	
	// Basic add with explicit date but implicit time, numbered item behind.
	@Test
	public void tc9() {
		String description = "Meet boss at MR5 [from 22 Sep 6pm to 8pm]";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals("2014-09-22T18:00", task.getDateStart().toString());
		assertEquals("2014-09-22T20:00", task.getDateEnd().toString());
	}
	
	// Start with date.
	@Test
	public void tc10() {
		String description = "[25 Sep 6pm to 26 Sep 8pm] clients conference.";
		Task task = TaskBuilder.createTask(description);
		assertEquals(description, task.getDescription());
		assertEquals("2014-09-25T18:00", task.getDateStart().toString());
		assertEquals("2014-09-26T20:00", task.getDateEnd().toString());
	}

}
