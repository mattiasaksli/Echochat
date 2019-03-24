import java.util.Scanner;

public class ClientOptions {
    private static boolean loggedin;

    /*toDO
     * make new account
     *
     * */
    public static void welcome() {
        System.out.println("//////////////////////////////");
        System.out.println("What would you like to do?");
        System.out.println("Press 1 to log in");
        System.out.println("Press 2 to create new account!");
        System.out.println("Press 5 to exit!");
        System.out.println("//////////////////////////////");
    }

    public static boolean login() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Log into your account!");
            System.out.println("Enter your username: ");
            String username = sc.nextLine();
            System.out.println("Enter your password: ");
            String password = sc.nextLine();

            if (isFromDatabase(username, password)) {
                //TODO login successful
                System.out.println("lOGIN SUCCESSFUL!");
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
        }
    }

    private static boolean isFromDatabase(String username, String password) {
//        return username.equals("Marten") && password.equals("ok");
        //RETURNING TRUE FOR TESTING PURPOSES
        return true;
    }

    public static void exit() {
        System.out.println("Bye-bye!");
        System.exit(0);
    }

    public static void createNewAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter new username: ");
        String newUsername = sc.nextLine();
        System.out.println("Enter new password: ");
        String newPassword = sc.nextLine();

        //TODO ADD ACCOUNT TO DATABASE

        System.out.println("Account created successfully!");


    }

    static boolean loggedIn() {
        return loggedin;
    }
}
