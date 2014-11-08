package moustachio.task_catalyst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
 * the file.
 * 
 * Likewise, the saved task is converted again to text for the purpose of
 * editing or displaying.
 * 
 */

// @author A0112764J
public class FileHandler {

	private static final String FOLDER_TASK_CATALYST = "Task Catalyst";
	private static final String PATH_TASK_CATALYST = "Task Catalyst/";
	private static final String STRING_SPACE = " ";
	private static final String STRING_WRITE_SETTING = "%1$s,%2$s";
	private static final String STRING_EMPTY = "";
	private static final String REGEX_VALID_FILE_FORMAT = "(?i)^((\\w+|\\d+)|((\\w+|\\d+)\\s*(\\w+|\\d+)))+\\.{1}(txt){1}$";
	private static final String REGEX_VALID_PATTERN = "(?i)^\\w+|\\d+$";

	private static final String MESSAGE_NOT_FOUND = "The file is not found!";
	private static final String MESSAGE_INCORRECT_FORMAT = "Incorrect format has been found";
	private static final String MESSAGE_EMPTY_FILE = "The file is empty.";
	private static final String MESSAGE_IO_FAULT_READ = "IO fault has been enountered during reading from file";
	private static final String MESSAGE_IO_FAULT_WRITE = "IO fault has been enountered during writing to file.";
	private static final String MESSAGE_IO_FAULT_DIR = "IO fault has been encountered during creating dir";
	private static final String MESSAGE_INVALID_FILE_FORMAT = "Invalid file format!";
	private static final String MESSAGE_IO_FAULT_DIR_READ = "IO fault has been encountered during making folder to read task.";

	private static BlackBox blackBox = BlackBox.getInstance();

	public void writeTask(Task task, String fileName) throws IOException {
		assert (task != null && fileName != null);
		checkTaskFileFormat(fileName);
		try {
			createFolder();
			writeJSONFile(task, fileName);
		} catch (IOException e) {
			blackBox.info(MESSAGE_IO_FAULT_WRITE);
		}
	}

	private void createFolder() throws IOException {
		File folder = new File(FOLDER_TASK_CATALYST);
		if (folder.exists() && folder.isFile()) {
			printMessage(MESSAGE_IO_FAULT_DIR);
		} else {
			if (!folder.exists()) {
				folder.mkdir();
			}
		}
	}

	private void writeJSONFile(Task task, String fileName) throws IOException {
		FileWriter jsonFile = new FileWriter(PATH_TASK_CATALYST + fileName,
				true);
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
		assert (fileName != null);
		List<Task> list = new ArrayList<Task>();

		checkTaskFileFormat(fileName);
		doActionToCreateFolder();

		File file = new File(PATH_TASK_CATALYST + fileName);

		if (!file.exists() || isEmptyFile(PATH_TASK_CATALYST + fileName)) {
			createNewTextFile(fileName);
			return new ArrayList<Task>();
		} else {
			readJSONFile(PATH_TASK_CATALYST + fileName, list);
		}
		return list;
	}

	private void doActionToCreateFolder() {
		try {
			createFolder();
		} catch (IOException e) {
			printMessage(MESSAGE_IO_FAULT_DIR_READ);
		}
	}

	private void createNewTextFile(String fileName) {
		File newfileName = new File(PATH_TASK_CATALYST + fileName);
		try {
			writeEmpty(newfileName.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
			blackBox.info(MESSAGE_IO_FAULT_READ);
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
			List<Task> tasks = objCodec.decodeToString(stringLine);
			if (tasks != null) {
				list.addAll(tasks);
			}
		}
		breader.close();
	}

	// @author A0112764J -unused
	/**
	 * This function is implemented for the purpose of storing setting in
	 * future. There is no setting to save for users but it can be useful for
	 * future if users set preferences.
	 * 
	 * @param name The name of the setting used.
	 * @param fileName The filename used to save.
	 * @param value The value of the setting.
	 * @return Returns true if successful, false otherwise.
	 */
	public boolean writeSetting(String name, String fileName, String value) {
		assert (fileName != null && name != null && value != null);
		Boolean isSuccess = false;

		checkWriteSettingParameters(name, fileName, value);

		try {
			isSuccess = writeSettingTofile(name, fileName, value);
		} catch (FileNotFoundException e) {
			blackBox.info(MESSAGE_NOT_FOUND);
		} catch (IOException e) {
			blackBox.info(MESSAGE_IO_FAULT_WRITE);
		}
		return isSuccess;
	}

	// @author A0112764J -unused
	private void checkWriteSettingParameters(String name, String fileName,
			String value) throws Error {
		if (isInvalidFileFormat(fileName) || isInvalidName(name)
				|| isInvalidName(value)) {
			blackBox.info(MESSAGE_INVALID_FILE_FORMAT);
			throw new Error(MESSAGE_INVALID_FILE_FORMAT);
		}
	}

	// @author A0112764J -unused
	private Boolean writeSettingTofile(String name, String fileName,
			String value) throws IOException {
		assert (value != null && name != null);
		Boolean isSuccess = false;
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,
				true));
		writer.write(String.format(STRING_WRITE_SETTING, name, value));
		writer.newLine();
		writer.close();
		isSuccess = true;
		return isSuccess;
	}

	// @author A0112764J -unused
	/**
	 * This function is implemented for the purpose of reading setting in
	 * future. There is no setting to save for users but it can be useful for
	 * future if users set preferences.
	 * 
	 * @param name The name of the setting to load.
	 * @param fileName The name of the file to load from.
	 * @return Returns true the setting is found successful, null otherwise.
	 */
	public String readSetting(String name, String fileName) {
		assert (name != null && fileName != null);

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
			blackBox.info(MESSAGE_IO_FAULT_READ);
		}
		return value;
	}

	// @author A0112764J -unused
	private void checkReadSettingParameters(String name, String fileName)
			throws Error {
		if (isInvalidFileFormat(fileName) || isInvalidName(name)) {
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
			blackBox.info(MESSAGE_IO_FAULT_READ);
		}
		return false;
	}

	public void clear(String fileName) {
		try {
			writeEmpty(PATH_TASK_CATALYST + fileName);
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

	public boolean isInvalidFileFormat(String fileName) {
		Pattern pattern = Pattern.compile(REGEX_VALID_FILE_FORMAT);
		Matcher matcher = pattern.matcher(fileName);
		boolean isMatch = matcher.matches();
		return !isMatch;
	}

	public boolean isInvalidName(String name) {
		Pattern pattern = Pattern.compile(REGEX_VALID_PATTERN);
		Matcher matcher = pattern.matcher(name);
		boolean isMatch = matcher.matches();
		return !isMatch;
	}

	private void printMessage(String message) {
		System.out.println(message);
	}
}