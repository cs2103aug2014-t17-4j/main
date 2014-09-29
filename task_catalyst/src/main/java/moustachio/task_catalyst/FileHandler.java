package moustachio.task_catalyst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class FileHandler {
	private static String fileName;

	public static void writeTask(Task task, String fileName) throws IOException{
		try{
			writeJSONFile(task, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private static void writeJSONFile(Task task, String fileName) throws IOException {
		FileWriter JSONFile = new FileWriter(fileName, true);
		BufferedWriter writer = new BufferedWriter(JSONFile);
		JSONObject object = new JSONObject();
		JSONConverter objCodec = new JSONConverter();
		object = objCodec.encode(task);
		writer.write(object.toJSONString());
		writer.newLine();
		writer.flush();
		writer.close();
	}
	
	public static List<Task> readTask(String fileName){
		List<Task> list = new ArrayList<Task> ();
		
		if(isEmptyFile(fileName)){
			return new ArrayList<Task>();
		}
		else{
			readJSONFile(fileName, list);
		}
		return list;
	}

	private static void readJSONFile(String fileName, List<Task> list) {
		try {
			readJSONFormat(fileName, list);
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}catch (ParseException e){
			e.printStackTrace();
		}
	}

	private static void readJSONFormat(String fileName, List<Task> list)
			throws FileNotFoundException, IOException, ParseException {
		String lineString;
		BufferedReader breader = getReader(fileName);
		JSONConverter ObjCodec = new JSONConverter();

		while ((lineString = breader.readLine()) != null){
			System.out.println(lineString);
			list.add(ObjCodec.decodeToStr(lineString));
		}
		breader.close();
	}
	
	public static boolean writeSetting(String name, String fileName, String value) {
		Boolean isSuccess = false;
		try {
			isSuccess = write(name, fileName, value);
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	private static Boolean write(String name, String fileName, String value) throws IOException {
		Boolean isSuccess;
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));
		writer.write(name+ "," + value);
		writer.newLine();
		writer.close();
		isSuccess = true;
		return isSuccess;
	} 
	
	public static String readSetting(String name, String fileName){
		String value="";
		try{
			BufferedReader reader = getReader(fileName);
			String lineString;
			if(isEmptyFile(fileName)){
				reader.close();
				return "Empty file";
			}
			while(((lineString=reader.readLine()) !=null) && lineString.contains(name)){
				value += lineString + "\n";
			}
			reader.close();
		}catch (IOException e){
			return "Error";
		}
		return value;
	}

	private static BufferedReader getReader (String fileName) throws FileNotFoundException {
		FileReader reader = new FileReader(fileName);
		BufferedReader breader = new BufferedReader(reader);
		return breader;
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
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		FileHandler.fileName = fileName;
	}	
}