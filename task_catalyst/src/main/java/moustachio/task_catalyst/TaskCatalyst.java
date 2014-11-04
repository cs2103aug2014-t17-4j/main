package moustachio.task_catalyst;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

public class TaskCatalyst extends Application implements HotKeyListener {

	Clipboard clipboard = Clipboard.getSystemClipboard();

	private boolean helpFlag = false;

	private double initialY;
	private double initialX;

	private UIController controller;
	private Stage primaryStage;
	private HelpViewController helpController = new HelpViewController();

	private static Provider hotKeys = null;
	private static String toggleLaunchHK = "control M";
	private static String pasteHK = "control D";

	private static final String MULTIPLE_INSTANCE_EXCEPTION_MESSAGE = "This application is single instance!";
	private static final String SYSTEM_TRAY_ERROR_MESSAGE = "System tray is not supported!";

	private static final String UI_FXML_PATH = "/fxml/userInterface.fxml";
	private static final String CSS_PATH = "/css/DarkTheme.css";
	private static final String SYSTEM_TRAY_IMAGE_PATH = "/images/moustachio.png";

	public static void main(String[] args) {
		try {
			if (!Lock.setLock("CUSTOM_LOCK_KEY")) {
				throw new RuntimeException(MULTIPLE_INSTANCE_EXCEPTION_MESSAGE);
			}
			launch(args);
		} finally {
			Lock.releaseLock();
		}
	}

	Stage getStage() {
		return this.primaryStage;
	}

