import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThreadSocket implements Runnable {

    private final Socket socket;
    private final Argon2 argon2 = Argon2Factory.create();
    private String username;
    private String email;
    private Chatroom chatroom;
    private List<Chatroom> chatrooms;
    private List<String> users;
    private List<Chatroom> chatroomsParticipatingIn = new ArrayList<>();
    private Map<String, NotificationSender> userNotifier;
    private Map<String, Thread> userNotifierThread;

    ThreadSocket(List<Chatroom> chatrooms, List<String> users, Socket ss, Map<String, NotificationSender> userNotifier,
                 Map<String, Thread> userNotifierThread) {
        this.chatrooms = chatrooms;
        this.users = users;
        this.socket = ss;
        this.userNotifier = userNotifier;
        this.userNotifierThread = userNotifierThread;
    }

    @Override
    public void run() {
        try (socket;
             DataInputStream dataIn = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("client connected");

            while (true) {

                int type = Commands.getType(dataIn);

                if (type == MessageTypes.REGISTRATION_REQ.value()) {
                    int response = registerUser(dataIn);
                    dataOut.writeInt(response);
                    continue;
                }

                if (type == MessageTypes.LOGIN_REQ.value()) {
                    int response = loginUser(dataIn);
                    dataOut.writeInt(response);
                    continue;
                }

                if (type == MessageTypes.CHATROOMS_LIST_REQ.value()) {
                    sendChatroomsList(dataIn, dataOut);
                    continue;
                }

                if (type == MessageTypes.CHATROOM_CONNECT_USER.value()) {
                    int status = connectUserToChatroom(dataIn);
                    dataOut.writeInt(status);

                    if (status == MessageTypes.CHATROOMS_ROOM_FULL.value()) {
                        continue;
                    }

                    Message message = new Message(System.currentTimeMillis(), "EchoBot",
                            "[" + username.toUpperCase() + " connected to chatroom " +
                                    chatroom.getName().toUpperCase() + "]");
                    for (String key : chatroom.getUserAndMessages().keySet()) {
                        if (!key.equals(username)) {
                            chatroom.addMessageToUser(key, message);
                        }
                    }
                    writeMessageToFile(message);
                    System.out.println(message.getMessage());

                    continue;
                }

                if (type == MessageTypes.UPDATE_REQ.value()) {

                    if (!chatroom.getUserAndMessages().get(username).isEmpty()) {

                        List<Message> messageList = chatroom.getUserAndMessages().get(username);
                        int length = messageList.size();
                        dataOut.writeInt(length);

                        for (Message m : messageList) {
                            String message = m.getMessage();
                            long timestamp = m.getTimestamp();
                            String author = m.getAuthor();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                            Date resultDate = new Date(timestamp);
                            String messageServerSide = "[" + sdf.format(resultDate) + "] " + author + " >>> " + message;

                            System.out.println(messageServerSide);

                            Commands.writeMessage(dataOut, message, MessageTypes.TEXT.value(), false);
                            Commands.writeMessage(dataOut, String.valueOf(timestamp), MessageTypes.TEXT.value(), false);
                            Commands.writeMessage(dataOut, author, MessageTypes.TEXT.value(), false);
                        }

                        chatroom.getUserAndMessages().get(username).clear();

                    } else {
                        dataOut.writeInt(0);
                        continue;
                    }
                }

                if (type == MessageTypes.FILE_UPDATE_REQ.value()) {

                    String fileName = dataIn.readUTF();

                    System.out.println(fileName);

                    if (Files.exists(Path.of("file_storage", fileName))) {
                        Path filePath = Paths.get("file_storage\\" + fileName);
                        fileName = filePath.getFileName().toString();
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        Commands.writeFile(dataOut, fileName, fileBytes);
                        System.out.println("Sent file to " + username);
                    } else {
                        dataOut.writeInt(0);
                        continue;
                    }


                }

                if (type == MessageTypes.EXIT_CHATROOM.value()) {
                    Message message = new Message(System.currentTimeMillis(), "EchoBot",
                            "[" + username.toUpperCase() + " exited chatroom " +
                                    chatroom.getName().toUpperCase() + "]");
                    for (String key : chatroom.getUserAndMessages().keySet()) {
                        if (!key.equals(username)) {
                            chatroom.addMessageToUser(key, message);
                        }
                    }
                    writeMessageToFile(message);
                    System.out.println("user " + username + " exited chatroom " + chatroom.getName());
                    continue;
                }

                if (type == MessageTypes.END_SESSION.value()) {
                    break;
                }

                //////////////////// READ ACTUAL MESSAGE ///////////////////////

                if (type == MessageTypes.TEXT.value()) {

                    String clientMessage = CurseFilter.replaceCurseWordsWithAsterisks(dataIn.readUTF());

                    if (clientMessage.isBlank()) {
                        continue;
                    }

                    Message message = new Message(System.currentTimeMillis(), username, clientMessage);

                    //chatroom.addToMessageList(message);

                    for (String key : chatroom.getUserAndMessages().keySet()) {
                        if (!key.equals(username)) {
                            chatroom.addMessageToUser(key, message);
                        }
                    }

                    writeMessageToFile(message);

                    System.out.println(chatroom.getName() + " received message from user " + username + " : " + clientMessage + "\n");
                }

                if (type == MessageTypes.SEND_FILE.value()) {

                    byte[] fileMessage = Commands.readFile(dataIn);
                    String fileName = dataIn.readUTF();

                    FileMessage file = new FileMessage(System.currentTimeMillis(), username, fileMessage, fileName);

                    if (!Files.exists(Path.of("file_storage"))) {
                        if (!new File("file_storage").mkdir()) {
                            throw new RuntimeException("Failed to create folder file_storage!");
                        }
                    }

                    Files.write(Paths.get("file_storage", fileName), file.getFile());

                    Message message = new Message(System.currentTimeMillis(), "EchoBot", "A file " +
                            fileName + " was sent to the server. Use !getfile <filename> to retrieve it.");
                    for (String key : chatroom.getUserAndMessages().keySet()) {
                        if (!key.equals(username)) {
                            chatroom.addMessageToUser(key, message);
                        }
                    }

                    writeMessageToFile(message);

                    System.out.println("received file from " + username);

                }
            }

        } catch (Exception e) {
            Message message = new Message(System.currentTimeMillis(), "EchoBot",
                    "[" + "Connection lost to user " + username.toUpperCase() + "]");
            for (String key : chatroom.getUserAndMessages().keySet()) {
                if (!key.equals(username)) {
                    chatroom.addMessageToUser(key, message);
                }
            }
            try {
                writeMessageToFile(message);
            } catch (IOException ex) {
                System.out.println("could not write to file!");
            }
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }

        users.remove(username);
        System.out.println("ended connection with user " + username + " at " + socket);

        if (!email.equals("!NONE")) {
            NotificationSender ns = new NotificationSender();
            Thread noteSendThread = new Thread(ns);

            ns.setEmail(email);
            ns.setUsername(username);
            ns.setChatrooms(chatroomsParticipatingIn);
            noteSendThread.start();
            userNotifier.put(username, ns);
            userNotifierThread.put(username, noteSendThread);
            System.out.println("started email notification sender");
        }
    }

    private int registerUser(DataInputStream socketIn) throws IOException {
        String userName = socketIn.readUTF();
        String passWord = socketIn.readUTF();
        String email = socketIn.readUTF();

        String hash = argon2.hash(30, 65536, 1, passWord.toCharArray());

        if (!Files.exists(Path.of("credentials.txt"))) {
            Files.createFile(Path.of("credentials.txt"));
        }

        List<String> credentials = Files.readAllLines(Path.of("credentials.txt"));

        for (String credential : credentials) {
            String[] split = credential.split("\t");
            if (userName.equals(split[0])) {
                return MessageTypes.REGISTRATION_WRONG_USERNAME.value();
            }
            if (passWord.equals(split[1])) {
                return MessageTypes.REGISTRATION_WRONG_PASSWORD.value();
            }
        }

        Path path = Path.of("credentials.txt");
        String newUser = userName + "\t" + hash + "\t" + email;
        Files.write(path, Collections.singletonList(newUser), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        return MessageTypes.REGISTRATION_SUCCESS.value();
    }

    private int loginUser(DataInputStream socketIn) throws IOException, InterruptedException {
        String userName = socketIn.readUTF();
        String passWord = socketIn.readUTF();

        if (users.contains(userName)) {
            return MessageTypes.LOGIN_USER_ALREADY_IN.value();
        }

        if (!Files.exists(Path.of("credentials.txt"))) {
            return MessageTypes.LOGIN_MISSING_DB.value();
        }

        List<String> credentials = Files.readAllLines(Path.of("credentials.txt"));

        for (String credential : credentials) {
            String[] split = credential.split("\t");
            if (userName.equals(split[0])) {
                if (argon2.verify(split[1], passWord)) {
                    username = userName;
                    users.add(username);
                    email = split[2];
                    return MessageTypes.LOGIN_SUCCESS.value();
                } else {
                    return MessageTypes.LOGIN_WRONG_PASSWORD.value();
                }
            }
        }

        if (userNotifier.containsKey(username)) {
            userNotifier.get(username).stop();
            userNotifierThread.get(username).join();
        }

        return MessageTypes.LOGIN_WRONG_USERNAME.value();
    }

    private void sendChatroomsList(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
        File f = new File("chatrooms");
        var files = f.listFiles();
        if (files == null) {
            throw new IOException("failed to list " + f);
        }

        List<String> FileChatroomNames = new ArrayList<>();

        for (File file : files) {
            if (file.toString().endsWith("_users.txt")) {
                continue;
            }
            List<String> chatroomContents = Files.readAllLines(file.toPath());
            String chatroomName = chatroomContents.get(0);

            FileChatroomNames.add(chatroomName);
        }

        List<String> currentChatroomNames = new ArrayList<>();

        for (Chatroom cr : chatrooms) {
            currentChatroomNames.add(cr.getName());
        }

        for (String fileChatName : FileChatroomNames) {
            if (!currentChatroomNames.contains(fileChatName)) {
                int size;
                try (BufferedReader br = new BufferedReader(new FileReader(
                        new File(Path.of("chatrooms", fileChatName + "_users.txt").toString()),
                        StandardCharsets.UTF_8))) {
                    String s = br.readLine();
                    size = Integer.valueOf(s);
                }
                Chatroom newChatroom = new Chatroom(fileChatName,
                        Path.of("chatrooms", fileChatName + ".txt"), size);
                chatrooms.add(newChatroom);
            }
        }

        sendChatroomNamesToClient(dataIn, dataOut);
    }

    private void sendChatroomNamesToClient(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
        List<Chatroom> chatroomsToSend = new ArrayList<>();

        for (Chatroom cr : chatrooms) {
            int crSize = cr.getSize();
            int crUsers = cr.getUserAndMessages().size();
            if (crSize == 0 || crUsers < crSize) {
                chatroomsToSend.add(cr);
            }
        }

        int length = chatroomsToSend.size();
        dataOut.writeInt(length);
        if (length != 0) {
            for (Chatroom cr : chatroomsToSend) {
                dataOut.writeUTF(cr.getName());
            }
        }
        if (dataIn.readInt() == MessageTypes.CHATROOMS_LIST_SUCCESS.value()) {
            System.out.println("successfully sent chatrooms list to " + socket);
        }
    }

    private int connectUserToChatroom(DataInputStream dataIn) throws IOException {
        String chatroomName = dataIn.readUTF();

        int chatroomSize = 0;
        if (chatroomName.contains(";")) {
            String[] chatroomInfo = chatroomName.split(";");
            chatroomName = chatroomInfo[0];
            chatroomSize = Integer.parseInt(chatroomInfo[1]);
        }

        boolean isChatroomInList = false;

        for (Chatroom cr : chatrooms) {
            if (cr.getName().equals(chatroomName)) {
                int crSize = cr.getSize();
                int crUsers = cr.getUserAndMessages().size();

                if (crSize != 0 && crSize == crUsers) {
                    return MessageTypes.CHATROOMS_ROOM_FULL.value();
                }

                this.chatroom = cr;

                List<String> chatroomContents = Files.readAllLines(Path.of("chatrooms", chatroomName + ".txt"));

                int messagesToAdd = chatroom.getMessageList().size();
                for (int i = 1 + messagesToAdd; i < chatroomContents.size(); i++) {
                    String[] messageInfo = chatroomContents.get(i).split("\t");
                    long timestamp = Long.valueOf(messageInfo[0]);
                    String author = messageInfo[1];
                    String text = messageInfo[2];
                    Message message = new Message(timestamp, author, text);
                    chatroom.addToMessageList(message);
                }


                chatroom.addUserToChatroom(username);
                isChatroomInList = true;
                break;
            }
        }

        if (!isChatroomInList) {
            createChatroom(chatroomName, chatroomSize);
        }

        Path path = Path.of("chatrooms", chatroomName + "_users.txt");
        List<String> usernames = Files.readAllLines(path);
        usernames.remove(0);

        if (!usernames.contains(username)) {
            Files.write(path, Collections.singletonList(username),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }

        if (!chatroomsParticipatingIn.contains(chatroom)) {
            chatroomsParticipatingIn.add(chatroom);
        }

        return MessageTypes.CHATROOMS_USER_CONNECTED.value();
    }

    private void createChatroom(String chatroomName, int size) throws IOException {
        Path path = Path.of("chatrooms", chatroomName + ".txt");
        Files.createFile(path);

        Files.write(path, Collections.singletonList(chatroomName), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);

        Path usersPath = Path.of("chatrooms", chatroomName + "_users.txt");
        Files.createFile(usersPath);
        Files.write(usersPath, Collections.singletonList(String.valueOf(size)), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);

        Chatroom cr = new Chatroom(chatroomName, path, size);
        this.chatroom = cr;
        chatrooms.add(cr);
        chatroom.addUserToChatroom(username);
    }

    private void writeMessageToFile(Message message) throws IOException {
        String line = message.getTimestamp() + "\t" + message.getAuthor() + "\t" + message.getMessage();
        Files.write(chatroom.getPath(), Collections.singletonList(line), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
    }

}