package com.messenger;
	
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class Messenger extends Application {
	private Pane root;
	private Parent chatScreen;
	private Scene scene;
	private AnotherController mainController;
	@FXML private ImageView homeImage;
	@Override
	public void start(Stage primaryStage) {
		try {
			
			root = FXMLLoader.load(getClass().getClassLoader().getResource("views/WelcomeScreen.fxml"));
			System.out.println(root.lookup("#anotherButton"));
			chatScreen = FXMLLoader.load(getClass().getClassLoader().getResource("views/ChatScreen.fxml"));
			scene = new Scene(root,600,650);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("styles/listStyle.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Welcome");
			primaryStage.setResizable(false);
			primaryStage.show();
			mainController = new AnotherController(this);
			mainController.setStage(primaryStage);
		} catch(Exception e) {
			System.out.println("There is a problem");
			e.printStackTrace();
		}
	}
	
	public void setRoot(Pane root)
	{
		this.root = root;
		scene.setRoot(root);
	}
	
	
	
	public Pane getRoot() {
		return root;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
