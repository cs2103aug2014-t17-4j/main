package moustachio.task_catalyst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is to create lock for global hotkeys. 
 * 
 * This was adapted from a post from Rumato Estorsky posted 20th July 2012 on following blog:
 * 
 * http://nerdydevel.blogspot.com/2012/07/run-only-single-java-application-instance.html
 */

//@author A0112764J -reused
public class Lock {
	private static final String EXTENSION_APP_LOCK = ".app_lock";
	private static final String EXTIONSION_LOCK = ".lock";
	
	private static final String MESSAGE_INSTATCE_IS_NULL = "INSTATCE IS NULL!";
	private static final String MESSAGE_FAIL_TO_SET_LOCK = "Fail to set Lock.";
	private static final String MESSAGE_FAIL_TO_CREATE_LOCK = "Can't create Lock";
	private static final String MESSAGE_ANOTHER_INSTANCE_DETECTED = "Another instance detected";
	private static final String MESSAGE_LOCKED_BY_KEY = "Java Lock Object\r\nLocked by key: ";
	private static final String MESSAGE_LOCK_FILE_FAIL = "Lock.Lock() file fail";
	
	private static final String NAME_LOCK_FILE = "MD5";
	private static final String FILE_SEPARATOR = "file.separator";
	private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";


	private Lock() {
	}

	File lockFile = null;
	FileLock lock = null;
	FileChannel lockChannel = null;
	FileOutputStream lockStream = null;

	private static Lock sInstance;

	/**
	 * This program instantiates a new lock.
	 *
	 * @param lockKey
	 *            Unique application key
	 * @throws Exception
	 */
	private Lock(String lockKey) throws Exception {
		String tmpDir = setTempDir();
		// Acquire MD5 - lock file
		acquireMD5(lockKey, tmpDir);
		// MD5 acquire fail
		if (lockFile == null) {
			createLockFile(lockKey, tmpDir, EXTIONSION_LOCK);
		}
		lockStream = new FileOutputStream(lockFile);
		writeLockStream(MESSAGE_LOCKED_BY_KEY + lockKey
				+ "\r\n");
		createLock();
		printFailureMessage();
	}

	private void createLockFile(String lockKey, String tmpDir, String extension) {
		lockFile = new File(tmpDir + lockKey + extension);
	}

	private void createLock() throws IOException {
		lockChannel = lockStream.getChannel();
		lock = lockChannel.tryLock();
	}

	private String setTempDir() {
		String tmpDir = System.getProperty(JAVA_IO_TMPDIR);
		if (!tmpDir.endsWith(System.getProperty(FILE_SEPARATOR))) {
			tmpDir += System.getProperty(FILE_SEPARATOR);
		}
		return tmpDir;
	}

	private void printFailureMessage() throws Exception {
		if (lock == null) {
			throw new Exception(MESSAGE_FAIL_TO_CREATE_LOCK);
		}
	}

	private void acquireMD5(String lockKey, String tmpDir) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance(NAME_LOCK_FILE);
			md.reset();
			String hashText = new java.math.BigInteger(1, md.digest(lockKey
					.getBytes())).toString(16);
			// Hash string has no leading zeros
			// Adding zeros to the beginning of has string
			while (hashText.length() < 32) {
				hashText = "0" + hashText;
			}
			createLockFile(hashText,tmpDir,EXTENSION_APP_LOCK);
		} catch (Exception ex) {
			System.out.println(MESSAGE_LOCK_FILE_FAIL);
		}
	}

	private void writeLockStream(String fileContent) {
		try {
			lockStream.write(fileContent.getBytes());
		} catch (Exception e) {
			Logger.getLogger(Lock.class.getName()).log(Level.INFO,
					MESSAGE_ANOTHER_INSTANCE_DETECTED);
			System.exit(0);
		}
	}

	/**
	 * This function is to release Lock.
	 *
	 * @throws Throwable
	 */
	private void release() throws Throwable {
		if (lock.isValid()) {
			lock.release();
		}
		if (lockStream != null) {
			lockStream.close();
		}
		if (lockChannel.isOpen()) {
			lockChannel.close();
		}
		if (lockFile.exists()) {
			lockFile.delete();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.release();
		super.finalize();
	}

	/**
	 * This function is to make only one application to be run and ignore next
	 * call of the application.
	 *
	 * @param lockKey
	 *            Unique application lock key
	 * @return true, if successful
	 */
	public static boolean setLock(String lockKey) {
		if (sInstance != null) {
			return true;
		}

		try {
			sInstance = new Lock(lockKey);
		} catch (Exception ex) {
			sInstance = null;
			Logger.getLogger(Lock.class.getName()).log(Level.SEVERE,
					MESSAGE_FAIL_TO_SET_LOCK, ex);
			return false;
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					Lock.releaseLock();
				} catch (Exception e) {
					System.exit(0);
				}
			}
		});
		return true;
	}

	/**
	 * This function is trying to release lock. It disables to use Lock again
	 * after executing release.
	 */
	public static void releaseLock() {
		try {
			if (sInstance == null) {
				throw new NoSuchFieldException(MESSAGE_INSTATCE_IS_NULL);
			}
			sInstance.release();
		} catch (Throwable ex) {
			//			Logger.getLogger(Lock.class.getName()).log(Level.SEVERE,
			//					"Fail to release lock.", ex);
		}
	}
}