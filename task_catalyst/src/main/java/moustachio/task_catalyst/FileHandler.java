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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * This program is to manage writing and reading tasks in a specific text file.
 * 
 * The description of a task is converted into JSON object before saving it in
 * the file. Likewise, the saved task is converted again to text for the purpose
 * of editing or displaying.
 * 
 * @author Lin XiuQing (A0112764J)
 */

public class FileHandler {

	private static BlackBox blackBox = BlackBox.getInstance();

	public void writeTask(Task task, String fileName) throws IOException {
		assert (task != null);

		if (isinvalidFileFormat(fileName)) {
			blackBox.info("Invalid file format!");
			throw new Error("Invalid file format!");
		}

		try {
			writeJSONFile(task, fileName);
		} catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		}
	}

	private void writeJSONFile(Task task, String fileName) throws IOException {
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

	public List<Task> readTask(String fileName) {
		List<Task> list = new ArrayList<Task>();

		if (isinvalidFileFormat(fileName)) {
			blackBox.info("Invalid file format!");
			throw new Error("Invalid file format!");
		}

		if (isEmptyFile(fileName)) {
			return new ArrayList<Task>();
		} else {
			if (isinvalidFileFormat(fileName)) {
				readJSONFile(fileName, list);
			}
		}
		return list;
	}

	private void readJSONFile(String fileName, List<Task> list) {
		try {
			readJSONFormat(fileName, list);
		} catch (FileNotFoundException e) {
			blackBox.info("The file is empty.");
		} catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		} catch (ParseException e) {
			blackBox.info("Incorrect format has been found");
		}
	}

	private void readJSONFormat(String fileName, List<Task> list)
			throws FileNotFoundException, IOException, ParseException {
		String stringLine;
		BufferedReader breader = getReader(fileName);
		JSONConverter objCodec = new JSONConverter();

		while ((stringLine = breader.readLine()) != null) {
			Task task = objCodec.decodeToString(stringLine);
			if (task != null) {
				list.add(task);
			}
		}
		breader.close();
	}

	public boolean writeSetting(String name, String fileName, String value) {
		Boolean isSuccess = false;

		if (isinvalidFileFormat(fileName) || isinvalidFileFormat(name)
				|| isinvalidFileFormat(value)) {
			blackBox.info("Invalid file format!");
			throw new Error("Invalid file format!");
		}

		try {
			isSuccess = write(name, fileName, value);
		} catch (FileNotFoundException e) {
			blackBox.info("The file is not found!");
		} catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		}
		return isSuccess;
	}

	private Boolean write(String name, String fileName, String value)
			throws IOException {
		assert (value != null && name != null);
		Boolean isSuccess;
		if (name != null && value != null && fileName != null) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,
					true));
			writer.write(name + "," + value);
			writer.newLine();
			writer.close();
			isSuccess = true;
		} else {
			isSuccess = false;
		}
		return isSuccess;
	}

	public String readSetting(String name, String fileName) {
		assert (name != null);

		if (isinvalidFileFormat(fileName) || isinvalidFileFormat(name)) {
			blackBox.info("Invalid file format!");
			throw new Error("Invalid file format!");
		}

		String value = "";
		try {
			BufferedReader reader = getReader(fileName);
			String lineString;
			if (isEmptyFile(fileName)) {
				reader.close();
				return "Empty file";
			}

			while ((lineString = reader.readLine()) != null) {
				if (lineString.contains(name)) {
					value += lineString + " ";
				}
			}
			reader.close();
		} catch (IOException e) {
			blackBox.info("IO fault has been enountered.");
		}
		return value;
	}

	private BufferedReader getReader(String fileName)
			throws FileNotFoundException {
		FileReader freader = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(freader);
		return reader;
	}

	private boolean isEmptyFile(String fileName) {
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

	public void clear(String fileName) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(fileName);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			blackBox.info("The file is not found!");
		}
	}

	private static boolean isinvalidFileFormat(String text) {
		if (text.contains(".")) {
			String[] name = text.split("\\.");
			return (!(isValidName(name[0]) && isValidName(name[1])
					&& name[1].equals("txt") && name[1] != null));
		} else {
			return true;
		}
	}

	public static boolean isValidName(String text) {
		Pattern pattern = Pattern.compile("^[^/./\\:*?\"<>|]+$");
		Matcher matcher = pattern.matcher(text);
		boolean isMatch = matcher.matches();
		return isMatch;
	}
}