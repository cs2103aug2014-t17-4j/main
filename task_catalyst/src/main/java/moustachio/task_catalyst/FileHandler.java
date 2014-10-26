package moustachio.task_catalyst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * This program is to manage writing and reading tasks in a specific text file. 
 * 
 * The description of a task is converted into JSON object before saving it in the file. 
 * Likewise, the saved task is converted again to text for the purpose of editing or displaying. 
 * 
 * @author Lin XiuQing (A0112764J)
 */

public class FileHandler {

	private static BlackBox blackBox = BlackBox.getInstance();
	
	public static void writeTask(Task task, String fileName) throws IOException{
		assert(task!=null);
		assert(fileName!=null);
		try{
			writeJSONFile(task, fileName);
		} catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		} 
	}
	
	private static void writeJSONFile(Task task, String fileName) throws IOException {
		FileWriter jsonFile = new FileWriter(fileName, true);
		BufferedWriter writer = new BufferedWriter(jsonFile);
		JSONObject object = new JSONObject();
		JSONConverter objCodec = new JSONConverter();
		object = objCodec.encode(task);
		writer.write(object.toJSONString());
		writer.newLine();
		writer.flush();
		writer.close();
	}
	
	public static List<Task> readTask(String fileName){
		assert(fileName!=null);
		List<Task> list = new ArrayList<Task> ();
		
		if(isEmptyFile(fileName)){
			return new ArrayList<Task>();
		}else{
			readJSONFile(fileName, list);
		}
		return list;
	}

	private static void readJSONFile(String fileName, List<Task> list) {
		try {
			readJSONFormat(fileName, list);
		}catch (FileNotFoundException e){
			blackBox.info("The file is empty.");
		}catch (IOException e){
			blackBox.info("IO fault has been enountered.");
		}catch (ParseException e){
			blackBox.info("Incorrect format has been found");
		}
	}

	private static void readJSONFormat(String fileName, List<Task> list)
			throws FileNotFoundException, IOException, ParseException {
		String stringLine;
		BufferedReader breader = getReader(fileName);
		JSONConverter objCodec = new JSONConverter();

		while ((stringLine = breader.readLine()) != null){
			list.add(objCodec.decodeToString(stringLine));
		}
		breader.close();
	}
	
	public static boolean writeSetting(String name, String fileName, String value) {
		assert(fileName!=null);
		Boolean isSuccess = false;
		try {
			isSuccess = write(name, fileName, value);
		}catch (FileNotFoundException e){
			blackBox.info("The file is not found!");
		}catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		}
		return isSuccess;
	}

	private static Boolean write(String name, String fileName, String value) throws IOException {
		assert(value!=null && name!=null);
		Boolean isSuccess;
		if(name!=null && value!=null && fileName!=null){
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));
			writer.write(name+ "," + value);
			writer.newLine();
			writer.close();
			isSuccess = true;
		}else{
			isSuccess=false;
		}
		return isSuccess;
	} 
	
	public static String readSetting(String name, String fileName){
		assert(fileName!=null);
		assert(name!=null);
		String value = "";
		try{
			BufferedReader reader = getReader(fileName);
			String lineString;
			if(isEmptyFile(fileName)){
				reader.close();
				return "Empty file";
			}
		
			while((lineString=reader.readLine()) !=null){
				if(lineString.contains(name)){
					value += lineString + " ";
				}
			}
			reader.close();
		}catch (IOException e){
			blackBox.info("IO fault has been enountered.");
		}
		return value;
	}
	
	private static BufferedReader getReader(String fileName)
			throws FileNotFoundException {
		FileReader freader = new FileReader(fileName);
		BufferedReader reader = new BufferedReader (freader);
		return reader;
	}

	private static boolean isEmptyFile(String fileName) {
		try {
			BufferedReader reader = getReader(fileName);
			if (reader.readLine() == null) {
				reader.close();
				return true;
			}
			reader.close();
		} catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		}
		return false;
	}
	
	public static void clear(String fileName) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(fileName);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			blackBox.info("The file is not found!");
		}
	}
}