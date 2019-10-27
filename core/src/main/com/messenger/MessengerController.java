package com.messenger;
import com.messages.PMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;

import java.util.Random;
import java.util.ResourceBundle;

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
    @FXML
    private Pane topPane;

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


    public void generateAnimation(){
        Random rand = new Random();
        int sizeOfSquare = rand.nextInt(30) + 1;
        int speedOfSquare = rand.nextInt(10) + 5;
        int startXPoint = rand.nextInt(500) + 300;
        int startYPoint = rand.nextInt(40) + 10;
        int direction = rand.nextInt(5) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle r1 = null;

        switch (direction){
            case 1 :
                // MOVE LEFT TO RIGHT
                r1 = new Rectangle(0,startYPoint,sizeOfSquare,sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 1040 -  sizeOfSquare);
                break;
            case 2 :
                // MOVE TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSquare,sizeOfSquare);
                moveYAxis = new KeyValue(r1.yProperty(), 60 - sizeOfSquare);
                break;
            case 3 :
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSquare,sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 1040 -  sizeOfSquare);
                moveYAxis = new KeyValue(r1.yProperty(), 80 - sizeOfSquare);
                break;
            case 4 :
                // MOVE BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,80-sizeOfSquare ,sizeOfSquare,sizeOfSquare);
                moveYAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 5 :
                // MOVE RIGHT TO LEFT
                r1 = new Rectangle(1040-sizeOfSquare,startYPoint,sizeOfSquare,sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 6 :
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,0,sizeOfSquare,sizeOfSquare);
                moveXAxis = new KeyValue(r1.xProperty(), 1040 -  sizeOfSquare);
                moveYAxis = new KeyValue(r1.yProperty(), 80 - sizeOfSquare);
                break;

            default:
                System.out.println("default");
        }

        r1.setFill(Color.web("#FDFFFC"));
        r1.setOpacity(0.2);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speedOfSquare * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        try {
            if (r1.getX()+r1.getWidth() <= 600 && r1.getY() + r1.getHeight() <= 48){
                topPane.getChildren().add(topPane.getChildren().size() - 1, r1);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
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

        int numberOfSquares = 60;
        while (numberOfSquares > 0){
            generateAnimation();
            numberOfSquares--;
        }
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
