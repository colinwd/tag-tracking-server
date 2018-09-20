package com.colinwd.client;

import com.colinwd.Request;
import com.colinwd.RequestBuilder;

import java.io.*;
import java.net.Socket;
import java.time.Instant;

public class TestClient implements Closeable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String send(String data) throws IOException {
        out.println(data);
        return in.readLine();
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static Request testRequest() {
        return new RequestBuilder().userId("Colin").addTags("timbers_army").timestamp(Instant.now()).build();
    }
}
