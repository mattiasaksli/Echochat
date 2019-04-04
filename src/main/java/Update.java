import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.SocketException;


public class Update implements Runnable {

    DataOutputStream dataOut;
    DataInputStream dataIn;

    public Update(DataOutputStream dataOut, DataInputStream dataIn) {
        this.dataOut = dataOut;
        this.dataIn = dataIn;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(2000);
                Commands.writeUpdateRequest(dataOut);
                int gotType = Commands.getType(dataIn);
                String message = Commands.readMessage(dataIn, gotType);
                message = message.trim();
                if (message.equals("")) {

                } else {
                    System.out.print(message + "\n");
                }
            } catch (SocketException e) {
                System.out.println("Connection terminated");
                break;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}
