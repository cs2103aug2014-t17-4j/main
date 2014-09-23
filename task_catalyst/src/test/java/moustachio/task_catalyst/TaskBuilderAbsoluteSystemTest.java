package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskBuilderAbsoluteSystemTest {

	TaskBuilder taskBuilder = new TaskBuilderAbsoluteSystem();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Task task = taskBuilder.createTask("Meet boss [8am] to [9pm]");
		assertEquals("Meet boss []", task.getDescription());
	}
}
