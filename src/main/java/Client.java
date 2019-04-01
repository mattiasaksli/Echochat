import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        ClientOptions.welcome();

        while (sc.hasNextLine()) {
            int chooseOption = Integer.parseInt(sc.next());

            // LOG IN
            if (chooseOption == 1) {
                ClientOptions.login();
                if (ClientOptions.loggedIn())
                    connectToServer();
                else
                    ClientOptions.welcome();

            //CREATE NEW ACCOUNT
            } else if (chooseOption == 2) {
                ClientOptions.createNewAccount();
                ClientOptions.login();
                if (ClientOptions.loggedIn())
                    connectToServer();

            // EXIT THE PROGRAM
            } else if (chooseOption == 5) {
                ClientOptions.exit();
                break;
            }
        }
    }

    private static void connectToServer() throws Exception {
        System.out.println("connecting to server");
        try (Socket socket = new Socket("localhost", 1337);
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

            if (type == 1) {

                while (sc.hasNextLine()) {

                    String toSend = sc.nextLine();

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

}