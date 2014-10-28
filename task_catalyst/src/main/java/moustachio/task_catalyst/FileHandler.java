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

	private static final String STRING_SPACE = " ";
	private static final String STRING_COMMA = ",";
	private static final String STRING_EMPTY = "";
	private static final String REGEX = "\\.";
	private static final String PATTERN_DOT = ".";
	private static final String EXTENSION = "txt";
	private static final String INVALID_PATTERN = "^[^/./\\:*?\"<>|]+$";
	
	private static final String MESSAGE_NOT_FOUND = "The file is not found!";
	private static final String MESSAGE_INCORRECT_FORMAT = "Incorrect format has been found";
	private static final String MESSAGE_EMPTY_FILE = "The file is empty.";
	private static final String MESSAGE_IO_FAULT = "IO fault has been enountered.";
	private static final String MESSAGE_INVALID_FILE_FORMAT = "Invalid file format!";
	private static BlackBox blackBox = BlackBox.getInstance();

	public void writeTask(Task task, String fileName) throws IOException {
		assert (task != null);

		checkTaskFileFormat(fileName);

		try {
			writeJSONFile(task, fileName);
		} catch (IOException e) {
			blackBox.info(MESSAGE_IO_FAULT);
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

		checkTaskFileFormat(fileName);

		if (isEmptyFile(fileName)) {
			return new ArrayList<Task>();
		} else {
			readJSONFile(fileName, list);
		}
		return list;
	}

	private void checkTaskFileFormat(String fileName) throws Error {
		if (isInvalidFileFormat(fileName)) {
			blackBox.info(MESSAGE_INVALID_FILE_FORMAT);
			throw new Error(MESSAGE_INVALID_FILE_FORMAT);
		}
	}

	private void readJSONFile(String fileName, List<Task> list) {
		try {
			readJSONFormat(fileName, list);
		} catch (FileNotFoundException e) {
			blackBox.info(MESSAGE_EMPTY_FILE);
		} catch (IOException e) {
			blackBox.info(MESSAGE_IO_FAULT);
		} catch (ParseException e) {
			blackBox.info(MESSAGE_INCORRECT_FORMAT);
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

		checkWriteSettingParameters(name, fileName, value);

		try {
			isSuccess = writeSettingTofile(name, fileName, value);
		} catch (FileNotFoundException e) {
			blackBox.info(MESSAGE_NOT_FOUND);
		} catch (IOException e) {
			blackBox.info(MESSAGE_IO_FAULT);
		}
		return isSuccess;
	}

	private void checkWriteSettingParameters(String name, String fileName,
			String value) throws Error {
		if (isInvalidFileFormat(fileName) || !isValidName(name)
				|| !isValidName(value)) {
			blackBox.info(MESSAGE_INVALID_FILE_FORMAT);
			throw new Error(MESSAGE_INVALID_FILE_FORMAT);
		}
	}

	private Boolean writeSettingTofile(String name, String fileName, String value)
			throws IOException {
		assert (value != null && name != null);
		Boolean isSuccess;
		if (name != null && value != null && fileName != null) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,
					true));
			writer.write(name + STRING_COMMA + value);
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

		checkReadSettingParameters(name, fileName);

		String value = STRING_EMPTY;
		try {
			BufferedReader reader = getReader(fileName);
			String lineString;
			if (isEmptyFile(fileName)) {
				reader.close();
				return MESSAGE_EMPTY_FILE;
			}

			while ((lineString = reader.readLine()) != null) {
				if (lineString.contains(name)) {
					value += lineString + STRING_SPACE;
				}
			}
			reader.close();
		} catch (IOException e) {
			blackBox.info(MESSAGE_IO_FAULT);
		}
		return value;
	}

	private void checkReadSettingParameters(String name, String fileName)
			throws Error {
		if (isInvalidFileFormat(fileName) || !isValidName(name)) {
			blackBox.info(MESSAGE_INVALID_FILE_FORMAT);
			throw new Error(MESSAGE_INVALID_FILE_FORMAT);
		}
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
			blackBox.info(MESSAGE_IO_FAULT);
		}
		return false;
	}

	public void clear(String fileName) {
		try {
			writeEmpty(fileName);
		} catch (FileNotFoundException e) {
			blackBox.info(MESSAGE_NOT_FOUND);
		}
	}

	private void writeEmpty(String fileName) throws FileNotFoundException {
		PrintWriter writer;
		writer = new PrintWriter(fileName);
		writer.print(STRING_EMPTY);
		writer.close();
	}

	public boolean isInvalidFileFormat(String text) {
		if (text.contains(PATTERN_DOT)) {
			String[] name = text.split(REGEX);
			return (!(isValidName(name[0]) && isValidName(name[1])
					&& name[1].equals(EXTENSION) && name[1] != null));
		} else {
			return true;
		}
	}

	public boolean isValidName(String text) {
		Pattern pattern = Pattern.compile(INVALID_PATTERN);
		Matcher matcher = pattern.matcher(text);
		boolean isMatch = matcher.matches();
		return isMatch;
	}
}