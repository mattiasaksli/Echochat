import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class clientOptions {
    private boolean loggedin;
    final Argon2 argon2 = Argon2Factory.create();

    /*TODO
     * make new account
     * Send account information to the server
     * */
    public void welcome() {
        System.out.println("//////////////////////////////");
        System.out.println("What would you like to do?");
        System.out.println("Press 1 to log in");
        System.out.println("Press 2 to create new account!");
        System.out.println("Press 5 to exit!");
        System.out.println("//////////////////////////////");
    }

    boolean login() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Log into your account!");
            System.out.println("Enter your username: ");
            String username = sc.nextLine();
            System.out.println("Enter your password: ");
            String password = sc.nextLine();

            if (isFromDatabase(username, password)) {
                //TODO login successful
                System.out.println("LOGIN SUCCESSFUL!");
                loggedin = true;
                return true;

            } else {
                System.out.println("Username or password is incorrect!");
                System.out.println("Would you like to try again?");
                System.out.println("(YES/NO)");
                String option = sc.nextLine();
                if (option.equals("NO")) {
                    loggedin = false;
                    return false;
                }
            }
            sc.close();
        }
    }

    private boolean isFromDatabase(String username, String password) {
//        return username.equals("Marten") && password.equals("ok");
        //RETURNING TRUE FOR TESTING PURPOSES
        return true;
    }

    void createNewAccount() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter new username: ");
        String newUsername = sc.nextLine();
        System.out.println("Enter new password: ");
        String newPassword = encryptData(sc.nextLine());

        System.out.println("Account created successfully!");

        //TODO
        // ADD ACCOUNT TO DATABASE
        // add encryption to the password

        sc.close();
    }


    private String encryptData(String password) {
        return argon2.hash(30, 65536, 1, password.toCharArray());
    }



    boolean loggedIn() {
        return loggedin;
    }

    void exit() {
        System.out.println("Bye-bye!");
        System.exit(0);
    }

}
