import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Update implements Runnable {

    private DataOutputStream dataOut;
    private DataInputStream dataIn;

    Update(DataOutputStream dataOut, DataInputStream dataIn) {
        this.dataOut = dataOut;
        this.dataIn = dataIn;
    }

    @Override
    public void run() {

        while (true) {
            try {
                // TEXTS

                Thread.sleep(500);
                Commands.writeUpdateRequest(dataOut);

                int length = dataIn.readInt();

                for (int i = 0; i < length; i++) {
                    int gotType = Commands.getType(dataIn);
                    String message = Commands.readMessage(dataIn, gotType);
                    message = message.trim();
                    if (!message.isEmpty()) {
                        System.out.print(message + "\n");
                    }
                }

                // FILES

                Commands.writeFileUpdateRequest(dataOut);

                int fileLength = dataIn.readInt();

                for (int i = 0; i < fileLength; i++) {
                    int gotType = Commands.getType(dataIn);
                    byte[] file = Commands.readFile(dataIn);
                    String fileName = dataIn.readUTF();

                    if (!Files.exists(Path.of("received_files"))) {
                        new File("received_files").mkdir();
                    }

                    Files.write(Paths.get("received_files\\" + fileName), file);
                    System.out.print("You received a file " + fileName + "\n");

                }
            } catch (SocketException e) {
                System.out.println("Connection terminated"); // TODO fix this.
                break;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

}