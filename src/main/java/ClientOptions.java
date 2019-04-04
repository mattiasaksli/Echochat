import java.util.Scanner;

class ClientOptions {
    private boolean loggedin;

    void login() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Log into your account!");
        System.out.println("Enter your username: ");
        if (sc.hasNext()) {

            String username = sc.next();
            System.out.println("Enter your password: ");
            String password = sc.next();

            if (isFromDatabase(username, password)) {
                loginSuccessful();

            } else {
                loginTryAgain();
                String tryAgainOption = sc.next();
                if (tryAgainOption.equals("NO")) {
                    loggedin = false;
                } else
                    login();
            }
        }
    }

    void createNewAccount() {
        //TODO
        // ADD ACCOUNT TO DATABASE
        // add encryption to the password

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter new username: ");
        String newUsername = sc.next();
        System.out.println("Enter new password: ");
//        String newPassword = encryptData(sc.next());
        String a = sc.next();
        System.out.println("Account created successfully!");
        System.out.println(newUsername + a);

    }

    void welcome() {
        System.out.println("//////////////////////////////");
        System.out.println("What would you like to do?");
        System.out.println("Press 1 to log in");
        System.out.println("Press 2 to create new account!");
        System.out.println("Press 3 to connect to localhost!");
        System.out.println("Press 4 to connect to 3.17.78.222!");
        System.out.println("Press 5 to exit!");
        System.out.println("//////////////////////////////");
    }

    private void loginTryAgain() {
        System.out.println("Username or password is incorrect!");
        System.out.println("Would you like to try again?");
        System.out.println("(YES/NO)");
    }

    private void loginSuccessful() {
        System.out.println("LOGIN SUCCESSFUL!");
        loggedin = true;
    }

    private boolean isFromDatabase(String username, String password) {
//        return username.equals("Marten") && password.equals("ok");
        //RETURNING TRUE FOR TESTING PURPOSES
        return true;
    }

    boolean loggedIn() {
        return loggedin;
    }

    void exit() {
        System.out.println("Bye-bye!");
        System.exit(0);
    }
}
