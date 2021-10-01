package com.muc;



/**
 * Created by Karan on 1st Oct 2021.
 */
public class ServerMain {
    public static void main(String[] args) {
        int port = 8818;
        Server server = new Server(port);
        server.start();
    }
}
