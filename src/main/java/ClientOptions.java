import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

class ClientOptions {
    private boolean displayLogo = true;
    private boolean loggedin;
    private boolean accountCreated;
    private String username;

    void welcome() {
        if (loggedin) {
            System.out.println("//////////////////////////////");
            System.out.println("What would you like to do?");
            System.out.println("Press 3 to connect to a chatroom!");
            System.out.println("Press 0 to exit!");
            System.out.println("//////////////////////////////");
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
            System.out.println("//////////////////////////////");
            System.out.println("What would you like to do?");
            System.out.println("Press 1 to log in");
            System.out.println("Press 2 to create new account!");
            System.out.println("Press 0 to exit!");
            System.out.println("//////////////////////////////");
        }
    }

    void createNewAccount(Scanner sc, DataInputStream dataIn, DataOutputStream dataOut) throws Exception {

        while (true) {

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

        while (!loggedin) {

            System.out.println("Log into your account!\n");

            System.out.println("Enter your username: ");
            this.username = sc.next();

            System.out.println("Enter your password: ");
            String passWord = sc.next();

            int type = MessageTypes.LOGIN_REQ.value();
            int status = sendUserInfo(dataIn, dataOut, type, username, passWord);

            if (status == MessageTypes.LOGIN_SUCCESS.value()) {
                System.out.println("\nLogin successful!\n");
                loggedin = true;
                accountCreated = true;
                break;
            } else {
                if (status == MessageTypes.LOGIN_WRONG_USERNAME.value()) {
                    System.out.println("\nUsername is incorrect!\n");
                } else if (status == MessageTypes.LOGIN_WRONG_PASSWORD.value()) {
                    System.out.println("\nPassword is incorrect!\n");
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
        return tryAgainOption.equals("N");
    }

    void connectToChatroom(ClientOptions clientOptions, Scanner sc,
                           DataInputStream dataIn, DataOutputStream dataOut) throws Exception {

        String username = clientOptions.getUsername();

        System.out.println("\nTo which chatroom would you like to connect?");

        // TODO List of chatroomNames to choose from

        String chatroomName = sc.next();

        Commands.writeChatroomName(dataOut, username, chatroomName);

        /*Commands.writeUserToMap(dataOut, username);*/

        Thread update = new Thread(new Update(dataOut, dataIn, username, chatroomName));
        update.start();

        int type = MessageTypes.TEXT.value();

        System.out.println("\n" + username + " connected to " + chatroomName + "\n");

        if (type == MessageTypes.TEXT.value()) {

            while (sc.hasNext()) {

                String input = sc.nextLine();
                String toSend = username + ": " + input;

                if (input.equals("END")) {
                    Commands.writeEnd(dataOut);
                    break;
                }

                Commands.messageAuthor(dataOut, username);
                Commands.writeMessage(dataOut, toSend, type, true);
            }
        }

        //TODO implement file transferring


        System.out.println("\nExited chatroom\n");
    }

    boolean loggedIn() {
        return loggedin;
    }

    private String getUsername() {
        return username;
    }

    boolean isAccountCreated() {
        return accountCreated;
    }
}