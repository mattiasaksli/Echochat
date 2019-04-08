import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
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
            System.out.println("        __________     ______       ______                       ");
            System.out.println("        ___  ____/________  /__________  /____________  _________");
            System.out.println("        __  __/  _  ___/_  __ \\  __ \\_  __ \\  __ \\_  / / /_  ___/");
            System.out.println("        _  /___  / /__ _  / / / /_/ /  /_/ / /_/ /  /_/ /_(__  ) ");
            System.out.println("        /_____/  \\___/ /_/ /_/\\____//_.___/\\____/_\\__, / /____/  ");
            System.out.println("                                                 /____/          ");
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

        System.out.println("Create a new account!");

        while (true) {

            System.out.println("Enter new username: ");
            String newUsername = sc.next();
            System.out.println("Enter new password: ");
            String newPassword = sc.next();

            int status = connectToServer(newUsername, newPassword, MessageTypes.REGISTRATION_REQ.value());

            if (status == MessageTypes.REGISTRATION_SUCCESS.value()) {
                System.out.println("Account created successfully!");
                break;
            } else {
                if (status == MessageTypes.REGISTRATION_WRONG_USERNAME.value()) {
                    System.out.println("\nThis username already exists!\n");
                } else if (status == MessageTypes.REGISTRATION_WRONG_PASSWORD.value()) {
                    System.out.println("\nPlease choose a different password!\n");
                }
                String tryAgainOption;
                do {
                    System.out.println("Would you like to try again?");
                    System.out.println("(Y/N)");
                    tryAgainOption = sc.next();
                } while (!tryAgainOption.equals("N") && !tryAgainOption.equals("Y"));
                if (tryAgainOption.equals("N")) {
                    break;
                }
            }
        }
    }

    void login(Scanner sc) throws Exception {

        System.out.println("Log into your account!");

        while (!loggedin) {

            System.out.println("Enter your username: ");

            username = sc.next();
            System.out.println("Enter your password: ");
            String password = sc.next();

            int status = connectToServer(username, password, MessageTypes.LOGIN_REQ.value());

            if (status == MessageTypes.LOGIN_SUCCESS.value()) {
                System.out.println("LOGIN SUCCESSFUL!");
                loggedin = true;
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
                String tryAgainOption;
                do {
                    System.out.println("Would you like to try again?");
                    System.out.println("(Y/N)");
                    tryAgainOption = sc.next();
                } while (!tryAgainOption.equals("N") && !tryAgainOption.equals("Y"));
                if (tryAgainOption.equals("N")) {
                    break;
                }
            }
        }
    }

    private int connectToServer(String userName, String passWord, int type) throws Exception {

        int response;

        String host = "localhost";
        int port = 1337;

        ClassLoader cl = Client.class.getClassLoader();
        String storePass = "secret";
        KeyStore store;
        try (InputStream keyIn = cl.getResourceAsStream("truststore.p12")) {
            store = KeyStore.getInstance("PKCS12");
            store.load(keyIn, storePass.toCharArray());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(store);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustManagers, null);

        try (Socket socket = ctx.getSocketFactory().createSocket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             DataInputStream dataIn = new DataInputStream(in)) {

            dataOut.writeInt(type);
            dataOut.writeUTF(userName);
            dataOut.writeUTF(passWord);

            response = dataIn.readInt();
        }

        return response;
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