	public void setStageHeight(double height) {
		primaryStage.setHeight(height);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		javafx.scene.image.Image applicationIcon = new javafx.scene.image.Image(
				getClass().getResourceAsStream(SYSTEM_TRAY_IMAGE_PATH));
		this.primaryStage.getIcons().add(applicationIcon);
		// this.setIconImage(Toolkit.getDefaultToolkit().getImage("images\\mylogo.png"));
		// primaryStage.getIcons().add(new
		// javafx.scene.image.Image(this.getClass().getResource(SYSTEM_TRAY_IMAGE_PATH).toExternalForm()));
		try {
			loadSystemTray(this.primaryStage);
			startHotKeys();
			FXMLLoader loader = new FXMLLoader(
					TaskCatalyst.class.getResource(UI_FXML_PATH));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			controller = loader.getController();
			controller.connectWithMainTaskCatalyst(this);
			addHotKeysListeners(primaryStage, scene);
			addDragListeners(root);
			// set stylesheet
			scene.getStylesheets().add(
					getClass().getResource(CSS_PATH).toExternalForm());

			// set stage
			Platform.setImplicitExit(false);
			this.primaryStage.setScene(scene);
			this.primaryStage.initStyle(StageStyle.UNDECORATED);
			this.primaryStage.setAlwaysOnTop(true);
			this.primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function registers the global hotkey (ctrl+m).
	 * 
	 * @author A0112764J
	 */
	private void startHotKeys() {
		TaskCatalyst tc = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (hotKeys == null) {
					hotKeys = Provider.getCurrentProvider(false);
				}
				hotKeys.reset();
				hotKeys.register(KeyStroke.getKeyStroke(toggleLaunchHK), tc);
				hotKeys.register(KeyStroke.getKeyStroke(pasteHK), tc);
			}
		}).start();
	}

	/**
	 * This function disables the global hotkey (ctrl+m).
	 * 
	 * @author A0112764J
	 */
	private static void stopHotKeys() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (hotKeys != null) {
					hotKeys.reset();
					hotKeys.stop();
				}
			}
		}).start();
	}

	/**
	 * This function creates hotkeys for the actions that are undo,redo and
	 * exit.
	 * 
	 * @author Lin XiuQing (A0112764J)
	 */

	private void addHotKeysListeners(Stage stage, Scene scene) {
		final KeyCombination undoHotKey = new KeyCodeCombination(KeyCode.Z,
				KeyCodeCombination.CONTROL_DOWN);
		final KeyCombination redoHotKey = new KeyCodeCombination(KeyCode.Y,
				KeyCodeCombination.CONTROL_DOWN);
		final KeyCombination exitHotKey = new KeyCodeCombination(KeyCode.E,
				KeyCodeCombination.CONTROL_DOWN);
		final KeyCombination helpHotKey = new KeyCodeCombination(KeyCode.H,
				KeyCodeCombination.CONTROL_DOWN);
		final KeyCombination scrollTaskUpHotKey = new KeyCodeCombination(
				KeyCode.UP, KeyCodeCombination.SHIFT_DOWN);
		final KeyCombination scrollTaskDownHotKey = new KeyCodeCombination(
				KeyCode.DOWN, KeyCodeCombination.SHIFT_DOWN);
		final KeyCombination scrollHashtagUpHotKey = new KeyCodeCombination(
				KeyCode.UP, KeyCodeCombination.ALT_DOWN);
		final KeyCombination scrollHashtagDownHotKey = new KeyCodeCombination(
				KeyCode.DOWN, KeyCodeCombination.ALT_DOWN);
		scene.addEventHandler(KeyEvent.KEY_RELEASED,
				new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {

						if (undoHotKey.match(event)) {
							controller.handleHotKeys("undo");
						} else if (redoHotKey.match(event)) {
							controller.handleHotKeys("redo");
						} else if (exitHotKey.match(event)) {
							stop();
						} else if (helpHotKey.match(event)) {
							if (helpFlag) {
								helpController.getStage().close();
								helpFlag = false;
							} else {
								helpController.openHelpWindow();
								primaryStage.requestFocus();
								helpFlag = true;
							}
						} else if (scrollTaskUpHotKey.match(event)) {
							controller.scrollTaskUp();
						} else if (scrollTaskDownHotKey.match(event)) {
							controller.scrollTaskDown();
						} else if (scrollHashtagUpHotKey.match(event)) {
							controller.scrollHashtagUp();
						} else if (scrollHashtagDownHotKey.match(event)) {
							controller.scrollHashtagDown();
						}
					}
				});
	}

	/**
	 * This function enables the UI to be draggable
	 * 
	 * @author A0111921W
	 */
	private void addDragListeners(final Node mainUI) {

		mainUI.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {

				if (me.getButton() != MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				} else {
					mainUI.getScene().getWindow().centerOnScreen();
					initialX = mainUI.getScene().getWindow().getX();
					initialY = mainUI.getScene().getWindow().getY();
				}

			}
		});

		mainUI.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					mainUI.getScene().getWindow()
							.setX(me.getScreenX() - initialX);
					mainUI.getScene().getWindow()
							.setY(me.getScreenY() - initialY);
				}
			}
		});
	}

	/**
	 * 
	 * @author A0112764J
	 */
	@Override
	public void stop() {
		BlackBox.getInstance().close();
		try {
			super.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * This function creates a system tray with 2 popup menu Launch and Exit
	 * 
	 * @param stage
	 */
	private static void loadSystemTray(Stage stage) {
		// checking for support
		if (!SystemTray.isSupported()) {
			System.out.println(SYSTEM_TRAY_ERROR_MESSAGE);
			return;
		}
		// get the systemTray of the system
		SystemTray systemTray = SystemTray.getSystemTray();
		// get default toolkit
		Image image = Toolkit.getDefaultToolkit().getImage(
				TaskCatalyst.class.getResource(SYSTEM_TRAY_IMAGE_PATH));

		// popupmenu
		JPopupMenu trayPopupMenu = new JPopupMenu();

		/**
		 * 
		 * @author A0112764J
		 */

		// 1st menuitem for popupmenu
		JMenuItem launch = new JMenuItem("Launch");
		launch.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_M,
				java.awt.event.InputEvent.CTRL_MASK));
		launch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						stage.show();
						stage.toFront();
					}
				});
			}
		});
		trayPopupMenu.add(launch);
		trayPopupMenu.addSeparator();

		// 2nd menuitem of popupmenu
		JMenuItem close = new JMenuItem("Exit");
		close.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E,
				java.awt.event.InputEvent.CTRL_MASK));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopHotKeys();
				System.exit(0);
			}
		});
		trayPopupMenu.add(close);

		// setting tray icon
		TrayIcon trayIcon = new TrayIcon(image, "Task Catalyst", null);
		// adjust to default size as per system recommendation
		trayIcon.setImageAutoSize(true);

		try {
			systemTray.add(trayIcon);
		} catch (AWTException awtException) {
			awtException.printStackTrace();
		}

		JDialog hiddenDialog = new JDialog();
		hiddenDialog.setSize(10, 10);
		hiddenDialog.setUndecorated(true);
		/* Add the window focus listener to the hidden dialog */
		hiddenDialog.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent we) {
				hiddenDialog.setVisible(false);
			}

			@Override
			public void windowGainedFocus(WindowEvent we) {
			}
		});

		trayIcon.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.getButton() == e.BUTTON1) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (stage.isShowing()) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										stage.hide();
									}
								});
							} else {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										stage.show();
										stage.toFront();
									}
								});
							}
						}
					});
				} else if (e.getButton() == e.BUTTON3) {
					trayPopupMenu.setInvoker(hiddenDialog);
					hiddenDialog.setVisible(true);
					trayPopupMenu.setVisible(true);
					hiddenDialog.setLocation((int) e.getX(), (int) e.getY()
							- trayPopupMenu.getHeight());
					trayPopupMenu.setLocation((int) e.getX(), (int) e.getY()
							- trayPopupMenu.getHeight());
				}

			}
		});
	}

	/**
	 * This function is to execute global hot key ctrl+m that minimizes
	 * application while it is running, and to relaunch application while it is
	 * minimize at system tray.
	 * 
	 * @author A0112764J
	 * 
	 * @param hotKey
	 */
	@Override
	public void onHotKey(HotKey hotKey) {
		switch (hotKey.keyStroke.getKeyCode()) {
		case java.awt.event.KeyEvent.VK_M:
			if (primaryStage.isShowing()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.hide();
					}
				});
			} else {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.show();
						primaryStage.toFront();
					}
				});
			}
			break;
		case java.awt.event.KeyEvent.VK_D:
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (clipboard.hasString()) {
						if (!primaryStage.isShowing()) {
							primaryStage.show();
						}
						primaryStage.toFront();
						controller.setCommandBar(clipboard.getString());
					}
				}
			});
			break;
		}

	}
}
