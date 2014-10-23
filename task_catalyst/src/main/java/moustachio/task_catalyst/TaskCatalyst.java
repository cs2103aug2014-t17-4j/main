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
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.JOptionPane;

public class TaskCatalyst extends Application {
	private double initialY;
	private double initialX;

	public static void main(String[] args) {
		loadSystemTray();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource(
					"userInterface.fxml"));
			Scene scene = new Scene(root);
			addDragListeners(root);
			// set stylesheet
			scene.getStylesheets().add(
					getClass().getResource("DarkTheme.css").toExternalForm());

			// set stage
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}	
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

	private static void loadSystemTray() {
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

		// 1st menuitem for popupmenu
		MenuItem action = new MenuItem("Action");
		action.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Action Clicked");
			}
		});
		trayPopupMenu.add(action);

		// 2nd menuitem of popupmenu
		MenuItem close = new MenuItem("Exit");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
}
