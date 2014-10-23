package moustachio.task_catalyst;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageTest {
	
	Storage data = new StorageStub();
	
	String fileName;
	Task task1 = new TaskAdvanced("meet boss at 5pm tmr");
	Task task2 = new TaskAdvanced("go school tmr 9am");
	
	@Before
	public void setUp() throws Exception {
		fileName = "tasks.txt";
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/*** Load task ***/
	@Test
	public void testLoadEmptyFile(){
		List<Task> list = new ArrayList<Task> ();
		FileHandler.clear(fileName);
		list = data.loadTasks(fileName);
		assertEquals(list.isEmpty(),true);
	}
	
	@Test
	public void testLoadOneTask() {
		List<Task> list = new ArrayList<Task> ();
		list.add(task1);
		data.saveTasks(list, fileName);
		
		List<Task> elist = new ArrayList<Task> ();
		elist = data.loadTasks(fileName);
		//System.out.println(list.get(0));
		//System.out.println(task1.getDescription());
		assertEquals(list.get(0).getDescription().equals(elist.get(0).getDescription()), true);
	}
	
	@Test
	public void testLoadTasks(){
		List<Task> list = new ArrayList<Task> ();
		list.add(task1);
		data.saveTasks(list, fileName);
		list.add(task2);
		data.saveTasks(list, fileName);
		
		List<Task> elist = new ArrayList<Task> ();
		elist = data.loadTasks(fileName);
		assertTrue(list.get(0).getDescription().equals(elist.get(0).getDescription()) && 
				list.get(1).getDescription().equals(elist.get(1).getDescription()));
	}
	
	/*** Save task ***/ 
	@Test
	public void testSaveTaskstoEmptyFile() {
		List<Task> list = new ArrayList<Task> ();
		list.add(task1);
		FileHandler.clear(fileName);
		assertEquals(data.saveTasks(list, fileName), true);
	}
	
	@Test
	public void testOverwriteSave(){
		List<Task> list = new ArrayList<Task> ();
		list.add(task1);
		data.saveTasks(list, fileName);
		list.add(task2);
		data.saveTasks(list, fileName);
		List<Task> elist = new ArrayList<Task> ();
		elist = data.loadTasks(fileName);
		assertEquals(data.saveTasks(list, fileName),true);
		assertEquals(list.get(0).getDescription().equals(elist.get(0).getDescription()), true);
	}


	@Test
	public void testSaveSetting() {
		//fail("Not yet implemented");
	}

	@Test
	public void testLoadSetting() {
		//fail("Not yet implemented");
	}

}
