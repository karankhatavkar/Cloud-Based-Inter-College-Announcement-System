package com.muc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
                //System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();
                //System.out.println("Accepted connection form " + clientSocket);
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write("This is a sample notice\n".getBytes());
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
