//@author A0112584J
package moustachio.task_catalyst;

import static org.junit.Assert.assertEquals;
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
		BlackBox.getInstance().close();
	}

	/* This is a boundary case for the case 'empty list' partition */
	@Test
	public void testSearchByHashtag() {
		assertEquals(listProcessor.searchByHashtag(tasks, "boss").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagAll() {
		assertEquals(listProcessor.searchByHashtag(tasks, "all").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagPriority() {
		assertEquals(listProcessor.searchByHashtag(tasks, "pri").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverdue() {
		assertEquals(listProcessor.searchByHashtag(tasks, "ovd").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagToday() {
		assertEquals(listProcessor.searchByHashtag(tasks, "tdy").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagTomorrow() {
		assertEquals(listProcessor.searchByHashtag(tasks, "tmr").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagUpcoming() {
		assertEquals(listProcessor.searchByHashtag(tasks, "upc").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagSomeday() {
		assertEquals(listProcessor.searchByHashtag(tasks, "smd").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap() {
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagDone() {
		assertEquals(listProcessor.searchByHashtag(tasks, "dne").size(), 0);
	}
	
	@Test
	public void testSearchByKeyword() {
		assertEquals(listProcessor.searchByKeyword(tasks, "boss").size(), 0);
	}
	
	@Test
	public void testSortByDate() {
		assertEquals(listProcessor.sortByDate(tasks).size(), 0);
	}
	
	/* This is a boundary case for the case 'list with at least one task' partition */
	@Test
	public void testSearchByHashtag2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "boss").get(0),
				tasks.get(0));
		assertEquals(listProcessor.searchByHashtag(tasks, "BOSS").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByHashtag3() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "girlfriend").size(), 0);
	}

	@Test
	public void testSearchByHashtag4() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #girlfriend at 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "boss").get(0),
				tasks.get(0));
		assertEquals(listProcessor.searchByHashtag(tasks, "girlfriend").get(0),
				tasks.get(1));
	}

	@Test
	public void testSearchByHashtag5() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife at 6pm"));
		tasks.addAll(taskBuilder
				.createTask("ask wife for a movie at 8pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "wife").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "wife").get(0),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByHashtagPriority2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet wife at 6pm #pri"));
		assertEquals(listProcessor.searchByHashtag(tasks, "pri").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "pri").get(0),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByHashtagOverdue2() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("call wife on 20 oct at 12am"));
		assertEquals(listProcessor.searchByHashtag(tasks, "ovd").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "ovd").get(0),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByHashtagAll2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife at 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "all").size(), 2);
		assertEquals(listProcessor.searchByHashtag(tasks, "all").get(0),
				tasks.get(0));
		assertEquals(listProcessor.searchByHashtag(tasks, "all").get(1),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByHashtagToday2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "tdy").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "tdy").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByHashtagTomorrow2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "tmr").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "tmr").get(0),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByHashtagUpcoming2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 6pm"));
		tasks.addAll(taskBuilder.createTask("meet James on 31 dec 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "upc").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "upc").get(0),
				tasks.get(2));
	}
	
	@Test
	public void testSearchByHashtagSomeday2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("buy a fan"));
		tasks.addAll(taskBuilder.createTask("fix the air con in my room"));
		assertEquals(listProcessor.searchByHashtag(tasks, "smd").size(), 2);
		assertEquals(listProcessor.searchByHashtag(tasks, "smd").get(0),
				tasks.get(1));
		assertEquals(listProcessor.searchByHashtag(tasks, "smd").get(1),
				tasks.get(2));
	}
	
	@Test
	public void testSearchByHashtagDone2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife at 6pm"));
		tasks.get(0).setDone(true);
		assertEquals(listProcessor.searchByHashtag(tasks, "dne").size(), 1);
		assertEquals(listProcessor.searchByHashtag(tasks, "dne").get(0),
				tasks.get(0));
	}
	
	/* This is a boundary case for the overlap method 'no tasks are overlapping' partition */
	@Test
	public void testSearchByHashtagOverlap2() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap3() {
		tasks.addAll(taskBuilder.createTask("meet #boss by 5pm tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap4() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 6pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 5pm to 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap5() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap6() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 5pm"));
		tasks.get(0).setDone(true);
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap7() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow from 5pm to 6pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 6pm to 7pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap8() {
		tasks.addAll(taskBuilder.createTask("meet #boss yesterday at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife yesterday at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	@Test
	public void testSearchByHashtagOverlap9() {
		tasks.addAll(taskBuilder.createTask("fix aircon at my room"));
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 0);
	}
	
	/* This is a boundary case for the overlap method 'one task is overlapping with at least one other task' partition */
	@Test
	public void testSearchByHashtagOverlap10() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}

	@Test
	public void testSearchByHashtagOverlap11() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap12() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 5pm to 8pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow at 5pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap13() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 5pm to 8pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow at 6pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap14() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 5pm to 8pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow from 5pm to 8pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap15() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 5pm to 7pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow from 5pm to 8pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap16() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 6pm to 7pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow from 5pm to 8pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap17() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 6pm to 8pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow from 5pm to 8pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}
	
	@Test
	public void testSearchByHashtagOverlap18() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow"));
		tasks.addAll(taskBuilder.createTask("meet #wife tomorrow from 3pm to 6pm"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow from 5pm to 8pm"));
		assertEquals(listProcessor.searchByHashtag(tasks, "olp").size(), 2);
	}

	@Test
	public void testSearchByKeyword2() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "wife").size(), 0);
	}
	
	@Test
	public void testSearchByKeyword3() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "boss").get(0),
				tasks.get(0));
	}

	@Test
	public void testSearchByKeyword4() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 6pm today"));
		assertEquals(listProcessor.searchByKeyword(tasks, "boss").get(0), 
				tasks.get(0));
	}

	@Test
	public void testSearchByKeyword5() {
		tasks.addAll(taskBuilder.createTask("meet #boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("meet #wife at 5pm"));
		tasks.addAll(taskBuilder
				.createTask("ask wife for a movie"));
		assertEquals(listProcessor.searchByKeyword(tasks, "wife").size(), 2);
		assertEquals(listProcessor.searchByKeyword(tasks, "WIFE").get(0),
				tasks.get(1));
		assertEquals(listProcessor.searchByKeyword(tasks, "wife").get(1),
				tasks.get(2));
	}
	
	@Test
	public void testSearchByKeyword6() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("meeting with stakeholders tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "mee").size(), 2);
		assertEquals(listProcessor.searchByKeyword(tasks, "mee").get(0),
				tasks.get(0));
		assertEquals(listProcessor.searchByKeyword(tasks, "mee").get(1),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByKeyword7() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "mee boss").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "mee boss").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword8() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "meet boss").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "meet boss").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword9() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "boss meet").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "boss meet").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword10() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "meet      boss").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "meet      boss").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword11() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "boss   meet").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "boss   meet").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword12() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "meet bpss").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "meet bpss").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword13() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "mee bpss").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "mee bpss").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword14() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "out wife").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "out wife").get(0),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByKeyword15() {
		tasks.addAll(taskBuilder.createTask("meet boss at 5pm"));
		tasks.addAll(taskBuilder.createTask("go out with wife tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "wife out").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "wife out").get(0),
				tasks.get(1));
	}
	
	@Test
	public void testSearchByKeyword16() {
		tasks.addAll(taskBuilder.createTask("meet boss today at 5pm"));
		tasks.addAll(taskBuilder.createTask("meeting with stakeholders tomorrow at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "today").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "today").get(0),
				tasks.get(0));
	}
	
	@Test
	public void testSearchByKeyword17() {
		tasks.addAll(taskBuilder.createTask("meet boss today at 5pm"));
		tasks.addAll(taskBuilder.createTask("meeting with stakeholders tomorrow at 6pm"));
		tasks.addAll(taskBuilder.createTask("meet wife on 31 dec at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "today, tomorrow, 31 dec").size(), 3);
		assertEquals(listProcessor.searchByKeyword(tasks, "today, tomorrow, and 31 dec").size(), 3);
		assertEquals(listProcessor.searchByKeyword(tasks, "today, tomorrow, 31 dec").get(0),
				tasks.get(0));
		assertEquals(listProcessor.searchByKeyword(tasks, "today, tomorrow, 31 dec").get(1),
				tasks.get(1));
		assertEquals(listProcessor.searchByKeyword(tasks, "today, tomorrow, 31 dec").get(2),
				tasks.get(2));
	}
	
	@Test
	public void testSearchByKeyword18() {
		tasks.addAll(taskBuilder.createTask("meet boss today at 5pm"));
		tasks.addAll(taskBuilder.createTask("meeting with stakeholders tomorrow at 6pm"));
		tasks.addAll(taskBuilder.createTask("meet wife on 31 dec at 6pm"));
		tasks.addAll(taskBuilder.createTask("watch movie from tomorrow 8pm to 10pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "tomorrow to 31 dec").size(), 3);
		assertEquals(listProcessor.searchByKeyword(tasks, "between tomorrow and 31 dec").size(), 3);
		assertEquals(listProcessor.searchByKeyword(tasks, "tomorrow to 31 dec").get(0),
				tasks.get(1));
		assertEquals(listProcessor.searchByKeyword(tasks, "tomorrow to 31 dec").get(1),
				tasks.get(3));
		assertEquals(listProcessor.searchByKeyword(tasks, "tomorrow to 31 dec").get(2),
				tasks.get(2));
	}
	
	@Test
	public void testSearchByKeyword19() {
		tasks.addAll(taskBuilder.createTask("meet boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("meeting with stakeholders [tomorrow] today at 6pm"));
		assertEquals(listProcessor.searchByKeyword(tasks, "[tomorrow]").size(), 1);
		assertEquals(listProcessor.searchByKeyword(tasks, "[tomorrow]").get(0),
				tasks.get(1));
	}
	
	/* This is a boundary case for the case 'list with only one task' partition */
	@Test
	public void testSortByDate2() {
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		assertEquals(listProcessor.sortByDate(tasks).size(), 1);
	}
	
	/* This is a boundary case for the case 'list with at least two tasks' partition */
	@Test
	public void testSortByDate3() {
		tasks.addAll(taskBuilder.createTask("meet wife from 6pm to 8pm"));
		tasks.addAll(taskBuilder.createTask("meet #boss at 5pm"));
		assertEquals(listProcessor.sortByDate(tasks).get(0), tasks.get(1));
		assertEquals(listProcessor.sortByDate(tasks).get(1), tasks.get(0));
	}
	
	@Test
	public void testSortByDate4() {
		tasks.addAll(taskBuilder.createTask("meet wife at 5pm"));
		tasks.addAll(taskBuilder.createTask("ask wife for a movie tomorrow at 11pm"));
		tasks.addAll(taskBuilder.createTask("meet boss tomorrow at 5pm"));
		tasks.addAll(taskBuilder.createTask("fix aircon at my room"));
		tasks.addAll(taskBuilder.createTask("meet James tomorrow for gathering"));
		tasks.addAll(taskBuilder.createTask("hackathon event from tomorrow 5pm to 7pm"));
		tasks.addAll(taskBuilder.createTask("family dinner tomorrow at 8pm"));
		assertEquals(listProcessor.sortByDate(tasks).get(0), tasks.get(0));
		assertEquals(listProcessor.sortByDate(tasks).get(1), tasks.get(4));
		assertEquals(listProcessor.sortByDate(tasks).get(2), tasks.get(2));
		assertEquals(listProcessor.sortByDate(tasks).get(3), tasks.get(5));
		assertEquals(listProcessor.sortByDate(tasks).get(4), tasks.get(6));
		assertEquals(listProcessor.sortByDate(tasks).get(5), tasks.get(1));
		assertEquals(listProcessor.sortByDate(tasks).get(6), tasks.get(3));
	}

}
