package moustachio.task_catalyst;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListProcessorActualTest {

	List<Task> tasks;
	TaskBuilder taskBuilder;
	ListProcessorActual listProcessor;
	@Before
	public void setUp() throws Exception {
		tasks = new ArrayList<Task>();
		taskBuilder = new TaskBuilderAdvanced();
		listProcessor = new ListProcessorActual();
	}

	@After
	public void tearDown() throws Exception {
	}

	/*This is a boundary case for the case 'empty list' partition*/
	@Test
	public void testSearchByHashtag() {
		assertEquals(listProcessor.searchByHashtag(tasks, "boss").size(), 0);
	}
	
	/*This is a boundary case for the case 'list with task' partition*/
	@Test
	public void testSearchByHashtag2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "boss").get(0), tasks.get(0));
	}
	
	@Test
	public void testSearchByHashtag3() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #KaiYao at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "boss").get(0), tasks.get(0));
	}
	
	@Test
	public void testSearchByHashtag4() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #KaiYao at 5pm"));
		tasks.addAll(taskBuilder.createTask("ask KaiYao CS2103 project questions"));
		assertEquals(listProcessor.searchByHashtag(tasks, "KaiYao").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "KaiYao").get(0), tasks.get(1));
	}

	@Test
	public void testSearchByKeyword() {
		assertEquals(listProcessor.searchByKeyword(tasks, "boss").size(), 0);
	}
	
	@Test
	public void testSearchByKeyword2() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "boss").get(0), tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword3() {
		tasks.addAll(taskBuilder.createTask("meet boss at 6pm today"));
		assertEquals(listProcessor.searchByKeyword(tasks, "5pm").size(), 0);
	}
	
	@Test
	public void testSearchByKeyword4() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #KaiYao at 5pm"));
		tasks.addAll(taskBuilder.createTask("ask KaiYao CS2103 project questions"));
		assertEquals(listProcessor.searchByKeyword(tasks, "KaiYao").size(), 2);
		assertEquals(listProcessor.searchByKeyword(tasks, "KaiYao").get(0), tasks.get(1));
		assertEquals(listProcessor.searchByKeyword(tasks, "KaiYao").get(1), tasks.get(2));
	}

	@Test
	public void testSortByDate() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #KaiYao at 5pm"));
		tasks.addAll(taskBuilder.createTask("ask KaiYao CS2103 project questions"));
		assertEquals(listProcessor.sortByDate(tasks).get(0), tasks.get(0));
		assertEquals(listProcessor.sortByDate(tasks).get(1), tasks.get(1));
		assertEquals(listProcessor.sortByDate(tasks).get(2), tasks.get(2));
	}

}
