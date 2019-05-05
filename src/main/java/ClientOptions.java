import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ClientOptions {
    private boolean displayLogo = true;
    private boolean loggedIn;
    private boolean accountCreated;
    private String username;
    private boolean ttsState = false;

    void welcome() {
        if (loggedIn) {
            System.out.println("///////////////////////////////////");
            System.out.println("What would you like to do?");
            System.out.println("Press 3 to connect to a chatroom!");
            System.out.println("Press 0 to exit!");
            System.out.println("///////////////////////////////////");
        } else {
            if (displayLogo) {
                System.out.println("        __________     ______       ______                       ");
                System.out.println("        ___  ____/________  /__________  /____________  _________");
                System.out.println("        __  __/  _  ___/_  __ \\  __ \\_  __ \\  __ \\_  / / /_  ___/");
                System.out.println("        _  /___  / /__ _  / / / /_/ /  /_/ / /_/ /  /_/ /_(__  ) ");
                System.out.println("        /_____/  \\___/ /_/ /_/\\____//_.___/\\____/_\\__, / /____/  ");
                System.out.println("                                                 /____/          ");
                displayLogo = false;
            }
            System.out.println("///////////////////////////////////");
            System.out.println("What would you like to do?");
            System.out.println("Press 1 to log in");
            System.out.println("Press 2 to create new account!");
            System.out.println("Press 0 to exit!");
            System.out.println("///////////////////////////////////");
        }
    }

    void createNewAccount(Scanner sc, DataInputStream dataIn, DataOutputStream dataOut) throws Exception {

        while (!accountCreated) {

            System.out.println("Create a new account!\n");

            System.out.println("Enter new username: ");
            String newUsername = sc.next();
            System.out.println("Enter new password: ");
            String newPassword = sc.next();

            int type = MessageTypes.REGISTRATION_REQ.value();
            int status = sendUserInfo(dataIn, dataOut, type, newUsername, newPassword);

            if (status == MessageTypes.REGISTRATION_SUCCESS.value()) {
                System.out.println("\nAccount created successfully!\n");
                accountCreated = true;
                break;
            } else {
                if (status == MessageTypes.REGISTRATION_WRONG_USERNAME.value()) {
                    System.out.println("\nThis username already exists!\n");
                } else if (status == MessageTypes.REGISTRATION_WRONG_PASSWORD.value()) {
                    System.out.println("\nPlease choose a different password!\n");
                }
                if (dontTryAgain(sc)) {
                    break;
                }
            }
        }
    }

    void login(Scanner sc, DataInputStream dataIn, DataOutputStream dataOut) throws Exception {

        while (!loggedIn) {

            System.out.println("Log into your account!\n");

            System.out.println("Enter your username: ");
            this.username = sc.next();

            System.out.println("Enter your password: ");
            String passWord = sc.next();

            int type = MessageTypes.LOGIN_REQ.value();
            int status = sendUserInfo(dataIn, dataOut, type, username, passWord);

            if (status == MessageTypes.LOGIN_SUCCESS.value()) {
                System.out.println("\nLogin successful!\n");
                loggedIn = true;
                accountCreated = true;
                break;
            } else {
                if (status == MessageTypes.LOGIN_WRONG_USERNAME.value()) {
                    System.out.println("\nUsername is incorrect!\n");
                } else if (status == MessageTypes.LOGIN_WRONG_PASSWORD.value()) {
                    System.out.println("\nPassword is incorrect!\n");
                } else if (status == MessageTypes.LOGIN_USER_ALREADY_IN.value()) {
                    System.out.println("\nThat account is already logged in!\n");
                } else if (status == MessageTypes.LOGIN_MISSING_DB.value()) {
                    System.out.println("\nRegister an account first!\n");
                    break;
                }
                if (dontTryAgain(sc)) {
                    break;
                }
            }
        }
    }

    private int sendUserInfo(DataInputStream dataIn, DataOutputStream dataOut, int type, String userName, String passWord) throws IOException {
        dataOut.writeInt(type);
        dataOut.writeUTF(userName);
        dataOut.writeUTF(passWord);

        return dataIn.readInt();
    }

    private boolean dontTryAgain(Scanner sc) {
        String tryAgainOption;
        do {
            System.out.println("Would you like to try again?");
            System.out.println("(Y/N)");
            tryAgainOption = sc.next();
        } while (!tryAgainOption.equals("N") && !tryAgainOption.equals("Y"));
        System.out.println();
        return tryAgainOption.equals("N");
    }

    private List<String> getChatroomNames(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
        dataOut.writeInt(MessageTypes.CHATROOMS_LIST_REQ.value());

        int length = dataIn.readInt();
        List<String> chatrooms = new ArrayList<>();
        if (length != 0) {
            for (int i = 0; i < length; i++) {
                String chatroomName = dataIn.readUTF();
                chatrooms.add(chatroomName);
            }
        }
        dataOut.writeInt(MessageTypes.CHATROOMS_LIST_SUCCESS.value());

        return chatrooms;
    }

    void connectToChatroom(ClientOptions clientOptions, Scanner sc,
                           DataInputStream dataIn, DataOutputStream dataOut) throws Exception {

        List<String> chatrooms = getChatroomNames(dataIn, dataOut);

        String username = clientOptions.getUsername();
        String chatroomName = "";

        if (chatrooms.size() == 0) {
            System.out.println("\nNo chatrooms available! Would you like to create one?");
            String response = "";
            while (!response.equals("Y") && !response.equals("N")) {
                System.out.println("Y/N");
                response = sc.next();
            }

            if (response.equals("N")) {
                return;
            }

            chatroomName = createChatroom(sc);
        }

        while (chatroomName.equals("")) {

            System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("Please choose a chatroom from the list or create a new one (CREATE)!\n");
            for (int i = 1; i <= chatrooms.size(); i++) {
                System.out.println("* " + chatrooms.get(i - 1));
            }

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            chatroomName = sc.next();

            if (chatroomName.equals("CREATE")) {
                chatroomName = createChatroom(sc);
            } else if (!chatrooms.contains(chatroomName)) {
                System.out.println("\nInvalid input!");
                chatroomName = "";
                if (dontTryAgain(sc)) {
                    return;
                }
            }
        }

        Commands.writeChatroomName(dataOut, chatroomName);

        if (dataIn.readInt() == MessageTypes.CHATROOMS_USER_CONNECTED.value()) {
            System.out.println("\n" + username + " connected to " + chatroomName + "!");
        }

        Thread update = new Thread(new Update(dataOut, dataIn));
        update.start();

        /*Thread fileUpdate = new Thread(new FileUpdate(dataOut, dataIn));
        fileUpdate.start();*/

        int type = MessageTypes.TEXT.value();

//        System.out.println("Start chatting!\n");
//        System.out.println("Type 'TTS enable' to enable text to speech");
//        System.out.println("and type 'TTS disable' to disable it");

        if (type == MessageTypes.TEXT.value()) {

            while (sc.hasNext()) {

                String input = sc.nextLine().trim();

                if (input.equals("END")) {
                    Commands.writeEnd(dataOut);
                    break;
                }
//                if (input.equals("TTS enable")) {
//                    ttsState = true;
//                    System.out.println("TTS enabled");
//                }
//                if (input.equals("TTS disable")) {
//                    ttsState = false;
//                    System.out.println("TTS disabled");
//                }

                if (input.startsWith("!file")) {
                    String[] getFile = input.split(" ");
                    String fileName = "";

                    try {
                        Path filePath = Paths.get(getFile[1]);
                        fileName = filePath.getFileName().toString();
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        Commands.writeFile(dataOut, fileName, fileBytes);
                    } catch (IndexOutOfBoundsException e ) {
                        System.out.println("Write !file <file name> to send file");
                        continue;
                    } catch (NoSuchFileException e) {
                        System.out.println("File " + fileName + " does not exist, try again");
                        continue;
                    }

                    System.out.println("File sent");
                    continue;
                }

                Commands.writeMessage(dataOut, input, type, true);
            }
        }

        System.out.println("\nExited chatroom\n");
    }

    private String createChatroom(Scanner sc) {
        String chatroomName = "";
        while (chatroomName.equals("")) {
            System.out.println("\nPlease enter a chatroom name (no whitespace): ");
            chatroomName = sc.next();

            if (chatroomName.equals("CREATE")) {
                System.out.println("\nThat is not a valid name!");
                chatroomName = "";
            }
        }
        return chatroomName;
    }

    boolean loggedIn() {
        return loggedIn;
    }

    private String getUsername() {
        return username;
    }

    boolean isAccountCreated() {
        return accountCreated;
    }

    boolean getTtsState() {
        return ttsState;
    }
}