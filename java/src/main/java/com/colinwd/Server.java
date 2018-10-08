package com.colinwd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class Server {

    private ServerSocket serverSocket;
    private boolean stopped = false;

    Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Unable to bind to port " + port + ", is it already in use?");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.stopped = true;
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close connections cleanly. Sorry!");
                e.printStackTrace();
            }
        }));
    }

    private static void handleConnection(Socket socket) {
        PrintWriter out;

        if (socket == null) {
            return;
        }

        try {
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input = in.readLine();

            String response = RequestHandler.handleRequest(input);
            out.println(response);

            socket.close();
            System.out.println("Connection to " + socket.getRemoteSocketAddress() + " closed.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to write to socket " + socket.getRemoteSocketAddress());
        }
    }

    void start() {
        try {
            while (!stopped) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection received from " + socket.getRemoteSocketAddress());

                new Thread(() -> handleConnection(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Failed to negotiate incoming connection on " + serverSocket.getLocalSocketAddress());
        }
    }
}
