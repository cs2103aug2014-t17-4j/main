package moustachio.task_catalyst;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This program is to create pop-up window for comprehensive quick guide for users. 
 * 
 * Users can use hotkey "Ctrl+H" to launch quick guide window and use "Ctrl+H" to close 
 * Task Catalyst while it is running.
 *
 */

//@author A0112764J
public class HelpViewController {
	@FXML
	private TabPane helpPane;
	@FXML
	private Tab hashtagTab;
	@FXML
	private Tab basicFeaturesTab;
	@FXML
	private Tab specialFeaturesTab;
	@FXML
	private Tab hotKeysTab;
	@FXML
	private Button exitButton;
	@FXML
	private TextArea hashTagArea;
	@FXML
	private ImageView basicFeatures;
	@FXML
	private ImageView specialFeatures;
	@FXML
	private ImageView hashtags;
	@FXML
	private ImageView hotkeys;

	private Stage helpStage;

	private static final String QUICKGUIDE_FXML_PATH = "/fxml/quickGuide.fxml";
	
	private HelpViewController controller;
	
	private double initialY;
	private double initialX;

	public HelpViewController() {
		
	}

	public void setStage(Stage helpStage) {
		this.helpStage = helpStage;
	}
	
	Stage getStage() {
		return this.helpStage;
	}
	
	@FXML
	public void initialize() {
		assert helpPane != null : "fx:id=\"helpPane\" was not injected: check your FXML file 'interface.fxml'.";
		assert hashtagTab != null : "fx:id=\"hashtagTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert basicFeaturesTab != null : "fx:id=\"basicFeaturesTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert specialFeaturesTab != null : "fx:id=\"specialFeaturesTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert hotKeysTab != null : "fx:id=\"hotKeysTab\" was not injected: check your FXML file 'interface.fxml'.";
		assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'interface.fxml'.";
	}
	
