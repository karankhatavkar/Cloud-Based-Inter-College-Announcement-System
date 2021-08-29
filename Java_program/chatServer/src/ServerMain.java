import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Karan on 19/08/2021
 *
 */

public class ServerMain {
    public static void main(String[] args) {
        int port = 8818;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();

                System.out.println("Accepted connection form " + clientSocket);
                ServerWorker worker = new ServerWorker(clientSocket);
                worker.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
