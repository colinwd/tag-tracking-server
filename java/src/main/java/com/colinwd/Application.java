package com.colinwd;

public class Application {

    public static void main(String[] args) {
        Server server = new Server(27015);
        server.start();
    }
}