	@FXML
	public void handleExit() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				helpStage.hide();
			}
		});
	}
	
	private void addDragListeners(final Node helpUI) {
		helpUI.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				} else {
					helpUI.getScene().getWindow().centerOnScreen();
					initialX = helpUI.getScene().getWindow().getX();
					initialY = helpUI.getScene().getWindow().getY();
				}
			}
		});

		helpUI.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					helpUI.getScene().getWindow()
					.setX(me.getScreenX() - initialX);
					helpUI.getScene().getWindow()
					.setY(me.getScreenY() - initialY);
				}
			}
		});
	}

	public void openHelpWindow() {
		AnchorPane helpPane;
		if (helpStage != null) {
			helpStage.show();
			helpStage.toFront();
		}
		else {
			try {
				helpStage = new Stage();
				FXMLLoader loader = new FXMLLoader(getClass().getResource(
						QUICKGUIDE_FXML_PATH));
				helpPane = loader.load();

				controller = loader.getController();
				addDragListeners(helpPane);
				controller.setStage(helpStage);
				helpStage.setScene(new Scene(helpPane));
				helpStage.initStyle(StageStyle.UNDECORATED);
				helpStage.setX(0);
				helpStage.setY(30);
				helpStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

//quickGuide.fxml
//<?xml version="1.0" encoding="UTF-8"?>
//
//<?import javafx.scene.text.*?>
//<?import javafx.scene.web.*?>
//<?import javafx.scene.*?>
//<?import javafx.scene.control.*?>
//<?import javafx.scene.image.*?>
//<?import java.lang.*?>
//<?import javafx.scene.layout.*?>
//<?import javafx.scene.layout.AnchorPane?>
//
//<AnchorPane prefHeight="457.0" prefWidth="505.0" styleClass="theme" stylesheets="@../css/helpTheme.css" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" fx:controller="moustachio.task_catalyst.HelpViewController">
//   <children>
//      <Pane layoutX="4.0" prefHeight="52.0" prefWidth="493.0" styleClass="theme" stylesheets="@../css/helpTheme.css" AnchorPane.bottomAnchor="348.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
//         <children>
//            <ImageView fitHeight="32.0" fitWidth="152.0" layoutX="40.0" layoutY="10.0" styleClass="helpTheme.css">
//               <image>
//                  <Image url="@../images/title.png" />
//               </image>
//            </ImageView>
//            <ImageView fitHeight="42.0" fitWidth="46.0" layoutX="4.0" layoutY="5.0">
//               <image>
//                  <Image url="@../images/info.png" />
//               </image>
//            </ImageView>
//            <Button fx:id="exitButton" layoutX="473.0" layoutY="8.0" mnemonicParsing="false" onAction="#handleExit" onMouseClicked="#handleExit" prefHeight="25.0" prefWidth="25.0" stylesheets="@../css/DarkTheme.css" />
//         </children>
//      </Pane>
//      <TabPane fx:id="helpPane" layoutX="6.0" layoutY="50.0" prefHeight="409.0" prefWidth="491.0" tabClosingPolicy="UNAVAILABLE" tabMinWidth="104.0">
//        <tabs>
//          <Tab fx:id="hashtagTab" text="Hashtags">
//            <content>
//              <AnchorPane focusTraversable="true" minHeight="0.0" minWidth="0.0" prefHeight="379.0" prefWidth="484.0" stylesheets="@../css/helpTheme.css">
//                     <children>
//                        <ImageView fx:id="hashtags" fitHeight="369.0" fitWidth="491.0" pickOnBounds="true" preserveRatio="true" AnchorPane.bottomAnchor="17.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
//                           <image>
//                              <Image url="@../images/hashtag.jpg" />
//                           </image></ImageView>
//                     </children></AnchorPane>
//            </content>
//          </Tab>
//          <Tab fx:id="basicFeaturesTab" text="Basic Features">
//            <content>
//              <AnchorPane minHeight="0.0" minWidth="0.0" prefHeight="180.0" prefWidth="200.0">
//                     <children>
//                        <ImageView fx:id="basicFeatures" fitHeight="402.0" fitWidth="491.0" pickOnBounds="true" preserveRatio="true" AnchorPane.bottomAnchor="-26.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
//                           <image>
//                              <Image url="@../images/basic.jpg" />
//                           </image></ImageView>
//                     </children></AnchorPane>
//            </content>
//          </Tab>
//            <Tab fx:id="specialFeaturesTab" text="Special Features">
//               <content>
//                  <AnchorPane minHeight="0.0" minWidth="0.0" prefHeight="317.0" prefWidth="435.0">
//                     <children>
//                        <ImageView fx:id="specialFeatures" fitHeight="395.0" fitWidth="491.0" pickOnBounds="true" preserveRatio="true" AnchorPane.bottomAnchor="-19.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
//                           <image>
//                              <Image url="@../images/special.jpg" />
//                           </image></ImageView>
//                     </children></AnchorPane>
//               </content>
//            </Tab>
//            <Tab fx:id="hotKeysTab" text="HotKeys">
//               <content>
//                  <AnchorPane minHeight="0.0" minWidth="0.0" prefHeight="317.0" prefWidth="456.0">
//                     <children>
//                        <ImageView fitHeight="377.0" fitWidth="491.0" pickOnBounds="true" preserveRatio="true">
//                           <image>
//                              <Image url="@../images/hotkeys.jpg" />
//                           </image>
//                        </ImageView>
//                     </children></AnchorPane>
//               </content>
//            </Tab>
//        </tabs>
//      </TabPane>
//   </children>
//</AnchorPane>

//helpTheme.css
//#exitButton {          
//	   
//    -fx-background-size: 24.0 24.0;
//    -fx-background-repeat: no-repeat;
//    -fx-background-position:left;   
//    -fx-background-color: transparent;  
//}
//#programTitle{
//	-fx-image: url("../images/title.png");
//}
//
//.rootAnchorPane {
//	
//}
//
//.tab-header-background{
//	-fx-background-color: lightgray;
//}
//
///* GUI Interface background colour */
//.theme {
//    master-color: gray;
//    -fx-background-color: lightgray;
//    -fx-font-size: 12.0px;
//}