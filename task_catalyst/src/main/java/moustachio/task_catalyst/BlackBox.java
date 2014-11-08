package moustachio.task_catalyst;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

//@author A0111890
/**
 * BlackBox provides the logging functionality for the system. It applies the
 * Singleton pattern to prevent multiple access to the logging file. Do remember
 * to close this logger after using it to release resources.
 */
public class BlackBox {

	private static final String DEFAULT_LOG_FILE_NAME = "Task Catalyst/blackbox.log";
	public static BlackBox blackBox;
	private static Logger logger;

	public static BlackBox getInstance() {
		if (blackBox == null) {
			blackBox = new BlackBox();
		}
		return blackBox;
	}

	private BlackBox() {
		try {
			initializeLogger();
			initializeHandler();
		} catch (IOException e) {
		}
	}

	private void initializeLogger() {
		logger = Logger.getLogger(BlackBox.class.getName());
		logger.setLevel(Level.SEVERE);
	}

	private void initializeHandler() throws IOException {
		String path = DEFAULT_LOG_FILE_NAME;
		boolean append = true;
		int limit = 1024000;
		int numFiles = 2;

		Handler handler = new FileHandler(path, limit, numFiles, append);

		logger.addHandler(handler);
	}

	public void setLevel(Level level) {
		logger.setLevel(level);
	}

	public void severe(String string) {
		logger.severe(string);
	}

	public void warning(String string) {
		logger.warning(string);
	}

	public void info(String string) {
		logger.info(string);
	}

	public void config(String string) {
		logger.config(string);
	}

	public void fine(String string) {
		logger.fine(string);
	}

	public void finer(String string) {
		logger.finer(string);
	}

	public void finest(String string) {
		logger.finest(string);
	}

	public void close() {
		for (Handler handler : logger.getHandlers()) {
			handler.close();
		}
		logger = null;
		blackBox = null;
	}
}
