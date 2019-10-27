package com.messenger;

import com.messages.PMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;

public class MessengerController {

    @FXML
    private Label listeningAtLabel;
    @FXML
    private Label connectedToLabel;
    @FXML
    private ImageView messageSendButton;
    @FXML
    private TextArea messageSendBox;
    @FXML
    private ImageView fileAttachButton;
    @FXML
    private ColorPicker themeChangeButton;
    @FXML
    private ScrollPane messageList;
    @FXML
    private ImageView saveMessageButton;
    @FXML
    private Label chatUserNameLabel;
    @FXML
    private ImageView loadMessageIcon;


    private Sender sender;
    private Receiver receiver;
    private Pane root;
    private Stage stage;
    public static ArrayList<String> allMessages;
    private FileChooser fileChooser;
    private File file;
    private byte[] byteArray;

    Logger logger = LoggerFactory.getLogger(MessengerController.class);

    public MessengerController() {
        this.sender = null;
        this.receiver = null;
    }

    public void linkSenderAndReceiver(Sender sender, Receiver receiver) {
        this.sender = sender;
        this.receiver = receiver;

        if (!this.sender.isAlive()) this.sender.start();
        if (!this.receiver.isAlive()) this.receiver.start();
    }

    public void setRoot(Pane root) {
        this.root = root;
    }


    public void initialize() {

        if (root != null && stage != null) {
            receiver.setRoot(root);
            sender.setRoot(root);
            sender.setSender("userName");
            receiver.setStage(stage);
        }

        themeChangeButton.setValue(Color.RED);
        messageList.setStyle("-fx-background:transparent;");

        messageSendButton.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event arg0) {
                String message = messageSendBox.getText().toString();
                try {
                    if (message.length() > 0) {
                        sender.sendMessage(message);
                    }
                    if (file != null) {
                        sender.sendFile(byteArray, file);
                        file = null;
                    }
                    messageSendBox.setText("");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        fileAttachButton.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                fileChooser = new FileChooser();
                file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    String fileName = file.getName();
                    byteArray = new byte[(int) file.length()];
                    try {
                        sender.sendFile(byteArray, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        themeChangeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Color color = themeChangeButton.getValue();

                String hexColor = "#" + Integer.toHexString(color.hashCode());

                try {
                    sender.sendBackgroundColor(hexColor);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        saveMessageButton.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                try {
                    saveMessages(false);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

        loadMessageIcon.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                for (String message : allMessages) {
                    logger.info(message);
                }
//                try {
//                    FileReader fileReader = new FileReader(ipAddressToConnect+".txt");
//                    StringBuilder messages = new StringBuilder();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
        });

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

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void saveMessages(boolean isExiting) throws IOException, InterruptedException {
//        sentMessages = messageSender.getMessageList();
        String messages = "";
        for (String message : allMessages) {
            messages += message + "\n";
        }
        logger.info(messages);
        byte[] byteArrayFromString = messages.getBytes(Charset.forName("UTF-8"));


        if (!isExiting) {
            FileChooser fileSaver = new FileChooser();
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileSaver.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text File",
                            "*.txt"),
                    new FileChooser.ExtensionFilter("Document",
                            "*.pdf", "*.docx")
            );
            File file = fileSaver.showSaveDialog(stage);
            if (file != null) {
                logger.info(file.getAbsolutePath());
                try {
                    saveFile(file.getAbsolutePath(), byteArrayFromString, false);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm to close");
            String s = "Are you sure to exit?";
            alert.setContentText(s);
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
//                saveFile(ipAddressToConnect+".txt",byteArrayFromString,true);
                Platform.exit();
                System.exit(0);
                if (receiver.isAlive()) receiver.join();
                if (sender.isAlive()) sender.join();
            }
        }
    }

    public void saveReceivedMessage() {

    }

    public void saveFile(String filePath, byte[] byteArray, boolean isExiting) throws IOException {

        logger.info("From OutSide");
        for (byte b : byteArray) {
            System.out.print((char) b);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(byteArray, 0, byteArray.length);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        if (!isExiting) {
            Label infoMessage = new Label("File is successfully saved as " + filePath);
            ;
            infoMessage.setStyle("-fx-font-size:15;-fx-padding:10;");
            Label openLink = new Label("Click Here To Open");
            openLink.setCursor(Cursor.HAND);
            openLink.setStyle("-fx-font-size:15;-fx-padding:10;-fx-text-fill:#3498db;");
            openLink.setOnMouseClicked(new EventHandler<Event>() {

                @Override
                public void handle(Event event) {
                    try {
                        Runtime.getRuntime().exec("explorer.exe /select," + filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            VBox vbox = new VBox(infoMessage, openLink);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Congratulation");
            alert.getDialogPane().setContent(vbox);
            alert.show();
        }
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }


    public void showScene() {
        stage.show();
    }

    public void newSendMessage(PMessage createPMessage) {

    }
}
