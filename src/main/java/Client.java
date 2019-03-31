import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        clientOptions clientOptions = new clientOptions();
        clientOptions.welcome();
        Scanner sc1 = new Scanner(System.in);

        while (true) {
            int chooseOption = Integer.parseInt(sc1.next());

            if (chooseOption == 1) {            // LOG IN
                clientOptions.login();
                if (clientOptions.loggedIn())
                    connectToServer();
                else
                    clientOptions.welcome();

            } else if (chooseOption == 2) {     //CREATE NEW ACCOUNT
                clientOptions.createNewAccount();
                clientOptions.login();
                if (clientOptions.loggedIn())
                    connectToServer();

            } else if (chooseOption == 3) {
                //TODO Add options
                break;

            } else if (chooseOption == 4) {
                //TODO Add options
                break;

            } else if (chooseOption == 5) {     // EXIT THE PROGRAM
                clientOptions.exit();
                break;

            } else if (chooseOption == 6) {
                //TODO Add options
                break;

            } else if (chooseOption == 7) {
                //TODO Add options
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

            int type = 1;

            System.out.println("connected; sending data");

            Scanner sc = new Scanner(System.in);

            if (type == 1) {

                while (true) {

                    String toSend = sc.nextLine();

                    if ((toSend.equals("END"))) {
//                        Commands.writeMessage(dataOut, "", -1, false);
//                        int gotType = Commands.getType(dataIn);
//                        Commands.readMessage(dataIn, gotType);
                        break;
                    }

                    Commands.writeMessage(dataOut, toSend, 1, false);
                    System.out.println("sent " + toSend);

                    if (dataIn.available() > 0) {
                        Commands.getType(dataIn);
                        String clientMessageEcho = Commands.readMessage(dataIn, type);
                        System.out.println("received " + clientMessageEcho);
                    }
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
            sc.close();
        }

        System.out.println("finished");
        System.out.println();

    }

}