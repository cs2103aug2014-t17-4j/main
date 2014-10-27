package moustachio.task_catalyst;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * credited
 * http://nerdydevel.blogspot.sg/2012/07/run-only-single-java-application
 * -instance.html
 * 
 * @author linxiuqing (A0112764J)
 *
 */

public class Lock {
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
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (!tmpDir.endsWith(System.getProperty("file.separator"))) {
			tmpDir += System.getProperty("file.separator");
		}

		// Acquire MD5
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.reset();
			String hashText = new java.math.BigInteger(1, md.digest(lockKey
					.getBytes())).toString(16);
			// Hash string has no leading zeros
			// Adding zeros to the beginnig of has string
			while (hashText.length() < 32) {
				hashText = "0" + hashText;
			}
			lockFile = new File(tmpDir + hashText + ".app_lock");
		} catch (Exception ex) {
			System.out.println("Lock.Lock() file fail");
		}

		// MD5 acquire fail
		if (lockFile == null) {
			lockFile = new File(tmpDir + lockKey + ".lock");
		}

		lockStream = new FileOutputStream(lockFile);

		String fileContent = "Java Lock Object\r\nLocked by key: " + lockKey
				+ "\r\n";
		try {
		lockStream.write(fileContent.getBytes());
		} catch (Exception e) {
			Logger.getLogger(Lock.class.getName()).log(Level.INFO,
					"Another instance detected");
			System.exit(0);
		}

		lockChannel = lockStream.getChannel();

		lock = lockChannel.tryLock();

		if (lock == null) {
			throw new Exception("Can't create Lock");
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
					"Fail to set Lock.", ex);
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
				throw new NoSuchFieldException("INSTATCE IS NULL!");
			}
			sInstance.release();
		} catch (Throwable ex) {
//			Logger.getLogger(Lock.class.getName()).log(Level.SEVERE,
//					"Fail to release lock.", ex);
		}
	}
}