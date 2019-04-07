import java.util.Scanner;

class ClientOptions {
    private boolean loggedin;
    private String username;

    void welcome() {
        if (loggedin) {
            System.out.println("//////////////////////////////");
            System.out.println("What would you like to do?");
            System.out.println("Press 3 to connect to localhost!");
            System.out.println("Press 4 to connect to 3.17.78.222!");
            System.out.println("Press 5 to exit!");
            System.out.println("//////////////////////////////");
        } else {
            System.out.println("//////////////////////////////");
            System.out.println("What would you like to do?");
            System.out.println("Press 1 to log in");
            System.out.println("Press 2 to create new account!");
            System.out.println("Press 3 to connect to localhost!");
            System.out.println("Press 4 to connect to 3.17.78.222!");
            System.out.println("Press 5 to exit!");
            System.out.println("//////////////////////////////");
        }
    }

    void createNewAccount(Scanner sc) throws Exception {
        //TODO
        // ADD ACCOUNT TO DATABASE
        // add encryption to the password

        System.out.println("Enter new username: ");
        String newUsername = sc.next();
        System.out.println("Enter new password: ");
        String newPassword = sc.next();

        int status = connectToServer(newUsername, newPassword, MessageTypes.REGISTRATION_REQ.getValue());

        if (status == MessageTypes.REGISTRATION_SUCCESS.getValue()) {
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Something went wrong!");
        }
    }

    void login(Scanner sc) throws Exception {
        System.out.println("Log into your account!");

        while (!loggedin) {

            System.out.println("Enter your username: ");

            if (sc.hasNext()) {
                username = sc.next();
                System.out.println("Enter your password: ");
                String password = sc.next();

                int status = connectToServer(username, password, MessageTypes.LOGIN_REQ.getValue());

                if (status == MessageTypes.LOGIN_SUCCESS.getValue()) {
                    System.out.println("LOGIN SUCCESSFUL!");
                    loggedin = true;
                    break;

                } else {
                    System.out.println("Username or password is incorrect!");
                    System.out.println("Would you like to try again?");
                    System.out.println("(YES/NO)");
                    String tryAgainOption = sc.next();
                    if (tryAgainOption.equals("NO")) {
                        break;
                    }
                }
            }
        }
    }

    private int connectToServer(String username, String password, int type) throws Exception {



        //RETURNING TRUE FOR TESTING PURPOSES
        if (type == MessageTypes.REGISTRATION_REQ.getValue()) {
            return MessageTypes.REGISTRATION_SUCCESS.getValue();
        }
        return MessageTypes.LOGIN_SUCCESS.getValue();
    }

    boolean loggedIn() {
        return loggedin;
    }

    String getUsername() {
        return username;
    }

    void exit() {
        System.out.println("Bye-bye!");
        System.exit(0);
    }
}
