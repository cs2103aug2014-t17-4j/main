package moustachio.task_catalyst;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

public class TaskCatalyst extends Application implements HotKeyListener{
	private double initialY;
	private double initialX; 
	
	private UIController controller;
	private Stage primaryStage;
	
	private static Provider hotKeys = null;
	private static String toggleLaunchHK = "control M";
	
	public static void main(String[] args) {
		try {
		    if (!Lock.setLock("CUSTOM_LOCK_KEY")) {
		        throw new RuntimeException("This application is single instance!");
		    }
		    launch(args);
		}
		finally{
		    Lock.releaseLock(); 
		}
	}
	
	Stage getStage() {
		return this.primaryStage;
	}

	@Override
	public void start(Stage primaryStage){
		this.primaryStage = primaryStage;
		
		try {
			/*Parent root = FXMLLoader.load(getClass().getResource(
					"userInterface.fxml"));*/
		
			loadSystemTray(this.primaryStage);
			startHotKeys();
			FXMLLoader loader = new FXMLLoader(TaskCatalyst.class.getResource("userInterface.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			controller = loader.getController();
			controller.connectWithMainTaskCatalyst(this);
			addHotKeysListeners(primaryStage,scene);
			addDragListeners(root);
			// set stylesheet
			scene.getStylesheets().add(
					getClass().getResource("DarkTheme.css").toExternalForm());

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
			}
		}).start();
	}
	
	/**
	 * This function disables the global hotkey (ctrl+m).
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
	 * This function creates hotkey for the actions that are undo,redo and exit. 
	 * 
	 * @author Lin XiuQing (A0112764J)
	 */
	
	private void addHotKeysListeners(Stage stage, Scene scene){
		final KeyCombination undoHotKey = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN);
		final KeyCombination redoHotKey = new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN);
		final KeyCombination exitHotKey = new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>(){
			
		@Override
			public void handle(KeyEvent event){
				if(undoHotKey.match(event))  {	
					controller.handleHotKeys("undo");
				}
				else if (redoHotKey.match(event)) {
					controller.handleHotKeys("redo");
				}
				else if(exitHotKey.match(event)){
					System.exit(0);
				}
			}	
		});
	}

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
					mainUI.getScene().getWindow().setX(me.getScreenX() - initialX);
					mainUI.getScene().getWindow().setY(me.getScreenY() - initialY);
				}
			}
		});
	}
	
	@Override
	public void stop(){
		BlackBox.getInstance().close();
		try {
			super.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadSystemTray(Stage stage) {
		// checking for support
		if (!SystemTray.isSupported()) {
			System.out.println("System tray is not supported !!! ");
			return;
		}
		// get the systemTray of the system
		SystemTray systemTray = SystemTray.getSystemTray();
		// get default toolkit
		Image image = Toolkit.getDefaultToolkit().getImage(
				"src/main/java/moustachio/images/moustachio.png");

		// popupmenu
		PopupMenu trayPopupMenu = new PopupMenu();
		
		/**
		 * 
		 * @author A0112764J
		 */
		
		// 1st menuitem for popupmenu
		MenuItem launch = new MenuItem("Launch");
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

		// 2nd menuitem for popupmenu
		MenuItem action = new MenuItem("Action");
		action.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Action Clicked");
			}
		});
		trayPopupMenu.add(action);

		// 3rd menuitem of popupmenu
		MenuItem close = new MenuItem("Exit");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopHotKeys();
				System.exit(0);
			}
		});
		trayPopupMenu.add(close);

		// setting tray icon
		TrayIcon trayIcon = new TrayIcon(image, "Task Catalyst", trayPopupMenu);
		// adjust to default size as per system recommendation
		trayIcon.setImageAutoSize(true);

		try {
			systemTray.add(trayIcon);
		} catch (AWTException awtException) {
			awtException.printStackTrace();
		}
	}
	/**
	 * This function is to execute global hot key ctrl+m that minimizes application while it is running, 
	 * and to relaunch application while it is minimize at system tray.
	 * 
	 * @author A0112764J
	 * 
	 * @param hotKey
	 */
	@Override
	public void onHotKey(HotKey hotKey) {
		switch (hotKey.keyStroke.getKeyCode()) {
		case java.awt.event.KeyEvent.VK_M :
			if (primaryStage.isShowing()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.hide();
					}
				});
			}
			else {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.show();
						primaryStage.toFront();
					}
				});
			}
			break;
		}
		
	}
}
