package com.lobby.community;

import com.lobby.login.LoginController;
import com.messages.*;
import com.messenger.MessageReceiver;
import com.messenger.MessageSender;
import com.messenger.MessengerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.messages.MessageType.CONNECTED;

public class Listener implements Runnable{

    private static final String HASCONNECTED = "has connected";
    private static final String COMMUNITY = "#Community";
    private static final String COMMUNITY_IMAGE = "images/alphabet/#.png";
    private static Random random;

    private static HashMap<String, Integer> peers = new HashMap<>();

    public static User community;
    private static String picture;
    private Socket socket;
    public static String hostname;
    public int port;
    public static String username;
    public ChatController controller;
    private static ObjectOutputStream oos;
    public static String channel;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;
    Logger logger = LoggerFactory.getLogger(Listener.class);

    public Listener(String hostname, int port, String username, String picture, ChatController controller) {
        this.port = port;
        this.controller = controller;
        Listener.random = new Random();
        Listener.hostname = hostname;
        Listener.username = username;
        Listener.picture = picture;
        Listener.community = new User(COMMUNITY, COMMUNITY_IMAGE, "ONLINE");
        Listener.channel = "#Community";
    }




    public void run() {
        try {
            socket = new Socket(hostname, port);
            LoginController.getInstance().showScene();
            outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            is = socket.getInputStream();
            input = new ObjectInputStream(is);
        } catch (IOException e) {
            LoginController.getInstance().showErrorDialog("Could not connect to server");
            logger.error("Could not Connect");
        }
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            connect();
            logger.info("Sockets in and out ready!");
            while (socket.isConnected()) {
                Message message = null;
                message = (Message) input.readObject();

                if (message != null) {
                    logger.debug("Message recieved:" + message.getMsg() + " MessageType:" + message.getType() + " Name:" + message.getName() + " Channel: " + message.getChannel());
                    switch (message.getType()) {
                        case USER:
                            controller.addToChat(message);
                            break;
                        case VOICE:
                            logger.info(message.getType() + " - " + message.getVoiceMsg().length);
                            controller.addToChat(message);
                            break;
                        case NOTIFICATION:
                            controller.newUserNotification(message);
                            break;
                        case SERVER:
                            controller.addAsServer(message);
                            break;
                        case CONNECTED:
                            controller.setUserList(message);
                            break;
                        case DISCONNECTED:
                            controller.setUserList(message);
                            break;
                        case STATUS:
                            controller.setUserList(message);
                            break;
                        case PICTURE:
                            controller.addToChat(message);
                            break;
                        case OPENP2P:
                            if (!peers.containsKey(message.getName()))
                                openP2PConnection(message.getName());
                            waitForConnection(message);
                            break;
                        case CLOSEP2P:
                            if (peers.containsKey(message.getName()))
                                closeP2PConnection(message.getName());
                            controller.closeMessenger(message);
                        case CHANNEL:
                            break;
                    }
                }
            }
            logger.info("Disconnected");
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            controller.logoutScene();
        }

    }

    private void waitForConnection(Message message) throws IOException {
        controller.openMessenger(message,
                new MessageSender(message.getPeer().getSourceHost(), peers.get(message.getName())),
                new MessageReceiver(message.getPeer().getSourcePort()));
    }

    public static void closeP2PConnection(String name) throws IOException {
        System.out.println(("Close P2P connection to " + name));
        if (peers.containsKey(name)) {
            Peer peer = new Peer();
            peer.setName(name);
            peer.setSourceHost(hostname);
            peer.setSourcePort(peers.get(name));

            Message createMessage = new Message();
            createMessage.setName(username);
            createMessage.setType(MessageType.CLOSEP2P);
            createMessage.setPeer(peer);
            oos.writeObject(createMessage);
            oos.flush();

            peers.remove(name);
        }
    }



    public static void openP2PConnection(String name) throws IOException {
        System.out.println(("Open P2P connection to " + name));
        if (!peers.containsKey(name)) {
            int randPort;
            do randPort = random.nextInt(11111) + 11111; while (peers.containsValue(randPort));
            Peer peer = new Peer();
            peer.setName(name);
            peer.setSourcePort(randPort);

            peers.put(name,randPort);

            Message createMessage = new Message();
            createMessage.setName(username);
            createMessage.setType(MessageType.OPENP2P);
            createMessage.setPeer(peer);
            oos.writeObject(createMessage);
            oos.flush();
        }
    }


    public static void sendPicture(byte[] base64Image) throws IOException{
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.PICTURE);
        createMessage.setStatus(Status.AWAY);
        createMessage.setPictureMsg(base64Image);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /*
    *   This method is used for sending message update channel
    *   @param msg -
     */
    public static void sendChannelUpadte(String updatedChannel) throws IOException {
        channel = updatedChannel;
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.CHANNEL);
        createMessage.setChannel(updatedChannel);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used for sending a normal Message
     * @param msg - The message which the user generates
     */
    public static void send(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.USER);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used for sending a voice Message
 * @param msg - The message which the user generates
 */
    public static void sendVoiceMessage(byte[] audio) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.VOICE);
        createMessage.setStatus(Status.AWAY);
        createMessage.setVoiceMsg(audio);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used for sending a normal Message
 * @param msg - The message which the user generates
 */
    public static void sendStatusUpdate(Status status) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.STATUS);
        createMessage.setStatus(status);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used to send a connecting message */
    public static void connect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(CONNECTED);
        createMessage.setMsg(HASCONNECTED);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
    }

}
