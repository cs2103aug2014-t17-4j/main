package moustachio.task_catalyst;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class BlackBox {

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
			logger = Logger.getLogger(BlackBox.class.getName());
			// logger.setLevel(Level.ALL);
			Handler handler = new FileHandler("blackbox.log");
			logger.addHandler(handler);
		} catch (IOException e) {
		}
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
}
