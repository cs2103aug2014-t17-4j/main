package moustachio.task_catalyst;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author A0112764J
 */

public class StorageTest {
	
	Storage data = new StorageActual();
	
	String fileName;
	Task task1 = new TaskAdvanced("meet boss at 5pm tmr");
	Task task2 = new TaskAdvanced("go school tmr 9am");
	
	String font1 = "font";
	String size1 = "10";
	String font2 = "font";
	String size2 = "20";
	
	@Before
	public void setUp() throws Exception {
		fileName = "text.txt";
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/*** Load task ***/
	
	/* This is a boundary case for 'load empty File' partition */
	@Test
	public void testLoadEmptyFile(){
		List<Task> list = new ArrayList<Task> ();
		FileHandler.clear(fileName);
		list = data.loadTasks(fileName);
		assertEquals(list.isEmpty(),true);
	}
	
	/* This is a boundary case for 'load one task to file' partition */
	@Test
	public void testLoadOneTask() {
		List<Task> list = new ArrayList<Task> ();
		list.add(task1);
		data.saveTasks(list, fileName);
		
		List<Task> elist = new ArrayList<Task> ();
		elist = data.loadTasks(fileName);
		assertEquals(list.get(0).getDescription().equals(elist.get(0).getDescription()), true);
	}
	
	/* This is a boundary case for 'load two tasks to file' partition */
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
	
	/* This is a boundary case for 'Save task to Empty File' partition */
	@Test
	public void testSaveTaskstoEmptyFile() {
		List<Task> list = new ArrayList<Task> ();
		list.add(task1);
		FileHandler.clear(fileName);
		assertEquals(data.saveTasks(list, fileName), true);
	}
	
	/* This is a boundary case for 'testOverwrite saving' partition */
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
	
	
	/*** Save Setting ***/
	
	/* This is a boundary case for 'save single setting' partition */
	@Test
	public void testSaveOneSetting(){
		FileHandler.clear(fileName);
		assertEquals(true, data.saveSetting(font1, fileName, size1));
	}
	
	/* This is a boundary case for 'save null setting' partition */
	@Test 
	public void testSaveEmptySetting(){
		FileHandler.clear(fileName);
		assertEquals(false,data.saveSetting(null, fileName, null));
	}
	
	
	/*** Load Setting ***/
	
	/* This is a boundary case for 'load setting from empty file' partition */
	@Test 
	public void testLoadSettingEmptyFile(){
		FileHandler.clear(fileName);
		String str = "font";
		String expectedString = data.loadSetting(str, fileName);
		assertEquals("Empty file", expectedString);
	}

	/* This is a boundary case for 'load one setting' partition */
	@Test 
	public void testLoadSetting(){
		FileHandler.clear(fileName);
		data.saveSetting(font1, fileName, size1);
		String input = font1 + "," + size1 + " ";
		//System.out.println(input);
		//System.out.println(data.loadSetting(input, fileName));
		assertEquals(input, data.loadSetting(font1, fileName));
	}
	
	/* This is a boundary case for 'load two settings' partition */
	@Test 
	public void testLoadSettings(){
		FileHandler.clear(fileName);
		data.saveSetting(font1, fileName, size1);
		data.saveSetting(font2, fileName, size2);
		String input1 = font1 + "," + size1 + " ";
		String input2 = font2 + "," + size2 + " ";
		System.out.println(data.loadSetting(font1, fileName));
		System.out.println(data.loadSetting(font2, fileName));
		assertEquals((data.loadSetting(font1, fileName).contains(input1)), true);
		assertEquals((data.loadSetting(font2,fileName).contains(input2)), true);
	}
}
