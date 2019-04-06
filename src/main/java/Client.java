import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        ClientOptions clientOptions = new ClientOptions();
        whatWouldYouLikeToDo(clientOptions);
    }

    private static void connectToServer(String host) throws Exception {

        int port = 1337;

        System.out.println("connecting to the awesome server");

        ClassLoader cl = Client.class.getClassLoader();
        InputStream keyIn = cl.getResourceAsStream("truststore.p12");
        String storePass = "secret";

        KeyStore store = KeyStore.getInstance("PKCS12");
        store.load(keyIn, storePass.toCharArray());
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

            Thread update = new Thread(new Update(dataOut, dataIn));
            update.start();

            //Path saveToPath;
            //int type = Integer.parseInt(args[0]);
            int type = 1;

            System.out.println("connected; sending data");

            Scanner sc = new Scanner(System.in);

            if (type == 1) {

                while (sc.hasNext()) { //merge-conflict: was true

                    String toSend = sc.next();

                    if ((toSend.equals("END"))) {
                        Commands.writeEnd(dataOut);
                        break;

                    } /*else if ((toSend.equals("update"))) {
                        Commands.writeUpdateRequest(dataOut);
                        int gotType = Commands.getType(dataIn);
                        String message = Commands.readMessage(dataIn, gotType);
                        System.out.print(message);
                    }*/

                    Commands.writeMessage(dataOut, toSend, type, true);

                    //Commands.getType(dataIn);
                    //String clientMessageEcho = Commands.readMessage(dataIn, type);
                    //System.out.println("received " + message);

                }

            }
            /*else if (type == 2) {
                String path = args[1];
                saveToPath = Paths.get(args[2]);

                Commands.writeMessage(dataOut, path, 2, true);

                int checkType = Commands.getType(dataIn);

                if (checkType == 3) {
                    throw new IllegalArgumentException("error, server-relative path needed");

                } else if (checkType == 2) {
                    byte[] file = Commands.readFile(dataIn, checkType);
                    Files.write(saveToPath, file);
                    System.out.println("received" + saveToPath.toString());
                    System.out.println("File size: \n" + new File(args[2]).length() + " bytes\n");
                    System.out.println("File was written to: " + saveToPath);

                } else {
                    throw new IllegalArgumentException("error, could not find file");
                }



            }*/
        }

        System.out.println("finished");
        System.out.println();

    }

    private static void whatWouldYouLikeToDo(ClientOptions clientOptions) throws Exception {

        int chosenOption;
        Scanner sc1 = new Scanner(System.in);
        clientOptions.welcome();

        try {
            if (sc1.hasNext()) {
                chosenOption = Integer.parseInt(sc1.next());
                if (chosenOption == 1) {
                    optionLogin(clientOptions);
                } else if (chosenOption == 2) {
                    optionCreateNewAccount(clientOptions);
                } else if (chosenOption == 3) {
                    optionConnectToLocal(clientOptions);
                } else if (chosenOption == 4) {
                    optionConnectToEC2(clientOptions);
                } else if (chosenOption == 5) {
                    optionExit(clientOptions);
                } else if (chosenOption == 6) {
                    //TODO Add options
                } else if (chosenOption == 7) {
                    //TODO Add options
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("What would you like to do?");
        }
    }

    private static void optionConnectToEC2(ClientOptions clientOptions) throws Exception {
        if (clientOptions.loggedIn())
            connectToServer("3.17.78.222");
        else {
            System.out.println("You must be logged in first!");
            whatWouldYouLikeToDo(clientOptions);
        }
    }

    private static void optionConnectToLocal(ClientOptions clientOptions) throws Exception {
        if (clientOptions.loggedIn())
            connectToServer("localhost");
        else {
            System.out.println("You must be logged in first!");
            whatWouldYouLikeToDo(clientOptions);
        }

    }

    private static void optionExit(ClientOptions clientOptions) {

        clientOptions.exit();
    }

    private static void optionCreateNewAccount(ClientOptions clientOptions) throws Exception {
        clientOptions.createNewAccount();
        clientOptions.login();
        whatWouldYouLikeToDo(clientOptions);
    }

    private static void optionLogin(ClientOptions clientOptions) throws Exception {
        if (clientOptions.loggedIn()) {
            System.out.println("You are already logged in!");
            whatWouldYouLikeToDo(clientOptions);
        } else {
            clientOptions.login();
            whatWouldYouLikeToDo(clientOptions);
        }


    }
}