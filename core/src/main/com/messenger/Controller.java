package com.messenger;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private Label listeningAtLabel;
    @FXML private Label connectedToLabel;
    @FXML private ImageView messageSendButton;
    @FXML private TextArea messageSendBox;
    @FXML private ImageView fileAttachButton;
    @FXML private ColorPicker themeChangeButton;
    @FXML private ScrollPane messageList;
    @FXML private ImageView saveMessageButton;
    @FXML private Label chatUserNameLabel;
    @FXML private ImageView loadMessageIcon;


    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private String userName;
    private Pane root;
    private Messenger main;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        connectedToLabel.setText("Sending Message at Port : "+messageSender.getSenderPort());
//        listeningAtLabel.setText("Receiving Message at Port :"+messageReceiver.getReceiverPort());
//        themeChangeButton.setValue(Color.RED);
//        messageList.setStyle("-fx-background:transparent;");
//        chatUserNameLabel.setText("Welcome " + userName);

//        messageSendButton.setOnMouseClicked(new EventHandler<Event>() {
//
//            @Override
//            public void handle(Event arg0) {
//                String message = messageSendBox.getText().toString();
//                try {
//                    if(message.length()>0)
//                    {
//                        messageSender.sendMessage(message);
//                    }
//                    if(file!=null)
//                    {
//                        messageSender.sendFile(byteArray,file);
//                        file = null;
//                    }
//                    messageSendBox.setText("");
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        fileAttachButton.setOnMouseClicked(new EventHandler<Event>() {
//
//            @Override
//            public void handle(Event event) {
//                fileChooser = new FileChooser();
//                file = fileChooser.showOpenDialog(stage);
//                if(file!=null)
//                {
//                    String fileName = file.getName();
//                    byteArray  = new byte [(int)file.length()];
//
//                }
//            }
//        });
//
//        themeChangeButton.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                Color color = themeChangeButton.getValue();
//
//                String hexColor = "#" + Integer.toHexString(color.hashCode());
//
//                try {
//                    messageSender.sendBackgroundColor(hexColor);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        saveMessageButton.setOnMouseClicked(new EventHandler<Event>() {
//
//            @Override
//            public void handle(Event arg0) {
//                try {
//                    saveMessages(false);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//        loadMessageIcon.setOnMouseClicked(new EventHandler<Event>() {
//
//            @Override
//            public void handle(Event event) {
//                for(String message : allMessages)
//                {
//                    logger.info(message);
//                }
//                try {
//                    FileReader fileReader = new FileReader(ipAddressToConnect+".txt");
//                    StringBuilder messages = new StringBuilder();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//
//
//            }
//        });
//
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//
//            @Override
//            public void handle(WindowEvent event) {
//                try {
//                    saveMessages(true);
//                } catch (IOException | InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//
//            }
//        });
    }

    public void showChatScreen() throws IOException
    {
        root = (Pane) (new FXMLLoader(getClass().getClassLoader().getResource("views/ChatScreen.fxml"))).load();
        if(main != null)
            main.setRoot(this.root);
    }
}
